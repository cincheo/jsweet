package def.js;

import jsweet.util.function.Function4;

@jsweet.lang.SyntacticIterable
public class Array<T> extends Iterable<T> implements java.lang.Iterable<T> {
	public native T[] toArray();

	/**
	 * Gets or sets the length of the array. This is a number one higher than the
	 * highest element defined in an array.
	 */
	public int length;

	/**
	 * Appends new elements to an array, and returns the new length of the array.
	 * 
	 * @param items
	 *            New elements of the Array.
	 */
	native public double push(@SuppressWarnings("unchecked") T... items);

	/**
	 * Removes the last element from an array and returns it.
	 */
	native public T pop();

	/**
	 * Combines two or more arrays.
	 * 
	 * @param items
	 *            Additional items to add to the end of array1.
	 */
	native public Array<T> concat(@SuppressWarnings("unchecked") T... items);

	/**
	 * Combines two or more arrays.
	 * 
	 * @param items
	 *            Additional items to add to the end of array1.
	 */
	native public <U extends T> Array<T> concat(@SuppressWarnings("unchecked") ItemsUs<U>... items);

	/**
	 * Combines two or more arrays.
	 * 
	 * @param items
	 *            Additional items to add to the end of array1.
	 */
	native public Array<T> concat(@SuppressWarnings("unchecked") ItemsTs<T>... items);

	/**
	 * Adds all the elements of an array separated by the specified separator
	 * string.
	 * 
	 * @param separator
	 *            A string used to separate one element of an array from the next in
	 *            the resulting String. If omitted, the array elements are separated
	 *            with a comma.
	 */
	native public String join(java.lang.String separator);

	/**
	 * Adds all the elements of an array separated by the specified separator
	 * string.
	 * 
	 * @param separator
	 *            A string used to separate one element of an array from the next in
	 *            the resulting String. If omitted, the array elements are separated
	 *            with a comma.
	 */
	native public String join(String separator);

	/**
	 * Reverses the elements in an Array.
	 */
	native public Array<T> reverse();

	/**
	 * Removes the first element from an array and returns it.
	 */
	native public T shift();

	/**
	 * Returns a section of an array.
	 * 
	 * @param start
	 *            The beginning of the specified portion of the array.
	 * @param end
	 *            The end of the specified portion of the array.
	 */
	native public Array<T> slice(int start, int end);

	/**
	 * Sorts an array.
	 * 
	 * @param compareFn
	 *            The name of the function used to determine the order of the
	 *            elements. If omitted, the elements are sorted in ascending, ASCII
	 *            character order.
	 */
	native public Array<T> sort(java.util.function.BiFunction<T, T, Integer> compareFn);

	/**
	 * Removes elements from an array and, if necessary, inserts new elements in
	 * their place, returning the deleted elements.
	 * 
	 * @param start
	 *            The zero-based location in the array from which to start removing
	 *            elements.
	 */
	native public Array<T> splice(int start);

	/**
	 * Removes elements from an array and, if necessary, inserts new elements in
	 * their place, returning the deleted elements.
	 * 
	 * @param start
	 *            The zero-based location in the array from which to start removing
	 *            elements.
	 * @param deleteCount
	 *            The number of elements to remove.
	 * @param items
	 *            Elements to insert into the array in place of the deleted
	 *            elements.
	 */
	native public Array<T> splice(int start, int deleteCount, @SuppressWarnings("unchecked") T... items);

	/**
	 * Inserts new elements at the start of an array.
	 * 
	 * @param items
	 *            Elements to insert at the start of the Array.
	 */
	native public int unshift(@SuppressWarnings("unchecked") T... items);

	/**
	 * Returns the index of the first occurrence of a value in an array.
	 * 
	 * @param searchElement
	 *            The value to locate in the array.
	 * @param fromIndex
	 *            The array index at which to begin the search. If fromIndex is
	 *            omitted, the search starts at index 0.
	 */
	native public int indexOf(T searchElement, int fromIndex);

	/**
	 * Returns the index of the last occurrence of a specified value in an array.
	 * 
	 * @param searchElement
	 *            The value to locate in the array.
	 * @param fromIndex
	 *            The array index at which to begin the search. If fromIndex is
	 *            omitted, the search starts at the last index in the array.
	 */
	native public int lastIndexOf(T searchElement, int fromIndex);

