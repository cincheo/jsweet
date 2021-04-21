package source.enums;

/**
 * @see FailingEnums
 */
public class PassingEnums {
    public static enum ToStringEnum {
        E1("E1.toString()"),
        E2("E2.toString()");

        private String toString;

        ToStringEnum(String toString) {
            this.toString = toString;
        }

        @Override
        public String toString() {
            return toString;
        }
    }
}
