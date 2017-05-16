package norn;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable representation of a union of two sets of email addresses.
 */
public class Union implements ListExpression {
    private final ListExpression left;
    private final ListExpression right;
    
    // Abstraction Function
    //  AF(left, right) = a list of emails that is the union of the set
    //  of emails in left and the set of emails in right
    // Rep Invariant
    //  true
    // Rep Safety
    //  All fields are private, final, and immutable.
    //  All references to any returned mutable objects are discarded.
    
    /**
     * Create a new Union object.
     * @param left one of the lists to perform the union operation on
     * @param right the second list to perform the union operation on
     */
    public Union(ListExpression left, ListExpression right) {
        this.left = left;
        this.right = right;
        checkRep();
    }
    
    /**
     * Check that the rep invariant is maintained.
     */
    private void checkRep() {   
        assert left != null;
        assert right != null;
    }
    
    @Override
    public Set<Recipient> recipients(Environment environment) {
        Set<Recipient> allRecipients = new HashSet<>(left.recipients(environment));
        allRecipients.addAll(right.recipients(environment));
        return allRecipients;
        
    }
    
    @Override
    public Set<ListExpression> getChildren(Environment environment) {
        return new HashSet<>(Arrays.asList(left, right));
    }
    
    /**
     * The returned String has the format 
     *      (leftList.toString, rightList.toString)
     * It contains every email in this Union exactly once and contains
     * no other emails. 
     */
    @Override
    public String toString() {
        return "("+ left.toString() + ", " + right.toString() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Union)) return false;
        Union that = (Union) obj;
        return left.equals(that.left) && right.equals(that.right);
    }
    
    @Override
    public int hashCode() {
        return left.hashCode() + right.hashCode();
    }
}
