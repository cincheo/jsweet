package def.dom;
@jsweet.lang.Extends({SVGStylable.class,SVGLangSpace.class,SVGExternalResourcesRequired.class,SVGFitToViewBox.class})
public class SVGMarkerElement extends SVGElement {
    public SVGAnimatedLength markerHeight;
    public SVGAnimatedEnumeration markerUnits;
    public SVGAnimatedLength markerWidth;
    public SVGAnimatedAngle orientAngle;
    public SVGAnimatedEnumeration orientType;
    public SVGAnimatedLength refX;
    public SVGAnimatedLength refY;
    native public void setOrientToAngle(SVGAngle angle);
    native public void setOrientToAuto();
    public double SVG_MARKERUNITS_STROKEWIDTH;
    public double SVG_MARKERUNITS_UNKNOWN;
    public double SVG_MARKERUNITS_USERSPACEONUSE;
    public double SVG_MARKER_ORIENT_ANGLE;
    public double SVG_MARKER_ORIENT_AUTO;
    public double SVG_MARKER_ORIENT_UNKNOWN;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGMarkerElement prototype;
    public SVGMarkerElement(){}
    public Object className;
    public CSSStyleDeclaration style;
    public String xmllang;
    public String xmlspace;
    public SVGAnimatedBoolean externalResourcesRequired;
    public SVGAnimatedPreserveAspectRatio preserveAspectRatio;
    public SVGAnimatedRect viewBox;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

