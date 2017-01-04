package def.dom;
public class DragEvent extends MouseEvent {
    public DataTransfer dataTransfer;
    native public void initDragEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, Boolean ctrlKeyArg, Boolean altKeyArg, Boolean shiftKeyArg, Boolean metaKeyArg, double buttonArg, EventTarget relatedTargetArg, DataTransfer dataTransferArg);
    native public void msConvertURL(File file, String targetType, String targetURL);
    public static DragEvent prototype;
    public DragEvent(){}
    native public void msConvertURL(File file, String targetType);
}

