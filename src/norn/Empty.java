package norn;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/** 
 * Immutable representation of an empty set of email addresses
 */
public class Empty implements ListExpression {

    // Abstraction function
    //  AF() = an empty list of email addresses
    // Rep invariant
    //  true
    // Rep safety
    //  There are no fields.
    
    @Override
    public Set<Recipient> recipients(Environment environment) {
        throw new UnsupportedOperationException("Implement me!");
//        return Collections.emptySet();
    }

    /**
     * Returned String is the empty String.
     */
    @Override
    public String toString() {
        return "";
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Empty;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
}
