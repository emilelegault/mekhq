/*
 * EditPersonnelHitsDialog.java
 *
 * Copyright (C) 2018 MegaMek team
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ. If not, see <http://www.gnu.org/licenses/>.
 */

package mekhq.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import megamek.common.util.EncodeControl;
import mekhq.MekHQ;
import mekhq.campaign.personnel.Person;
import megamek.client.ui.preferences.JWindowPreference;
import megamek.client.ui.preferences.PreferencesNode;

/**
 * Provides an editor for the number of hits sustained by a person,
 * when advanced medical rules are not in use.
 */
public class EditPersonnelHitsDialog extends JDialog {
    private Person person;

    private JButton btnOK;
    private JSpinner spinnerHits;
    private SpinnerNumberModel spinnerModel;

    /** Creates new form EditPersonnelHitsDialog */
    public EditPersonnelHitsDialog(Frame parent, boolean modal, Person p) {
        super(parent, modal);

        person = p;

        initComponents();
        setLocationRelativeTo(parent);
        setUserPreferences();
    }

    private void initComponents() {
        spinnerHits = new JSpinner();
        btnOK = new JButton();

        final ResourceBundle resourceMap = ResourceBundle.getBundle("mekhq.resources.EditPersonnelHitsDialog",
                MekHQ.getMHQOptions().getLocale(), new EncodeControl());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form");
        setTitle(resourceMap.getString("Form.title") + " " + person.getFullName());
        getContentPane().setLayout(new BorderLayout());
        setMinimumSize(new Dimension(240, 40));

        spinnerModel = new SpinnerNumberModel(person.getHits(), 0, 5, 1);
        spinnerHits.setModel(spinnerModel);
        spinnerHits.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(resourceMap.getString("spinnerHits.title")),
            BorderFactory.createEmptyBorder(5,5,5,5)));
        getContentPane().add(spinnerHits, BorderLayout.CENTER);

        btnOK.setText(resourceMap.getString("btnOK.text"));
        btnOK.setName("btnOK");
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        getContentPane().add(btnOK, BorderLayout.PAGE_END);

        pack();
    }

    private void setUserPreferences() {
        PreferencesNode preferences = MekHQ.getMHQPreferences().forClass(EditPersonnelHitsDialog.class);

        this.setName("dialog");
        preferences.manage(new JWindowPreference(this));
    }

    private void btnOKActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnHireActionPerformed
        person.setHits((Integer) spinnerHits.getModel().getValue());

        this.setVisible(false);
    }
}
