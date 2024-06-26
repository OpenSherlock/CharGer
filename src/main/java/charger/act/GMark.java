package charger.act;
import java.util.*;

import charger.*;
/* 	$Header$ *//*    CharGer - Conceptual Graph Editor    Copyright 1998-2020 by Harry S. Delugach            This package is free software; you can redistribute it and/or modify    it under the terms of the GNU Lesser General Public License as    published by the Free Software Foundation; either version 2.1 of the    License, or (at your option) any later version. This package is     distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;     without even the implied warranty of MERCHANTABILITY or FITNESS FOR A     PARTICULAR PURPOSE. See the GNU Lesser General Public License for more     details. You should have received a copy of the GNU Lesser General Public    License along with this package; if not, write to the Free Software    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA*//**	Contains the information used for the marking algorithm for actor updating.	@author Harry S. Delugach ( delugach@uah.edu ) Copyright 1998-2020 by Harry S. Delugach	@see GraphUpdater */
public class GMark {	    
	/** Whether the node we're marking has been changed during this activation */	
	boolean changed = false;	    
	/** Whether this node is currently participating in an actor's firing */	
	boolean active = false;		
	public GMark()	{		
		changed = false;		
		active = false;	
	}				
	/** Does the marker indicate object has changed? 		
	 * @return true if object has changed 
	 **/	
	public boolean isChanged() { 
		return changed; 
	}			
	/** Does the marker indicate an active input concept to an actor? 			
	 * @return true if concept is an input to any actor; false otherwise */	
	public boolean isActive() { 
		return active; 
	}		
	public void setChanged( boolean b ) { 
		changed = b; 
	}		
	public void setActive( boolean b ) { 
		active = b; 
	}
}
	 