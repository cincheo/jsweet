package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public abstract class HTMLCollectionOf<TElement extends Element> extends Iterable<TElement> {
	 /**
     * Sets or retrieves the number of objects in a collection.
     */
   public int length;
   /**
     * Retrieves an object from various collections.
     */
   native public TElement item(java.lang.Object nameOrIndex, java.lang.Object optionalIndex);
   /**
     * Retrieves a select object or an object from an options collection.
     */
   native public TElement namedItem(java.lang.String name);
   native public TElement $get(int index);
   public static HTMLCollectionOf prototype;
   public HTMLCollectionOf(){}
   /**
     * Retrieves an object from various collections.
     */
   native public TElement item(java.lang.Object nameOrIndex);
   /**
     * Retrieves an object from various collections.
     */
   native public TElement item();
   /** From Iterable, to allow foreach loop (do not use directly). */
   @jsweet.lang.Erased
   native public java.util.Iterator<TElement> iterator();
}
