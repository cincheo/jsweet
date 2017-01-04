declare module "foo" {
    interface A {
        field?: number;
        method(p: any): boolean;
        f: A;
    }
    
    interface B extends A {
        f1 : B;
        f2: A;
    }
    
    type Alias = A;
}

declare module ImportTests {

    import Foo = require('foo');
    
    class A {
        field: Foo.A;
        fieldAlias: Foo.Alias;    
    }
}

import T1 = _T1;

declare module _T1 {

    interface I {}
    
}    

declare module publicModule {

    module privateModule {
        var v : number;
        interface TestExport {
        }
        function f(): TestExport;
    }
    
    function privateModule(): privateModule.TestExport;
    
    export = privateModule;
    
}    

declare module publicModule2 {
    
    function privateFunction(): string;
    
    export = privateFunction;
    
}