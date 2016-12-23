package def.dom;
public class HTMLAllCollection extends HTMLCollection {
    native public Element namedItem(String name);
    public static HTMLAllCollection prototype;
    public HTMLAllCollection(){}
}

