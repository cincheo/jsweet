package def.dom;

@jsweet.lang.Extends({LinkStyle.class})
public class HTMLStyleElement extends HTMLElement {
    /**
      * Sets or retrieves the media type.
      */
    public java.lang.String media;
    /**
      * Retrieves the CSS language in which the style sheet is written.
      */
    public java.lang.String type;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLStyleElement prototype;
    public HTMLStyleElement(){}
    public StyleSheet sheet;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

