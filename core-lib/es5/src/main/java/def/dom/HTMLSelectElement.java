package def.dom;
public class HTMLSelectElement extends HTMLElement {
    /**
      * Provides a way to direct a user to a specific field when a document loads. This can provide both direction and convenience for a user, reducing the need to click or tab to a field when a page opens. This attribute is true when present on an element, and false when missing.
      */
    public Boolean autofocus;
    public Boolean disabled;
    /**
      * Retrieves a reference to the form that the object is embedded in. 
      */
    public HTMLFormElement form;
    /**
      * Sets or retrieves the number of objects in a collection.
      */
    public double length;
    /**
      * Sets or retrieves the Boolean value indicating whether multiple items can be selected from a list.
      */
    public Boolean multiple;
    /**
      * Sets or retrieves the name of the object.
      */
    public String name;
    public HTMLCollection options;
    /**
      * When present, marks an element that can't be submitted without a value.
      */
    public Boolean required;
    /**
      * Sets or retrieves the index of the selected option in a select object.
      */
    public double selectedIndex;
    /**
      * Sets or retrieves the number of rows in the list box. 
      */
    public double size;
    /**
      * Retrieves the type of select control based on the value of the MULTIPLE attribute.
      */
    public String type;
    /**
      * Returns the error message that would be displayed if the user submits the form, or an empty string if no error message. It also triggers the standard error message, such as "this is a required field". The result is that the user sees validation messages without actually submitting.
      */
    public String validationMessage;
    /**
      * Returns a  ValidityState object that represents the validity states of an element.
      */
    public ValidityState validity;
    /**
      * Sets or retrieves the value which is returned to the server when the form control is submitted.
      */
    public String value;
    /**
      * Returns whether an element will successfully validate based on forms validation rules and constraints.
      */
    public Boolean willValidate;
    public HTMLCollection selectedOptions;
    /**
      * Adds an element to the areas, controlRange, or options collection.
      * @param element Variant of type Number that specifies the index position in the collection where the element is placed. If no value is given, the method places the element at the end of the collection.
      * @param before Variant of type Object that specifies an element to insert before, or null to append the object to the collection. 
      */
    native public void add(HTMLElement element, HTMLElement before);
    /**
      * Returns whether a form will validate when it is submitted, without having to submit it.
      */
    native public Boolean checkValidity();
    /**
      * Retrieves a select object or an object from an options collection.
      * @param name Variant of type Number or String that specifies the object or collection to retrieve. If this parameter is an integer, it is the zero-based index of the object. If this parameter is a string, all objects with matching name or id properties are retrieved, and a collection is returned if more than one match is made.
      * @param index Variant of type Number that specifies the zero-based index of the object to retrieve when a collection is returned.
      */
    native public Object item(Object name, Object index);
    /**
      * Retrieves a select object or an object from an options collection.
      * @param namedItem A String that specifies the name or id property of the object to retrieve. A collection is returned if more than one match is made.
      */
    native public Object namedItem(String name);
    /**
      * Removes an element from the collection.
      * @param index Number that specifies the zero-based index of the element to remove from the collection.
      */
    native public void remove(double index);
    /**
      * Sets a custom error message that is displayed when a form is submitted.
      * @param error Sets a custom error message that is displayed when a form is submitted.
      */
    native public void setCustomValidity(String error);
    native public java.lang.Object $get(String name);
    public static HTMLSelectElement prototype;
    public HTMLSelectElement(){}
    /**
      * Adds an element to the areas, controlRange, or options collection.
      * @param element Variant of type Number that specifies the index position in the collection where the element is placed. If no value is given, the method places the element at the end of the collection.
      * @param before Variant of type Object that specifies an element to insert before, or null to append the object to the collection. 
      */
    native public void add(HTMLElement element);
    /**
      * Retrieves a select object or an object from an options collection.
      * @param name Variant of type Number or String that specifies the object or collection to retrieve. If this parameter is an integer, it is the zero-based index of the object. If this parameter is a string, all objects with matching name or id properties are retrieved, and a collection is returned if more than one match is made.
      * @param index Variant of type Number that specifies the zero-based index of the object to retrieve when a collection is returned.
      */
    native public Object item(Object name);
    /**
      * Retrieves a select object or an object from an options collection.
      * @param name Variant of type Number or String that specifies the object or collection to retrieve. If this parameter is an integer, it is the zero-based index of the object. If this parameter is a string, all objects with matching name or id properties are retrieved, and a collection is returned if more than one match is made.
      * @param index Variant of type Number that specifies the zero-based index of the object to retrieve when a collection is returned.
      */
    native public Object item();
    /**
      * Removes an element from the collection.
      * @param index Number that specifies the zero-based index of the element to remove from the collection.
      */
    native public void remove();
    /**
      * Adds an element to the areas, controlRange, or options collection.
      * @param element Variant of type Number that specifies the index position in the collection where the element is placed. If no value is given, the method places the element at the end of the collection.
      * @param before Variant of type Object that specifies an element to insert before, or null to append the object to the collection. 
      */
    native public void add(HTMLElement element, double before);
}

