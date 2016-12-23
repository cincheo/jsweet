package def.dom;
public class HTMLAppletElement extends HTMLElement {
    /**
      * Retrieves a string of the URL where the object tag can be found. This is often the href of the document that the object is in, or the value set by a base element.
      */
    public String BaseHref;
    public String align;
    /**
      * Sets or retrieves a text alternative to the graphic.
      */
    public String alt;
    /**
      * Gets or sets the optional alternative HTML script to execute if the object fails to load.
      */
    public String altHtml;
    /**
      * Sets or retrieves a character string that can be used to implement your own archive functionality for the object.
      */
    public String archive;
    public String border;
    public String code;
    /**
      * Sets or retrieves the URL of the component.
      */
    public String codeBase;
    /**
      * Sets or retrieves the Internet media type for the code associated with the object.
      */
    public String codeType;
    /**
      * Address of a pointer to the document this page or frame contains. If there is no document, then null will be returned.
      */
    public Document contentDocument;
    /**
      * Sets or retrieves the URL that references the data of the object.
      */
    public String data;
    /**
      * Sets or retrieves a character string that can be used to implement your own declare functionality for the object.
      */
    public Boolean declare;
    public HTMLFormElement form;
    /**
      * Sets or retrieves the height of the object.
      */
    public String height;
    public double hspace;
    /**
      * Sets or retrieves the shape of the object.
      */
    public String name;
    public String object;
    /**
      * Sets or retrieves a message to be displayed while an object is loading.
      */
    public String standby;
    /**
      * Returns the content type of the object.
      */
    public String type;
    /**
      * Sets or retrieves the URL, often with a bookmark extension (#name), to use as a client-side image map.
      */
    public String useMap;
    public double vspace;
    public double width;
    public static HTMLAppletElement prototype;
    public HTMLAppletElement(){}
}

