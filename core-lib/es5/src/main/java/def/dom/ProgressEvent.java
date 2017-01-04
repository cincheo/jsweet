package def.dom;
public class ProgressEvent extends Event {
    public Boolean lengthComputable;
    public double loaded;
    public double total;
    native public void initProgressEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Boolean lengthComputableArg, double loadedArg, double totalArg);
    public static ProgressEvent prototype;
    public ProgressEvent(String type, ProgressEventInit eventInitDict){}
    public ProgressEvent(String type){}
    protected ProgressEvent(){}
}

