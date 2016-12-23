/* Generated from Java with JSweet 2.0.0-SNAPSHOT - http://www.jsweet.org */
namespace source.generics {
    import Deque = java.util.Deque;

    import LinkedList = java.util.LinkedList;

    export class RawTypes<T> extends Cls<any> implements Itf<any> {
        public static main(args : string[]) {
            let r1 : RawTypes<string> = <any>(new RawTypes());
            let r2 : RawTypes<any> = <any>(new RawTypes());
            let f : any = (o) => o;
            console.info("Hi, random=" + f(4));
            let deque1 : Deque<string> = <any>(new LinkedList());
            let deque2 : Deque<any> = <any>(new LinkedList());
        }

        constructor() {
            super();
        }
    }
    RawTypes["__class"] = "source.generics.RawTypes";
    RawTypes["__interfaces"] = ["source.generics.Itf"];



    export interface Itf<T> {    }

    export class Cls<T> {    }
    Cls["__class"] = "source.generics.Cls";

}


source.generics.RawTypes.main(null);
