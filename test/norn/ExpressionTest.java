package norn;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for the ListExpression abstract data type
 */
public class ExpressionTest {
    /*
     * Testing strategy:
     *
     *  Variant classes:
     *      Union   
     *      Intersect
     *      Difference
     *      Definition
     *      Name
     *      Sequence
     *      Empty
     *      Recipient
     *
     *  recipients(environment)/environment constructor/environment.reassign():
     *      each concrete variant class
     *      letters/numbers/special characters
     *      number of recipients: 0, 1, >1
     *      duplicate recipients
     *      includes empty set
     *      empty value in Definition
     *
     *      number of Names in environment keyset: 0, 1, >1
     *      nesting in environment
     *      reassign: new key/replace key
     *      
     *      number of Names to evaluate: 0, 1, >1
     *      nesting in list expression
     *      fully evaluates/list name lookup fails
     *      
     *      add new definition
     *      reassign list name with/without recursion
     *      
     *      definition within another definition
     *      listname defined with itself
     *      sequence within another variant
     *  
     *  getChildren():
     *      duplicate children
     *      nesting in expression
     *      each concrete variant class
     *      
     *  getDependents(environment):
     *      Name:
     *          Name obj is not in environment
     *          Name obj is in environment
     *      all other variant classes
     *      
     *  toString():
     *      case - lower, upper
     *      each concrete variant class
     *
     *  equals():
     *      each concrete variant class
     *      structural equality
     *
     *  hashCode():
     *      each concrete variant class
     *      
     *  
     */
    
    // Recipients
    private final static Recipient AB = new Recipient("a@b");
    private final static Recipient CD = new Recipient("c@d");
    private final static Recipient SPECIAL = new Recipient("-_@b");
    
    // ListExpressions (immutable)
    
    // (AB, SPECIAL) * a
    private final static Intersect ONE_EVAL = new Intersect(new Union(AB, SPECIAL), new Name("a")); 
    
    // ((AB, SPECIAL), a) ! b
    private final static Difference TWO_EVAL = new Difference(new Union(new Union(AB, SPECIAL), new Name("a")), new Name("b")); 
    
    // c = AB, CD; c
    private final static Sequence SEQ_EVAL = new Sequence(new Definition(new Name("c"), new Union(AB, CD)), new Name("c")); 
    
    // a = (b = SPECIAL)
    private final static Definition ASSIGN_EVAL = new Definition(new Name("a"), new Definition(new Name("b"), SPECIAL)); 
    
