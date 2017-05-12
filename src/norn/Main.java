package norn;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import lib6005.parser.UnableToParseException;

/**
 * TODO update to reflect Proj 2 specs
 * 
 * This is the interactive console for processing and analyzing email list expressions.
 * It supports user-inputted email list expressions, defined by the specification in the
 * Norn2 project handout. 
 * 
 * List expressions are sets of recipients who should receive email messages. A list 
 * expression may be an expression, a sequence of list expressions, a list name (set of 
 * recipients of a mailing list), an email address (single recipient), or an empty string.
 * 
 * Expressions use the following operators: 
 *      , (union)
 *      ! (difference)
 *      * (intersection)
 *      = (definition)
 *      ; (sequencing)
 * The order of operations in descending order is:
 * '*', '!', ',', '=', and ';'. The user may also nest subexpressions in parentheses.
 *
 * A list expression may define a list name: listname = e defines listname as the expression
 * e and returns the set of recipients of e. List names are nonempty case-insensitive strings 
 * of letters, digits, underscores, dashes, and periods. 
 *
 * Sequencing of list expressions or list definitions:
 * Ex: x = a@mit.edu,b@mit.edu ; x * b@mit.edu. After substituting for x in the second part 
 * of the expression, this expression is equivalent to (a@mit.edu,b@mit.edu) * b@mit.edu, 
 * which represents the single recipient b@mit.edu.
 *
 * The output of this console is an order-independent view of the emails specified in an email
 * list expression.
 * 
 * Lastly, the user can use the commands !save or !load to save currently defined named lists
 * to a file or load a saved file. Saved files contain a single valid list expression (a sequence
 * of list definitions). A user can specify multiple files in the same !load command but must
 * separate the files by a comma.
 * 
 * If a filename does not contain a valid list expression (during loading), or the file is 
 * unreadable (during saving), then 
 *
 * If given no input, this console proceeds to wait until valid input is given.
 */

/*
 * T E S T S
 * 
 * L O A D
 * test loading dependent list definitions
 *      !load loadtest1 --> \n
 *      lunch --> brie@whole.wheat, crouton@tomato.bisque
 *      soup --> crouton@tomato.bisque
 *      bread --> brie@whole.wheat
 * test loading list definitions dependent on previously entered lists
 *      dogs = corgi@dog.com, poodle@dog.com, wolf@dog.com --> corgi@dog.com, poodle@dog.com, wolf@dog.com
 *      pets = poodle@dog.com, bombay@cat.com, corgi@dog.com --> poodle@dog.com, bombay@cat.com, corgi@dog.com
 *      !load loadtest2 --> \n
 *      petdogs --> corgi@dog.com, poodle@dog.com
 *      
 * S A V E
 * test 0 list definitions previously entered
 *      !save savetest1 --> \n
 *      [savetest1 should be an empty file]
 * test 1 list definition previously entered
 *      dogs = corgi@dog.com, poodle@dog.com, wolf@dog.com --> corgi@dog.com, poodle@dog.com, wolf@dog.com
 *      !save savetest2 --> \n
 *      [savetest2 should contain only one line with text: dogs = corgi@dog.com, poodle@dog.com, wolf@dog.com]
 * test > 1 list definitions previously entered
 *      gryffindor = harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts --> harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts
 *      quidditch = harry@hogwarts, ron@hogwarts, draco@hogwarts, cedric@hogwarts --> harry@hogwarts, ron@hogwarts, draco@hogwarts, cedric@hogwarts
 *      sportsfans = gryffindor ! quidditch
 *      !save savetest3
 *      [savetest3 should contain 3 lines:
 *          gryffindor = harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts
 *          quidditch = harry@hogwarts, ron@hogwarts, draco@hogwarts, cedric@hogwarts
 *          sportsfans = hermione@hogwarts, neville@hogwarts]
 *          
 * U N D E F I N E D  L I S T S
 * test undefined name alone
 *      a --> {}
 * test undefined name in larger expression
 *      gryffindor = harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts --> harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts
 *      gryffindor ! slytherin --> harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts
 * 
 * E D I T E D  L I S T S
 * test when no definitions are dependent on the edited definition(s)
 *      p --> {}
 *      p = a@a --> a@a
 *      p --> a@a
 * test when edited list is dependent on itself
 *      cats = bombay@cat.com, tuxedo@cat.com --> bombay@cat.com, tuxedo@cat.com
 *      cats = cats * bombay@cat.com --> bombay@cat.com
 * test when some definitions are dependent on the edited definition(s)
 *      gryffindor = harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts --> harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts
 *      students = gryffindor ! prefects --> harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts
 *      prefects = hermione@hogwarts, ron@hogwarts --> hermione@hogwarts, ron@hogwarts
 *      students --> harry@hogwarts, neville@hogwarts
 * 
 * M A I L  L O O P S
 * test loop created by 2 mutually recursive list definitions
 *      a = a@a --> a@a
 *      b = a --> a@a
 *      a = b --> [error message]
 * test loops created by > 2 mutually recursive list definitions
 *      a = a@a --> a@a
 *      b = a --> a@a
 *      c = b --> a@a
 *      a = c --> [error message]
 */


public class Main {
    public static final String EMPTY_LIST = "{}";
    private static final String LOAD_COMMAND = "!load";
    private static final String SAVE_COMMAND = "!save";

    /**
     * Reads expressions as command inputs from the console and outputs results
     * as defined in the class specification.
     *
     * @param args unused
     * @throws IOException if there is an error in reading the input
     * @throws UnableToParseException
     */
    public static void main(String[] args) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("> ");
            final String input = in.readLine();
            // TODO create a common environment
            
            if (input.isEmpty()) {
                System.out.println(EMPTY_LIST);
                continue; // Gives nothing back to user and waits for further input.
            }
            try{
                // handle !load for one or more comma-separated file names
                if (input.startsWith(LOAD_COMMAND)) {
                    
                } else if (input.startsWith(SAVE_COMMAND)) {
                    // handle !save
                    
                } else {
                    Set<Recipient> parsed = ListExpression.parse(input); // TODO: call recipients
                    System.out.println(parsed.toString().replaceAll("[\\[\\]]", ""));
                }
            } catch(IllegalArgumentException e){
                System.out.println("expression unable to be parsed");
            }
        }
    }
    
    /**
     * Reads in a file and parses the file, if the file contains a valid list expression.
     * 
     * @param file The name of the file to be loaded. Cannot contain newlines. 
     * The contents of the file should be a single valid list expression. 
     * @return true if file was successfully read, false otherwise
     */
    private static boolean load(File file) {
        return true; // placeholder
    }
    
    /**
     * Saves all currently-defined named lists to a file.
     * @param fileName The name of the file to be written
     * @param expression 
     * @return true if expression was successfully saved to fileName
     */
    private static boolean save(String fileName, ListExpression expression) {
        return true; // placeholder
    }
}
