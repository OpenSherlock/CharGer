/*
 * Copyright 1998-2020 by Harry Delugach (UAH), Huntsville, AL 35899, USA. All Rights Reserved.
 * Unless permission is granted, this material may not be copied, reproduced or coded for reproduction
 *  by any electrical, mechanical or chemical process or combination thereof, now known or later developed.
 *
 * $Header$
 */

package charger.cgx;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.time.DateTimeException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import charger.EditFrame;
import charger.Global;
import charger.exception.CGEncodingException;
import charger.gloss.AbstractTypeDescriptor;
import charger.gloss.GenericTypeDescriptor;
import charger.gloss.wn.WordnetTypeDescriptor;
import charger.obj.Concept;
import charger.obj.GEdge;
import charger.obj.GNode;
import charger.obj.Graph;
import charger.obj.GraphObject;
import charger.obj.GraphObjectID;
import charger.util.CGUtil;
import chargerlib.CDateTime;
import chargerlib.General;
import chargerlib.history.CustomHistoryRecord;
import chargerlib.history.DerivedHistoryRecord;
import chargerlib.history.FileHistoryRecord;
import chargerlib.history.HistoryRecord;
import chargerlib.history.ObjectHistoryRecord;
import chargerlib.history.UserHistoryRecord;

/**
 * Used for parsing XML representations of a conceptual graph into its CharGer
 * internal form. Uses the DOM parsing routines.
 */
public class CGXParser extends DefaultHandler {

    Document doc = null;        // used for the experimental DOM parser

    private boolean _traceTags = false;
    private boolean _keepIDs = true;
    private boolean _ignoreLayout = false;
    /**
     * Whether to keep the top level graph intact (perhaps adding to it), but
     * retaining its created, modified, etc.
     */
    private boolean _preserveGraph = false;
    /**
     * Whether to gather all parsed objects into a arraylist (usually used for
     * selecting)
     */
    private boolean _makeList = false;
    public static Point2D.Double offsetZero = new Point2D.Double( 0.0, 0.0 );
    private Point2D.Double _offset = offsetZero;
//    private Point2D.Double _newOrigin = null;
    private ArrayList _parsedObjects = new ArrayList();
    private Graph _topLevelGraph = null;

    /**
     * Has entries of the form "nnn", "nnn" denoting the old ident (i.e. the one
     * in the file) and the new ident which has been assigned during this parse.
     * Assures that no two "new" objects have the same ident.
     */
    private Hashtable<String, String> oldNewIDs = new Hashtable<String, String>();
    String genericString = "";	// the string we just processed before an ending tag
    StringBuilder stringCollector = new StringBuilder( "" );
    WordnetTypeDescriptor wordnetDescr = null;
    GenericTypeDescriptor genericDescr = null;
    ArrayList descriptors = new ArrayList();
    String definition = null;

    /**
     * Parser works as an instance; this allows for multiple parsers to be open
     * at the same time.
     */
    public CGXParser() {
        super();
    }

