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

    // warning '==' behaves like the JavaScript one at runtime
    int i = 2;
    assert i == 2;
    double d = i + 4;
    assert d == 6;
    String s = "string" + '0' + i;
    assert s == "string02"; // JavaScript '=='
    boolean b = false;
    assert !b;

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

        -   `substring(int)`

        -   `substring(int,int)` (with the JavaScript behavior)

        -   `replace(java.lang.String,java.lang.String)`

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

    // warning '==' behaves like the JavaScript one at runtime
    Integer i = 2;
    assert i == 2; // JavaScript '=='
    Double d = i + 4;
    assert d.toString() == "6"; // JavaScript '=='
    assert d == "6"; // JavaScript '=='
    BiFunction<String, Integer, String> f = (s, i) -> { return s.substring(i); };
    assert "bc" == f.apply("abc", 1); // JavaScript '=='

#### Java arrays

Arrays can be used in JSweet and are transpiled to JavaScript arrays. Array initialization, accesses and and iteration are all valid statements.

    int[] arrayOfInts = { 1, 2, 3, 4};
    assert arrayOfInts.length == 4;  
    assert arrayOfInts[0] == 1;
    for(int i : arrayOfInts) {
        arrayOfInts[i] = arrayOfInts[i] - 1;
        assert arrayOfInts[i] == i;
    }

#### Core JavaScript API

