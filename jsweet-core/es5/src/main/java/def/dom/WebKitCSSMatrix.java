package def.dom;
public class WebKitCSSMatrix extends def.js.Object {
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
    native public WebKitCSSMatrix inverse();
    native public WebKitCSSMatrix multiply(WebKitCSSMatrix secondMatrix);
    native public WebKitCSSMatrix rotate(double angleX, double angleY, double angleZ);
    native public WebKitCSSMatrix rotateAxisAngle(double x, double y, double z, double angle);
    native public WebKitCSSMatrix scale(double scaleX, double scaleY, double scaleZ);
    native public void setMatrixValue(String value);
    native public WebKitCSSMatrix skewX(double angle);
    native public WebKitCSSMatrix skewY(double angle);
    native public String toString();
    native public WebKitCSSMatrix translate(double x, double y, double z);
    public static WebKitCSSMatrix prototype;
    public WebKitCSSMatrix(String text){}
    native public WebKitCSSMatrix rotate(double angleX, double angleY);
    native public WebKitCSSMatrix rotate(double angleX);
    native public WebKitCSSMatrix scale(double scaleX, double scaleY);
    native public WebKitCSSMatrix scale(double scaleX);
    native public WebKitCSSMatrix translate(double x, double y);
    public WebKitCSSMatrix(){}
}

