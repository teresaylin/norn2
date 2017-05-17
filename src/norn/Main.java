package norn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import lib6005.parser.UnableToParseException;

/** 
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
 * test loading multiple files
 *      !load loadtest1 loadtest3 --> \n
 *      lunch --> brie@whole.wheat, crouton@tomato.bisque
 *      dinner --> roast@lamb.mint, german@chocolate.cake
 * test loading files with invalid list expressions --> print human readable error message
 *      !load loadtest4 --> [error message]
 *      
 * S A V E
 * test 0 list definitions previously entered
 *      !save savetests/savetest1 --> \n
 *      [savetest1 should be an empty file]
 * test 1 list definition previously entered
 *      dogs = corgi@dog.com, poodle@dog.com, wolf@dog.com --> corgi@dog.com, poodle@dog.com, wolf@dog.com
 *      !save savetests/savetest2 --> \n
 *      [savetest2 should contain only one line with text: dogs = corgi@dog.com, poodle@dog.com, wolf@dog.com]
 * test > 1 list definitions previously entered
 *      gryffindor = harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts --> harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts
 *      quidditch = harry@hogwarts, ron@hogwarts, draco@hogwarts, cedric@hogwarts --> harry@hogwarts, ron@hogwarts, draco@hogwarts, cedric@hogwarts
 *      sportsfans = gryffindor ! quidditch
 *      !save savetests/savetest3
 *      [savetest3 should contain 3 lines:
 *          gryffindor = harry@hogwarts, hermione@hogwarts, ron@hogwarts, neville@hogwarts
 *          quidditch = harry@hogwarts, ron@hogwarts, draco@hogwarts, cedric@hogwarts
 *          sportsfans = hermione@hogwarts, neville@hogwarts]
 * test saving to a file that cannot be opened for writing --> print human readable error message
 *      !save loadtests/savetest4 --> [error message; loadtests does not exist]
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

// Thread safety argument: TODO

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
        Environment environment = new Environment();
        WebServer server = new WebServer(environment);

        while (true) {
            System.out.print("> ");
            final String input = in.readLine();
            
            if (input.isEmpty()) {
                System.out.println(EMPTY_LIST);
                continue; // Gives nothing back to user and waits for further input.
            }
            final int prefixLength = "!load".length();
            try{
                // handle !load for one or more comma-separated file names
                if (input.startsWith(LOAD_COMMAND)) {
                    String[] fileNames = input.substring(prefixLength).replaceAll("\\s", "").split(",");
                    for(String fileName : fileNames){
                        File loadFile = new File(fileName);
                        if ( ! loadFile.isFile()){
                            System.out.println("here");
                            throw new IllegalArgumentException("file not found: \"" + loadFile + "\"");
                        }
                        load(loadFile, server.getEnvironment());
                    }
                    
                } else if (input.startsWith(SAVE_COMMAND)) {
                    // handle !save
                    save(input.substring(prefixLength).replaceAll("\\s", ""), environment);
                    
                } else {
                    // handle all list expressions
                    Set<Recipient> parsed = ListExpression.parse(input).recipients(environment); 
                    System.out.println(parsed.toString().replaceAll("[\\[\\]]", ""));
                }
            } catch(IllegalArgumentException e){
                System.out.println("expression unable to be parsed");
            }
        }
    }
   
    /**
     * Saves all definitions in this Environment to a file.
     * @param filename the name of the file to be written
     * @param env environment whose definitions are to be saved
     * @return true if definitions were successfully saved to fileName
     */
    private static boolean save(String filename, Environment env) {
        synchronized(env){
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                Set<Name> names = env.getNames();
                for (Name n : names) {
                    writer.write(n.toString() + " = (" + env.getExpression(n).toString() + ")"); 
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
    }
    
    /**
     * Attempts to read and parse a file containing a valid list expression.
     * Adds all definitions in the file to this Environment.
     * @param file the file to be loaded. Cannot contain newlines. 
     *  The contents of the file should be a single valid list expression. 
     * @return true if the contents of the file were successfully loaded,
     *  false otherwise.
     * @throws IOException 
     */
    private static boolean load(File file, Environment env) throws IOException {
        synchronized(env) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                String toParse = reader.readLine();
                ListExpression parsed = ListExpression.parse(toParse);
                parsed.recipients(env);
                return true;
            } catch (IOException e) {
                System.out.println("Invalid input, could not parse: " + e.getMessage());
                return false;
            }
            finally{
                reader.close();
            }
        }
    }
}
