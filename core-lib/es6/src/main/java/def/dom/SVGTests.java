package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class SVGTests extends def.js.Object {
    public SVGStringList requiredExtensions;
    public SVGStringList requiredFeatures;
    public SVGStringList systemLanguage;
    native public java.lang.Boolean hasExtension(java.lang.String extension);
}

