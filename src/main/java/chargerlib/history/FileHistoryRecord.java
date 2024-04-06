/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib.history;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents an event where an object is read from a file.
 * @since 4.1
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class FileHistoryRecord extends ObjectHistoryRecord {
    
    public static String FileHistoryRecordType = "FILE";
    
    public File file = null;

    public FileHistoryRecord() {
        
    }
    
    public FileHistoryRecord( Object obj, File f ) {
        super( obj );
        type = FileHistoryRecordType;
        file = f;
    }
    
    
     /** Create an XML document such that "event" is the outermost tag.
     * 
     * @return a ready-to-parse document
     */
   public Document makeDocument( ) {
        Document doc = super.makeDocument(  );
        Element fileElem = doc.createElement( "file");
        String filename = file.getAbsolutePath();
        fileElem.insertBefore( doc.createTextNode( filename ), fileElem.getLastChild());
        doc.getDocumentElement().appendChild( fileElem );
        return doc;
    }
    
    public void copyInfoFrom( FileHistoryRecord he ) {
        super.copyInfoFrom( he );
        this.setFile( he.getFile().getAbsoluteFile());
    }

    public File getFile() {
        return file;
    }

    public void setFile( File file ) {
        this.file = file;
    }
    
    
    
    public String toString() {
        return super.toString() + "; file= " + getFile().getName();
    }

}
