/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib.history;

import static chargerlib.history.HistoryRecord.GenericHistoryRecordType;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A custom event is generally obtained from a parse with some
 * module or other library's events included. Since Charger doesn't know
 * what to do with it, or even what fields it contains, it is preserved
 * as its "raw" xml DOM element, to be returned intact when needed for export.
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class CustomHistoryRecord extends HistoryRecord {
    
    Element domElement = null;

    public CustomHistoryRecord() {
        this.type = "CUSTOM";
    }
    
        /** Copy this event's information from the event argument. 
     Subclasses are responsible for copying their own information after calling this method.
     * @param he the event to receive  the values from this event.
     */
    public void copyInfoFrom( CustomHistoryRecord he ) {
        super.copyInfoFrom( he );
        this.domElement = ((CustomHistoryRecord)he).domElement;

    }

    public Element getDomElement() {
        return domElement;
    }
    


    public void setDomElement( Element domElement ) {
        this.domElement = domElement;
//        this.type = "CUSTOM";
    }
    
    
    /** Simply attach the custom history record's domElement to a document
     * and return it.
     * @return 
     */
    public Document makeDocument() {
        if ( type == null )
            type = GenericHistoryRecordType;
        Document doc;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
        } catch ( ParserConfigurationException ex ) {
            Logger.getLogger( getClass().getName() ).log( Level.SEVERE, null, ex );
            return null;
        }
        

        doc.adoptNode( domElement );
        doc.appendChild( domElement );
        
        return doc;

    }

}
