package charger.exception;

/*
 * Copyright 1998-2020 by Harry Delugach (UAH), Huntsville, AL 35899, USA. All Rights Reserved.
 * Unless permission is granted, this material may not be copied, reproduced or coded for reproduction
 *  by any electrical, mechanical or chemical process or combination thereof, now known or later developed.
 *
 * $Header$
 */

/**
 * Errors related to problems with ill formed graphs, non-adherence to rules,
 * etc.	@author Harry S. Delugach ( delugach@uah.edu ) Copyright 1998-2020 by
 * Harry S. Delugach
 */
public class CGError extends CGThrowable {

    public CGError( String s ) {
        super( s );
    }

    public CGError( String s, Object o ) {
        super( s, o );
    }
}
