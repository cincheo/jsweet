package def.dom;
@jsweet.lang.Interface
public abstract class MouseEventInit extends SharedKeyboardAndMouseEventInit {
    @jsweet.lang.Optional
    public double screenX;
    @jsweet.lang.Optional
    public double screenY;
    @jsweet.lang.Optional
    public double clientX;
    @jsweet.lang.Optional
    public double clientY;
    @jsweet.lang.Optional
    public double button;
    @jsweet.lang.Optional
    public double buttons;
    @jsweet.lang.Optional
    public EventTarget relatedTarget;
}

