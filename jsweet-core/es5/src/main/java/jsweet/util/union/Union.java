package jsweet.util.union;

/**
 * This interface is an auxiliary type used by JSweet to define
 * <em>union types</em>.
 * 
 * <p>
 * A union type is a type which can be cast to one of its union-ed types. For
 * instance:
 * 
 * <pre>
 * Union&lt;String, Number&gt; u = ...;
 * String s = union(u);
 * // or:
 * Number n = union(u);
 * </pre>
 * 
 * <p>
 * Note that Java will not complain when casting to a type that is not part of
 * the union, but the JSweet transpiler will.
 * 
 * <pre>
 * Union&lt;String, Number&gt; u = ...;
 * Date d = union(u); // compiles in pure Java, but not in JSweet
 * </pre>
 * 
 * <p>
 * When a method expects a union, such as the following <code>m</code> method:
 * 
 * <pre>
 * void m(Union&lt;String, Number&gt; p);
 * </pre>
 * 
 * Then it can be called with a String or a Number, by first translating to a
 * union:
 * 
 * <pre>
 * String s = ...
 * m(union(s));
 * </pre>
 * 
 * <p>
 * In case of union types of more than two types, one can extend the
 * <code>Union</code> interface for accepting more type parameters. For
 * instance, to define a union type that takes 3 arguments, just write:
 * 
 * <pre>
 * public interface Union3<T1, T2, T3> extends Union<Object, Object> {
 * }
 * </pre>
 * 
 * @author Renaud Pawlak
 *
 * @param <T1>
 * @param <T2>
 */
public interface Union<T1, T2> {
}
