package norn;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable representation of a recipient of email
 */
public class Recipient implements ListExpression {
    private final String emailAddress;
    
    // Abstraction Function
    //  AF(emailAddress) = an email address as defined in Norn Specification
    // Rep Invariant
    //  emailAddress has a username and domain name separated by '@'. Usernames 
    //  and domain names are nonempty lowercase strings of letters, digits, 
    //  underscores, dashes, and periods.
    // Rep Safety
    //  All fields are private, final and immutable.
    //  All references to any returned mutable objects are discarded.
    
    /**
     * Create a new Recipient object.
     * @param emailAddress the email address of this Recipient
     */
    public Recipient(String emailAddress) {
        this.emailAddress = emailAddress.toLowerCase();
        checkRep();
    }
    
    /**
     *  Check that the rep invariant is maintained.
     */
    private void checkRep() {
        assert emailAddress != null;
        assert emailAddress.length() > 0;
        assert emailAddress.equals(emailAddress.toLowerCase());
        assert emailAddress.matches("[A-Za-z0-9_.-]+@[A-Za-z0-9_.-]+");
    }
    
    @Override
    public Set<Recipient> recipients(Environment environment) {
        return new HashSet<Recipient>(Arrays.asList(this));
    }
   
    /**
     * The returned String is the email address belonging to
     * this Recipient.
     */
    @Override
    public String toString() {
        return emailAddress;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Recipient)) return false;
        Recipient that = (Recipient) obj;
        return emailAddress.equals(that.emailAddress);
    }
    
    @Override
    public int hashCode() {
        return emailAddress.hashCode();
    }

}
