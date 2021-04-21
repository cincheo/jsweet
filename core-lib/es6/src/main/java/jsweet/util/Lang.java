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
package jsweet.util;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import def.js.Promise;
import jsweet.util.function.Consumer4;
import jsweet.util.function.Consumer5;
import jsweet.util.function.Consumer6;
import jsweet.util.function.Function4;
import jsweet.util.function.Function5;
import jsweet.util.function.Function6;
import jsweet.util.function.TriConsumer;
import jsweet.util.function.TriFunction;
import jsweet.util.union.Union;
import jsweet.util.union.Union3;
import jsweet.util.union.Union4;

/**
 * A set of helper methods for manipulating JavaScript core lang features and
 * relate them to the Java lang.
 * 
 * <p>
 * Be aware that these functions have no corresponding implementation. They are
 * used mainly for typing and syntax purpose. By convention, most functions
 * starting with $ are syntax macros to support JavaScript syntactic constructs.
 * Most other functions are cast functions to switch from Java to JavaScript
 * objects. The later are erased during the generation process or are translated
 * to TypeScript casts (in any case they have no runtime counterparts).
 * 
 * <p>
 * To work with JSweet, programmers may import these helpers most of the time,
 * as well as JavaScript's {@link def.js.Globals} (and DOM's
 * {@link def.dom.Globals}).
 * 
 * @author Renaud Pawlak
 * @see def.js.Globals
 * @see def.dom.Globals
 */
public final class Lang {

    private static final ThreadLocal<Map<String, Object>> EXPORTED_VARS = new ThreadLocal<>();

    private Lang() {
    }

    /**
     * An accessor to the module id, only when using modules.
     */
    public static final class module {
	public final static String id = null;
    }

    /**
     * Casts a JavaScript array object to a native Java array.
     * 
     * @param array
     *            a JavaScript array
     * @return a Java array
     */
    native public static <T> T[] array(def.js.Array<T> array);

    /**
     * Casts a native Java array to a JavaScript array object.
     * 
     * @param array
     *            a Java array
     * @return a JavaScript array
     */
    native public static <T> def.js.Array<T> array(T[] array);

    /**
     * Casts a native Java array of primitive booleans to a JavaScript array
     * object.
     * 
     * @param array
     *            a Java array
     * @return a JavaScript array
     */
    native public static def.js.Array<Boolean> array(boolean[] array);

    /**
     * Casts a native Java array of primitive ints to a JavaScript array object.
     * 
     * @param array
     *            a Java array
     * @return a JavaScript array
     */
    native public static def.js.Array<Integer> array(int[] array);

    /**
     * Casts a native Java array of primitive doubles to a JavaScript array
     * object.
     * 
     * @param array
     *            a Java array
     * @return a JavaScript array
     */
    native public static def.js.Array<Double> array(double[] array);

    /**
     * Casts a native Java array of primitive shorts to a JavaScript array
     * object.
     * 
     * @param array
     *            a Java array
     * @return a JavaScript array
     */
    native public static def.js.Array<Short> array(short[] array);

    /**
     * Casts a native Java array of primitive bytes to a JavaScript array
     * object.
     * 
     * @param array
     *            a Java array
     * @return a JavaScript array
     */
    native public static def.js.Array<Byte> array(byte[] array);

    /**
     * Casts a native Java array of primitive longs to a JavaScript array
     * object.
     * 
     * @param array
     *            a Java array
     * @return a JavaScript array
     */
    native public static def.js.Array<Long> array(long[] array);

    /**
     * Casts a native Java array of primitive floats to a JavaScript array
     * object.
     * 
     * @param array
     *            a Java array
     * @return a JavaScript array
     */
    native public static def.js.Array<Float> array(float[] array);

    /**
     * Forces JSweet to create a plain JavaScript function rather than an
     * "arrow" function.
     * 
     * <p>
     * Hence, <code>function((a) -> { return a * a; })</code> transpiles to
     * <code>(a) => { return a * a; }</code>. But
     * <code>$noarrow(function((a) -> { return a * a; }))</code> transpiles to
     * <code>function(a) { return a * a; }</code>
     */
    native public static def.js.Function $noarrow(def.js.Function function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Runnable function);

    /**
     * Casts a type to a JavaScript function object.
     * 
     * <p>
     * Indeed, type references are functions in javascript.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Class<?> type);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Consumer<?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Supplier<?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(BiConsumer<?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(TriConsumer<?, ?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Consumer4<?, ?, ?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Consumer5<?, ?, ?, ?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Consumer6<?, ?, ?, ?, ?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Function<?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(BiFunction<?, ?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(TriFunction<?, ?, ?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Function4<?, ?, ?, ?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Function5<?, ?, ?, ?, ?, ?> function);

    /**
     * Casts a functional interface to a JavaScript function object.
     * 
     * <p>
     * Note that this casts allows for a reflective access to the function as a
     * plain object, but it looses the actual function type. Various APIs take
     * function objects as arguments, and this function can be useful in such
     * circumstances.
     * 
     * @see jsweet.util.function
     */
    native public static def.js.Function function(Function6<?, ?, ?, ?, ?, ?, ?> function);

