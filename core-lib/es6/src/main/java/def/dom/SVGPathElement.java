package def.dom;

@jsweet.lang.Extends({SVGStylable.class,SVGTransformable.class,SVGTests.class,SVGLangSpace.class,SVGExternalResourcesRequired.class,SVGAnimatedPathData.class})
public class SVGPathElement extends SVGElement {
    native public SVGPathSegArcAbs createSVGPathSegArcAbs(double x, double y, double r1, double r2, double angle, java.lang.Boolean largeArcFlag, java.lang.Boolean sweepFlag);
    native public SVGPathSegArcRel createSVGPathSegArcRel(double x, double y, double r1, double r2, double angle, java.lang.Boolean largeArcFlag, java.lang.Boolean sweepFlag);
    native public SVGPathSegClosePath createSVGPathSegClosePath();
    native public SVGPathSegCurvetoCubicAbs createSVGPathSegCurvetoCubicAbs(double x, double y, double x1, double y1, double x2, double y2);
    native public SVGPathSegCurvetoCubicRel createSVGPathSegCurvetoCubicRel(double x, double y, double x1, double y1, double x2, double y2);
    native public SVGPathSegCurvetoCubicSmoothAbs createSVGPathSegCurvetoCubicSmoothAbs(double x, double y, double x2, double y2);
    native public SVGPathSegCurvetoCubicSmoothRel createSVGPathSegCurvetoCubicSmoothRel(double x, double y, double x2, double y2);
    native public SVGPathSegCurvetoQuadraticAbs createSVGPathSegCurvetoQuadraticAbs(double x, double y, double x1, double y1);
    native public SVGPathSegCurvetoQuadraticRel createSVGPathSegCurvetoQuadraticRel(double x, double y, double x1, double y1);
    native public SVGPathSegCurvetoQuadraticSmoothAbs createSVGPathSegCurvetoQuadraticSmoothAbs(double x, double y);
    native public SVGPathSegCurvetoQuadraticSmoothRel createSVGPathSegCurvetoQuadraticSmoothRel(double x, double y);
    native public SVGPathSegLinetoAbs createSVGPathSegLinetoAbs(double x, double y);
    native public SVGPathSegLinetoHorizontalAbs createSVGPathSegLinetoHorizontalAbs(double x);
    native public SVGPathSegLinetoHorizontalRel createSVGPathSegLinetoHorizontalRel(double x);
    native public SVGPathSegLinetoRel createSVGPathSegLinetoRel(double x, double y);
    native public SVGPathSegLinetoVerticalAbs createSVGPathSegLinetoVerticalAbs(double y);
    native public SVGPathSegLinetoVerticalRel createSVGPathSegLinetoVerticalRel(double y);
    native public SVGPathSegMovetoAbs createSVGPathSegMovetoAbs(double x, double y);
    native public SVGPathSegMovetoRel createSVGPathSegMovetoRel(double x, double y);
    native public double getPathSegAtLength(double distance);
    native public SVGPoint getPointAtLength(double distance);
    native public double getTotalLength();
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static SVGPathElement prototype;
    public SVGPathElement(){}
    public SVGAnimatedString className;
    public CSSStyleDeclaration style;
    public SVGAnimatedTransformList transform;
    public SVGStringList requiredExtensions;
    public SVGStringList requiredFeatures;
    public SVGStringList systemLanguage;
    native public java.lang.Boolean hasExtension(java.lang.String extension);
    public java.lang.String xmllang;
    public java.lang.String xmlspace;
    public SVGAnimatedBoolean externalResourcesRequired;
    public SVGPathSegList pathSegList;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

