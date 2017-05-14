package norn;

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
        
    }
    
    /*
     * Checks that rep invariant has been maintained.
     */
    private void checkRep() {
        
    }
    
    @Override
    public Set<Recipient> recipients(Environment environment) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * Returned String has the format:
     *      name.toString = definition.toString
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
