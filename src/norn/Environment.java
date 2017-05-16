package norn;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
        checkRep();
    }

    /**
     * Checks that representation invariant is maintained.
     */
    private void checkRep() {
        // assert definitions is not null
        // assert that there are no mail loops
        assert definitions != null;
        for (Name name : definitions.keySet()) {
            // for each name in definitions, run DFS on each child node, keep track of nodes visited
            ListExpression expression = definitions.get(name);
            Set<ListExpression> visited = new HashSet<>();
            Set<ListExpression> finalVisited = findDependencies(expression, visited);
            assert !finalVisited.contains(name) : name.toString() + " has a mail loop in its definition! Please reassign " + name.toString();
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
        checkRep();
        return exp;
    }
    
    /**
     * Performs a DFS search on a listname to find its ListExpression dependencies.
     * @param expression the expression assigned to the listname
     * @param visited a set of ListExpressions in the listname's dependencies
     * @return the set of ListExpressions in the listname's dependencies
     */
    private Set<ListExpression> findDependencies(ListExpression expression, Set<ListExpression> visited) {
        List<ListExpression> children = expression.getChildren();
        if (children.size() != 0) {
            visited.addAll(children);
            for (ListExpression child : children) {
                visited = findDependencies(child, visited);
            }
        }
        return visited;
    }
}
