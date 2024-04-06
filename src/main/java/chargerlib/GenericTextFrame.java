/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chargerlib;

import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * A general utility class to handle simple text editing and saving.
 * It's the caller's responsibility to load with whatever text is desired.
 * A suggested file can be associated with the window, so that the text
 * can be saved. If used as a regular file editor, loadFile should be called.
 * The title of a text frame is the reqular supported window title.
 * The label is inside the frame window, as a "header" to the content.
 * @author hsd
 */
public class GenericTextFrame extends JFrame implements ClipboardOwner, ManagedWindow {

    JFrame ownerFrame = null;
    File fullPathFile = null;
    
    boolean editable = true;
    

    /**
     * Creates new form GenericTextFrame
     */
    public GenericTextFrame( JFrame owner ) {
        setup( owner, "Untitled", "Display Text", "", null );

    }
    
        /**
     * Creates a new TextDisplayFrame with no text in it; use setText to fill
     * it. The frame is not visible; this gives a chance to set various
     * characteristics before showing it.
     *
     * @param owner the frame which spawned this one
     * @param title the window's title
     * @param label a descriptive header label (not the window title)
     * @param text the text to be included in the text area
     * @param suggestedFile a file to save the text to (null if not used)
     */
    public GenericTextFrame( JFrame owner, String title, String label, String text, File suggestedFile ) {
        setup( owner, title, label, text, suggestedFile );
    }

    /**
     * Allows subclasses to add their own option panel to the form without inconvenience.
     * The panel must use GroupLayout?
     * @param optionPane The new panel to be added just above the text area.
     */
    public void setOptionPanel( JPanel optionPane ) {
        optionPanel.removeAll();
        optionPanel.add(  optionPane);
    }


    private void setup( JFrame owner, String title, String label, String text, File suggestedFile ) {
        //LibGlobal.info( "suggested file is " + suggestedFile.toString() );
        initComponents();
        getContentPane().setBackground( General.chargerBlueColor);

        ownerFrame = owner;
            // PR-132 02-18-18 hsd - complete rework of the window manager.
//        WindowManager.manageWindow( this );
//        WindowManager.makeMenu( windowMenu, this);

        setTheText( text );
        setLabel( label );
        setSuggestedFile( suggestedFile );
        setTitle( title );

        // set scrolling to look at the beginning of the text
        scroller.getViewport().setViewPosition( new Point( 0, 0 ) );

        if ( owner.getFont() != null ) {
            setFont( owner.getFont() );
            theText.setFont( owner.getFont() );
        }
        
        // PR-132 02-15-18 hsd - do this after all the elements of the frame have been set.
        WindowManager.manageWindow( this );
//        WindowManager.makeMenu( windowMenu, this );
        setMenuItems();
        
        this.optionPanel.setLayout( new BoxLayout( optionPanel, BoxLayout.X_AXIS));
        
        this.setLocationRelativeTo( owner );

        repaint();
        //setVisible( true );
    }

    public String getTheText() {
        return theText.getText();
    }
    
    

    public void setTheText( String text ) {
        theText.setText( text );
        theText.setCaretPosition( 0);
        setMenuItems();
    }

    /**
     * Sets the descriptive label appearing above the text (not the title). Can
     * be changed at any time.
     *
     * @param label the label to be displayed.
     */
    public void setLabel( String label ) {
        if ( label != null ) {
            mainLabel.setText( label );
        } else {
            mainLabel.setText( "Displaying text:" );
        }
    }
    
    public void setEditable( boolean b ) {
        editable = b;
    }

    /**
     * Sets the suggested file path in case the user wants to save. Can be
     * changed at any time. Invokes a dialog where the user can change it too.
     *
     * @param suggestedFile a reasonably named file to start the user off.
     */
    public void setSuggestedFile( File suggestedFile ) {
        if ( suggestedFile != null ) {
            fullPathFile = suggestedFile.getAbsoluteFile();
        } else {
            fullPathFile = ( new File( "Untitled.txt" ) ).getAbsoluteFile();
        }
    }

    /** Read the frame's suggested file into the frame's text area for editing.
     * Relies entirely on the correctness of the suggested file associated
     * with the frame; if something goes wrong, exceptions are thrown.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void loadFile( ) throws FileNotFoundException, IOException {
        if ( fullPathFile == null )
            return;
        byte[] chars = null;
              chars = Files.readAllBytes( fullPathFile.toPath() );
        String text = new String( chars );
        this.setTheText( text );
    }
    /**
     * Sets up the given font for the text display part of the frame only.
     *
     * @param f the font to be displayed.
     */
    public void setTextFont( Font f ) {
        theText.setFont( f );
        repaint();
    }

