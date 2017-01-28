package def.dom;

public class HTMLButtonElement extends HTMLElement {
    /**
      * Provides a way to direct a user to a specific field when a document loads. This can provide both direction and convenience for a user, reducing the need to click or tab to a field when a page opens. This attribute is true when present on an element, and false when missing.
      */
    public java.lang.Boolean autofocus;
    public java.lang.Boolean disabled;
    /**
      * Retrieves a reference to the form that the object is embedded in.
      */
    public HTMLFormElement form;
    /**
      * Overrides the action attribute (where the data on a form is sent) on the parent form element.
      */
    public java.lang.String formAction;
    /**
      * Used to override the encoding (formEnctype attribute) specified on the form element.
      */
    public java.lang.String formEnctype;
    /**
      * Overrides the submit method attribute previously specified on a form element.
      */
    public java.lang.String formMethod;
    /**
      * Overrides any validation or required attributes on a form or form elements to allow it to be submitted without validation. This can be used to create a "save draft"-type submit option.
      */
    public java.lang.String formNoValidate;
    /**
      * Overrides the target attribute on a form element.
      */
    public java.lang.String formTarget;
    /** 
      * Sets or retrieves the name of the object.
      */
    public java.lang.String name;
    public java.lang.Object status;
    /**
      * Gets the classification and default behavior of the button.
      */
    public java.lang.String type;
    /**
      * Returns the error message that would be displayed if the user submits the form, or an empty string if no error message. It also triggers the standard error message, such as "this is a required field". The result is that the user sees validation messages without actually submitting.
      */
    public java.lang.String validationMessage;
    /**
      * Returns a  ValidityState object that represents the validity states of an element.
      */
    public ValidityState validity;
    /** 
      * Sets or retrieves the default or selected value of the control.
      */
    public java.lang.String value;
    /**
      * Returns whether an element will successfully validate based on forms validation rules and constraints.
      */
    public java.lang.Boolean willValidate;
    /**
      * Returns whether a form will validate when it is submitted, without having to submit it.
      */
    native public java.lang.Boolean checkValidity();
    /**
      * Creates a TextRange object for the element.
      */
    native public TextRange createTextRange();
    /**
      * Sets a custom error message that is displayed when a form is submitted.
      * @param error Sets a custom error message that is displayed when a form is submitted.
      */
    native public void setCustomValidity(java.lang.String error);
    public static HTMLButtonElement prototype;
    public HTMLButtonElement(){}
}

