package def.js;
@jsweet.lang.Interface
public abstract class ProxyConstructor extends def.js.Object {
    native public <T> Revocable<T> revocable(T target, ProxyHandler<T> handler);
    public <T> ProxyConstructor(T target, ProxyHandler<T> handler){}
    /** This is an automatically generated object type (see the source definition). */
    @jsweet.lang.ObjectType
    public static class Revocable<T> extends def.js.Object {
        public T proxy;
        public java.lang.Runnable revoke;
    }
    protected ProxyConstructor(){}
}

