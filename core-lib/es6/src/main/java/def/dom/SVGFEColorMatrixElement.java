package def.dom;

@jsweet.lang.Extends({SVGFilterPrimitiveStandardAttributes.class})
public class SVGFEColorMatrixElement extends SVGElement {
    public SVGAnimatedString in1;
    public SVGAnimatedEnumeration type;
    public SVGAnimatedNumberList values;
    public double SVG_FECOLORMATRIX_TYPE_HUEROTATE;
    public double SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA;
    public double SVG_FECOLORMATRIX_TYPE_MATRIX;
    public double SVG_FECOLORMATRIX_TYPE_SATURATE;
    public double SVG_FECOLORMATRIX_TYPE_UNKNOWN;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGFEColorMatrixElement prototype;
    public SVGFEColorMatrixElement(){}
    public SVGAnimatedLength height;
    public SVGAnimatedString result;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

