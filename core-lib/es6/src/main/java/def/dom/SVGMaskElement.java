package def.dom;

@jsweet.lang.Extends({SVGStylable.class,SVGTests.class,SVGLangSpace.class,SVGExternalResourcesRequired.class,SVGUnitTypes.class})
public class SVGMaskElement extends SVGElement {
    public SVGAnimatedLength height;
    public SVGAnimatedEnumeration maskContentUnits;
    public SVGAnimatedEnumeration maskUnits;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGMaskElement prototype;
    public SVGMaskElement(){}
    public SVGAnimatedString className;
    public CSSStyleDeclaration style;
    public SVGStringList requiredExtensions;
    public SVGStringList requiredFeatures;
    public SVGStringList systemLanguage;
    native public java.lang.Boolean hasExtension(java.lang.String extension);
    public java.lang.String xmllang;
    public java.lang.String xmlspace;
    public SVGAnimatedBoolean externalResourcesRequired;
    public static double SVG_UNIT_TYPE_OBJECTBOUNDINGBOX;
    public static double SVG_UNIT_TYPE_UNKNOWN;
    public static double SVG_UNIT_TYPE_USERSPACEONUSE;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

