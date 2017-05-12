package norn;

<<<<<<< HEAD
=======
import java.util.HashSet;
>>>>>>> 4f04f769f79cd8516157f564a55ab37b8ceca380
import java.util.Map;
import java.util.Set;

/**
<<<<<<< HEAD
 * Immutable representation of the name of an email list definition.
 */
public class Name implements ListExpression {

    public Name() {
        
    }
    
    @Override
    public Set<Recipient> recipients(Map<String, Definition> environment) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
=======
 * Immutable representation of a named set of email addresses.
 */
public class Name implements ListExpression {
    private final String name;
    
    // Abstraction function: AF(name) = the listname defined by name
    // Rep invariant: true
    // Rep safety: All fields are private, final, and immutable.
    // All references to any returned mutable objects are discarded.
    
    /**
     * Create a new Definition.
     * @param value the ListExpression used in this Definition
     * @param name the name of this list
     */
    public Name(String name) {
        this.name = name;
        checkRep();
    }
    
    
    /**
     * Check that the rep invariant is maintained.
     */
    private void checkRep() {
        assert name != null;
    }
    
    @Override
    public Set<Recipient> recipients(Map<Name, ListExpression> environment) {
        return new HashSet<>();
        // TODO: lookup name in environment
    }
    
    /**
     * Returned String has the format: 
     *      name = value.toString()
     */
    @Override
    public String toString() {
        return "";
>>>>>>> 4f04f769f79cd8516157f564a55ab37b8ceca380
    }
    
    @Override
    public boolean equals(Object obj) {
<<<<<<< HEAD
        // TODO Auto-generated method stub
        return super.equals(obj);
=======
        if (!(obj instanceof Name)) return false;
        Name that = (Name) obj;
        return name.equals(that.name);
>>>>>>> 4f04f769f79cd8516157f564a55ab37b8ceca380
    }
    
    @Override
    public int hashCode() {
<<<<<<< HEAD
        // TODO Auto-generated method stub
        return super.hashCode();
    }
    
=======
        return name.hashCode();
    }

>>>>>>> 4f04f769f79cd8516157f564a55ab37b8ceca380
}
