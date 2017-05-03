package norn;

import java.util.Map;
import java.util.Set;

import lib6005.parser.*;

/**
 * An immutable data type representing a set of email addresses.
 */
public interface ListExpression {
    
    // Datatype Definition
    // ListExpression = Empty 
    //                  + Recipient(emailAddress: String)
    //                  + Union(left: ListExpression, right: ListExpression)
    //                  + Difference(left: ListExpression, right: ListExpression)
    //                  + Intersect(left: ListExpression, right: ListExpression)
    //                  + Definition(name: Name, value: ListExpression) 
    //                  + Name(name: String)
    
    /**
     * Parse an input according to the grammar in NornGrammar.g.
     * @param input String to parse
     * @return listexpression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static ListExpression parse(String input) {
        try {
            return NornParser.parse(input);
        } catch (UnableToParseException e) {
            throw new IllegalArgumentException("expression invalid");
        }   
    }
    
    /**
     * Determines the set of unique recipients represented by the current ListExpression,
     * evaluating all list names completely. Throws IllegalArgumentException if expression contains
     * a list name that was not previously defined
     * @param environment Map of list definitions (name --> list expression) to use in 
     *        evaluating list names where all list names in the current ListExpression are
     *        in environment.
     * @return the Set of unique recipients represented
     */
    public Set<Recipient> recipients(Map<String, ListExpression> environment);
    
    /**
     * Determine all list definitions in a list expression.
     * @return a map of list names to their corresponding expressions
     */
    public Map<String, ListExpression> environment();
    
    /**
     * @return a parsable representation of this listexpression, such that
     * for all e:ListExpression, e.equals(ListExpression.parse(e.toString())).
     */
    @Override
    public String toString();
    
    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally equal
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that 2 ListExpressions that are .equals must also have equal hashCodes
     */
    @Override
    public int hashCode();
}
