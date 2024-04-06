/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cgfca;

import java.util.ArrayList;

import charger.obj.Concept;
import repgrid.RGElement;

/**
 *
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class CGFCA_Element extends RGElement {
    
    Concept concept = null;
    // to handle corefs
    ArrayList<Concept> concepts = new ArrayList<>();

    public CGFCA_Element( Concept con ) {
        super( con.getTextLabel() );
        concept = con;
        concepts.add( con );
    }

    public Concept getConcept() {
//        return concept;
        return concepts.get( 0 );
    }
    
    public ArrayList<Concept> getConcepts() {
        return concepts;
    }
    
    public void addToConcepts( Concept con ) {
        concepts.add( con );
    }

    public void setConcept( Concept concept ) {
        this.concept = concept;
    }
    
    public boolean equals( CGFCA_Element other ) {
        if ( ! CG_FCA.enableCoreferents) {
            if ( this.getConcept() != other.getConcept() ) return true;
            else return false;
        } else {
            if ( this.getConcept().getReferent().equals( other.getConcept().getReferent())) {
                return true;
            } else {
                // TODO: check for co-referent links here
                return false;
            }
        }
    }
    
    /**
     * Concatenate all non-duplicate type labels, followed by concatenating all non-duplicate referent strings.
     * @return 
     */
    public String getLabel() {
        String typeLabels = new String( "" );
        String referentLabels = new String( "" );

        ArrayList<String> previousTypeLabels = new ArrayList<>();
        ArrayList<String> previousRefLabels = new ArrayList<>();

        for ( Concept con : concepts ) {
            if( ! previousTypeLabels.contains( con.getTypeLabel())) {
                typeLabels += (typeLabels.isEmpty() ? "" : "; ") + con.getTypeLabel();
                previousTypeLabels.add( con.getTypeLabel());
            }
            
            if( ! previousRefLabels.contains( con.getReferent())) {
                referentLabels += (referentLabels.isEmpty() ? "" : "; ") + con.getReferent();
                previousRefLabels.add( con.getReferent());
            }
        }
        
        
        return typeLabels + (! referentLabels.isEmpty() ? ": ": "") + referentLabels;
    }

}
