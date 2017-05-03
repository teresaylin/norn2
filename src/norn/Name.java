package norn;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Immutable representation of the name of a set of email addresses.
 */
public class Name implements ListExpression {
    private final String name;
    
    // Abstraction function: AF(name) = the name of a set
    // Rep invariant: name is nonempty
    // Rep safety: all fields are private, final, and immutable
    //  All references to any returned mutable objects are discarded.
    
    /**
     * Create a new Name object.
     * @param name the name to use
     */
    public Name(String name) {
        this.name = name;
        checkRep();
    }
    
    /**
     * Check that the rep invariant is maintained
     */
    private void checkRep() {
        assert name != null;
        assert name.length() > 0;
    }
    
    @Override
    public Set<Recipient> recipients(Map<String, ListExpression> environment) {
        if (!environment.containsKey(name)){
            throw new IllegalArgumentException("This list name is undefined");
        }
        return environment.get(name).recipients(environment);
    }
    
    @Override
    public Map<String, ListExpression> environment() {
        return Collections.emptyMap();
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
