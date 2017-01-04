package def.dom;
@jsweet.lang.Extends({GetSVGDocument.class})
public class HTMLEmbedElement extends HTMLElement {
    /**
      * Sets or retrieves the height of the object.
      */
    public String height;
    public Object hidden;
    /**
      * Gets or sets whether the DLNA PlayTo device is available.
      */
    public Boolean msPlayToDisabled;
    /**
      * Gets or sets the path to the preferred media source. This enables the Play To target device to stream the media content, which can be DRM protected, from a different location, such as a cloud media server.
      */
    public String msPlayToPreferredSourceUri;
    /**
      * Gets or sets the primary DLNA PlayTo device.
      */
    public Boolean msPlayToPrimary;
    /**
      * Gets the source associated with the media element for use by the PlayToManager.
      */
    public Object msPlayToSource;
    /**
      * Sets or retrieves the name of the object.
      */
    public String name;
    /**
      * Retrieves the palette used for the embedded document.
      */
    public String palette;
    /**
      * Retrieves the URL of the plug-in used to view an embedded document.
      */
    public String pluginspage;
    public String readyState;
    /**
      * Sets or retrieves a URL to be loaded by the object.
      */
    public String src;
    /**
      * Sets or retrieves the height and width units of the embed object.
      */
    public String units;
    /**
      * Sets or retrieves the width of the object.
      */
    public String width;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static HTMLEmbedElement prototype;
    public HTMLEmbedElement(){}
    native public Document getSVGDocument();
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

