package norn;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
        this.left = left;
        this.right = right;
        checkRep();
    }
    
    /*
     * Check that the rep invariant holds.
     */
    private void checkRep() {
        assert left != null;
        assert right != null;
    }

    @Override
    public Set<Recipient> recipients(Environment environment) {
        synchronized (environment) {
            left.recipients(environment);
            return right.recipients(environment);
        }
    }
    
    @Override
    public Set<ListExpression> getChildren() {
        return new HashSet<>(Arrays.asList(left, right));
    }
    
    @Override
    public Set<ListExpression> getDependents(Environment environment) {
        return Collections.emptySet();
    }
    
    /*
     * Returned String is of the format:
     *      left.toString; right.toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return left.toString() + "; " + right.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sequence)) return false;
        Sequence that = (Sequence) obj;
        return left.equals(that.left) && right.equals(that.right);
    }
    
    @Override
    public int hashCode() {
        return left.hashCode() + right.hashCode();
    }

}
