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
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGMarkerElement prototype;
    public SVGMarkerElement(){}
    public SVGAnimatedString className;
    public CSSStyleDeclaration style;
    public java.lang.String xmllang;
    public java.lang.String xmlspace;
    public SVGAnimatedBoolean externalResourcesRequired;
    public SVGAnimatedPreserveAspectRatio preserveAspectRatio;
    public SVGAnimatedRect viewBox;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

