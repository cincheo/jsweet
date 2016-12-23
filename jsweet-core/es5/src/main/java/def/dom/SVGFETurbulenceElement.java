package def.dom;
@jsweet.lang.Extends({SVGFilterPrimitiveStandardAttributes.class})
public class SVGFETurbulenceElement extends SVGElement {
    public SVGAnimatedNumber baseFrequencyX;
    public SVGAnimatedNumber baseFrequencyY;
    public SVGAnimatedInteger numOctaves;
    public SVGAnimatedNumber seed;
    public SVGAnimatedEnumeration stitchTiles;
    public SVGAnimatedEnumeration type;
    public double SVG_STITCHTYPE_NOSTITCH;
    public double SVG_STITCHTYPE_STITCH;
    public double SVG_STITCHTYPE_UNKNOWN;
    public double SVG_TURBULENCE_TYPE_FRACTALNOISE;
    public double SVG_TURBULENCE_TYPE_TURBULENCE;
    public double SVG_TURBULENCE_TYPE_UNKNOWN;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGFETurbulenceElement prototype;
    public SVGFETurbulenceElement(){}
    public SVGAnimatedLength height;
    public SVGAnimatedString result;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

