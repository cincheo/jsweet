package def.dom;

public class DragEvent extends MouseEvent {
    public DataTransfer dataTransfer;
    native public void initDragEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, java.lang.Boolean ctrlKeyArg, java.lang.Boolean altKeyArg, java.lang.Boolean shiftKeyArg, java.lang.Boolean metaKeyArg, double buttonArg, EventTarget relatedTargetArg, DataTransfer dataTransferArg);
    native public void msConvertURL(File file, java.lang.String targetType, java.lang.String targetURL);
    public static DragEvent prototype;
    public DragEvent(){}
    native public void msConvertURL(File file, java.lang.String targetType);
}

