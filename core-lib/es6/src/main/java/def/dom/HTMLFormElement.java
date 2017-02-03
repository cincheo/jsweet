package def.dom;
public class HTMLFormElement extends HTMLElement {
    /**
      * Sets or retrieves a list of character encodings for input data that must be accepted by the server processing the form.
      */
    public java.lang.String acceptCharset;
    /**
      * Sets or retrieves the URL to which the form content is sent for processing.
      */
    public java.lang.String action;
    /**
      * Specifies whether autocomplete is applied to an editable text field.
      */
    public java.lang.String autocomplete;
    /**
      * Retrieves a collection, in source order, of all controls in a given form.
      */
    public HTMLCollection elements;
    /**
      * Sets or retrieves the MIME encoding for the form.
      */
    public java.lang.String encoding;
    /**
      * Sets or retrieves the encoding type for the form.
      */
    public java.lang.String enctype;
    /**
      * Sets or retrieves the number of objects in a collection.
      */
    public double length;
    /**
      * Sets or retrieves how to send the form data to the server.
      */
    public java.lang.String method;
    /**
      * Sets or retrieves the name of the object.
      */
    public java.lang.String name;
    /**
      * Designates a form that is not validated when submitted.
      */
    public java.lang.Boolean noValidate;
    /**
      * Sets or retrieves the window or frame at which to target content.
      */
    public java.lang.String target;
    /**
      * Returns whether a form will validate when it is submitted, without having to submit it.
      */
    native public java.lang.Boolean checkValidity();
    /**
      * Retrieves a form object or an object from an elements collection.
      * @param name Variant of type Number or String that specifies the object or collection to retrieve. If this parameter is a Number, it is the zero-based index of the object. If this parameter is a string, all objects with matching name or id properties are retrieved, and a collection is returned if more than one match is made.
      * @param index Variant of type Number that specifies the zero-based index of the object to retrieve when a collection is returned.
      */
    native public java.lang.Object item(java.lang.Object name, java.lang.Object index);
    /**
      * Retrieves a form object or an object from an elements collection.
      */
    native public java.lang.Object namedItem(java.lang.String name);
    /**
      * Fires when the user resets a form.
      */
    native public void reset();
    /**
      * Fires when a FORM is about to be submitted.
      */
    native public void submit();
    native public java.lang.Object $get(java.lang.String name);
    public static HTMLFormElement prototype;
    public HTMLFormElement(){}
    /**
      * Retrieves a form object or an object from an elements collection.
      * @param name Variant of type Number or String that specifies the object or collection to retrieve. If this parameter is a Number, it is the zero-based index of the object. If this parameter is a string, all objects with matching name or id properties are retrieved, and a collection is returned if more than one match is made.
      * @param index Variant of type Number that specifies the zero-based index of the object to retrieve when a collection is returned.
      */
    native public java.lang.Object item(java.lang.Object name);
    /**
      * Retrieves a form object or an object from an elements collection.
      * @param name Variant of type Number or String that specifies the object or collection to retrieve. If this parameter is a Number, it is the zero-based index of the object. If this parameter is a string, all objects with matching name or id properties are retrieved, and a collection is returned if more than one match is made.
      * @param index Variant of type Number that specifies the zero-based index of the object to retrieve when a collection is returned.
      */
    native public java.lang.Object item();
}

