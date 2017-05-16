package norn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        assert definitions != null;
        for (Name name : definitions.keySet()) {
            ListExpression expression = definitions.get(name);
            // check for mail loop
            // a = a is not a mail loop
            // a = b,c and then b = a is a mail loop
            // use graph to check for mail loops
        }
    }
    
    /**
     * Get the expression corresponding to the specified list name
     * @param name the Name to get the definition of
     * @return the corresponding expression in definitions if one exists;
     *  returns empty expression otherwise (i.e., name has not been defined).
     */
    public ListExpression getExpression(Name name) {
        if (definitions.containsKey(name)) {
            return definitions.get(name);
        }
        return new Empty();
    }
    
    /**
     * Get the names of all definitions in this environment
     * @return a set containing all names in this environment
     */
    public Set<Name> getNames() {
        return definitions.keySet();
    }
    
    /**
     * Reassign the specified name to the specified expression and return
     * the expression previously linked to the name. If there was no 
     * definition for this name previously, returns an empty expression.
     * @param name the name to define
     * @param expression the expression to link to name
     * @return the previous expression linked to this name (empty if none).
     */
    public ListExpression reassign(Name name, ListExpression expression) {
        ListExpression exp = getExpression(name);
        definitions.put(name, expression);
        return exp;
    }
}
