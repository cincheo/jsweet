package def.dom;

@jsweet.lang.Extends({NodeSelector.class})
public class DocumentFragment extends Node {
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static DocumentFragment prototype;
    public DocumentFragment(){}
    native public Element querySelector(java.lang.String selectors);
    native public NodeList querySelectorAll(java.lang.String selectors);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

