package def.js;
/**
 * Allows enumerating over a COM collection, which may not have indexed item access.
 */
public class Enumerator<T> extends def.js.Object {
    /**
     * Returns true if the current item is the last one in the collection, or the collection is empty,
     * or the current item is undefined.
     */
    native public java.lang.Boolean atEnd();
    /**
     * Returns the current item in the collection
     */
    native public T item();
    /**
     * Resets the current item in the collection to the first item. If there are no items in the collection,
     * the current item is set to undefined.
     */
    native public void moveFirst();
    /**
     * Moves the current item to the next item in the collection. If the enumerator is at the end of
     * the collection or the collection is empty, the current item is set to undefined.
     */
    native public void moveNext();
    public Enumerator(java.lang.Object collection){}
    protected Enumerator(){}
}

