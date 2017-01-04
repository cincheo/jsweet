package def.dom;
public class Console extends def.js.Object {
    @jsweet.lang.Name("assert")
    native public void Assert(Boolean test, String message, Object... optionalParams);
    native public void clear();
    native public void count(String countTitle);
    native public void debug(String message, Object... optionalParams);
    native public void dir(Object value, Object... optionalParams);
    native public void dirxml(Object value);
    native public void error(Object message, Object... optionalParams);
    native public void group(String groupTitle);
    native public void groupCollapsed(String groupTitle);
    native public void groupEnd();
    native public void info(Object message, Object... optionalParams);
    native public void log(Object message, Object... optionalParams);
    native public Boolean msIsIndependentlyComposed(Element element);
    native public void profile(String reportName);
    native public void profileEnd();
    native public void select(Element element);
    native public void time(String timerName);
    native public void timeEnd(String timerName);
    native public void trace(Object message, Object... optionalParams);
    native public void warn(Object message, Object... optionalParams);
    public static Console prototype;
    public Console(){}
    @jsweet.lang.Name("assert")
    native public void Assert(Boolean test);
    @jsweet.lang.Name("assert")
    native public void Assert();
    native public void count();
    native public void debug();
    native public void dir();
    native public void error();
    native public void group();
    native public void groupCollapsed();
    native public void info();
    native public void log();
    native public void profile();
    native public void time();
    native public void timeEnd();
    native public void trace();
    native public void warn();
}

