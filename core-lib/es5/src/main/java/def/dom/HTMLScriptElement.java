package def.dom;
public class HTMLScriptElement extends HTMLElement {
    public Boolean async;
    /**
      * Sets or retrieves the character set used to encode the object.
      */
    public String charset;
    /**
      * Sets or retrieves the status of the script.
      */
    public Boolean defer;
    /**
      * Sets or retrieves the event for which the script is written. 
      */
    public String event;
    /** 
      * Sets or retrieves the object that is bound to the event script.
      */
    public String htmlFor;
    /**
      * Retrieves the URL to an external file that contains the source code or data.
      */
    public String src;
    /**
      * Retrieves or sets the text of the object as a string. 
      */
    public String text;
    /**
      * Sets or retrieves the MIME type for the associated scripting engine.
      */
    public String type;
    public static HTMLScriptElement prototype;
    public HTMLScriptElement(){}
}