    public void setContentType( String type ) {
        theText.setContentType( type );
    }
    
        /**
     * Determines for each menu item whether to be enabled or disabled ("gray'ed
     * out" )
     */
    public void setMenuItems()  {
        // NEED TO use Toolkit.getDefaultToolkit().getSystemClipboard
        //LibGlobal.info("set menu items, somethingHasBeenSelected = " + somethingHasBeenSelected );
        for ( int num = 0; num < editMenu.getItemCount(); num++ ) {
            JMenuItem mi = editMenu.getItem( num );
            String s = mi.getText();
            if ( s.equals( "Copy" ) ) {
                mi.setEnabled( ( theText.getSelectedText() != null ) && ( !theText.getSelectedText().equals( "" ) ) );
            } else if ( s.equals( "Cut" ) ) {
                mi.setEnabled( editable && ( theText.getSelectedText() != null ) && ( !theText.getSelectedText().equals( "" ) )  );
            } else if ( s.equals( "Clear" ) ) {
                mi.setEnabled( ( editable && theText.getSelectedText() != null ) && ( !theText.getSelectedText().equals( "" ) ) );
            } else if ( s.equals( "Paste" ) ) {
                if ( !editable ) {
                    mi.setEnabled( false );
                } else if ( Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable( DataFlavor.stringFlavor ) ) {
                    String clips;
                    try {
                        clips = (String)( Toolkit.getDefaultToolkit().getSystemClipboard().getData( DataFlavor.stringFlavor ) );
                        if ( clips != null && clips.length() > 0 ) {
                            mi.setEnabled( true );
                        }
                    } catch ( UnsupportedFlavorException ex ) {
                        General.error( "problem with system clipboard; " + ex.getMessage() );
                    } catch ( IOException ex ) {
                        General.error( "problem with system clipboard; " + ex.getMessage() );
                    }
                }
            } else {
                mi.setEnabled( true );
            }
        }
    }

    /**
     * Tells a window manager what label to put on the menu to select this
     * window
     */
    public String getMenuItemLabel() {
        return getTitle();
    }


    /**
     * If there's a file associated with the window, return its name; null
     * otherwise.
     */
    public String getFilename() {
        if ( fullPathFile != null ) {
            return fullPathFile.getAbsolutePath();
        } else {
            return getTitle();
        }
    }

    @Override
    public JMenu getWindowMenu() {
        return windowMenu;
    }
    
    

