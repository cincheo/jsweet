package def.dom;
@jsweet.lang.Extends({SVGStylable.class,SVGTransformable.class,SVGTests.class,SVGLangSpace.class,SVGExternalResourcesRequired.class})
public class SVGLineElement extends SVGElement {
    public SVGAnimatedLength x1;
    public SVGAnimatedLength x2;
    public SVGAnimatedLength y1;
    public SVGAnimatedLength y2;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGLineElement prototype;
    public SVGLineElement(){}
    public Object className;
    public CSSStyleDeclaration style;
    public SVGAnimatedTransformList transform;
    public SVGStringList requiredExtensions;
    public SVGStringList requiredFeatures;
    public SVGStringList systemLanguage;
    native public Boolean hasExtension(String extension);
    public String xmllang;
    public String xmlspace;
    public SVGAnimatedBoolean externalResourcesRequired;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