    /**
     * Used for copy/paste. Encoding exceptions should never occur since internal strings are all UTF-8.
     * @param input
     * @throws CGEncodingException 
     */
    public CGXParser( String input ) throws CGEncodingException {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream( input.getBytes( "UTF-8") );
            buildDocument( is );
    } catch ( UnsupportedEncodingException ex ) {
            Logger.getLogger( CGXParser.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    public CGXParser( InputStream is ) throws CGEncodingException { // CR-1005
        buildDocument( is );
    }

    public boolean isIgnoreLayout() {
        return _ignoreLayout;
    }

    public void setIgnoreLayout( boolean _ignoreLayout ) {
        this._ignoreLayout = _ignoreLayout;
    }

    public boolean isKeepIDs() {
        return _keepIDs;
    }

    public void setKeepIDs( boolean _keepIDs ) {
        this._keepIDs = _keepIDs;
    }

    public Point2D.Double getOffset() {
        return _offset;
    }

    public void setOffset( Point2D.Double _offset ) {
        this._offset = _offset;
    }

    public boolean isMakeList() {
        return _makeList;
    }

    public void setMakeList( boolean _makeList ) {
        this._makeList = _makeList;
    }

    public boolean isPreserveGraph() {
        return _preserveGraph;
    }

    public void setPreserveGraph( boolean _preserveGraph ) {
        this._preserveGraph = _preserveGraph;
    }

    public ArrayList<GraphObject> getParsedObjects() {
        return _parsedObjects;
    }

    public void setParsedObjects( ArrayList<GraphObject> _parsedObjects ) {
        this._parsedObjects = _parsedObjects;
    }

    public Graph getParsedGraph() {
        return _topLevelGraph;
    }


    /**
     *
     * used to keep track of old and new versions of CharGer class names that
     * might appear in a ".cgx" file.
     */
    public static Properties CharGerXMLTagNameToClassName = new Properties();

    /**
     * Creates a lookup that helps parsers determine (from an XML tag) what
     * class is required. This could probably be replaced with a simple
     * Encode/Decode XML "XML serialized" version.
     *
     */
    public static void loadCharGerKeyWordToClassTable() {
        CharGerXMLTagNameToClassName.setProperty( "Graph", "Graph" );
        CharGerXMLTagNameToClassName.setProperty( "Concept", "Concept" );
        CharGerXMLTagNameToClassName.setProperty( "Relation", "Relation" );
        CharGerXMLTagNameToClassName.setProperty( "Actor", "Actor" );
        CharGerXMLTagNameToClassName.setProperty( "TypeLabel", "TypeLabel" );
        CharGerXMLTagNameToClassName.setProperty( "CGType", "TypeLabel" );
        CharGerXMLTagNameToClassName.setProperty( "RelationLabel", "RelationLabel" );
        CharGerXMLTagNameToClassName.setProperty( "CGRelType", "RelationLabel" );
        CharGerXMLTagNameToClassName.setProperty( "Arrow", "Arrow" );
        CharGerXMLTagNameToClassName.setProperty( "GenSpecLink", "GenSpecLink" );
        CharGerXMLTagNameToClassName.setProperty( "Coref", "Coref" );
        CharGerXMLTagNameToClassName.setProperty( "Note", "Note" );

        CharGerXMLTagNameToClassName.setProperty( "graph", "Graph" );
        CharGerXMLTagNameToClassName.setProperty( "concept", "Concept" );
        CharGerXMLTagNameToClassName.setProperty( "relation", "Relation" );
        CharGerXMLTagNameToClassName.setProperty( "actor", "Actor" );
        CharGerXMLTagNameToClassName.setProperty( "typelabel", "TypeLabel" );
        CharGerXMLTagNameToClassName.setProperty( "relationlabel", "RelationLabel" );
        CharGerXMLTagNameToClassName.setProperty( "arrow", "Arrow" );
        CharGerXMLTagNameToClassName.setProperty( "genspeclink", "GenSpecLink" );
        CharGerXMLTagNameToClassName.setProperty( "coref", "Coref" );
        CharGerXMLTagNameToClassName.setProperty( "customedge", "CustomEdge" );
        CharGerXMLTagNameToClassName.setProperty( "note", "Note" );
    }

    // =====================================================================================
    // ========  DOM Parser routines
    //
    /**
     * Factory method for setting up a parser for a new graph from a stream.
     *
     * @param is
     * @return true if parser instance was instantiated and all worked properly; false otherwise.
     */
    public static boolean parseForNewGraph( InputStream is, Graph g ) {
        CGXParser parser = null;
        try {
            parser = new CGXParser( is );
        } catch ( CGEncodingException ex ) {
            CGUtil.showMessageDialog( null, ex.getMessage() );
            return false;
        }
        
        parser._topLevelGraph = g;
        parser.setKeepIDs( true );
        parser.setOffset( offsetZero );
        parser.setMakeList( false );
        parser.setPreserveGraph( false );

        parser.parseCGXMLCG( g );
        return true;
    }


    /**
     * Factory method for setting up a parser for a new graph from a string. May
     * be used either for reading in an entirely new graph or for restoring one
     * (e.g., via undo).
     *
     * @param xmlString a complete cgx graph string
     * @return parser instance appropriately set up
     */
    public static void parseForNewGraph( String xmlString, Graph g ) {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream( xmlString.getBytes( "UTF-8") );
        } catch ( UnsupportedEncodingException ex ) {
            Logger.getLogger( CGXParser.class.getName() ).log( Level.SEVERE, null, ex );
        }
        parseForNewGraph( is, g );
    }

    /**
     * Factory method for setting up a parser for a list of graph objects from a
     * string. To get the list of objects, caller needs to follow up with a call
     * to getParsedObjects
     *
     * @param cgxmlString a string of an arbitrary set of objects
     * @param g the graph to which the parsed objects will be added
     * @return parser instance appropriately set up.
     */
    public static ArrayList<GraphObject> parseForCopying( String cgxmlString, Graph g ) {

        CGXParser parser = null;
        try {
            parser = new CGXParser( cgxmlString );
        } catch ( CGEncodingException ex ) {
            Logger.getLogger( CGXParser.class.getName() ).log( Level.SEVERE, null, ex );
            // CR-1005 This should never happen -- copying is internal and all internal strings are UTF-8.
            CGUtil.showMessageDialog( null, ex );
            return null;
        }
        parser.setKeepIDs( false );
        parser.setOffset( new Point2D.Double( EditFrame.xoffsetForCopy, EditFrame.yoffsetForCopy ) );
        parser.setMakeList( true );
        parser.setPreserveGraph( true );

        parser._parsedObjects.clear();

        parser.parseCGXMLCG( g );

        return parser.getParsedObjects();

//        return parser;
    }

    /**
     * Create the entire parse tree for the document.
     *
     * @param is an XML stream containing CG-XML objects.
     */
    public void buildDocument( InputStream is ) throws CGEncodingException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            
             doc = dBuilder.parse( new InputSource( is ));
             // 08-14-19 hsd trying to use for any encoding
       } catch ( ParserConfigurationException ex ) {
            Global.warning( "Parser configuration exception: " + ex.getMessage() );
        } catch ( SAXException ex ) {
            Global.warning( "SAX parser exception: " + ex.getMessage() );
        } catch ( IOException ex ) {
            // BUG: CR-1005 If not UTF-8, try to re-parse?
            Global.error( "IO exception: " + ex.getMessage() );
            throw new CGEncodingException( );
        }

    }

    /**
     * Assumes that the Document has already been built. This is the very top of
     * the parse tree processing.
     *
     * @param graph initialized but possibly empty graph
     */
    public void parseCGXMLCG( Graph graph ) {
        if ( graph == null ) {
            _topLevelGraph = new Graph();       // never used
        } else {
            _topLevelGraph = graph;
        }
        Element docelem = doc.getDocumentElement();
        if ( docelem == null ) {
            return;
        }

        if ( !docelem.getTagName().equals( "conceptualgraph" ) ) {
            Global.warning( "Outermost XML tag \"" + docelem.getTagName() + "\" should be \"conceptualgraph\"." );
        }

        String s = docelem.getAttribute( "editor" );
        if ( s == null ) {
            CGUtil.showMessageDialog( null, "This is not a CharGer file.\n" );
            return;
        } else  if ( s.startsWith( "Know" ) ) {
            CGUtil.showMessageDialog( null,
                    "<html>This is a Knowledge Capture Pro file!<br/>"
                    + "Please open with Knowledge Capture Pro<br/>"
                    + "<a href=\"http://concept.cs.uah.edu/download-selector.jsp\">Click here for more info.</a></html>"
            );
            return;
        }

        if ( !this.isPreserveGraph() ) {
//            NamedNodeMap docmap = docelem.getAttributes();
            _topLevelGraph.createdTimeStamp = docelem.getAttribute(  "created" );
            _topLevelGraph.modifiedTimeStamp = docelem.getAttribute( "modified" );;
            if ( _topLevelGraph.modifiedTimeStamp == null ) {
                _topLevelGraph.modifiedTimeStamp = _topLevelGraph.createdTimeStamp;
            }
            if ( _topLevelGraph.createdTimeStamp == null ) {
                _topLevelGraph.createdTimeStamp = _topLevelGraph.modifiedTimeStamp;
            }

             s = docelem.getAttribute( "wrapLabels" );
            if ( s != null && ! s.isEmpty()) {
                _topLevelGraph.wrapLabels = Boolean.parseBoolean( s );
            }
            s = docelem.getAttribute("wrapColumns" );
            if ( s != null  && ! s.isEmpty()) {
                _topLevelGraph.wrapColumns = Integer.parseInt( s );
            }
        }

        // TODO: if parseGraphObjectElement fails, then remove go from the graph in which it's inserted
        // this means altering parseGraphObjectElement to return a boolean for successful parse
        NodeList nodes = docelem.getChildNodes();
        for ( int childnum = 0; childnum < nodes.getLength(); childnum++ ) {
            if ( nodes.item( childnum ).getNodeType() == Node.ELEMENT_NODE ) {
                // is either a top level graph element or a graph being pasted/duplicated
                Element elem = (Element)nodes.item( childnum );
                if ( isPreserveGraph() ) {
                    GraphObject go = instantiateGraphObject( elem.getTagName() );
                    if ( go != null ) {
                        setID( elem, go );       // make sure we set the id before adding to the graph
                        _topLevelGraph.insertObject( go );  // add this object to the graph
                        if ( isMakeList() ) {
                            _parsedObjects.add( go );   // add to the list of objects to be returned
                        }
                        parseGraphObjectElement( elem, go );    // populate the object
                    }
                } else {
                    parseCGXMLGraphTagElement( elem, _topLevelGraph );
                }
            }
        }
    }

    /**
     * Handle the "graph" tag. Assumes that the element is a "graph" tag and
     * that the graph object itself is either the top level graph or it's been
     * added to the graph it belongs to.
     *
     * @param graphelem
     * @param g the graph object, already instantiated (but probably empty).
     */
    public void parseCGXMLGraphTagElement( Element graphelem, Graph g ) {
//        Global.info( "at parseCGXMLGraphElement parsing tag " + graphelem.getTagName() );
        setID( graphelem, g );
        // Handle any attributes of this graph
//        NamedNodeMap map = graphelem.getAttributes();
        Node n = graphelem.getAttributeNode( "negated" );
        if ( n != null ) {
            g.setNegated( Boolean.parseBoolean( n.getNodeValue() ) );
        }

        NodeList nodes = graphelem.getChildNodes();
        // Add all the elements of this graph to it. 
        for ( int childnum = 0; childnum < nodes.getLength(); childnum++ ) {
            if ( nodes.item( childnum ).getNodeType() == Node.ELEMENT_NODE ) {
                // should be top level graph element
                Element element = (Element)nodes.item( childnum );
                String elementName = element.getNodeName();
                if ( elementName.equals( "layout" ) ) {
                    parseLayoutInfo( element, (GraphObject)g );
                } else if ( elementName.equals( "type" ) ) {
                    parseTypeInfo( element, (GraphObject)g );
                } else if ( elementName.equals( "referent" ) ) {
                    parseReferentInfo( element, (GraphObject)g );
                } else if ( elementName.equals( "history" ) ) {
                    parseHistoryInfo( element, (GraphObject)g );
                } else {
                    GraphObject go = instantiateGraphObject( element.getTagName() );
                    if ( go != null ) {
                        setID( element, go );       // make sure we set the id before adding to the graph
                        g.insertObject( go );
                        if ( isMakeList() ) {
                            _parsedObjects.add( go );
                        }
                        parseGraphObjectElement( element, go );
                    } else {
                        Global.info( "unknown element in graph is " + element.getTagName() );
                    }
                }
            }
        }
    }

