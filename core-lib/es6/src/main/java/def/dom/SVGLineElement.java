package def.dom;

@jsweet.lang.Extends({SVGStylable.class,SVGTransformable.class,SVGTests.class,SVGLangSpace.class,SVGExternalResourcesRequired.class})
public class SVGLineElement extends SVGElement {
    public SVGAnimatedLength x1;
    public SVGAnimatedLength x2;
    public SVGAnimatedLength y1;
    public SVGAnimatedLength y2;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGLineElement prototype;
    public SVGLineElement(){}
    public SVGAnimatedString className;
    public CSSStyleDeclaration style;
    public SVGAnimatedTransformList transform;
    public SVGStringList requiredExtensions;
    public SVGStringList requiredFeatures;
    public SVGStringList systemLanguage;
    native public java.lang.Boolean hasExtension(java.lang.String extension);
    public java.lang.String xmllang;
    public java.lang.String xmlspace;
    public SVGAnimatedBoolean externalResourcesRequired;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

