/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerplugin;

import static charger.HubFrame.DataBaseLinkToolWindow;

//import java.awt.Cursor;
//import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import charger.Global;
import charger.db.DatabaseFrame;

/**
 *
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class DatabaseLinkingPlugin extends ModulePlugin {

    @Override
    public String getDisplayName() {
        return "Database Linking Tool";
    }

    @Override
    public KeyStroke getKeyStroke() {
        return null;
    }

    @Override
    public String getInfo() {
        return "Database Linking Tool\nFor attaching to a text file \"database\" to generate a graph for each record.\n"
                  + "(c) 1999-2020 Harry S. Delugach";
  }

    @Override
    public void startup( ModulePlugin module ) {
        //Global.info( "at menuToolsDatabaseLinkingToolActionPerformed.. ");
        if ( DataBaseLinkToolWindow == null ) {
            DataBaseLinkToolWindow
                    = new DatabaseFrame( Global.DatabaseFolderString + File.separator + "DBElements.txt" );
        }
        DataBaseLinkToolWindow.toFront();
        DataBaseLinkToolWindow.setVisible( true );
    }

    @Override
    public void activate() {
        DataBaseLinkToolWindow.toFront();
        DataBaseLinkToolWindow.requestFocus();
    }

    @Override
    public void shutdown() {
        DataBaseLinkToolWindow.dispose();
    }

    @Override
    public JPanel getPropertiesPanel() {
        return null;
    }

}
