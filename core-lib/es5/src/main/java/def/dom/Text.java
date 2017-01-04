package def.dom;
public class Text extends CharacterData {
    public String wholeText;
    native public Text replaceWholeText(String content);
    native public Text splitText(double offset);
    public static Text prototype;
    public Text(){}
}