    // (AB; CD), SPECIAL
    private final static Union SEQ_WITHIN = new Union(new Sequence(AB, CD), SPECIAL); 
    

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
///////////////////////////////////////////////////////////////////////////////////////////////////
    // Testing recipients...

    
    // Intersect/Union/Name
    // reassign(): new definition, replace definition
    // 1 Name lookup
    // 1 Name in environment
    // No recursion in environment
    // Recursion in expression
    @Test   
    public void testRecipientReassignOneLookUp() { 
        final Environment oneEnvironment = new Environment();
        assertEquals("Expected previous assignment", new Empty(),
                oneEnvironment.reassign(new Name("a"), new Union(AB, CD))); 
        assertEquals("Expected previous assignment", new Union(AB, CD),
                oneEnvironment.reassign(new Name("a"), new Intersect(new Union(AB, CD), AB))); 
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("a@b"));
        assertEquals("Expected correct recipients", aSet, ONE_EVAL.recipients(oneEnvironment));
    }
    
    // Difference/Union
    // 2 Name lookup
    // 2 Names in environment
    // Recursion in environment
    // Recursion in expression
    @Test  
    public void testRecipientTwoLookUp() { 
        final Environment twoEnvironment = new Environment();
        twoEnvironment.reassign(new Name("a"), new Union(AB, CD)); // a: a@b, c@d
        twoEnvironment.reassign(new Name("b"), new Difference(new Name("a"), CD)); // b: a ! c@d
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("c@d"));
        aSet.add(new Recipient("-_@b"));
        assertEquals("Expected correct recipients", aSet, TWO_EVAL.recipients(twoEnvironment));
    }
    
    // 1 Name lookup
    // no Names in environment
    // undefined Name
    @Test   
    public void testRecipientMissingDefinition() { 
        final Environment emptyEnvironment = new Environment();
        Set<ListExpression> aSet = new HashSet<>();
        assertEquals("Expected correct recipients", aSet, ONE_EVAL.recipients(emptyEnvironment));
    }
    
    // Sequence; Definition in Sequence
    // add new definition during evaluation
    @Test   
    public void testRecipientAddNewDefinition() { 
        final Environment emptyEnvironment = new Environment();
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("a@b"));
        aSet.add(new Recipient("c@d"));
        assertEquals("Expected correct recipients", aSet, SEQ_EVAL.recipients(emptyEnvironment));
    }
    
    // replace definition during evaluation
    // nested definition update
    @Test   
    public void testRecipientReplaceDefinition() { 
        final ListExpression testExpr = new Name("b");
        
        final Environment twoEnvironment = new Environment();
        twoEnvironment.reassign(new Name("a"), new Union(AB, CD)); // a: a@b, c@d
        twoEnvironment.reassign(new Name("b"), new Difference(new Name("a"), CD)); // b: a ! c@d
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("a@b"));
        assertEquals("Expected correct recipients before reassignment", aSet, testExpr.recipients(twoEnvironment));
        
        // Reassignment
        twoEnvironment.reassign(new Name("a"), new Union(SPECIAL, CD)); // a: a@b, c@d
        Set<ListExpression> bSet = new HashSet<>();
        bSet.add(new Recipient("-_@b"));
        assertEquals("Expected correct recipients after reassignment", bSet, testExpr.recipients(twoEnvironment));
    }
    
    // listname defined with itself
    @Test   
    public void testRecipientValidCircularDefinition() { 
        final ListExpression testExpr = new Definition(new Name("a"), new Union(new Name("a"), CD));
        // a = a, c@d
        
        final Environment oneEnvironment = new Environment();
        oneEnvironment.reassign(new Name("a"), AB); 
        // a = a@b
        
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("a@b"));
        aSet.add(new Recipient("c@d"));
        
        assertEquals("Expected correct recipients", aSet, testExpr.recipients(oneEnvironment));
    }

    // Definition within another Definition
    @Test   
    public void testRecipientNestedDefinition() { 
        final Environment emptyEnvironment = new Environment();
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("-_@b"));
        assertEquals("Expected correct recipients", aSet, ASSIGN_EVAL.recipients(emptyEnvironment));
    }
    
    // Sequence within Union
    @Test   
    public void testRecipientSequenceInUnion() { 
        final Environment emptyEnvironment = new Environment();
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("c@d"));
        aSet.add(new Recipient("-_@b"));
        assertEquals("Expected correct recipients", aSet, SEQ_WITHIN.recipients(emptyEnvironment));
    }
    
