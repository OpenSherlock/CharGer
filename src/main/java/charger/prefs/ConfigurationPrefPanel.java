/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package charger.prefs;

import charger.Global;
import charger.util.CGUtil;
import chargerplugin.ModulePlugin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * Controls some basic configuration information, as well as the tools menu.
 * @author hsd
 */
public class ConfigurationPrefPanel extends javax.swing.JPanel {

     int matchingStrategyDisplayWidth = 280;

     ArrayList<JCheckBox> checkboxes = new ArrayList<>();

    /**
     * Creates new form CompatibilityPrefPanel
     */
    public ConfigurationPrefPanel() {
        initComponents();
        enablingPanel.setLayout( new BoxLayout( this.enablingPanel, BoxLayout.Y_AXIS));

        for ( ModulePlugin module : Global.modulePluginsAvailable ) {
            this.enablingPanel.add(  makeToolCheckbox( module ) );

        }
    }

    final JPanel parent = this;

    /**
     * Create and configure a checkbox to enable/disable a module plugin
     * @param plugin an instantiated
     * @return a checkbox ready to be added to the Tools panel.
     * @see ModulePlugin
     */
    public JCheckBox makeToolCheckbox( ModulePlugin plugin ) {
        ModulePlugin thisPlugin = plugin;
        JCheckBox checkbox = new JCheckBox( plugin.getDisplayName() );
        if ( new ArrayList<String>( Arrays.asList( Global.moduleNamesToEnableCommaSeparated.split( "," ) ) )
                .contains( plugin.getClass().getSimpleName() ) ) {
            checkbox.setSelected( true );
        }
        checkboxes.add( checkbox );

        checkbox.addActionListener( e -> {
            ArrayList<String> newEnabledList
                    = new ArrayList<String>( Arrays.asList( Global.moduleNamesToEnableCommaSeparated.split( "," ) ) );
            newEnabledList.remove( "" );
            if ( ( (JCheckBox)e.getSource() ).isSelected() ) {
                if ( !newEnabledList.contains( thisPlugin.getClass().getSimpleName() ) ) {
                    newEnabledList.add( thisPlugin.getClass().getSimpleName() );
                }
                if ( !Global.modulePluginsEnabled.contains( thisPlugin ) ) {
                    Global.modulePluginsEnabled.add( thisPlugin );
                }
            } else { // is un-checkedf
                newEnabledList.remove( thisPlugin.getClass().getSimpleName() );
                Global.modulePluginsEnabled.remove( thisPlugin );
                if ( Global.modulePluginsActivated.contains( thisPlugin ) ) {
                    thisPlugin.shutdown();
                    Global.modulePluginsActivated.remove( thisPlugin );
                }
                Global.setModulesChanged();
                CGUtil.showMessageDialog( this, "Note: Tabs for Tools may not be refreshed until this window is closed.\n"
                        + "Please close Preferences window when you've made your selection(s).");
            }
            Global.moduleNamesToEnableCommaSeparated = String.join( ",", newEnabledList );
//            Global.pf = null;
            Global.CharGerMasterFrame.refreshToolsMenu();
        } );

        checkbox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                descriptionField.setText( thisPlugin.getInfo());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                descriptionField.setText( "");
            }
        });
        return checkbox;
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ShowBoringDebugInfo = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        numIterationsField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        enablingPanel = new javax.swing.JPanel();
        descriptionField = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        ShowBoringDebugInfo.setBackground(new java.awt.Color(255, 255, 255));
        ShowBoringDebugInfo.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        ShowBoringDebugInfo.setSelected(Global.ShowBoringDebugInfo);
        ShowBoringDebugInfo.setText("Show internal info (boring)");
        ShowBoringDebugInfo.setToolTipText("Show some boring debug information");
        ShowBoringDebugInfo.setOpaque(true);
        ShowBoringDebugInfo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ShowBoringDebugInfoItemStateChanged(evt);
            }
        });

        jPanel1.setBorder(BorderFactory.createTitledBorder( Global.BeveledBorder,
            "Spring Layout Parameters", TitledBorder.LEFT,
            TitledBorder.TOP, new Font( "SansSerif", Font.BOLD + Font.ITALIC, 11 ), Color.black ));

    numIterationsField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    numIterationsField.setText(Global.springLayoutMaxIterations + "");
    numIterationsField.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            numIterationsFieldActionPerformed(evt);
        }
    });

    jLabel1.setText("Number of layout iterations:");

    org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel1Layout.createSequentialGroup()
            .add(21, 21, 21)
            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(numIterationsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel1Layout.createSequentialGroup()
            .add(14, 14, 14)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(numIterationsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel1))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tools Menu (check to enable)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 3, 14))); // NOI18N

    enablingPanel.setBackground(new java.awt.Color(255, 255, 255));
    enablingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tools available", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 3, 13))); // NOI18N
    enablingPanel.setPreferredSize(new java.awt.Dimension(237, 300));

    org.jdesktop.layout.GroupLayout enablingPanelLayout = new org.jdesktop.layout.GroupLayout(enablingPanel);
    enablingPanel.setLayout(enablingPanelLayout);
    enablingPanelLayout.setHorizontalGroup(
        enablingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 267, Short.MAX_VALUE)
    );
    enablingPanelLayout.setVerticalGroup(
        enablingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 0, Short.MAX_VALUE)
    );

    descriptionField.setColumns(20);
    descriptionField.setLineWrap(true);
    descriptionField.setRows(5);
    descriptionField.setWrapStyleWord(true);
    descriptionField.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Description", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 3, 13))); // NOI18N

    org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .add(enablingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
            .add(18, 18, 18)
            .add(descriptionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 274, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(enablingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .add(descriptionField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
            .addContainerGap())
    );

    jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
    jLabel2.setText("Other settings:");

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(layout.createSequentialGroup()
            .add(17, 17, 17)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(layout.createSequentialGroup()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                            .add(jLabel2)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(layout.createSequentialGroup()
                            .add(0, 0, Short.MAX_VALUE)
                            .add(ShowBoringDebugInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 190, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(27, 27, 27)))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(227, 227, 227))))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(layout.createSequentialGroup()
            .add(20, 20, 20)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(18, 18, 18)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(layout.createSequentialGroup()
                    .add(jLabel2)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(ShowBoringDebugInfo))
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap(108, Short.MAX_VALUE))
    );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    }//GEN-LAST:event_formComponentShown

    private void ShowBoringDebugInfoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ShowBoringDebugInfoItemStateChanged
        if ( evt.getStateChange() == ItemEvent.SELECTED ) {
            Global.ShowBoringDebugInfo = true;
        } else {
            Global.ShowBoringDebugInfo = false;
        }
    }//GEN-LAST:event_ShowBoringDebugInfoItemStateChanged

    private void numIterationsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numIterationsFieldActionPerformed
        Global.springLayoutMaxIterations= PreferencesFrame.getNonNegativeIntFromField( numIterationsField, 5000 );

    }//GEN-LAST:event_numIterationsFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JCheckBox ShowBoringDebugInfo;
    public javax.swing.JTextArea descriptionField;
    public javax.swing.JPanel enablingPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField numIterationsField;
    // End of variables declaration//GEN-END:variables
}