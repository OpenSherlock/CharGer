/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib.history;

import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A single event to be recorded in an object's history.
 * The information in a history event depends on the event type.
 * For a file, the filename is recorded. For a graph, the
 * top level graph is recorded.
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public  class ObjectHistoryRecord extends HistoryRecord  {
    
    public static String DEFAULT_ID = "no id";
    /** Whatever object id is determined. Default is "no id". */
    String id = DEFAULT_ID;    // whenever there's no id.
//    /** Whatever the implementor requires of a locator for the object. */
//     String url = "about:blank";
   /**
     * A new blank history event.
     */
    public ObjectHistoryRecord( Object graphObject ) {
        type = "OBJECT";
        setObject( graphObject );
    }

    public ObjectHistoryRecord() {
        
    }
    /**
     * An object history event whose type is represented as the string.
     * @param s a string indicator of its type.
     * 
     * @see ObjectHistoryRecordType
     */
    public ObjectHistoryRecord( String s ) {
        this();
        setType( s );
    }
    
    public void copyInfoFrom( ObjectHistoryRecord he) {
        super.copyInfoFrom( he );
        this.setID( new String( he.id ));
    }
    
    
   public Object getObject() {
        return object;
    }

    public void setObject( Object graphObject ) {
        this.object = graphObject;
    }

    public String getID() {
        return id;
    }

    public void setID( String id ) {
        this.id = id;
    }

//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl( String url ) {
//        this.url = url;
//    }
//    
    
    
     /** Create an XML document such that "event" is the outermost tag.
     * 
     * @return a ready-to-parse document
     */
   public Document makeDocument(  ) {
        Document doc = super.makeDocument(  );
        
        Element idElem = doc.createElement( "id");
        idElem.insertBefore( doc.createTextNode(id ), idElem.getLastChild());
        doc.getFirstChild().appendChild( idElem );
        
//        Element urlElem = doc.createElement( "url");
//        urlElem.insertBefore( doc.createTextNode(url ), urlElem.getLastChild());
//        doc.getFirstChild().appendChild( urlElem );
        
        return doc;
    }
    
    
}
