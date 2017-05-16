package norn;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lib6005.parser.*;

/**
 * An immutable data type representing a set of email addresses.
 */
public interface ListExpression {
    
    // Datatype Definition
    // ListExpression = Empty() 
    //                  + Recipient(emailAddress: String)
    //                  + Union(left: ListExpression, right: ListExpression)
    //                  + Difference(left: ListExpression, right: ListExpression)
    //                  + Intersect(left: ListExpression, right: ListExpression)
    //                  + Sequence(left: ListExpression, right: ListExpression)
    //                  + Name(name: String)
    //                  + Definition(name: Name, expression: ListExpression)
    
    /**
     * Parse an input according to the grammar in ListExpression.g.
     * @param input String to parse
     * @return listexpression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static ListExpression parse(String input) {
        try {
            return ListExpressionParser.parse(input);
        } catch (UnableToParseException e) {
            throw new IllegalArgumentException("expression invalid");
        }   
    }
    
    /**
     * Determines the set of unique recipients represented by the current ListExpression,
     * evaluating all list names completely. List names that are not defined in the current
     * environment are evaluated as empty lists. Modifies environment to contain all 
     * definitions in this ListExpression.
     * @param environment the list definitions used to evaluate this ListExpression
     * @return the Set of unique recipients represented
     */
    public Set<Recipient> recipients(Environment environment);
    
    
    /**
     * For the current ListExpression, get the list of ListExpressions that it depends on.
     * If the current ListExpression does not depend on any ListExpressions, then an empty list
     * is returned.
     * @return the list of ListExpressions that make up a ListExpression
     */
    public List<ListExpression> getChildren();
    
    
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
