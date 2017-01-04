declare module ObjectTypeTests {

    class A {
        field: { a: string; b: number };    
    }
    
    class B extends A {
        field: { a: string; b: number };    
    }
    
    class C<T extends { a: string; b: number }> {
        
    }
    
    var variable: { a: string; b: number };
    var variableGenerics: C<{ a: string; b: number }>;
    
    function foo(p: { a: string; b: number }): void;
    
    function bar(): { a: string; b: number };

    interface I {
        // notice the comma that seems to be allowed
        connect(options: { port: number, host: string }, connectionListener?: B): A;
    }    
    
}
    