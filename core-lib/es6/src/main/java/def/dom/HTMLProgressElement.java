package def.dom;
public class HTMLProgressElement extends HTMLElement {
    /**
      * Retrieves a reference to the form that the object is embedded in.
      */
    public HTMLFormElement form;
    /**
      * Defines the maximum, or "done" value for a progress element.
      */
    public double max;
    /**
      * Returns the quotient of value/max when the value attribute is set (determinate progress bar), or -1 when the value attribute is missing (indeterminate progress bar).
      */
    public double position;
    /**
      * Sets or gets the current value of a progress element. The value must be a non-negative number between 0 and the max value.
      */
    public double value;
    public static HTMLProgressElement prototype;
    public HTMLProgressElement(){}
}

