declare module TypeMacroTests {
    
    interface BaseTypedElement<P> {
        type: string;
        props: P;
    }
    
    interface ClassicElement<P> extends BaseTypedElement<P> {
        type: string;
        ref: string;
    }

    interface DOMElement<P> extends ClassicElement<P> {
        type: string;
        ref: string | ((component: DOMElement<P>) => any);
    }
    
    interface HTMLAttributes { }
    interface Component<T1, T2> {}
    interface ComponentClass<T> {}
    
    type HTMLElement = DOMElement<HTMLAttributes>;
    type HTMLElementAlias = HTMLElement | string;
    type HTMLElementComplex = HTMLElementAlias | number | void;
    type Ref<T> = string | ((instance: T) => any);
    type ClassType<P, T extends Component<P, {}>, C extends ComponentClass<P>> =
        C &
        (new() => T) &
        (new() => { props: P });
    
    interface ClassAttributes<U> {
        ref?: Ref<U>;
        l : String|LatLngLiteral;
    }
    
    interface EnclosingInterface {
        field: HTMLElement;
        fieldGenerics: BaseTypedElement<HTMLElement>;
        
        fieldWithUnion: HTMLElementComplex;
        fieldWithGenericsAndUnion: BaseTypedElement<HTMLElementComplex>;
        
        methodWithParam(p: HTMLElement, ...p2: HTMLElementComplex[]): void;
        methodWithReturn(): HTMLElement;
        l : String|LatLngLiteral;
    }

    interface ReactElement<T> {}
    interface Array<T> {}

    type GenericType = <T1 extends Array<any>, T2>(p1:T1, p2:T2)=>void;
    
    type ReactText = string | number;
    type ReactChild = ReactElement<any> | ReactText;

    // Should be Array<ReactNode> but type aliases cannot be recursive
    type ReactFragment = {} | Array<ReactChild | any[] | boolean>;
    type ReactNode = ReactChild | ReactFragment | boolean;

    export type LatLngLiteral = { lat: number; lng: number }
    
    //
    // Top Level API
    // ----------------------------------------------------------------------

    function createElement<P>(
        type: string,
        props?: P,
        ...children: ReactNode[]): DOMElement<P>;
    var reactNode : ReactNode;
    
    interface Test {
        forEach(children: ReactNode, fn: (child: ReactChild, index: number) => any): void;
    }        
    
}
    