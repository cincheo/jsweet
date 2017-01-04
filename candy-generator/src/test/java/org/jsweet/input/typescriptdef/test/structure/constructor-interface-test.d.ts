
export namespace test {

    interface Math {
        /**
          * Returns the absolute value of a number (the value without regard to whether it is positive or negative). 
          * For example, the absolute value of -5 is the same as the absolute value of 5.
          * @param x A numeric expression for which the absolute value is needed.
          */
        abs(x: number): number;
    }
    /** An intrinsic object that provides basic mathematics functionality and constants. */
    declare const Math: Math;

}