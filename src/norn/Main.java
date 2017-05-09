package norn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
 * If given no input, this console proceeds to wait until valid input is given.
 */
public class Main {
    public static final String EMPTY_LIST = "{}";

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
            
            if (input.isEmpty()) {
                System.out.println(EMPTY_LIST);
                continue; // Gives nothing back to user and waits for further input.
            }
            try{
                ListExpression parsed = ListExpression.parse(input);
                Set<Recipient> printout = parsed.recipients();
                System.out.println(printout.toString().replaceAll("[\\[\\]]", ""));
            } catch(IllegalArgumentException e){
                System.out.println("expression unable to be parsed");
            }
        }
    }
}
