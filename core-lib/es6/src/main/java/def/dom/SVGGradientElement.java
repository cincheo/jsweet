package def.dom;

@jsweet.lang.Extends({SVGStylable.class,SVGExternalResourcesRequired.class,SVGURIReference.class,SVGUnitTypes.class})
public class SVGGradientElement extends SVGElement {
    public SVGAnimatedTransformList gradientTransform;
    public SVGAnimatedEnumeration gradientUnits;
    public SVGAnimatedEnumeration spreadMethod;
    public double SVG_SPREADMETHOD_PAD;
    public double SVG_SPREADMETHOD_REFLECT;
    public double SVG_SPREADMETHOD_REPEAT;
    public double SVG_SPREADMETHOD_UNKNOWN;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGGradientElement prototype;
    public SVGGradientElement(){}
    public SVGAnimatedString className;
    public CSSStyleDeclaration style;
    public SVGAnimatedBoolean externalResourcesRequired;
    public SVGAnimatedString href;
    public static double SVG_UNIT_TYPE_OBJECTBOUNDINGBOX;
    public static double SVG_UNIT_TYPE_UNKNOWN;
    public static double SVG_UNIT_TYPE_USERSPACEONUSE;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

