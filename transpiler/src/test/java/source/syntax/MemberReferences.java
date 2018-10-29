package source.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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
class NaiveStringBuilder {
    String cur = "";
    String add(String n) {
        cur += n;
        return n;
    }
}
class CustomStream<T> {
    private List<T> l;
    CustomStream(List<T> l) {
        this.l = l != null ? l : new ArrayList<>();
    }
    <R> CustomStream<R> map(Function<T, R> mapFunc) {
        List<R> result = new ArrayList<>();
        for(T elem : l) {
            result.add(mapFunc.apply(elem));
        }
        return new CustomStream<>(result);
    }
    CustomStream<T> filter(Predicate<T> filterFunc) {
        List<T> result = new ArrayList<>();
        for(T elem : l) {
            if(filterFunc.test(elem)) {
                result.add(elem);
            }
        }
        this.l = result;
        return this;
    }
    List<T> getL() {
        return l;
    }
}

public class MemberReferences {

    public static void main() {
        NaiveStringBuilder nsb = new NaiveStringBuilder();
        List<String> l = new ArrayList<>();
        l.add(".a"); l.add("a"); l.add(".b"); l.add("b");
        l = new CustomStream<>(l)
                .map(SomeStringWrapper::new) // constructor reference
                .filter(SomeStringWrapper::startsWithDot) // instance member reference
                .map(SomeStringWrapper::getS) // again instance member reference
                .map(nsb::add) // instance member reference of an existing object
                .filter(SomeStringWrapper::stringContainsA) // class/static member reference
                .getL();
        assert l.size() == 1;
        assert ".a.b".equals(nsb.cur);
    }
}
