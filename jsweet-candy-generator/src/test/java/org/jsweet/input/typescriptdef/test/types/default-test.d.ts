
declare module 'm' {

    interface Typed<T, U, V> { }

    class TypedImpl extends Typed<string, any, number> { }

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
        'field-with-dashes': string;
    }

    interface Tinytest {
        add(name: string, func: Function): any;
        addAsync(name: string, func: Function): any;
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

    class C {
    }

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
    static: number;
    static: number;
    extends: string;
    extends: string;
}

declare module "module-with-dashes" {
    var v;
}

declare module "module/with/slashes" {
    var v;
}


declare module a.b {
    module c1 {
        var v;
    }
    module c2 {
        var v;
    }
    module c3 {
        var v;
    }
}

module datetest {

    interface Date {
        /** Returns a string representation of a date. The format of the string depends on the locale. */
        toString(): string;
        /** Returns a date as a string value. */
        toDateString(): string;
        /** Returns a time as a string value. */
        toTimeString(): string;
    }

    interface DateConstructor {
        new (): Date;
        new (value: number): Date;
        new (value: string): Date;
    }

    declare var Date: DateConstructor;
}

module events {
    export interface EventEmitter {
        addListener(event: string, listener: Function): this;
        on(event: string, listener: Function): this;
        once(event: string, listener: Function): this;
        removeListener(event: string, listener: Function): this;
        removeAllListeners(event?: string): this;
        setMaxListeners(n: number): this;
        getMaxListeners(): number;
        listeners(event: string): Function[];
        emit(event: string, ...args: any[]): boolean;
        listenerCount(type: string): number;
    }
    
    export function myFunction(s : string) : string;
}    

declare var myFunction2: typeof events.myFunction;


