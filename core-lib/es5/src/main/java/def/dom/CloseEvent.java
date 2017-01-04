package def.dom;
public class CloseEvent extends Event {
    public double code;
    public String reason;
    public Boolean wasClean;
    native public void initCloseEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Boolean wasCleanArg, double codeArg, String reasonArg);
    public static CloseEvent prototype;
    public CloseEvent(){}
}

