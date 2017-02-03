package def.dom;

@jsweet.lang.Extends({SVGStylable.class,SVGTests.class,SVGLangSpace.class,SVGExternalResourcesRequired.class})
public class SVGTextContentElement extends SVGElement {
    public SVGAnimatedEnumeration lengthAdjust;
    public SVGAnimatedLength textLength;
    native public double getCharNumAtPosition(SVGPoint point);
    native public double getComputedTextLength();
    native public SVGPoint getEndPositionOfChar(double charnum);
    native public SVGRect getExtentOfChar(double charnum);
    native public double getNumberOfChars();
    native public double getRotationOfChar(double charnum);
    native public SVGPoint getStartPositionOfChar(double charnum);
    native public double getSubStringLength(double charnum, double nchars);
    native public void selectSubString(double charnum, double nchars);
    public double LENGTHADJUST_SPACING;
    public double LENGTHADJUST_SPACINGANDGLYPHS;
    public double LENGTHADJUST_UNKNOWN;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGTextContentElement prototype;
    public SVGTextContentElement(){}
    public SVGAnimatedString className;
    public CSSStyleDeclaration style;
    public SVGStringList requiredExtensions;
    public SVGStringList requiredFeatures;
    public SVGStringList systemLanguage;
    native public java.lang.Boolean hasExtension(java.lang.String extension);
    public java.lang.String xmllang;
    public java.lang.String xmlspace;
    public SVGAnimatedBoolean externalResourcesRequired;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

