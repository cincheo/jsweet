package def.dom;
@jsweet.lang.Extends({DOML2DeprecatedColorProperty.class,DOML2DeprecatedSizeProperty.class})
public class HTMLFontElement extends HTMLElement {
    /**
      * Sets or retrieves the current typeface family.
      */
    public String face;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static HTMLFontElement prototype;
    public HTMLFontElement(){}
    public String color;
    public double size;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

