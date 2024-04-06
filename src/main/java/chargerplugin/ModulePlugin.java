/*
 * Copyright (C) 2015 hsd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package chargerplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import charger.Global;

/**
 * Provides a set of "plug in" services for external modules. The module is assumed to have complete access to Charger and in general
 * should be compiled with all the Charger packages in the class path.
 *
 * To make your module accessible to Charger, you create a module plugin ("boot") class,
 * one that controls (or at least initiates) the rest of your module operation,
 * subject to the following constraints:
 * <ul>
 * <li>The plugin class itself must be a compiled class in the <code>chargerplugin</code> package.
 * The rest of the module's actual code can be packaged anywhere you want.
 * </li>
 * <li>
 * The module may have any name, but it's recommended that the name
 * indicate somehow that it is a plugin.
 * </li>
 * <li>
 * As a sub-class of this class, of course it must implement all the abstract methods of this class, in accordance with standard Java rules.
 * </li>
 * </ul>
 * Once you've done all that, the name (see <code>getDisplayName()</code> below) will appear on the
 * Tools menu of the HubFrame using the keystroke specified in <code>getKeyStroke()</code>.
 * The module's properties Panel (if non-null) will be appear in Charger's own Preferences window as one of the tabs,
 * but only after it has been enabled (see below)
 * When selected, Charger will invoke the startup() method of your
 * boot class. After that, it's up to you!
 * <p>
 *  Note that if your module creates any windows, those windows should implement and invoke
 * the methods from Charger's window manager interface, or else they will not appear in the Window menu
 * of Charger. They will still be accessible and selectable according to your operating system's
 * way of managing windows.
 * </p>
 * <p>
 * An implementer is free to use the plugin class as the main class
 * providing most or all of the module's functionality. The intention of this architecture, however,
 * is that the plugin's actual instance is small and fast, since it may be instantiated in some situations
 * where the module's main purpose will never be utilized.
 * </p>
 * <p>
 * A module's state can be categorized as follows:
 * </p>
 * <dl>
 * <dt>available</dt>
 * <dd>means that the module is present in Charger's classpath.</dd>
 * <dt>enabled</dt>
 * <dd>means that the user has chosen to have the module present in the Tools menu, whether they
 * have used it or not</dd>
 * <dt>activated</dt>
 * <dd>the module's startup method has been called (by invocation from the Tools
 * menu).</dd>
 * </dl>
 * <p>
 * When the module's <code>shutdown()</code> method is called,
 * the module is no longer activated, but may remain enabled. Users may enable
 * tools from the Config tab of the Preferences window.
 * </p>
 * <p>
 * A module is provided with a built-in set of properties (default empty) that will be automatically loaded and saved
 * for each activated module.
 * </p>
 * @see charger.util.WindowManager
 * @author hsd
 */
public abstract class ModulePlugin {

    ModuleMenuItem moduleMenuItem = null;


    /**
     * Automatically saved and retrieved whenever Charger properties are saved
     * or retrieved. Can also be saved explicitly.
     * @see #saveProperties()
     */
    public static Properties moduleProperties = new Properties();

    /**
     * Return the simple name of this module. Will appear on standard output and serves as the label in the Tools menu.
     */
    abstract public String getDisplayName( );

    /** Get the accelerator key combination needed for the menu.
     * Null if no keystroke is desired.
     */
    abstract public KeyStroke getKeyStroke();

    /** Return any other information about the module, such as copyright, purpose, references, etc. */
    abstract public String getInfo();

    /** Launch this module, attaching it to Charger.
     * If module is already running, then a call to startup will be replaced
     * by a call to activate.
     */
    abstract public void startup( ModulePlugin module );

    /**
     * Called whenever the module is invoked (e.g., by selecting through the Tools menu),  but is already running.
     */
    abstract public void activate();

    /** Perform whatever cleanup and finalizing necessary.
     * Called automatically when Charger exits.
     */
    abstract public void shutdown();



    /**
     * Assemble the module's properties file name and construct a File object for it.
     * Currently the file name is the simple name with the extension ".props"
     * in the user home config folder for charger
     * @return
     * @see Global#APPLICATION_CONFIG_FOLDER
     */
    protected File getModulePropertiesFile() {
        File modulePrefsFile = new File(
            new File( System.getProperty( "user.home"), Global.APPLICATION_CONFIG_FOLDER),
            this.getClass().getSimpleName() + ".props" );
        return modulePrefsFile;
    }