    /**
     * Casts a native Java Boolean to a JavaScript Boolean.
     */
    native public static def.js.Boolean bool(Boolean bool);

    /**
     * Casts back a JavaScript boolean to a Java boolean.
     */
    native public static Boolean bool(def.js.Boolean bool);

    /**
     * Casts a native Java number to a JavaScript Number.
     */
    native public static def.js.Number number(Number number);

    /**
     * Casts back a JavaScript number to a Java integer.
     */
    native public static Integer integer(def.js.Number number);

    /**
     * Casts a JavaScript number to a Java double.
     */
    native public static Double number(def.js.Number number);

    /**
     * Casts a native Java string to a JavaScript string.
     * 
     * <p>
     * By default, JSweet API use plain Java strings so that the program can
     * easily use string literals. However, when the programmer needs to access
     * to runtime string manipulation functions, then need to cast to a
     * JavaScript string, which allows the access to the standard Web API.
     */
    native public static def.js.String string(String string);

    /**
     * Casts a native Java char to a JavaScript string.
     */
    native public static def.js.String string(char c);

    /**
     * Casts back a JavaScript string to a Java string.
     * 
     * <p>
     * By default, JSweet API use plain Java strings so that the program can
     * easily use string literals. However, when the programmer needs to access
     * to runtime string manipulation functions, then need to cast to a
     * JavaScript string, which allows the access to the standard Web API.
     */
    native public static String string(def.js.String string);

    /**
     * Casts a native Java object to a JavaScript object.
     * 
     * <p>
     * By default, JSweet API use plain Java objects. However, when the
     * programmer needs to access to the standard Web API, they need to cast
     * through this function.
     */
    native public static def.js.Object object(Object object);

    /**
     * This syntax macro should be used to create untyped JavaScript
     * objects/maps. It takes a list of key/value pairs, where the keys must be
     * string literals and values are objects.
     * 
     * <p>
     * For instance, the expression:
     * 
     * <pre>
     * $map("responsive", true, "defaultSize", "100px")
     * </pre>
     * 
     * <p>
     * Will be transpiled to:
     * 
     * <pre>
     * {responsive:true, defaultSize:"100px"}
     * </pre>
     * 
     * @param keyValues
     *            the key values pairs that initialize the object (keys must be
     *            string literals)
     * @return an untyped object containing the given key/value pairs
     */
    native public static def.js.Object $map(Object... keyValues);

    /**
     * A syntax macro to construct an array with the given elements.
     * 
     * <p>
     * For instance, the expression:
     * 
     * <pre>
     * $array("a", "b", "c")
     * </pre>
     * 
     * <p>
     * Will be transpiled to:
     * 
     * <pre>
     * ["a", "b", "c"]
     * </pre>
     * 
     * 
     * @param elements
     *            the elements initializing the array
     * @return a new array
     */
    @SafeVarargs
    native public static <E> def.js.Array<E> $array(E... elements);

    /**
     * Uses the target object as a function and call it. This is not typesafe
     * and should be avoided.
     * 
     * @param target
     *            the functional object
     * @param arguments
     *            the call arguments
     * @return the function result
     */
    native public static <T> T $apply(Object target, Object... arguments);

    /**
     * Uses the target object as a constructor and call it. This is not typesafe
     * and should be avoided.
     * 
     * @param target
     *            the constructor object
     * @param arguments
     *            the call arguments
     * @return the constructor result
     */
    native public static <T> T $new(Object target, Object... arguments);

    /**
     * This helper casts an object to one of the types in a given union type.
     * 
     * <p>
     * The JSweet transpiler will ensure that T is one of T1 and T2. If not, it
     * will raise an error.
     * 
     * @param union
     *            the object typed after a union type
     * @return the same object, but typed after one of the types of the union
     *         type
     */
    native public static <T1, T2, T> T union(Union<T1, T2> union);

    /**
     * This helper casts an object to an union type.
     * 
     * <p>
     * The JSweet transpiler will ensure that T is one of actual type elements
     * of U. If not, it will raise an error.
     * 
     * @param union
     *            the object typed after one of the types of the union type
     * @return the same object, but typed after a union type
     */
    native public static <U extends Union<?, ?>, T> U union(T object);

    /**
     * This helper casts an object to an union type.
     * 
     * <p>
     * The JSweet transpiler will ensure that T is one of actual type elements
     * of U. If not, it will raise an error.
     * 
     * @param union
     *            the object typed after one of the types of the union type
     * @return the same object, but typed after a union type
     */
    native public static <T> Union3<?, ?, ?> union3(T object);

