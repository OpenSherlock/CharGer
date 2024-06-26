package charger.obj;

import java.awt.*;
import java.awt.geom.*;

import charger.*;
import charger.util.CGUtil;
import chargerlib.CharacterSets;
import chargerlib.General;
import chargerlib.WrappedText;
import kb.KnowledgeBase;
import kb.hierarchy.TypeHierarchyNode;

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
 * Implements the relation type construct in a hierarchy.
 *
 * @author Harry S. Delugach ( delugach@uah.edu ) Copyright (c) 1998-2020 by
 * Harry S. Delugach.
 */
public class RelationLabel extends GNode {

    // @bug this is probably not what I want
    /* REMOVE-NOTIO public notio.RelationType nxRelationType = new notio.RelationType( "link" );*/

    /**
     * Instantiates a relation type with default label "link".
     */
    public RelationLabel() {
        init( "link" );
    }

    /**
     * Instantiates a relation type with the given label.
     *
     * @param label the relation label's string value
     */
    public RelationLabel( String label ) {
        init( label );
    }

    private void init( String label ) {
        setTextLabel( label );
        foreColor = (Color)( Global.userForeground.get(CGUtil.shortClassName( this ) ) );
        backColor = (Color)( Global.userBackground.get(CGUtil.shortClassName( this ) ) );
        displayRect3D.height =  displayRect3D.height * 0.60;  
    }

    /**
     * @see GNode#getShape
     */
    public Shape getShape() {
        return new RoundRectangle2D.Double(
                getUpperLeft().x, getUpperLeft().y, getDim().width, getDim().height,
                ( getDim().width ) / 2, ( getDim().width ) / 2 );
    }

    public void draw( Graphics2D g, boolean printing ) {
        if ( Global.showShadows ) {
            g.setColor( Global.shadowColor );
            g.translate( Global.shadowOffset.x, Global.shadowOffset.y );
            g.fill( getShape() );
            g.translate( -1 * Global.shadowOffset.x, -1 * Global.shadowOffset.y );
        }
        g.setColor( backColor );
        g.fill( getShape() );

        g.setColor( foreColor );
        g.draw( new Line2D.Double( getUpperLeft().x, getUpperLeft().y + getDim().height - 3,
                getUpperLeft().x + getDim().width, getUpperLeft().y + getDim().height - 3 ) );

//        g.drawString( textLabel, textLabelLowerLeftPt.x, textLabelLowerLeftPt.y );
        WrappedText text = new WrappedText( textLabel, GraphObject.defaultWrapColumns );
        if ( !getWrapLabels() ) {
            text.setColumns( -1 );
        }
        text.drawWrappedText( g, getCenter(), getLabelFont() );

        Stroke s = g.getStroke();
        g.setStroke(
                new BasicStroke( 1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dashpattern, 1.0f ) );
        g.draw( getShape() );
        g.setStroke( s );

        super.draw( g, printing );
    }

    /**
     * Sets the text label of this relation. If there's a ":", then it and
     * subsequent characters are ignored.
     */
    public void setTextLabel( String label ) {
        if ( label == null ) {
            label = new String( "" );
        }
        // 09-22-2005 by hsd for R0019
        if ( label.contains( ":" ) ) {
            label = label.substring( 0, label.indexOf( ":" ) );
        }
        super.setTextLabel(General.excludeChars( label, CharacterSets.TYPE_LABEL_EXCLUDED_CHARS ) );
//        super.setTextLabel(General.makeLegalChars( label, CharacterSets.TYPE_LABEL_CHARSET ) );
    }

    /**
     * Perform whatever activities are required for this concept to be committed to a knowledge base.
     * Adds the type label to the type hierarchy if it is not already there.
     * @param kb 
     */
    public boolean commitToKnowledgeBase( KnowledgeBase kb ) {
        TypeHierarchyNode node = kb.getRelationTypeHierarchy().addTypeLabel( this.getTypeLabel() );
//            Global.info( "Relation \"" + this.getTypeLabel() + "\" added = " + b + " " + kb.showRelationTypeHierarchy() );
        if ( node == null ) {
            return false;
        } else {
            kb.getRelationTypeHierarchy().addToTopAndBottom( node );
            return true;
        }
    }

    /**
     * Perform whatever activities are required for this concept to be committed to a knowledge base.
     * Adds the type label to the type hierarchy.
     * @param kb 
     */
    public boolean UnCommitFromKnowledgeBase( KnowledgeBase kb ) {
                // TODO: needs to check if this label is used by any other relations and if so, don't do anything more to the hierarchy.
        boolean b = kb.getRelationTypeHierarchy().removeTypeLabel( this.getTypeLabel() );
//            Global.info( "Relation \"" + this.getTypeLabel() + "\" removed = " + b + " " + kb.showRelationTypeHierarchy() );
        return b;
    }

    /**
     * Allocate and initialize a notio relation type that is consistent with the
     * CharGer relation type. Assumes that the CharGer node is correct.
     *
     * @param kb Notio knowledge base, to be used for storing the relation type.
     */
   /* REMOVE-NOTIO  public void updateForNotio( notio.KnowledgeBase kb ) {
        try {
            notio.RelationType r = null;
            if ( textLabel.equalsIgnoreCase( "link" ) ) {
                r = kb.getRelationTypeHierarchy().getTypeByLabel( notio.RelationTypeHierarchy.UNIVERSAL_TYPE_LABEL );
            } else {
                r = kb.getRelationTypeHierarchy().getTypeByLabel( textLabel );
            }
            // @bug doesn't check whether type's old type label should be deleted
            if ( r == null ) {
                nxRelationType = new notio.RelationType( textLabel );
                kb.getRelationTypeHierarchy().addTypeToHierarchy( nxRelationType );
            } else {
                nxRelationType = r;	// linking this type node to an already-existing one! dangerous!!
            }
            nxRelationType.setComment( makeCGIFcomment() );

        } catch ( notio.TypeChangeError tce ) {
        }
    }*/
}
