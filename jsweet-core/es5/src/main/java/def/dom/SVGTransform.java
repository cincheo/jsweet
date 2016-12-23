package def.dom;
public class SVGTransform extends def.js.Object {
    public double angle;
    public SVGMatrix matrix;
    public double type;
    native public void setMatrix(SVGMatrix matrix);
    native public void setRotate(double angle, double cx, double cy);
    native public void setScale(double sx, double sy);
    native public void setSkewX(double angle);
    native public void setSkewY(double angle);
    native public void setTranslate(double tx, double ty);
    public double SVG_TRANSFORM_MATRIX;
    public double SVG_TRANSFORM_ROTATE;
    public double SVG_TRANSFORM_SCALE;
    public double SVG_TRANSFORM_SKEWX;
    public double SVG_TRANSFORM_SKEWY;
    public double SVG_TRANSFORM_TRANSLATE;
    public double SVG_TRANSFORM_UNKNOWN;
    public static SVGTransform prototype;
    public SVGTransform(){}
}

