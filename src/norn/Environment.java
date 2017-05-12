package norn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A mutable, threadsafe representation of currently defined list names in a 
 * session of creating email lists.
 */
public class Environment {
    private final Map<Name, ListExpression> definitions;
    
    // AF(definitions) = a list expression execution environment where the keys in definitions
    //                   comprise the lists that have been defined and the values are the corresponding
    //                   list expressions. Any name not in definitions is defined as the empty expression.
    // RI: there are no mail loops (mutually recursive list definitions).
    // Rep exposure: 
    // Thread safety: 
    
    /**
     * Create a new Environment.
     */
    public Environment() {
        definitions = Collections.synchronizedMap(new HashMap<Name, ListExpression>());
    }

    /**
     * Checks that representation invariant is maintained.
     */
    private void checkRep() {
        // assert definitions is not null
        // assert that there are no mail loops
    }
    
    /**
     * Get the expression corresponding to the specified list name
     * @param name the Name to get the definition of
     * @return the corresponding expression in definitions if one exists;
     *  returns empty expression otherwise (i.e., name has not been defined).
     */
    public ListExpression getExpression(Name name) {
        // TODO implement
        throw new UnsupportedOperationException("Implement me!");
    }
    
    /**
     * Saves all definitions in this Environment to a file.
     * @param fileName The name of the file to be written
     * @param expression 
     * @return true if definitions were successfully saved to fileName
     */
    public boolean save(String filename) {
        // TODO implement
        throw new UnsupportedOperationException("Implement me!");
    }
    
    /**
     * Attempts to read and parse a file containing a valid list expression.
     * Adds all definitions in the file to this Environment.
     * @param file The name of the file to be loaded. Cannot contain newlines. 
     *  The contents of the file should be a single valid list expression. 
     * @return true if the contents of the file were successfully loaded,
     *  false otherwise.
     */
    public boolean load(String filename) {
        // TODO implement
        throw new UnsupportedOperationException("Implement me!");
    }
}
