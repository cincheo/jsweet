
type LatLngExpression = string | number[] | ({ lat: number; lng: number })

interface I<V> {
    items():[string,V][];
}

interface TestStringTypes {
    
    m1(abc:"abc");

    m2(abc:'abc');
    
}

interface Array<T> {
}

interface I2 extends I<I2 & Array<I2>> {
}

export module config {
    interface IPromise<T> {}
     
    export var Promise: { new <T>(resolver: (resolvePromise: (value: T) => void, rejectPromise: (reason: any) => void) => void): IPromise<T>; };
 
    interface I<T> {
        toPromise<TPromise extends IPromise<T>>(promiseCtor: { new (resolver: (resolvePromise: (value: T) => void, rejectPromise: (reason: any) => void) => void): TPromise; }): TPromise;
    }
}