    /**
     * This helper casts an object to an union type.
     * 
     * <p>
     * The JSweet transpiler will ensure that T is one of actual type elements
     * of U. If not, it will raise an error.
     * 
     * @param union
     *            the object typed after one of the types of the union type
     * @return the same object, but typed after a union type
     */
    native public static <T> Union4<?, ?, ?, ?> union4(T object);

    /**
     * This utility function allows using the <code>typeof</code> JavaScript
     * operator.
     * 
     * The expression <code>typeof(o)</code> transpiles to <code>typeof o</code>
     * . See the JavaScript documentation for more details.
     */
    native public static String typeof(Object o);

    /**
     * This syntax macro allows using the <code>==</code> or <code>!=</code>
     * JavaScript operators. Note that this macro will have no effect on other
     * operators than <code>==</code> or <code>!=</code>, but will recursively
     * apply to all operators contained in the macro.
     * 
     * <p>
     * Since JSweet version 1.1, the Java expression <code>o1!=o2</code>
     * transpiles to <code>o1!==o2</code> to remain close to the Java strict
     * inequality (except when diffing with the <code>null</code> literal where
     * the <code>==</code> operator is used).
     * 
     * <p>
     * Examples:
     * 
     * <pre>
     * if(a == b) { ... }
     * if(a != b) { ... }
     * if($loose(a == b)) { ... }
     * if($loose(a != b)) { ... }
     * if($strict(a == b)) { ... }
     * if(a == undefined) { ... }
     * if(a != null) { ... }
     * if($strict(a != null)) { ... }
     * if($loose(a == f(b != a))) { ... }
     * </pre>
     * 
     * Transpile to:
     * 
     * <pre>
     * if(a === b) { ... }
     * if(a !== b) { ... }
     * if(a == b) { ... }
     * if(a != b) { ... }
     * if(a === b) { ... }
     * if(a === undefined) { ... }
     * if(a != null) { ... }
     * if(a !== null) { ... }
     * if(a == f(b != a)) { ... }
     * </pre>
     * 
     * @param expression
     *            can be any expression returning a boolean, but only
     *            (in)equalities operators will be impacted
     * @return the expression modified to use loose JavaScript (in)equalities
     *         operators
     * @since 2.0
     */
    native public static boolean $loose(boolean expression);

    /**
     * This syntax macro is the reverse of {@link #$loose(boolean)}, and
     * enforces strict (in)equalities when it makes sense.
     * 
     * @param expression
     *            can be any expression returning a boolean, but only
     *            (in)equalities operators will be impacted
     * @return the expression modified to use strict JavaScript (in)equalities
     *         operators
     */
    native public static boolean $strict(boolean expression);

    /**
     * Disable type checking on the target object (cast to any). This helper is
     * valid in Java.
     */
    @SuppressWarnings("unchecked")
    public static <T> T any(Object object) {
	return (T) object;
    }

    /**
     * This helper function allows the programmer to reflectively set a global
     * variable named <code>"_exportedVar_"+name</code>.
     * 
     * <p>
     * This function must only be used in specifically cases, typically to
     * define results when testing from Java.
     * 
     * @param name
     *            the base name of the exported global variable, necessarily as
     *            a string literal
     * @param value
     *            the value to set to the variable
     */
    public static void $export(String name, Object value) {
	// default Java implementation when running in Java (usually for testing
	// purposes)
	EXPORTED_VARS.get().put("_exportedVar_" + name, value);
    }

    /**
     * Accesses the current JavaScript <code>this</code>. Within an object
     * scope, <code>$this</code> corresponds to a regular object oriented
     * <code>this</code>. Outside on an object (i.e. within a global function),
     * <code>$this</code> has the JavaScript meaning, representing the current
     * function object.
     */
    public static final def.js.Object $this = null;

    /**
     * Defines a template string.
     * 
     * <p>
     * For example:
     * 
     * <pre>
     * console.info($template("Get: ${key} => ${_val}"));
     * </pre>
     * 
     * <p>
     * gets transpiled to:
     * </p>
     * 
     * <pre>
     * console.info(`Get: ${key} => ${_val}`));
     * </pre>
     * 
     * @param templateString
     *            the regular Java string to be turned into a template string
     * @return a JavaScript template string
     */
    public static native String $template(String templateString);

    /**
     * Defines a template string with a tag.
     * 
     * @see #$template(String)
     */
    public static native String $template(Object tag, String templateString);

    /**
     * Inserts a TypeScript string as is in the generated program (not
     * recommended). This can be seen as a compile-time eval.
     * 
     * <p>
     * Although the TypeScript compiler will check the inserted code by
     * compiling it, it is obviously a dangerous macro that should be used only
     * in last resort.
     * 
     * @see def.js.Globals#eval(String)
     */
    public static native <T> T $insert(String typescriptString);

    public static native <R> R await(Promise<R> promise);
    
    public static native <R> R await(R promise);
    
    public static native <R> def.js.Function async(def.js.Function function);
    
    public static native <T> Promise<T> asyncReturn(T result);
}
