package def.dom;

import def.js.Object;

public class SVGLength extends def.js.Object {
    public double unitType;
    public double value;
    public java.lang.String valueAsString;
    public double valueInSpecifiedUnits;
    native public void convertToSpecifiedUnits(double unitType);
    native public void newValueSpecifiedUnits(double unitType, double valueInSpecifiedUnits);
    public double SVG_LENGTHTYPE_CM;
    public double SVG_LENGTHTYPE_EMS;
    public double SVG_LENGTHTYPE_EXS;
    public double SVG_LENGTHTYPE_IN;
    public double SVG_LENGTHTYPE_MM;
    public double SVG_LENGTHTYPE_NUMBER;
    public double SVG_LENGTHTYPE_PC;
    public double SVG_LENGTHTYPE_PERCENTAGE;
    public double SVG_LENGTHTYPE_PT;
    public double SVG_LENGTHTYPE_PX;
    public double SVG_LENGTHTYPE_UNKNOWN;
    public static SVGLength prototype;
    public SVGLength(){}
}

