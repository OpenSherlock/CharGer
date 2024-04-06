/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib.history;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import chargerlib.General;

/**
 * Represents the provenance and subsequent transformations of a given object.
 * @see GraphObjectHistoryRecord
 * 
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class ObjectHistory {
    
    /**
     * List of history events for an object. Should always add to the end, in chronologically ascending order.
     */
    private ArrayList<HistoryRecord> history = new ArrayList();
    
    public void addHistoryRecord( HistoryRecord he ) {
        if ( he != null )
            history.add( he );
    }
    
    /** Returns the list of history events for this history.
     * 
     * @return 
     */
    public ArrayList<HistoryRecord> getHistoryEvents() {
        return history;
    }

    /** Make a copy of this history, using a clone-like copy for each event.
     * This method relies on each event class concerned having a zero-argument constructor to be instantiated for copy.
     * If any event in the history does not have such a constructor, then a message is displayed and 
     * the offending event is not instantiated, 
     * but the copy of succeeding events should proceed.
     * @see HistoryRecord
     * @return A copy of this history, with  new instances (copies) of all events in its list.
     */
    public ObjectHistory duplicateHistory() {
        ObjectHistory oh = new ObjectHistory();
        for ( HistoryRecord he : history ) {
            try {
                HistoryRecord newevent = he.getClass().newInstance();
                            // When instantiating a subclass of HistoryEvent, I thought it would be invoking the subclass's
                            // method, but because the subclass argument is of the subclass type,
                            // it's actually overloading, not overriding the HistoryEvent's method.
                            // Was solved by using reflection methods.
                  newevent.copyInfoFrom( he );

                Method copier = he.getClass().getMethod( "copyInfoFrom", he.getClass() );
                copier.invoke( newevent, he );
                
                oh.addHistoryRecord( newevent);
            } catch ( InstantiationException ex ) {
                System.out.println( "Event of type " + he.getClass().getCanonicalName() + " could not be instantiated for duplicate history.");
            } catch ( IllegalAccessException ex ) {
                System.out.println( "Event of type " + he.getClass().getCanonicalName() + " could not be instantiated for duplicate history.");
            } catch ( NoSuchMethodException ex ) {
                Logger.getLogger( ObjectHistory.class.getName() ).log( Level.SEVERE, null, ex );
            } catch ( SecurityException ex ) {
                Logger.getLogger( ObjectHistory.class.getName() ).log( Level.SEVERE, null, ex );
            } catch ( IllegalArgumentException ex ) {
                Logger.getLogger( ObjectHistory.class.getName() ).log( Level.SEVERE, null, ex );
            } catch ( InvocationTargetException ex ) {
                Logger.getLogger( ObjectHistory.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }
        return oh;
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }
    
    /**
     * Get the last event in the list (which should be the most recent).
     * @return the last event in the history; null if there's no history.
     */
    public HistoryRecord getLastEvent() {
        if ( history != null )
            return history.get( history.size() - 1 );
        else
            return null;
    }
    
    /**
     * Finds the oldest (first) history event of the given type.

     * @param type
     * @return the first history event of the given type found in the history list for an object.
     * null if there isn't one
     */
    public  HistoryRecord getOldestEventByType( String type ) {
        for ( HistoryRecord he : history ) {
            if ( he.getType().equalsIgnoreCase( type ))
                return he;
        }
        return null;
    }
    
    /**
     * Finds the most recent (last) history event of the given type.
     * If there is more than one, only one is chosen (probably the first one)
     * @param type
     * @return the first history event of the given type found in the history list for an object.
     * null if there isn't one
     */
    public  HistoryRecord getNewestEventByType( String type ) {
        HistoryRecord toReturn = null;
        for ( HistoryRecord he : history ) {
            if ( he.getType().equalsIgnoreCase( type ))
                toReturn = he;
        }
        return toReturn;
    }
    
    /**
     * Finds all events of the given type
     * @param type case-insensitive indicator of the type
     * @return all events of the given type. If none found, then arraylist is empty, never null.
     */
    public ArrayList<HistoryRecord> getEventsByType( String type ) {
        ArrayList<HistoryRecord> events = new ArrayList<>();
        for ( HistoryRecord he : history ) {
            if ( he.getType().equalsIgnoreCase(type ))
                events.add( he );
        }
        return events;
    }
    
    /**
     * Finds all events of the given type
     * @param type case-insensitive indicator of the type
     * @return all events of the given type. If none found, then arraylist is empty, never null.
     */
    public ArrayList<HistoryRecord> getEventsByClass( Class c ) {
        ArrayList<HistoryRecord> events = new ArrayList<>();
        for ( HistoryRecord he : history ) {
            if ( he.getClass() == c )
                events.add( he );
        }
        return events;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer( "" );
        for ( HistoryRecord he : history ) {
            sb.append( he.toString() + "\n");
        }
        return sb.toString();
    }
    
    public String toXML( String indent ) {
        if ( isEmpty() )
            return "";
//        StringBuffer sb = new StringBuffer( "" );
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
// create the root element node
        Element root = doc.createElement("history");

        // insert each of the events as child nodes, invoking its constructor
        for ( HistoryRecord he : history ) {
            Element elem = (Element)doc.adoptNode( he.makeDocument().getDocumentElement() );
//            JOptionPane.showMessageDialog( null, chargerlib.Util.toXML(elem ));
            root.appendChild( elem );
        }
        doc.appendChild( root );
        return General.toXML( doc );

//        return sb.toString();

    }
}
