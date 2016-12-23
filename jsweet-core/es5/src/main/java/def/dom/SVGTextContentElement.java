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
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static SVGTextContentElement prototype;
    public SVGTextContentElement(){}
    public Object className;
    public CSSStyleDeclaration style;
    public SVGStringList requiredExtensions;
    public SVGStringList requiredFeatures;
    public SVGStringList systemLanguage;
    native public Boolean hasExtension(String extension);
    public String xmllang;
    public String xmlspace;
    public SVGAnimatedBoolean externalResourcesRequired;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

