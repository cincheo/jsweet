package def.dom;
public class HTMLAreaElement extends HTMLElement {
    /**
      * Sets or retrieves a text alternative to the graphic.
      */
    public String alt;
    /**
      * Sets or retrieves the coordinates of the object.
      */
    public String coords;
    /**
      * Sets or retrieves the subsection of the href property that follows the number sign (#).
      */
    public String hash;
    /**
      * Sets or retrieves the hostname and port number of the location or URL.
      */
    public String host;
    /**
      * Sets or retrieves the host name part of the location or URL. 
      */
    public String hostname;
    /**
      * Sets or retrieves a destination URL or an anchor point.
      */
    public String href;
    /**
      * Sets or gets whether clicks in this region cause action.
      */
    public Boolean noHref;
    /**
      * Sets or retrieves the file name or path specified by the object.
      */
    public String pathname;
    /**
      * Sets or retrieves the port number associated with a URL.
      */
    public String port;
    /**
      * Sets or retrieves the protocol portion of a URL.
      */
    public String protocol;
    public String rel;
    /**
      * Sets or retrieves the substring of the href property that follows the question mark.
      */
    public String search;
    /**
      * Sets or retrieves the shape of the object.
      */
    public String shape;
    /**
      * Sets or retrieves the window or frame at which to target content.
      */
    public String target;
    /** 
      * Returns a string representation of an object.
      */
    native public String toString();
    public static HTMLAreaElement prototype;
    public HTMLAreaElement(){}
}

