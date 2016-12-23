package def.dom;
@jsweet.lang.Extends({SVGExternalResourcesRequired.class,SVGFitToViewBox.class,SVGZoomAndPan.class})
public class SVGViewElement extends SVGElement {
    public SVGStringList viewTarget;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGViewElement prototype;
    public SVGViewElement(){}
    public SVGAnimatedBoolean externalResourcesRequired;
    public SVGAnimatedPreserveAspectRatio preserveAspectRatio;
    public SVGAnimatedRect viewBox;
    public double zoomAndPan;
    public static double SVG_ZOOMANDPAN_DISABLE;
    public static double SVG_ZOOMANDPAN_MAGNIFY;
    public static double SVG_ZOOMANDPAN_UNKNOWN;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

