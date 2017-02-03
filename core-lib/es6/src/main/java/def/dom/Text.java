package def.dom;

public class Text extends CharacterData {
    public java.lang.String wholeText;
    native public Text replaceWholeText(java.lang.String content);
    native public Text splitText(double offset);
    public static Text prototype;
    public Text(){}
}

