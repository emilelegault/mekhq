/*
 * MegaMekLab - Copyright (C) 2019 - The MegaMekTeam
 *
 * Original author - Jay Lawson (jaylawson39 at yahoo.com)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package mekhq.gui.dialog;

import megamek.client.generator.RandomNameGenerator;
import megamek.client.ui.dialogs.PortraitChooserDialog;
import megamek.client.ui.preferences.JWindowPreference;
import megamek.client.ui.preferences.PreferencesNode;
import megamek.common.enums.Gender;
import megamek.common.util.EncodeControl;
import mekhq.MekHQ;
import mekhq.campaign.personnel.Person;
import mekhq.campaign.personnel.enums.Profession;
import mekhq.gui.CampaignGUI;
import mekhq.gui.displayWrappers.RankDisplay;
import mekhq.gui.view.PersonViewPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This dialog is used to both hire new pilots and to edit existing ones
 */
public class NewRecruitDialog extends JDialog {

    private Person person;

    private CampaignGUI hqView;

    private JComboBox<RankDisplay> choiceRanks;

    private JScrollPane scrollView;

    /** Creates new form CustomizePilotDialog */
    public NewRecruitDialog(CampaignGUI hqView, boolean modal, Person person) {
        super(hqView.getFrame(), modal);
        this.hqView = hqView;
        this.person = person;
        initComponents();
        setLocationRelativeTo(hqView.getFrame());
        setUserPreferences();
    }

    private void refreshView() {
        scrollView.setViewportView(new PersonViewPanel(person, hqView.getCampaign(), hqView));
        // This odd code is to make sure that the scrollbar stays at the top
        // I cant just call it here, because it ends up getting reset somewhere
        // later
        javax.swing.SwingUtilities.invokeLater(() -> scrollView.getVerticalScrollBar().setValue(0));
    }

    private void initComponents() {
        scrollView = new JScrollPane();
        choiceRanks = new javax.swing.JComboBox<>();

        final ResourceBundle resourceMap = ResourceBundle.getBundle("mekhq.resources.NewRecruitDialog",
                MekHQ.getMHQOptions().getLocale(), new EncodeControl());
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setTitle(resourceMap.getString("Form.title"));

        setName("Form");
        getContentPane().setLayout(new java.awt.BorderLayout());

        JPanel panSidebar = createSidebar(resourceMap);

        JPanel panBottomButtons = createButtonPanel(resourceMap);

        scrollView.setMinimumSize(new java.awt.Dimension(450, 180));
        scrollView.setPreferredSize(new java.awt.Dimension(450, 180));
        scrollView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollView.setViewportView(null);
        refreshView();

        getContentPane().add(panSidebar, BorderLayout.LINE_START);
        getContentPane().add(scrollView, BorderLayout.CENTER);
        getContentPane().add(panBottomButtons, BorderLayout.PAGE_END);

        pack();
    }

    private JPanel createButtonPanel(ResourceBundle resourceMap) {
        JPanel panButtons = new JPanel();
        panButtons.setName("panButtons");
        panButtons.setLayout(new GridBagLayout());

        JButton button = new JButton(resourceMap.getString("btnHire.text"));
        button.setName("btnOk");
        button.addActionListener(e -> hire());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;

        panButtons.add(button, gridBagConstraints);
        gridBagConstraints.gridx++;

        if (hqView.getCampaign().isGM()) {
            button = new JButton(resourceMap.getString("btnAddGM.text"));
            button.setName("btnGM");
            button.addActionListener(e -> addGM());

            panButtons.add(button, gridBagConstraints);
            gridBagConstraints.gridx++;
        }

        button = new JButton(resourceMap.getString("btnClose.text"));
        button.setName("btnClose");
        button.addActionListener(e -> setVisible(false));
        panButtons.add(button, gridBagConstraints);

        return panButtons;
    }

