
package chargerplugin;


import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import charger.Global;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * A test module for ensuring the module interface is working as expected.
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class TestModule extends ModulePlugin {
    
    JFrame frame = null;

    @Override
    public String getDisplayName() {
        return "Test Module";
    }

    @Override
    public KeyStroke getKeyStroke() {
        return null;
//        return KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0);
    }

    @Override
    public String getInfo() {
        return "A module for testing/demo purposes.\nJust displays a blank window.";
    }

    @Override
    public void startup( ModulePlugin module ) {
        Global.info("starting up " + getDisplayName() );
        frame = new JFrame();
        frame.setTitle( "Test Module");
        frame.setSize( new Dimension( 300, 300));
        frame.setLocation( new Point( 200, 200 ));
        frame.setVisible( true );
        
        getProperties().setProperty( "testProperty", "Hello" );
    }

    @Override
    public void activate() {
        frame.toFront();
        frame.requestFocus();
    }
    
    

    @Override
    public void shutdown() {
        Global.info("shutting down " + getDisplayName() );
        frame.dispose();
    }

    @Override
    public JPanel getPropertiesPanel() {
        return null;
    }

}
