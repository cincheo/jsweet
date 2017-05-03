package def.js;
import jsweet.util.union.Union3;
/** Utility class. */
public class Reflect {
    private Reflect(){}
    native public static java.lang.Object applyStatic(Function target, java.lang.Object thisArgument, ArrayLike<?> argumentsList);
    native public static java.lang.Object construct(Function target, ArrayLike<?> argumentsList);
    native public static java.lang.Boolean defineProperty(java.lang.Object target, java.lang.String propertyKey, PropertyDescriptor attributes);
    native public static java.lang.Boolean deleteProperty(java.lang.Object target, java.lang.String propertyKey);
    native public static IterableIterator<?> enumerate(java.lang.Object target);
    native public static java.lang.Object get(java.lang.Object target, java.lang.String propertyKey, java.lang.Object receiver);
    native public static PropertyDescriptor getOwnPropertyDescriptor(java.lang.Object target, java.lang.String propertyKey);
    native public static Object getPrototypeOf(java.lang.Object target);
    native public static java.lang.Boolean has(java.lang.Object target, java.lang.String propertyKey);
    native public static java.lang.Boolean has(java.lang.Object target, String propertyKey);
    native public static java.lang.Boolean isExtensible(java.lang.Object target);
    native public static Array<Union3<java.lang.String,Double,String>> ownKeys(java.lang.Object target);
    native public static java.lang.Boolean preventExtensions(java.lang.Object target);
    native public static java.lang.Boolean set(java.lang.Object target, java.lang.String propertyKey, java.lang.Object value, java.lang.Object receiver);
    native public static java.lang.Boolean setPrototypeOf(java.lang.Object target, java.lang.Object proto);
    native public static java.lang.Object get(java.lang.Object target, java.lang.String propertyKey);
    native public static java.lang.Boolean set(java.lang.Object target, java.lang.String propertyKey, java.lang.Object value);
    native public static java.lang.Boolean defineProperty(java.lang.Object target, double propertyKey, PropertyDescriptor attributes);
    native public static java.lang.Boolean defineProperty(java.lang.Object target, String propertyKey, PropertyDescriptor attributes);
    native public static java.lang.Boolean deleteProperty(java.lang.Object target, double propertyKey);
    native public static java.lang.Boolean deleteProperty(java.lang.Object target, String propertyKey);
    native public static java.lang.Object get(java.lang.Object target, String propertyKey, java.lang.Object receiver);
    native public static java.lang.Object get(java.lang.Object target, double propertyKey, java.lang.Object receiver);
    native public static PropertyDescriptor getOwnPropertyDescriptor(java.lang.Object target, double propertyKey);
    native public static PropertyDescriptor getOwnPropertyDescriptor(java.lang.Object target, String propertyKey);
    native public static java.lang.Boolean set(java.lang.Object target, double propertyKey, java.lang.Object value, java.lang.Object receiver);
    native public static java.lang.Boolean set(java.lang.Object target, String propertyKey, java.lang.Object value, java.lang.Object receiver);
    native public static java.lang.Object get(java.lang.Object target, String propertyKey);
    native public static java.lang.Object get(java.lang.Object target, double propertyKey);
    native public static java.lang.Boolean set(java.lang.Object target, double propertyKey, java.lang.Object value);
    native public static java.lang.Boolean set(java.lang.Object target, String propertyKey, java.lang.Object value);
    native public static java.lang.Object applyStatic(Function target, java.lang.Object thisArgument, java.lang.Object[] argumentsList);
    native public static java.lang.Object construct(Function target, java.lang.Object[] argumentsList);
}

