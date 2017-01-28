package def.dom;

@jsweet.lang.Extends({SVGFilterPrimitiveStandardAttributes.class,SVGLangSpace.class,SVGURIReference.class,SVGExternalResourcesRequired.class})
public class SVGFEImageElement extends SVGElement {
    public SVGAnimatedPreserveAspectRatio preserveAspectRatio;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGFEImageElement prototype;
    public SVGFEImageElement(){}
    public SVGAnimatedLength height;
    public SVGAnimatedString result;
    public SVGAnimatedLength width;
    public SVGAnimatedLength x;
    public SVGAnimatedLength y;
    public java.lang.String xmllang;
    public java.lang.String xmlspace;
    public SVGAnimatedString href;
    public SVGAnimatedBoolean externalResourcesRequired;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

