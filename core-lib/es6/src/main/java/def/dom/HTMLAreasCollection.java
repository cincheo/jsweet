package def.dom;
public class HTMLAreasCollection extends HTMLCollection {
    /**
      * Adds an element to the areas, controlRange, or options collection.
      */
    native public void add(HTMLElement element, HTMLElement before);
    native public void add(HTMLElement element, double before);
    /**
      * Removes an element from the collection.
      */
    native public void remove(double index);
    public static HTMLAreasCollection prototype;
    public HTMLAreasCollection(){}
    /**
      * Adds an element to the areas, controlRange, or options collection.
      */
    native public void add(HTMLElement element);
    /**
      * Removes an element from the collection.
      */
    native public void remove();
}

