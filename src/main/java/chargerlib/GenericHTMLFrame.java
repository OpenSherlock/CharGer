/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chargerlib;

import java.io.File;
import javax.swing.JFrame;

/**
 *
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class GenericHTMLFrame extends GenericTextFrame {

    public GenericHTMLFrame( JFrame owner ) {
        this( owner, "", "", "", null  );
    }

    public GenericHTMLFrame( JFrame owner, String title, String label, String text, File suggestedFile ) {
        super( owner, title, label, text, suggestedFile );
        setContentType( "text/html");
        theText.setText( text );
        theText.setCaretPosition( 0);
    }

}
