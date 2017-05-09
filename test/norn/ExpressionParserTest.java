package norn;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;


/**
 * Tests for the ListExpressionParser class.
 * Verifies integrity of parseable email
 * list ListExpressions and their outputs.
 */
public class ExpressionParserTest {

    /*
     * T E S T I N G  S T R A T E G Y :
     *
     *  parse():
     *      valid inputs:
     *          empty input
     *          case: lowercase, uppercase
     *          emails: 1, 2, >2
     *          emails: same, different
     *          list definition
     *          union
     *          difference
     *          intersection
     *          sequencing
     *      invalid inputs:
     *          no comma separation
     *          email is invalid
     *      types of inputs:
     *          email
     *          union of emails
     *          difference of emails
     *          intersection of emails
     */

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // T E S T I N G  p a r s e ( )

    private static final Set<String> emptySet = Collections.emptySet();
    
    // empty input, as defined by spec of Main.java
    @Test
    public void testParserEmptyInput() {
        final ListExpression e = ListExpression.parse("");
        assertEquals(true, e.recipients().equals(Collections.emptySet()));
    }

    // uppercase, 1 email, email input
    @Test
    public void testParserSingleEmail() {
        final ListExpression e = ListExpression.parse("BEN@mit.edu");
        assertEquals("BEN@mit.edu becomes ben@mit.edu", "ben@mit.edu", e.toString());
    }
    
    // union of 1 email and empty
    @Test
    public void testParserUnionWithEmpty() {
        final ListExpression e = ListExpression.parse("ben@mit.edu,");
        assertEquals("ben@mit.edu, becomes ben@mit.edu", "ben@mit.edu", e.toString());
    }

    // lowercase and uppercase, 2 emails same, union of emails
    @Test
    public void testParserTwoEmailsSame() {
        final ListExpression e = ListExpression.parse("BEN@mit.edu, ben@mit.edu");
        assertEquals("BEN@mit.edu, ben@mit.edu returns 1 email", "ben@mit.edu", e.toString());
    }

    // lowercase, 2 emails same, union of emails
    @Test
    public void testParserTwoEmailsDiff() {
        final ListExpression e = ListExpression.parse("ben@mit.edu, alice@mit.edu");
        assertTrue("ben@mit.edu, alice@mit.edu returns both emails",
                   e.recipients().contains("ben@mit.edu") && e.recipients().contains("alice@mit.edu"));
    }

    // >2 emails, union of emails
    @Test
    public void testParserMultipleEmails() {
        final ListExpression e = ListExpression.parse("ben@mit.edu, alice@mit.edu, TIm@yahoo.com");
        assertTrue("ben@mit.edu, alice@mit.edu, TIm@yahoo.com", e.recipients().contains("ben@mit.edu") &&
                   e.recipients().contains("alice@mit.edu") && e.recipients().contains("tim@yahoo.com"));
    }

    // >2 emails, union with parentheses
    @Test
    public void testParserUnionParentheses() {
        final ListExpression e = ListExpression.parse("(ben@mit.edu, alice@mit.edu), (TIm@yahoo.com)");
        assertTrue("ben@mit.edu, alice@mit.edu, TIm@yahoo.com", e.recipients().contains("ben@mit.edu") &&
                   e.recipients().contains("alice@mit.edu") && e.recipients().contains("tim@yahoo.com"));
    }

    // 2 emails, difference results in empty set
    @Test
    public void testParserDifferenceEmptyOutput() {
        final ListExpression e = ListExpression.parse("a@mit ! a@mit");
        assertEquals(true, e.recipients().equals(emptySet));
    }

    // 2 emails, difference is set of one email
    @Test
    public void testParserDifferenceOutputHasOneEmail() {
        final ListExpression e = ListExpression.parse("(a@mit,b@c) ! a@mit");
        assertEquals(true, e.recipients().contains("b@c"));
    }

    // empty set, difference
    @Test
    public void testParserDifferenceEmptyBase() {
        final ListExpression e = ListExpression.parse(" ! a@mit");
        assertEquals(true, e.recipients().equals(emptySet));
    }

    // difference, set - the empty set
    @Test
    public void testParserDifferenceSubtractEmptyFromBase() {
        final ListExpression e = ListExpression.parse("a@mit !");
        assertEquals(true, e.recipients().size() == 1);
        assertEquals(true, e.recipients().contains("a@mit"));
    }

    // 2 emails, intersection results in empty set
    @Test
    public void testParserIntersectionEmptyOutput() {
        final ListExpression e = ListExpression.parse("a@mit * b@mit");
        assertEquals(true, e.recipients().equals(emptySet));
    }

    // > 2 emails, intersection
    @Test
    public void testParserIntersectionMultipleEmails() {
        final ListExpression e = ListExpression.parse("(a@b,b@c,c@d) * (b@c,c@d)");
        assertEquals(true,e.recipients().contains("b@c"));
        assertEquals(true,e.recipients().contains("c@d"));
    }

    // > 2 emails, intersection of a list ListExpression with itself
    @Test
    public void testParserIntersectionItself() {
        final ListExpression e = ListExpression.parse("(a@b,b@c,c@d) * (a@b,b@c,c@d)");
        assertEquals(true,e.recipients().contains("a@b"));
        assertEquals(true,e.recipients().contains("b@c"));
        assertEquals(true,e.recipients().contains("c@d"));
    }

    // empty set, intersection
    @Test
    public void testParserIntersectionEmptyBase() {
        final ListExpression e = ListExpression.parse("* ");
        assertEquals(true,e.recipients().equals(emptySet));
    }
    
    // test replacing listname
    @Test
    public void testParserReplaceListname(){
        final String listname = "list";
        final ListExpression e = new Union(ListExpression.parse("a@b.c"), ListExpression.parse("b@b.c"));
        final ListExpression parsed = ListExpression.parse(listname);
        assertEquals("Listname not correctly replaced", e, parsed);
    }
    
    // INVALID INPUTS

    // invalid: no comma separating emails
    @Test(expected = IllegalArgumentException.class)
    public void testParserNoCommaSeparation() {
        ListExpression.parse("ben@mit.edualice@mit.edu");
    }

    // invalid: email does not contain domain
    @Test(expected = IllegalArgumentException.class)
    public void testParserEmailNoDomain() {
        ListExpression.parse("ben@");
    }

    // invalid: email does not contain username
    @Test(expected = IllegalArgumentException.class)
    public void testParserEmailNoUsername() {
        ListExpression.parse("@mit.edu");
    }

}