///////////////////////////////////////////////////////////////////////////////////////////////////

    // 0 recipients
    @Test 
    public void testRecipientEmptyList() {
        final Environment emptyEnvironment = new Environment();
        String input = ""; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> emptySet = new HashSet<>();
        assertTrue("Expected empty recipients set", parsed.recipients(emptyEnvironment).equals(emptySet));
    }
    
    // 1 recipient
    // letters
    @Test 
    public void testRecipientSingleRecipient() {
        final Environment emptyEnvironment = new Environment();
        String input = "joe@shmoe.com";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> oneElementSet = new HashSet<>();
        oneElementSet.add(new Recipient("joe@shmoe.com"));
        assertTrue("Expected one element in recipients set", parsed.recipients(emptyEnvironment).equals(oneElementSet));
    }
    
    // Union
    // >1 recipient
    @Test 
    public void testRecipientMultipleRecipients() {
        final Environment emptyEnvironment = new Environment();
        String input = "joe@shmoe.com, average@joe.com, gi@joe";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("joe@shmoe.com"));
        multiElementSet.add(new Recipient("average@joe.com"));
        multiElementSet.add(new Recipient("gi@joe"));
        assertTrue("Expected many element in recipients set", parsed.recipients(emptyEnvironment).equals(multiElementSet));    
    }
    
    // Union
    // Duplicates
    @Test
    public void testRecipientDuplicateRecipients() {
        final Environment emptyEnvironment = new Environment();
        String input = "joe@shmoe.com, average@joe.com, gi@joe, average@joe.com";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("joe@shmoe.com"));
        multiElementSet.add(new Recipient("average@joe.com"));
        multiElementSet.add(new Recipient("gi@joe"));
        assertTrue("Expected no duplicates in recipients set", parsed.recipients(emptyEnvironment).equals(multiElementSet));    
    }
    
    // Union
    // Equivalent recipients
    @Test 
    public void testRecipientMultipleEquivalentRecipients() {
        final Environment emptyEnvironment = new Environment();
        String input = "JAMUN@blah, jamun@BLAH"; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("jamun@blah"));
        assertTrue("Expected no duplicates in recipients set", parsed.recipients(emptyEnvironment).equals(multiElementSet));    
    }
    
    // Definition
    // 1 recipient
    @Test
    public void testRecipientSingleDefinition() {
        final Environment emptyEnvironment = new Environment();
        String input = "Hogwarts = harry@potter";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("harry@potter"));
        assertTrue("Expected one recipient in Hogwarts definition", parsed.recipients(emptyEnvironment).equals(multiElementSet));
    }
    
    // Definition
    // >1 recipient
    @Test
    public void testRecipientManyInOneDefinition() {
        final Environment emptyEnvironment = new Environment();
        String input = "Hogwarts = harry@potter, hermione@granger, ron@weasley, albus@dumbledore";
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> multiElementSet = new HashSet<>();
        multiElementSet.add(new Recipient("harry@potter"));
        multiElementSet.add(new Recipient("hermione@granger"));
        multiElementSet.add(new Recipient("ron@weasley"));
        multiElementSet.add(new Recipient("albus@dumbledore"));
        assertTrue("Expected many recipients in Hogwarts definition", parsed.recipients(emptyEnvironment).equals(multiElementSet));
    }
    
    // Union
    // Includes empty
    // numbers/special characters
    @Test
    public void testRecipientWithEmptyUnion() { 
        final Environment emptyEnvironment = new Environment();
        String input = "he1lo@here,, t-._re@there"; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("he1lo@here"));
        aSet.add(new Recipient("t-._re@there"));
        assertTrue("Expected union of two emails and empty set", parsed.recipients(emptyEnvironment).equals(aSet));
    }
    
    // Difference
    // Empty
    @Test 
    public void testRecipientWithEmptyDifference() { 
        final Environment emptyEnvironment = new Environment();
        String input = "hello@here! "; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> aSet = new HashSet<>();
        aSet.add(new Recipient("hello@here"));
        assertTrue("Expected difference of email and empty set", parsed.recipients(emptyEnvironment).equals(aSet));
    }
    
    // Intersection
    // Empty
    @Test  
    public void testRecipientWithEmptyIntersection() { 
        final Environment emptyEnvironment = new Environment();
        String input = "(hello@here,there@there)*"; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> aSet = new HashSet<>();
        assertTrue("Expected intersection of empty set and email list", parsed.recipients(emptyEnvironment).equals(aSet));
    }
    
    // Definition
    // Empty value
    @Test 
    public void testRecipientWithEmptyDefinition() { 
        final Environment emptyEnvironment = new Environment();
        String input = "a = "; 
        ListExpression parsed = ListExpression.parse(input);
        Set<ListExpression> aSet = new HashSet<>();
        assertTrue("Expected empty set", parsed.recipients(emptyEnvironment).equals(aSet));
    }
    
    
///////////////////////////////////////////////////////////////////////////////////////////////////
    // getChildren()
    
    // Union/Intersect
    // Nesting in expression
    @Test
    public void testChildrenIntersect() {
        Set<ListExpression> children = new HashSet<>(Arrays.asList(new Union(AB, SPECIAL), new Name("a")));
        assertEquals("expected correct children of intersect/union", children, ONE_EVAL.getChildren());
    }
    
    // Union/Difference
    // Nesting in expression
    @Test
    public void testChildrenDifference() {
        Set<ListExpression> children = new HashSet<>(Arrays.asList(new Union(new Union(AB, SPECIAL), new Name("a")), new Name("b")));
        assertEquals("expected correct children of difference/union", children, TWO_EVAL.getChildren());
    }

    
