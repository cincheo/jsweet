package def.dom;
public class XPathEvaluator extends def.js.Object {
    native public XPathExpression createExpression(String expression, XPathNSResolver resolver);
    native public XPathNSResolver createNSResolver(Node nodeResolver);
    native public XPathResult evaluate(String expression, Node contextNode, XPathNSResolver resolver, double type, XPathResult result);
    public static XPathEvaluator prototype;
    public XPathEvaluator(){}
    native public XPathNSResolver createNSResolver();
}