	/**
	 * Determines whether all the members of an array satisfy the specified test.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The every method
	 *            calls the callbackfn function for each element in array1 until the
	 *            callbackfn returns false, or until the end of the array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public java.lang.Boolean every(
			jsweet.util.function.TriFunction<T, Double, Array<T>, java.lang.Boolean> callbackfn,
			java.lang.Object thisArg);

	/**
	 * Determines whether all the members of an array satisfy the specified test.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The every method
	 *            calls the callbackfn function for each element in array1 until the
	 *            callbackfn returns false, or until the end of the array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public java.lang.Boolean every(java.util.function.Function<T, java.lang.Boolean> callbackfn,
			java.lang.Object thisArg);

	/**
	 * Determines whether the specified callback function returns true for any
	 * element of an array.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The some method
	 *            calls the callbackfn function for each element in array1 until the
	 *            callbackfn returns true, or until the end of the array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public java.lang.Boolean some(
			jsweet.util.function.TriFunction<T, Double, Array<T>, java.lang.Boolean> callbackfn,
			java.lang.Object thisArg);

	/**
	 * Determines whether the specified callback function returns true for any
	 * element of an array.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The some method
	 *            calls the callbackfn function for each element in array1 until the
	 *            callbackfn returns true, or until the end of the array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public java.lang.Boolean some(java.util.function.Function<T, java.lang.Boolean> callbackfn,
			java.lang.Object thisArg);

	/**
	 * Performs the specified action for each element in an array.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. forEach calls the
	 *            callbackfn function one time for each element in the array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public void forEach(jsweet.util.function.TriConsumer<T, Integer, Array<T>> callbackfn,
			java.lang.Object thisArg);

	/**
	 * Performs the specified action for each element in an array.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. forEach calls the
	 *            callbackfn function one time for each element in the array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public void forEach(java.util.function.Consumer<T> callbackfn, java.lang.Object thisArg);

	/**
	 * Calls a defined callback function on each element of an array, and returns an
	 * array that contains the results.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The map method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public <U> Array<U> map(jsweet.util.function.TriFunction<T, Integer, Array<T>, U> callbackfn,
			java.lang.Object thisArg);

	/**
	 * Calls a defined callback function on each element of an array, and returns an
	 * array that contains the results.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The map method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public <U> Array<U> map(java.util.function.Function<T, U> callbackfn, java.lang.Object thisArg);

	/**
	 * Returns the elements of an array that meet the condition specified in a
	 * callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The filter method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public Array<T> filter(jsweet.util.function.TriFunction<T, Integer, Array<T>, java.lang.Boolean> callbackfn,
			java.lang.Object thisArg);

	/**
	 * Returns the elements of an array that meet the condition specified in a
	 * callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The filter method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public Array<T> filter(java.util.function.Function<T, java.lang.Boolean> callbackfn,
			java.lang.Object thisArg);

	/**
	 * Calls the specified callback function for all the elements in an array. The
	 * return value of the callback function is the accumulated result, and is
	 * provided as an argument in the next call to the callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to four arguments. The reduce method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param initialValue
	 *            If initialValue is specified, it is used as the initial value to
	 *            start the accumulation. The first call to the callbackfn function
	 *            provides this value as an argument instead of an array value.
	 */
	@jsweet.lang.Name("reduce")
	native public T reduceCallbackfnFunction4(Function4<T, T, Double, Array<T>, T> callbackfn,
			InitialValueT<T> initialValue);

	/**
	 * Calls the specified callback function for all the elements in an array. The
	 * return value of the callback function is the accumulated result, and is
	 * provided as an argument in the next call to the callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to four arguments. The reduce method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param initialValue
	 *            If initialValue is specified, it is used as the initial value to
	 *            start the accumulation. The first call to the callbackfn function
	 *            provides this value as an argument instead of an array value.
	 */
	@jsweet.lang.Name("reduce")
	native public <U> U reduceCallbackfnUUFunction4(Function4<U, T, Double, Array<T>, U> callbackfn,
			U initialValue);

	/**
	 * Calls the specified callback function for all the elements in an array, in
	 * descending order. The return value of the callback function is the
	 * accumulated result, and is provided as an argument in the next call to the
	 * callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to four arguments. The reduceRight
	 *            method calls the callbackfn function one time for each element in
	 *            the array.
	 * @param initialValue
	 *            If initialValue is specified, it is used as the initial value to
	 *            start the accumulation. The first call to the callbackfn function
	 *            provides this value as an argument instead of an array value.
	 */
	@jsweet.lang.Name("reduceRight")
	native public T reduceRightCallbackfnFunction4(Function4<T, T, Double, Array<T>, T> callbackfn,
			InitialValueT<T> initialValue);

