package def.dom.intl;
import def.js.Date;
public class DateTimeFormat extends def.js.Object {
    native public String format(Date date);
    native public ResolvedDateTimeFormatOptions resolvedOptions();
    public DateTimeFormat(String[] locales, DateTimeFormatOptions options){}
    public DateTimeFormat(String locale, DateTimeFormatOptions options){}
    native public static DateTimeFormat applyStatic(String[] locales, DateTimeFormatOptions options);
    native public static DateTimeFormat applyStatic(String locale, DateTimeFormatOptions options);
    native public static String[] supportedLocalesOf(String[] locales, DateTimeFormatOptions options);
    native public static String[] supportedLocalesOf(String locale, DateTimeFormatOptions options);
    native public String format();
    public DateTimeFormat(String[] locales){}
    public DateTimeFormat(){}
    public DateTimeFormat(String locale){}
    native public static DateTimeFormat applyStatic(String[] locales);
    native public static DateTimeFormat applyStatic();
    native public static DateTimeFormat applyStatic(String locale);
    native public static String[] supportedLocalesOf(String[] locales);
    native public static String[] supportedLocalesOf(String locale);
    native public String format(double date);
}

