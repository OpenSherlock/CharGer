/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chargerlib.undo;

/**
 * Represents an object's capability to have its changes undone/redone.
 * Changes represent transitions between states, which are then able
 * to be restored or re-done.
 * No edit actions <em>per se</em> should be performed by
 * the implementation -- restoreState should exactly re-construct
 * the state it represents.
 * @see chargerlib.undo.UndoStateManager
 *  @since Charger 3.8.3
 * @author hsd
 */
public interface Undoable {
    /** Creates a new instance of the current state of the target object.
     * Note that this method must create a complete copy (either through new, clone() or 
     * some other method. If only a reference is saved, then all saved states will really
     * point to the same (current) state and there will appear to be no effect.
     * It is up to the user to ensure that nothing in this method
     * causes the undo manager to become enabled; obviously strange
     * occurrences will result.
     */
    public  UndoableState currentState();
    
    /** Restores the object's current state from the given state.
     * It is up to the user to ensure that nothing in this method
     * causes the undo manager to become enabled; obviously strange
     * occurrences will result.
     */
    public  void restoreState( UndoableState state );
    
    /** Perform whatever setup is necessary if the undo/redo status may have changed.
     * Subclasses override to do whatever menu (or other) setup is needed to make undo/redo available */
    public  void setupMenus();

}
