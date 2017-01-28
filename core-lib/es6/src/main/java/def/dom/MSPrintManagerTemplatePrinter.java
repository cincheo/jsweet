package def.dom;

@jsweet.lang.Extends({EventTarget.class})
public class MSPrintManagerTemplatePrinter extends MSTemplatePrinter {
    public double percentScale;
    public java.lang.Boolean showHeaderFooter;
    public java.lang.Boolean shrinkToFit;
    native public void drawPreviewPage(HTMLElement element, double pageNumber);
    native public void endPrint();
    native public java.lang.Object getPrintTaskOptionValue(java.lang.String key);
    native public void invalidatePreview();
    native public void setPageCount(double pageCount);
    native public void startPrint();
    public static MSPrintManagerTemplatePrinter prototype;
    public MSPrintManagerTemplatePrinter(){}
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public java.lang.Boolean dispatchEvent(Event evt);
    native public void removeEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void removeEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void removeEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
    native public void removeEventListener(java.lang.String type, EventListenerObject listener);
}

