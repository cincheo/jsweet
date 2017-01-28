package def.dom;

@jsweet.lang.Extends({HTMLTableAlignment.class})
public class HTMLTableCellElement extends HTMLElement {
    /**
      * Sets or retrieves abbreviated text for the object.
      */
    public java.lang.String abbr;
    /**
      * Sets or retrieves how the object is aligned with adjacent text.
      */
    public java.lang.String align;
    /**
      * Sets or retrieves a comma-delimited list of conceptual categories associated with the object.
      */
    public java.lang.String axis;
    public java.lang.Object bgColor;
    /**
      * Retrieves the position of the object in the cells collection of a row.
      */
    public double cellIndex;
    /**
      * Sets or retrieves the number columns in the table that the object should span.
      */
    public double colSpan;
    /**
      * Sets or retrieves a list of header cells that provide information for the object.
      */
    public java.lang.String headers;
    /**
      * Sets or retrieves the height of the object.
      */
    public java.lang.Object height;
    /**
      * Sets or retrieves whether the browser automatically performs wordwrap.
      */
    public java.lang.Boolean noWrap;
    /**
      * Sets or retrieves how many rows in a table the cell should span.
      */
    public double rowSpan;
    /**
      * Sets or retrieves the group of cells in a table to which the object's information applies.
      */
    public java.lang.String scope;
    /**
      * Sets or retrieves the width of the object.
      */
    public java.lang.String width;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLTableCellElement prototype;
    public HTMLTableCellElement(){}
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
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