    public void lostOwnership( Clipboard clipboard, Transferable contents ) {
        //LibGlobal.info( ef.graphName + " lost ownership of clipboard " + clipboard.getName() );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scroller = new javax.swing.JScrollPane();
        theText = new javax.swing.JEditorPane();
        mainLabel = new javax.swing.JLabel();
        optionPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        menuFileSave = new javax.swing.JMenuItem();
        menuFileClose = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        menuEditCut = new javax.swing.JMenuItem();
        menuEditCopy = new javax.swing.JMenuItem();
        menuEditPaste = new javax.swing.JMenuItem();
        menuEditClear = new javax.swing.JMenuItem();
        menuEditSelectAll = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(General.chargerBlueColor);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        scroller.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        theText.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        theText.setPreferredSize(new java.awt.Dimension(700, 500));
        theText.setSelectionColor(new java.awt.Color(153, 153, 153));
        theText.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                theTextCaretUpdate(evt);
            }
        });
        scroller.setViewportView(theText);

        mainLabel.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        mainLabel.setForeground(new java.awt.Color(255, 255, 255));
        mainLabel.setText("jLabel1");

        optionPanel.setBackground(General.chargerBlueColor);

        org.jdesktop.layout.GroupLayout optionPanelLayout = new org.jdesktop.layout.GroupLayout(optionPanel);
        optionPanel.setLayout(optionPanelLayout);
        optionPanelLayout.setHorizontalGroup(
            optionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 689, Short.MAX_VALUE)
        );
        optionPanelLayout.setVerticalGroup(
            optionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 18, Short.MAX_VALUE)
        );

        fileMenu.setText("File");

        menuFileSave.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_S, chargerlib.General.AcceleratorKey ));
        menuFileSave.setText("Save Text...");
        menuFileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileSaveActionPerformed(evt);
            }
        });
        fileMenu.add(menuFileSave);

        menuFileClose.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_W, chargerlib.General.AcceleratorKey ));
        menuFileClose.setText("Close");
        menuFileClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileCloseActionPerformed(evt);
            }
        });
        fileMenu.add(menuFileClose);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");

        menuEditCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, chargerlib.General.AcceleratorKey ));
        menuEditCut.setText("Cut");
        menuEditCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditCutActionPerformed(evt);
            }
        });
        editMenu.add(menuEditCut);

        menuEditCopy.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_C, chargerlib.General.AcceleratorKey ));
        menuEditCopy.setText("Copy");
        menuEditCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditCopyActionPerformed(evt);
            }
        });
        editMenu.add(menuEditCopy);

        menuEditPaste.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, chargerlib.General.AcceleratorKey ));
        menuEditPaste.setText("Paste");
        editMenu.add(menuEditPaste);

        menuEditClear.setText("Clear");
        menuEditClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditClearActionPerformed(evt);
            }
        });
        editMenu.add(menuEditClear);

        menuEditSelectAll.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_A, chargerlib.General.AcceleratorKey ));
        menuEditSelectAll.setText("Select All");
        menuEditSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditSelectAllActionPerformed(evt);
            }
        });
        editMenu.add(menuEditSelectAll);

        jMenuBar1.add(editMenu);

        windowMenu.setText("Window");
        jMenuBar1.add(windowMenu);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, scroller)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, optionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(9, 9, 9)
                .add(mainLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(optionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .add(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuFileCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileCloseActionPerformed
            // PR-132 02-18-18 hsd - complete rework of the window manager.
//        WindowManager.forgetWindow( this );
        ownerFrame.toFront();
        setVisible( false );
    }//GEN-LAST:event_menuFileCloseActionPerformed

    private void menuEditClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditClearActionPerformed
        theText.setText(General.removeSubstring( theText.getText(), theText.getSelectionStart(), theText.getSelectionEnd() ) );
    }//GEN-LAST:event_menuEditClearActionPerformed

    private void menuEditCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditCopyActionPerformed
        StringSelection sel = new StringSelection( theText.getSelectedText() );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( sel, this );
    }//GEN-LAST:event_menuEditCopyActionPerformed

    private void menuEditSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditSelectAllActionPerformed
        theText.selectAll();
        setMenuItems();
    }//GEN-LAST:event_menuEditSelectAllActionPerformed

    private void menuFileSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileSaveActionPerformed
        String promptstring = new String( "Save " + mainLabel.getText() + " as " );
        File folder = fullPathFile.getParentFile();
        File chosenOne = General.queryForOutputFile( promptstring, folder, fullPathFile.getName() );
        if ( chosenOne != null ) {
            try {
                FileWriter writer = new FileWriter( chosenOne );
                writer.write( theText.getText());
                writer.close();
            } catch ( IOException ex ) {
                JOptionPane.showMessageDialog( this, "File not written; " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_menuFileSaveActionPerformed

    private void menuEditCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditCutActionPerformed
        StringSelection sel = new StringSelection( theText.getSelectedText() );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( sel, this );
        theText.setText(General.removeSubstring( theText.getText(), theText.getSelectionStart(), theText.getSelectionEnd()) );
    }//GEN-LAST:event_menuEditCutActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
            // PR-132 02-18-18 hsd - complete rework of the window manager.
//        WindowManager.forgetWindow( this );
        ownerFrame.toFront();
        setVisible( false );
    }//GEN-LAST:event_formWindowClosing

    private void theTextCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_theTextCaretUpdate
        setMenuItems();
    }//GEN-LAST:event_theTextCaretUpdate

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        int margin = 10;
//        theText.s( new Dimension(getContentPane().getWidth() - 2*margin, getContentPane().getHeight() - 10*margin));
    }//GEN-LAST:event_formComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuBar jMenuBar1;
    public javax.swing.JLabel mainLabel;
    private javax.swing.JMenuItem menuEditClear;
    private javax.swing.JMenuItem menuEditCopy;
    private javax.swing.JMenuItem menuEditCut;
    private javax.swing.JMenuItem menuEditPaste;
    private javax.swing.JMenuItem menuEditSelectAll;
    private javax.swing.JMenuItem menuFileClose;
    private javax.swing.JMenuItem menuFileSave;
    public javax.swing.JPanel optionPanel;
    protected javax.swing.JScrollPane scroller;
    public javax.swing.JEditorPane theText;
    private javax.swing.JMenu windowMenu;
    // End of variables declaration//GEN-END:variables
}
