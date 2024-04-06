/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cgif.parser;

import java.util.HashMap;

import charger.obj.GraphObject;

/**
 * Provides the functionality needed to keep track of all referents so that relations can be tied to them.
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class ReferentMap {
        /** Key is a variable name (e.g., x1) or string referent */
    private HashMap<String, GraphObject> objectmap = new HashMap<String, GraphObject>();
    
    public ReferentMap() {
        
    }
    
    /** Look up the graph object that corresponds to the referent given.
     * If more than one, returns the first one found.
     * If the referent begins with "?" (meaning it is a backward reference),
     * then the object that first defined the referent will be returned.
     * @param ref
     * @return the graph object with the given referent, null if none found.
     */
    public GraphObject getObjectByReferent( String ref ) {
        if ( ref == null ) return null;
        if ( ref.startsWith( "?"))
            ref = "*" + ref.substring( 1);
        return objectmap.get( ref );
    }
    
    /**
     * Insert the graph object into the list, using its referent as a key.
     * @param ref
     * @param go
     * @throws CGIFVariableException if the referent has already been associated with an object.
     */
    public void putObjectByReferent( String ref, GraphObject go ) throws CGIFVariableException {
        if ( getObjectByReferent( ref ) != null ) {
            throw new CGIFVariableException( "Variable " + ref + " already exists.");
        } else {
            objectmap.put(  ref, go);
        }
    }
    
    public void clear() {
        objectmap.clear();
    }
}
