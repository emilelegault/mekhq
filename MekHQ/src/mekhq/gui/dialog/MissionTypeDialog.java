/*
 * MissionTypeDialog.java
 *
 * Created on Jan 6, 2010, 10:46:02 PM
 */

package mekhq.gui.dialog;

import java.awt.Frame;
import java.util.ResourceBundle;

import javax.swing.JButton;

import megamek.common.util.EncodeControl;
import mekhq.MekHQ;
import megamek.client.ui.preferences.JWindowPreference;
import megamek.client.ui.preferences.PreferencesNode;

/**
 *
 * @author natit
 */
public class MissionTypeDialog extends javax.swing.JDialog {

    private boolean contract;

    /** Creates new form */
    public MissionTypeDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
    }

    private void initComponents() {
        final ResourceBundle resourceMap = ResourceBundle.getBundle("mekhq.resources.MissionTypeDialog",
                MekHQ.getMHQOptions().getLocale(), new EncodeControl());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form");
        setTitle(resourceMap.getString("Form.title"));

        getContentPane().setLayout(new java.awt.GridLayout(2,1));

        JButton btnMission = new javax.swing.JButton(resourceMap.getString("btnMission.text"));
        btnMission.setToolTipText(resourceMap.getString("btnMission.tooltip"));
        btnMission.setName("btnMission");
        btnMission.addActionListener(ev -> {
            contract = false;
            setVisible(false);
        });
        getContentPane().add(btnMission);

        JButton btnContract = new javax.swing.JButton(resourceMap.getString("btnContract.text"));
        btnContract.setToolTipText(resourceMap.getString("btnContract.tooltip"));
        btnContract.setName("btnContract");
        btnContract.addActionListener(ev -> {
            contract = true;
            setVisible(false);
        });
        getContentPane().add(btnContract);

        setSize(250, 150);
        setUserPreferences();
    }

    private void setUserPreferences() {
        PreferencesNode preferences = MekHQ.getMHQPreferences().forClass(MissionTypeDialog.class);

        this.setName("dialog");
        preferences.manage(new JWindowPreference(this));
    }

    public boolean isContract() {
        return contract;
    }
}
