package def.dom;
public class CSSGroupingRule extends CSSRule {
    public CSSRuleList cssRules;
    native public void deleteRule(double index);
    native public double insertRule(java.lang.String rule, double index);
    public static CSSGroupingRule prototype;
    public CSSGroupingRule(){}
    native public void deleteRule();
    native public double insertRule(java.lang.String rule);
}

