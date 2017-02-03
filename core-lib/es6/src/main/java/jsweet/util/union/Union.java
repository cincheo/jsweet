/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
