package def.dom;

public class CSSStyleSheet extends StyleSheet {
    public CSSRuleList cssRules;
    public java.lang.String cssText;
    public java.lang.String href;
    public java.lang.String id;
    public StyleSheetList imports;
    public java.lang.Boolean isAlternate;
    public java.lang.Boolean isPrefAlternate;
    public CSSRule ownerRule;
    public Element owningElement;
    public StyleSheetPageList pages;
    public java.lang.Boolean readOnly;
    public CSSRuleList rules;
    native public double addImport(java.lang.String bstrURL, double lIndex);
    native public double addPageRule(java.lang.String bstrSelector, java.lang.String bstrStyle, double lIndex);
    native public double addRule(java.lang.String bstrSelector, java.lang.String bstrStyle, double lIndex);
    native public void deleteRule(double index);
    native public double insertRule(java.lang.String rule, double index);
    native public void removeImport(double lIndex);
    native public void removeRule(double lIndex);
    public static CSSStyleSheet prototype;
    public CSSStyleSheet(){}
    native public double addImport(java.lang.String bstrURL);
    native public double addPageRule(java.lang.String bstrSelector, java.lang.String bstrStyle);
    native public double addRule(java.lang.String bstrSelector, java.lang.String bstrStyle);
    native public double addRule(java.lang.String bstrSelector);
    native public void deleteRule();
    native public double insertRule(java.lang.String rule);
}

