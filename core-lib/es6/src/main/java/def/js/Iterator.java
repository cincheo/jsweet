package def.js;
@jsweet.lang.Interface
public abstract class Iterator<T> extends def.js.Object {
    native public IteratorResult<T> next(java.lang.Object value);
    @jsweet.lang.Name("return")
    native public IteratorResult<T> Return(java.lang.Object value);
    @jsweet.lang.Name("throw")
    native public IteratorResult<T> Throw(java.lang.Object e);
    native public IteratorResult<T> next();
    @jsweet.lang.Name("return")
    native public IteratorResult<T> Return();
    @jsweet.lang.Name("throw")
    native public IteratorResult<T> Throw();
}

