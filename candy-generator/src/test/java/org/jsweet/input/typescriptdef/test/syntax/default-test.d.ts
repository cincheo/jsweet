
module 'm' {

    interface Typed<T, U, V> {}
    
    class TypedImpl extends Typed<string, any, number> {}

    // test
    
    type T = string | number;
    
    ///////////////////////////////////////////////////////////////////////////
    // SCEService
    // see http://docs.angularjs.org/api/ng.$sce
    ///////////////////////////////////////////////////////////////////////////

    /**
     * titi 1
     */
    interface ISCEService {
        getTrusted(type: string, mayBeTrusted: any): any;
    }

    interface Tinytest {
        add(name:string, func:Function):any;
        addAsync(name:string, func:Function):any;
    }

    interface Array {
        isArray(arg: any): arg is Array<any>;
    }        
    
    /**
     * titi 2
     */
    enum StatusEnum {
        connected,
        connecting,
        failed,
        waiting,
        offline
    }

    export = i;

    /** the * promise of the original server interaction that created this instance. ** **/
      interface I2<{ test: string; }> extends I {

        /** The notify function can either be passed a string or an object. */

        (translationId: string[], interpolateParams?: any, interpolationId?: string): ng.IPromise<{ [key: string]: string }>;

            var test: string;

            a(b: any, c : number): string;

            b<T>(f : ((n: number) => void )[]) : t.x.y;
 
        [a : number] : T;

            (b: any, c: number): string;

                a2(b: { p1: P1; f(x:number):string; } [], c : number): string;

            function getLevel(nameOrValue: string): webdriver.logging.Level;

            sendKeys(...var_args: string[]): webdriver.promise.Promise;
    }

    module logging {
        var Preferences: any;

        class LevelName extends webdriver.logging.LevelName { }
        class Type extends webdriver.logging.Type { }
        class Level extends webdriver.logging.Level { }
        class Entry extends webdriver.logging.Entry { }

        /**
         * titi 3
         */
        function getLevel(nameOrValue: string): webdriver.logging.Level;
        function getLevel(nameOrValue: number): webdriver.logging.Level;
    }
}

interface I {
	a, b, c,
}

module m2 {
    rest: T;
    
    import * as i from m;
}
    

// TODO: support object_type @tsdef\angular-http-auth\angular-http-auth.d.ts


enum StatusEnum {
    connected, connecting,
    failed, waiting, offline
}

interface NodeListOf<TNode extends Node> extends NodeList, T, U {
    length: {
    }
    item(index: number): TNode;
    [index: number]: TNode;
    static : number;
    static: number;
    extends: string;
    extends : string;
}

class Model extends ModelBase {

    object<T>(...keyValuePairs: any[][]): T;
    
    /**
    * Do not use, prefer TypeScript's extend functionality.
    **/
    private static extend(properties: any, classProperties?: any): any;

    attributes: any;
    changed: any[];
    cid: string;
    collection: Collection<any>;
}

namespace n {
    
}

