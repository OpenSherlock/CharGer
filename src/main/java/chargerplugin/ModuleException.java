/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerplugin;

/**
 * Exceptions related to locating and linking modules into Charger.
 * @see ModulePlugin 
 * @author Harry S. Delugach (delugach@uah.edu)
 */
    public  class ModuleException  extends Exception {
        public ModuleException( String msg ) {
            super( msg );
        }
    }
    
