package def.js;
@java.lang.SuppressWarnings("serial")
public class Error extends RuntimeException {
    public java.lang.String name;
    public java.lang.String message;
    
    public java.lang.String stack;
    public native <T> T $get(String key);
    
    public Error(java.lang.String message){}
    native public static Error applyStatic(java.lang.String message);
    public static final Error prototype=null;
    public Error(){}
    native public static Error applyStatic();
}

