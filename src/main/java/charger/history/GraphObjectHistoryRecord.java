/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package charger.history;

import charger.obj.GraphObject;
import chargerlib.history.ObjectHistoryRecord;

/**
 * An object history event where the object is assumed to be a graph object.
 * @author Harry S. Delugach (delugach@uah.edu)
 * @since 4.1
 */
public class GraphObjectHistoryRecord extends ObjectHistoryRecord {

    public GraphObjectHistoryRecord() {
    }

    
    public GraphObjectHistoryRecord( GraphObject graphObject ) {
        super( graphObject );
    }
    
    public GraphObject getGraphObject() {
        return (GraphObject)getObject();
    }
    
    public void setGraphObject( GraphObject graphObject ) {
        setObject(object);
    }

}
