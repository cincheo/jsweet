package source.overload;

import static jsweet.util.Lang.$insert;

public class OverLoadVaragsNoStubs {

    public static void main(String[] args) {
        OverLoadVaragsNoStubs ov = new OverLoadVaragsNoStubs();
        System.out.println(ov.m(new AClass22()));
        System.out.println(ov.m(new AClass22(), true, "a", "b", "c"));
        System.out.println((String)$insert("ov.m$source_overload_AnInterface22(new AClass22())"));
        System.out.println((String)$insert("ov.m$source_overload_AnInterface22$boolean$java_lang_String_A(new AClass22(), true, 'a', 'b', 'c')"));
        assert ov.m(new AClass22()) == "1:object";
        assert ov.m(new AClass22(), true, "a", "b", "c") == "objecta,b,c/3";
        assert $insert("ov.m$source_overload_AnInterface22(new AClass22())") == "1:object";
        assert $insert("ov.m$source_overload_AnInterface22$boolean$java_lang_String_A(new AClass22(), true, 'a', 'b', 'c')") == "objecta,b,c/3";
        // passing an array does not work
        // TODO: when invoking org method, test if passed expression type is an array and use call
        //assert $insert("ov.m(new AClass22(), true, ['a', 'b', 'c'])") == "objecta,b,c/3";
        assert ov.mref(new AClass22(), "a", "b", "c") == "objecta,b,c/3";
        assert ov.mref(new AClass22(), new String[] {"a", "b", "c"}) == "objecta,b,c/3";
    }

    String m(AnInterface22 dto, boolean b, String... options) {
        return dto.getName() + varM(options);
    }

    String m(AnInterface22 dto) {
        return "1:" + dto.getName();
    }

    String mref(AnInterface22 dto, String... options) {
        return dto.getName() + varM(options);
    }
    
    String varM(String... options) {
        return "" + options + "/" + $insert("arguments.length");
    }

}

interface AnInterface22 {
    String getName();
}

class AClass22 implements AnInterface22 {
    public String getName() {
        return "object";
    }
}
