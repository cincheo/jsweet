package def.dom;
public class SVGMatrix extends def.js.Object {
    public double a;
    public double b;
    public double c;
    public double d;
    public double e;
    public double f;
    native public SVGMatrix flipX();
    native public SVGMatrix flipY();
    native public SVGMatrix inverse();
    native public SVGMatrix multiply(SVGMatrix secondMatrix);
    native public SVGMatrix rotate(double angle);
    native public SVGMatrix rotateFromVector(double x, double y);
    native public SVGMatrix scale(double scaleFactor);
    native public SVGMatrix scaleNonUniform(double scaleFactorX, double scaleFactorY);
    native public SVGMatrix skewX(double angle);
    native public SVGMatrix skewY(double angle);
    native public SVGMatrix translate(double x, double y);
    public static SVGMatrix prototype;
    public SVGMatrix(){}
}

