package def.dom;

@jsweet.lang.Extends({LinkStyle.class})
public class HTMLLinkElement extends HTMLElement {
    /**
      * Sets or retrieves the character set used to encode the object.
      */
    public java.lang.String charset;
    public java.lang.Boolean disabled;
    /**
      * Sets or retrieves a destination URL or an anchor point.
      */
    public java.lang.String href;
    /**
      * Sets or retrieves the language code of the object.
      */
    public java.lang.String hreflang;
    /**
      * Sets or retrieves the media type.
      */
    public java.lang.String media;
    /**
      * Sets or retrieves the relationship between the object and the destination of the link.
      */
    public java.lang.String rel;
    /**
      * Sets or retrieves the relationship between the object and the destination of the link.
      */
    public java.lang.String rev;
    /**
      * Sets or retrieves the window or frame at which to target content.
      */
    public java.lang.String target;
    /**
      * Sets or retrieves the MIME type of the object.
      */
    public java.lang.String type;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLLinkElement prototype;
    public HTMLLinkElement(){}
    public StyleSheet sheet;
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

