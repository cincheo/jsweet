package def.dom;
@jsweet.lang.Extends({SVGFilterPrimitiveStandardAttributes.class})
public class SVGFEGaussianBlurElement extends SVGElement {
    public SVGAnimatedString in1;
    public SVGAnimatedNumber stdDeviationX;
    public SVGAnimatedNumber stdDeviationY;
    native public void setStdDeviation(double stdDeviationX, double stdDeviationY);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGFEGaussianBlurElement prototype;
    public SVGFEGaussianBlurElement(){}
    public SVGAnimatedLength height;
    public SVGAnimatedString result;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

