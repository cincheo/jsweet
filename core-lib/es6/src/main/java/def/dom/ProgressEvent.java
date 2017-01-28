package def.dom;

public class ProgressEvent extends Event {
    public java.lang.Boolean lengthComputable;
    public double loaded;
    public double total;
    native public void initProgressEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, java.lang.Boolean lengthComputableArg, double loadedArg, double totalArg);
    public static ProgressEvent prototype;
    public ProgressEvent(){}
}

