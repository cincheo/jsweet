package source.enums;

/**
 * Enums that fail to compile with the current version of JSweet (last check with 3.0.0-RC1)
 * @see PassingEnums for possible workarounds
 */
public class FailingEnums {
    public static enum ToStringEnum {
        E1 {
            @Override
            public String toString() {
                return "E1.toString()";
            }
        },
        E2 {
            @Override
            public String toString() {
                return "E2.toString()";
            }
        }
    }
}
