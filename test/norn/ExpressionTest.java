package norn;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for the Expression abstract data type
 */
public class ExpressionTest {
    /*
     * Testing strategy:
     *
     *  recipients():
     *      0; 1; > 1 recipients
     *      Constraints: Union, Difference, and Intersection >=1 operand being the empty set
     *                   Definition where ListExpression is Empty
     *
     *  toString():
     *      case - lower, upper
     *      Empty
     *      Union
     *      Difference
     *      Intersection
     *      Recipient
     *      Definition
     *
     *  equals():
     *      Empty
     *      Union
     *      Difference
     *      Intersection
     *      Recipient
     *      Definition
     *      structural equality
     *
     *  hashCode():
     *      Empty
     *      Union
     *      Difference
     *      Intersection
     *      Recipient
     *      Definition
     */

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // recipients()
    @Test //Tests 0 recipients
    public void testRecipientEmptyList() {
        String input = ""; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> emptySet = new HashSet<>();
        assertTrue("Expected empty recipients set", parsed.recipients().equals(emptySet));
    }
    @Test //Tests 1 recipient
    public void testRecipientSingleRecipient() {
        String input = "joe@shmoe.com";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> oneElementSet = new HashSet<>();
        oneElementSet.add(new Recipient("joe@shmoe.com"));
        assertTrue("Expected one element in recipients set", parsed.recipients().equals(oneElementSet));
    }
    @Test //Tests >1 recipients
    public void testRecipientMultipleRecipients() {
        String input = "joe@shmoe.com, average@joe.com, gi@joe";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("joe@shmoe.com"));
        multiElementSet.add(new Recipient("average@joe.com"));
        multiElementSet.add(new Recipient("gi@joe"));
        assertTrue("Expected many element in recipients set", parsed.recipients().equals(multiElementSet));    
    }
    @Test //Tests duplicate recipients
    public void testRecipientDuplicateRecipients() {
        String input = "joe@shmoe.com, average@joe.com, gi@joe, average@joe.com";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("joe@shmoe.com"));
        multiElementSet.add(new Recipient("average@joe.com"));
        multiElementSet.add(new Recipient("gi@joe"));
        assertTrue("Expected no duplicates in recipients set", parsed.recipients().equals(multiElementSet));    
    }
    
    @Test //Tests multiple equivalent recipients
    public void testRecipientMultipleEquivalentRecipients() {
        String input = "JAMUN@blah, jamun@BLAH"; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("jamun@blah"));
        assertTrue("Expected no duplicates in recipients set", parsed.recipients().equals(multiElementSet));    
    }
    
    @Test//Tests a single definition recipient
    public void testRecipientSingleDefinition() {
        String input = "Hogwarts = harry@potter";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("harry@potter"));
        assertTrue("Expected one recipient in Hogwarts definition", parsed.recipients().equals(multiElementSet));
    }
    
    @Test//Tests a single definition recipient
    public void testRecipientManyInOneDefinition() {
        String input = "Hogwarts = harry@potter, hermione@granger, ron@weasley, albus@dumbledore";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("harry@potter"));
        multiElementSet.add(new Recipient("hermione@granger"));
        multiElementSet.add(new Recipient("ron@weasley"));
        multiElementSet.add(new Recipient("albus@dumbledore"));
        assertTrue("Expected many recipients in Hogwarts definition", parsed.recipients().equals(multiElementSet));
    }
    @Test //Tests recipients of a union 
    public void testRecipientWithEmptyUnion() { 
        String input = "hello@here,, there@there"; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("hello@here"));
        aSet.add(new Recipient("there@there"));
        assertTrue("Expected union of two emails and empty set", parsed.recipients().equals(aSet));
    }
    
    @Test //Tests recipients of a difference 
    public void testRecipientWithEmptyDifference() { 
        String input = "hello@here! "; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("hello@here"));
        assertTrue("Expected difference of email and empty set", parsed.recipients().equals(aSet));
    }
    
    @Test //Tests recipients of an intersection 
    public void testRecipientWithEmptyIntersection() { 
        String input = "(hello@here,there@there)*"; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> aSet = new HashSet<>();
        assertTrue("Expected intersection of empty set and email list", parsed.recipients().equals(aSet));
    }
    
    @Test //Tests recipients of a definition 
    public void testRecipientWithEmptyDefinition() { 
        String input = "a = "; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> aSet = new HashSet<>();
        assertTrue("Expected empty set", parsed.recipients().equals(aSet));
    }
    
///////////////////////////////////////////////////////////////////////////////////////////////////
    // toString()
    // Empty
    @Test
    public void testToStringEmpty() {
        ListExpression e = ListExpression.parse("");
        assertEquals("Empty input returns ''", "", e.toString());
    }
    
    // Definition
    @Test
    public void testToStringDefinition() {
        final ListExpression e = ListExpression.parse("a = b@mit.edu");
        System.out.println(e instanceof Definition);
        assertEquals("a = b@mit.edu returns b@mit.edu", "a = b@mit.edu", e.toString());
    }
    
    // Recipient, uppercase
    @Test
    public void testToStringRecipient() {
        final ListExpression e = ListExpression.parse("B@mit.edu");
        assertEquals("B@mit.edu returns b@mit.edu", "b@mit.edu", e.toString());
    }
    

    // Union, upper and lowercase
    @Test
    public void testToStringUnion() {
        final ListExpression e = ListExpression.parse("B@mit.edu, a@mit.edu");
        assertEquals("B@mit.edu returns b@mit.edu", "(b@mit.edu, a@mit.edu)", e.toString());
    }
    
    // Union of two empty lists
    @Test
    public void testToStringUnionEmpty() {
        final ListExpression e = ListExpression.parse(",");
        assertEquals("',' returns ''", "(, )", e.toString());
    }
    
    // Difference
    @Test
    public void testToStringDifferenceSimple() {
        ListExpression e = ListExpression.parse("(a@b, b@c)!b@c");
        assertEquals("'(a@b, b@c)!b@c' returns ((a@b, b@c) ! b@c)", "((a@b, b@c) ! b@c)", e.toString());
    }
    
    // Intersection
    @Test
    public void testToStringIntersectionEmpty() {
        ListExpression e = ListExpression.parse("(a@b, b@c) * c@d");
        assertEquals("'(a@b, b@c) * c@d' returns ((a@b, b@c) * c@d)", "((a@b, b@c) * c@d)", e.toString());
    }
