package def.dom;
import def.js.ArrayBufferView;
@jsweet.lang.Interface
public abstract class RandomSource extends def.js.Object {
    native public ArrayBufferView getRandomValues(ArrayBufferView array);
}

