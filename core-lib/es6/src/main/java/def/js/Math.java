package def.js;
@jsweet.lang.Interface
public abstract class Math extends def.js.Object {
    /** The mathematical constant e. This is Euler's number, the base of natural logarithms. */
    public static double E;
    /** The natural logarithm of 10. */
    public static double LN10;
    /** The natural logarithm of 2. */
    public static double LN2;
    /** The base-2 logarithm of e. */
    public static double LOG2E;
    /** The base-10 logarithm of e. */
    public static double LOG10E;
    /** Pi. This is the ratio of the circumference of a circle to its diameter. */
    public static double PI;
    /** The square root of 0.5, or, equivalently, one divided by the square root of 2. */
    public static double SQRT1_2;
    /** The square root of 2. */
    public static double SQRT2;
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
    /**
      * Returns the number of leading zero bits in the 32-bit binary representation of a number.
      * @param x A numeric expression.
      */
    native public static double clz32(double x);
    /**
      * Returns the result of 32-bit multiplication of two numbers.
      * @param x First number
      * @param y Second number
      */
    native public static double imul(double x, double y);
    /**
      * Returns the sign of the x, indicating whether x is positive, negative or zero.
      * @param x The numeric expression to test
      */
    native public static double sign(double x);
    /**
      * Returns the base 10 logarithm of a number.
      * @param x A numeric expression.
      */
    native public static double log10(double x);
    /**
      * Returns the base 2 logarithm of a number.
      * @param x A numeric expression.
      */
    native public static double log2(double x);
    /**
      * Returns the natural logarithm of 1 + x.
      * @param x A numeric expression.
      */
    native public static double log1p(double x);
    /**
      * Returns the result of (e^x - 1) of x (e raised to the power of x, where e is the base of 
      * the natural logarithms).
      * @param x A numeric expression.
      */
    native public static double expm1(double x);
    /**
      * Returns the hyperbolic cosine of a number.
      * @param x A numeric expression that contains an angle measured in radians.
      */
    native public static double cosh(double x);
    /**
      * Returns the hyperbolic sine of a number.
      * @param x A numeric expression that contains an angle measured in radians.
      */
    native public static double sinh(double x);
    /**
      * Returns the hyperbolic tangent of a number.
      * @param x A numeric expression that contains an angle measured in radians.
      */
    native public static double tanh(double x);
    /**
      * Returns the inverse hyperbolic cosine of a number.
      * @param x A numeric expression that contains an angle measured in radians.
      */
    native public static double acosh(double x);
    /**
      * Returns the inverse hyperbolic sine of a number.
      * @param x A numeric expression that contains an angle measured in radians.
      */
    native public static double asinh(double x);
    /**
      * Returns the inverse hyperbolic tangent of a number.
      * @param x A numeric expression that contains an angle measured in radians.
      */
    native public static double atanh(double x);
    /**
      * Returns the square root of the sum of squares of its arguments.
      * @param values Values to compute the square root for.
      *     If no arguments are passed, the result is +0.
      *     If there is only one argument, the result is the absolute value.
      *     If any argument is +Infinity or -Infinity, the result is +Infinity.
      *     If any argument is NaN, the result is NaN.
      *     If all arguments are either +0 or −0, the result is +0.
      */
    native public static double hypot(double... values);
    /**
      * Returns the integral part of the a numeric expression, x, removing any fractional digits.
      * If x is already an integer, the result is x.
      * @param x A numeric expression.
      */
    native public static double trunc(double x);
    /**
      * Returns the nearest single precision float representation of a number.
      * @param x A numeric expression.
      */
    native public static double fround(double x);
    /**
      * Returns an implementation-dependent approximation to the cube root of number.
      * @param x A numeric expression.
      */
    native public static double cbrt(double x);
    native public static java.lang.String $getStatic(Symbol toStringTag);
}

