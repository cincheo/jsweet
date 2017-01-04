package def.js;
@jsweet.lang.Interface
public abstract class Math extends def.js.Object {
    /** The mathematical constant e. This is Euler's number, the base of natural logarithms. */
    public static final double E=0;
    /** The natural logarithm of 10. */
    public static final double LN10=0;
    /** The natural logarithm of 2. */
    public static final double LN2=0;
    /** The base-2 logarithm of e. */
    public static final double LOG2E=0;
    /** The base-10 logarithm of e. */
    public static final double LOG10E=0;
    /** Pi. This is the ratio of the circumference of a circle to its diameter. */
    public static final double PI=0;
    /** The square root of 0.5, or, equivalently, one divided by the square root of 2. */
    public static final double SQRT1_2=0;
    /** The square root of 2. */
    public static final double SQRT2=0;
    /**
      * Returns the absolute value of a number (the value without regard to whether it is positive or negative). 
      * For example, the absolute value of -5 is the same as the absolute value of 5.
      * @param x A numeric expression for which the absolute value is needed.
      */
    native public static double abs(double x);
    /**
      * Returns the arc cosine (or inverse cosine) of a number. 
      * @param x A numeric expression.
      */
    native public static double acos(double x);
    /** 
      * Returns the arcsine of a number. 
      * @param x A numeric expression.
      */
    native public static double asin(double x);
    /**
      * Returns the arctangent of a number. 
      * @param x A numeric expression for which the arctangent is needed.
      */
    native public static double atan(double x);
    /**
      * Returns the angle (in radians) from the X axis to a point.
      * @param y A numeric expression representing the cartesian y-coordinate.
      * @param x A numeric expression representing the cartesian x-coordinate.
      */
    native public static double atan2(double y, double x);
    /**
      * Returns the smallest number greater than or equal to its numeric argument. 
      * @param x A numeric expression.
      */
    native public static double ceil(double x);
    /**
      * Returns the cosine of a number. 
      * @param x A numeric expression that contains an angle measured in radians.
      */
    native public static double cos(double x);
    /**
      * Returns e (the base of natural logarithms) raised to a power. 
      * @param x A numeric expression representing the power of e.
      */
    native public static double exp(double x);
    /**
      * Returns the greatest number less than or equal to its numeric argument. 
      * @param x A numeric expression.
      */
    native public static double floor(double x);
    /**
      * Returns the natural logarithm (base e) of a number. 
      * @param x A numeric expression.
      */
    native public static double log(double x);
    /**
      * Returns the larger of a set of supplied numeric expressions. 
      * @param values Numeric expressions to be evaluated.
      */
    native public static double max(double... values);
    /**
      * Returns the smaller of a set of supplied numeric expressions. 
      * @param values Numeric expressions to be evaluated.
      */
    native public static double min(double... values);
    /**
      * Returns the value of a base expression taken to a specified power. 
      * @param x The base value of the expression.
      * @param y The exponent value of the expression.
      */
    native public static double pow(double x, double y);
    /** Returns a pseudorandom number between 0 and 1. */
    native public static double random();
    /** 
      * Returns a supplied numeric expression rounded to the nearest number.
      * @param x The value to be rounded to the nearest number.
      */
    native public static double round(double x);
    /**
      * Returns the sine of a number.
      * @param x A numeric expression that contains an angle measured in radians.
      */
    native public static double sin(double x);
    /**
      * Returns the square root of a number.
      * @param x A numeric expression.
      */
    native public static double sqrt(double x);
    /**
      * Returns the tangent of a number.
      * @param x A numeric expression that contains an angle measured in radians.
      */
    native public static double tan(double x);
}

