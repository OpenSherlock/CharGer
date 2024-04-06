/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chargerlib;

import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Some static values and methods to help with html formatting
 * In general values beginnig with an underscore "_" are ending tags.
 * @author Harry Delugach
 * @since Charger 3.8.0
 */
public class Tag {
        /** The &lt;p&gt; tag */
  public static final String p = "<p>";
         /** The &lt;/p&gt; tag  with a regular ASCII newline */
 public static final String _p = "</p>" + System.getProperty( "line.separator" );
  
  public static final String p_red = "<p style=\"color:red;\">";
  
  public static final String italic = "<em>";
  public static final String _italic = "</em>";
  
         /** The &lt;br&gt; tag  with a regular ASCII newline */
  public static final String br = "<br>" + System.getProperty( "line.separator" );;
  
         /** The &lt;/strong&gt; tag   */
  public static final String bold = "<strong>";
         /** The &lt;/strong&gt; tag   */
  public static final String _bold = "</strong>";
  
          /** The &lt;ol&gt; tag  with a regular ASCII newline */
 public static final String olist = "<ol>" + System.getProperty( "line.separator" );
          /** The &lt;ol&gt; tag  with a regular ASCII newline */
 public static final String _olist = "</ol>" + System.getProperty( "line.separator" );

         /** The &lt;ul&gt; tag  with a regular ASCII newline */
  public static final String ulist = "<ul>" + System.getProperty( "line.separator" );
         /** The &lt;/ul&gt; tag  with a regular ASCII newline */
  public static final String _ulist = "</ul>" + System.getProperty( "line.separator" );
  

         /** The &lt;li&gt; tag  */
  public static final String li = "<li>";
         /** The &lt;/li&gt; tag  with a regular ASCII newline */
  public static final String _li = "</li>" + System.getProperty( "line.separator" );
  
        /** a non-breaking space */
  public static final String sp = "&nbsp;";
  
  public static final String hr = "<hr style=\"size=6px\">";
  
  public static final String div = "<div>";
  
  public static final String body = "<body style=\"font-family:Arial; \">";
  

  public static final String _body = "</body>";
  
  public static final String _table = "</table>";
  
  public static final String lightPink = "#FFCCCC";
  public static final String lighterPink = "#FFDDDD";
  public static final String lightYellow = "#FFFFBB";
  public static final String lighterYellow = "#FFFFDD";
  public static final String lightGreen = "#CCFFCC";
  public static final String lighterGreen = "#DDFFDD";
  
  private static Transformer transformer = null;
  
  public static String xmlTaggedString( String tagname, String text ) {
        Document doc;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
        } catch ( ParserConfigurationException ex ) {
            Logger.getLogger( Tag.class.toString() ).log( Level.SEVERE, null, ex );
          return null;
      }
//        
      Element elem = doc.createElement( tagname );
      doc.appendChild( elem );
      elem.appendChild( doc.createTextNode( text ) );

      String s = doc.getDocumentElement().toString();
      
      // pretty print the doc
      try {
          if ( transformer == null ) {
               transformer = TransformerFactory.newInstance().newTransformer();
              transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
              transformer.setOutputProperty( OutputKeys.INDENT, "no" );
              transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
          }
          Writer out = new StringWriter();
          transformer.transform( new DOMSource( doc ), new StreamResult( out ) );
          return out.toString();
      } catch ( TransformerConfigurationException ex ) {
          Logger.getLogger( Transformer.class.getName() ).log( Level.SEVERE, null, ex );
      } catch ( TransformerException ex ) {
          Logger.getLogger( Transformer.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return "Error in generating XML";


  }
    
  /** return a paragraph-tagged-terminated string */
  public static String p( String s ) {
      return p + s + _p;
  }
  
  public static String p_red( String s ) {
      return p_red + s + _p;
  }

  /** return a bold-tagged-terminated string */
  public static String bold( String s ) {
      return bold + s + _bold;
  }
  /** return an italic-tagged-terminated string */
  public static String italic( String s ) {
      return italic + s + _italic;
  }
  
  public static String item( String s ) {
      return li + s + _li;
  }
  
  public static String sp( int spaces ) {
      StringBuilder r = new StringBuilder("");
      for ( int i = 0; i < spaces; i++) 
          r.append( sp );
      return r.toString();
  }
  
  /**
   * Create tags to establish a div element.
   * @param styles argument to a "style" attribute for the div, not including the enclosing double quotes
   * @param s The text or other elements to be included in the div
   * @return html formatted text for the div
   */
  public static String div( String styles, String s )  {
      StringBuilder r = new StringBuilder("<div style=\"");
      r.append( styles );
      r.append( "\">");
      r.append( s );
      r.append ("</div>");
      return r.toString();
  }
  
  public static String comment( String s ) {
      String news = s;
      /*news = news.replace( "\"", "&quot;");
      news = news.replace( "<", "&lt;" );
      news = news.replace( ">", "&gt;" );*/
      return "<!-- " + news + " -->" + System.getProperty( "line.separator" );
  }
  
    /** Brackets a &lt;span&gt; begin-end tag, setting a color style for the foreground.
   * @param color Any string suitable for HTML color values
   * @param s The string to be bracketed in color
   * */
    public static String color( String color, String s ) {
      return "\n<span style=\"color: " + color + "\">" + System.getProperty( "line.separator" ) + s + "</span>" + System.getProperty( "line.separator" );
  }
  
  /**
   * Creates a begin table tag with the width specified. The end tag is NOT accounted for.
   * @param width
   * @return The tag with a width
   */
  public static String table( int width ) {
      return "<table width=" + width + " >" + System.getProperty( "line.separator" );
      
  }
  
  /** a  table row begin-end tag */
  public static String tr( String s ) {
      return "<tr>" + System.getProperty( "line.separator" ) + s + "\n</tr>" + System.getProperty( "line.separator" );
  }
  
  /** a table cell begin-end tag */
  public static String td( String s ) {
      return td( s, 1 );
  }

  /** a table cell begin-end tag 
   * @param span an integer showing how many columns to span; default is 1.
   */
  public static String td( String s, int span ) {
      return "<td colspan=\"" + span + "\">" + s + "</td>" + System.getProperty( "line.separator" );
  }
  
  /** Brackets a &lt;div&gt; begin-end tag, setting a color style for the background of the div. 
   * @param color Any string suitable for HTML color values
   * @param s The string to be bracketed in color
   * */
  
  public static String colorDiv( String color, String s ) {
      return "\n<div style=\"background: " + color + "\">" + System.getProperty( "line.separator" ) + s + "</div>" + System.getProperty( "line.separator" );
  }
  
  /** A table cell that is centered */
  public static String tdc( String s ) {
      return "<td><div style=\"text-align:center;\">" + s + "</div></td>" + System.getProperty( "line.separator" );
  }
  
  /** A table cell that is right justified */
  public static String tdr( String s ) {
      return "<td><div style=\"text-align:right;\">" + s + "</div></td>" + System.getProperty( "line.separator" );
  }
  
  /**
   * Returns a table data element that spans cols number of columns around the string 
   * @param s
   * @param cols
   * @return HTML tagged string suitable for inclusion in an HTML table
   */
  public static String tdspan( int cols, String s ) {
      return "<td colspan=\"" + cols + "\"> " + s + "</td>" + System.getProperty( "line.separator" );
  }
  
  /** Creates an html header at the level specified */
  public static String h( int level, String s ) {
      return "<h" + level + "> " + s + "</h" + level + ">" + System.getProperty( "line.separator" );
  }

}
