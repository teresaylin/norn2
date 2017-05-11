package norn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Immutable representation of the set difference of two sets of email addresses.
 */

public class Difference implements ListExpression {
    private final ListExpression left;
    private final ListExpression right;
    
    // AF(left, right) = the set of all elements in the set defined by left
    //                   that are not in the set defined by right
    // Rep invariant: true
    // Rep safety: all fields are private, final, and immutable. All 
    // references to any returned mutable objects are discarded.
    
    /**
     * Create a new Difference object
     * @param left the left ListExpression of this Difference
     * @param right the right ListExpression of this Difference
     */
    public Difference(ListExpression left, ListExpression right) {
        this.left = left;
        this.right = right;
        checkRep();
    }
    
    /**
     * Check that the representation invariant is maintained.
     */
    private void checkRep() {
        assert left != null;
        assert right != null;
    }
    
    @Override
    public Set<Recipient> recipients(Map<Name, ListExpression> environment) {
        Set<Recipient> difference = new HashSet<>();
        Set<Recipient> rightRecipients = right.recipients(environment);
        for (Recipient l : left.recipients(environment)) {
            if (!(rightRecipients.contains(l))) {
                difference.add(l);
            }
        }
        return difference;
    }

    /**
     * The returned String has the format 
     *  (left.toString ! right.toString)
     */
    @Override
    public String toString() {
        return "(" + left.toString() + " ! " + right.toString() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Difference)) return false;
        Difference that = (Difference) obj;
        return left.equals(that.left) && right.equals(that.right);
    }
    
    @Override
    public int hashCode() {
        return left.hashCode() - right.hashCode();
    }
}
