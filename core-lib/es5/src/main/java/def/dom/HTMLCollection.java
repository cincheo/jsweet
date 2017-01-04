package def.dom;
public class HTMLCollection extends def.js.Object implements Iterable<Element> {
    /**
      * Sets or retrieves the number of objects in a collection.
      */
    public double length;
    /**
      * Retrieves an object from various collections.
      */
    native public Element item(Object nameOrIndex, Object optionalIndex);
    /**
      * Retrieves a select object or an object from an options collection.
      */
    native public Element namedItem(String name);
    native public Element $get(double index);
    public static HTMLCollection prototype;
    public HTMLCollection(){}
    /**
      * Retrieves an object from various collections.
      */
    native public Element item(Object nameOrIndex);
    /**
      * Retrieves an object from various collections.
      */
    native public Element item();
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Element> iterator();
}

