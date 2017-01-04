package def.dom;
@jsweet.lang.Interface
public abstract class NodeSelector extends def.js.Object {
    native public Element querySelector(String selectors);
    native public NodeListOf<Element> querySelectorAll(String selectors);
}

