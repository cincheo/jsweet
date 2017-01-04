package def.dom;
@jsweet.lang.Interface
public abstract class SVGLocatable extends def.js.Object {
    public SVGElement farthestViewportElement;
    public SVGElement nearestViewportElement;
    native public SVGRect getBBox();
    native public SVGMatrix getCTM();
    native public SVGMatrix getScreenCTM();
    native public SVGMatrix getTransformToElement(SVGElement element);
}