//    public void parseCGXMLInputStreamDOM( InputStream is, Graph graph ) {
//        if ( _makeList ) {
//            _parsedObjects.clear();
//        }
//        //Global.info( "ready to parse... top level graph is " + graph.toString() );
//
//        buildDocument( is );
//        parseCGXMLCG( graph );
//        try {
//            is.close();
//        } catch ( IOException ex ) {
//
//        }
//    }

    /**
     * Assume document consists of only layout information. This is primarily
     * used by the CGIF parser for CG comments.
     */
    public void parseLayoutOnly( GraphObject go ) {
        Element docelem = doc.getDocumentElement();
        if ( docelem == null ) {
            return;
        }
        parseLayoutInfo( docelem, go );
    }
    /**
     * Handles the "type" tag element for the type label and for 
     * both generic descriptors and wordnet descriptors.
     * @see WordnetTypeDescriptor#getInstanceFromXMLDOM(org.w3c.dom.Element) 
     * @see GenericTypeDescriptor#getInstanceFromXMLDOM(org.w3c.dom.Element) 
     * @param node The DOM element containing the tag and its subtree.
     * @param go the graph object to be modified
     */

    public void parseTypeInfo( Element element, GraphObject go ) {
        NodeList elems = element.getElementsByTagName( "label" );
        if ( elems != null && elems.getLength() > 0 ) {
            String label = elems.item( 0 ).getTextContent();
            ( (GNode)go ).setTypeLabel( label );
        }
        ArrayList<AbstractTypeDescriptor> descriptors = new ArrayList<>();
        elems = element.getElementsByTagName( GenericTypeDescriptor.getTagName() );
        if ( elems != null && elems.getLength() > 0 ) {
            for ( int n = 0; n < elems.getLength(); n++ ) {
                AbstractTypeDescriptor descr = GenericTypeDescriptor.getInstanceFromXMLDOM( (Element)elems.item( 0 ) );
                descriptors.add( descr );
            }
        }
        elems = element.getElementsByTagName( WordnetTypeDescriptor.getTagName() );
        if ( elems != null && elems.getLength() > 0 ) {
            for ( int n = 0; n < elems.getLength(); n++ ) {
                AbstractTypeDescriptor descr = WordnetTypeDescriptor.getInstanceFromXMLDOM( (Element)elems.item( 0 ) );
                descriptors.add( descr );
            }
        }
        ( (GNode)go ).setTypeDescriptors( descriptors.toArray( new AbstractTypeDescriptor[ descriptors.size() ] ) );
    }

        /**
     * Handles the "referent" tag element 
     * @param element The DOM element containing the tag and its subtree.
     * @param go the graph object to be modified
     */

    public void parseReferentInfo( Element element, GraphObject go ) {
        NodeList elems = element.getElementsByTagName( "label" );
        if ( elems == null || elems.getLength() == 0 ) {
            return;
        }
        String label = element.getElementsByTagName( "label" ).item( 0 ).getTextContent();
        ( (Concept)go ).setReferent( label, false );
    }

    /**
     * Handles the "layout" tag element for the rectangle, color, font and edge elements.
     * @param layoutElem The DOM element containing the tag and its subtree.
     * @param go the graph object to be modified
     */
    public void parseLayoutInfo( Element layoutElem, GraphObject go ) {
        if ( layoutElem.getNodeType() != Node.ELEMENT_NODE ) {
            return;
        }
        if ( !layoutElem.getNodeName().equals( "layout" ) ) {
            return;
        }

        // Assume we now are looking at the layout element
        NodeList nodes = layoutElem.getElementsByTagName( "rectangle" );
        if ( nodes.getLength() > 0 ) {
            Node rectnode = nodes.item( 0 );
            parseRectangleInfo( rectnode, go );
        }

        nodes = layoutElem.getElementsByTagName( "color" );
        if ( nodes.getLength() > 0 ) {
            Node colornode = nodes.item( 0 );
            parseColorInfo( colornode, go );
        }

        nodes = layoutElem.getElementsByTagName( "font" );
        if ( nodes.getLength() > 0 ) {
            Node fontnode = nodes.item( 0 );
            parseFontInfo( fontnode, go );
        }

        nodes = layoutElem.getElementsByTagName( "edge" );
        if ( nodes.getLength() > 0 ) {
            Node fontnode = nodes.item( 0 );
            parseEdgeInfo( fontnode, go );
        }

    }


    /**
     * Assumes its node is a rectangle tag from Charger. Updates the graph
     * object according to the display rect implied by the rectangle.
     * Uses the pre-set offset for the entire parser.
     * @see #parseRectangleInfo(org.w3c.dom.Node, charger.obj.GraphObject, java.awt.geom.Point2D.Double) 
     * @param node
     * @param go
     */
    public void parseRectangleInfo( Node node, GraphObject go ) {
        parseRectangleInfo( node, go, getOffset() );
    }

        /**
     * Handles the "rectangle" tag element for arrow height, width and edge thickness.
     * @param node The DOM element containing the tag and its subtree.
     * @param go the graph object to be modified
     */

    public void parseRectangleInfo( Node node, GraphObject go, Point2D.Double offset ) {
//        NamedNodeMap map = node.getAttributes();
        Element rectangleElem = (Element)node;
        double x = Double.parseDouble( rectangleElem.getAttribute(  "x" ).replaceAll( ",", "" ) ) + offset.x;
        double y = Double.parseDouble( rectangleElem.getAttribute(  "y" ).replaceAll( ",", "" ) ) + offset.y;
        double width = Double.parseDouble( rectangleElem.getAttribute(   "width" ).replaceAll( ",", "" ) );
        double height = Double.parseDouble( rectangleElem.getAttribute(  "height" ).replaceAll( ",", "" ) );
                // Since "depth" is a new (03-14-2015) basic attribute, it may not appear in older versions
        double depth = 1;
        String d = rectangleElem.getAttribute( "depth" );
        if ( d != null && ! d.isEmpty())
         depth = Double.parseDouble( d.replaceAll( ",", "" ) );
        else
            depth = 1;

        go.setDisplayRect( new Rectangle2D.Double( x, y, width, height ) );
        go.setDepth( depth );
    }

    /**
     * Assumes its node is a "color" tag generated by Charger. Updates the graph object
     * according to the color info parsed.
     *
     * @param node The DOM element containing the tag and its subtree.
     * @param go the graph object to be modified
     */
    public void parseColorInfo( Node node, GraphObject go ) {
//        NamedNodeMap map = node.getAttributes();
        Element colorElem = (Element)node;
        String foreground = colorElem.getAttribute(  "foreground" );
        String background = colorElem.getAttribute(  "background" );

        Color forecolor = parseRGB( foreground );
        go.setColor( "text", forecolor );

        Color backcolor = parseRGB( background );
        go.setColor( "fill", backcolor );

    }
    /**
     * Handles the "font" tag element for arrow height, width and edge thickness.
     * @param node The DOM element containing the tag and its subtree.
     * @param go the graph object to be modified
     */
    public void parseFontInfo( Node node, GraphObject go ) {
//        NamedNodeMap map = node.getAttributes();
        Element fontElem = (Element)node;
        String fontname = fontElem.getAttribute( "name" );
        int fontstyle = Integer.parseInt( fontElem.getAttribute(  "style" ) );
        int fontsize = Integer.parseInt( fontElem.getAttribute(  "size" ) );

        go.setLabelFont( new Font( fontname, fontstyle, fontsize ) );
    }

    /**
     * Handles the "edge" tag element for arrow height, width and edge thickness.
     * @param node The DOM element containing the tag and its subtree.
     * @param go the edge to be modified
     */
    public void parseEdgeInfo( Node node, GraphObject go ) {
//        NamedNodeMap map = node.getAttributes();
        Element edgeElem = (Element)node;
        int arrowHeadHeight = Global.userEdgeAttributes.getArrowHeadHeight();
        int arrowHeadWidth = Global.userEdgeAttributes.getArrowHeadWidth();
        double edgeThickness = Global.userEdgeAttributes.getEdgeThickness();

        if ( edgeElem.getAttribute( "arrowHeadWidth" ) != null ) {
            arrowHeadWidth = Integer.parseInt( edgeElem.getAttribute( "arrowHeadWidth" ) );
        }
        if ( edgeElem.getAttribute( "arrowHeadHeight" ) != null ) {
            arrowHeadHeight = Integer.parseInt( edgeElem.getAttribute( "arrowHeadHeight" ) );
        }
        if ( edgeElem.getAttribute(  "edgeThickness" ) != null ) {
            edgeThickness = Double.parseDouble( edgeElem.getAttribute(  "edgeThickness" ) );
        }

        if ( !( go instanceof GEdge ) ) {
            return;
        }
        GEdge ge = (GEdge)go;
        ge.setArrowHeadWidth( arrowHeadWidth );
        ge.setArrowHeadHeight( arrowHeadHeight );
        ge.setEdgeThickness( edgeThickness );
    }

    /**
     *
     * @param rgb a string of the form "rrr,ggg,bbb"
     * @return the corresponding color, or black if null argument.
     */
    public static Color parseRGB( String rgb ) {
        if ( rgb == null ) {
            return new Color( 0, 0, 0 );
        }
        StringTokenizer nums = new StringTokenizer( rgb );
        int r = Integer.parseInt( nums.nextToken( "," ) );
        int g = Integer.parseInt( nums.nextToken( "," ) );
        int b = Integer.parseInt( nums.nextToken( "," ) );
        return new Color( r, g, b );
    }

            /**
     * Handles the "history" tag element which contains one or more history event nodes..
     * @param historyElem The DOM element containing the tag and its subtree.
     * @param go the graph object to which the history belongs
     */
    public void parseHistoryInfo( Element historyElem, GraphObject go ) {
        if ( historyElem.getNodeType() != Node.ELEMENT_NODE ) {
            return;
        }
        if ( !historyElem.getNodeName().equals( "history" ) ) {
            return;
        }

        // Assume we now are looking at the history event element
        NodeList nodes = historyElem.getElementsByTagName( "event" );
        for ( int nodeNum = 0; nodeNum < nodes.getLength(); nodeNum++ ) {
            if ( nodes.item( nodeNum ).getNodeType() == Node.ELEMENT_NODE ) {
                Element element = (Element)nodes.item( nodeNum );
                parseHistoryRecordInfo( element, go );
            }
        }
    }

    /** Parse an individual history event tag and add the history event to the given object. 
     * Parses all history events that CharGer knows about.
     * If the type is not recognized, then ignore it.
     * TODO: figure out how to
     * instantiate ones it doesn't know...
     *
     * @param eventElem a DOM node with an "event" tag.
     * @param go
     */
    public void parseHistoryRecordInfo( Element eventElem, GraphObject go ) {
        HistoryRecord newEvent = null;
//        NamedNodeMap eventElemAttrMap = eventElem.getAttributes();
        String eventType = eventElem.getAttribute( "type" );
//        String className = getNamedAttributeFromMap( map, "class" );
        if ( eventType == null ) {
            return;
        } else {
            if ( eventType.equalsIgnoreCase( "CUSTOM")) {
//            try {
//                // check if it's a known class of history event; if not, then create an unknown history event
//                // and store the event element un-processed
//                Class c = Class.forName( className );
//            } catch ( ClassNotFoundException ex ) {
                newEvent = new CustomHistoryRecord();
                ( (CustomHistoryRecord)newEvent ).setDomElement( eventElem );
//            }
            } else {      // if the event is a known class, then proceed to instantiate it.
                String gid = null;
                if ( eventElem.getElementsByTagName( "id" ) == null ) {
                    gid = ObjectHistoryRecord.DEFAULT_ID;
                }
                gid = (String)( eventElem.getElementsByTagName( "id" ).item( 0 ).getTextContent() );
                String className = eventElem.getAttribute( "class" );

                if ( className.contains( "FileHistoryRecord" ) ) {
                    String filename = (String)( eventElem.getElementsByTagName( "file" ).item( 0 ).getTextContent() );
                    File f = new File( filename );
                    newEvent = new FileHistoryRecord( go, f );
                    ( (FileHistoryRecord)newEvent ).setFile( new File( filename ) );
                } else if ( className.contains( "UserHistoryRecord" ) ) {
                    String username = (String)( eventElem.getElementsByTagName( "user" ).item( 0 ).getTextContent() );
                    newEvent = new UserHistoryRecord( go, username );
                    ( (UserHistoryRecord)newEvent ).setID( gid );
                    ( (UserHistoryRecord)newEvent ).setUser( username );

                } else if ( className.contains( "DerivedHistoryRecord" ) ) {
                    newEvent = new DerivedHistoryRecord( go, "", "" );      // set these later
                    ( (DerivedHistoryRecord)newEvent ).setID( gid );
                } else {    // it must be some unknown type event used by a plug-in module or other class, so do your best...
                    newEvent = new ObjectHistoryRecord( go );
                }
            }
            

            String dateString = eventElem.getAttribute( "timestamp" );
//            DateFormat df = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.MEDIUM );
            try {
            CDateTime eventDate = new CDateTime( dateString );
                newEvent.setTimestamp( eventDate );
            } catch ( DateTimeException ex ) {
                Logger.getLogger( CGXParser.class.getName() ).log( Level.SEVERE, null, ex );
            }
            newEvent.setType( eventType );
            String description = General.getXmlText( eventElem, "description" );
            newEvent.setDescription( description );
        }
        go.addHistoryRecord( newEvent );
    }

