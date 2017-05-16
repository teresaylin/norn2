package norn;

import java.util.Collections;
import java.util.Set;

/**
 * Immutable representation of a named set of email addresses.
 */
public class Name implements ListExpression {
    private final String name;
    
    // Abstraction function: AF(name) = the listname defined by name
    // Rep invariant: all characters in name are letters, digits, underscores, dashes, or periods.
    // Rep safety: All fields are private, final, and immutable.
    // All references to any returned mutable objects are discarded.
    
    /**
     * Create a new Name.
     * @param value the ListExpression used in this Definition
     * @param name the name of this list
     */
    public Name(String name) {
        this.name = name;
        checkRep();
    }
    
    
    /**
     * Check that the rep invariant is maintained.
     */
    private void checkRep() {
        assert name != null;
        assert name.matches("[A-Za-z0-9_.-]+");
    }
    
    @Override
    public Set<Recipient> recipients(Environment environment) {
        ListExpression expr = environment.getExpression(this);
        return expr.recipients(environment);
    }
    
    @Override
    public Set<ListExpression> getChildren() {
        return Collections.emptySet();
    }
    
    /**
     * Returned String has the format: 
     *      name 
     */
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Name)) return false;
        Name that = (Name) obj;
        return name.equals(that.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
