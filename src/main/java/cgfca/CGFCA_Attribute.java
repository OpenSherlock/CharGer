/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cgfca;

import charger.obj.Concept;
import kb.BinaryTuple;
import repgrid.RGAttribute;

/**
 * An attribute specifically for the CGFCA application. 
 * 
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class CGFCA_Attribute extends RGAttribute {
    
    /** The tuple representing this attribute */
    BinaryTuple tuple = null;

    public CGFCA_Attribute( BinaryTuple bt ) {
        super( CG_FCA.binaryToString( bt )  );
        tuple = bt;
    }

    public BinaryTuple getTuple() {
        return tuple;
    }

    public void setTuple( BinaryTuple tuple ) {
        this.tuple = tuple;
    }
    
    public boolean equals( CGFCA_Attribute other ) {
        if ( this == other )
            return true;
        else
            return false;
    }

    @Override
    public String getLabel() {
//        return super.getLabel();
        if ( !CG_FCA.enableCoreferents ) {
            return CG_FCA.binaryToString( tuple );
        }

        Concept fromConcept = tuple.getFromConcept();
        // Need access to context here, to resolve co-referents in from concept.
        CGFCA_Element elem = CG_FCA.getGridForCGFCA().findCoReferentElement( fromConcept );
        if ( elem == null ) {
            return CG_FCA.binaryToString( tuple );
        } else {
            return elem.getLabel() + " " + tuple.getRelation().getTypeLabel();
        }

    }

}
