package def.dom;

@jsweet.lang.Extends({SVGStylable.class,SVGLangSpace.class})
public class SVGTitleElement extends SVGElement {
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGTitleElement prototype;
    public SVGTitleElement(){}
    public SVGAnimatedString className;
    public CSSStyleDeclaration style;
    public java.lang.String xmllang;
    public java.lang.String xmlspace;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

