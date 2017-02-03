package def.dom;

public class HTMLTextAreaElement extends HTMLElement {
    /**
      * Provides a way to direct a user to a specific field when a document loads. This can provide both direction and convenience for a user, reducing the need to click or tab to a field when a page opens. This attribute is true when present on an element, and false when missing.
      */
    public java.lang.Boolean autofocus;
    /**
      * Sets or retrieves the width of the object.
      */
    public double cols;
    /**
      * Sets or retrieves the initial contents of the object.
      */
    public java.lang.String defaultValue;
    public java.lang.Boolean disabled;
    /**
      * Retrieves a reference to the form that the object is embedded in.
      */
    public HTMLFormElement form;
    /**
      * Sets or retrieves the maximum number of characters that the user can enter in a text control.
      */
    public double maxLength;
    /**
      * Sets or retrieves the name of the object.
      */
    public java.lang.String name;
    /**
      * Gets or sets a text string that is displayed in an input field as a hint or prompt to users as the format or type of information they need to enter.The text appears in an input field until the user puts focus on the field.
      */
    public java.lang.String placeholder;
    /**
      * Sets or retrieves the value indicated whether the content of the object is read-only.
      */
    public java.lang.Boolean readOnly;
    /**
      * When present, marks an element that can't be submitted without a value.
      */
    public java.lang.Boolean required;
    /**
      * Sets or retrieves the number of horizontal rows contained in the object.
      */
    public double rows;
    /**
      * Gets or sets the end position or offset of a text selection.
      */
    public double selectionEnd;
    /**
      * Gets or sets the starting position or offset of a text selection.
      */
    public double selectionStart;
    /**
      * Sets or retrieves the value indicating whether the control is selected.
      */
    public java.lang.Object status;
    /**
      * Retrieves the type of control.
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
      * Retrieves or sets the text in the entry field of the textArea element.
      */
    public java.lang.String value;
    /**
      * Returns whether an element will successfully validate based on forms validation rules and constraints.
      */
    public java.lang.Boolean willValidate;
    /**
      * Sets or retrieves how to handle wordwrapping in the object.
      */
    public java.lang.String wrap;
    /**
      * Returns whether a form will validate when it is submitted, without having to submit it.
      */
    native public java.lang.Boolean checkValidity();
    /**
      * Creates a TextRange object for the element.
      */
    native public TextRange createTextRange();
    /**
      * Highlights the input area of a form element.
      */
    native public void select();
    /**
      * Sets a custom error message that is displayed when a form is submitted.
      * @param error Sets a custom error message that is displayed when a form is submitted.
      */
    native public void setCustomValidity(java.lang.String error);
    /**
      * Sets the start and end positions of a selection in a text field.
      * @param start The offset into the text field for the start of the selection.
      * @param end The offset into the text field for the end of the selection.
      */
    native public void setSelectionRange(double start, double end);
    public static HTMLTextAreaElement prototype;
    public HTMLTextAreaElement(){}
}

