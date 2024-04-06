/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerplugin;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

import cgfca.CG_FCA;

/**
 *
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class CG_FCA_Plugin extends ModulePlugin {

    @Override
    public String getDisplayName() {
        return "CG to FCA";
    }

    @Override
    public KeyStroke getKeyStroke() {
        return null;
    }

    @Override
    public String getInfo() {
        return "Support for some CGFCA Functions (see Andrews and Polovina http://shura.shu.ac.uk/19112/)";
    }

    @Override
    public void startup( ModulePlugin module ) {
        CG_FCA.startupCGFCA();
    }

    @Override
    public void activate() {
        CG_FCA.activateCGFCA();
    }

    @Override
    public void shutdown() {
        CG_FCA.shutdownCGFCA();
    }

    @Override
    public JPanel getPropertiesPanel() {
        return null;   // for now....
    }

}
