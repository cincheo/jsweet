package source.nativestructures;

import java.util.ArrayList;
import java.util.List;

public class ExtendsJDKAnonymous {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        // you can use regular Java API
        List<String> l = new ArrayList() {
            final ArrayList me = this;

            @Override
            public boolean add(Object o) {
                return super.add(o);
            }
        };
        l.add("Hello");
        l.add("world");
    }
	
}
