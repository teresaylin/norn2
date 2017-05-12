package norn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Immutable representation of a named set of email addresses.
 */
public class Name implements ListExpression {
    private final String name;
    
    // Abstraction function: AF(name) = the listname defined by name
    // Rep invariant: true
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
    }
    
    @Override
    public Set<Recipient> recipients(Map<Name, ListExpression> environment) {
        return new HashSet<>();
        // TODO: lookup name in environment
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
