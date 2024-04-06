/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib.history;

//import chargerlib.history.HistoryRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Captures the knowledge that an object was derived from some procedure.
 * Since there may be lots of different kinds of derivation procedures,
 * the only specific knowledge is stored in the description.
 * Derivation procedures that create such events are responsible for making
 * sure enough information is appended to the description.
 * @author Harry S. Delugach (delugach@uah.edu)
 * @since 4.1
 */
public class DerivedHistoryRecord extends HistoryRecord {
    
    String id = "";

    public DerivedHistoryRecord() {
        type = "DERIVED";
    }
    
    public DerivedHistoryRecord( Object derivedObject, String id, String description ) {
        super();
        type = "DERIVED";
        object = derivedObject;
        this.id = id;
        setDescription( description );
    }

    
    public Document makeDocument(  ) {
        Document doc = super.makeDocument(  );
        Element idElem = doc.createElement( "id");
        idElem.insertBefore( doc.createTextNode( id ), idElem.getLastChild());
        doc.getFirstChild().appendChild( idElem );
        return doc;
    }

    public void copyInfoFrom( DerivedHistoryRecord he ) {
        super.copyInfoFrom( he );
        this.id = new String( he.id );
    }

    public String getId() {
        return id;
    }

    public void setID( String id ) {
        this.id = id;
    }
    
    public String toString() {
        return super.toString();
    }
}
