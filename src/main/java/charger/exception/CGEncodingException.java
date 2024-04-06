/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package charger.exception;

/**
 * Indicates an encoding exception, typically that a string or file was not
 * UTF-8 compliant.
 *
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class CGEncodingException extends CGException {

    public static String explanation
            = "Input could not be handled correctly.\n"
            + "This usually happens when an input file is not properly encoded as UTF-8.\n"
            + "Please ensure that the input file is saved or converted to UTF-8.\n"
            + "(This error usually only occurs on Windows machines.)\n";

    public CGEncodingException( String s ) {
        super( s );
    }

    public CGEncodingException() {
        super( explanation );
    }

}
