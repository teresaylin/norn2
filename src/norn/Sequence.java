package norn;

import java.util.Map;
import java.util.Set;

/**
 * An immutable representation of a sequence of email list expressions. 
 */
public class Sequence implements ListExpression {
    private final ListExpression left;
    private final ListExpression right;
    
    // AF: AF(left, right) = a sequence of email list expressions where the first
    //                       part of the sequence is described by left and the second
    //                       part is described by right.
    // RI: true
    // Rep Safety: All fields are private, final, and immutable. 
    
    /**
     * Create a new Sequence object
     * @param left the left expression in this sequence
     * @param right the right expression in this sequence
     */
    public Sequence(ListExpression left, ListExpression right) {
        
    }
    
    /*
     * Check that the rep invariant holds.
     */
    private void checkRep() {
        
    }

    @Override
    public Set<Recipient> recipients() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /*
     * Returned String is of the format:
     *      left.toString; right.toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }

}
