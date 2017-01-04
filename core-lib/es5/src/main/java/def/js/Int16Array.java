package def.js;
import jsweet.util.function.Function4;
/**
  * A typed array of 16-bit signed integer values. The contents are initialized to 0. If the 
  * requested number of bytes could not be allocated an exception is raised.
  */
public class Int16Array extends def.js.Object implements Iterable<java.lang.Double> {
    /**
      * The size in bytes of each element in the array. 
      */
    public final double BYTES_PER_ELEMENT=0;
    /**
      * The ArrayBuffer instance referenced by the array. 
      */
    public final ArrayBuffer buffer=null;
    /**
      * The length in bytes of the array.
      */
    public final double byteLength=0;
    /**
      * The offset in bytes of the array.
      */
    public final double byteOffset=0;
    /** 
      * Returns the this object after copying a section of the array identified by start and end
      * to the same array starting at position target
      * @param target If target is negative, it is treated as length+target where length is the 
      * length of the array. 
      * @param start If start is negative, it is treated as length+start. If end is negative, it 
      * is treated as length+end.
      * @param end If not specified, length of the this object is used as its default value. 
      */
    native public Int16Array copyWithin(double target, double start, double end);
    /**
      * Determines whether all the members of an array satisfy the specified test.
      * @param callbackfn A function that accepts up to three arguments. The every method calls 
      * the callbackfn function for each element in array1 until the callbackfn returns false, 
      * or until the end of the array.
      * @param thisArg An object to which the this keyword can refer in the callbackfn function.
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public java.lang.Boolean every(jsweet.util.function.TriFunction<Double,Double,Int16Array,java.lang.Boolean> callbackfn, java.lang.Object thisArg);
    /**
        * Returns the this object after filling the section identified by start and end with value
        * @param value value to fill array section with
        * @param start index to start filling the array at. If start is negative, it is treated as 
        * length+start where length is the length of the array. 
        * @param end index to stop filling the array at. If end is negative, it is treated as 
        * length+end.
        */
    native public Int16Array fill(double value, double start, double end);
    /**
      * Returns the elements of an array that meet the condition specified in a callback function. 
      * @param callbackfn A function that accepts up to three arguments. The filter method calls 
      * the callbackfn function one time for each element in the array. 
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. 
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public Int16Array filter(jsweet.util.function.TriFunction<Double,Double,Int16Array,java.lang.Boolean> callbackfn, java.lang.Object thisArg);
    /** 
      * Returns the value of the first element in the array where predicate is true, and undefined 
      * otherwise.
      * @param predicate find calls predicate once for each element of the array, in ascending 
      * order, until it finds one where predicate returns true. If such an element is found, find 
      * immediately returns that element value. Otherwise, find returns undefined.
      * @param thisArg If provided, it will be used as the this value for each invocation of 
      * predicate. If it is not provided, undefined is used instead.
      */
    native public double find(jsweet.util.function.TriFunction<Double,Double,Array<Double>,java.lang.Boolean> predicate, java.lang.Object thisArg);
    /** 
      * Returns the index of the first element in the array where predicate is true, and undefined 
      * otherwise.
      * @param predicate find calls predicate once for each element of the array, in ascending 
      * order, until it finds one where predicate returns true. If such an element is found, find 
      * immediately returns that element value. Otherwise, find returns undefined.
      * @param thisArg If provided, it will be used as the this value for each invocation of 
      * predicate. If it is not provided, undefined is used instead.
      */
    native public double findIndex(java.util.function.Function<Double,java.lang.Boolean> predicate, java.lang.Object thisArg);
    /**
      * Performs the specified action for each element in an array.
      * @param callbackfn  A function that accepts up to three arguments. forEach calls the 
      * callbackfn function one time for each element in the array. 
      * @param thisArg  An object to which the this keyword can refer in the callbackfn function. 
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public void forEach(jsweet.util.function.TriConsumer<Double,Double,Int16Array> callbackfn, java.lang.Object thisArg);
    /**
      * Returns the index of the first occurrence of a value in an array.
      * @param searchElement The value to locate in the array.
      * @param fromIndex The array index at which to begin the search. If fromIndex is omitted, the
      *  search starts at index 0.
      */
    native public double indexOf(double searchElement, double fromIndex);
    /**
      * Adds all the elements of an array separated by the specified separator string.
      * @param separator A string used to separate one element of an array from the next in the 
      * resulting String. If omitted, the array elements are separated with a comma.
      */
    native public java.lang.String join(java.lang.String separator);
    /**
      * Returns the index of the last occurrence of a value in an array.
      * @param searchElement The value to locate in the array.
      * @param fromIndex The array index at which to begin the search. If fromIndex is omitted, the 
      * search starts at index 0.
      */
    native public double lastIndexOf(double searchElement, double fromIndex);
    /**
      * The length of the array.
      */
    public final double length=0;
    /**
      * Calls a defined callback function on each element of an array, and returns an array that 
      * contains the results.
      * @param callbackfn A function that accepts up to three arguments. The map method calls the 
      * callbackfn function one time for each element in the array. 
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. 
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public Int16Array map(jsweet.util.function.TriFunction<Double,Double,Int16Array,Double> callbackfn, java.lang.Object thisArg);
    /**
      * Calls the specified callback function for all the elements in an array. The return value of 
      * the callback function is the accumulated result, and is provided as an argument in the next 
      * call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduce method calls the 
      * callbackfn function one time for each element in the array.
      * @param initialValue If initialValue is specified, it is used as the initial value to start 
      * the accumulation. The first call to the callbackfn function provides this value as an argument
      * instead of an array value.
      */
    native public double reduce(Function4<Double,Double,Double,Int16Array,Double> callbackfn, double initialValue);
    /**
      * Calls the specified callback function for all the elements in an array. The return value of 
      * the callback function is the accumulated result, and is provided as an argument in the next 
      * call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduce method calls the 
      * callbackfn function one time for each element in the array.
      * @param initialValue If initialValue is specified, it is used as the initial value to start 
      * the accumulation. The first call to the callbackfn function provides this value as an argument 
      * instead of an array value.
      */
    native public <U> U reduce(Function4<U,Double,Double,Int16Array,U> callbackfn, U initialValue);
    /** 
      * Calls the specified callback function for all the elements in an array, in descending order. 
      * The return value of the callback function is the accumulated result, and is provided as an 
      * argument in the next call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduceRight method calls 
      * the callbackfn function one time for each element in the array. 
      * @param initialValue If initialValue is specified, it is used as the initial value to start 
      * the accumulation. The first call to the callbackfn function provides this value as an 
      * argument instead of an array value.
      */
    native public double reduceRight(Function4<Double,Double,Double,Int16Array,Double> callbackfn, double initialValue);
    /** 
      * Calls the specified callback function for all the elements in an array, in descending order. 
      * The return value of the callback function is the accumulated result, and is provided as an 
      * argument in the next call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduceRight method calls
      * the callbackfn function one time for each element in the array. 
      * @param initialValue If initialValue is specified, it is used as the initial value to start 
      * the accumulation. The first call to the callbackfn function provides this value as an argument
      * instead of an array value.
      */
    native public <U> U reduceRight(Function4<U,Double,Double,Int16Array,U> callbackfn, U initialValue);
    /**
      * Reverses the elements in an Array. 
      */
    native public Int16Array reverse();
    /**
      * Sets a value or an array of values.
      * @param index The index of the location to set.
      * @param value The value to set.
      */
    native public void set(double index, double value);
    /**
      * Sets a value or an array of values.
      * @param array A typed or untyped array of values to set.
      * @param offset The index in the current array at which the values are to be written.
      */
    native public void set(ArrayLike<Double> array, double offset);
    /** 
      * Returns a section of an array.
      * @param start The beginning of the specified portion of the array.
      * @param end The end of the specified portion of the array.
      */
    native public Int16Array slice(double start, double end);
    /**
      * Determines whether the specified callback function returns true for any element of an array.
      * @param callbackfn A function that accepts up to three arguments. The some method calls the 
      * callbackfn function for each element in array1 until the callbackfn returns true, or until 
      * the end of the array.
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. 
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public java.lang.Boolean some(jsweet.util.function.TriFunction<Double,Double,Int16Array,java.lang.Boolean> callbackfn, java.lang.Object thisArg);
    /**
      * Sorts an array.
      * @param compareFn The name of the function used to determine the order of the elements. If 
      * omitted, the elements are sorted in ascending, ASCII character order.
      */
    native public Int16Array sort(java.util.function.BiFunction<Double,Double,Double> compareFn);
    /**
      * Gets a new Int16Array view of the ArrayBuffer store for this array, referencing the elements
      * at begin, inclusive, up to end, exclusive. 
      * @param begin The index of the beginning of the array.
      * @param end The index of the end of the array.
      */
    native public Int16Array subarray(double begin, double end);
    /**
      * Converts a number to a string by using the current locale. 
      */
    native public java.lang.String toLocaleString();
    /**
      * Returns a string representation of an array.
      */
    native public java.lang.String toString();
    native public java.lang.Double $get(double index);
    public static final Int16Array prototype=null;
    public Int16Array(double length){}
    public Int16Array(ArrayLike<Double> array){}
    public Int16Array(ArrayBuffer buffer, double byteOffset, double length){}
    /**
      * Returns a new array from a set of elements.
      * @param items A set of elements to include in the new array object.
      */
    native public static Int16Array of(double... items);
    /**
      * Creates an array from an array-like or iterable object.
      * @param arrayLike An array-like or iterable object to convert to an array.
      * @param mapfn A mapping function to call on every element of the array.
      * @param thisArg Value of 'this' used to invoke the mapfn.
      */
    native public static Int16Array from(ArrayLike<Double> arrayLike, java.util.function.BiFunction<Double,Double,Double> mapfn, java.lang.Object thisArg);
    /** 
      * Returns the this object after copying a section of the array identified by start and end
      * to the same array starting at position target
      * @param target If target is negative, it is treated as length+target where length is the 
      * length of the array. 
      * @param start If start is negative, it is treated as length+start. If end is negative, it 
      * is treated as length+end.
      * @param end If not specified, length of the this object is used as its default value. 
      */
    native public Int16Array copyWithin(double target, double start);
    /**
      * Determines whether all the members of an array satisfy the specified test.
      * @param callbackfn A function that accepts up to three arguments. The every method calls 
      * the callbackfn function for each element in array1 until the callbackfn returns false, 
      * or until the end of the array.
      * @param thisArg An object to which the this keyword can refer in the callbackfn function.
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public java.lang.Boolean every(jsweet.util.function.TriFunction<Double,Double,Int16Array,java.lang.Boolean> callbackfn);
    /**
        * Returns the this object after filling the section identified by start and end with value
        * @param value value to fill array section with
        * @param start index to start filling the array at. If start is negative, it is treated as 
        * length+start where length is the length of the array. 
        * @param end index to stop filling the array at. If end is negative, it is treated as 
        * length+end.
        */
    native public Int16Array fill(double value, double start);
    /**
        * Returns the this object after filling the section identified by start and end with value
        * @param value value to fill array section with
        * @param start index to start filling the array at. If start is negative, it is treated as 
        * length+start where length is the length of the array. 
        * @param end index to stop filling the array at. If end is negative, it is treated as 
        * length+end.
        */
    native public Int16Array fill(double value);
    /**
      * Returns the elements of an array that meet the condition specified in a callback function. 
      * @param callbackfn A function that accepts up to three arguments. The filter method calls 
      * the callbackfn function one time for each element in the array. 
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. 
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public Int16Array filter(jsweet.util.function.TriFunction<Double,Double,Int16Array,java.lang.Boolean> callbackfn);
    /** 
      * Returns the value of the first element in the array where predicate is true, and undefined 
      * otherwise.
      * @param predicate find calls predicate once for each element of the array, in ascending 
      * order, until it finds one where predicate returns true. If such an element is found, find 
      * immediately returns that element value. Otherwise, find returns undefined.
      * @param thisArg If provided, it will be used as the this value for each invocation of 
      * predicate. If it is not provided, undefined is used instead.
      */
    native public double find(jsweet.util.function.TriFunction<Double,Double,Array<Double>,java.lang.Boolean> predicate);
    /** 
      * Returns the index of the first element in the array where predicate is true, and undefined 
      * otherwise.
      * @param predicate find calls predicate once for each element of the array, in ascending 
      * order, until it finds one where predicate returns true. If such an element is found, find 
      * immediately returns that element value. Otherwise, find returns undefined.
      * @param thisArg If provided, it will be used as the this value for each invocation of 
      * predicate. If it is not provided, undefined is used instead.
      */
    native public double findIndex(java.util.function.Function<Double,java.lang.Boolean> predicate);
    /**
      * Performs the specified action for each element in an array.
      * @param callbackfn  A function that accepts up to three arguments. forEach calls the 
      * callbackfn function one time for each element in the array. 
      * @param thisArg  An object to which the this keyword can refer in the callbackfn function. 
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public void forEach(jsweet.util.function.TriConsumer<Double,Double,Int16Array> callbackfn);
    /**
      * Returns the index of the first occurrence of a value in an array.
      * @param searchElement The value to locate in the array.
      * @param fromIndex The array index at which to begin the search. If fromIndex is omitted, the
      *  search starts at index 0.
      */
    native public double indexOf(double searchElement);
    /**
      * Adds all the elements of an array separated by the specified separator string.
      * @param separator A string used to separate one element of an array from the next in the 
      * resulting String. If omitted, the array elements are separated with a comma.
      */
    native public java.lang.String join();
    /**
      * Returns the index of the last occurrence of a value in an array.
      * @param searchElement The value to locate in the array.
      * @param fromIndex The array index at which to begin the search. If fromIndex is omitted, the 
      * search starts at index 0.
      */
    native public double lastIndexOf(double searchElement);
    /**
      * Calls a defined callback function on each element of an array, and returns an array that 
      * contains the results.
      * @param callbackfn A function that accepts up to three arguments. The map method calls the 
      * callbackfn function one time for each element in the array. 
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. 
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public Int16Array map(jsweet.util.function.TriFunction<Double,Double,Int16Array,Double> callbackfn);
    /**
      * Calls the specified callback function for all the elements in an array. The return value of 
      * the callback function is the accumulated result, and is provided as an argument in the next 
      * call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduce method calls the 
      * callbackfn function one time for each element in the array.
      * @param initialValue If initialValue is specified, it is used as the initial value to start 
      * the accumulation. The first call to the callbackfn function provides this value as an argument
      * instead of an array value.
      */
    native public double reduce(Function4<Double,Double,Double,Int16Array,Double> callbackfn);
    /** 
      * Calls the specified callback function for all the elements in an array, in descending order. 
      * The return value of the callback function is the accumulated result, and is provided as an 
      * argument in the next call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduceRight method calls 
      * the callbackfn function one time for each element in the array. 
      * @param initialValue If initialValue is specified, it is used as the initial value to start 
      * the accumulation. The first call to the callbackfn function provides this value as an 
      * argument instead of an array value.
      */
    native public double reduceRight(Function4<Double,Double,Double,Int16Array,Double> callbackfn);
    /**
      * Sets a value or an array of values.
      * @param array A typed or untyped array of values to set.
      * @param offset The index in the current array at which the values are to be written.
      */
    native public void set(ArrayLike<Double> array);
    /** 
      * Returns a section of an array.
      * @param start The beginning of the specified portion of the array.
      * @param end The end of the specified portion of the array.
      */
    native public Int16Array slice(double start);
    /** 
      * Returns a section of an array.
      * @param start The beginning of the specified portion of the array.
      * @param end The end of the specified portion of the array.
      */
    native public Int16Array slice();
    /**
      * Determines whether the specified callback function returns true for any element of an array.
      * @param callbackfn A function that accepts up to three arguments. The some method calls the 
      * callbackfn function for each element in array1 until the callbackfn returns true, or until 
      * the end of the array.
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. 
      * If thisArg is omitted, undefined is used as the this value.
      */
    native public java.lang.Boolean some(jsweet.util.function.TriFunction<Double,Double,Int16Array,java.lang.Boolean> callbackfn);
    /**
      * Sorts an array.
      * @param compareFn The name of the function used to determine the order of the elements. If 
      * omitted, the elements are sorted in ascending, ASCII character order.
      */
    native public Int16Array sort();
    /**
      * Gets a new Int16Array view of the ArrayBuffer store for this array, referencing the elements
      * at begin, inclusive, up to end, exclusive. 
      * @param begin The index of the beginning of the array.
      * @param end The index of the end of the array.
      */
    native public Int16Array subarray(double begin);
    public Int16Array(ArrayBuffer buffer, double byteOffset){}
    public Int16Array(ArrayBuffer buffer){}
    /**
      * Creates an array from an array-like or iterable object.
      * @param arrayLike An array-like or iterable object to convert to an array.
      * @param mapfn A mapping function to call on every element of the array.
      * @param thisArg Value of 'this' used to invoke the mapfn.
      */
    native public static Int16Array from(ArrayLike<Double> arrayLike, java.util.function.BiFunction<Double,Double,Double> mapfn);
    /**
      * Creates an array from an array-like or iterable object.
      * @param arrayLike An array-like or iterable object to convert to an array.
      * @param mapfn A mapping function to call on every element of the array.
      * @param thisArg Value of 'this' used to invoke the mapfn.
      */
    native public static Int16Array from(ArrayLike<Double> arrayLike);
    /**
      * Sets a value or an array of values.
      * @param array A typed or untyped array of values to set.
      * @param offset The index in the current array at which the values are to be written.
      */
    native public void set(Double[] array, double offset);
    public Int16Array(Double[] array){}
    /**
      * Creates an array from an array-like or iterable object.
      * @param arrayLike An array-like or iterable object to convert to an array.
      * @param mapfn A mapping function to call on every element of the array.
      * @param thisArg Value of 'this' used to invoke the mapfn.
      */
    native public static Int16Array from(Double[] arrayLike, java.util.function.BiFunction<Double,Double,Double> mapfn, java.lang.Object thisArg);
    /**
      * Sets a value or an array of values.
      * @param array A typed or untyped array of values to set.
      * @param offset The index in the current array at which the values are to be written.
      */
    native public void set(Double[] array);
    /**
      * Creates an array from an array-like or iterable object.
      * @param arrayLike An array-like or iterable object to convert to an array.
      * @param mapfn A mapping function to call on every element of the array.
      * @param thisArg Value of 'this' used to invoke the mapfn.
      */
    native public static Int16Array from(Double[] arrayLike, java.util.function.BiFunction<Double,Double,Double> mapfn);
    /**
      * Creates an array from an array-like or iterable object.
      * @param arrayLike An array-like or iterable object to convert to an array.
      * @param mapfn A mapping function to call on every element of the array.
      * @param thisArg Value of 'this' used to invoke the mapfn.
      */
    native public static Int16Array from(Double[] arrayLike);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.Double> iterator();
    protected Int16Array(){}
}

