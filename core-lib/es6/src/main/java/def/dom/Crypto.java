package def.dom;

import def.js.ArrayBufferView;

@jsweet.lang.Extends({RandomSource.class})
public class Crypto extends java.lang.Object {
    public SubtleCrypto subtle;
    public static Crypto prototype;
    public Crypto(){}
    native public ArrayBufferView getRandomValues(ArrayBufferView array);
}

