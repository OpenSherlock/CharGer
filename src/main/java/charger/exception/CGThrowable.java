package charger.exception;
import charger.*;
/* 	$Header$ *//*    CharGer - Conceptual Graph Editor    Copyright 1998-2020 by Harry S. Delugach            This package is free software; you can redistribute it and/or modify    it under the terms of the GNU Lesser General Public License as    published by the Free Software Foundation; either version 2.1 of the    License, or (at your option) any later version. This package is     distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;     without even the implied warranty of MERCHANTABILITY or FITNESS FOR A     PARTICULAR PURPOSE. See the GNU Lesser General Public License for more     details. You should have received a copy of the GNU Lesser General Public    License along with this package; if not, write to the Free Software    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA*//**	Top-level abstract exception to superclass all the others defined here.	@author Harry S. Delugach ( delugach@uah.edu ) Copyright 1998-2020 by Harry S. Delugach */
public class CGThrowable extends Throwable {	
	Object offendingObject = null;		    
	/** Creates a throwable CharGer exception with the given message 		
	 * @param s message associated with this exception	     
	 */	
	public CGThrowable( String s ) 	{		
		super( s );	
	}	    
	/** Creates a throwable CharGer exception with the given message and source		
	 * @param s message associated with this exception		
	 * @param o object that threw the exception	     
	 */		
	public CGThrowable( String s, Object o ) 	{		
		super( s );		
		offendingObject = o;	
	}	    
	/** Creates a throwable CharGer exception involving the given object 		
	 * @param o object that threw the exception (null if there isn't one)	     
	 */			
	public void setObject( Object o )	{		
		offendingObject = o;	
	}	    
	/**		
	 * @return the offending object that caused the exception; null if there isn't one	     
	 */	
	public Object getObject()	{		
		return offendingObject;	
	}
}
	 