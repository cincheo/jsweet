package def.dom;
@jsweet.lang.Extends({SVGStylable.class,SVGTests.class,SVGLangSpace.class,SVGExternalResourcesRequired.class,SVGUnitTypes.class})
public class SVGMaskElement extends SVGElement {
    public SVGAnimatedLength height;
    public SVGAnimatedEnumeration maskContentUnits;
    public SVGAnimatedEnumeration maskUnits;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGMaskElement prototype;
    public SVGMaskElement(){}
    public Object className;
    public CSSStyleDeclaration style;
    public SVGStringList requiredExtensions;
    public SVGStringList requiredFeatures;
    public SVGStringList systemLanguage;
    native public Boolean hasExtension(String extension);
    public String xmllang;
    public String xmlspace;
    public SVGAnimatedBoolean externalResourcesRequired;
    public static double SVG_UNIT_TYPE_OBJECTBOUNDINGBOX;
    public static double SVG_UNIT_TYPE_UNKNOWN;
    public static double SVG_UNIT_TYPE_USERSPACEONUSE;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

