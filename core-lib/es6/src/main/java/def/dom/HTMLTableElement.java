package def.dom;
public class HTMLTableElement extends HTMLElement {
    /**
      * Sets or retrieves a value that indicates the table alignment.
      */
    public java.lang.String align;
    public java.lang.Object bgColor;
    /**
      * Sets or retrieves the width of the border to draw around the object.
      */
    public java.lang.String border;
    /**
      * Sets or retrieves the border color of the object. 
      */
    public java.lang.Object borderColor;
    /**
      * Retrieves the caption object of a table.
      */
    public HTMLTableCaptionElement caption;
    /**
      * Sets or retrieves the amount of space between the border of the cell and the content of the cell.
      */
    public java.lang.String cellPadding;
    /**
      * Sets or retrieves the amount of space between cells in a table.
      */
    public java.lang.String cellSpacing;
    /**
      * Sets or retrieves the number of columns in the table.
      */
    public double cols;
    /**
      * Sets or retrieves the way the border frame around the table is displayed.
      */
    public java.lang.String frame;
    /**
      * Sets or retrieves the height of the object.
      */
    public java.lang.Object height;
    /**
      * Sets or retrieves the number of horizontal rows contained in the object.
      */
    public HTMLCollection rows;
    /**
      * Sets or retrieves which dividing lines (inner borders) are displayed.
      */
    public java.lang.String rules;
    /**
      * Sets or retrieves a description and/or structure of the object.
      */
    public java.lang.String summary;
    /**
      * Retrieves a collection of all tBody objects in the table. Objects in this collection are in source order.
      */
    public HTMLCollection tBodies;
    /**
      * Retrieves the tFoot object of the table.
      */
    public HTMLTableSectionElement tFoot;
    /**
      * Retrieves the tHead object of the table.
      */
    public HTMLTableSectionElement tHead;
    /**
      * Sets or retrieves the width of the object.
      */
    public java.lang.String width;
    /**
      * Creates an empty caption element in the table.
      */
    native public HTMLElement createCaption();
    /**
      * Creates an empty tBody element in the table.
      */
    native public HTMLElement createTBody();
    /**
      * Creates an empty tFoot element in the table.
      */
    native public HTMLElement createTFoot();
    /**
      * Returns the tHead element object if successful, or null otherwise.
      */
    native public HTMLElement createTHead();
    /**
      * Deletes the caption element and its contents from the table.
      */
    native public void deleteCaption();
    /**
      * Removes the specified row (tr) from the element and from the rows collection.
      * @param index Number that specifies the zero-based position in the rows collection of the row to remove.
      */
    native public void deleteRow(double index);
    /**
      * Deletes the tFoot element and its contents from the table.
      */
    native public void deleteTFoot();
    /**
      * Deletes the tHead element and its contents from the table.
      */
    native public void deleteTHead();
    /**
      * Creates a new row (tr) in the table, and adds the row to the rows collection.
      * @param index Number that specifies where to insert the row in the rows collection. The default value is -1, which appends the new row to the end of the rows collection.
      */
    native public HTMLElement insertRow(double index);
    public static HTMLTableElement prototype;
    public HTMLTableElement(){}
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
}

