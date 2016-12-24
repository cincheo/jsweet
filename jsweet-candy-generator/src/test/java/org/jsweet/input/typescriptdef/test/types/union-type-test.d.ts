declare module UnionTypeTests {

    class A {
        field: string | number | boolean;    
    }
    
    class B extends A {
        field: string | number | boolean;
    }
    
    class C<T extends string | number | boolean | A> {
        field: T;    
    }
    
    class D extends C<boolean | A> {
    }
    
    var variable: string | number;
    var variableGenerics: C<string | number>;
    
    function foo(p: number | A | void): void;
    
    function bar(): boolean | A | void;
    
    interface I1 {
        f : string | number;    
    }

    interface I2 {
        f : string | (() => string);    
    }

    interface I3 {
        f : string | { () : string };    
    }
    
}
    