package charger.exception;
import charger.*;
/* 	$Header$ *//*    CharGer - Conceptual Graph Editor    Copyright 1998-2020 by Harry S. Delugach            This package is free software; you can redistribute it and/or modify    it under the terms of the GNU Lesser General Public License as    published by the Free Software Foundation; either version 2.1 of the    License, or (at your option) any later version. This package is     distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;     without even the implied warranty of MERCHANTABILITY or FITNESS FOR A     PARTICULAR PURPOSE. See the GNU Lesser General Public License for more     details. You should have received a copy of the GNU Lesser General Public    License along with this package; if not, write to the Free Software    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA*//**	Exceptions related to problems with syntax, parsing, etc.	@author Harry S. Delugach ( delugach@uah.edu ) Copyright 1998-2020 by Harry S. Delugach */
public class CGSyntaxException extends CGException {	
	public CGSyntaxException( String s ) {		
		super( s );	
	}	
	public CGSyntaxException( String s, Object o ) {		
		super( s, o );	
	}
}