package norn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lib6005.parser.UnableToParseException;

/**
 * TODO update to reflect Proj 2 specs
 * 
 * This is the interactive console for processing and analyzing email list expressions.
 * It supports user-inputted email list expressions, defined by the specification in the
 * Norn project handout. That is, list expressions are sets of recipients who should
 * receive email messages.
 *
 * Operators include , (union), ! (difference), * (intersection), = (definition), and
 * ; (sequencing). The order of operations is as follows in descending order:
 * '*', '!', ',', '=', and ';'. The user may nest subexpressions in parentheses.
 *
 * A list expression may define a list name: listname = e defines listname as the expression
 * e and returns the set of recipients of e. Any defined expressions may be assumed as stored
 * in memory, ready to use.
 *
 * Sequencing is also permitted: consider x = a@mit.edu,b@mit.edu ; x * b@mit.edu. After
 * substituting for x in the second part of the expression, this expression is equivalent
 * to (a@mit.edu,b@mit.edu) * b@mit.edu, which represents the single recipient b@mit.edu.
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
        }
    }
}
