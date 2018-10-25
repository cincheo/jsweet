package source.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class SomeStringWrapper {
    String s;
    SomeStringWrapper(String s) {
        this.s = s;
    }
    boolean startsWithDot() {
        return s.charAt(0) == '.';
    }
    static boolean stringContainsA(String a) {
        return a.toLowerCase().contains("a");
    }
    String getS() {
        return s;
    }
}

public class MemberReferences {

    public static void main() {
        List<String> l = new ArrayList<>();
        l.add(".a"); l.add("a"); l.add(".b"); l.add("b");
        l = l.stream()
                .map(SomeStringWrapper::new) // constructor reference
                .filter(SomeStringWrapper::startsWithDot) // instance member reference
                .map(SomeStringWrapper::getS) // again instance member reference
                .filter(SomeStringWrapper::stringContainsA) // class/static member reference
                .collect(Collectors.toList());
        assert l.size() == 1;
    }
}
