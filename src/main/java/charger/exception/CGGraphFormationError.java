package charger.exception;
import charger.*;/**	Errors related to problems with ill formed graphs, non-adherence to rules, etc.	@author Harry S. Delugach ( delugach@uah.edu ) Copyright 1998-2020 by Harry S. Delugach */
public class CGGraphFormationError extends CGError {	
	public CGGraphFormationError( String s ) {		
		super( s );	
	}	
	public CGGraphFormationError( String s, Object o ) {		
		super( s, o );	
	}
}