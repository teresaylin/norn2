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
    // Rep exposure: the definitions map is private, final, and never returned through any of the methods.
    // Thread safety: definitions is a synchronized map, which means that any actions to modify it are atomic.
    //                Any methods that mutate definitions are therefore threadsafe.
    
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
        assert definitions != null;
        for (Name name : definitions.keySet()) {
            System.out.println("checking key: " + name.toString());
            // for each name in definitions, run DFS on each child node, keep track of nodes visited
            ListExpression expression = definitions.get(name);
            Set<ListExpression> visited = new HashSet<>();
            Set<ListExpression> finalVisited = findDependencies(name, expression, visited);
            System.out.println("finalVisited: " + finalVisited);

            assert !finalVisited.contains(name) : name.toString() + " has a mail loop in its definition! Please reassign " + name.toString();
        }
        System.out.println(definitions);
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
     * @param name the listname to perform the DFS search on
     * @param expression the expression assigned to the listname
     * @param visited a set of ListExpressions in the listname's dependencies
     * @return the set of ListExpressions in the listname's dependencies
     */
    private Set<ListExpression> findDependencies(Name name, ListExpression expression, Set<ListExpression> visited) {
        Set<ListExpression> children = expression.getChildren(this);
        System.out.println("children of " + expression.toString() + ": " + children);
        System.out.println("how many children: " + children.size());
        if (children.size() != 0) {
            System.out.println("has children!");
            visited.addAll(children);
            for (ListExpression child : children) {
                if (child.equals(name)) { break; }
                visited = findDependencies(name, child, visited);
            }
        }
        return visited;
    }
}
