package def.dom;
@jsweet.lang.Extends({SVGFilterPrimitiveStandardAttributes.class})
public class SVGFEBlendElement extends SVGElement {
    public SVGAnimatedString in1;
    public SVGAnimatedString in2;
    public SVGAnimatedEnumeration mode;
    public double SVG_FEBLEND_MODE_COLOR;
    public double SVG_FEBLEND_MODE_COLOR_BURN;
    public double SVG_FEBLEND_MODE_COLOR_DODGE;
    public double SVG_FEBLEND_MODE_DARKEN;
    public double SVG_FEBLEND_MODE_DIFFERENCE;
    public double SVG_FEBLEND_MODE_EXCLUSION;
    public double SVG_FEBLEND_MODE_HARD_LIGHT;
    public double SVG_FEBLEND_MODE_HUE;
    public double SVG_FEBLEND_MODE_LIGHTEN;
    public double SVG_FEBLEND_MODE_LUMINOSITY;
    public double SVG_FEBLEND_MODE_MULTIPLY;
    public double SVG_FEBLEND_MODE_NORMAL;
    public double SVG_FEBLEND_MODE_OVERLAY;
    public double SVG_FEBLEND_MODE_SATURATION;
    public double SVG_FEBLEND_MODE_SCREEN;
    public double SVG_FEBLEND_MODE_SOFT_LIGHT;
    public double SVG_FEBLEND_MODE_UNKNOWN;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGFEBlendElement prototype;
    public SVGFEBlendElement(){}
    public SVGAnimatedLength height;
    public SVGAnimatedString result;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

