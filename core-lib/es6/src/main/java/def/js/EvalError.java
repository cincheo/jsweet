package def.js;
public class EvalError extends Error {
    public EvalError(java.lang.String message){}
    native public static EvalError applyStatic(java.lang.String message);
    public static EvalError prototype;
    public EvalError(){}
    native public static EvalError applyStatic();
}

