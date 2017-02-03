package def.dom;

@jsweet.lang.Extends({SVGFilterPrimitiveStandardAttributes.class})
public class SVGFECompositeElement extends SVGElement {
    public SVGAnimatedString in1;
    public SVGAnimatedString in2;
    public SVGAnimatedNumber k1;
    public SVGAnimatedNumber k2;
    public SVGAnimatedNumber k3;
    public SVGAnimatedNumber k4;
    public SVGAnimatedEnumeration operator;
    public double SVG_FECOMPOSITE_OPERATOR_ARITHMETIC;
    public double SVG_FECOMPOSITE_OPERATOR_ATOP;
    public double SVG_FECOMPOSITE_OPERATOR_IN;
    public double SVG_FECOMPOSITE_OPERATOR_OUT;
    public double SVG_FECOMPOSITE_OPERATOR_OVER;
    public double SVG_FECOMPOSITE_OPERATOR_UNKNOWN;
    public double SVG_FECOMPOSITE_OPERATOR_XOR;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGFECompositeElement prototype;
    public SVGFECompositeElement(){}
    public SVGAnimatedLength height;
    public SVGAnimatedString result;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

