JSweet Language Specifications
==============================

Version: 1.1.0

Author: Renaud Pawlak

JSweet JavaDoc API: http://www.jsweet.org/core-api-javadoc/

Note: this markdown is automatically generated from the Latex source file. Do not modify directly.

Content
-------

-   [Basic concepts](#basic-concepts)
    -   [Core types and objects](#core-types-and-objects)
    -   [Classes](#classes)
    -   [Testing the type of an object](#testing-the-type-of-an-object)
    -   [Interfaces](#interfaces)
    -   [Untyped objects (maps)](#untyped-objects-maps)
    -   [Enums](#enums)
    -   [Globals](#globals)
    -   [Optional parameters](#optional-parameters)
-   [Bridging to external JavaScript elements](#bridging-to-external-javascript-elements)
    -   [Ambient declarations](#ambient-declarations)
    -   [Definitions](#definitions)
    -   [Mixins](#mixins)
-   [Auxiliary types](#auxiliary-types)
    -   [Functional types](#functional-types)
    -   [Object types](#object-types)
    -   [String types](#string-types)
    -   [Tuple types](#tuple-types)
    -   [Union types](#union-types)
    -   [Intersection types](#intersection-types)
-   [Semantics](#semantics)
    -   [Main methods](#main-methods)
    -   [Initializers](#initializers)
    -   [Name clashes](#name-clashes)
    -   [Variable scoping in lambda expressions](#variable-scoping-in-lambda-expressions)
    -   [Scope of *this*](#scope-of-this)
-   [Packaging](#packaging)
    -   [Packages and modules](#packages-and-modules)
    -   [Root packages](#root-packages)
    -   [External modules](#external-modules)
    -   [Packaging a JSweet jar (candy)](#packaging-a-jsweet-jar-candy)
-   [Appendix 1: JSweet transpiler options](#appendix-1-jsweet-transpiler-options)
-   [Appendix 2: JSweet strict mode](#appendix-2-jsweet-strict-mode)

Basic concepts
--------------

This section presents the JSweet language basic concepts. One must keep in mind that JSweet, as a Java-to-JavaScript transpiler, is an extension of Java at compile-time, and executes as JavaScript at runtime. Thus, most Java typing and syntactic constraints will apply at compile time, but some JavaScript semantics may apply at runtime. This document will mention when Java syntactic constructs and semantics are not legal (or differ) in JSweet.

### Core types and objects

JSweet allows the use of primitive Java types, core Java objects (with some restrictions) and of core JavaScript objects, which are defined in the `jsweet.lang` package. Next, we describe the use of such core types and objects.

#### Primitive Java types

JSweet allows the use of Java primitive types (and associated literals).

-   `int`, `byte`, `short`, `double`, `float` are all converted to JavaScript numbers (precision does not matter in JSweet).

-   `char` follows the Java typing rules but is converted to a JavaScript string by the transpiler.

-   `boolean` corresponds to the JavaScript boolean.

-   `java.lang.String` corresponds to the JavaScript `string`. (not per say a primitive type, but is immutable and used as the class of string literals in Java)

Examples of valid statements:

``` java
// warning '==' behaves like the JavaScript one at runtime
int i = 2;
assert i == 2;
double d = i + 4;
assert d == 6;
String s = "string" + '0' + i;
assert s == "string02";
boolean b = false;
assert !b;
```

Note that in JSweet the `==` operator behaves like in JavaScript on primitive types, so that when comparing a string and an integer, it will try to match the two without taking the types in consideration. For instance, `2 == 2` evaluates to `true`. In JavaScript, one can use the `===` operator to strictly compare the objects, so that `2 === 2` evaluates to `false`. To use the `===` operator in JSweet, use the `jsweet.util.Globals.equalsStrict` utility method as follows.

``` java
import static jsweet.util.Globals.equalsStrict;
[...]
int i = 2;
assert i == 2;
assert "2" == i;
assert !(equalsStrict("2", i);
```

#### Allowed Java objects

Here follows the list of allowed Java classes in JSweet:

-   `java.lang.Object`

    -   allowed methods: `toString()`

-   `java.lang.String`

    -   allowed methods:

        -   `charAt(int)`

        -   `concat(java.lang.String)`

        -   `indexOf(java.lang.String)`

        -   `lastIndexOf(java.lang.String)`

        -   `lastIndexOf(java.lang.String,int)`

        -   `length()` (transpiles to `length`)

        -   `substring(int)`

        -   `substring(int,int)` (with the JavaScript behavior)

        -   `replace(java.lang.CharSequence,java.lang.CharSequence)`

        -   `split(java.lang.String)`

        -   `trim()`

        -   `toLowerCase()`

        -   `toUpperCase()`

-   `java.lang.Class`

    -   allowed methods: none

-   `java.lang.Boolean`

    -   allowed methods: none

-   `java.lang.Void`

    -   allowed methods: none

-   `java.lang.Integer`

    -   allowed methods: none

-   `java.lang.Double`

    -   allowed methods: none

-   `java.lang.Number`

    -   allowed methods: none

-   `java.lang.Float`

    -   allowed methods: none

-   `java.lang.Byte`

    -   allowed methods: none

-   `java.lang.Short`

    -   allowed methods: none

-   `java.lang.Iterable`

    -   allowed methods: none (for using the *foreach* loop on indexed objects)

-   `java.lang.Runnable`

    -   allowed methods: none (for declaring lambdas)

-   `java.util.function.*` (for declaring lambdas)

    -   prohibited method names:

        -   `and`

        -   `negate`

        -   `or`

        -   `andThen`

Examples of valid statements:

``` java
// warning '==' behaves like the JavaScript one at runtime
Integer i = 2;
assert i == 2;
Double d = i + 4d;
assert d.toString() == "6";
assert (Object) d == "6";
BiFunction<String, Integer, String> f = (s, i) -> { return s.substring(i); };
assert "bc" == f.apply("abc", 1);
```

#### Java arrays

Arrays can be used in JSweet and are transpiled to JavaScript arrays. Array initialization, accesses and and iteration are all valid statements.

``` java
int[] arrayOfInts = { 1, 2, 3, 4};
assert arrayOfInts.length == 4;  
assert arrayOfInts[0] == 1;
int i = 0;
for (int intItem : arrayOfInts) { 
    assert arrayOfInts[i++] == intItem;
}
```

#### Core JavaScript API

The core JavaScript API is defined in `jsweet.lang` (the full documentation can be found at <http://www.jsweet.org/core-api-javadoc/>). Main JavaScript classes are:

-   `jsweet.lang.Object`: JavaScript Object class. Common ancestor for JavaScript objects functions and properties.

-   `jsweet.lang.Boolean`: JavaScript Boolean class. A wrapper for boolean values.

-   `jsweet.lang.Number`: JavaScript Number class. A wrapper for numerical values.

-   `jsweet.lang.String`: JavaScript String class. A wrapper and constructor for strings.

-   `jsweet.lang.Function`: JavaScript Function class. A constructor for functions.

-   `jsweet.lang.Date`: JavaScript Date class, which enables basic storage and retrieval of dates and times.

-   `jsweet.lang.Array<T>`: JavaScript Array class. It is used in the construction of arrays, which are high-level, list-like objects.

-   `jsweet.lang.Error`: JavaScript Error class. This class implements `java.lang.RuntimeException` and can be thrown and caught with `try` ... `catch` statements.

Programmers should use this API most of the time, which is HTML5 compatible and follows the JavaScript latest supported versions. However, for objects that need to be used with Java literals (numbers, booleans, and strings), the use of the `java.lang` package classes is recommended. For instance, the jQuery API declares `$(java.lang.String)` instead of `$(jsweet.lang.String)`. This allows the programmer to write expressions using literals, such as `$("a")` (for selecting all links in a document).

As a consequence, programmers need to be able to switch to the JavaScript API when coming from a Java object. The `jsweet.util.Globals` class defines convenient static methods to cast back and forth core Java objects to their corresponding JSweet objects. For instance the `string(...)` method will allow the programmer to switch from the Java to the JSweet strings and conversely.

``` java
import static jsweet.util.Globals.string;
String str = "This is a test string";
str.toLowerCase(); // valid: toLowerCase it defined both in Java and JavaScript
str.substr(1); // compile error: substr is not a Java method
string(str).substr(1); // valid: string(str) is a jsweet.lang.String.
```

Note: using the `jsweet.util.Globals` class to access the JavaScript API can be avoided in some cases when working in *strict* mode (see Appendix 2).

Here is another example that shows the use of the `array` method to access the `push` method available on JavaScript arrays.

``` java
import static jsweet.util.Globals.array;
String[] strings = { "a", "b", "c" };
array(strings).push("d");
assert strings[3] == "d";
```

### Classes

Classes in JSweet are very similar to Java classes. For example:

``` java
public class BankAccount {  
    public double balance = 0;  
    public double deposit(double credit) {  
        balance += credit;  
        return this.balance;  
    }  
}  
```

Which is transpiled to the following JavaScript code:

``` java
var BankAccount = (function () {  
    function BankAccount() {  
        this.balance = 0;  
    }  
    BankAccount.prototype.deposit = function(credit) {  
        this.balance += credit;  
        return this.balance;  
    };  
    return BankAccount;  
})();
```

Classes can define constructors, have super classes and be instantiated exactly like in Java. The only restriction compared to Java is that inner classes or anonymous classes are not allowed in JSweet (in the general case). For instance, the following code will raise an error.

``` java
public class ContainerClass {
    // error: inner classes are not allowed
    public class InnerClass {
    }
}
```

Exceptions: inner classes annotated with `@ObjectType` or `@Erased` are allowed (see the part on Auxiliary Types).

### Testing the type of an object

To test the type of a given object at runtime, one can use the `instanceof` Java operator. It is the advised and preferred way to test types at runtime. JSweet will transpile to a regular `instanceof` or to a `typeof` operator depending on the tested type (it will fallback in `typeof` for `number`, `string`, and `boolean` core types).

It is also possible to directly use the `typeof` operator from JSweet with the `jsweet.util.Globals.typeof` utility method. In general, it is not necessary to use it and the `instanceof` operator is preferred and more general.

Here are some examples of valid type tests:

``` java
import static jsweet.util.Globals.typeof;
import static jsweet.util.Globals.equalsStrict;
[...]
Number n1 = 2;
Object n2 = 2;
int n3 = 2;
Object s = "test";
MyClass c = new MyClass();

assert n1 instanceof Number; // transpiles to a typeof
assert n2 instanceof Number; // transpiles to a typeof
assert n2 instanceof Integer; // transpiles to a typeof
assert !(n2 instanceof String); // transpiles to a typeof
assert s instanceof String; // transpiles to a typeof
assert !(s instanceof Integer); // transpiles to a typeof
assert c instanceof MyClass;
assert typeof(n3) == "number";
assert equalsStrict(typeof(n3), "number");
```

Note: the `instanceof` operator is not allowed in interfaces, for reasons that will be explained in the following sections.

### Interfaces

In JSweet, an interface (a.k.a. object type) can be seen as object signature, that is to say the accessible functions and properties of an object (without specifying any implementation). An interface is defined for typing only and has no runtime representation (no instances), however, they can be used to type objects.

JSweet interfaces can be defined as regular Java interfaces, but also as Java classes annotated with `@jsweet.lang.Interface`, so that is is possible to define properties as fields. Such classes impose many constraints, as shown in the following code.

``` java
@Interface
public class WrongConstructsInInterfaces {
    native public void m1(); // OK
    // error: field initializers are not allowed
    public long l = 4;
    // error: statics are not allowed
    static String s1;
    // error: private are not allowed
    private String s2;
    // error: constructors are not allowed
    public WrongConstructsInInterfaces() {
        l = 4;
    }
    // error: bodies are not allowed
    public void m2() {
        l = 4;
    }
    // error: statics are not allowed
    native static void m3();
    // error: initializers are not allowed
    {
        l = 4;
    }
    // error: static initializers are not allowed
    static {
        s1 = "";
    }
}
```

#### Object typing

In JSweet, typed objects can be constructed out of interfaces. If we take the following interface:

``` java
@Interface
public class Point {
    public double x;
    public double y;
}
```

We can create an object typed after the interface. Note that the following code is not actually creating an instance of the `Point` interface, it is creating an object that conforms to the interface.

``` java
Point p1 = new Point() {{ x=1; y=1; }};
```

As a direct consequence, in JSweet it is not allowed to check an instance against an interface type.

``` java
if (p1 instanceof Point) { ... } // compile error
```

This may seems quite confusing for Java programmers, but you have to remember that, on contrary to Java where interfaces are available as special classes at runtime, in JSweet, interfaces have no reality at runtime. Think of generics, which are of the same kind in Java. As a consequence, the `instanceof` operator is applicable to classes, but not to interfaces at runtime (like it is not applicable on generics).

#### Optional fields

Interfaces can define *optional fields*, which are used to report errors when the programmer forgets to initialize a mandatory field in an object. Supporting optional fields in JSweet is done through the use of `@jsweet.lang.Optional` annotations. For instance:

``` java
@Interface
public class Point {
    public double x;
    public double y;
    @Optional
    public double z = 0;
}
```

It is the JSweet compiler that will check that the fields are correctly initialized, when constructing an object.

``` java
// no errors (z is optional)
Point p1 = new Point() {{ x=1; y=1; }};
// JSweet reports a compile error since y is not optional
Point p2 = new Point() {{ x=1; z=1; }};
```

#### Special functions in interfaces

In JavaScript, objects can have properties and functions, but can also (not exclusively), be used as constructors and functions themselves. This is not possible in Java, so JSweet defines special functions for handling these cases.

-   `apply` is used to state that the object can be used as a function.

-   `$new` is used to state that the object can be used as a constructor.

### Untyped objects (maps)

In JavaScript, object can be seen as maps containing key-value pairs (key is often called *index*, especially when it is a number). So, in JSweet, all objects define the special functions (defined on `jsweet.lang.Object`):

-   `$get(key)` accesses a value with the given key.

-   `$set(key,value)` sets or replace a value for the given key.

-   `$delete(key)` deletes the value for the given key.

#### Reflective/untyped accesses

The functions `$get(key)`, `$set(key,value)` and `$delete(key)` can be seen as a simple reflective API to access object fields and state. Note also the static method `jsweet.lang.Object.keys(object)`, which returns all the keys defined on a given object.

The following code uses this API to introspect the state of an object `o`.

``` java
for(String key : jsweet.lang.Object.keys(o)) {
  console.log("key=" + key +  " value=" + o.$get(key));
});
```

When not having the typed API of a given object, this API can be useful to manipulate the object in an untyped way (of course it should be avoided as much as possible).

#### Untyped objects initialization

One can use the `$set(key,value)` function to create new untyped object. For instance:

``` java
Object point = new jsweet.lang.Object() {{ $set("x", 1); $set("y", 1); }};
```

As a shortcut, one can use the `jsweet.util.Global.$map` function:

``` java
import static jsweet.util.Global.$map;
[...]
Object point = $map("x", 1, "y", 1);
```

#### Indexed objects

The type of keys and values can be overloaded for every object. For example, the `Array<T>` class, will define keys as numbers and values as objects conforming to type `T`.

In the case of objects indexed with number keys, it is allowed to implement the `java.lang.Iterable` interface so that it is possible to use they in *foreach* loops. For instance, the `NodeList` type (from the DOM) defines an indexed function:

``` java
@Interface
class NodeList implements java.lang.Iterable {
    public double length;
    public Node item(double index);
    public Node $get(double index);
}
```

In JSweet, you can access the node list elements with the `$get` function, and you can also iterate with the *foreach* syntax. The following code generates fully valid JavaScript code.

``` java
NodeList nodes = ...
for (int i = 0; i < nodes.length; i++) {
    HTMLElement element = (HTMLElement) nodes.$get(i);
    [...]
}
// same as:
NodeList nodes = ...
for (Node node : nodes) {
    HTMLElement element = (HTMLElement) node;
    [...]
}
```

### Enums

JSweet allows the definition of enums similarly to Java, but with some restrictions. The following code declares an enum with tree possible values (`A`, `B`, and `C`).

``` java
enum MyEnum {
    A, B, C
}
```

The following statements are valid statements in JSweet.

``` java
MyEnum e = MyEnum.A;
assert MyEnum.A == e;
assert e.name() == "A";
assert e.ordinal() == 0;
assert MyEnum.valueOf("A") == e;
assert array(MyEnum.values()).indexOf(MyEnum.valueOf("C")) == 2;
```

On the other hand, unlike Java enums, other members than constants are not allowed, raising JSweet transpilation errors, as shown in the following code.

``` java
public enum WrongConstructsInEnums {

    A, B, C;
    
    // error: fields are not allowed
    public long l = 4;
    // error: constructors are not allowed  
    private WrongConstructsInEnums() {
        l = 4;
    }
    // error: methods are not allowed
    public void m2() {
        l = 4;
    }
    // error: initializers are not allowed
    {
        l = 4;
    }
}
```

### Globals

In Java, on contrary to JavaScript, there is no such thing as global variables or functions (there are only static members, but even those must belong to a class). Thus, JSweet introduces reserved `Globals` classes and `globals` packages. These have two purposes:

-   Generate code that has global variables and functions (this is discouraged in Java)

-   Bind to existing JavaScript code that defines global variables and functions (as many JavaScript frameworks do)

In Globals classes, only static fields (global variables) and static methods (global functions) are allowed. Here are the main constraints applying to Globals classes:

-   no non-static members

-   no super class

-   cannot be extended

-   cannot be used as types like regular classes

-   no public constructor (empty private constructor is OK)

-   cannot use $get, $set and $delete within the methods

For instance, the following code snippets will raise transpilation errors.

``` java
class Globals {
    public int a;
    // error: public constructors are not allowed
    public Globals() {
        this.a = 3;
    }
    public static void test() {
        // error: no instance is available
        $delete("key");
    }
}
```

``` java
// error: Globals classes cannot be used as types
Globals myVariable = null;
```

One must remember that `Globals` classes and `global` packages are erased at runtime so that their members will be directly accessible. For instance `mypackage.Globals.m()` in a JSweet program corresponds to the `mypackage.m()` function in the generated code and in the JavaScript VM at runtime. Also, `mypackage.globals.Globals.m()` corresponds to *m()*.

In order to erase packages in the generated code, programmers can also use the `@Root` annotation, which will be explained in Section \[packaging\].

### Optional parameters

In JavaScript, parameters can be optional, in the sense that a parameter value does not need to be provided when calling a function. Except for varargs, which are fully supported in JSweet, the general concept of an optional parameter does not exist in Java. To simulate optional parameters, JSweet supports overloading as long as it is for implementing optional parameters – a very commonly used idiom. Here are some examples of valid and invalid overloading in JSweet:

``` java
String m(String s, double n) { return s + n; }
// valid overloading (JSweet transpiles to optional parameter)
String m(String s) { return m(s, 0); }
// invalid overloading (JSweet error)
String m(String s) { return s; }
```

Bridging to external JavaScript elements
----------------------------------------

It can be the case that programmers need to use existing libraries from JSweet. In most cases, one should look up in the available candies (<http://www.jsweet.org/candies-releases/> and <http://www.jsweet.org/candies-snapshots/>), which are automatically generated from TypeScript’s DefinitelyTyped. When the candy does not exist, or does not entirely cover what is needed, one can use the `@jsweet.lang.Ambient` annotation, which will make available to the programmers a class definition or an interface.

### Ambient declarations

The following example shows the backbone store class made accessible to the JSweet programmer with a simple ambient declaration. This class is only for typing and will be erased during the JavaScript generation.

``` java
@Ambient
class Store {
    public Store(String dbName) {}
}
```

Note that ambient classes constructors must have an empty body. Also, ambient classes methods must be `abstract` or `native`. For instance:

``` java
@Ambient
class MyExternalJavaScriptClass {
    public native myExternalJavaScriptMethod();
}
```

### Definitions

By convention, putting the classes in a `def.libname` package defines a set of definitions for the `libname` external JavaScript library called `libname`. Definitions are by default all ambient declarations and do not need to be annotated with `@jsweet.lang.Ambient` annotations since they are implicit in `def.*` packages and sub-packages. Note that this mechanism is similar to the TypeScript `d.ts` definition files.

Candies (bridges to external JavaScript libraries) use definitions. For instance, the jQuery candy defines all the jQuery API in the `def.jquery` package.

### Mixins

In JavaScript, it is common practice to enhance an existing class with news elements (field and methods). It is an extension mechanism used when a framework defines plugins for instance. Typically, jQuery plugins add new elements to the `JQuery` class. For example the jQuery timer plugin adds a `timer` field to the `JQuery` class. As a consequence, the `JQuery` class does not have the same prototype if you are using jQuery alone, or jQuery enhanced with its timer plugin.

In Java, this extension mechanism is problematic because Java cannot dynamically enhance a given class.

#### Untyped accesses

Programmers can access the added element with `$get` accessors and/or with brute-force casting.

Here is an example using `$get` for the timer plugin case:

``` java
((Timer)$("#myId").$get("timer")).pause();
```

Here is an other way to do it exampled through the use of the jQuery UI plugin (note that this solution forces the use of `def.jqueryui.JQuery` instead of `def.jquery.JQuery` in order to access the `menu()` function, added by the UI plugin):

``` java
import def.jqueryui.JQuery;
[...]
Object obj = $("#myMenu");
JQuery jq = (JQuery) obj;
jq.menu();
```

However, these solutions are not satisfying because clearly unsafe in terms of typing.

#### Typed accesses with mixins

When cross-candy dynamic extension is needed, JSweet defines the notion of a mixin. A mixin is a class that defines members that will end up being directly accessible within a target class (mixin-ed class). Mixins are defined with a `@Mixin` annotation. Here is the excerpt of the `def.jqueryui.JQuery` mixin:

``` java
package def.jqueryui;
import jsweet.dom.MouseEvent;
import jsweet.lang.Function;
import jsweet.lang.Date;
import jsweet.lang.Array;
import jsweet.lang.RegExp;
import jsweet.dom.Element;
import def.jquery.JQueryEventObject;
@jsweet.lang.Interface
@jsweet.lang.Mixin(target=def.jquery.JQuery.class)
public abstract class JQuery extends jsweet.lang.Object {
    native public JQuery accordion();
    native public void accordion(jsweet.util.StringTypes.destroy methodName);
    native public void accordion(jsweet.util.StringTypes.disable methodName);
    native public void accordion(jsweet.util.StringTypes.enable methodName);
    native public void accordion(jsweet.util.StringTypes.refresh methodName);
    ...
    native public def.jqueryui.JQuery menu();
    ...
```

One can notice the `@jsweet.lang.Mixin(target=def.jquery.JQuery.class)` that states that this mixin will be merged to the `def.jquery.JQuery` so that users will be able to use all the UI plugin members directly and in a well-typed way.

#### Implementation and how to use

JSweet merges mixins using a bytecode manipulation tool called Javassist. It takes the mixin classes bytecode, copies all the members to the target classes, and writes the resulting merged classes bytecode to the `.jsweet/candies/processed` directory. As a consequence, in order to benefit the JSweet mixin mechanism, one must add the `.jsweet/candies/processed` directory to the compilation classpath. This directory should be placed before all the other classpath elements so that the mixined results override the original classes (for example the `def.jquery.JQuery` should be overridden and, as a consequence, `.jsweet/candies/processed/def/jquery/JQuery.class` must be found first in the classpath).

The JSweet transpiler automatically adds the `.jsweet/candies/processed` directory to the compilation classpath so that you do not have to do anything special when using JSweet with Maven. However, when using mixins within an IDE, you must force your project classpath to include this directory in order to ensure compilation of mixin-ed elements. When using the JSweet Eclipse plugin for instance, this is done automatically and transparently for the user. But when not using any plugins, this configuration must be done manually.

For example, with Eclipse (similar configuration can be made with other IDEs):

1.  Right-click on the project &gt;Build path &gt;Configure build path... &gt;Libraries (tab) &gt;Add class folder (button). Then choose the `.jsweet/candies/processed` directory.

2.  In the “order and export” tab of the build path dialog, make sure that the `.jsweet/candies/processed` directory appears at the top of the list (or at least before the Maven dependencies).

NOTE: you do not have to configure anything if you are not using mixins or if you are using the Eclipse plugin.

Once this configuration is done, you can safely use mixins. For instance, if using the jQuery candy along with jQuery UI, you will be able to write statements such as:

``` java
$("#myMenu").menu();
```

This is neat compared to the untyped access solution because it is checked by the Java compiler (and you will also have completion on mixin-ed elements).

Auxiliary types
---------------

JSweet uses most Java typing features (including functional types) but also extends the Java type system with so-called *auxiliary types*. The idea behind auxiliary types is to create classes or interfaces that can hold the typing information through the use of type parameters (a.k.a *generics*), so that the JSweet transpiler can cover more typing scenarios. These types have been mapped from TypeScript type system, which is much richer than the Java one (mostly because JavaScript is a dynamic language and requires more typing scenarios than Java).

### Functional types

For functional types, JSweet reuses the `java.Runnable` and `java.util.function` functional interfaces of Java 8. These interfaces are generic but only support up to 2-parameter functions. Thus, JSweet adds some support for more parameters in `jsweet.util.function`, since it is a common case in JavaScript APIs.

Here is an example using the `Function` generic functional type:

``` java
import java.util.function.Function;

public class C {

    String test(Function<String, String> f) {
        f.apply("a");
    }

    public static void main(String[] args) {
        String s = new C().test(p -> p);
        assert s == "a";
    }
}
```

We encourage programmers to use the generic functional interfaces defined in the `jsweet.util.function` and `java.util.function` (besides `java.lang.Runnable`). When requiring functions with more parameters, programmers can define their own generic functional types in `jsweet.util.function` by following the same template as the existing ones.

In some cases, programmers will prefer defining their own specific functional interfaces. This is supported by JSweet. For example:

``` java
@FunctionalInterface
interface MyFunction {
    void run(int i, String s);
}

public class C {
    void m(MyFunction f) {
        f.run(1, "test");
    }
    public static void main(String[] args) {
        new C().m((i, s) -> {
            // do something with i and s
        });
    }
}
```

Important warning: it is to be noted here that, on contrary to Java, the use of the `@FunctionInterface` annotation is mandatory.

Note also the possible use of the `apply` function, which is by convention always a functional definition on the target object (unless if `apply` is annotated with the `@Name` annotation). Defining/invoking `apply` can done on any class/object (because in JavaScript any object can become a functional object).

### Object types

Object types are similar to interfaces: they define a set of fields and methods that are applicable to an object (but remember that it is a compile-time contract). In TypeScript, object types are inlined and anonymous. For instance, in TypeScript, the following method `m` takes a parameter, which is an object containing an `index` field:

``` java
// TypeScript:
public class C {
    public m(param : { index : number }) { ... }
}
```

Object types are a convenient way to write shorter code. One can pass an object that is correctly typed by constructing an object on the fly:

``` java
// TypeScript:
var c : C = ...;
c.m({ index : 2 });
```

Obviously, object types are a way to make the typing of JavaScript programs very easy to programmers, which is one of the main goals of TypeScript. It makes the typing concise, intuitive and straightforward to JavaScript programmers. In Java/JSweet, no similar inlined types exist and Java programmers are used to defining classes or interfaces for such cases. So, in JSweet, programmers have to define auxiliary classes annotated with `@ObjectType` for object types. This may seem more complicated, but it has the advantage to force the programmers to name all the types, which, in the end, can lead to more readable and maintenable code depending on the context. Note that similarily to interfaces, object types are erased at runtime. Also `@ObjectType` annotated classes can be inner classes so that they are used locally.

Here is the JSweet version of the previous TypeScript program.

``` java
public class C {
    @ObjectType
    public static class Indexed {
        int index;
    }
    public void m(Indexed param) { ... }
}
```

Using an object type is similar to using an interface:

``` java
C c = ...;
c.m(new Indexed() {{ index = 2; }});
```

When object types are shared objects and represent a typing entity that can be used in several contexts, it is recommended to use the `@Interface` annotation instead of `@ObjectType`. Here is the interface-based version.

``` java
@Interface
public class Indexed {
    int index;
}

public class C {
    public m(Indexed param) { ... }
}

C c = ...;
c.m(new Indexed {{ index = 2; }});
```

### String types

In TypeScript, string types are a way to simulate function overloading depending on the value of a string parameter. For instance, here is a simplified excerpt of the DOM TypeScript definition file:

``` java
// TypeScript:
interface Document {
    [...]
    getElementsByTagName(tagname: "a"): NodeListOf<HTMLAnchorElement>;
    getElementsByTagName(tagname: "b"): NodeListOf<HTMLPhraseElement>;
    getElementsByTagName(tagname: "body"): NodeListOf<HTMLBodyElement>;
    getElementsByTagName(tagname: "button"): NodeListOf<HTMLButtonElement>;
    [...]
}
```

In this code, the `getElementsByTagName` functions are all overloads that depend on the strings passed to the `tagname` parameter. Not only string types allow function overloading (which is in general not allowed in TypeScript/JavaScript), but they also constrain the string values (similarly to an enumeration), so that the compiler can automatically detect typos in string values and raise errors.

This feature being useful for code quality, JSweet provides a mechanism to simulate string types with the same level of type safety. A string type is a public static field annotated with `@StringType`. It must be typed with an interface of the same name declared in the same container type.

For JSweet translated libraries (candies), all string types are declared in a the `jsweet.util.StringTypes` class, so that it is easy for the programmers to find them. For instance, if a `"body"` string type needs to be defined, a Java interface called `body` and a static final field called `body` are defined in a `jsweet.util.StringTypes`.

Note that each candy may have its own string types defined in the `jsweet.util.StringTypes` class. The JSweet transpiler merges all these classes at the bytecode level so that all the string types of all candies are available in the same `jsweet.util.StringTypes` utility class. As a result, the JSweet DOM API will look like:

``` java
@Interface
public class Document {
    [...]
    public native NodeListOf<HTMLAnchorElement> getElementsByTagName(a tagname);
    public native NodeListOf<HTMLPhraseElement> getElementsByTagName(b tagname);
    public native NodeListOf<HTMLBodyElement> getElementsByTagName(body tagname);
    public native NodeListOf<HTMLButtonElement> getElementsByTagName(button tagname);
    [...]
}
```

In this API, `a`, `b`, `body` and `button` are interfaces defined in the `jsweet.util.StringTypes` class. When using one the method of `Document`, the programmer just need to use the corresponding type instance (of the same name). For instance:

``` java
Document doc = ...;
NodeListOf<HTMLAnchorElement> elts = doc.getElementsByTagName(StringTypes.a);
```

Note: if the string value is not a valid Java identifier (for instance `"2d"` or `"string-with-dashes"`), it is then translated to a valid one and annotated with `@Name("originalName")`, so that the JSweet transpiler knows what actual string value must be used in the generated code. For instance, by default, `"2d"` and `"string-with-dashes"` will correspond to the interfaces `StringTypes._2d` and `StringTypes.string_with_dashes` with `@Name` annotations.

Programmers can define string types for their own needs, as shown below:

``` java
import jsweet.lang.Erased;
import jsweet.lang.StringType;

public class CustomStringTypes {
    @Erased
    public interface abc {}
    
    @StringType
    public static final abc abc = null;

    // This method takes a string type parameter
    void m2(abc arg) {
    }
    
    public static void main(String[] args) {
        new CustomStringTypes().m2(abc);
    }
}
```

Note the use of the `@Erased` annotation, which allows the declaration of the `abc` inner interface. This interface is used to type the string type field `abc`. In general, we advise the programmer to group all the string types of a program in the same utility class so that it is easy to find them.

### Tuple types

Tuple types represent JavaScript arrays with individually tracked element types. For tuple types, JSweet defines parameterized auxiliary classes `TupleN<T0, ... TN-1>`, which define `$0`, `$1`, ... `$N-1` public fields to simulate typed array accessed (field `$i` is typed with `Ti`).

For instance, given the following tuple of size 2:

``` java
Tuple2<String, Integer> tuple = new Tuple2<String, Integer>("test", 10);
```

We can expect the following (well-typed) behavior:

``` java
assert tuple.$0 == "test";
assert tuple.$1 == 10;
tuple.$0 = "ok";
tuple.$1--;
assert tuple.$0 == "ok";
assert tuple.$1 == 9;
```

Tuple types are all defined (and must be defined) in the `jsweet.util.tuple` package. By default classes `Tuple[2..6]` are defined. Other tuples (\(>6\)) are automatically generated when encountered in the candy APIs. Of course, when requiring larger tuples that cannot be found in the `jsweet.util.tuple` package, programmers can add their own tuples in that package depending on their needs, just by following the same template as existing tuples.

### Union types

Union types represent values that may have one of several distinct representations. When such a case happens within a method signature (for instance a method allowing several types for a given parameter), JSweet takes advantage of the *method overloading* mechanism available in Java. For instance, the following `m` method accept a parameter `p`, which can be either a `String` or a `Integer`.

``` java
public void m(String p) {...}
public void m(Integer p) {...}
```

In the previous case, the use of explicit union types is not required. For more general cases, JSweet defines an auxiliary interface `Union<T1, T2>` (and `UnionN<T1, ... TN>`) in the `jsweet.util.union` package. By using this auxiliary type and a `union` utility method, programmers can cast back and forth between union types and union-ed type, so that JSweet can ensure similar properties as TypeScript union types.

The following code shows a typical use of union types in JSweet. It simply declares a variable as a union between a string and a number, which means that the variable can actually be of one of that types (but of no other types). The switch from a union type to a regular type is done through the `jsweet.util.Globals.union` helper method. This helper method is completely untyped, allowing from a Java perspective any union to be transformed to another type. It is actually the JSweet transpiler that checks that the union type is consistently used.

``` java
import static jsweet.util.Globals.union;
import jsweet.util.union.Union;
[...]
Union<String, Number> u = ...;
// u can be used as a String
String s = union(u);
// or a number
Number n = union(u);
// but nothing else
Date d = union(u); // JSweet error
```

The `union` helper can also be used the other way, to switch from a regular type back to a union type, when expected.

``` java
import static jsweet.util.Globals.union;
import jsweet.util.union.Union3;
[...]
public void m(Union3<String, Number, Date>> u) { ... }
[...]
// u can be a String, a Number or a Date
m(union("a string"));
// but nothing else
m(union(new RegExp(".*"))); // compile error
```

Note: the use of Java function overloading is preferred over union types when typing function parameters. For example:

``` java
// with union types (discouraged)
native public void m(Union3<String, Number, Date>> u);
// with overloading (preferred way)
native public void m(String s);
native public void m(Number n);
native public void m(Date d);
```

### Intersection types

TypeScript defines the notion of type intersection. When types are intersected, it means that the resulting type is a larger type, which is the sum of all the intersected types. For instance, in TypeScript, `A & B` corresponds to a type that defines both `A` and `B` members.

Intersection types in Java cannot be implemented easily for many reasons. So, the practical choice being made here is to use union types in place of intersection types. In JSweet, `A & B` is thus defined as `Union<A, B>`, which means that the programmer can access both `A` and `B` members by using the `jsweet.util.Globals.union` helper method. It is of course less convenient than the TypeScript version, but it is still type safe.

Semantics
---------

Semantics designate how a given program behaves when executed. Although JSweet relies on the Java syntax, programs are transpiled to JavaScript and do not run in a JRE. As a consequence, the JavaScript semantics will impact the final semantics of a JSweet program compared to a Java program. In this section, we discuss the semantics by focusing on differences or commonalities between Java/JavaSript and JSweet.

### Main methods

Main methods are the program execution entry points and will be invoked globally when a class containing a `main` method is evaluated. For instance:

``` java
public class C {
    private int n;
    public static C instance;
    public static void main(String[] args) {
        instance = new C();
        instance.n = 4;
    }
    public int getN() {
        return n;
    }
}
// when the source file containing C has been evaluated:
assert C.instance != null;
assert C.instance.getN() == 4;
```

Main methods do not behave exactly like in Java and depend on the way the program is packaged and deployed.

-   **Regular packaging (no modules)**. With regular packaging, one Java source file corresponds to one generated JavaScript file. In that case, when loading a file in the browser, all the main methods will be invoked, right after the file is evaluated.

-   **Module packaging**. With module packaging (module option), one Java package corresponds to one module. With modules, it is mandatory to have only one main method in the program, which will be the global entry point from which the module dependency graph will be calculated. The main module will use directly or transitively all the other modules.

Because of modules, it is good practice to have only one main method in an application.

### Initializers

Initializers behave like in Java.

For example:

``` java
public class C1 {
    int n;
    {
        n = 4;
    }
}
assert new C1().n == 4;
```

And similarly with static initializers:

``` java
public class C2 {
    static int n;
    static {
        n = 4;
    }
}
assert C2.n == 4;
```

### Name clashes

On contrary to Java, methods and fields of the same name are not allowed within the same class or within classes having a subclassing link. The reason behind this behavior is that in JavaScript, object variables and functions are stored within the same object map, which basically means that you cannot have the same key for several object members (this also explains that method overloading in the Java sense is not possible in JavaScript).

In order to avoid programming mistakes due to this JavaScript behavior, which is not necessarily known by Java programmers, JSweet adds a semantics check to avoid duplicate names in classes (this also takes into account members defined in parent classes). As an example:

``` java
public class NameClashes {

    // error: field name clashes with existing method name
    public String a;

    // error: method name clashes with existing field name
    public void a() {
        return a;
    }

}
```

In general, it also not possible to have two methods with the same name but with different parameters (so-called overloads). An exception to this behavior is the use of method overloading for defining optional parameters. JSweet allows this idiom under the condition that it corresponds to the following template:

``` java
String m(String s, double n) { return s + n; }
// valid overloading (JSweet transpiles to optional parameter)
String m(String s) { return m(s, 0); }
```

In that case, JSweet will generate JavaScript code with only one method having default values for the optional parameters, so that the behavior of the generated program corresponds to the original one. In this case:

``` java
function m(s, n = 0) { return s + n; }
```

If the programmer tries to use overloading differently, for example by defining two different implementations for the same method name, JSweet will raise a compile error.

``` java
String m(String s, double n) { return s + n; }
// invalid overloading (JSweet error)
String m(String s) { return s; }
```

Local variables can also clash with the use of a global method. For instance, using the `alert` global method from the DOM (`jsweet.dom.Globals.alert`) requires that no local variable hides it. For instance:

``` java
import static jsweet.dom.Globals.alert;

[...]

public void m1(boolean alert) {
    // JSweet compile error: name clash between parameter and method call
    alert("test");
}

public void m2() {
    // JSweet compile error: name clash between local variable and method call
    String alert = "test";
    alert(alert);
}
```

Note that this problem also happens when using fully qualified names when calling the global methods (that because the qualification gets erased in TypeScript/JavaScript). In any case, JSweet will report sound errors when such problems happen so that programmers can adjust local variable names to avoid clashes with globals.

### Variable scoping in lambda expressions

JavaScript variable scoping is known to pose some problems to the programmers, because it is possible to change the reference to a variable from outside of a lambda that would use this variable. As a consequence, a JavaScript programmer cannot rely on a variable declared outside of a lambda scope, because when the lambda is executed, the variable may have been modified somewhere else in the program. For instance, the following program shows a typical case:

``` java
NodeList nodes = document.querySelectorAll(".control");
for (int i = 0; i < nodes.length; i++) {
    HTMLElement element = (HTMLElement) nodes.$get(i); // final
    element.addEventListener("keyup", (evt) -> {
        // this element variable will not change here 
        element.classList.add("hit");
    });
}
```

In JavaScript (note that EcmaScript 6 fixes this issue), such a program would fail its purpose because the `element` variable used in the event listener is modified by the for loop and does not hold the expected value. In JSweet, such problems are dealt with similarly to final Java variables. In our example, the `element` variable is re-scoped in the lambda expression so that the enclosing loop does not change its value and so that the program behaves like in Java (as expected by most programmers).

### Scope of *this*

On contrary to JavaScript and similarly to Java, using a method as a lambda will prevent loosing the reference to `this`. For instance, in the `action` method of the following program, `this` holds the right value, even when `action` was called as a lambda in the `main` method. Although this seem logical to Java programmers, it is not a given that the JavaScript semantics ensures this behavior.

``` java
package example;
import static jsweet.dom.Globals.console;

public class Example {
    private int i = 8;
    public Runnable getAction() {
        return this::action;
    }
    public void action() {
        console.log(this.i); // this.i is 8
    }
    public static void main(String[] args) {
        Example instance = new Example();
        instance.getAction().run();
    }
}
```

Packaging
---------

Packaging is one of the complex point of JavaScript, especially when coming from Java. Complexity with JavaScript packaging boils down to the fact that JavaScript did not define any packaging natively. As a consequence, many *de facto* solutions and quidelines came up along the years, making the understanding of packaging uneasy for regular Java programmers. In spite EcmaScript UMS is supposed to bring a standard for modules in Java, other *de facto* standards remain and the notion of a namespace/package is still not handled by JavaScript, making the notion of a module and a namespace confusing for JavaScript and TypeScript developers themselves.

### Packages and modules

Packages and modules are two similar concepts but for different contexts.

Java packages must be understood as compile-time *namespaces*. They allow a compile-time structuration of the programs through name paths, with implicit or explicit visibility rules. Packages have usually not much impact on how the program is actually bundled and deployed.

Modules must be understood as deployment and runtime *bundles*. The closest concept to a module in the Java world would probably be an OSGi bundle. A module defines imported and exported elements so that they create a strong runtime structure that can be used for deploying software components independently and thus avoiding name clashes. For instance, with modules, two different libraries may define a `util.List` class and be actually running and used on the same VM with no naming issues (as long as the libraries are bundled in different modules).

JSweet uses the Java package concept for namespaces. Modules are a deployment concept and should be created with some deployment scripts involving third party tools. However, for easy deployment, JSweet defines a `module` option that automatically creates a default module organization following the simple rule: one package = one module. With this rule, it is actually possible to run a JSweet program on Node.js without requiring extra work from the programmer. Also, using both the `bundle` and `module` options of JSweet creates a bundle file containing the application modules, and which can be deployed on a browser with no extra packaging.

### Root packages

Root packages are a way to tune the generated code so that JSweet packages are erased in the generated code and thus at runtime. To set a root package, just define a package-info.java file and use the *@Root* annotation on the package, as follows:

``` java
@Root
package a.b.c;
```

The above declaration means that the `c` package is a root package, i.e. it will be erased in the generated code, as well as all its parent packages. Thus, if `c` contains a package `d`, and a class `C`, these will be top-level objects at runtime. In other words, `a.b.c.d` becomes `d`, and `a.b.c.C` becomes `C`.

Note that since that packaged placed before the *@Root* package are erased, there cannot be any type defined before a *@Root* package. In the previous example, the *a* and *b* packages are necessarily empty packages.

#### Behavior when not using modules (default)

By default, root packages do not change the folder hierarchy of the generated files. For instance, the `a.b.c.C` class will still be generated in the `<jsout>/a/b/c/C.js` file (relatively to the `<jsout>` output directory). However, switching on the `noRootDirectories` option will remove the root directories so that the `a.b.c.C` class gets generated to the `<jsout>/C.js` file.

When not using modules (default), it is possible to have several *@Root* packages (but a *@Root* package can never contain another *@Root* package).

#### Behavior when using modules

When using modules (see the *module* option), only one *@Root* package is allowed, and when having one *@Root* package, no other package or type can be outside of the scope of that *@Root* package. The generated folder/file hierarchy then starts at the root package so that all the folders before it are actually erased.

### External modules

When compiling JSweet programs with the `module` options, all external libraries and components must be required as external modules. JSweet can automatically require modules, simply by using the `@Module(name)` annotation. In JSweet, importing or using a class or a member annotated with `@Module(name)` will automatically require the corresponding module at runtime. Please not that it is true only when the code is generated with the `module` option. If the `module` option is off, the `@Module` annotations are ignored.

``` java
package def.jquery;
public final class Globals extends jsweet.lang.Object {
    ...
    @jsweet.lang.Module("jquery")
    native public static def.jquery.JQuery $(java.lang.String selector);
    ...
}
```

The above code shows an excerpt of the JSweet jQuery API. As we can notice, the $ function is annotated with `@Module("jquery")`. As a consequence, any call to this function will trigger the require of the `jquery` module.

Note: the notion of manual require of a module may be available in future releases. However, automatic require is sufficient for most programmers and hides the complexity of having to require modules explicitly. It also brings the advantage of having the same code whether modules are used or not.

### Packaging a JSweet jar (candy)

When creating a program (especially a library or a component) with JSweet, one may want to package it in a JSweet jar (a.k.a. a candy) and deploy it on a Maven repository, so that it can be used by other JSweet programs.

Candies are regular Jars that can be generated with the Maven `package` goal. To be a valid candy and recognized as such by the JSweet transpiler, your jar file must contain two additional resources (to be placed in the resources directory of your Maven project so that they end up being packaged in your jar by Maven).

1.  A `META-INF/candy-metadata.json` file that contains the expected target version of the transpiler (to be adapted to your target transpiler version).

2.  The program’s declarations in `d.ts` files, to be placed in the `src/typings` directory of the jar.

Here is an example of the `META-INF/candy-metadata.json` file:

``` java
{
   "transpilerVersion": "1.0.0"
}
```

Typically, `d.ts` files shall be generated by the JSweet transpiler using the following options:

-   `declaration`: turns on the generation of the `d.ts` files.

-   `dtsout`: tells where to put the generated `d.ts` files (for packaging, place them in `RESOURCES/src/typings`, where `RESOURCES` is your Maven project resources directory).

Note: this section only described how to package JSweet jars for compilation. For execution, the generated JavaScript must be accessible to the using project too. A way is to package the JavaScript following the WebJar conventions: <http://www.webjars.org/>.

Appendix 1: JSweet transpiler options
-------------------------------------

      [-h|--help]

      [-v|--verbose]
            Turn on all levels of logging.

      [--encoding <encoding>]
            Force the Java compiler to use a specific encoding (UTF-8, UTF-16, ...).
            (default: UTF-8)

      [--jdkHome <jdkHome>]
            Set the JDK home directory to be used to find the Java compiler. If not
            set, the transpiler will try to use the JAVA_HOME environment variable.
            Note that the expected JDK version is greater or equals to version 8.

      (-i|--input) <input>
            An input dir containing Java files to be transpiled.

      [--noRootDirectories]
            Skip the root directories (i.e. packages annotated with
            @jsweet.lang.Root) so that the generated file hierarchy starts at the
            root directories rather than including the entire directory structure.

      [--tsout <tsout>]
            Specify where to place generated TypeScript files. (default: .ts)

      [(-o|--jsout) <jsout>]
            Specify where to place generated JavaScript files (ignored if jsFile is
            specified). (default: js)

      [--declaration]
            Tells the transpiler to generate the d.ts files along with the js files,
            so that other programs can use them to compile.

      [--dtsout <dtsout>]
            Specify where to place generated d.ts files when the declaration option
            is set (by default, d.ts files are generated in the JavaScript output
            directory - next to the corresponding js files).

      [--classpath <classpath>]
            The JSweet transpilation classpath (candy jars). This classpath should
            at least contain the core candy.

      [(-m|--module) <module>]
            The module kind (none, commonjs, amd, system or umd). (default: none)

      [-b|--bundle]
            Bundle up the generated files and used modules to bundle files, which
            can be used in the browser. Bundles contain all the dependencies and are
            thus standalone. There is one bundle generated per entry (a Java 'main'
            method) in the program. By default, bundles are generated in the entry
            directory, but the output directory can be set by using the
            --bundlesDirectory option. NOTE: bundles will be generated only when
            choosing the commonjs module kind.

      [--bundlesDirectory <bundlesDirectory>]
            Generate all the bundles (see option --bundle) within the given
            directory.

      [--sourceMap]
            Set the transpiler to generate source map files for the Java files, so
            that it is possible to debug them in the browser. This feature is not
            available yet when using the --module option. Currently, when this
            option is on, the generated TypeScript file is not pretty printed in a
            programmer-friendly way (disable it in order to generate readable
            TypeScript code).

      [--ignoreAssertions]
            Set the transpiler to ignore 'assert' statements, i.e. no code is
            generated for assertions.

Appendix 2: JSweet strict mode
------------------------------

For programmers developing JavaScript-only application (no Java at all), JSweet proposes an optional *strict* mode. In strict mode, Java APIs are not accessible at all anymore and are substituted by the JavaScript APIs. This is possible through a technique invented by Peter Kriens consisting in replacing the core Java classes at compile-time. To enable strict mode, just add to your dependencies (in first position) the `jsweet-core-strict` artifact (group `org.jsweet`).

The advantage of using the strict mode is that Java core objects become JavaScript object directly and expose the JavaScript API, so that programmers do not need to switch from Java to JSweet API anymore (through the `jsweet.util.Globals` utilities). This means for instance that the `$get`, `$set` and `$delete` functions are directly accessible on all objects.

Here is an example with a native Java string:

``` java
String str = "This is a test string";
// compile error in non strict mode: substr is not a Java method
// but OK with strict mode! 
str.substr(1); 
```

The main benefit in using strict mode is when manipulating objects and strings. However, it has limitations. For instance, Java arrays still need to be cast to JSweet arrays to access the JavaScript API. Moreover, programmers have to be aware that using strict mode within an IDE will prevent from having JSweet and Java code mixed within the same project (they may be in different projects though). Here is a list of tips about using strict mode.

-   You can use strict mode on full JavaScript projects, for instance pure client-side applications or full-stack applications with a Node.js server.

-   You can use strict mode on small pure JavaScript components with very few interactions with the outside world (almost no data exchanged).

-   Avoid using strict mode when wanting to share some data between JavaScript and some Java tiers (typically trough DTOs).

-   Avoid using strict mode when you feel that one day, your code may collaborate closer with a Java application (typically a Java server).
