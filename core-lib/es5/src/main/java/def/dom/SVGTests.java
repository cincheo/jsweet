package def.dom;
@jsweet.lang.Interface
public abstract class SVGTests extends def.js.Object {
    public SVGStringList requiredExtensions;
    public SVGStringList requiredFeatures;
    public SVGStringList systemLanguage;
    native public Boolean hasExtension(String extension);
}

