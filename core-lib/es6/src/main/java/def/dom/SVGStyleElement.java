package def.dom;

@jsweet.lang.Extends({SVGLangSpace.class})
public class SVGStyleElement extends SVGElement {
    public java.lang.String media;
    public java.lang.String title;
    public java.lang.String type;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGStyleElement prototype;
    public SVGStyleElement(){}
    public java.lang.String xmllang;
    public java.lang.String xmlspace;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

