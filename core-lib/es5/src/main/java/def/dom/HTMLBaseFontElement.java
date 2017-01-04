package def.dom;
@jsweet.lang.Extends({DOML2DeprecatedColorProperty.class})
public class HTMLBaseFontElement extends HTMLElement {
    /**
      * Sets or retrieves the current typeface family.
      */
    public String face;
    /**
      * Sets or retrieves the font size of the object.
      */
    public double size;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static HTMLBaseFontElement prototype;
    public HTMLBaseFontElement(){}
    public String color;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

