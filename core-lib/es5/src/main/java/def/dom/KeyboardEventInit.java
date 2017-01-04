package def.dom;
@jsweet.lang.Interface
public abstract class KeyboardEventInit extends SharedKeyboardAndMouseEventInit {
    @jsweet.lang.Optional
    public String key;
    @jsweet.lang.Optional
    public double location;
    @jsweet.lang.Optional
    public Boolean repeat;
}

