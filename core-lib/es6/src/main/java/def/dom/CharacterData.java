package def.dom;

@jsweet.lang.Extends({ChildNode.class})
public class CharacterData extends Node {
    public java.lang.String data;
    public double length;
    native public void appendData(java.lang.String arg);
    native public void deleteData(double offset, double count);
    native public void insertData(double offset, java.lang.String arg);
    native public void replaceData(double offset, double count, java.lang.String arg);
    native public java.lang.String substringData(double offset, double count);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static CharacterData prototype;
    public CharacterData(){}
    native public void remove();
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

