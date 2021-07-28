package source.enums;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.any;

import java.util.HashMap;
import java.util.Map;

import jsweet.lang.StringEnum;

public class StringEnums {

    public static void main(String[] args) {
        m(StringEnumType.TEST1);

        $export("value", StringEnumType.TEST3);

        ComplexStringEnum v2 = ComplexStringEnum.getFromValue("V2");
        $export("value2", v2);
        $export("value2_getValue", v2.getValue());
    }

    public static void m(StringEnumType e) {
        assert any(e) == "TEST1";
        switch (e) {
        case TEST2:
        case TEST3:
        default:
            assert false;
        case TEST1:
            break;
        }
    }

    @StringEnum
    enum ComplexStringEnum {
        VAL1("V1"), VAL2("V2"), VAL3("V3");

        private final String value;
        private static final Map<String, ComplexStringEnum> lookup = new HashMap<>();

        static {
            for (ComplexStringEnum type : ComplexStringEnum.values()) {
                
                System.out.println("MIAOU");
                System.out.println(type);
                System.out.println(type.getValue());
                
                lookup.put(type.getValue(), type);
            }
        }

        public static ComplexStringEnum getFromValue(String abbreviation) {
            return lookup.get(abbreviation);
        }

        ComplexStringEnum(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