	/**
	 * Calls the specified callback function for all the elements in an array, in
	 * descending order. The return value of the callback function is the
	 * accumulated result, and is provided as an argument in the next call to the
	 * callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to four arguments. The reduceRight
	 *            method calls the callbackfn function one time for each element in
	 *            the array.
	 * @param initialValue
	 *            If initialValue is specified, it is used as the initial value to
	 *            start the accumulation. The first call to the callbackfn function
	 *            provides this value as an argument instead of an array value.
	 */
	@jsweet.lang.Name("reduceRight")
	native public <U> U reduceRightCallbackfnUUFunction4(Function4<U, T, Double, Array<T>, U> callbackfn,
			U initialValue);

	native public T $get(int n);

	native public T $set(int n, T value);

	public Array(int arrayLength) {
	}

	public Array(@SuppressWarnings("unchecked") T... items) {
	}

	native public static Array<Object> $applyStatic(int arrayLength);

	native public static <T> T[] $applyStatic(@SuppressWarnings("unchecked") T... items);

	native public static java.lang.Boolean isArray(java.lang.Object arg);

	public static Array<?> prototype;

	/**
	 * Returns an array of key, value pairs for every entry in the array
	 */
	native public IterableIterator<jsweet.util.tuple.Tuple2<Double, T>> entries();

	/**
	 * Returns an list of keys in the array
	 */
	native public IterableIterator<Double> keys();

	/**
	 * Returns an list of values in the array
	 */
	native public IterableIterator<T> values();

	/**
	 * Returns the value of the first element in the array where predicate is true,
	 * and undefined otherwise.
	 * 
	 * @param predicate
	 *            find calls predicate once for each element of the array, in
	 *            ascending order, until it finds one where predicate returns true.
	 *            If such an element is found, find immediately returns that element
	 *            value. Otherwise, find returns undefined.
	 * @param thisArg
	 *            If provided, it will be used as the this value for each invocation
	 *            of predicate. If it is not provided, undefined is used instead.
	 */
	native public T find(jsweet.util.function.TriFunction<T, Integer, Array<T>, java.lang.Boolean> predicate,
			java.lang.Object thisArg);

	/**
	 * Returns the value of the first element in the array where predicate is true,
	 * and undefined otherwise.
	 * 
	 * @param predicate
	 *            find calls predicate once for each element of the array, in
	 *            ascending order, until it finds one where predicate returns true.
	 *            If such an element is found, find immediately returns that element
	 *            value. Otherwise, find returns undefined.
	 * @param thisArg
	 *            If provided, it will be used as the this value for each invocation
	 *            of predicate. If it is not provided, undefined is used instead.
	 */
	native public T find(java.util.function.Function<T, java.lang.Boolean> predicate, java.lang.Object thisArg);

	/**
	 * Returns the index of the first element in the array where predicate is true,
	 * and undefined otherwise.
	 * 
	 * @param predicate
	 *            find calls predicate once for each element of the array, in
	 *            ascending order, until it finds one where predicate returns true.
	 *            If such an element is found, find immediately returns that element
	 *            value. Otherwise, find returns undefined.
	 * @param thisArg
	 *            If provided, it will be used as the this value for each invocation
	 *            of predicate. If it is not provided, undefined is used instead.
	 */
	native public int findIndex(java.util.function.Function<T, java.lang.Boolean> predicate, java.lang.Object thisArg);

	/**
	 * Returns the this object after filling the section identified by start and end
	 * with value
	 * 
	 * @param value
	 *            value to fill array section with
	 * @param start
	 *            index to start filling the array at. If start is negative, it is
	 *            treated as length+start where length is the length of the array.
	 * @param end
	 *            index to stop filling the array at. If end is negative, it is
	 *            treated as length+end.
	 */
	native public Array<T> fill(T value, int start, int end);

	/**
	 * Returns the this object after copying a section of the array identified by
	 * start and end to the same array starting at position target
	 * 
	 * @param target
	 *            If target is negative, it is treated as length+target where length
	 *            is the length of the array.
	 * @param start
	 *            If start is negative, it is treated as length+start. If end is
	 *            negative, it is treated as length+end.
	 * @param end
	 *            If not specified, length of the this object is used as its default
	 *            value.
	 */
	native public Array<T> copyWithin(int target, int start, int end);

