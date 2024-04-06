package charger.exception;

import charger.*;



/**
 * General class of exceptions defined by the CharGer System	@author Harry S.
 * Delugach ( delugach@uah.edu ) Copyright 1998-2020 by Harry S.
 * Delugach
 */
public class CGException extends CGThrowable {

    public CGException( String s ) {
        super( s );
    }

    public CGException( String s, Object o ) {
        super( s, o );
    }
}
