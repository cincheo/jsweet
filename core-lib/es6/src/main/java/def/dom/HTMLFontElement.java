package def.dom;

@jsweet.lang.Extends({DOML2DeprecatedColorProperty.class,DOML2DeprecatedSizeProperty.class})
public class HTMLFontElement extends HTMLElement {
    /**
      * Sets or retrieves the current typeface family.
      */
    public java.lang.String face;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLFontElement prototype;
    public HTMLFontElement(){}
    public java.lang.String color;
    public double size;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

