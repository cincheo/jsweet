package def.dom;
public class SVGZoomEvent extends UIEvent {
    public double newScale;
    public SVGPoint newTranslate;
    public double previousScale;
    public SVGPoint previousTranslate;
    public SVGRect zoomRectScreen;
    public static SVGZoomEvent prototype;
    public SVGZoomEvent(){}
}