    private JPanel createSidebar(ResourceBundle resourceMap) {
        boolean randomizeOrigin = hqView.getCampaign().getCampaignOptions().getRandomOriginOptions().isRandomizeOrigin();

        JPanel panSidebar = new JPanel();
        panSidebar.setName("panButtons");
        panSidebar.setLayout(new java.awt.GridLayout(6 + (randomizeOrigin ? 1 : 0), 1));

        choiceRanks.setName("choiceRanks");
        refreshRanksCombo();
        choiceRanks.addActionListener(e -> changeRank());
        panSidebar.add(choiceRanks);

        JButton button = new JButton(resourceMap.getString("btnRandomName.text"));
        button.setName("btnRandomName");
        button.addActionListener(e -> randomName());
        panSidebar.add(button);

        button = new JButton(resourceMap.getString("btnRandomPortrait.text"));
        button.setName("btnRandomPortrait");
        button.addActionListener(e -> randomPortrait());
        panSidebar.add(button);

        if (randomizeOrigin) {
            button = new JButton(resourceMap.getString("btnRandomOrigin.text"));
            button.setName("btnRandomOrigin");
            button.addActionListener(e -> randomOrigin());
            panSidebar.add(button);
        }

        button = new JButton(resourceMap.getString("btnChoosePortrait.text"));
        button.setName("btnChoosePortrait");
        button.addActionListener(e -> choosePortrait());
        panSidebar.add(button);

        button = new JButton(resourceMap.getString("btnEditPerson.text"));
        button.setName("btnEditPerson");
        button.addActionListener(e -> editPerson());
        button.setEnabled(hqView.getCampaign().isGM());
        panSidebar.add(button);

        button = new JButton(resourceMap.getString("btnRegenerate.text"));
        button.setName("btnRegenerate");
        button.addActionListener(e -> regenerate());
        button.setEnabled(hqView.getCampaign().isGM());
        panSidebar.add(button);

        return panSidebar;
    }

    private void setUserPreferences() {
        PreferencesNode preferences = MekHQ.getMHQPreferences().forClass(NewRecruitDialog.class);

        this.setName("dialog");
        preferences.manage(new JWindowPreference(this));
    }

    private void hire() {
        if (hqView.getCampaign().recruitPerson(person, false)) {
            createNewRecruit();
        }
        refreshView();
    }

    private void addGM() {
        if (hqView.getCampaign().recruitPerson(person, true)) {
            createNewRecruit();
        }
        refreshView();
    }

    private void createNewRecruit() {
        person = hqView.getCampaign().newPerson(person.getPrimaryRole());
        refreshRanksCombo();
        person.setRank(((RankDisplay) Objects.requireNonNull(choiceRanks.getSelectedItem())).getRankNumeric());
    }

    private void randomName() {
        String factionCode = hqView.getCampaign().getCampaignOptions().useOriginFactionForNames()
                ? person.getOriginFaction().getShortName()
                : RandomNameGenerator.getInstance().getChosenFaction();

        String[] name = RandomNameGenerator.getInstance().generateGivenNameSurnameSplit(
                person.getGender(), person.isClanner(), factionCode);
        person.setGivenName(name[0]);
        person.setSurname(name[1]);
        refreshView();
    }

    private void randomPortrait() {
        hqView.getCampaign().assignRandomPortraitFor(person);
        refreshView();
    }

    private void randomOrigin() {
        hqView.getCampaign().assignRandomOriginFor(person);
        refreshView();
    }

    private void choosePortrait() {
        final PortraitChooserDialog portraitDialog = new PortraitChooserDialog(hqView.getFrame(), person.getPortrait());
        if (portraitDialog.showDialog().isConfirmed()) {
            person.setPortrait(portraitDialog.getSelectedItem());
            refreshView();
        }
    }

    private void editPerson() {
        Gender gender = person.getGender();
        CustomizePersonDialog npd = new CustomizePersonDialog(hqView.getFrame(), true, person, hqView.getCampaign());
        npd.setVisible(true);
        if (gender != person.getGender()) {
            randomPortrait();
        }
        refreshRanksCombo();
        refreshView();
    }

    private void regenerate() {
        person = hqView.getCampaign().newPerson(person.getPrimaryRole(), person.getSecondaryRole());
        refreshRanksCombo();
        refreshView();
    }

    private void changeRank() {
        person.setRank(((RankDisplay) Objects.requireNonNull(choiceRanks.getSelectedItem())).getRankNumeric());
        refreshView();
    }

    private void refreshRanksCombo() {
        DefaultComboBoxModel<RankDisplay> ranksModel = new DefaultComboBoxModel<>();
        ranksModel.addAll(RankDisplay.getRankDisplaysForSystem(person.getRankSystem(),
                Profession.getProfessionFromPersonnelRole(person.getPrimaryRole())));
        choiceRanks.setModel(ranksModel);
        choiceRanks.setSelectedIndex(0);
    }
}
