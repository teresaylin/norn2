package norn;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib6005.parser.ParseTree;
import lib6005.parser.Parser;
import lib6005.parser.UnableToParseException;

public class ListExpressionParser {
    
    // Expression grammar nonterminals
    private enum ListExpressionGrammar {
        ROOT, SEQUENCE, DEFINITION, UNION, DIFFERENCE, INTERSECTION, PRIMARY, ADDRESS, WHITESPACE, LISTNAME
    };

    private static final Map<String, Definition> environment = new HashMap<String, Definition>();
    private static Parser<ListExpressionGrammar> parser = makeParser();
    
    /**
     * Compile the grammar into a parser.
     * 
     * @param grammarFilename <b>Must be in this class's Java package.</b>
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<ListExpressionGrammar> makeParser() {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/norn/ListExpression.g");
            return Parser.compile(grammarFile, ListExpressionGrammar.ROOT);
            

        // Parser.compile() throws two checked exceptions.
        // Translate these checked exceptions into unchecked RuntimeExceptions,
        // because these failures indicate internal bugs rather than client errors
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }

    /**
     * Parse a string into a mailing list.
     * @param string string to parse
     * @return IntegerExpression parsed from the string
     * @throws UnableToParseException if the string doesn't match the ListExpression grammar
     */
    public static ListExpression parse(final String string) throws UnableToParseException {
        // parse the example into a parse tree
        String lowercased = string.toLowerCase();
        final ParseTree<ListExpressionGrammar> parseTree = parser.parse(lowercased);

        // make an AST from the parse tree
        final ListExpression expression = makeAbstractSyntaxTree(parseTree);
        return expression;
    }
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in ListExpression.g
     * @return abstract syntax tree corresponding to parseTree
     */
    private static ListExpression makeAbstractSyntaxTree(final ParseTree<ListExpressionGrammar> parseTree) {
        switch (parseTree.name()) {
        case ROOT: // root ::= sequence;
            {
                final ParseTree<ListExpressionGrammar> child = parseTree.children().get(0);
                return makeAbstractSyntaxTree(child);
            }
            
        case SEQUENCE: // sequence ::= definition (';' definition)*;
        {
            final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();
            ListExpression expression = makeAbstractSyntaxTree(children.get(0));
            for (int i = 1; i < children.size(); i++) {
                expression = new Sequence(expression, makeAbstractSyntaxTree(children.get(i)));
            }
            return expression;
        }
        case DEFINITION: // definition ::= (listname '=')? union;
            {
                final int ASSIGNMENT_NUMBER = 2;
                boolean isAssignment = parseTree.children().size() == ASSIGNMENT_NUMBER;
                if (isAssignment) {
                    Definition assignment = new Definition(parseTree.children().get(0).text(),
                            makeAbstractSyntaxTree(parseTree.children().get(1)));
                    environment.put(parseTree.children().get(0).text(), assignment);
                    return assignment;
                }
                return makeAbstractSyntaxTree(parseTree.children().get(parseTree.children().size() - 1));
            }
        
        case UNION: // union ::= difference (',' difference)*;
            {
                final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();
                ListExpression expression = makeAbstractSyntaxTree(children.get(0));
                for (int i = 1; i < children.size(); ++i) {
                    expression = new Union(expression, makeAbstractSyntaxTree(children.get(i)));
                }
                return expression;
            }
        case DIFFERENCE: // difference ::= intersection ('!' intersection)*;
            {
                final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();
                ListExpression expression = makeAbstractSyntaxTree(children.get(0));
                for (int i = 1; i < children.size(); ++i) {
                    expression = new Difference(expression, makeAbstractSyntaxTree(children.get(i)));
                }
                return expression;
            }
        case INTERSECTION: // intersection ::= primary ('*' primary)*;
        {
            final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();
            ListExpression expression = makeAbstractSyntaxTree(children.get(0));
            for (int i = 1; i < children.size(); ++i) {
                expression = new Intersect(expression, makeAbstractSyntaxTree(children.get(i)));
            }
            return expression;
        }
        case PRIMARY: // primary ::= listname | address | '(' union ')';
            {
                final ParseTree<ListExpressionGrammar> child = parseTree.children().get(0);
                return makeAbstractSyntaxTree(child);
            }
        case ADDRESS: // address ::= ([A-Za-z0-9_\-\.]+[@][A-Za-z0-9_\-\.]+)?;
            {
                final String address = parseTree.text();
                if(address.equals(""))
                    return new Empty();
                return new Recipient(address);
            }
        case LISTNAME: // listname ::= [A-Za-z0-9_\-\.]+;
        {
            String listNameText = parseTree.text();
            for (String i: environment.keySet()) {
                if (i.equals(listNameText)) {
                    return environment.get(i).getValue();
                }
            }
        }
        default:
            throw new AssertionError("should never get here");
        }

    }

}

