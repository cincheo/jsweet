package def.dom;
public class HTMLOptGroupElement extends HTMLElement {
    /**
      * Sets or retrieves the status of an option.
      */
    public Boolean defaultSelected;
    public Boolean disabled;
    /**
      * Retrieves a reference to the form that the object is embedded in.
      */
    public HTMLFormElement form;
    /**
      * Sets or retrieves the ordinal position of an option in a list box.
      */
    public double index;
    /**
      * Sets or retrieves a value that you can use to implement your own label functionality for the object.
      */
    public String label;
    /**
      * Sets or retrieves whether the option in the list box is the default item.
      */
    public Boolean selected;
    /**
      * Sets or retrieves the text string specified by the option tag.
      */
    public String text;
    /**
      * Sets or retrieves the value which is returned to the server when the form control is submitted.
      */
    public String value;
    public static HTMLOptGroupElement prototype;
    public HTMLOptGroupElement(){}
}

