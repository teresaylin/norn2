package norn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Immutable representation of a named set of email addresses. 
 */
public class Definition implements ListExpression {
    private final Name name;
    private final ListExpression expression;
    
    // AF(name, expression) = a set of email addresses defined by expression named name
    // RI: expression does not result in mutually recursive definition of name.
    // Rep exposure: all fields are private and final.
    
    /**
     * Create a new Definition object.
     * @param name the name of this set of email addresses
     * @param expression the ListExpression defining this set of email addresses
     */
    public Definition(Name name, ListExpression expression) {
        this.name = name;
        this.expression = expression;
        checkRep();
    }
    
    /*
     * Checks that rep invariant has been maintained.
     */
    private void checkRep() {
        //TODO how to check for recursion ?
    }
    
    @Override
    public Set<Recipient> recipients(Environment environment) {
        environment.reassign(name, expression);
        return expression.recipients(environment);
    }
    
    @Override
    public List<ListExpression> getChildren() {
        return new ArrayList<>(Arrays.asList(name, expression));
    }

    /*
     * Returned String has the format:
     *      name.toString = definition.toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name.toString() + " = " + expression.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Definition)) { return false; }
        Definition that = (Definition) obj;
        return this.name.equals(that.name) && this.expression.equals(that.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
}
