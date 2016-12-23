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
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGFilterElement prototype;
    public SVGFilterElement(){}
    public static double SVG_UNIT_TYPE_OBJECTBOUNDINGBOX;
    public static double SVG_UNIT_TYPE_UNKNOWN;
    public static double SVG_UNIT_TYPE_USERSPACEONUSE;
    public Object className;
    public CSSStyleDeclaration style;
    public String xmllang;
    public String xmlspace;
    public SVGAnimatedString href;
    public SVGAnimatedBoolean externalResourcesRequired;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

