package norn;

import java.util.Map;
import java.util.Set;

/**
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
    }
    
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }
    
}
