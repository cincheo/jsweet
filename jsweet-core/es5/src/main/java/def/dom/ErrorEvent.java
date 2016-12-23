package def.dom;
public class ErrorEvent extends Event {
    public double colno;
    public Object error;
    public String filename;
    public double lineno;
    public String message;
    native public void initErrorEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, String messageArg, String filenameArg, double linenoArg);
    public static ErrorEvent prototype;
    public ErrorEvent(){}
}