///////////////////////////////////////////////////////////////////////////////////////////////////
    // getDependents(environment)

    // Empty
    @Test
    public void testDependentsEmpty() {
        Environment env = new Environment();
        assertEquals("expected empty set of dependents of Empty", Collections.emptySet(), new Empty().getDependents(env));
    }
    
    // Recipient
    @Test
    public void testDependentsRecipient() {
        Environment env = new Environment();
        assertEquals("expected empty set of dependents of Recipient", Collections.emptySet(), AB.getDependents(env));
    }
    
    // Definition
    @Test
    public void testDependentsDefinition() {
        Environment env = new Environment();
        assertEquals("expected empty set of dependents of Definition", Collections.emptySet(), ASSIGN_EVAL.getDependents(env));
    }
    
    // Difference
    @Test
    public void testDependentsDifference() {
        Environment env = new Environment();
        assertEquals("expected empty set of dependents of Difference", Collections.emptySet(), TWO_EVAL.getDependents(env));
    }
    
    // Union
    @Test
    public void testDependentsUnion() {
        Environment env = new Environment();
        assertEquals("expected empty set of dependents of Difference", Collections.emptySet(), SEQ_WITHIN.getDependents(env));
    }
    
    // Intersect
    @Test
    public void testDependentsIntersect() {
        Environment env = new Environment();
        assertEquals("expected empty set of dependents of Difference", Collections.emptySet(), ONE_EVAL.getDependents(env));
    }
    
    // Sequence
    @Test
    public void testDependentsSequence() {
        Environment env = new Environment();
        assertEquals("expected empty set of dependents of Difference", Collections.emptySet(), SEQ_EVAL.getDependents(env));
    }
    
    // Name, where the Name obj is not in environment
    @Test
    public void testDependentsNameNotInEnv() {
        Environment env = new Environment();
        assertEquals("expected empty set of dependents of Difference", Collections.emptySet(), new Name("a").getDependents(env));
    }
    
    // Name, where the Name obj is in the environment
    @Test
    public void testDependentsNameInEnv() {
        Environment env = new Environment();
        env.reassign(new Name("a"), new Name("b"));
        assertEquals("expected empty set of dependents of Difference", new HashSet<ListExpression>(Arrays.asList(new Name("b"))), new Name("a").getDependents(env));
    }
    
    
///////////////////////////////////////////////////////////////////////////////////////////////////
    // toString()
    // Empty
    @Test
    public void testToStringEmpty() {
        ListExpression e = ListExpression.parse("");
        assertEquals("Empty input returns ''", "", e.toString());
    }
    
    // Definition with Name
    // 2 levels
    @Test
    public void testToStringDefinition() {
        final ListExpression e = ListExpression.parse("a = b@mit.edu, c");
        assertEquals("expected correct toString()", "a = (b@mit.edu, c)", e.toString());
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
    // Empty
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
    public void testToStringIntersection() {
        ListExpression e = ListExpression.parse("(a@b, b@c) * c@d");
        assertEquals("'(a@b, b@c) * c@d' returns ((a@b, b@c) * c@d)", "((a@b, b@c) * c@d)", e.toString());
    }
    
    // Name
    @Test
    public void testToStringName() {
        ListExpression e = ListExpression.parse("nAme1_-.");
        assertEquals("expected correct toString()", "name1_-.", e.toString());
    }
    
    // Sequence
    @Test
    public void testToStringSequence() {
        ListExpression e = ListExpression.parse("nAme1_-.; a = 5");
        assertEquals("expected correct toString()", "name1_-.; a = 5", e.toString());
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
    
    // Sequence
    @Test
    public void testEqualsTwoSequences() {
        final ListExpression e1 = ListExpression.parse("B@mit.edu; a@mit.edu");
        final ListExpression e2 = ListExpression.parse("b@mit.edu; A@mit.edu");
        assertEquals("sequences should be equal", e1, e2);
    }
    
    // Name
    @Test
    public void testEqualsTwoNames() {
        final ListExpression e1 = ListExpression.parse("B");
        final ListExpression e2 = ListExpression.parse("b");
        assertEquals("names should be equal", e1, e2);
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
    
    // Intersect, Sequence, Name
    @Test
    public void testHashCodeIntersection() {
        ListExpression e1 = ListExpression.parse("(a@mit.edu, b@mit.edu) * b@mit.edu; b");
        ListExpression e2 = ListExpression.parse("(A@mit.edu, B@mit.edu) * b@mit.edu; B");
        assertEquals("Same hashcode for intersection of email lists", e2.hashCode(), e1.hashCode());
    }
}
