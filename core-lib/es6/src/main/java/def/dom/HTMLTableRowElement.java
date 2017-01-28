package def.dom;

@jsweet.lang.Extends({HTMLTableAlignment.class})
public class HTMLTableRowElement extends HTMLElement {
    /**
      * Sets or retrieves how the object is aligned with adjacent text.
      */
    public java.lang.String align;
    public java.lang.Object bgColor;
    /**
      * Retrieves a collection of all cells in the table row.
      */
    public HTMLCollection cells;
    /**
      * Sets or retrieves the height of the object.
      */
    public java.lang.Object height;
    /**
      * Retrieves the position of the object in the rows collection for the table.
      */
    public double rowIndex;
    /**
      * Retrieves the position of the object in the collection.
      */
    public double sectionRowIndex;
    /**
      * Removes the specified cell from the table row, as well as from the cells collection.
      * @param index Number that specifies the zero-based position of the cell to remove from the table row. If no value is provided, the last cell in the cells collection is deleted.
      */
    native public void deleteCell(double index);
    /**
      * Creates a new cell in the table row, and adds the cell to the cells collection.
      * @param index Number that specifies where to insert the cell in the tr. The default value is -1, which appends the new cell to the end of the cells collection.
      */
    native public HTMLElement insertCell(double index);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLTableRowElement prototype;
    public HTMLTableRowElement(){}
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
      * Removes the specified cell from the table row, as well as from the cells collection.
      * @param index Number that specifies the zero-based position of the cell to remove from the table row. If no value is provided, the last cell in the cells collection is deleted.
      */
    native public void deleteCell();
    /**
      * Creates a new cell in the table row, and adds the cell to the cells collection.
      * @param index Number that specifies where to insert the cell in the tr. The default value is -1, which appends the new cell to the end of the cells collection.
      */
    native public HTMLElement insertCell();
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

