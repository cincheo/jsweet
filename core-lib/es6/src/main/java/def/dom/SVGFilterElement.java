package def.dom;

@jsweet.lang.Extends({SVGUnitTypes.class,SVGStylable.class,SVGLangSpace.class,SVGURIReference.class,SVGExternalResourcesRequired.class})
public class SVGFilterElement extends SVGElement {
    public SVGAnimatedInteger filterResX;
    public SVGAnimatedInteger filterResY;
    public SVGAnimatedEnumeration filterUnits;
    public SVGAnimatedLength height;
    public SVGAnimatedEnumeration primitiveUnits;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void setFilterRes(double filterResX, double filterResY);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGFilterElement prototype;
    public SVGFilterElement(){}
    public static double SVG_UNIT_TYPE_OBJECTBOUNDINGBOX;
    public static double SVG_UNIT_TYPE_UNKNOWN;
    public static double SVG_UNIT_TYPE_USERSPACEONUSE;
    public SVGAnimatedString className;
    public CSSStyleDeclaration style;
    public java.lang.String xmllang;
    public java.lang.String xmlspace;
    public SVGAnimatedString href;
    public SVGAnimatedBoolean externalResourcesRequired;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

