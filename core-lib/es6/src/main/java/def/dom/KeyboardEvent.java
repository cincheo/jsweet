package def.dom;

public class KeyboardEvent extends UIEvent {
    public java.lang.Boolean altKey;
    @jsweet.lang.Name("char")
    public java.lang.String Char;
    public double charCode;
    public java.lang.Boolean ctrlKey;
    public java.lang.String key;
    public double keyCode;
    public java.lang.String locale;
    public double location;
    public java.lang.Boolean metaKey;
    public java.lang.Boolean repeat;
    public java.lang.Boolean shiftKey;
    public double which;
    native public java.lang.Boolean getModifierState(java.lang.String keyArg);
    native public void initKeyboardEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, java.lang.String keyArg, double locationArg, java.lang.String modifiersListArg, java.lang.Boolean repeat, java.lang.String locale);
    public double DOM_KEY_LOCATION_JOYSTICK;
    public double DOM_KEY_LOCATION_LEFT;
    public double DOM_KEY_LOCATION_MOBILE;
    public double DOM_KEY_LOCATION_NUMPAD;
    public double DOM_KEY_LOCATION_RIGHT;
    public double DOM_KEY_LOCATION_STANDARD;
    public static KeyboardEvent prototype;
    public KeyboardEvent(java.lang.String typeArg, KeyboardEventInit eventInitDict){}
    public KeyboardEvent(java.lang.String typeArg){}
    protected KeyboardEvent(){}
}

