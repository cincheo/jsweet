/**
 * @External
 */
declare class RuntimeException {
}

interface Error extends RuntimeException {
}

/**
 * @External
 */
interface Iterable<T> {
}

//interface String extends RuntimeException {
//}

interface Object {
    /**
    * Gets the value for the given key. Generates <code>this[key]</code>.
    * 
    * @see jsweet.util.Globals#$get(java.lang.String)
    * @see jsweet.util.Globals#$set(java.lang.String,java.lang.Object)
    * @see jsweet.util.Globals#$delete(java.lang.String)
    */
    $get(key:String):Object;
    /**
    * Sets the value for the given key. Generates <code>this[key]=value</code>.
    * 
    * @see jsweet.util.Globals#$get(java.lang.String)
    * @see jsweet.util.Globals#$set(java.lang.String,java.lang.Object)
    * @see jsweet.util.Globals#$delete(java.lang.String)
    */
    $set(key:String,value:Object):void;
    /**
    * Deletes the value of the given key. Generates <code>delete this[key]</code>.
    * 
    * @see jsweet.util.Globals#$get(java.lang.String)
    * @see jsweet.util.Globals#$set(java.lang.String,java.lang.Object)
    * @see jsweet.util.Globals#$delete(java.lang.String)
    */
    $delete(key: String):void;
    $super(...params : any[]):Object;
}

interface Array<T> {

    reduce<R>(callbackfn: (reducedValue: R, currentValue: T) => R, initialValue?: R): R;  

}

