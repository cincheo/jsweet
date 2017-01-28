package def.dom;

public class TextEvent extends UIEvent {
    public java.lang.String data;
    public double inputMethod;
    public java.lang.String locale;
    native public void initTextEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, java.lang.String dataArg, double inputMethod, java.lang.String locale);
    public double DOM_INPUT_METHOD_DROP;
    public double DOM_INPUT_METHOD_HANDWRITING;
    public double DOM_INPUT_METHOD_IME;
    public double DOM_INPUT_METHOD_KEYBOARD;
    public double DOM_INPUT_METHOD_MULTIMODAL;
    public double DOM_INPUT_METHOD_OPTION;
    public double DOM_INPUT_METHOD_PASTE;
    public double DOM_INPUT_METHOD_SCRIPT;
    public double DOM_INPUT_METHOD_UNKNOWN;
    public double DOM_INPUT_METHOD_VOICE;
    public static TextEvent prototype;
    public TextEvent(){}
}

