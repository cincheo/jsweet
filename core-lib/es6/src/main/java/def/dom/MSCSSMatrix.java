package def.dom;

import def.js.Object;

public class MSCSSMatrix extends def.js.Object {
    public double a;
    public double b;
    public double c;
    public double d;
    public double e;
    public double f;
    public double m11;
    public double m12;
    public double m13;
    public double m14;
    public double m21;
    public double m22;
    public double m23;
    public double m24;
    public double m31;
    public double m32;
    public double m33;
    public double m34;
    public double m41;
    public double m42;
    public double m43;
    public double m44;
    native public MSCSSMatrix inverse();
    native public MSCSSMatrix multiply(MSCSSMatrix secondMatrix);
    native public MSCSSMatrix rotate(double angleX, double angleY, double angleZ);
    native public MSCSSMatrix rotateAxisAngle(double x, double y, double z, double angle);
    native public MSCSSMatrix scale(double scaleX, double scaleY, double scaleZ);
    native public void setMatrixValue(java.lang.String value);
    native public MSCSSMatrix skewX(double angle);
    native public MSCSSMatrix skewY(double angle);
    native public java.lang.String toString();
    native public MSCSSMatrix translate(double x, double y, double z);
    public static MSCSSMatrix prototype;
    public MSCSSMatrix(java.lang.String text){}
    native public MSCSSMatrix rotate(double angleX, double angleY);
    native public MSCSSMatrix rotate(double angleX);
    native public MSCSSMatrix scale(double scaleX, double scaleY);
    native public MSCSSMatrix scale(double scaleX);
    native public MSCSSMatrix translate(double x, double y);
    public MSCSSMatrix(){}
}

