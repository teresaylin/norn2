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
     * Saves all definitions in this Environment to a file.
     * @param filename The name of the file to be written
     * @param expression 
     * @return true if definitions were successfully saved to fileName
     */
    public boolean save(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (Name n : definitions.keySet()) {
                writer.write(n.toString() + " = (" + definitions.get(n).toString() + ")"); // TODO should this be recipients?
                writer.write("; ");
            }
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println("Could not open file to writer: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Attempts to read and parse a file containing a valid list expression.
     * Adds all definitions in the file to this Environment.
     * @param file the file to be loaded. Cannot contain newlines. 
     *  The contents of the file should be a single valid list expression. 
     * @return true if the contents of the file were successfully loaded,
     *  false otherwise.
     * @throws FileNotFoundException if file not found
     */
    public boolean load(File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String toParse = reader.readLine();
            ListExpression parsed = ListExpression.parse(toParse);
            parsed.recipients(this);
            checkRep();
            return true;
        } catch (IOException e) {
            System.out.println("Invalid input, could not parse: " + e.getMessage());
        }
        return false;
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
