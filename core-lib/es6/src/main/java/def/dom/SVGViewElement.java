package def.dom;

@jsweet.lang.Extends({SVGExternalResourcesRequired.class,SVGFitToViewBox.class,SVGZoomAndPan.class})
public class SVGViewElement extends SVGElement {
    public SVGStringList viewTarget;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGViewElement prototype;
    public SVGViewElement(){}
    public SVGAnimatedBoolean externalResourcesRequired;
    public SVGAnimatedPreserveAspectRatio preserveAspectRatio;
    public SVGAnimatedRect viewBox;
    public static double SVG_ZOOMANDPAN_DISABLE;
    public static double SVG_ZOOMANDPAN_MAGNIFY;
    public static double SVG_ZOOMANDPAN_UNKNOWN;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

