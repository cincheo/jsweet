package def.js;
import jsweet.util.union.Union3;
@jsweet.lang.Interface
public abstract class ProxyHandler<T> extends def.js.Object {
    native public java.lang.Boolean setPrototypeOf(T target, java.lang.Object v);
    native public java.lang.Boolean has(T target, java.lang.String p);
    native public Object get(T target, java.lang.String p, java.lang.Object receiver);
    native public java.lang.Boolean set(T target, java.lang.String p, java.lang.Object value, java.lang.Object receiver);
    native public java.lang.Boolean deleteProperty(T target, java.lang.String p);
    native public Union3<java.lang.String,Double,String>[] enumerate(T target);
    native public Union3<java.lang.String,Double,String>[] ownKeys(T target);
    native public Object $apply(T target, java.lang.Object thisArg, java.lang.Object argArray);
    native public Object construct(T target, java.lang.Object thisArg, java.lang.Object argArray);
    native public Object $apply(T target, java.lang.Object thisArg);
    native public Object construct(T target, java.lang.Object thisArg);
    native public PropertyDescriptor getOwnPropertyDescriptor(T target, double p);
    native public PropertyDescriptor getOwnPropertyDescriptor(T target, String p);
    native public java.lang.Boolean has(T target, String p);
    native public java.lang.Boolean has(T target, double p);
    native public Object get(T target, String p, java.lang.Object receiver);
    native public Object get(T target, double p, java.lang.Object receiver);
    native public java.lang.Boolean set(T target, double p, java.lang.Object value, java.lang.Object receiver);
    native public java.lang.Boolean set(T target, String p, java.lang.Object value, java.lang.Object receiver);
    native public java.lang.Boolean deleteProperty(T target, String p);
    native public java.lang.Boolean deleteProperty(T target, double p);
    native public java.lang.Boolean defineProperty(T target, double p, PropertyDescriptor attributes);
}

