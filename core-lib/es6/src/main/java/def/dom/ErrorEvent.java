package def.dom;

public class ErrorEvent extends Event {
    public double colno;
    public java.lang.Object error;
    public java.lang.String filename;
    public double lineno;
    public java.lang.String message;
    native public void initErrorEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, java.lang.String messageArg, java.lang.String filenameArg, double linenoArg);
    public static ErrorEvent prototype;
    public ErrorEvent(){}
}

