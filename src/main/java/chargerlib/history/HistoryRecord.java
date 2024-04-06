/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib.history;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import chargerlib.CDateTime;
import chargerlib.General;

/**
 * An abstraction of some event in the history of some object.
 * Keeps a timestamp, description, and an object (possibly null).
 * The type is a string that sub-classes and parsers may define.
 * Sub-classes must provide a zero-argument constructor in order if 
 * they want to be fully supported when copying a complete history.
 * @see ObjectHistory#duplicateHistory() 
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public abstract class HistoryRecord {
    public static String GenericHistoryRecordType = "GENERIC";
    /** When this event was first created; should be preserved through copies, saving, etc. */
    protected CDateTime timestamp = new CDateTime();
    /** A free-form text description. */
    protected String description = "";
    /** What type event is this -- Charger reserves a few for itself. 
     * Other modules and applications should use "CUSTOM" as their type 
     * and provide their own additional XML elements as they are needed.
     */
    protected String type = null;
    /**
     * An object that's associated with this event. In a sense, it's redundant,
     * since history events are already associated with an object, but there 
     * may be cases where this is relevant.
     */
    protected Object object = null;
    
    public HistoryRecord() {
        
    }
    
    /** Copy this event's information from the event argument. 
     Subclasses are responsible for copying their own information after calling this method.
     * Doesn't have the deep responsibility of a true clone; 
     * @param he the event to receive  the values from this event.
     */
    public void copyInfoFrom( HistoryRecord he ) {
        this.setTimestamp( new CDateTime( he.getTimestamp().toString()) );
        this.setDescription( new String( he.getDescription()));
        this.setType( new String( he.getType() ));
        this.setObject( he.getObject());
    }
    
 
    /** Get the date/time that this event occurred. */
    public CDateTime getTimestamp() {
        return timestamp;
    }

    /** Set the date and time that this event occurred. */
    public void setTimestamp( CDateTime timestamp ) {
        if ( timestamp == null ) {
            System.out.println( this.getClass().getCanonicalName() + ": setTimestamp arg is null." );
        }
        this.timestamp = timestamp;
    }

    /** Get an explanatory note for this event. 
     * @return If the description is null or empty, return the empty string.
     */
    public String getDescription() {
        if ( description != null )
        return description.trim();
        else
            return "";
    }

    /** Append a new portion to the description, separated by the separator.
     * If the description is still empty, then omit the separator.
     * @param description 
     * @param separator either a newline or some xml separator.
     */
    public void appendDescription( String description, String separator ) {
        if ( description == null || description.isEmpty())
            this.description = description;
        else
            this.description = this.description + separator + description;
    }
    
    /** Uses newline as a separator by default. 
     * 
     * @param description 
     */
    public void appendDescription( String description ) {
        appendDescription( description, "\n");
    }
    
    public void setDescription( String description )  {
        this.description = "";
        appendDescription( description );
    }

    /** The string identifier for the type of this history event. */
    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject( Object object ) {
        this.object = object;
    }
    
    /** Return the history event DOM tree node for the bare history event.
     * It is wrapped in a Document for convenience. Use getDocumentElement() to extract as the top-level element.
     * Will be used by subclasses to create the tag so they can
     * add things to it.
     * @return a DOM document tree for the event.
     */
    public Document makeDocument(  ) {
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

        Element root = doc.createElement( "event" );
        doc.appendChild( root );
        root.setAttribute( "type", type );
        root.setAttribute( "class", getClass().getCanonicalName());
        // PR-218 01-02-18 hsd
//        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM );
        root.setAttribute( "timestamp", timestamp.toString() );
        
        if ( description != null /* && ! getDescription().isEmpty()  */) {
            Element descriptionElem = doc.createElement( "description" );
            descriptionElem.insertBefore( doc.createTextNode( getDescription() ), descriptionElem.getLastChild() );
            doc.getDocumentElement().appendChild( descriptionElem );
        }
        return doc;
    }

    /**
     * Create an event tag in XML for this history event.
     *
     * @param indent prepend this to every line.
     */
    public String toXML() {
        return General.toXML( makeDocument() );
    }

    public String toString() {
        StringBuffer s = new StringBuffer(   );
//        s.append( type );
        s.append( "  TYPE: " + getType() + "  " + getTimestamp().toString() + "\n" );
        if ( description != null && !getDescription().isEmpty() ) {
            s.append(  getDescription()  );
        }
        return s.toString();
    }

}
