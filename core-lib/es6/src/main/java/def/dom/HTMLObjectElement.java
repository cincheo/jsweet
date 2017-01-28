package def.dom;

@jsweet.lang.Extends({GetSVGDocument.class})
public class HTMLObjectElement extends HTMLElement {
    /**
      * Retrieves a string of the URL where the object tag can be found. This is often the href of the document that the object is in, or the value set by a base element.
      */
    public java.lang.String BaseHref;
    public java.lang.String align;
    /**
      * Sets or retrieves a text alternative to the graphic.
      */
    public java.lang.String alt;
    /**
      * Gets or sets the optional alternative HTML script to execute if the object fails to load.
      */
    public java.lang.String altHtml;
    /**
      * Sets or retrieves a character string that can be used to implement your own archive functionality for the object.
      */
    public java.lang.String archive;
    public java.lang.String border;
    /**
      * Sets or retrieves the URL of the file containing the compiled Java class.
      */
    public java.lang.String code;
    /**
      * Sets or retrieves the URL of the component.
      */
    public java.lang.String codeBase;
    /**
      * Sets or retrieves the Internet media type for the code associated with the object.
      */
    public java.lang.String codeType;
    /**
      * Retrieves the document object of the page or frame.
      */
    public Document contentDocument;
    /**
      * Sets or retrieves the URL that references the data of the object.
      */
    public java.lang.String data;
    public java.lang.Boolean declare;
    /**
      * Retrieves a reference to the form that the object is embedded in.
      */
    public HTMLFormElement form;
    /**
      * Sets or retrieves the height of the object.
      */
    public java.lang.String height;
    public double hspace;
    /**
      * Gets or sets whether the DLNA PlayTo device is available.
      */
    public java.lang.Boolean msPlayToDisabled;
    /**
      * Gets or sets the path to the preferred media source. This enables the Play To target device to stream the media content, which can be DRM protected, from a different location, such as a cloud media server.
      */
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
      * Retrieves the contained object.
      */
    public java.lang.Object object;
    public double readyState;
    /**
      * Sets or retrieves a message to be displayed while an object is loading.
      */
    public java.lang.String standby;
    /**
      * Sets or retrieves the MIME type of the object.
      */
    public java.lang.String type;
    /**
      * Sets or retrieves the URL, often with a bookmark extension (#name), to use as a client-side image map.
      */
    public java.lang.String useMap;
    /**
      * Returns the error message that would be displayed if the user submits the form, or an empty string if no error message. It also triggers the standard error message, such as "this is a required field". The result is that the user sees validation messages without actually submitting.
      */
    public java.lang.String validationMessage;
    /**
      * Returns a  ValidityState object that represents the validity states of an element.
      */
    public ValidityState validity;
    public double vspace;
    /**
      * Sets or retrieves the width of the object.
      */
    public java.lang.String width;
    /**
      * Returns whether an element will successfully validate based on forms validation rules and constraints.
      */
    public java.lang.Boolean willValidate;
    /**
      * Returns whether a form will validate when it is submitted, without having to submit it.
      */
    native public java.lang.Boolean checkValidity();
    /**
      * Sets a custom error message that is displayed when a form is submitted.
      * @param error Sets a custom error message that is displayed when a form is submitted.
      */
    native public void setCustomValidity(java.lang.String error);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLObjectElement prototype;
    public HTMLObjectElement(){}
    native public Document getSVGDocument();
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