//    /**
//     * Convenience method to retrieve the value of the attribute.
//     *
//     * @param map Usually obtained from a getAttributes call in the DOM parser
//     * routines.
//     * @param attrName the attribute whose value is requested.
//     * @return the value of the named attribute; null if not found.
//     */
//    private String getNamedAttributeFromMap( NamedNodeMap map, String attrName ) {
//        String s = map.getNamedItem( attrName ) == null ? null : map.getNamedItem( attrName ).getNodeValue();
//        return s;
//    }

    /**
     * Factory to create a graph object from the given tagname.
     * The tagname is the not the actual name of the class, but can be looked up in the tagname to classname map.
     * Instantiates the object, but doesn't fill in any information.
     * @see #CharGerXMLTagNameToClassName
     * @return an instantiated GraphObject of type indicated by the tagname; null if the name isn't recognized,
     */
    private GraphObject instantiateGraphObject( String tagname ) {
//        Global.info( "Instantiating object of type " + tagname );
        if ( CharGerXMLTagNameToClassName.isEmpty() ) {
            loadCharGerKeyWordToClassTable();
        }
        String t = CharGerXMLTagNameToClassName.getProperty( tagname, "DUMMY" );
        if ( t.equals( "DUMMY" ) ) {
            return null;
        }

        GraphObject go = null;
        Class objClass = null;
        try {
            objClass = Class.forName( "charger.obj." + t );
            go = (GraphObject)objClass.newInstance();
        } catch ( ClassNotFoundException ex ) {
            Global.error( "Parsing an illegal object tag " + tagname );
        } catch ( InstantiationException ex ) {
            Global.error( "Parsing an illegal object tag " + tagname );
        } catch ( IllegalAccessException ex ) {
            Global.error( "Parsing an illegal object tag " + tagname );
        }

        return go;
    }

    /**
     * Set the object's id, either by using its already-generated id or else by
     * the id attribute in the element. If not keeping old id's then put the new
     * id in the old-new id hashtable.
     *
     * @param elem Any element that might contain an "id" attribute
     * @param go The object whose id is to be set.
     */
    public void setID( Element elem, GraphObject go ) {
//        NamedNodeMap docmap = elem.getAttributes();
        String oldID = elem.getAttribute( "id" );
        if ( oldID == null ) {
            return;        // a special case when copying, since the top level element has no info
        }
        if ( !isKeepIDs() ) {
            oldNewIDs.put( oldID, go.objectID.toString() );
        } else {
            go.objectID = new GraphObjectID( oldID );
        }
    }

    /**
     * Handles any document element representing a GraphObject.
     *
     * @param goelem The graphobject tag element. Uses the tag name to determine which type argument "go" is.
     * @param go The graph object whose properties are to be set from this. Must be already instantiated.
     * element.
     */
    public void parseGraphObjectElement( Element goelem, GraphObject go ) {
        String classname = CharGerXMLTagNameToClassName.getProperty( goelem.getTagName(), "DUMMY" );
        if ( !go.getClass().getName().endsWith( classname ) ) {
            Global.error( "CGXParser: processing xml tag \"" + goelem.getTagName()
                    + "\" with object type \"" + go.getClass().getName() + "\"." );
            return;
        }

        if ( go instanceof Graph ) {
            parseCGXMLGraphTagElement( goelem, (Graph)go );
            return;
        }
        // parse the tags that are enclosed
        NodeList nodes = goelem.getChildNodes();
        for ( int childnum = 0; childnum < nodes.getLength(); childnum++ ) {
            if ( nodes.item( childnum ).getNodeType() == Node.ELEMENT_NODE ) {
                // should be top level graph element
                Element element = (Element)nodes.item( childnum );
                String elementName = element.getNodeName();
                if ( elementName.equals( "type" ) ) {
                    parseTypeInfo( element, go );
                }
                if ( elementName.equals( "referent" ) ) {
                    parseReferentInfo( element, go );
                }
                if ( elementName.equals( "layout" ) ) {
                    parseLayoutInfo( element, go );
                  } 
                if ( elementName.equals( "history" ) ) {
                    parseHistoryInfo( element, go );
              }
            }
        }

        // parse the attributes in the tag
//        NamedNodeMap docmap = goelem.getAttributes();

        if ( go instanceof GEdge ) {
            String label = goelem.getAttribute( "label" );
            go.setTextLabel( label );
            String to = goelem.getAttribute( "to" );
            String from = goelem.getAttribute( "from" );
            if ( !isKeepIDs() ) {
                to = oldNewIDs.get( to );
                from = oldNewIDs.get( from );
            }
            if ( to == null || from == null ) {
                Global.error( "Error in parsing graph edge!" );
            } else {
                GEdge ge = (GEdge)go;
                ge.fromObj = _topLevelGraph.findByID( new GraphObjectID( from ) );
                ge.toObj = _topLevelGraph.findByID( new GraphObjectID( to ) );
                ( (GNode)( ge.fromObj ) ).attachGEdge( ge );
                ( (GNode)( ge.toObj ) ).attachGEdge( ge );
                ge.placeEdge();
            }

        }
    }
}
