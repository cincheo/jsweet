package def.dom;

@jsweet.lang.Interface
public abstract class PointerEventInit extends MouseEventInit {
    @jsweet.lang.Optional
    public double pointerId;
    @jsweet.lang.Optional
    public double width;
    @jsweet.lang.Optional
    public double height;
    @jsweet.lang.Optional
    public double pressure;
    @jsweet.lang.Optional
    public double tiltX;
    @jsweet.lang.Optional
    public double tiltY;
    @jsweet.lang.Optional
    public java.lang.String pointerType;
    @jsweet.lang.Optional
    public java.lang.Boolean isPrimary;
}