    /**
     * Provides access to the module's built-in properties. A general facility
     * for keeping configuration information on a module.
     * To set or add a property, get the module instance from one of the Global lists,
     * and then <code>module.getProperties().setProperty( propertyLabel, propertyStringValue )</code>
     * @return the module properties that have been set or loaded. If not properties were ever saved,
     * then return null.
     */
    public  Properties getProperties() {
        if ( ! moduleProperties.isEmpty() )
            return moduleProperties;
        FileInputStream is;
        File modulePrefsFile = getModulePropertiesFile();
        try {
            is = new FileInputStream( modulePrefsFile );
            moduleProperties.load( is );
        } catch ( FileNotFoundException ex ) {
            // NO problem if not found
            modulePrefsFile.getParentFile().mkdirs();
            try {
                modulePrefsFile.createNewFile();
//            Logger.getLogger( ModulePlugin.class.getDisplayName() ).log( Level.SEVERE, null, ex );
            } catch ( IOException ex1 ) {
                Logger.getLogger( ModulePlugin.class.getName() ).log( Level.SEVERE, null, ex1 );
            }
        } catch ( IOException ex ) {
            Logger.getLogger( ModulePlugin.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return moduleProperties;
    }

    /**
     * Set the module's properties directly.
     * Mostly useful if an entirely new set of properties is desired for a particular user.
     * @param props
     */
    public void setProperties( Properties props ) {
        moduleProperties = props;
    }

    /**
     * Saves the module's built-in properties as returned by getProperties(). The built-in properties are a general facility
     * for keeping configuration information on a module. This is for the module writer's
     * convenience --
     * it is always allowed for the module to create/modify its own files
     * whenever desired. It is recommended that any sub-class overriding this
     * method should also call this super-class method. Called automatically
     * when Charger exits, if the module is still activated.
     * Properties are saved in a "hidden" folder in the user's home directory,
     * currently ".edu.uah.Charger/modulePluginName.props"
     */
    public void saveProperties() {
        if ( moduleProperties.isEmpty() ) {
            return;
        }

        FileWriter fw = null;
        File modulePrefsFile = getModulePropertiesFile();
        try {
            modulePrefsFile.getParentFile().mkdirs();
            fw = new FileWriter( modulePrefsFile );
            moduleProperties.store( fw, "Built-in properties for module " + this.getClass().getSimpleName() );
            fw.close();
//            Global.Prefs.store( new FileOutputStream( prefsFilename ), Global.getInfoString() );
//            CGUtil.showMessageDialog( null, "Module \"" + this.getClass().getSimpleName()
//                    + "\" built-in properties saved in:\n" + modulePrefsFile.getAbsolutePath() );
        } catch ( FileNotFoundException ex ) {
            Logger.getLogger( ModulePlugin.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( IOException ex ) {
            Logger.getLogger( ModulePlugin.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }


    /**
     * Access the properties panel for this module, suitable for adding as a tab in the main Preferences window.
     * If no properties panel is desired, then return null;
     * @return
     */
    abstract public JPanel getPropertiesPanel();


    public ModuleMenuItem getModuleMenuItem() {
        return moduleMenuItem;
    }


    /**
     * The menu item associated with this plugin module.
     * A new instance of this menu item will be added to every Tools menu in Charger.
     * Tools menu appears in both the main window and each EditFrame.
     * The module manager will instantiate this menu item in each tools menu.
     */
     public class ModuleMenuItem extends JMenuItem {

        JFrame ownerFrame = null;
        Action action = null;
        /**
         * Create a new instance of this menu item, remembering the frame in which its enclosing Tools menu resides.
         * @param ownerFrame
         */
        public ModuleMenuItem( JFrame ownerFrame, Action action ) {
            super( action );
            this.ownerFrame = ownerFrame;
            this.action = action;
        }

        public JFrame getOwnerFrame() {
            return ownerFrame;
        }

        public Action getAction() {
            return action;
        }



    }
}
