package def.dom.intl;
public class NumberFormat extends def.js.Object {
    native public String format(double value);
    native public ResolvedNumberFormatOptions resolvedOptions();
    public NumberFormat(String[] locales, NumberFormatOptions options){}
    public NumberFormat(String locale, NumberFormatOptions options){}
    native public static NumberFormat applyStatic(String[] locales, NumberFormatOptions options);
    native public static NumberFormat applyStatic(String locale, NumberFormatOptions options);
    native public static String[] supportedLocalesOf(String[] locales, NumberFormatOptions options);
    native public static String[] supportedLocalesOf(String locale, NumberFormatOptions options);
    public NumberFormat(String[] locales){}
    public NumberFormat(){}
    public NumberFormat(String locale){}
    native public static NumberFormat applyStatic(String[] locales);
    native public static NumberFormat applyStatic();
    native public static NumberFormat applyStatic(String locale);
    native public static String[] supportedLocalesOf(String[] locales);
    native public static String[] supportedLocalesOf(String locale);
}

