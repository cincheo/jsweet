package source.structural;

public class InnerClassWithAbstractClassAndInterface {

}

interface Valuable {
    int getValue();
}

class Iterator {    
    private int v;
    public Iterator(int v) {
        this.v = v;
    }    
}

abstract class AbstractValuable implements Valuable {            
    private final class InternalIterator extends Iterator {
        public InternalIterator() {
            super(AbstractValuable.this.getValue());
        }        
    }    
}
