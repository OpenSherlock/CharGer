/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib;

/**
 * A class to represent the various character sets allowed in Charger.
 * There are two main sets -- one for type labels and the other for referents.
 * The character set for types is a subset of the one for referents.
 * 
 * The design of this feature is probably wrong-headed. There should probably
 * be a "stop" list of characters not allowed and then any others are admitted.
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public final class CharacterSets {
    /**
     * Allowed characters for a type label.
     * <pre>
"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" // alphabetic characters
+ "0123456789" /// digits
+ " " // space, single quote, double quote
+ "-_¬~" // dash, underscore, "not" symbol, tilde
</pre>     */
    public static String TYPE_LABEL_CHARSET = 
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" // alphabetic characters
            + "0123456789" /// digits
            + " '\"" // space, single quote
            + "-_¬~" // dash, underscore, "not" symbol, tilde
            + ";"
            ;
    
    /**
     * Allowed characters for the referent field. Note a leading or trailing space will probably
     * be stripped out.
     * <pre>
TYPE_LABEL_CHARSET // all characters in a type label
     * + "@#&*{}\\\\./?" // at, hash mark, ampersand, start, backslash, period (full stop), forward slash, question mark
     * + "'\"<>+=%"   // single quote, double quote, less than, greater than, plus, equals, percent
     * + "$£€%"  // dollar sign, pound sign, euro sign
     * ;
     * </pre>
     */
    public static String REFERENT_CHARSET
            = TYPE_LABEL_CHARSET // all characters in a type label
            + "@#&*{}\\\\./?" // at, hash mark, ampersand, start, backslash, period (full stop), forward slash, question mark
            + "'\"<>+=%" // less than, greater than, plus, equals, percent
            + "$£€%" //  dollar sign, pound sign, euro sign
            ;

    /** 
     * Characters NOT allowed in a type label. 
     * Currently only colon ":" is excluded.
     */
    public static String TYPE_LABEL_EXCLUDED_CHARS
            = ":";

}
