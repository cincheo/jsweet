package def.dom;

@jsweet.lang.Extends({HTMLTableAlignment.class})
public class HTMLTableSectionElement extends HTMLElement {
    /**
      * Sets or retrieves a value that indicates the table alignment.
      */
    public java.lang.String align;
    /**
      * Sets or retrieves the number of horizontal rows contained in the object.
      */
    public HTMLCollection rows;
    /**
      * Removes the specified row (tr) from the element and from the rows collection.
      * @param index Number that specifies the zero-based position in the rows collection of the row to remove.
      */
    native public void deleteRow(double index);
    /**
      * Creates a new row (tr) in the table, and adds the row to the rows collection.
      * @param index Number that specifies where to insert the row in the rows collection. The default value is -1, which appends the new row to the end of the rows collection.
      */
    native public HTMLElement insertRow(double index);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLTableSectionElement prototype;
    public HTMLTableSectionElement(){}
    /**
      * Sets or retrieves a value that you can use to implement your own ch functionality for the object.
      */
    public java.lang.String ch;
    /**
      * Sets or retrieves a value that you can use to implement your own chOff functionality for the object.
      */
    public java.lang.String chOff;
    /**
      * Sets or retrieves how text and other content are vertically aligned within the object that contains them.
      */
    public java.lang.String vAlign;
    /**
      * Removes the specified row (tr) from the element and from the rows collection.
      * @param index Number that specifies the zero-based position in the rows collection of the row to remove.
      */
    native public void deleteRow();
    /**
      * Creates a new row (tr) in the table, and adds the row to the rows collection.
      * @param index Number that specifies where to insert the row in the rows collection. The default value is -1, which appends the new row to the end of the rows collection.
      */
    native public HTMLElement insertRow();
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

