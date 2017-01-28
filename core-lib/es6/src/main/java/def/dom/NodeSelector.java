package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class NodeSelector extends def.js.Object {
    native public Element querySelector(java.lang.String selectors);
    native public NodeList querySelectorAll(java.lang.String selectors);
}

