package def.dom;

@jsweet.lang.Extends({SVGFilterPrimitiveStandardAttributes.class})
public class SVGFEDisplacementMapElement extends SVGElement {
    public SVGAnimatedString in1;
    public SVGAnimatedString in2;
    public SVGAnimatedNumber scale;
    public SVGAnimatedEnumeration xChannelSelector;
    public SVGAnimatedEnumeration yChannelSelector;
    public double SVG_CHANNEL_A;
    public double SVG_CHANNEL_B;
    public double SVG_CHANNEL_G;
    public double SVG_CHANNEL_R;
    public double SVG_CHANNEL_UNKNOWN;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGFEDisplacementMapElement prototype;
    public SVGFEDisplacementMapElement(){}
    public SVGAnimatedLength height;
    public SVGAnimatedString result;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

