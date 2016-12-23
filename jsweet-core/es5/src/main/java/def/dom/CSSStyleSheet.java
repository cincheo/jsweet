package def.dom;
public class CSSStyleSheet extends StyleSheet {
    public CSSRuleList cssRules;
    public String cssText;
    public String href;
    public String id;
    public StyleSheetList imports;
    public Boolean isAlternate;
    public Boolean isPrefAlternate;
    public CSSRule ownerRule;
    public Element owningElement;
    public StyleSheetPageList pages;
    public Boolean readOnly;
    public CSSRuleList rules;
    native public double addImport(String bstrURL, double lIndex);
    native public double addPageRule(String bstrSelector, String bstrStyle, double lIndex);
    native public double addRule(String bstrSelector, String bstrStyle, double lIndex);
    native public void deleteRule(double index);
    native public double insertRule(String rule, double index);
    native public void removeImport(double lIndex);
    native public void removeRule(double lIndex);
    public static CSSStyleSheet prototype;
    public CSSStyleSheet(){}
    native public double addImport(String bstrURL);
    native public double addPageRule(String bstrSelector, String bstrStyle);
    native public double addRule(String bstrSelector, String bstrStyle);
    native public double addRule(String bstrSelector);
    native public void deleteRule();
    native public double insertRule(String rule);
}

