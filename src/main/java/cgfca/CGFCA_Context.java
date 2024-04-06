/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cgfca;

import java.util.Iterator;

import charger.obj.Concept;
import kb.BinaryTuple;
import repgrid.RGElement;
import repgrid.RepertoryGrid;

/**
 * Extends the idea of a repertory grid to contain the information needed for a CG-FCA context.
 * In CG-FCA, attributes are really a CG concept-relation pair (as contained in the incomplete
 * binary tuples used in this package), while elements are really a CG concept. 
 * In order to account for multiple occurrences with identical labels,
 * the binary tuple and concept themselves must be recorded as attributes and elements respectively.
 * 
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class CGFCA_Context extends RepertoryGrid {

    public CGFCA_Context() {
        super();
        this.setAttributeLabel( "Tuples" );
        this.setElementLabel( "Concepts" );
    }
    
    
    
    public void addAttributeAsTuple( BinaryTuple bt ) {
        CGFCA_Attribute attribute = new CGFCA_Attribute( bt );

        this.addRGAttribute(attribute );
    }

    public void addElementAsConcept( Concept c ) {
        // TODO: Check for a co-referent here; if co-referent to an existing element, then add to element's concept list
        // else proceed
        CGFCA_Element element = checkForCoReferents( c );
        if ( element != null )
            this.addRGElement( element );
        
    }
    
        /**
     * Finds an attribute object based on its label.
     *
     * @param label identifier for this attribute
     * @return attribute object if found; <code>null</code> otherwise.
     */
    public CGFCA_Attribute getAttribute( BinaryTuple bt ) {
        Iterator iter = attributes.iterator();
        while ( iter.hasNext() ) {
            CGFCA_Attribute attr = (CGFCA_Attribute)iter.next();
//            if ( attr.getLabel().equals( label ) ) {
            if ( attr.getTuple() == bt )  { // Make sure it's the same object, not just same values      
                return attr;
            }
        }
        return null;
    }

    /**
     * Finds an element object based on its label.
     *
     * @param label identifier for this element
     * @return element object if found; <code>null</code> otherwise.
     */
    public CGFCA_Element getElement( Concept con ) {
        for ( RGElement elem : elements ) {
            CGFCA_Element element = (CGFCA_Element)elem;
            if ( element.getConcepts().contains( con )) {
                return element;
            }
        }
//        Iterator iter = elements.iterator();
//        while ( iter.hasNext() ) {
//            CGFCA_Element elem = (CGFCA_Element)iter.next();
////            if ( elem.getLabel().equals( label ) ) {
//        // TODO: Use concepts list instead
//            if ( elem.getConcept() == con  ) {
//                return elem;
//            }
//        }
        return null;
    }

    /**
     * Scan the list of elements looking for a coreferent to the given concept in their concept lists.
     * If one is found, then add the concept to that element.
     * If one isn't found, then create a new element suitable for adding
     * @param con
     * @return null if concept can be combined with another via coreference; a
     * new element otherwise.
     */
    public CGFCA_Element checkForCoReferents( Concept con ) {
        // TODO: Look for actual coreferent links to this concept.
        if ( !CG_FCA.enableCoreferents ) {
            return new CGFCA_Element( con );
        }
        for ( RGElement e : this.getElements() ) {
            CGFCA_Element element = (CGFCA_Element)e;
            for ( Concept c : element.getConcepts() ) {
                if ( ! c.getReferent().isEmpty() && ! con.getReferent().isEmpty() 
                        && c.getReferent().equals( con.getReferent())) {
                    element.addToConcepts( con );
                    return null;
                }
            }
        }
        
        return new CGFCA_Element( con );
    }
    
    /**
     * Scan the elements and find one that contains the given co-referent concept in its concept list.
     * @return the first co-referent found; null if there isn't one.
    */
    public CGFCA_Element findCoReferentElement( Concept con ) {
        if ( !CG_FCA.enableCoreferents ) {
            return null;
        }
        for ( RGElement e : this.getElements() ) {
            CGFCA_Element element = (CGFCA_Element)e;
            for ( Concept c : element.getConcepts() ) {
                if ( ! c.getReferent().isEmpty() && ! con.getReferent().isEmpty() 
                        && c.getReferent().equals( con.getReferent())) {
                    
                    return element;
                }
            }
        }
        
        return null;
    }
}
