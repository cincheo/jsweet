declare module FunctionalInterfaceTests {

    // this is a functional interface
    interface ListIterator<T, TResult> {
        // translates to $apply
        (value: T, index: number, list: List<T>): TResult;
    }

    interface Collection<T> { }

    // translates to a class that implements the java.lang.Iterable interface
    interface List<T> extends Collection<T> {
        [index: number]: T;
        length: number;
    }

}