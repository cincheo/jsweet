package def.js;
@jsweet.lang.Interface
public abstract class PropertyDescriptor extends def.js.Object {
    @jsweet.lang.Optional
    public java.lang.Boolean configurable;
    @jsweet.lang.Optional
    public java.lang.Boolean enumerable;
    @jsweet.lang.Optional
    public java.lang.Object value;
    @jsweet.lang.Optional
    public java.lang.Boolean writable;
    native public java.lang.Object get();
    native public void set(java.lang.Object v);
}

