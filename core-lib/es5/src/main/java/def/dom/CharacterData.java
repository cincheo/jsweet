package def.dom;
@jsweet.lang.Extends({ChildNode.class})
public class CharacterData extends Node {
    public String data;
    public double length;
    native public void appendData(String arg);
    native public void deleteData(double offset, double count);
    native public void insertData(double offset, String arg);
    native public void replaceData(double offset, double count, String arg);
    native public String substringData(double offset, double count);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static CharacterData prototype;
    public CharacterData(){}
    native public void remove();
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

