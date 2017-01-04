package def.dom;
import def.js.Date;
public class HTMLInputElement extends HTMLElement {
    /**
      * Sets or retrieves a comma-separated list of content types.
      */
    public String accept;
    /**
      * Sets or retrieves how the object is aligned with adjacent text.
      */
    public String align;
    /**
      * Sets or retrieves a text alternative to the graphic.
      */
    public String alt;
    /**
      * Specifies whether autocomplete is applied to an editable text field.
      */
    public String autocomplete;
    /**
      * Provides a way to direct a user to a specific field when a document loads. This can provide both direction and convenience for a user, reducing the need to click or tab to a field when a page opens. This attribute is true when present on an element, and false when missing.
      */
    public Boolean autofocus;
    /**
      * Sets or retrieves the width of the border to draw around the object.
      */
    public String border;
    /**
      * Sets or retrieves the state of the check box or radio button.
      */
    public Boolean checked;
    /**
      * Retrieves whether the object is fully loaded.
      */
    public Boolean complete;
    /**
      * Sets or retrieves the state of the check box or radio button.
      */
    public Boolean defaultChecked;
    /**
      * Sets or retrieves the initial contents of the object.
      */
    public String defaultValue;
    public Boolean disabled;
    /**
      * Returns a FileList object on a file type input object.
      */
    public FileList files;
    /**
      * Retrieves a reference to the form that the object is embedded in. 
      */
    public HTMLFormElement form;
    /**
      * Overrides the action attribute (where the data on a form is sent) on the parent form element.
      */
    public String formAction;
    /**
      * Used to override the encoding (formEnctype attribute) specified on the form element.
      */
    public String formEnctype;
    /**
      * Overrides the submit method attribute previously specified on a form element.
      */
    public String formMethod;
    /**
      * Overrides any validation or required attributes on a form or form elements to allow it to be submitted without validation. This can be used to create a "save draft"-type submit option.
      */
    public String formNoValidate;
    /**
      * Overrides the target attribute on a form element.
      */
    public String formTarget;
    /**
      * Sets or retrieves the height of the object.
      */
    public String height;
    /**
      * Sets or retrieves the width of the border to draw around the object.
      */
    public double hspace;
    public Boolean indeterminate;
    /**
      * Specifies the ID of a pre-defined datalist of options for an input element.
      */
    public HTMLElement list;
    /**
      * Defines the maximum acceptable value for an input element with type="number".When used with the min and step attributes, lets you control the range and increment (such as only even numbers) that the user can enter into an input field.
      */
    public String max;
    /**
      * Sets or retrieves the maximum number of characters that the user can enter in a text control.
      */
    public double maxLength;
    /**
      * Defines the minimum acceptable value for an input element with type="number". When used with the max and step attributes, lets you control the range and increment (such as even numbers only) that the user can enter into an input field.
      */
    public String min;
    /**
      * Sets or retrieves the Boolean value indicating whether multiple items can be selected from a list.
      */
    public Boolean multiple;
    /**
      * Sets or retrieves the name of the object.
      */
    public String name;
    /**
      * Gets or sets a string containing a regular expression that the user's input must match.
      */
    public String pattern;
    /**
      * Gets or sets a text string that is displayed in an input field as a hint or prompt to users as the format or type of information they need to enter.The text appears in an input field until the user puts focus on the field.
      */
    public String placeholder;
    public Boolean readOnly;
    /**
      * When present, marks an element that can't be submitted without a value.
      */
    public Boolean required;
    /**
      * Gets or sets the end position or offset of a text selection.
      */
    public double selectionEnd;
    /**
      * Gets or sets the starting position or offset of a text selection.
      */
    public double selectionStart;
    public double size;
    /**
      * The address or URL of the a media resource that is to be considered.
      */
    public String src;
    public Boolean status;
    /**
      * Defines an increment or jump between values that you want to allow the user to enter. When used with the max and min attributes, lets you control the range and increment (for example, allow only even numbers) that the user can enter into an input field.
      */
    public String step;
    /**
      * Returns the content type of the object.
      */
    public String type;
    /**
      * Sets or retrieves the URL, often with a bookmark extension (#name), to use as a client-side image map.
      */
    public String useMap;
    /**
      * Returns the error message that would be displayed if the user submits the form, or an empty string if no error message. It also triggers the standard error message, such as "this is a required field". The result is that the user sees validation messages without actually submitting.
      */
    public String validationMessage;
    /**
      * Returns a  ValidityState object that represents the validity states of an element.
      */
    public ValidityState validity;
    /**
      * Returns the value of the data at the cursor's current position.
      */
    public String value;
    public Date valueAsDate;
    /**
      * Returns the input field value as a number.
      */
    public double valueAsNumber;
    /**
      * Sets or retrieves the vertical margin for the object.
      */
    public double vspace;
    /**
      * Sets or retrieves the width of the object.
      */
    public String width;
    /**
      * Returns whether an element will successfully validate based on forms validation rules and constraints.
      */
    public Boolean willValidate;
    /**
      * Returns whether a form will validate when it is submitted, without having to submit it.
      */
    native public Boolean checkValidity();
    /**
      * Creates a TextRange object for the element.
      */
    native public TextRange createTextRange();
    /**
      * Makes the selection equal to the current object.
      */
    native public void select();
    /**
      * Sets a custom error message that is displayed when a form is submitted.
      * @param error Sets a custom error message that is displayed when a form is submitted.
      */
    native public void setCustomValidity(String error);
    /**
      * Sets the start and end positions of a selection in a text field.
      * @param start The offset into the text field for the start of the selection.
      * @param end The offset into the text field for the end of the selection.
      */
    native public void setSelectionRange(double start, double end);
    /**
      * Decrements a range input control's value by the value given by the Step attribute. If the optional parameter is used, it will decrement the input control's step value multiplied by the parameter's value.
      * @param n Value to decrement the value by.
      */
    native public void stepDown(double n);
    /**
      * Increments a range input control's value by the value given by the Step attribute. If the optional parameter is used, will increment the input control's value by that value.
      * @param n Value to increment the value by.
      */
    native public void stepUp(double n);
    public static HTMLInputElement prototype;
    public HTMLInputElement(){}
    /**
      * Decrements a range input control's value by the value given by the Step attribute. If the optional parameter is used, it will decrement the input control's step value multiplied by the parameter's value.
      * @param n Value to decrement the value by.
      */
    native public void stepDown();
    /**
      * Increments a range input control's value by the value given by the Step attribute. If the optional parameter is used, will increment the input control's value by that value.
      * @param n Value to increment the value by.
      */
    native public void stepUp();
}

