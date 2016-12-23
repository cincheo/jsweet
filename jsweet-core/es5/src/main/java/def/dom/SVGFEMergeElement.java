package def.dom;
@jsweet.lang.Extends({SVGFilterPrimitiveStandardAttributes.class})
public class SVGFEMergeElement extends SVGElement {
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGFEMergeElement prototype;
    public SVGFEMergeElement(){}
    public SVGAnimatedLength height;
    public SVGAnimatedString result;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

