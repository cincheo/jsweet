package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class HTMLCollection extends Iterable<Element> {
    /**
      * Sets or retrieves the number of objects in a collection.
      */
    public double length;
    /**
      * Retrieves an object from various collections.
      */
    native public Element item(java.lang.Object nameOrIndex, java.lang.Object optionalIndex);
    /**
      * Retrieves a select object or an object from an options collection.
      */
    native public Element namedItem(java.lang.String name);
    native public Element $get(double index);
    public static HTMLCollection prototype;
    public HTMLCollection(){}
    /**
      * Retrieves an object from various collections.
      */
    native public Element item(java.lang.Object nameOrIndex);
    /**
      * Retrieves an object from various collections.
      */
    native public Element item();
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Element> iterator();
}

