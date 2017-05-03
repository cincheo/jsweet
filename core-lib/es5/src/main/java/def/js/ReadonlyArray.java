package def.js;
import jsweet.util.function.Function4;
@jsweet.lang.Interface
public abstract class ReadonlyArray<T> extends def.js.Object implements Iterable<T> {
    /**
      * Gets the length of the array. This is a number one higher than the highest element defined in an array.
      */
    public final int length=0;
    native public String toLocaleString();
    /**
      * Combines two or more arrays.
      * @param items Additional items to add to the end of array1.
      */
    native public <U extends ReadonlyArray<T>> T[] concat(@SuppressWarnings("unchecked") U... items);
    /**
      * Combines two or more arrays.
      * @param items Additional items to add to the end of array1.
      */
    native public Array<T> concat(@SuppressWarnings("unchecked") T... items);
    /**
      * Adds all the elements of an array separated by the specified separator string.
      * @param separator A string used to separate one element of an array from the next in the resulting String. If omitted, the array elements are separated with a comma.
      */
    native public String join(java.lang.String separator);
    /**
     * Adds all the elements of an array separated by the specified separator string.
     * @param separator A string used to separate one element of an array from the next in the resulting String. If omitted, the array elements are separated with a comma.
     */
   native public String join(String separator);
    /**
      * Returns a section of an array.
      * @param start The beginning of the specified portion of the array.
      * @param end The end of the specified portion of the array.
      */
    native public Array<T> slice(double start, double end);
    /**
      * Returns the index of the first occurrence of a value in an array.
      * @param searchElement The value to locate in the array.
      * @param fromIndex The array index at which to begin the search. If fromIndex is omitted, the search starts at index 0.
      */
    native public int indexOf(T searchElement, double fromIndex);
    /**
      * Returns the index of the last occurrence of a specified value in an array.
      * @param searchElement The value to locate in the array.
      * @param fromIndex The array index at which to begin the search. If fromIndex is omitted, the search starts at the last index in the array.
      */
    native public int lastIndexOf(T searchElement, double fromIndex);
    /**
      * Determines whether all the members of an array satisfy the specified test.
      * @param callbackfn A function that accepts up to three arguments. The every method calls the callbackfn function for each element in array1 until the callbackfn returns false, or until the end of the array.
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public java.lang.Boolean every(jsweet.util.function.TriFunction<T,Double,ReadonlyArray<T>,java.lang.Boolean> callbackfn, java.lang.Object thisArg);
    /**
      * Determines whether the specified callback function returns true for any element of an array.
      * @param callbackfn A function that accepts up to three arguments. The some method calls the callbackfn function for each element in array1 until the callbackfn returns true, or until the end of the array.
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public java.lang.Boolean some(jsweet.util.function.TriFunction<T,Double,ReadonlyArray<T>,java.lang.Boolean> callbackfn, java.lang.Object thisArg);
    /**
      * Performs the specified action for each element in an array.
      * @param callbackfn  A function that accepts up to three arguments. forEach calls the callbackfn function one time for each element in the array. 
      * @param thisArg  An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public void forEach(jsweet.util.function.TriConsumer<T,Double,ReadonlyArray<T>> callbackfn, java.lang.Object thisArg);
    /**
      * Calls a defined callback function on each element of an array, and returns an array that contains the results.
      * @param callbackfn A function that accepts up to three arguments. The map method calls the callbackfn function one time for each element in the array. 
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public <U> U[] map(jsweet.util.function.TriFunction<T,Double,ReadonlyArray<T>,U> callbackfn, java.lang.Object thisArg);
    /**
      * Returns the elements of an array that meet the condition specified in a callback function.
      * @param callbackfn A function that accepts up to three arguments. The filter method calls the callbackfn function one time for each element in the array. 
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public T[] filter(jsweet.util.function.TriFunction<T,Double,ReadonlyArray<T>,java.lang.Boolean> callbackfn, java.lang.Object thisArg);
    /**
      * Calls the specified callback function for all the elements in an array. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduce method calls the callbackfn function one time for each element in the array.
      * @param initialValue If initialValue is specified, it is used as the initial value to start the accumulation. The first call to the callbackfn function provides this value as an argument instead of an array value.
      */
    @jsweet.lang.Name("reduce")
    native public T reduceCallbackfnFunction4(Function4<T,T,Double,ReadonlyArray<T>,T> callbackfn, InitialValueT<T> initialValue);
    /**
      * Calls the specified callback function for all the elements in an array. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduce method calls the callbackfn function one time for each element in the array.
      * @param initialValue If initialValue is specified, it is used as the initial value to start the accumulation. The first call to the callbackfn function provides this value as an argument instead of an array value.
      */
    @jsweet.lang.Name("reduce")
    native public <U> U reduceCallbackfnUUFunction4(Function4<U,T,Double,ReadonlyArray<T>,U> callbackfn, InitialValueU<U> initialValue);
    /**
      * Calls the specified callback function for all the elements in an array, in descending order. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduceRight method calls the callbackfn function one time for each element in the array. 
      * @param initialValue If initialValue is specified, it is used as the initial value to start the accumulation. The first call to the callbackfn function provides this value as an argument instead of an array value.
      */
    @jsweet.lang.Name("reduceRight")
    native public T reduceRightCallbackfnFunction4(Function4<T,T,Double,ReadonlyArray<T>,T> callbackfn, InitialValueT<T> initialValue);
    /**
      * Calls the specified callback function for all the elements in an array, in descending order. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduceRight method calls the callbackfn function one time for each element in the array. 
      * @param initialValue If initialValue is specified, it is used as the initial value to start the accumulation. The first call to the callbackfn function provides this value as an argument instead of an array value.
      */
    @jsweet.lang.Name("reduceRight")
    native public <U> U reduceRightCallbackfnUUFunction4(Function4<U,T,Double,ReadonlyArray<T>,U> callbackfn, InitialValueU<U> initialValue);
    native public T $get(double n);
    /**
      * Adds all the elements of an array separated by the specified separator string.
      * @param separator A string used to separate one element of an array from the next in the resulting String. If omitted, the array elements are separated with a comma.
      */
    native public java.lang.String join();
    /**
      * Returns a section of an array.
      * @param start The beginning of the specified portion of the array.
      * @param end The end of the specified portion of the array.
      */
    native public T[] slice(double start);
    /**
      * Returns a section of an array.
      * @param start The beginning of the specified portion of the array.
      * @param end The end of the specified portion of the array.
      */
    native public T[] slice();
    /**
      * Returns the index of the first occurrence of a value in an array.
      * @param searchElement The value to locate in the array.
      * @param fromIndex The array index at which to begin the search. If fromIndex is omitted, the search starts at index 0.
      */
    native public double indexOf(T searchElement);
    /**
      * Returns the index of the last occurrence of a specified value in an array.
      * @param searchElement The value to locate in the array.
      * @param fromIndex The array index at which to begin the search. If fromIndex is omitted, the search starts at the last index in the array.
      */
    native public double lastIndexOf(T searchElement);
    /**
      * Determines whether all the members of an array satisfy the specified test.
      * @param callbackfn A function that accepts up to three arguments. The every method calls the callbackfn function for each element in array1 until the callbackfn returns false, or until the end of the array.
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public java.lang.Boolean every(jsweet.util.function.TriFunction<T,Double,ReadonlyArray<T>,java.lang.Boolean> callbackfn);
    /**
      * Determines whether the specified callback function returns true for any element of an array.
      * @param callbackfn A function that accepts up to three arguments. The some method calls the callbackfn function for each element in array1 until the callbackfn returns true, or until the end of the array.
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public java.lang.Boolean some(jsweet.util.function.TriFunction<T,Double,ReadonlyArray<T>,java.lang.Boolean> callbackfn);
    /**
      * Performs the specified action for each element in an array.
      * @param callbackfn  A function that accepts up to three arguments. forEach calls the callbackfn function one time for each element in the array. 
      * @param thisArg  An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public void forEach(jsweet.util.function.TriConsumer<T,Double,ReadonlyArray<T>> callbackfn);
    /**
      * Calls a defined callback function on each element of an array, and returns an array that contains the results.
      * @param callbackfn A function that accepts up to three arguments. The map method calls the callbackfn function one time for each element in the array. 
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public <U> U[] map(jsweet.util.function.TriFunction<T,Double,ReadonlyArray<T>,U> callbackfn);
    /**
      * Returns the elements of an array that meet the condition specified in a callback function.
      * @param callbackfn A function that accepts up to three arguments. The filter method calls the callbackfn function one time for each element in the array. 
      * @param thisArg An object to which the this keyword can refer in the callbackfn function. If thisArg is omitted, undefined is used as the this value.
      */
    native public T[] filter(jsweet.util.function.TriFunction<T,Double,ReadonlyArray<T>,java.lang.Boolean> callbackfn);
    /**
      * Calls the specified callback function for all the elements in an array. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduce method calls the callbackfn function one time for each element in the array.
      * @param initialValue If initialValue is specified, it is used as the initial value to start the accumulation. The first call to the callbackfn function provides this value as an argument instead of an array value.
      */
    native public T reduce(Function4<T,T,Double,ReadonlyArray<T>,T> callbackfn);
    /**
      * Calls the specified callback function for all the elements in an array, in descending order. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.
      * @param callbackfn A function that accepts up to four arguments. The reduceRight method calls the callbackfn function one time for each element in the array. 
      * @param initialValue If initialValue is specified, it is used as the initial value to start the accumulation. The first call to the callbackfn function provides this value as an argument instead of an array value.
      */
    native public T reduceRight(Function4<T,T,Double,ReadonlyArray<T>,T> callbackfn);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<T> iterator();
    /** This class was automatically generated for disambiguating erased method signatures. */
    @jsweet.lang.Erased
    public static class InitialValueU<U> extends def.js.Object {
        public InitialValueU(U initialValue){}
    }
    /** This class was automatically generated for disambiguating erased method signatures. */
    @jsweet.lang.Erased
    public static class InitialValueT<T> extends def.js.Object {
        public InitialValueT(T initialValue){}
    }
}

