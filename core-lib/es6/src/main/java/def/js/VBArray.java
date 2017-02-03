package def.js;
/**
 * Enables reading from a COM safe array, which might have an alternate lower bound, or multiple dimensions.
 */
public class VBArray<T> extends def.js.Object {
    /**
     * Returns the number of dimensions (1-based).
     */
    native public double dimensions();
    /**
     * Takes an index for each dimension in the array, and returns the item at the corresponding location.
     */
    native public T getItem(double dimension1Index, double... dimensionNIndexes);
    /**
     * Returns the smallest available index for a given dimension.
     * @param dimension 1-based dimension (defaults to 1)
     */
    native public double lbound(double dimension);
    /**
     * Returns the largest available index for a given dimension.
     * @param dimension 1-based dimension (defaults to 1)
     */
    native public double ubound(double dimension);
    /**
     * Returns a Javascript array with all the elements in the VBArray. If there are multiple dimensions,
     * each successive dimension is appended to the end of the array.
     * Example: [[1,2,3],[4,5,6]] becomes [1,2,3,4,5,6]
     */
    native public T[] toArray();
    public VBArray(java.lang.Object safeArray){}
    /**
     * Returns the smallest available index for a given dimension.
     * @param dimension 1-based dimension (defaults to 1)
     */
    native public double lbound();
    /**
     * Returns the largest available index for a given dimension.
     * @param dimension 1-based dimension (defaults to 1)
     */
    native public double ubound();
    protected VBArray(){}
}

