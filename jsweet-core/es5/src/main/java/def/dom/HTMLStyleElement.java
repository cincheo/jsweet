package def.dom;
@jsweet.lang.Extends({LinkStyle.class})
public class HTMLStyleElement extends HTMLElement {
    /**
      * Sets or retrieves the media type.
      */
    public String media;
    /**
      * Retrieves the CSS language in which the style sheet is written.
      */
    public String type;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static HTMLStyleElement prototype;
    public HTMLStyleElement(){}
    public StyleSheet sheet;
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

