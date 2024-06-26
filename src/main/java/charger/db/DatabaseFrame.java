package charger.db;

import charger.*;
import charger.EditingChangeState.EditChange;
import charger.exception.*;
import chargerlib.CGButton;
import chargerlib.General;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/* 
	$Header$ 
*/
/*
    CharGer - Conceptual Graph Editor
    Copyright 1998-2020 by Harry S. Delugach
        
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
/**
	Provides a way for the CharGer user to examine the contents of a text "database" file.
	Allows setting up of simple graph as a starting point for database semantic modeling.
	@author Harry S. Delugach ( delugach@uah.edu ) Copyright 1998-2020 by Harry S. Delugach
 */
public class DatabaseFrame extends JFrame implements ListSelectionListener, ChangeListener
{
	private TextDatabase db;
			/** The list of field (column) identifiers, in order */
	private String[] DBFieldArray = null;
			/** The last record number that was read from the db file */
	private int currentRecordNumber = 0;
	

	private CGButton ExamineButton = new CGButton();
	private CGButton setPrimaryKey = new CGButton();
	private CGButton setupGraph = new CGButton();
	private CGButton selectDBbutton = new CGButton();
	private CGButton nextRecord = new CGButton();
	private JTextField selectedIndex = new JTextField();
	private JTextField primaryKeyIndex = new JTextField();
	private JTextField filePath = new JTextField();
	private JTextField DatabaseTypeReferent = new JTextField();
	private JTextField messageWindow = new JTextField();
	private JLabel inputConceptLabel = new JLabel();
	private JLabel databaseFieldsLabel = new JLabel();
	private JLabel databaseValuesLabel = new JLabel();
	private JLabel sequenceNumberLabel = new JLabel();
	private JLabel primaryKeyLabel = new JLabel();
	private JLabel titleTextLabel = new JLabel();
	private JScrollPane SPFieldList = new JScrollPane();
	private JList FieldList = new JList();
	private JScrollPane SPValueList = new JScrollPane();
	private JList ValueList = new JList();
	
	private JScrollPane SPFieldValuePane = new JScrollPane();
	//private JTable FieldValueTable = new JTable( fieldValueModel );
	
	private abstract class MyTableModel extends AbstractTableModel
	{
		String[] columnNames = null;
		Object[][] data = null;
	}
	public DatabaseFrame( String Folder )
	{
		try {
			initComponents();
			filePath.setText( Folder );
		} catch ( Exception e ) {
			Global.error( "Database frame initializing components failed: " + e.getMessage() ); };
		setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
	}

