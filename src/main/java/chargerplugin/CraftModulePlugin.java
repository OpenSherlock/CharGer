/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerplugin;

import charger.Global;
import craft.Craft;
import craft.CraftPrefPanel;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Properties;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * The plugin hooks for the CRAFT module.
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class CraftModulePlugin extends ModulePlugin {

    @Override
    public String getDisplayName() {
        return "Requirements Acquisition (CRAFT)";
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.META_DOWN_MASK );
    }

    @Override
    public String getInfo() {
        return "Conceptual Requirements Acquisition and Formation Tool (CRAFT)\n"
                + "Creates and manages repertory grids from Charger graphs.\n"
                + "(c) 1999-2020 Harry S. Delugach";
    }

    @Override
    public void startup( ModulePlugin module ) {
        //Global.info( "at menuToolsCraftToolActionPerformed.. ");
          Global.info("starting up " + getDisplayName() );

      if ( Craft.craftModulePlugin == null ) {
            Craft.craftModulePlugin = module;
            new craft.Craft();
        }
        Craft.craftWindow.refresh();
    }

    @Override
    public void activate() {
        Craft.craftWindow.toFront();
        Craft.craftWindow.requestFocus();
    }


    @Override
    public void shutdown() {
        Craft.craftWindow.dispose();
        Global.info("Shutting down \"" + getDisplayName() + "\"." );
    }

    @Override
    public JPanel getPropertiesPanel() {
        if ( Craft.craftPreferencesPanel == null ) {
            Craft.craftPreferencesPanel = new CraftPrefPanel();
        }
        return Craft.craftPreferencesPanel;
    }

    @Override
    public void setProperties( Properties props ) {
        Craft.craftProperties.setProperty(
                "useOnlyGenericConceptsinCraft", Craft.useOnlyGenericConceptsinCraft + "" );
        Craft.craftProperties.setProperty(
                "GridFolder", Craft.GridFolder );
        Craft.craftProperties.setProperty(
                "useOnlyBinaryRelationsinCraft", Craft.useOnlyBinaryRelationsinCraft + "" );
        Craft.craftProperties.setProperty(
                "tryGenericSenseInRepGrid", Craft.tryGenericSenseInRepGrid + "" );

    }

    @Override
    public void saveProperties() {
        setProperties( Craft.craftProperties );
        super.saveProperties(); //To change body of generated methods, choose Tools | Templates.
    }





}
