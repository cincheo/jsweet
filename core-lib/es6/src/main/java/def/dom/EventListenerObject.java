package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class EventListenerObject extends def.js.Object {
    native public void handleEvent(Event evt);
}

