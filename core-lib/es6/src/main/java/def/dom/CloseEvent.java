package def.dom;

public class CloseEvent extends Event {
    public double code;
    public java.lang.String reason;
    public java.lang.Boolean wasClean;
    native public void initCloseEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, java.lang.Boolean wasCleanArg, double codeArg, java.lang.String reasonArg);
    public static CloseEvent prototype;
    public CloseEvent(){}
}

