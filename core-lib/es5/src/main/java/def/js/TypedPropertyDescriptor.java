package def.js;
@jsweet.lang.Interface
public abstract class TypedPropertyDescriptor<T> extends def.js.Object {
    @jsweet.lang.Optional
    public java.lang.Boolean enumerable;
    @jsweet.lang.Optional
    public java.lang.Boolean configurable;
    @jsweet.lang.Optional
    public java.lang.Boolean writable;
    @jsweet.lang.Optional
    public T value;
    @jsweet.lang.Optional
    public java.util.function.Supplier<T> get;
    @jsweet.lang.Optional
    public java.util.function.Consumer<T> set;
}

