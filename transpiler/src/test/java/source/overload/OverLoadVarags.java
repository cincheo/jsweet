package source.overload;

import static jsweet.util.Lang.$insert;

public class OverLoadVarags {

    public static void main(String[] args) {
        OverLoadVarags ov = new OverLoadVarags();
        System.out.println(ov.m(new AClass2()));
        System.out.println(ov.m(new AClass2(), true, "a", "b", "c"));
        System.out.println((String) $insert("ov.m(new AClass2())"));
        System.out.println((String) $insert("ov.m(new AClass2(), true, 'a', 'b', 'c')"));
        assert ov.m(new AClass2()) == "1:object";
        assert ov.m(new AClass2(), true, "a", "b", "c") == "objecta,b,c/3";
        assert $insert("ov.m(new AClass2())") == "1:object";
        assert $insert("ov.m(new AClass2(), true, 'a', 'b', 'c')") == "objecta,b,c/3";
        // passing an array does not work
        // TODO: when invoking org method, test if passed expression type is an array
        // and use call
        // assert $insert("ov.m(new AClass2(), true, ['a', 'b', 'c'])") ==
        // "objecta,b,c/3";
        assert ov.mref(new AClass2(), "a", "b", "c") == "objecta,b,c/3";
        assert ov.mref(new AClass2(), new String[] { "a", "b", "c" }) == "objecta,b,c/3";
    }

    String m(AnInterface2 dto, boolean b, String... options) {
        return dto.getName() + varM(options);
    }

    String m(AnInterface2 dto) {
        return "1:" + dto.getName();
    }

    String mref(AnInterface2 dto, String... options) {
        return dto.getName() + varM(options);
    }

    String varM(String... options) {
        return "" + options + "/" + $insert("arguments.length");
    }

}

interface AnInterface2 {
    String getName();
}

class AClass2 implements AnInterface2 {
    public String getName() {
        return "object";
    }
}