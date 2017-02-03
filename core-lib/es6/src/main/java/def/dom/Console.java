package def.dom;

import def.js.Object;

public class Console extends def.js.Object {
    @jsweet.lang.Name("assert")
    native public void Assert(java.lang.Boolean test, java.lang.String message, java.lang.Object... optionalParams);
    native public void clear();
    native public void count(java.lang.String countTitle);
    native public void debug(java.lang.String message, java.lang.Object... optionalParams);
    native public void dir(java.lang.Object value, java.lang.Object... optionalParams);
    native public void dirxml(java.lang.Object value);
    native public void error(java.lang.Object message, java.lang.Object... optionalParams);
    native public void group(java.lang.String groupTitle);
    native public void groupCollapsed(java.lang.String groupTitle);
    native public void groupEnd();
    native public void info(java.lang.Object message, java.lang.Object... optionalParams);
    native public void log(java.lang.Object message, java.lang.Object... optionalParams);
    native public java.lang.Boolean msIsIndependentlyComposed(Element element);
    native public void profile(java.lang.String reportName);
    native public void profileEnd();
    native public void select(Element element);
    native public void time(java.lang.String timerName);
    native public void timeEnd(java.lang.String timerName);
    native public void trace();
    native public void warn(java.lang.Object message, java.lang.Object... optionalParams);
    public static Console prototype;
    public Console(){}
    @jsweet.lang.Name("assert")
    native public void Assert(java.lang.Boolean test);
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
    native public void warn();
}

