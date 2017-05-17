package norn;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for the ListExpression abstract data type
 */
public class EnvironmentTest {
    /*
     * Testing strategy:
     *
     *  constructor, reassign()
     *      tested with the below methods and their partitions
     *
     *  getExpression(Name name)
     *      standard Name defined in terms of ListExpressions only
     *      Name defined in terms of other Names
     *      undefined Name
     *  
     *  getNames()
     *      number of Names: 0, 1, >1
     *     
     *  
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Recipients
    private final static Recipient AB = new Recipient("a@b");
    private final static Recipient CD = new Recipient("c@d");
    private final static Recipient SPECIAL = new Recipient("-_@b");

    // Testing getExpression...
    
    // standard list expression
    @Test
    public void testGetExpressionStandard() {
        final Environment testEnv = new Environment();
        testEnv.reassign(new Name("a"), new Union(new Intersect(CD, SPECIAL), AB));
        ListExpression expectedExpression = new Union(new Intersect(CD, SPECIAL), AB);
        assertEquals("expected correct standard list expression", expectedExpression, testEnv.getExpression(new Name("a")));
    }
    
    // list expression containing other listnames
    @Test
    public void testGetExpressionRecursive() {
        final Environment testEnv = new Environment();
        testEnv.reassign(new Name("a"), new Definition(new Name("b"), AB));
        ListExpression expectedExpression = new Definition(new Name("b"), AB);
        assertEquals("expected correct recursive list expression", expectedExpression, testEnv.getExpression(new Name("a")));
    }
    
    // undefined listname
    @Test
    public void testGetExpressionUndefined() {
        final Environment testEnv = new Environment();
        ListExpression expectedExpression = new Empty();
        assertEquals("expected correct empty list expression", expectedExpression, testEnv.getExpression(new Name("a")));
    }
    
    // mail loop
    @Test(expected=AssertionError.class)
    public void testMailLoopDetection() {
        final Environment testEnv = new Environment();
        testEnv.reassign(new Name("a"), new Name("b"));
        testEnv.reassign(new Name("b"), new Name("a"));
    }
    
    
///////////////////////////////////////////////////////////////////////////
    // Testing getNames()...
    
    // length = 0
    @Test
    public void testGetNamesEmpty() {
        final Environment testEnv = new Environment();
        assertEquals("expected correct number of names for empty environment", 0, testEnv.getNames().size());
    }
    
    // length = 1
    @Test
    public void testGetNamesOne() {
        final Environment testEnv = new Environment();
        testEnv.reassign(new Name("a"), new Union(AB, CD));
        final Set<ListExpression> expectedNames = new HashSet<>(Arrays.asList(new Name("a")));
        assertEquals("expected correct name for single name environment", expectedNames, testEnv.getNames());
    }
    
    // length > 1
    @Test
    public void testGetNamesMultiple() {
        final Environment testEnv = new Environment();
        testEnv.reassign(new Name("a"), new Union(AB, CD));
        testEnv.reassign(new Name("-"), new Union(new Name("a"), CD));
        final Set<ListExpression> expectedNames = new HashSet<>(Arrays.asList(new Name("a"), new Name("-")));
        assertEquals("expected correct name for multiple name environment", expectedNames, testEnv.getNames());
    }
    
///////////////////////////////////////////////////////////////////////////
    // Testing mail loops
    
    // Covers > 2-element loop
    @Test(expected=AssertionError.class)
    public void testMailLoop() {
        final Environment testEnv = new Environment();
        testEnv.reassign(new Name("a"), new Name("b"));
        testEnv.reassign(new Name("b"), new Name("c"));
        testEnv.reassign(new Name("c"), new Name("d"));
        testEnv.reassign(new Name("d"), new Name("a"));
    }
    
    // Covers 2-element loop
    @Test(expected=AssertionError.class)
    public void testMailLoopAB() {
        final Environment testEnv = new Environment();
        testEnv.reassign(new Name("a"), new Name("b"));
        testEnv.reassign(new Name("b"), new Name("a"));
    }
    
    // Covers definition in terms of self; should be allowed
    public void testSelfLoop() {
        final Environment testEnv = new Environment();
        testEnv.reassign(new Name("a"), new Recipient("a@c"));
        testEnv.reassign(new Name("a"), new Union(new Name("a"), new Recipient("b@c")));
        final Set<ListExpression> expectedRecipients = new HashSet<>(Arrays.asList(new Recipient("a@c"), new Recipient("b@c")));
        assertEquals("Expected a to have 2 recipients", expectedRecipients, testEnv.getExpression(new Name("a")).recipients(testEnv));
    }
    
}