	/**
	 * Adds all the elements of an array separated by the specified separator
	 * string.
	 * 
	 * @param separator
	 *            A string used to separate one element of an array from the next in
	 *            the resulting String. If omitted, the array elements are separated
	 *            with a comma.
	 */
	native public String join();

	/**
	 * Returns a section of an array.
	 * 
	 * @param start
	 *            The beginning of the specified portion of the array.
	 * @param end
	 *            The end of the specified portion of the array.
	 */
	native public Array<T> slice(int start);

	/**
	 * Returns a section of an array.
	 * 
	 * @param start
	 *            The beginning of the specified portion of the array.
	 * @param end
	 *            The end of the specified portion of the array.
	 */
	native public Array<T> slice();

	/**
	 * Sorts an array.
	 * 
	 * @param compareFn
	 *            The name of the function used to determine the order of the
	 *            elements. If omitted, the elements are sorted in ascending, ASCII
	 *            character order.
	 */
	native public Array<T> sort();

	/**
	 * Returns the index of the first occurrence of a value in an array.
	 * 
	 * @param searchElement
	 *            The value to locate in the array.
	 * @param fromIndex
	 *            The array index at which to begin the search. If fromIndex is
	 *            omitted, the search starts at index 0.
	 */
	native public int indexOf(T searchElement);

	/**
	 * Returns the index of the last occurrence of a specified value in an array.
	 * 
	 * @param searchElement
	 *            The value to locate in the array.
	 * @param fromIndex
	 *            The array index at which to begin the search. If fromIndex is
	 *            omitted, the search starts at the last index in the array.
	 */
	native public int lastIndexOf(T searchElement);

	/**
	 * Determines whether all the members of an array satisfy the specified test.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The every method
	 *            calls the callbackfn function for each element in array1 until the
	 *            callbackfn returns false, or until the end of the array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public java.lang.Boolean every(
			jsweet.util.function.TriFunction<T, Integer, Array<T>, java.lang.Boolean> callbackfn);

	/**
	 * Determines whether the specified callback function returns true for any
	 * element of an array.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The some method
	 *            calls the callbackfn function for each element in array1 until the
	 *            callbackfn returns true, or until the end of the array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public java.lang.Boolean some(
			jsweet.util.function.TriFunction<T, Integer, Array<T>, java.lang.Boolean> callbackfn);

	/**
	 * Performs the specified action for each element in an array.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. forEach calls the
	 *            callbackfn function one time for each element in the array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public void forEach(jsweet.util.function.TriConsumer<T, Integer, Array<T>> callbackfn);

	/**
	 * Calls a defined callback function on each element of an array, and returns an
	 * array that contains the results.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The map method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public <U> Array<U> map(jsweet.util.function.TriFunction<T, Integer, Array<T>, U> callbackfn);

	/**
	 * Calls a defined callback function on each element of an array, and returns an
	 * array that contains the results.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The map method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public <U> Array<U> map(java.util.function.Function<T, U> callbackfn);

	/**
	 * Returns the elements of an array that meet the condition specified in a
	 * callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The filter method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public Array<T> filter(jsweet.util.function.TriFunction<T, Integer, Array<T>, java.lang.Boolean> callbackfn);

	/**
	 * Returns the elements of an array that meet the condition specified in a
	 * callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to three arguments. The filter method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param thisArg
	 *            An object to which the this keyword can refer in the callbackfn
	 *            function. If thisArg is omitted, undefined is used as the this
	 *            value.
	 */
	native public Array<T> filter(java.util.function.Function<T, java.lang.Boolean> callbackfn);

	/**
	 * Calls the specified callback function for all the elements in an array. The
	 * return value of the callback function is the accumulated result, and is
	 * provided as an argument in the next call to the callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to four arguments. The reduce method
	 *            calls the callbackfn function one time for each element in the
	 *            array.
	 * @param initialValue
	 *            If initialValue is specified, it is used as the initial value to
	 *            start the accumulation. The first call to the callbackfn function
	 *            provides this value as an argument instead of an array value.
	 */
	native public T reduce(Function4<T, T, Integer, Array<T>, T> callbackfn);

