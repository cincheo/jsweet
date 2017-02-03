package def.dom.intl;
@jsweet.lang.Interface
public abstract class ResolvedNumberFormatOptions extends def.js.Object {
    public String locale;
    public String numberingSystem;
    public String style;
    @jsweet.lang.Optional
    public String currency;
    @jsweet.lang.Optional
    public String currencyDisplay;
    public double minimumintegerDigits;
    public double minimumFractionDigits;
    public double maximumFractionDigits;
    @jsweet.lang.Optional
    public double minimumSignificantDigits;
    @jsweet.lang.Optional
    public double maximumSignificantDigits;
    public Boolean useGrouping;
}

