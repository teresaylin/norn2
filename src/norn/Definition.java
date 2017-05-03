package norn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Immutable representation of a named set of email addresses.
 */
public class Definition implements ListExpression {
    private final ListExpression value;
    private final Name name;
    
    // Abstraction function: AF(value, name) = the set of recipients defined by value, named name
    // Rep invariant: true
    // Rep safety: All fields are private, final, and immutable.
    // All references to any returned mutable objects are discarded.
    
    /**
     * Create a new Definition.
     * @param value the ListExpression used in this Definition
     * @param name the name of this list
     */
    public Definition(ListExpression value, Name name) {
        this.value = value;
        this.name = name;
        checkRep();
    }
    
    /**
     * Check that the rep invariant is maintained.
     */
    private void checkRep() {
        assert value != null;
        assert name != null;
    }
    
    @Override
    public Set<Recipient> recipients(Map<String, ListExpression> environment) {
        return new HashSet<Recipient>(value.recipients(environment));
    }
    
    @Override
    public Map<String, ListExpression> environment() {
        Map<String, ListExpression> environment = new HashMap<>();
        environment.put(name.toString(), value);
        return environment;
    }
    
    /**
     * Returned String has the format: 
     *      name = value.toString()
     */
    @Override
    public String toString() {
        return name.toString() + " = " + value.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Definition)) return false;
        Definition that = (Definition) obj;
        return name.equals(that.name) && value.equals(that.value);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() + value.hashCode();
    }

}
