package norn;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for the static methods of ListExpression ADT 
 *
 */
public class ListExpressionTest {
    // Testing Strategy:
    //      The ListExpression is a Union
    //      The ListExpression is a Recipient
    //      The ListExpression is Empty
    //      The ListExpression is a Difference
    //      The ListExpression is an Intersection 
    //      The ListExpression is a Definition
    //      The ListExpression is a Name
    // recipients: 
    //      The ListExpression has 0; 1; > 1 recipients
    //      Constraints: Union, Difference, and Intersection >=1 operand being the empty set
    //                   Definition where ListExpression is Empty
    // toString:
    //      The ListExpression is a Union
    //      The ListExpression is a Recipient
    //      The ListExpression is Empty
    //      The ListExpression is a Difference
    //      The ListExpression is an Intersection 
    //      The ListExpression is a Definition
    //      The ListExpression is a Name
    // equals:
    //      ListExpressions are structurally equal
    // hashCode:
    //      ListExpressions that are equal have the same hashCode
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }
    
    /*Recipient Test Cases*/
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
    
    /*ToString Test Cases*/
    @Test //ListExpression covers Empty
    public void testToStringEmpty() {
        String input = ""; 
        ListExpression parsed = ListExpression.parse(input);
        String expected = "";
        assertTrue("Expected empty String", parsed.toString().equals(expected));
    }
    
    @Test //ListExpression covers Recipient
    public void testToStringRecipient() {
        String input = "Hello@kitty"; 
        ListExpression parsed = ListExpression.parse(input);
        String expected = "hello@kitty";
        assertTrue("Expected one recipient in toString", parsed.toString().equals(expected));
    }
    @Test//ListExpression covers Union
    public void testToStringUnion() {
        String input = "Hello@kitty, pand@apple, pochacco@dog"; 
        ListExpression parsed = ListExpression.parse(input);
        String expected = "((hello@kitty, pand@apple), pochacco@dog)";
        assertTrue("Expected union of three recipients in toString", parsed.toString().equals(expected));
    }
    
    @Test //List Expression covers Difference
    public void testToStringDifference() {
        String input = "(Sherlock@221B , Watson@221B) ! (Watson@221B , House@MD , Meredith@Grey)"; 
        ListExpression parsed = ListExpression.parse(input);
        String expected = "((sherlock@221b, watson@221b) ! ((watson@221b, house@md), meredith@grey))";
        assertTrue("Expected difference of two sets of recipients in toString", parsed.toString().equals(expected));
     }
         
     @Test //List Expression covers Intersection
     public void testToStringIntersection() {
        String input = "(Sherlock@221B , Watson@221B) * (Watson@221B , House@MD , Meredith@Grey)"; 
        ListExpression parsed = ListExpression.parse(input);
        String expected = "((sherlock@221b, watson@221b) * ((watson@221b, house@md), meredith@grey))";
        assertTrue("Expected intersection of two sets of recipients in toString", parsed.toString().equals(expected));
     }
     
     @Test //List Expression covers Definition
     public void testToStringDefinition() {
         String input = "gryffindor = harry@potter, hermione@granger, ron@weasley";
         ListExpression parsed = ListExpression.parse(input);
         String expected = "gryffindor = ((harry@potter, hermione@granger), ron@weasley)";
         assertTrue("Expected definition of Gryffindor", parsed.toString().equals(expected));
     }
    
     
     @Test //ListExpression covers Name 
     public void testToStringName() { 
         String input = "a";
         ListExpression parsed = ListExpression.parse(input);
         String expected = "a";
         assertTrue("Expected single name", parsed.toString().equals(expected));
         
     }
     
    /*Equals Test Cases*/ 
    @Test //ListExpression covers Empty
    public void testEqualsEmpty() {
        String input = ""; 
        ListExpression inputParsed = ListExpression.parse(input);
        String expected = "";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected empty String", inputParsed.equals(expectedParsed));
    }
    @Test //ListExpression covers Recipient
    public void testEqualsRecipient() {
        String input = "Hello@kitty"; 
        ListExpression inputParsed = ListExpression.parse(input);
        String expected = "hello@kitty";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected ASTs to be structurally equal", inputParsed.equals(expectedParsed));
    }
    @Test//ListExpression covers Union
    public void testEqualsUnion() {
        String input = "Hello@kitty, pand@apple, pochacco@dog"; 
        String expected = "hello@kitty , pand@apple , pochacco@dog";
        ListExpression inputParsed = ListExpression.parse(input);
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected ASTs to be structurally equal", inputParsed.equals(expectedParsed));
    }
    
    @Test //List Expression covers Difference
    public void testEqualsDifference() {
        String input = "(Sherlock@221B, Watson@221B) ! (Watson@221B, House@MD, Meredith@Grey)"; 
        ListExpression parsed = ListExpression.parse(input);
        String expected = "(Sherlock@221B, Watson@221B) ! (Watson@221B, House@MD, Meredith@Grey)";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected identical ASTs", parsed.equals(expectedParsed));
    }
    
    @Test //List Expression covers Intersection
    public void testEqualsIntersection() {
        String input = "(Sherlock@221B, Watson@221B) * (Watson@221B, House@MD, Meredith@Grey)"; 
        ListExpression parsed = ListExpression.parse(input);
        String expected = "(Sherlock@221B, Watson@221B) * (Watson@221B, House@MD, Meredith@Grey)";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected identical ASTs", parsed.equals(expectedParsed));
    }
    
    @Test //List Expression covers Definition
    public void testEqualsDefinition() {
        String input = "gryffindor = harry@potter, hermione@granger, ron@weasley";
        ListExpression parsed = ListExpression.parse(input);
        String expected = "gryffindor = harry@potter , hermione@granger , ron@weasley";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected identical ASTs", parsed.equals(expectedParsed));
    }
    
    @Test //ListExpression covers Name 
    public void testEqualsName() { 
        String input = "name";
        ListExpression parsed = ListExpression.parse(input);
        String expected = "name";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected identical ASTs", parsed.equals(expectedParsed));
    }
    
    @Test //ListExpression covers Union; checks for structural equality
    public void testEqualsStructuralEquality() {
        String firstInput = "sarah@gmail, john@gmail, bob@gmail";
        String secondInput = "sarah@gmail, (john@gmail, bob@gmail)";
        ListExpression firstParsed = ListExpression.parse(firstInput);
        ListExpression secondParsed = ListExpression.parse(secondInput);
        assertFalse("These expressions are not structurally equal", firstParsed.equals(secondParsed));
    }
    
    /*HashCode Test Cases*/ 
    @Test //ListExpression covers Empty
    public void testHashCodeEmpty() {
        String input = ""; 
        ListExpression inputParsed = ListExpression.parse(input);
        String expected = "";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected empty expressions to have the same hash code", inputParsed.hashCode() == expectedParsed.hashCode());
    }
    @Test //ListExpression covers Recipient
    public void testHashCodeRecipient() {
        String input = "Hello@kitty"; 
        ListExpression inputParsed = ListExpression.parse(input);
        String expected = "hello@kitty";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected the same hash code for the equal expressions", inputParsed.hashCode() == expectedParsed.hashCode());
    }
    @Test//ListExpression covers Union
    public void testHashCodeUnion() {
        String input = "Hello@kitty, pand@apple, pochacco@dog"; 
        String expected = "hello@kitty, pand@apple, pochacco@dog";
        ListExpression inputParsed = ListExpression.parse(input);
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected the same hash code for the equal expressions", inputParsed.hashCode() == expectedParsed.hashCode());
    }
    
    @Test //List Expression covers Difference
    public void testHashCodeDifference() {
        String input = "(Sherlock@221B, Watson@221B) ! (Watson@221B, House@MD, Meredith@Grey)"; 
        ListExpression parsed = ListExpression.parse(input);
        String expected = "(Sherlock@221B, Watson@221B) ! (Watson@221B, House@MD, Meredith@Grey)";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected the same hash code for the equal expressions", parsed.hashCode() == expectedParsed.hashCode());
    }
    
    @Test //List Expression covers Intersection
    public void testHashCodeIntersection() {
    String input = "(Sherlock@221B, Watson@221B) * (Watson@221B, House@MD, Meredith@Grey)"; 
       ListExpression parsed = ListExpression.parse(input);
       String expected = "(Sherlock@221B, Watson@221B) * (Watson@221B, House@MD, Meredith@Grey)";
       ListExpression expectedParsed = ListExpression.parse(expected);
       assertTrue("Expected the same hash code for the equal expressions", parsed.hashCode() == expectedParsed.hashCode());
    }
    
    @Test //List Expression covers Definition
    public void testHashCodeDefinition() {
        String input = "gryffindor = harry@potter, hermione@granger, ron@weasley";
        ListExpression parsed = ListExpression.parse(input);
        String expected = "gryffindor = harry@potter , hermione@granger , ron@weasley";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected the same hash code for the equal expressions", parsed.hashCode() == expectedParsed.hashCode());
    }
    
    @Test //ListExpression covers Name 
    public void testHashCodeName() { 
        String input = "hello";
        ListExpression parsed = ListExpression.parse(input);
        String expected = "hello";
        ListExpression expectedParsed = ListExpression.parse(expected);
        assertTrue("Expected the same hash code for the equal expressions", parsed.hashCode() == expectedParsed.hashCode());
    }
    
    /*Environment() tests*/
    
    @Test //ListExpression covers empty
    public void testEnvEmpty() {
        String input = "";
        ListExpression parsed = ListExpression.parse(input);
        Map<String, ListExpression> expected = new HashMap<>();
        assertTrue("Expected empty environment", parsed.environment().equals(expected));
    }
    
    @Test //ListExpression covers recipient 
    public void testEnvRecipient() {
        String input = "hello@world";
        ListExpression parsed = ListExpression.parse(input);
        Map<String, ListExpression> expected = new HashMap<>();
        assertTrue("Expected empty environment", parsed.environment().equals(expected));
    }
    
    @Test//ListExpression covers union
    public void testEnvUnion() {
        String input = "percy@jackson, grover@underwood, annabeth@chase";
        ListExpression parsed = ListExpression.parse(input);
        Map<String, ListExpression> expected = new HashMap<>();
        assertTrue("Expected empty environment", parsed.environment().equals(expected));
    }
    
    @Test //ListExpression covers Difference
    public void testEnvDifference() {
        String input = "percy@jackson, grover@underwood, annabeth@chase ! percy@jackson";
        ListExpression parsed = ListExpression.parse(input);
        Map<String, ListExpression> expected = new HashMap<>();
        assertTrue("Expected empty environment", parsed.environment().equals(expected));
    }
    
    @Test //ListExpression covers Intersection
    public void testEnvIntersection() {
        String input = "(percy@jackson, grover@underwood, annabeth@chase) * percy@jackson";
        ListExpression parsed = ListExpression.parse(input);
        Map<String, ListExpression> expected = new HashMap<>();
        assertTrue("Expected empty environment", parsed.environment().equals(expected));
    }
    @Test //ListExpression covers Definition
    public void testEnvDefinition() {
        String input = "heroes = percy@jackson, grover@underwood, annabeth@chase";
        ListExpression parsed = ListExpression.parse(input);
        String expected = "percy@jackson, grover@underwood, annabeth@chase";
        ListExpression expectedParsed = ListExpression.parse(expected);
        Map<String, ListExpression> expectedMap = new HashMap<>();
        expectedMap.put("heroes", expectedParsed);
        assertTrue("Expected filled environment", parsed.environment().equals(expectedMap));
    }
    @Test //ListExpression covers Name
    public void testEnvName() {
        String input = "hello";
        ListExpression parsed = ListExpression.parse(input);
        Map<String,ListExpression> expected = new HashMap<>();
        assertTrue("Expected empty Environment",parsed.environment().equals(expected));
    }
    
}