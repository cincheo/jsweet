package def.dom;

@jsweet.lang.Extends({HTMLTableAlignment.class})
public class HTMLTableColElement extends HTMLElement {
    /**
      * Sets or retrieves the alignment of the object relative to the display or table.
      */
    public java.lang.String align;
    /**
      * Sets or retrieves the number of columns in the group.
      */
    public double span;
    /**
      * Sets or retrieves the width of the object.
      */
    public java.lang.Object width;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLTableColElement prototype;
    public HTMLTableColElement(){}
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