The core JavaScript API is defined in `jsweet.lang` (the full documentation can be found at <http://www.jsweet.org/core-api-javadoc/>). Main JavaScript classes are:

-   `jsweet.lang.Object`: common ancestor for JavaScript objects functions and properties.

-   `jsweet.lang.Boolean`: a wrapper for boolean values.

-   `jsweet.lang.Number`: a wrapper for numerical values.

-   `jsweet.lang.String`: a wrapper and constructor for strings.

-   `jsweet.lang.Function`: a constructor for functions.

-   `jsweet.lang.Date`: enables basic storage and retrieval of dates and times.

-   `jsweet.lang.Array<T>`: used in the construction of arrays, which are high-level, list-like objects.

-   `jsweet.lang.Error`: this class implements `java.lang.RuntimeException` and can be thrown and caught with `try` ... `catch` statements.

Programmers should use this API most of the time. However, for objects that need to be used with Java literals (numbers, booleans, and strings), the use of the `java.lang` package classes is recommended. For instance, the jQuery API declares `$(java.lang.String)` instead of `$(jsweet.lang.String)`. This allows the programmer to write expressions using literals, such as `$(a)` (for selecting all links in a document).

As a consequence, programmers need to be able to switch to the JavaScript API when coming from a Java object. The `jsweet.util.Globals` class defines convenient static methods to cast back and forth core Java objects to their corresponding JSweet objects. For instance the `string(...)` method will allow the programmer to switch from the Java to the JSweet strings and conversely.

    import jsweet.util.Globals.string;
    String str = "This is a test string";
    str.toLowerCase(); // valid: toLowerCase it defined both in Java and JavaScript
    str.substr(1); // compile error: substr is not a Java method
    string(str).substr(1); // valid: string(str) is a jsweet.lang.String.

Here is another example that shows the use of the `array` method to access the `push` method available on JavaScript arrays.

    import jsweet.util.Globals.array;
    String[] strings = { "a", "b", "c" };
    array(strings).push("d");
    assert strings[3] == "d";

### Classes

Classes in JSweet are very similar to Java classes. For example:

    public class BankAccount {  
        public double balance = 0;  
        public double deposit(double credit) {  
            balance += credit;  
            return this.balance;  
        }  
    }  

Which is transpiled to the following JavaScript code:

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

Classes can define constructors, have super classes and be instantiated exactly like in Java. The only restriction compared to Java is that inner classes or anonymous classes are not allowed in JSweet. For instance, the following code will raise an error.

    public class ContainerClass {
        // error: inner classes are not allowed
        public class InnerClass {
        }
    }

### Interfaces

In JSweet, an interface (a.k.a. object type) can be seen as object signature, that is to say the accessible functions and properties of an object (without specifying any implementation). An interface is defined for typing only and has no runtime representation (no instances), however, they can be used to type objects.

JSweet interfaces can be defined as regular Java interfaces, but also as Java classes annotated with `@jsweet.lang.Interface`, so that is is possible to define properties as fields. Such classes impose many constraints, as shown in the following code.

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

#### Object typing

In JSweet, typed object can be constructed out of interfaces. If we take the following interface:

    @Interface
    public class Point {
        public double x;
        public double y;
    }

We can create an object typed after the interface. Note that the following code is not actually creating an instance of the `Point` interface, it is creating an object that conforms to the interface.

    Point p1 = new Point() {{ x=1; y=1; }};

As a direct consequence, in JSweet it is not allowed to check an instance against an interface type.

    if (p1 instanceof Point) { ... } // compile error 

This may seems quite confusing for Java programmers, but you have to remember that, on contrary to Java where interfaces are available as special classes at runtime, in JSweet, interfaces have no reality at runtime. Think of generics, which are of the same kind in Java. As a consequence, the `instanceof` operator is not applicable on interfaces at runtime (like it is not applicable on generics).

#### Optional fields

Interfaces can define *optional fields*, which are used to report errors when the programmer forgets to initialize a mandatory field in an object. Supporting optional fields in JSweet is done through the use of `@jsweet.lang.Optional` annotations. For instance:

    @Interface
    public class Point {
        public double x;
        public double y;
        @Optional
        public double z = 0;
    }

It is the JSweet compiler that will check that the fields are correctly initialized, when constructing an object.

    // no errors (z is optional)
    Point p1 = new Point() {{ x=1; y=1; }};
    // JSweet reports a compile error since y is not optional
    Point p2 = new Point() {{ x=1; z=1; }};

#### Special functions in interfaces

In JavaScript, objects can have properties and functions, but can also (not exclusively), be used as constructors and functions themselves. This is not possible in Java, so JSweet defines special functions for handling these cases.

-   `$apply` is used to state that the object can be used as a function.

-   `$new` is used to state that the object can be used as a constructor.

### Enums

JSweet allows the definition of enums similarly to Java, but with some restrictions. The following code declares an enum with tree possible values (`A`, `B`, and `C`).

    enum MyEnum {
        A, B, C
    }

The following statements are valid statements in JSweet.

    MyEnum e = MyEnum.A;
    assert MyEnum.A == e;
    assert e.name().equals("A");
    assert e.ordinal() == 0;
    assert MyEnum.valueOf("A") == e;
    assert array(MyEnum.values()).indexOf(MyEnum.valueOf("C")) == 2;

On the other hand, unlike Java enums, other members than constants are not allowed, raising JSweet transpilation errors, as shown in the following code.

    public enum WrongConstructsInEnums {

        A, B, C;
        
        // error: fields are not allowed
        public long l = 4;
        // error: fields are not allowed
        static String s1;
        // error: fields are not allowed
        private String s2;
        // error: constructors are not allowed  
        private WrongConstructsInEnums() {
            l = 4;
        }
        // error: methods are not allowed   
        native public void m1();
        // error: methods are not allowed
        public void m2() {
            l = 4;
        }
        // error: methods are not allowed
        native static void m3();
        // error: initializers are not allowed
        {
            l = 4;
        }
        // error: initializers are not allowed
        static {
            s1 = "";
        }
    }

### Indexed objects

In JavaScript, object can be seen as maps containing key-value pairs (key is often called *index*, especially when it is a number). So, in JSweet, all objects define the special functions (defined on `jsweet.lang.Object`):

-   `$get(key)` accesses a value with the given key.

-   `$set(key,value)` sets or replace a value for the given key.

-   `$delete(key)` deletes the value for the given key.

The type of keys and values can be overloaded for every object. For example, the `Array<T>` class, will define keys as numbers and values as objects conforming to type `T`.

In the case of objects indexed with number keys, it is allowed to implement the `java.lang.Iterable` interface so that it is possible to use they in *foreach* loops. For instance, the `NodeList` type (from the DOM) defines an indexed function:

    @Interface
    class NodeList implements java.lang.Iterable {
        public double length;
        public Node item(double index);
        public Node $get(double index);
    }

In JSweet, you can access the node list elements with the `$get` function, and your can also iterate with the *foreach* syntax. The following code generates fully valid JavaScript code.

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

### Optional parameters

In JavaScript, parameters can be optional, in the sense that a parameter value does not need to be provided when calling a function. Except for varargs, which are fully supported in JSweet, the general concept of an optional parameter does not exits in Java. To simulate optional parameters, JSweet supports overloading as long as it is for implementing optional parameters – a very commonly used idiom. Here are some example of valid and invalid overloading in JSweet:

    String m(String s, double n) { return s + n; }
    // valid overloading (JSweet transpiles to optional parameter)
    String m(String s) { return m(s, 0); }
    // invalid overloading (JSweet error)
    String m(String s) { return s; }

Types
-----

JSweet uses most Java typing features (including functional types) but also extends the Java type system with so-called *auxiliary types*. The idea behind auxiliary types is to create classes or interfaces that can hold the typing information through the use of type parameters (a.k.a *generics*), so that the JSweet transpiler can cover more typing scenarios. These types have been mapped from TypeScript type system, which is much richer than the Java one (mostly because JavaScript is a dynamic language and request more typing scenarios than Java).

### Functional types

For functional types, JSweet reuses the `java.Runnable` and `java.util.function` functional interfaces of Java 8. These interfaces only support up to 2-parameter functions. Thus, JSweet adds some support for more parameters in `jsweet.util.function`, since it is a common case in JavaScript APIs.

// To be completed

### Object types

// Todo

### String types

For string types, the use of specific types and final instances of these types is the way to simulate string types in Java. For instance, if a `span` string type needs to be defined, a Java interface called `span` and a static final field called `span` in a `StringTypes` class will do the job.

// To be completed

### Tuple types

Tuple types represent JavaScript arrays with individually tracked element types. For tuple types, JSweet defines parameterized auxiliary classes `TupleN<T0, ... TN-1>`, which define `$0`, `$1`, ... `$N-1` public fields to simulate typed array accessed (field `$i` is typed with `Ti`).

// To be completed

### Union types

Union types represent values that may have one of several distinct representations. For union types, JSweet takes advantage of the `method overloading` mechanism available in Java . For more general cases, it defines an auxiliary interface `Union<T1, T2>` (and `UnionN<T1, ... TN>`) in the `jsweet.util.union` package. By using this auxiliary type and a `union` utility method, programmer can cast back and forth between union types and union-ed type, so that JSweet can ensure similar properties as TypeScript union types.

The following code shows a typical use of union types in JSweet. It simply declares a variable as a union between a string and a number, which means that the variable can actually be of one of that types (but of no other types). The switch from a union type to a regular type is done through the `jsweet.util.Globals.union` helper method. This helper method is completely untyped, allowing from a Java perspective any union to be transformed to another type. It is actually the JSweet transpiler that checks that the union type is consistently used.

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

The `union` helper can also be used the other way, to switch from a regular type back to a union type, when expected.

    import static jsweet.util.Globals.union;
    import jsweet.util.union.Union3;
    [...]
    public void m(Union3<String, Number, Date>> u) { ... }
    [...]
    // u can be a String, a Number or a Date
    m(union("a string"));
    // but nothing else
    m(union(new RegExp(".*"))); // compile error

Note: the use of Java function overloading is preferred over union types when typing function parameters. For example:

    // with union types (discouraged)
    native public void m(Union3<String, Number, Date>> u);
    // with overloading (preferred way)
    native public void m(String s);
    native public void m(Number n);
    native public void m(Date d);

Semantics
---------

Semantics designate how a given program behaves when executed. Although JSweet relies on the Java syntax, programs are transpiled to JavaScript and do not run in a JRE. As a consequence, the JavaScript semantics will impact the final semantics of a JSweet program compared to a Java program. In this section, we discuss the semantics of some tricky programming constructs.

### Main methods

Main methods are the program execution entry points and will be invoked globally when a class containing a `main` method is evaluated.

### Initializers

Initializers behave like in Java.

To be completed...

### Name clashes

On contrary to Java, methods and fields of the same name are not allowed within the same class or within classes having a subclassing link.

As an example:

    public class NameClashes {

        // error: field name clashes with existing method name
        public String a;

        // error: method name clashes with existing field name
        public void a() {
        }

    }

#### Variable scoping in lambda expressions

Variables in JSweet are dealt with like final Java variables. In the following code, the element variable is re-scoped in the lambda expression so that the enclosing loop does not change its value.

    NodeList nodes = document.querySelectorAll(".control");
    for (int i = 0; i < nodes.length; i++) {
        HTMLElement element = (HTMLElement) nodes.$get(i); // final
        element.addEventListener("keyup", (evt) -> {
            // this element variable will not change here 
            element.classList.add("hit");
        });
    }

#### Scope of *this*

On contrary to JavaScript and similarly to Java, using a method as a lambda will prevent loosing the reference to `this`. For instance, at line 10 of the following program, `this` holds the right value, even when `action` was called as a lambda (line 14). Although this seem logical to Java programmers, it is not a given that the JavaScript semantics ensures this behavior.

``` numberLines
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

### Packages and modules

Todo...

### Globals

In Java, on contrary to JavaScript, there is no such thing as global variables or functions. Thus, JSweet introduces reserved `Globals` classes and `globals` packages. These have two purposes:

-   Generate code that has global variables and functions (this is discouraged in Java)

-   Bind to existing JavaScript code that defines global variables and functions (as many JavaScript framework do)

In Globals classes, only static fields (global variables) and static methods (global functions) are allowed. Here are the main constraints applying to Globals classes:

-   no non-static members

-   no super class

-   cannot be extended

-   cannot be used as types like regular classes

-   no public constructor (empty private constructor is OK)

-   cannot use $get, $set and $delete within the methods

For instance, the following code snippets will raise transpilation errors.

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

    // error: Globals classes cannot be used as types
    Globals myVariable = null;

### Root packages

Todo...

### Modules

Todo...

### Bundles

Todo...
