package def.dom;
@jsweet.lang.Extends({SVGFilterPrimitiveStandardAttributes.class})
public class SVGFEConvolveMatrixElement extends SVGElement {
    public SVGAnimatedNumber bias;
    public SVGAnimatedNumber divisor;
    public SVGAnimatedEnumeration edgeMode;
    public SVGAnimatedString in1;
    public SVGAnimatedNumberList kernelMatrix;
    public SVGAnimatedNumber kernelUnitLengthX;
    public SVGAnimatedNumber kernelUnitLengthY;
    public SVGAnimatedInteger orderX;
    public SVGAnimatedInteger orderY;
    public SVGAnimatedBoolean preserveAlpha;
    public SVGAnimatedInteger targetX;
    public SVGAnimatedInteger targetY;
    public double SVG_EDGEMODE_DUPLICATE;
    public double SVG_EDGEMODE_NONE;
    public double SVG_EDGEMODE_UNKNOWN;
    public double SVG_EDGEMODE_WRAP;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGFEConvolveMatrixElement prototype;
    public SVGFEConvolveMatrixElement(){}
    public SVGAnimatedLength height;
    public SVGAnimatedString result;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

