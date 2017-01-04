package def.dom;
public class CSSKeyframesRule extends CSSRule {
    public CSSRuleList cssRules;
    public String name;
    native public void appendRule(String rule);
    native public void deleteRule(String rule);
    native public CSSKeyframeRule findRule(String rule);
    public static CSSKeyframesRule prototype;
    public CSSKeyframesRule(){}
}

