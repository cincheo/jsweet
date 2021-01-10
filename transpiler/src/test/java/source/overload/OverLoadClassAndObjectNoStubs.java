package source.overload;

import static jsweet.util.Lang.$apply;
import static jsweet.util.Lang.object;

public class OverLoadClassAndObjectNoStubs {

    public static void main(String[] args) {
        OverLoadClassAndObjectNoStubs overload = new OverLoadClassAndObjectNoStubs();

        System.out.println(overload.m(AnInterface.class));
        System.out.println(overload.m(new AClass()));
        System.out.println((String)$apply(object(overload).$get("m$source_overload_AnInterface"), new AClass()));
        System.out.println((String)$apply(object(overload).$get("m$java_lang_Class"), AClass.class));
        // Overload with strings cannot work because AnInterface.class is transpiled as a string
        System.out.println((String)$apply(object(overload).$get("m$java_lang_Class"), AnInterface.class));
        
        System.out.println(overload.m2(AClass.class));
        System.out.println(overload.m2(new AClass()));
        System.out.println((String)$apply(object(overload).$get("m2$source_overload_AClass"), new AClass()));
        System.out.println((String)$apply(object(overload).$get("m2$java_lang_Class"), AClass.class));
        
        assert overload.m(AnInterface.class) == "2:source.overload.AnInterface";
        assert overload.m(new AClass()) == "1:object";
        assert $apply(object(overload).$get("m$source_overload_AnInterface"), new AClass()) == "1:object";
        assert $apply(object(overload).$get("m$java_lang_Class"), AClass.class) == "2:source.overload.AClass";
        assert $apply(object(overload).$get("m$java_lang_Class"), AnInterface.class) == "2:source.overload.AnInterface";

        assert overload.m2(AClass.class) == "2:source.overload.AClass";
        assert overload.m2(new AClass()) == "1:object";
        assert $apply(object(overload).$get("m2$source_overload_AClass"), new AClass()) == "1:object";
        assert $apply(object(overload).$get("m2$java_lang_Class"), AClass.class) == "2:source.overload.AClass";
    
    }

    String m(AnInterface o) {
        return "1:" + o.getName();
    }

    String m(Class<AnInterface> clazz) {
        return "2:" + clazz.getName();
    }

    String m2(AClass o) {
        return "1:" + o.getName();
    }

    String m2(Class<AClass> clazz) {
        return "2:" + clazz.getName();
    }

}

interface AnInterface {
    String getName();
}

class AClass implements AnInterface {
    public String getName() {
        return "object";
    }
}