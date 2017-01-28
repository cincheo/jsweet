package def.dom;

import def.js.Object;

public class XPathEvaluator extends def.js.Object {
    native public XPathExpression createExpression(java.lang.String expression, XPathNSResolver resolver);
    native public XPathNSResolver createNSResolver(Node nodeResolver);
    native public XPathResult evaluate(java.lang.String expression, Node contextNode, XPathNSResolver resolver, double type, XPathResult result);
    public static XPathEvaluator prototype;
    public XPathEvaluator(){}
    native public XPathNSResolver createNSResolver();
}

