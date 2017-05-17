package norn;

import java.util.Arrays;
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
    //                Additionally, all public methods are synchronized, so any multi-step modification
    //                to definitions is atomic as well. 
    
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
        synchronized (definitions) {
            assert definitions != null;
            for (Name name : definitions.keySet()) {
                ListExpression expression = definitions.get(name);
                Set<ListExpression> visited = new HashSet<>(Arrays.asList(name));
                Set<ListExpression> flattened = flatten(expression, new HashSet<>());
                if (flattened.contains(name)) flattened.remove(name);
                boolean hasLoop = findCycle(flattened, visited);
    
                assert !hasLoop : "Oops! You have created a mail loop.";
            }
        }
    }
    
    /**
     * Returns a flattened set of elements in the specified ListExpression (no element
     * in the returned set has children). 
     * @param e the expression to flatten
     * @param elements the set of elements to add to
     * @return the set of elements in e
     */
    private Set<ListExpression> flatten(ListExpression e, Set<ListExpression> elements) {
        if (e.getChildren().isEmpty()) {
            return new HashSet<>(Arrays.asList(e));
        }
        for (ListExpression c : e.getChildren()) {
            elements = flatten(c, elements);
        }
        return elements;
    }
    
    /**
     * Detects mail loops (mutually recursive definitions) in the current state of the environment.
     * A definition that includes its own name is not necessarily mutually recursive (e.g. a=a should
     * return false). 
     * @param toVisit the Set of ListExpressions to examine
     * @param visited the Set of ListExpressions that have already been examined
     * @return true if there is a mail loop; false otherwise
     */
    private boolean findCycle(Set<ListExpression> toVisit, Set<ListExpression> visited) {
        boolean cycle = false;
        for (ListExpression c : toVisit) {
            if (visited.contains(c)) {
                return true;
            } else {
                visited.add(c);
                Set<ListExpression> children = new HashSet<>(c.getChildren());
                for (ListExpression d : c.getDependents(this)) {
                    children.add(d);
                }
                cycle = cycle || findCycle(children, visited);
            }
        }
        return cycle;
    }
    
    /**
     * Get the expression corresponding to the specified list name
     * @param name the Name to get the definition of
     * @return the corresponding expression in definitions if one exists;
     *  returns empty expression otherwise (i.e., name has not been defined).
     */
    public synchronized ListExpression getExpression(Name name) {
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
    public synchronized ListExpression reassign(Name name, ListExpression expression) {
        ListExpression exp = getExpression(name);
        definitions.put(name, expression);
        checkRep();
        return exp;
    }

}
