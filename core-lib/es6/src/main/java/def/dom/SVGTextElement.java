package def.dom;

@jsweet.lang.Extends({SVGTransformable.class})
public class SVGTextElement extends SVGTextPositioningElement {
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGTextElement prototype;
    public SVGTextElement(){}
    public SVGAnimatedTransformList transform;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

