package source.overload;

import static jsweet.util.Lang.$apply;
import static jsweet.util.Lang.object;

public class OverLoadClassAndObjectNoStubs {

    public static void main(String[] args) {
        OverLoadClassAndObjectNoStubs overload = new OverLoadClassAndObjectNoStubs();

        System.out.println(overload.m(AnInterface11.class));
        System.out.println(overload.m(new AClass11()));
        System.out.println((String)$apply(object(overload).$get("m$source_overload_AnInterface11"), new AClass11()));
        System.out.println((String)$apply(object(overload).$get("m$java_lang_Class"), AClass11.class));
        // Overload with strings cannot work because AnInterface.class is transpiled as a string
        System.out.println((String)$apply(object(overload).$get("m$java_lang_Class"), AnInterface.class));
        
        System.out.println(overload.m2(AClass11.class));
        System.out.println(overload.m2(new AClass11()));
        System.out.println((String)$apply(object(overload).$get("m2$source_overload_AClass11"), new AClass11()));
        System.out.println((String)$apply(object(overload).$get("m2$java_lang_Class"), AClass11.class));
        
        assert overload.m(AnInterface11.class) == "2:source.overload.AnInterface11";
        assert overload.m(new AClass11()) == "1:object";
        assert $apply(object(overload).$get("m$source_overload_AnInterface11"), new AClass11()) == "1:object";
        assert $apply(object(overload).$get("m$java_lang_Class"), AClass11.class) == "2:source.overload.AClass11";
        assert $apply(object(overload).$get("m$java_lang_Class"), AnInterface11.class) == "2:source.overload.AnInterface11";

        assert overload.m2(AClass11.class) == "2:source.overload.AClass11";
        assert overload.m2(new AClass11()) == "1:object";
        assert $apply(object(overload).$get("m2$source_overload_AClass11"), new AClass11()) == "1:object";
        assert $apply(object(overload).$get("m2$java_lang_Class"), AClass11.class) == "2:source.overload.AClass11";
    
    }

    String m(AnInterface11 o) {
        return "1:" + o.getName();
    }

    String m(Class<AnInterface11> clazz) {
        return "2:" + clazz.getName();
    }

    String m2(AClass11 o) {
        return "1:" + o.getName();
    }

    String m2(Class<AClass11> clazz) {
        return "2:" + clazz.getName();
    }

}

interface AnInterface11 {
    String getName();
}

class AClass11 implements AnInterface11 {
    public String getName() {
        return "object";
    }
}
