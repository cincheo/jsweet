package def.dom;
public class SVGAngle extends def.js.Object {
    public double unitType;
    public double value;
    public String valueAsString;
    public double valueInSpecifiedUnits;
    native public void convertToSpecifiedUnits(double unitType);
    native public void newValueSpecifiedUnits(double unitType, double valueInSpecifiedUnits);
    public double SVG_ANGLETYPE_DEG;
    public double SVG_ANGLETYPE_GRAD;
    public double SVG_ANGLETYPE_RAD;
    public double SVG_ANGLETYPE_UNKNOWN;
    public double SVG_ANGLETYPE_UNSPECIFIED;
    public static SVGAngle prototype;
    public SVGAngle(){}
}

