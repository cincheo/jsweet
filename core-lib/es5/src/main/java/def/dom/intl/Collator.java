package def.dom.intl;
public class Collator extends def.js.Object {
    native public double compare(String x, String y);
    native public ResolvedCollatorOptions resolvedOptions();
    public Collator(String[] locales, CollatorOptions options){}
    public Collator(String locale, CollatorOptions options){}
    native public static Collator applyStatic(String[] locales, CollatorOptions options);
    native public static Collator applyStatic(String locale, CollatorOptions options);
    native public static String[] supportedLocalesOf(String[] locales, CollatorOptions options);
    native public static String[] supportedLocalesOf(String locale, CollatorOptions options);
    public Collator(String[] locales){}
    public Collator(){}
    public Collator(String locale){}
    native public static Collator applyStatic(String[] locales);
    native public static Collator applyStatic();
    native public static Collator applyStatic(String locale);
    native public static String[] supportedLocalesOf(String[] locales);
    native public static String[] supportedLocalesOf(String locale);
}

