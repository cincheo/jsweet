package def.dom;

@jsweet.lang.Extends({DOML2DeprecatedColorProperty.class})
public class HTMLBaseFontElement extends HTMLElement {
    /**
      * Sets or retrieves the current typeface family.
      */
    public java.lang.String face;
    /**
      * Sets or retrieves the font size of the object.
      */
    public double size;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLBaseFontElement prototype;
    public HTMLBaseFontElement(){}
    public java.lang.String color;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