	public void initComponents() throws Exception
	{
		// the following code sets the frame's initial state
		ExamineButton.setText("Examine");
		ExamineButton.setLocation(new Point(10, 30));
		ExamineButton.setToolTipText("Open the database file and set up its fields");
		ExamineButton.setSize(new Dimension(90, 20));
		ExamineButton.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				ExamineButtonActionPerformed(e);
			}
		});
		
		setPrimaryKey.setText("Set Primary Key");
		setPrimaryKey.setLocation(new Point(10, 210));
		setPrimaryKey.setToolTipText("Set the selected database field as the primary key (if there is one)");
		setPrimaryKey.setSize(new Dimension(110, 20));
		setPrimaryKey.addActionListener(new  ActionListener() {
			public void actionPerformed( ActionEvent e) {
				setPrimaryKeyActionPerformed(e);
			}
		});

		setupGraph.setText("Set Up Graph");
		setupGraph.setLocation(new Point(10, 260));
		setupGraph.setToolTipText("Create a conceptual graph based on the database using <dbfind> actors");
		setupGraph.setSize(new Dimension(110, 20));
		setupGraph.addActionListener(new  ActionListener() {
			public void actionPerformed( ActionEvent e) {
				setupGraphActionPerformed(e);
			}
		});

		selectDBbutton.setText("Select new DB...");
		selectDBbutton.setLocation(new Point(500, 60));
		selectDBbutton.setToolTipText("Choose a new database file");
		selectDBbutton.setSize(new Dimension(110, 30));
		selectDBbutton.addActionListener(new  ActionListener() {
			public void actionPerformed( ActionEvent e) {
				selectDBbuttonActionPerformed(e);
			}
		});

		nextRecord.setText(">>");
		nextRecord.setLocation(new Point(570, 110));
		nextRecord.setToolTipText("Display next set of values");
		nextRecord.setSize(new Dimension(40, 20));
		nextRecord.addActionListener(new  ActionListener() {
			public void actionPerformed( ActionEvent e) {
				nextRecordActionPerformed(e);
			}
		});		

		selectedIndex.setLocation(new Point(90, 130));
		selectedIndex.setBackground( Color.white );
		selectedIndex.setVisible(true);
		selectedIndex.setFont(new Font("Dialog", 0, 12));
		selectedIndex.setToolTipText("Sequence number of selected field (-1 if none)");
		selectedIndex.setSize(new Dimension(30, 20));
		//selectedIndex.setBorder( BorderFactory.createLoweredBevelBorder() );
		selectedIndex.setHorizontalAlignment( JTextField.RIGHT );
		selectedIndex.setEditable( false );

		primaryKeyIndex.setLocation(new Point(90, 180));
		primaryKeyIndex.setBackground( Color.white );
		primaryKeyIndex.setVisible(true);
		primaryKeyIndex.setFont(new Font("Dialog", 0, 12));
		primaryKeyIndex.setToolTipText("Sequence number of the field chosen as the primary key (-1 if no key)");
		primaryKeyIndex.setSize(new Dimension(30, 20));
		//primaryKeyIndex.setBorder( BorderFactory.createLoweredBevelBorder() );
		primaryKeyIndex.setHorizontalAlignment( JTextField.RIGHT );
		primaryKeyIndex.setEditable( false );

		filePath.setLocation(new Point(110, 30));
		filePath.setVisible(true);
		filePath.setFont(new Font("Dialog", 0, 12));
		filePath.setToolTipText("Filename of current database");
		filePath.setSize(new Dimension(500, 20));
		filePath.setMargin( new Insets( 2, 2, 2, 2 ) );
		//filePath.setBorder( BorderFactory.createLoweredBevelBorder() );
		

		DatabaseTypeReferent.setMaximumSize(new Dimension(100, 25));
		DatabaseTypeReferent.setLocation(new Point(150, 60));
		DatabaseTypeReferent.setForeground( Global.getDefaultColor( "Concept", "text" ));
		DatabaseTypeReferent.setBackground( Global.getDefaultColor( "Concept", "fill" ) );
		DatabaseTypeReferent.setVisible(true);
		DatabaseTypeReferent.setFont(new Font("Dialog", Font.BOLD, 12));
		DatabaseTypeReferent.setToolTipText("Concept to be used as input to <dbfind> or <lookup>");
		DatabaseTypeReferent.setSize(new Dimension(330, 32));
		DatabaseTypeReferent.setMargin( new Insets( 3, 3, 3, 3 ) );
		DatabaseTypeReferent.setBorder(  BorderFactory.createLineBorder( Global.chargerBlueColor, 3 ) );

		messageWindow.setLocation(new Point(10, 300));
		messageWindow.setForeground( Global.chargerBlueColor );
		messageWindow.setVisible(true);
		messageWindow.setFont(new Font("Dialog", Font.BOLD, 12));
		messageWindow.setToolTipText("Helpful messages");
		messageWindow.setSize(new Dimension(600, 30));
		messageWindow.setMargin( new Insets( 2, 2, 2, 2 ) );
		messageWindow.setBorder( BorderFactory.createCompoundBorder (
			BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder() ) );


		inputConceptLabel.setText("Use as input concept:");
		inputConceptLabel.setForeground(new Color(0, 0, 0));
		inputConceptLabel.setLocation(new Point(10, 70));
		inputConceptLabel.setVisible(true);
		inputConceptLabel.setFont(new Font("Dialog", 0, 12));
		inputConceptLabel.setSize(new Dimension(135, 20));

		databaseFieldsLabel.setText("Database Fields");
		databaseFieldsLabel.setForeground(new Color(0, 0, 0));
		databaseFieldsLabel.setLocation(new Point(150, 110));
		databaseFieldsLabel.setVisible(true);
		databaseFieldsLabel.setFont(new Font("Dialog", 0, 12));
		databaseFieldsLabel.setToolTipText("These names are valid input or output concept types");
		databaseFieldsLabel.setSize(new Dimension(100, 20));

		databaseValuesLabel.setText("Database Values:");
		databaseValuesLabel.setForeground(new Color(0, 0, 0));
		databaseValuesLabel.setLocation(new Point(350, 110));
		databaseValuesLabel.setVisible(true);
		databaseValuesLabel.setFont(new Font("Dialog", 0, 12));
		databaseValuesLabel.setToolTipText("These are the values in the database corresponding to each field");
		databaseValuesLabel.setSize(new Dimension(110, 20));

		sequenceNumberLabel.setText("Sequence:");
		sequenceNumberLabel.setForeground(new Color(0, 0, 0));
		sequenceNumberLabel.setLocation(new Point(20, 130));
		sequenceNumberLabel.setVisible(true);
		sequenceNumberLabel.setFont(new Font("Dialog", 0, 10));
		sequenceNumberLabel.setToolTipText("Sequence number of selected field (-1 if none)");
		sequenceNumberLabel.setSize(new Dimension(70, 20));

		primaryKeyLabel.setText("Primary Key");
		primaryKeyLabel.setForeground(new Color(0, 0, 0));
		primaryKeyLabel.setLocation(new Point(20, 180));
		primaryKeyLabel.setVisible(true);
		primaryKeyLabel.setFont(new Font("Dialog", 0, 10));
		primaryKeyLabel.setToolTipText("Sequence number of the field chosen as the primary key (-1 if no key)");
		primaryKeyLabel.setSize(new Dimension(70, 20));

		titleTextLabel.setText("CharGer: Examine A Database");
		titleTextLabel.setForeground( Global.chargerBlueColor );
		titleTextLabel.setLocation(new Point(10, 0));
		titleTextLabel.setVisible(true);
		titleTextLabel.setFont(new Font("Dialog", 1, 18));
		titleTextLabel.setSize(new Dimension(460, 30));

		SPFieldList.setLocation(new Point(130, 130));
		SPFieldList.setVisible(true);
		SPFieldList.setSize(new Dimension(190, 150));
		SPFieldList.getViewport().add(FieldList);
		SPFieldList.getViewport().addChangeListener( this );
		//SPFieldList.getViewport().setBorder( BorderFactory.createLoweredBevelBorder() ); // JDK 1.4.1 awaits?

		FieldList.setVisible(true);
		FieldList.setToolTipText("These names are valid input or output concept types (click to select; ctrl-click to de-select)");
		FieldList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		FieldList.addListSelectionListener( this );
		FieldList.addMouseListener(new  MouseAdapter() {
			public void mouseClicked( MouseEvent e) {
				FieldListMouseClicked(e);
			}
		});

		SPValueList.setLocation(new Point(330, 130));
		SPValueList.setVisible(true);
		SPValueList.setToolTipText("These are the values in the database corresponding to each field");
		SPValueList.setSize(new Dimension(280, 150));
		SPValueList.getViewport().add(ValueList);
		SPValueList.getViewport().addChangeListener( this );
		//SPValueList.getViewport().setBorder( BorderFactory.createLoweredBevelBorder() );		// JDK 1.4.1 awaits?

		ValueList.setVisible(true);
		ValueList.setToolTipText("These are the values in the database corresponding to each field");

		//SPFieldValuePane = new JScrollPane( FieldValueTable );
		SPFieldValuePane.setLocation(new Point(330, 130));
		SPFieldValuePane.setVisible(true);
		SPFieldValuePane.setToolTipText("These are the values in the database corresponding to each field");
		SPFieldValuePane.setSize(new Dimension(280, 150));
		//SPFieldValuePane.getViewport().add(ValueList);
		SPFieldValuePane.getViewport().addChangeListener( this );

		setLocation(new Point(0, 0));
		setTitle("Database Linking Tool");
		getContentPane().setLayout(null);
		setSize(new Dimension(624, 380));
		getContentPane().add(ExamineButton);
		getContentPane().add(setPrimaryKey);
		getContentPane().add(setupGraph);
		getContentPane().add(selectDBbutton);
		getContentPane().add(nextRecord);
		getContentPane().add(selectedIndex);
		getContentPane().add(primaryKeyIndex);
		getContentPane().add(filePath);
		getContentPane().add(DatabaseTypeReferent);
		getContentPane().add(messageWindow);
		getContentPane().add(inputConceptLabel);
		getContentPane().add(databaseFieldsLabel);
		getContentPane().add(databaseValuesLabel);
		getContentPane().add(sequenceNumberLabel);
		getContentPane().add(primaryKeyLabel);
		getContentPane().add(titleTextLabel);
		getContentPane().add(SPFieldList);
		getContentPane().add(SPValueList);

		getContentPane().setBackground( Global.fuchsiaColor );
		
	}
  
  /*	private boolean mShown = false;
  	
	public void addNotify() 
	{
		super.addNotify();
		
		if (mShown)
			return;
			
		// move components to account for insets
		Insets insets = getInsets();
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Point location = components[i].getLocation();
			location.move(location.x, location.y + insets.top);
			components[i].setLocation(location);
		}

		mShown = true;
	}
*/


	public void valueChanged(ListSelectionEvent e) 
	{
		String selectedOne = (String)FieldList.getSelectedValue();
				// Global.info( "selected one is " + selectedOne );
		if ( db == null ) messageWindow.setText( "Choose some database and examine it first." );
		else {
			selectedIndex.setText( "" + db.getFieldPosition( selectedOne) );
			messageWindow.setText( "" );
		}
		
	}

	/**
		Handle scrolling list events. 
		If the viewport of either scrollpane reports a change (e.g., we've scrolled either),
		then set the other viewport's position. Allows user to scroll either side.
	 */
	public void stateChanged( ChangeEvent ce ) 
	{
		Object source = ce.getSource();
		if ( source instanceof JViewport ) {
			JViewport vp = (JViewport)source;
				Point p = vp.getViewPosition();
			if ( vp.getView() == FieldList )
			{
				SPValueList.getViewport().setViewPosition( new Point( 0, p.y ) );
			}
			if ( vp.getView() == ValueList )
			{
				SPFieldList.getViewport().setViewPosition( new Point( 0, p.y ) );
			}
		}
	}
	
	// Hide the window when the close box is clicked but leave it intact
	void thisWindowClosing( WindowEvent e)
	{
		setVisible(false);
	}

	public void ExamineButtonActionPerformed( ActionEvent e) 
	{
		ExamineDatabase();
	}
	

	void clearWindow()
	{
		messageWindow.setText( "" );
		FieldList.removeAll();
		ValueList.removeAll();
		selectedIndex.setText( "" );
	}
	

	public void ExamineDatabase()
	{
		clearWindow();
			//charger.Global.info( "ready for examine button action performed." );
     	String fname = null;
     	
     	fname = filePath.getText();
     	
     	if ( fname.equals( "" ) ) {
	    messageWindow.setText( "Please select a database to examine." );
			//selectDB();
     	}
	else
	{
     	
	    fname = filePath.getText();
     	
	    try {
	    db = (TextDatabase) Global.activateDatabase( fname );
	    if ( db == null ) throw new CGFileException( "Can't find \"" + fname + "\" database." );
	    else {
			    db = new TextDatabase( fname );
			    LoadFieldsAndPositions( );
		    }
	    } catch ( CGFileException fe ) { 
		    messageWindow.setText( "Error in loading database file \"" + fname + "\"." );
		}
	    DatabaseTypeReferent.setText("Database:  " + 
					    General.getSimpleFilename( fname ) );
	    selectedIndex.setText( "" );
	    primaryKeyIndex.setText( "" );
	    }
	}

	
	/**
		Scans the given input file (generally a path relative to the current Folder) and determines
		whether it is a valid database file. For CharGer purposes, "valid" means it is a tabular
		list of tab-separated values, where the first line contains the headers to the table.
		A number of conditions can be noted for the user.
		
	 */
	public void LoadFieldsAndPositions(  )
	{
		FieldList.removeAll();
		DBFieldArray = db.getValidNames();	// 1st record should contain header names
		FieldList.setListData( DBFieldArray );
		LoadNextValues();
	}
	
	/**
		Gets the next line of data from the database file and displays it in the window.
	 */
	public void LoadNextValues()
	{
		if ( db == null )
		{
			messageWindow.setText( "Please set up a database (Select New DB) before looking at values.");
			return; 
		}
		try {
			DBFieldArray = db.getFields(); 	// should be first line of real data
			if ( DBFieldArray == null )
			{
				messageWindow.setText( "There are more values in database file." );	
			}
			else {
				ValueList.removeAll();
				ValueList.setListData( DBFieldArray );
			}
		} catch ( CGFileException fe ) 
		{
			messageWindow.setText( "Something went wrong trying to load next values." );	 
		}
	
	}
	
	
	public void nextRecordActionPerformed( ActionEvent e)
	{
		LoadNextValues();
	}
	
    private boolean selectDB()
    {
	File DatabaseFile = null;
	JFileChooser filechooser = new JFileChooser( Global.DatabaseFolderFile.getAbsolutePath() ); 
	filechooser.setDialogTitle( "Choose Database (text) File" );

	int returned = filechooser.showOpenDialog( this );
	
		// if approved, then continue
	if ( returned == JFileChooser.APPROVE_OPTION ) 
	{
	    DatabaseFile = filechooser.getSelectedFile();
	}
	else
	{
	    messageWindow.setText( "No new database opened." );
	    return false;
	}


     	String fname = DatabaseFile.getAbsolutePath();
     	filePath.setText( fname );
     	return true;	
    }
	
	/**
		Sets up a graph based on the user's choice of a database and primary key.
	 */
	public void setupGraphActionPerformed( ActionEvent e)
	{
		setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
		try {
			if ( db == null ) 
				messageWindow.setText( "Please set up a database (Select New DB) before setting up a graph." );
			else {
				if ( ! primaryKeyIndex.getText().equals( "" ) )
					db.primaryKey = Integer.parseInt( primaryKeyIndex.getText() );
				else 
					db.primaryKey = -1;
				EditFrame ef = null;
				ef = db.setupGraph( db.primaryKey );
				ef.emgr.setChangedContent( EditChange.SEMANTICS  );  // not sure whether this should be undo-able
				//ef.toFront();		// commented 12-16-02
				//Global.setCurrentEditFrame( ef );
			}
		} catch ( CGException cge ) { messageWindow.setText( "Can't set up a graph. " + cge.getMessage() ); }
		setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
	}
	
	public void selectDBbuttonActionPerformed( ActionEvent e)
	{
		if ( selectDB() ) ExamineDatabase();
	}
	
	public void setPrimaryKeyActionPerformed( ActionEvent e)
	{
		String selectedOne = (String)FieldList.getSelectedValue();
				// Global.info( "selected one is " + selectedOne );
		if ( db == null ) 
			messageWindow.setText( "Please open a database (Select New DB) before setting a key.");
		else {
			if ( selectedOne == null ) selectedOne = new String( "" );
			db.setPrimaryKey( selectedOne );
			primaryKeyIndex.setText( "" + db.primaryKey );
			if ( db.primaryKey >= 0 )
				messageWindow.setText( "Key is field number " + db.getPrimaryKey() + 
					" called \"" + db.getPrimaryKeyString() + "\"" );
			else
			messageWindow.setText( "No primary key set." );
			
		}
	}
	
	public void FieldListMouseClicked( MouseEvent e)
	{
		//Global.info( "mouse clicked" );
		if ( FieldList.getSelectedIndex() == -1 ) FieldList.clearSelection();
	}
	
		
		
}
