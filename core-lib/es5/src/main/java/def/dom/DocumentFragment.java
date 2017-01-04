package def.dom;
@jsweet.lang.Extends({NodeSelector.class})
public class DocumentFragment extends Node {
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static DocumentFragment prototype;
    public DocumentFragment(){}
    native public Element querySelector(String selectors);
    native public NodeListOf<Element> querySelectorAll(String selectors);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

