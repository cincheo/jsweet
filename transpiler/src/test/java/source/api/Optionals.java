package source.api;

import java.util.Optional;

import def.js.Error;

public class Optionals {
    private static final String PREFERRED_VALUE = "c'est la vie";

    public static void main(String[] args) {

    }

    static void executePresentTests() {
        Optional<String> o = Optional.of(PREFERRED_VALUE);
        assertContains(o, PREFERRED_VALUE);

        o = Optional.ofNullable(PREFERRED_VALUE);
        assertContains(o, PREFERRED_VALUE);

        assert !o.equals(Optional.ofNullable(null));

        assertContains(o.filter((current) -> current == PREFERRED_VALUE), PREFERRED_VALUE);
        assertEmpty(o.filter((current) -> current == "XXXC'est la vie"));

        assertContains(o.flatMap(current -> Optional.of("new")), "new");
        assertContains(o.map(current -> "new2"), "new2");

        String v = o.orElse("defaultVal");
        assert v == PREFERRED_VALUE;
        v = o.orElseGet(() -> "defaultVal2");
        assert v == PREFERRED_VALUE;

        String[] modifiedForPresent = { "", "" };
        o.ifPresent(__ -> modifiedForPresent[0] = "modified");

        assert modifiedForPresent[0] == "modified";
        assert modifiedForPresent[1] == "modified";

        // TODO
//        Object[] valsAfterStream = o.stream().toArray();
    }

    static void executeAbsentTests() {
        Optional<String> o = Optional.empty();
        assertEmpty(o);

        o = Optional.ofNullable(null);
        assertEmpty(o);

        assert o.equals(Optional.ofNullable(null));

        boolean caught = false;
        try {
            o = Optional.of(null);
        } catch (Error e) {
            caught = true;
        }
        assert caught;

        String v = o.orElse("defaultVal");
        assert v == "defaultVal";
        v = o.orElseGet(() -> "defaultVal2");
        assert v == "defaultVal2";

        final String[] modified = { "", "" };
        o.ifPresent(__ -> modified[0] = "what");

        assert modified[0] == "";
        assert modified[1] == "valid";
    }

    static <T> void assertContains(Optional<T> o, T value) {
        assert o.get() == value;
        assert o.isPresent();

        o.orElseThrow(() -> new MyMissingError());
    }

    static void assertEmpty(Optional<?> o) {
        assert !o.isPresent();
        assert o != null;

        boolean caught = false;
        try {
            o.get();
        } catch (Error e) {
            caught = true;
        }
        assert caught;

        caught = false;
        try {
            o.orElseThrow(() -> new MyMissingError());
        } catch (MyMissingError e) {
            caught = true;
        }
        assert caught;
    }
}

class MyMissingError extends def.js.Error {
} 