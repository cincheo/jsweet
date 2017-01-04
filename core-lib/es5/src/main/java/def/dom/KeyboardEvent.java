package def.dom;
public class KeyboardEvent extends UIEvent {
    public Boolean altKey;
    @jsweet.lang.Name("char")
    public String Char;
    public double charCode;
    public Boolean ctrlKey;
    public String key;
    public double keyCode;
    public String locale;
    public double location;
    public Boolean metaKey;
    public Boolean repeat;
    public Boolean shiftKey;
    public double which;
    native public Boolean getModifierState(String keyArg);
    native public void initKeyboardEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, String keyArg, double locationArg, String modifiersListArg, Boolean repeat, String locale);
    public double DOM_KEY_LOCATION_JOYSTICK;
    public double DOM_KEY_LOCATION_LEFT;
    public double DOM_KEY_LOCATION_MOBILE;
    public double DOM_KEY_LOCATION_NUMPAD;
    public double DOM_KEY_LOCATION_RIGHT;
    public double DOM_KEY_LOCATION_STANDARD;
    public static KeyboardEvent prototype;
    public KeyboardEvent(String typeArg, KeyboardEventInit eventInitDict){}
    public KeyboardEvent(String typeArg){}
    protected KeyboardEvent(){}
}