///////////////////////////////////////////////////////////////////////////////////////////////////
    // equals()
    // Definition
    @Test
    public void testEqualsTwoDefinitions() {
        final ListExpression e1 = ListExpression.parse("a = B@mit.edu");
        final ListExpression e2 = ListExpression.parse("A = b@mit.edu");
        assertEquals("a = B@mit.edu equals A = b@mit.edu", e1, e2);
    }
    
    // Recipient
    @Test
    public void testEqualsTwoRecipients() {
        final ListExpression e1 = ListExpression.parse("B@mit.edu");
        final ListExpression e2 = ListExpression.parse("b@mit.edu");
        assertEquals("B@mit.edu equals b@mit.edu", e1, e2);
    }

    // Union
    @Test
    public void testEqualsUnion() {
        final ListExpression e1 = ListExpression.parse("B@mit.edu, a@mit.edu");
        final ListExpression e2 = ListExpression.parse("b@mit.edu, A@mit.edu");
        assertEquals("B@mit.edu, a@mit.edu equals b@mit.edu, A@mit.edu", e1, e2);
    }
    
    // Empty
    @Test
    public void testEqualsEmpty() {
        ListExpression e1 = ListExpression.parse("");
        ListExpression e2 = ListExpression.parse("           ");
        assertEquals("Two empty email lists should be equal", e1, e2);
    }
    
    // Difference
    @Test
    public void testEqualsDifference() {
        ListExpression e1 = ListExpression.parse("(a@mit.edu, b@mit.edu) ! b@mit.edu");
        ListExpression e2 = ListExpression.parse("(A@mit.edu, B@mit.edu) ! b@mit.edu");
        assertEquals("(a@mit.edu, b@mit.edu) ! b@mit.edu equals (A@mit.edu, B@mit.edu) ! b@mit.edu", e1, e2);
    }
    
    // Intersection
    @Test
    public void testEqualsIntersection(){
        ListExpression e1 = ListExpression.parse("(a@mit.edu, b@mit.edu) * b@mit.edu");
        ListExpression e2 = ListExpression.parse("(A@mit.edu, B@mit.edu) * b@mit.edu");
        assertEquals("(a@mit.edu, b@mit.edu) * b@mit.edu equals (A@mit.edu, B@mit.edu) * b@mit.edu", e1, e2);
    }
    
    // Structural
    @Test
    public void testEqualsStructuralEquality() {
        String firstInput = "sarah@gmail, john@gmail, bob@gmail";
        String secondInput = "sarah@gmail, (john@gmail, bob@gmail)";
        ListExpression firstParsed = ListExpression.parse(firstInput);
        ListExpression secondParsed = ListExpression.parse(secondInput);
        assertFalse("These expressions are not structurally equal", firstParsed.equals(secondParsed));
    }
///////////////////////////////////////////////////////////////////////////////////////////////////
    // hashCode()
    // Definition
    @Test
    public void testHashCodeDefinition() {
        final ListExpression e1 = ListExpression.parse("a = B@mit.edu");
        final ListExpression e2 = ListExpression.parse("A = b@mit.edu");
        assertEquals("Two same list definitions should have equal hashcodes", e2.hashCode(), e1.hashCode());
    }
    
    // Recipient
    @Test
    public void testHashCodeRecipient() {
        final ListExpression e1 = ListExpression.parse("B@mit.edu");
        final ListExpression e2 = ListExpression.parse("b@mit.edu");
        assertEquals("Two same recipients should have equal hashcodes", e2.hashCode(), e1.hashCode());
    }

    // Union
    @Test
    public void testHashCodeUnion() {
        final ListExpression e1 = ListExpression.parse("B@mit.edu, a@mit.edu");
        final ListExpression e2 = ListExpression.parse("b@mit.edu, A@mit.edu");
        assertEquals("B@mit.edu, a@mit.edu and b@mit.edu, A@mit.edu have same hashcode", e2.hashCode(), e1.hashCode());
    }
    
    // Empty
    @Test
    public void testHashCodeEmpty() {
        ListExpression e1 = ListExpression.parse("");
        ListExpression e2 = ListExpression.parse("           ");
        assertEquals("Two empty email lists should have equal hashcodes", e2.hashCode(), e1.hashCode());
    }
    
    // Difference
    @Test
    public void testHashCodeDifference() {
        ListExpression e1 = ListExpression.parse("(a@mit.edu, b@mit.edu) ! b@mit.edu");
        ListExpression e2 = ListExpression.parse("(A@mit.edu, B@mit.edu) ! b@mit.edu");
        assertEquals("Difference of email lists should have same hashcode", e2.hashCode(), e1.hashCode());
    }
    
    // Intersection
    @Test
    public void testHashCodeIntersection() {
        ListExpression e1 = ListExpression.parse("(a@mit.edu, b@mit.edu) * b@mit.edu");
        ListExpression e2 = ListExpression.parse("(A@mit.edu, B@mit.edu) * b@mit.edu");
        assertEquals("Same hashcode for intersection of email lists", e2.hashCode(), e1.hashCode());
    }
}
