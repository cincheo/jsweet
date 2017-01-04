package def.dom;
@jsweet.lang.Extends({SVGLangSpace.class})
public class SVGStyleElement extends SVGElement {
    public String media;
    public String title;
    public String type;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGStyleElement prototype;
    public SVGStyleElement(){}
    public String xmllang;
    public String xmlspace;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

