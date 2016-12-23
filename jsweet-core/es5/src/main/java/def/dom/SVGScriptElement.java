package def.dom;
@jsweet.lang.Extends({SVGExternalResourcesRequired.class,SVGURIReference.class})
public class SVGScriptElement extends SVGElement {
    public String type;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGScriptElement prototype;
    public SVGScriptElement(){}
    public SVGAnimatedBoolean externalResourcesRequired;
    public SVGAnimatedString href;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