	/**
	 * Calls the specified callback function for all the elements in an array, in
	 * descending order. The return value of the callback function is the
	 * accumulated result, and is provided as an argument in the next call to the
	 * callback function.
	 * 
	 * @param callbackfn
	 *            A function that accepts up to four arguments. The reduceRight
	 *            method calls the callbackfn function one time for each element in
	 *            the array.
	 * @param initialValue
	 *            If initialValue is specified, it is used as the initial value to
	 *            start the accumulation. The first call to the callbackfn function
	 *            provides this value as an argument instead of an array value.
	 */
	native public T reduceRight(Function4<T, T, Integer, Array<T>, T> callbackfn);

	public Array() {
	}

	native public static Array<Object> $applyStatic();

	/**
	 * Returns the value of the first element in the array where predicate is true,
	 * and undefined otherwise.
	 * 
	 * @param predicate
	 *            find calls predicate once for each element of the array, in
	 *            ascending order, until it finds one where predicate returns true.
	 *            If such an element is found, find immediately returns that element
	 *            value. Otherwise, find returns undefined.
	 * @param thisArg
	 *            If provided, it will be used as the this value for each invocation
	 *            of predicate. If it is not provided, undefined is used instead.
	 */
	native public T find(jsweet.util.function.TriFunction<T, Integer, Array<T>, java.lang.Boolean> predicate);

	/**
	 * Returns the index of the first element in the array where predicate is true,
	 * and undefined otherwise.
	 * 
	 * @param predicate
	 *            find calls predicate once for each element of the array, in
	 *            ascending order, until it finds one where predicate returns true.
	 *            If such an element is found, find immediately returns that element
	 *            value. Otherwise, find returns undefined.
	 * @param thisArg
	 *            If provided, it will be used as the this value for each invocation
	 *            of predicate. If it is not provided, undefined is used instead.
	 */
	native public int findIndex(java.util.function.Function<T, java.lang.Boolean> predicate);

	/**
	 * Returns the this object after filling the section identified by start and end
	 * with value
	 * 
	 * @param value
	 *            value to fill array section with
	 * @param start
	 *            index to start filling the array at. If start is negative, it is
	 *            treated as length+start where length is the length of the array.
	 * @param end
	 *            index to stop filling the array at. If end is negative, it is
	 *            treated as length+end.
	 */
	native public Array<T> fill(T value, int start);

	/**
	 * Returns the this object after filling the section identified by start and end
	 * with value
	 * 
	 * @param value
	 *            value to fill array section with
	 * @param start
	 *            index to start filling the array at. If start is negative, it is
	 *            treated as length+start where length is the length of the array.
	 * @param end
	 *            index to stop filling the array at. If end is negative, it is
	 *            treated as length+end.
	 */
	native public Array<T> fill(T value);

	/**
	 * Returns the this object after copying a section of the array identified by
	 * start and end to the same array starting at position target
	 * 
	 * @param target
	 *            If target is negative, it is treated as length+target where length
	 *            is the length of the array.
	 * @param start
	 *            If start is negative, it is treated as length+start. If end is
	 *            negative, it is treated as length+end.
	 * @param end
	 *            If not specified, length of the this object is used as its default
	 *            value.
	 */
	native public Array<T> copyWithin(int target, int start);

	/** From Iterable, to allow foreach loop (do not use directly). */
	@jsweet.lang.Erased
	native public java.util.Iterator<T> iterator();

	/**
	 * This class was automatically generated for disambiguating erased method
	 * signatures.
	 */
	@jsweet.lang.Erased
	public static class ItemsTs<T> extends def.js.Object {
		public ItemsTs(@SuppressWarnings("unchecked") T... items) {
		}
	}

	/**
	 * This class was automatically generated for disambiguating erased method
	 * signatures.
	 */
	@jsweet.lang.Erased
	public static class ItemsUs<U> extends def.js.Object {
		public ItemsUs(@SuppressWarnings("unchecked") U... items) {
		}
	}

	/**
	 * This class was automatically generated for disambiguating erased method
	 * signatures.
	 */
	@jsweet.lang.Erased
	public static class InitialValueU<U> extends def.js.Object {
		public InitialValueU(U initialValue) {
		}
	}

	/**
	 * This class was automatically generated for disambiguating erased method
	 * signatures.
	 */
	@jsweet.lang.Erased
	public static class InitialValueT<T> extends def.js.Object {
		public InitialValueT(T initialValue) {
		}
	}

	public static native <T> Array<T> from(java.lang.Object arrayLike);
}
