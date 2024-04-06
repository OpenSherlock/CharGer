//
//  WindowTracking.java
//  CharGer 2003
//
//  Created by Harry Delugach on Sun May 18 2003.
//

package chargerlib;

import javax.swing.JMenu;

/* 
	$Header$ 
*/
/*
    CharGer - Conceptual Graph Editor
    Copyright reserved 1998-2017 by Harry S. Delugach
        
    This package is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation; either version 2.1 of the
    License, or (at your option) any later version. This package is 
    distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
    PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
    details. You should have received a copy of the GNU Lesser General Public
    License along with this package; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

/** Specifies services needed for a window that requests to be managed. 
	If a window implements this interface, it is entitled to be included in
	a window menu and to be activated by selecting from that menu.
        * The managed window must also call manageWindow in order to be recognized.
	@see WindowManager#manageWindow(charger.util.ManagedWindow) 
 */
public interface ManagedWindow {

    /**
     * Tells a window manager what label to put on the menu to select this
     * window. The window manager is entitled to shorten the label, but must
     * subsequently take that into account. It's therefore necessary that no two actual
     * menu item labels be the same.
     */
    public String getMenuItemLabel();

    /**
     * If there's a file associated with the window, return its name; null
     * otherwise.
     */
    public String getFilename();
    
    /** 
     * The window menu to be populated by the window manager.
     * @return the managed window's menu to be populated. The menu cannot be null;
     * however, the managed window itself may choose not to add it to the menu bar.
     */
    public JMenu getWindowMenu();

}
