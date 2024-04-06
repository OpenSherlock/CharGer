package charger.exception;

import charger.*;
/**
 * Exceptions related to syntax problems with saving, opening, etc.	@author
 * Harry S. Delugach ( delugach@uah.edu ) Copyright 1998-2020 by Harry S.
 * Delugach
 */
public class CGFileException extends CGException {

    public CGFileException( String s ) {
        super( s );
    }

    public CGFileException( String s, Object o ) {
        super( s, o );
    }
}
