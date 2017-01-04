package def.dom;
@jsweet.lang.Extends({SVGStylable.class,SVGLangSpace.class})
public class SVGTitleElement extends SVGElement {
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGTitleElement prototype;
    public SVGTitleElement(){}
    public Object className;
    public CSSStyleDeclaration style;
    public String xmllang;
    public String xmlspace;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

