/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
//import jdk.nashorn.internal.objects.Global;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Some generally useful utilities
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class General {
//    public static Color chargerBlueColor = new Color( 0, 94, 192 );
        public static Color chargerBlueColor = new Color( 0, 111, 185 );
    private static Border raisedBevel = BorderFactory.createRaisedBevelBorder();
    private static Border loweredBevel = BorderFactory.createLoweredBevelBorder();
    public static Border BeveledBorder = BorderFactory.createCompoundBorder( raisedBevel, loweredBevel );
    public static boolean infoOn = true;
    public static int defaultMaxUndoLevels = 15;

        /**
     * Key to use with shortcut commands; i.e., CNTL on PC, Cmd on Mac
     */
    public static int AcceleratorKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    public static BasicStroke defaultStroke = new BasicStroke( 1.5f );
//    public static SimpleDateFormat MDYdateFormat = new SimpleDateFormat( "M-dd-yy" );

      /**
     * Displays a status message on System.out
     *
     * @param s the status message to be shown
     * @see Global#infoOn
     * @see Global#ShowBoringDebugInfo
     */
    public static void info( String s ) {
        if ( !infoOn  ) {
            return;
        }
        if ( s.equals( "" ) ) {
            System.out.println( "" );
        } else {
            System.out.println( "INFO-MSG: " + s );
        }
    }

        /**
     * Displays a warning message on System.out
     *
     * @param s the warning message to be shown
     */
    public static void warning( String s ) {
        System.out.println( "WARNING: " + s );
    }

      /**
     * Displays an error message on System.out
     *
     * @param s the error message to be shown
     */
    public static void error( String s ) {
        System.out.println( "Internal ERROR: " + s );
    }

        /**
     * Displays a status message on System.out
     *
     * @param s the status message to be shown
     */
    public static void consoleMsg( String s ) {
        if ( s.equals( "" ) ) {
            System.out.println( "" );
        } else {
            System.out.println( "CharGer: " + s );
        }
    }
    /**
     * Displays the text string centered about the point x,y in the given color.
     * taken from p 586, Naughton and Schildt, 1996
     *
     * @param g the current Graphics context
     * @param s the string to be displayed
     * @param x horizontal position of the centerpoint (in pixels)
     * @param y vertical position of the centerpoint (in pixels)
     * @param c the color to be displayed
     */
    static public void drawCenteredString( Graphics2D g, String s, double x, double y, Color c ) {
        FontMetrics fm = g.getFontMetrics();
        double startx = x - ( ( fm.stringWidth( s ) ) / 2 );
//        float starty = y  +  ( fm.getAscent() + fm.getDescent() + fm.getLeading() ) / 2;
        double starty = y - 1 + ( fm.getDescent() + fm.getAscent() ) / 2;
        Color oldcolor = g.getColor();
        g.setColor( c );
        g.drawString( s, (float)startx, (float)starty );
        g.setColor( oldcolor );
    }

    /**
     * Displays a centered text string in black
     *
     * @see CGUtil#drawCenteredString
     */
    static public void drawCenteredString( Graphics2D g, String s, double x, double y ) {
        drawCenteredString( g, s, x, y, Color.black );
    }

    static public void drawCenteredString( Graphics2D g, String s, Point2D.Double p, Color c ) {
        drawCenteredString( g, s, p.x, p.y, c );
    }

    /**
     * Queries the user to find a file to be used for input.
     *
     * @param sourceDirectoryFile starting point directory (user may change)
     * @param filter allows the viewing of only certain files.
     * @return an existing file, possibly unreadable; <code>null</code> if user
     * cancels.
     */
    public static File queryForInputFile( String query, File sourceDirectoryFile, javax.swing.filechooser.FileFilter filter ) {
        File sourceFile = null;
        JFileChooser filechooser = new JFileChooser( sourceDirectoryFile );
        filechooser.setDialogTitle( query );
        filechooser.setFileFilter( filter );
        filechooser.setCurrentDirectory( sourceDirectoryFile );
        int returned = filechooser.showOpenDialog( null );
        if ( returned == JFileChooser.APPROVE_OPTION ) {
            sourceFile = filechooser.getSelectedFile();
            return sourceFile.getAbsoluteFile();
        } else {
            return null; // throw new CGFileException( "user cancelled." );
        }
    }

//    public static String getFormattedCurrentDateTime() {
//    // PR-218 01-02-18 hsd
////        Date now = Calendar.getInstance().getTime();
////        String today = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale( "en", "us" ) ).format( now );
//        String today = new CDateTime().formatted( Global.ChargerDefaultDateTimeStyle );
//        return today;
//    }

    public static void setTableColumnWidth( TableColumn col, int width ) {
        col.setWidth( width );
        col.setMaxWidth( width );
        col.setMinWidth( width );
        col.setPreferredWidth( width );
    }

    /**
     * Queries the user to find a file to be used for input.
     *
     * @param query the title for the querying dialog
     * @param initialDirectory path for the directory where we'll start trying
     * to query
     * @param filename initial choice of where to point the saving operation
     * (user may change)
     * @return an existing file, possibly unreadable; <code>null</code> if user
     * cancels.
     */
    public static File queryForOutputFile( String query, File initialDirectory, String filename ) {
        File destinationFile = null;
        JFileChooser filechooser = new JFileChooser( initialDirectory );
        boolean fileOkay = false;
        filechooser.setDialogTitle( query );
        if ( filename != null ) {
            filechooser.setSelectedFile( new File( filename ) );
        }
        int userAnswer = JOptionPane.CLOSED_OPTION;
        File chosenOne = null;
        while ( !fileOkay ) {
            userAnswer = JOptionPane.CLOSED_OPTION;
            File dummy = filechooser.getSelectedFile();
            filechooser.setSelectedFile( dummy );
            userAnswer = filechooser.showSaveDialog( null );
            if ( userAnswer == JFileChooser.APPROVE_OPTION ) {
                chosenOne = filechooser.getSelectedFile();
                if ( chosenOne.exists() ) {
                    JTextArea wrappedName = new JTextArea( "The file \n\"" + chosenOne.getAbsolutePath() + "\"\n\nalready exists. Replace it?", 0, 30 );
                    wrappedName.setLineWrap( true );
                    userAnswer = JOptionPane.showConfirmDialog( filechooser, wrappedName, query, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
                    if ( userAnswer == JOptionPane.YES_OPTION ) {
                        fileOkay = true;
                    }
                } else {
                    fileOkay = true;
                }
            } else {
                fileOkay = true;
                chosenOne = null;
            }
        }
        if ( userAnswer == JFileChooser.APPROVE_OPTION ) {
            destinationFile = filechooser.getSelectedFile();
            return destinationFile.getAbsoluteFile();
        } else {
            return null; // throw new CGFileException( "user cancelled." );
        }
    }

    /**
     * Identifies the extension part of a filename string.
     *
     * @param filename any string
     * @return suffix extension (not including'.'); null if no suffix
     */
    public static String getFileExtension( String filename ) {
        int dot = filename.lastIndexOf( "." );
        if ( dot == -1 ) {
            return null;
        }
        return filename.substring( dot + 1, filename.length() );
    }
    
    
    

    /**
     * Returns the contents of a file after first deciding whether to load from
     * a jar file or from a directory and class hierarchy. If Charger is run by
     * invoking its .jar file (the usual way), then this function assumes the
     * JAR file was created on a Mac or Linux system with Mac or Linux path
     * element separators. That is, on a Windows platform, it's still looking
     * for folder/filename even though its real file name ought to be
     * folder\filename.
     *
     * @param desiredFilename file name of the text file, relative to the
     * classpath
     * @param desiredFoldername folder name containing the text file
     * @return contents of the file as a string
     */
    public static String getFileFromClassPath( String desiredFoldername, String desiredFilename ) {
        String classpath = System.getProperty( "java.class.path" );
        String[] classPathSegment = classpath.split( File.pathSeparator );
        ImageIcon ii = new ImageIcon();
        for ( int cpsIndex = 0; cpsIndex < classPathSegment.length; cpsIndex++ ) {
            if ( classPathSegment[cpsIndex].endsWith( ".jar" ) ) {
                General.info( "jar file named: " + classPathSegment[cpsIndex] );
                try {
                    JarFile jarfile = new JarFile( classPathSegment[cpsIndex] );
                    Enumeration entries = jarfile.entries();
                    while ( entries.hasMoreElements() ) {
                        ZipEntry entry = (ZipEntry)entries.nextElement();
                        General.info( "entry " + entry.getName() );
                    }
                    JarResources jar = new JarResources( classPathSegment[cpsIndex] );
                    String filename = desiredFilename.replace( File.separator.charAt( 0 ), '/' );
                    String foldername = desiredFoldername;
                } catch ( IOException e ) {
                    General.info( "problem loading icon from jarfile " + classPathSegment[cpsIndex] + ": " + e.getMessage() );
                }
            } else {
                //                File imageFile = new File( "Bad_Path_Name" );
                //                imageFile = new File( classPathSegment[cpsIndex] + File.separator + gifname );
                //
                //                if ( imageFile != null ) {
                //                    ii = new ImageIcon( imageFile.getAbsolutePath() );
                ////                    LibGlobal.info( "Found \"" + gifname + "\" in  folder \"" + classPathSegment[cpsIndex] + "\".");
                //                    return ii;
                //                } else {
                //                    LibGlobal.consoleMsg( "image file directory not found for "
                //                            + gifname );
                //                }
                //
            }
        }
        return "";
    }

    /**
     * Creates a vector of a given size where every element has the same value.
     *
     * @param theSize The desired vector size
     * @param value The value of each element
     * @return a vector with the appropriate values.
     */
    public static ArrayList repeatingVector( int theSize, Object value ) {
        ArrayList rVector = new ArrayList();
        int k;
        for ( k = 0; k < theSize; k++ ) {
            rVector.add( value );
        }
        return rVector;
    }

    /**
     * Handles the choosing of an input file.
     *
     * @param filename the initial selection of a file; <code>null</code> if
     * none is known
     * @param sourceDirectoryFile initial directory (user may change)
     * @param filter allows the viewing of only certain files.
     * @return an absolute <code>File</code>, possibly permission-protected.
     */
    public static File chooseInputFile( String query, String filename, File sourceDirectoryFile, javax.swing.filechooser.FileFilter filter ) {
        File sourceFile = null;
        File sourceAbsoluteFile = null;
        if ( filename != null ) {
            sourceFile = new File( filename );
            if ( sourceFile.isAbsolute() ) {
                sourceAbsoluteFile = sourceFile;
            } else {
                sourceAbsoluteFile = new File( sourceDirectoryFile, sourceFile.getName() );
            }
            sourceDirectoryFile = sourceAbsoluteFile.getParentFile();
        } else {
            sourceAbsoluteFile = queryForInputFile( query, sourceDirectoryFile, filter );
        }
        return sourceAbsoluteFile;
    }

    /** Determine whether there's another screen, and if so, get the x,y of its bounds.
     *  If there are more than two screens, will return an origin for any one other than the main screen.
     * @return 0,0 if there isn't a second screen.
     */
    public static Point get2ndScreenOrigin() {
        Rectangle bounds = getScreenBounds( false );
        if ( bounds != null ) {
            return new Point( bounds.x, bounds.y );
        } else {
            return new Point( 0, 0 );
        }
    }

    /**
     * Handles the choosing of an input file by the user, with possible user
     * intervention.
     *
     * @param query the prompt string for the user
     * @param filename the initial selection of a file; <code>null</code> if
     * none is known. If it's an absolute path name, then the directory is
     * obtained from it, otherwise some implementation-dependent directory is
     * chosen.
     * @return an absolute <code>File</code>, possibly permission-protected.
     */
    public static File chooseOutputFile( String query, String filename ) {
        File destinationFile = null;
        File destinationAbsoluteFile = null;
        File destinationDirectoryFile = null;
        if ( filename != null ) {
            destinationFile = new File( filename );
            if ( destinationFile.isAbsolute() ) {
                destinationAbsoluteFile = destinationFile;
            } else {
                destinationAbsoluteFile = new File( destinationFile.getAbsoluteFile().getAbsolutePath() );
            }
            destinationDirectoryFile = destinationAbsoluteFile.getParentFile();
        }
        {
            destinationAbsoluteFile = queryForOutputFile( query, destinationDirectoryFile, filename );
        }
        return destinationAbsoluteFile;
    }

    /** Find the bounds of one of the available screens.
     *
     * @param useMainScreen whether to get the bounds of the main screen or not
     * @return if true, return the main screen (x=0,y=0) bounds, if false, then the first non-main screen.
     */
    public static Rectangle getScreenBounds( boolean useMainScreen ) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        for ( GraphicsDevice dev : screens ) {
            if ( dev.getDefaultConfiguration().getBounds().x == 0 && dev.getDefaultConfiguration().getBounds().y == 0 ) {
                if ( useMainScreen || screens.length < 2 ) {
                    return dev.getDefaultConfiguration().getBounds();
                }
            } else {
                if ( !useMainScreen ) {
                    return dev.getDefaultConfiguration().getBounds();
                }
            }
        }
        return null;
    }

    /**
     * Returns a printable string to show the time elapsed since a given time.
     *
     * @param time Start time
     * @return number of millisecons since the start time
     */
    public static long elapsedTime( long time ) {
        return System.currentTimeMillis() - time;
    }

    /**
     * Returns the icon image after first deciding whether to load from a jar
     * file or from a directory and class hierarchy. If a jar file, then see if
     * there's a directory named for Hub.gifpath. If Charger is run by invoking
     * its .jar file (the usual way), then this function assumes the JAR file
     * was created on a Mac or Linux system with Mac or Linux path element
     * separators. That is, on a Windows platform, it's still looking for
     * GIF/filename even though its real file name ought to be GIF\filename.
     *
     * @param gifname file name of the icon's image, relative to the classpath
     */
    public static ImageIcon getIconFromClassPath( String gifname ) {
        String classpath = System.getProperty( "java.class.path" );
        String[] classPathSegment = classpath.split( File.pathSeparator );
        ImageIcon ii = new ImageIcon();
        for ( int cpsIndex = 0; cpsIndex < classPathSegment.length; cpsIndex++ ) {
            if ( classPathSegment[cpsIndex].endsWith( ".jar" ) ) {
                try {
                    JarResources jar = new JarResources( classPathSegment[cpsIndex] );
                    String osXgifname = gifname.replace( File.separator.charAt( 0 ), '/' );
                    byte[] im = jar.getResource( osXgifname );
                    if ( im != null ) {
                        ii = new ImageIcon( ImageIO.read( new ByteArrayInputStream( im ) ) );
                        im = null;
                        General.info( "Found \"" + gifname + "\" in jar file \"" + classPathSegment[cpsIndex] + "\"." );
                        return ii;
                    }
                } catch ( IOException e ) {
                    General.info( "problem loading icon from jarfile " + classPathSegment[cpsIndex] + ": " + e.getMessage() );
                }
            } else {
                File imageFile = new File( "Bad_Path_Name" );
                imageFile = new File( classPathSegment[cpsIndex] + File.separator + gifname );
                if ( imageFile != null ) {
                    byte[] imbytes = new byte[ (int)imageFile.length() ];
                    try {
                        new FileInputStream( imageFile ).read( imbytes );
                        ii = new ImageIcon( ImageIO.read( new ByteArrayInputStream( imbytes ) ) );
                    } catch ( FileNotFoundException ex ) {
                        Logger.getLogger( General.class.getName() ).log( Level.SEVERE, null, ex );
                    } catch ( IOException ex ) {
                        Logger.getLogger( General.class.getName() ).log( Level.SEVERE, null, ex );
                    }
                    General.info( "Found \"" + gifname + "\" in  folder \"" + classPathSegment[cpsIndex] + "\"." );
                    return ii;
                } else {
                    General.consoleMsg( "image file directory not found for " + gifname );
                }
            }
        }
        return ii;
    }


    /**
     * Extracts the suffixed filename part of a supposed file name string, incl
     * extensions.
     *
     * @param s supposed file name string
     * @return filename part of the string; if no path, then returns s
     */
    public static String getSimpleFilename( String s ) {
        if ( s == null ) {
            return "";
        }
        String fname = s;
        String pathname = "";
        int fileSepIsAt = s.lastIndexOf( System.getProperty( "file.separator" ) );
        if ( fileSepIsAt > 0 ) {
            fname = new String( s.substring( fileSepIsAt + 1, s.length() ) );
        }
        fileSepIsAt = fname.lastIndexOf( File.separator );
        if ( fileSepIsAt > 0 ) {
            fname = new String( fname.substring( fileSepIsAt + 1, fname.length() ) );
        }
        return fname;
    }

    public static void hideTableColumn( TableColumn col ) {
        setTableColumnWidth( col, 0 );
    }

    /** Pretty print the doc as given.
     *
     * @param doc Any DOM document
     * @return a pretty-printed string, omitting an XML header
     */
    public static String toXML( Node doc ) {
        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
            tf.setOutputProperty( OutputKeys.INDENT, "yes" );
            tf.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
            tf.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );
            Writer out = new StringWriter();
            tf.transform( new DOMSource( doc ), new StreamResult( out ) );
            return out.toString();
        } catch ( TransformerConfigurationException ex ) {
            Logger.getLogger( General.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( TransformerException ex ) {
            Logger.getLogger( General.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return "Error in generating XML";
    }

    public static void showArrayList( ArrayList v ) {
        General.info( "ArrayList has " + v.size() + " elements." );
        for ( int k = 0; k < v.size(); k++ ) {
            General.info( "ArrayList Element " + k + " = " + v.get( k ) );
        }
        General.info( "ArrayList finished " + v.size() + " elements." );
    }

    //    public static Point2D.Float make2DFloat( Point2D.Double p) {
    //        return new Point2D.Float( (float)p.x, (float)p.y );
    //    }
    //
    /** Converts a number to its ordinal description. For example, the value 1 become "1st" etc. */
    public static String ordinal( int num ) {
        if ( num < 0 ) {
            return "minus " + ordinal( Math.abs( num ) );
        }
        if ( num == 0 ) {
            return "0th";
        }
        if ( num == 1 ) {
            return "1st";
        }
        if ( num == 2 ) {
            return "2nd";
        }
        if ( num == 3 ) {
            return "3rd";
        } else {
            return num + "th";
        }
    }

    public static String displayAsHex( byte[] bytes ) {
        StringBuffer s = new StringBuffer( "{ " );
        for ( byte b : bytes ) {
            s.append( String.format( "%02X ", b ) );
        }
        return s.toString() + " }";
    }

    /**
     *
     * @param s
     * @param startPos the index (starting at 0) of the start of the string to
     * remove
     * @param endPos the index (starting at 0) of the start of the string to
     * remove
     * @return The resulting string after removal
     */
    public static String removeSubstring( String s, int startPos, int endPos ) {
        if ( endPos <= startPos ) {
            return s;
        }
        if ( startPos == 0 ) {
            return s.substring( endPos + 1 );
        }
        String s1 = s.substring( 0, startPos - 1 );
        String s2 = s.substring( endPos + 1 );
        return s1 + s2;
    }

    /** Figure out the point where the two lines intersect.
     *
     * @param pLine1
     * @param pLine2
     * @return null, if the lines do not intersect.
     */
    public static Point2D.Double get_line_intersection( Line2D.Double pLine1, Line2D.Double pLine2 ) {
        Point2D.Double result = null;
        double s1_x = pLine1.x2 - pLine1.x1;
        double s1_y = pLine1.y2 - pLine1.y1;
        double s2_x = pLine2.x2 - pLine2.x1;
        double s2_y = pLine2.y2 - pLine2.y1;
        double s = ( -s1_y * ( pLine1.x1 - pLine2.x1 ) + s1_x * ( pLine1.y1 - pLine2.y1 ) ) / ( -s2_x * s1_y + s1_x * s2_y );
        double t = ( s2_x * ( pLine1.y1 - pLine2.y1 ) - s2_y * ( pLine1.x1 - pLine2.x1 ) ) / ( -s2_x * s1_y + s1_x * s2_y );
        if ( s >= 0 && s <= 1 && t >= 0 && t <= 1 ) {
            result = new Point2D.Double( (int)( pLine1.x1 + ( t * s1_x ) ), (int)( pLine1.y1 + ( t * s1_y ) ) );
        }
        return result;
    }

    /**
     * For each column of a JTable, scans every entry and determines the maximum
     * width for the column. Also checks the header row labels.
     *
     * @param table the table to be adjusted.
     */
    public static void adjustTableColumnWidths( JTable table ) {
        TableColumn col = null;
        for ( int c = 0; c < table.getColumnCount(); c++ ) {
            col = table.getColumnModel().getColumn( c );
            Component comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent( table, col.getHeaderValue(), false, false, -1, c );
            int colWidth = 0;
            for ( int r = 0; r < table.getRowCount(); r++ ) {
                comp = table.getDefaultRenderer( String.class ).getTableCellRendererComponent( table, table.getModel().getValueAt( r, c ), false, false, r, c );
                colWidth = Math.max( colWidth, comp.getPreferredSize().width );
                col.setPreferredWidth( colWidth );
            }
        }
    }

    /**
     * Prints a string with a prefix, deciding whether to use "a" or "an" for
     * the article, based on the string. For example
     * <code>a_or_an( "element" )</code> returns
     * <code> "an element"</code>, whereas
     * <code>a_or_an( "person" )</code> returns
     * <code> "a person"</code>
     */
    public static String a_or_an( String s ) {
        String sl = s.toLowerCase();
        String article = "a";
        if ( sl.startsWith( "a" ) ) {
            article = "an";
        }
        if ( sl.startsWith( "e" ) ) {
            article = "an";
        }
        if ( sl.startsWith( "i" ) ) {
            article = "an";
        }
        if ( sl.startsWith( "o" ) ) {
            article = "an";
        }
        if ( sl.startsWith( "u" ) ) {
            article = "an";
        }
        return article + " " + s;
    }

    public static Point2D.Double midPoint( Point2D.Double p1, Point2D.Double p2 ) {
        return new Point2D.Double( ( p1.x + p2.x ) / 2.0F, ( p1.y + p2.y ) / 2.0F );
    }

    /** If the rectangles' location and dimension all round (Math.round) to the same int (pixel) then return true.
     * Otherwise false.
     * @param r1
     * @param r2
     * @return
     */
    public static boolean equalsToRoundedInt( Rectangle2D.Double r1, Rectangle2D.Double r2  ) {
        // Originally motivated by CR-1007
        if ( Math.round( r1.x) != Math.round( r2.x )) return false;
        if ( Math.round( r1.y) != Math.round( r2.y )) return false;
        if ( Math.round( r1.width) != Math.round( r2.width )) return false;
        if ( Math.round( r1.height ) != Math.round( r2.height )) return false;
        return true;

    }

    /**
     * A generic file writer, usable by any Charger class
     */
    public static void writeToFile( File f, String s, boolean append ) {
        if ( f != null ) {
            try {
                BufferedWriter out = new BufferedWriter( new FileWriter( f, append ) );
                out.write( s );
                out.close();
            } catch ( Exception e ) {
                General.error( e.getMessage() );
            }
        }
    }

    /**
     * Extracts the prepended pathname part of a supposed file name string
     *
     * @param s supposed file name string
     * @return pathname part of the string (including the trailing file
     * separator); if no path, then returns "" (not null!)
     */
    public static String getPathname( String s ) {
        if ( s == null ) {
            return "";
        }
        String fname = s;
        String pathname = "";
        int fileSepIsAt = s.lastIndexOf( System.getProperty( "file.separator" ) );
        if ( fileSepIsAt > 0 ) {
            pathname = new String( s.substring( 0, fileSepIsAt + 1 ) );
        }
        return pathname;
    }

    /**
     * Tells whether there's a path prepended or not
     *
     * @param s supposed file name string
     * @return true if there seems to be a pathname, false otherwise
     */
    public static boolean hasPathName( String s ) {
        if ( s == null ) {
            return false;
        }
        String fname = s;
        String pathname = "";
        int fileSepIsAt = s.lastIndexOf( System.getProperty( "file.separator" ) );
        if ( fileSepIsAt > 0 ) {
            // will fail on pathnames like ":temp"
            return true;
        }
        return false;
    }

    /**
     * Does a complete erasure of a JMenu. For each item in the menu, gets rid
     * of all listeners and removes item from menu.
     *
     * @param menu
     */
    public static void tearDownMenu( JMenu menu ) {
        Component[] items = menu.getMenuComponents();
        for ( Component item : items ) {
            ComponentListener[] listeners = item.getComponentListeners();
            for ( ComponentListener listener : listeners ) {
                item.removeComponentListener( listener );
                listener = null;
            }
            menu.remove( item );
        }
    }

    /** Convert any forbidden characters to a space character.
     *
     * @param s
     * @param set The set of excluded characters, each of which will be converted to a space.
     * @return
     */
    public static String excludeChars( String s, String set ) {
        String t = new String( s.replaceAll( "[" + set + "]", " " ) );
        return t;
    }

    /**
     * Makes sure that no "illegal" characters remain in the string. Strips out
     * any illegal characters silently.
     *
     * @param the string to be examined
     * @param the character set allowed for that string.
     * @see CharacterSets
     *
     */
    public static String makeLegalChars( String s, String characterSet ) {
        String validChars = // alphabetic characters
        /// digits
        // punctuation
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" + "0123456789" /// digits
         + "@#&*{}\\-_\\./?\u00ac" // punctuation
         + " " // space
        ;
        String t = new String( s.replaceAll( "[^" + validChars + "]", " " ) );
        return t;
    }

    /**
     * Splits a string according to each occurrence of a regular expression,
     * inserting a separator between each part of the string.
     *
     * @param original The original string to be split
     * @param regex A regular expression to be found in the string. Follows the
     * same rules as String#split
     * @param sep The string to be inserted between the parts (if null, then
     * this method has no effect)
     * @return a new string with the appropriate separators inserted if the
     * regex was found
     */
    public static String splitWithSeparator( String original, String regex, String sep ) {
        String[] ss = original.split( regex );
        String newString = "";
        for ( int k = 0; k < ss.length; k++ ) {
            newString += ss[k];
            if ( k != ss.length - 1 ) {
                newString += sep;
            }
        }
        return newString;
    }

    /**
     * Creates a clone of the rectangle.
     *
     * @param rect
     * @return a new instance
     */
    public static Rectangle2D.Double make2DDouble( Rectangle2D.Double rect ) {
        return (Rectangle2D.Double)rect.clone();
    }

    /**
     * Creates a clone of the point.
     *
     * @param point
     * @return a new instance
     */
    public static Point2D.Double make2DDouble( Point2D.Double point ) {
        return (Point2D.Double)point.clone();
    }

    //    /**
    //     * Make a new 2D rectangle (double) from a float one.
    //     *
    //     * @param r a float rectangle
    //     * @return a 2D rectangle
    //     */
    //    public static Rectangle2D.Double make2DDouble( Rectangle2D.Float r ) {
    //        return new Rectangle2D.Double(
    //                (double)r.x,
    //                (double)r.y,
    //                (double)r.width,
    //                (double)r.height );
    //    }
    //    /**
    //     * Make a new 2D rectangle (float) from a double one.
    //     *
    //     * @param r a float rectangle
    //     * @return a double rectangle
    //     */
    //    public static Rectangle2D.Float make2DFloat( Rectangle2D.Double r ) {
    //        return new Rectangle2D.Float(
    //                (float)r.x,
    //                (float)r.y,
    //                (float)r.width,
    //                (float)r.height );
    //    }
    public static Point2D.Double make2DDouble( Point2D.Float p ) {
        return new Point2D.Double( p.x, p.y );
    }

    /**
     * Extracts the extension part of a filename string.
     *
     * @param filename any string
     * @return suffix extension (not including'.'); null if no suffix
     */
    public static String stripFileExtension( String filename ) {
        int dot = filename.lastIndexOf( "." );
        if ( dot == -1 ) {
            return filename;
        }
        return filename.substring( 0, filename.lastIndexOf( '.' ) );
    }

    /** Find the value of the text bracketed inside the given tagname.
     * Returns null if it is not found.
     * @param element
     * @param tagname
     * @return string value of the text inside the tag (using getTextContent); null if not found.
     */
    public static String getXmlText( Element element, String tagname ) {
        NodeList list = element.getElementsByTagName( tagname );
        if ( list == null || list.getLength() == 0 ) {
            return null;
        } else {
            return (String)( list.item( 0 ).getTextContent() );
        }
    }

    public static Point2D.Double get_rectangle_line_intersection( Rectangle2D.Double rect, Line2D.Double line ) {
        Line2D.Double border;
        Point2D.Double intersectPt;
        border = new Line2D.Double( rect.x, rect.y, rect.x + rect.width, rect.y );
        intersectPt = get_line_intersection( border, line );
        if ( intersectPt != null ) {
            return intersectPt;
        }
        // bottom
        // left
        return null;
    }



    /**
 * Installs a listener to receive notification when the text of any
 * {@code JTextComponent} is changed. Internally, it installs a
 * {@link DocumentListener} on the text component's {@link Document},
 * and a {@link PropertyChangeListener} on the text component to detect
 * if the {@code Document} itself is replaced.
 *
 * @param textComponent any text component, such as a {@link JTextField}
 *        or {@link JTextArea}
 * @param changeListener a listener to receieve {@link ChangeEvent}s
 *        when the text is changed; the source object for the events
 *        will be the text component
 * @throws NullPointerException if either parameter is null
 * obtained from https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
 */
public static void addChangeListener(JTextComponent textComponent, ChangeListener changeListener) {
    Objects.requireNonNull(textComponent);
    Objects.requireNonNull(changeListener);
    DocumentListener dl = new DocumentListener() {
        private int lastChange = 0, lastNotifiedChange = 0;

        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            lastChange++;
            SwingUtilities.invokeLater(() -> {
                if (lastNotifiedChange != lastChange) {
                    lastNotifiedChange = lastChange;
                    changeListener.stateChanged(new ChangeEvent(textComponent));
                }
            });
        }
    };
    textComponent.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
        Document d1 = (Document)e.getOldValue();
        Document d2 = (Document)e.getNewValue();
        if (d1 != null) {
            d1.removeDocumentListener(dl);
        }
        if (d2 != null) {
            d2.addDocumentListener(dl);
        }
        dl.changedUpdate(null);
    });
    Document d = textComponent.getDocument();
    if (d != null) d.addDocumentListener(dl);
}

    /* https://stackoverflow.com/questions/28678026/how-can-i-get-all-class-files-in-a-specific-package-in-java */
    public static final List<Class<?>> getClassesInPackage(String packageName) {
        String path = packageName.replaceAll("\\.", File.separator);
        List<Class<?>> classes = new ArrayList<>();
        String[] classPathEntries = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
        String name;
        for (String classpathEntry : classPathEntries) {
            if (classpathEntry.endsWith(".jar")) {
                File jar = new File(classpathEntry);
                try {
                    JarInputStream is = new JarInputStream(new FileInputStream(jar));
                    JarEntry entry;
                    while (( entry = is.getNextJarEntry() ) != null) {
                        name = entry.getName();
                        if (name.endsWith(".class")) {
                            if (name.contains(path) && name.endsWith(".class")) {
                                String classPath = name.substring(0, entry.getName().length() - 6);
                                classPath = classPath.replaceAll("[\\|/]", ".");
                                classes.add(Class.forName(classPath));
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            } else {
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);
                    for (File file : base.listFiles()) {
                        name = file.getName();
                        if (name.endsWith(".class")) {
                            name = name.substring(0, name.length() - 6);
                            classes.add(Class.forName(packageName + "." + name));
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            }
        }
        return classes;
    }
    // public void loadModulePluginsOLD() {
    //        String[] modules = null;
    //        String classpath = System.getProperty( "java.class.path" );
    ////                   JOptionPane.showMessageDialog( null, "classpath...\n" + classpath);
    //
    //
    ////        Global.consoleMsg( "Class path is \"" + classpath + "\"" );
    ////          ArrayList<Class> availableModuleClasses = new ArrayList<>();
    //
    //        // DRP: split the classpath
    //        String classPathSegment[] = classpath.split( java.io.File.pathSeparator );
    //
    //        for ( int cpsIndex = 0; cpsIndex < classPathSegment.length; cpsIndex++ ) {
    ////            Global.consoleMsg( "looking for modules in " + classPathSegment[cpsIndex] );
    //
    //          Class moduleClass = null;
    //            if ( classPathSegment[cpsIndex].toLowerCase().endsWith( ".jar" ) ) {
    //                // Looking in a .jar file
    //                JarFile myJar = null;
    //                try {
    //                    myJar = new JarFile( classPathSegment[cpsIndex] );
    //                } catch ( IOException e ) {
    ////                    Global.warning( "can't open jarfile " + e.getMessage() );
    //                }
    //                Enumeration entries = myJar.entries();
    //                while ( entries.hasMoreElements() ) {
    //                    JarEntry j = (JarEntry)( entries.nextElement() );
    ////                    Global.info( "jar entry name is " + j.getDisplayName() );
    //                    String name = j.getDisplayName();
    //                    Global.info( "Reading jar entry: \"" + name + "\".");
    //                    if ( name.endsWith(Global.modulePluginClassSuffix )
    //                             && !name.contains( "/" + Global.modulePluginClassSuffix ) &&
    //                            Global.modulePluginNamesToEnable.contains(  name + ".class")) {
    //                        try {
    //                            name = name.replace( ".class", "");
    //                            moduleClass = ClassLoader.getSystemClassLoader().loadClass( name );
    //                        } catch ( ClassNotFoundException ex ) {
    //                            Logger.getLogger( HubFrame.class.getDisplayName() ).log( Level.SEVERE, null, ex );
    //                            continue;
    //                        }
    //                        Global.consoleMsg( "Yay! found module named " + moduleClass.getCanonicalName() );
    //                        Global.modulePluginsEnabled.add( moduleClass );
    //                    }
    //                }
    //            } else { // looking in a class folder structure
    //
    //                File moduleDirectoryFile = new File( "Bad_Path_Name" );
    //                moduleDirectoryFile = new File( classPathSegment[cpsIndex] );
    //
    //                if ( moduleDirectoryFile.exists() ) {
    //
    ////                    Global.consoleMsg( "possible module folder is " + moduleDirectoryFile.getAbsolutePath() );
    //                    // get the list of modules
    //                    modules = moduleDirectoryFile.list( new FilenameFilter() {
    //                        public boolean accept( File f, String name ) {
    //                            if ( name.endsWith(Global.modulePluginClassSuffix ) &&
    //                                    !name.equalsIgnoreCase(Global.modulePluginClassSuffix ) &&
    //                            Global.modulePluginNamesToEnable.contains(  name + ".class")) {
    //                                return true;
    //                            }
    //                            return false;
    //                        }
    //                    } );
    //                    if ( modules.length > 0 ) {
    //                        try {
    ////                            String moduleClassname = modules[0].substring( 0, modules[0].lastIndexOf( "." ) );
    //                            String moduleClassname = modules[0].replace( ".class", "" );
    //                            moduleClass = ClassLoader.getSystemClassLoader().loadClass( moduleClassname );
    //                            if ( Global.modulePluginNamesToEnable.contains( moduleClassname) )
    //                                   Global.modulePluginsEnabled.add( moduleClass );
    //                        } catch ( ClassNotFoundException ex ) {
    //                            Logger.getLogger( HubFrame.class.getDisplayName() ).log( Level.SEVERE, null, ex );
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //                // If there are any modules found to be added to the Tools menu, here's where we do it.
    //        if ( Global.modulePluginsEnabled.size() > 0 )
    //            menuTools.addSeparator();
    //        for ( Class moduleClass : Global.modulePluginsEnabled ) {
    //            try {
    //                ModulePlugin module = (ModulePlugin)moduleClass.newInstance();
    ////                Global.modulePluginsAvailable.add( module );
    //                this.addModuleMenuItem( module );
    //            } catch ( InstantiationException ex ) {
    //                Logger.getLogger( HubFrame.class.getDisplayName() ).log( Level.SEVERE, null, ex );
    //            } catch ( IllegalAccessException ex ) {
    //                Logger.getLogger( HubFrame.class.getDisplayName() ).log( Level.SEVERE, null, ex );
    //            }
    //        }
    //                // Now check to see if there's an argument that exactly matches a module. If so,
    //                // then go ahead and launch the module.
    //                // If more than one module name matches, all are launched.
    ////        for ( String arg : Global.extraArguments ) {
    ////            if ( arg.startsWith( "-")) {
    ////                String argvalue = arg.substring( 1);
    ////                for ( ModulePlugin plugin : Global.modulePluginsAvailable ) {
    ////                    if ( plugin.getDisplayName().equals( argvalue )) {
    ////                        plugin.startup();
    ////                    }
    ////                }
    ////            }
    ////        }
    //    }


}
