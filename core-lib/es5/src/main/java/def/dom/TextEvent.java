package def.dom;
public class TextEvent extends UIEvent {
    public String data;
    public double inputMethod;
    public String locale;
    native public void initTextEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, String dataArg, double inputMethod, String locale);
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

