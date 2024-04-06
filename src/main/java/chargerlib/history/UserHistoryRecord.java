/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib.history;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents the adding of a graph object by a user interactively.
 * @since 4.1
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class UserHistoryRecord extends ObjectHistoryRecord {

    protected String user = null;

    public UserHistoryRecord() {
    }
    
    
    public UserHistoryRecord( Object obj, String user ) {
        super( obj );
        type = "USER";
        this.user = user;
    }
    
    /** Create an XML document such that "event" is the outermost tag.
     * 
     * @return a ready-to-parse document
     */
    public Document makeDocument(  ) {
        Document doc = super.makeDocument(  );
        Element userElem = doc.createElement( "user");
        userElem.insertBefore( doc.createTextNode( user ), userElem.getLastChild());
        doc.getFirstChild().appendChild( userElem );
        
        return doc;
    }
    
    public String toString() {
        StringBuffer s = new StringBuffer( super.toString());
        s.append( "; User= " + user );
        return s.toString();
    }
    
    public void copyInfoFrom( UserHistoryRecord he ) {
        super.copyInfoFrom( he );
        this.setUser( new String( he.getUser()));
    }

    public String getUser() {
        return user;
    }

    public void setUser( String user ) {
        this.user = user;
    }

    
}
