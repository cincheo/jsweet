package def.dom;
public class HTMLImageElement extends HTMLElement {
    /**
      * Sets or retrieves how the object is aligned with adjacent text.
      */
    public java.lang.String align;
    /**
      * Sets or retrieves a text alternative to the graphic.
      */
    public java.lang.String alt;
    /**
      * Specifies the properties of a border drawn around an object.
      */
    public java.lang.String border;
    /**
      * Retrieves whether the object is fully loaded.
      */
    public java.lang.Boolean complete;
    public java.lang.String crossOrigin;
    public java.lang.String currentSrc;
    /**
      * Sets or retrieves the height of the object.
      */
    public double height;
    /**
      * Sets or retrieves the width of the border to draw around the object.
      */
    public double hspace;
    /**
      * Sets or retrieves whether the image is a server-side image map.
      */
    public java.lang.Boolean isMap;
    /**
      * Sets or retrieves a Uniform Resource Identifier (URI) to a long description of the object.
      */
    public java.lang.String longDesc;
    /**
      * Gets or sets whether the DLNA PlayTo device is available.
      */
    public java.lang.Boolean msPlayToDisabled;
    public java.lang.String msPlayToPreferredSourceUri;
    /**
      * Gets or sets the primary DLNA PlayTo device.
      */
    public java.lang.Boolean msPlayToPrimary;
    /**
      * Gets the source associated with the media element for use by the PlayToManager.
      */
    public java.lang.Object msPlayToSource;
    /**
      * Sets or retrieves the name of the object.
      */
    public java.lang.String name;
    /**
      * The original height of the image resource before sizing.
      */
    public double naturalHeight;
    /**
      * The original width of the image resource before sizing.
      */
    public double naturalWidth;
    /**
      * The address or URL of the a media resource that is to be considered.
      */
    public java.lang.String src;
    public java.lang.String srcset;
    /**
      * Sets or retrieves the URL, often with a bookmark extension (#name), to use as a client-side image map.
      */
    public java.lang.String useMap;
    /**
      * Sets or retrieves the vertical margin for the object.
      */
    public double vspace;
    /**
      * Sets or retrieves the width of the object.
      */
    public double width;
    public double x;
    public double y;
    native public java.lang.Object msGetAsCastingSource();
    public static HTMLImageElement prototype;
    public HTMLImageElement(){}
    native public static HTMLImageElement create();
}

