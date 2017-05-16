package norn;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable representation of the intersection of two sets of email addresses.
 */
public class Intersect implements ListExpression {
    private final ListExpression left;
    private final ListExpression right;
    
    // AF(left, right) = the set of all elements in the set defined by left
    //                   that also appear in the set defined by right
    // Rep invariant: true
    // Rep safety: all fields are private, final, and immutable. 
    //  All references to any returned mutable objects are discarded.
    
    /**
     * Create a new Intersect object.
     * @param left the left ListExpression 
     * @param right the right ListExpression
     */
    public Intersect(ListExpression left, ListExpression right) {
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
    public Set<Recipient> recipients(Environment environment) {
        Set<Recipient> intersection = new HashSet<>();
        Set<Recipient> rightRecipients = right.recipients(environment);
        for (Recipient l : left.recipients(environment)) {
            if (rightRecipients.contains(l)) {
                intersection.add(l);
            }
        }
        return intersection;
    }
    
    @Override
    public Set<ListExpression> getChildren(Environment environment) {
        return new HashSet<>(Arrays.asList(left, right));
    }

    /**
     * The returned String has the format 
     *  (left.toString * right.toString)
     */
    @Override
    public String toString() {
        return "(" + left.toString() + " * " + right.toString() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Intersect)) return false;
        Intersect that = (Intersect) obj;
        return left.equals(that.left) && right.equals(that.right);
    }
    
    @Override
    public int hashCode() {
        return left.hashCode() * right.hashCode();
    }

}
