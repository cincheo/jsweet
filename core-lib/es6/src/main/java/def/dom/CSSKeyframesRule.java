package def.dom;
public class CSSKeyframesRule extends CSSRule {
    public CSSRuleList cssRules;
    public java.lang.String name;
    native public void appendRule(java.lang.String rule);
    native public void deleteRule(java.lang.String rule);
    native public CSSKeyframeRule findRule(java.lang.String rule);
    public static CSSKeyframesRule prototype;
    public CSSKeyframesRule(){}
}

