package def.dom;
@jsweet.lang.Extends({SVGStylable.class,SVGLangSpace.class,SVGExternalResourcesRequired.class,SVGFitToViewBox.class})
public class SVGSymbolElement extends SVGElement {
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGSymbolElement prototype;
    public SVGSymbolElement(){}
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

