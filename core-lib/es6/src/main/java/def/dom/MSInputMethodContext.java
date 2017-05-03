package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.MSCandidateWindowHide;
import jsweet.util.StringTypes.MSCandidateWindowShow;
import jsweet.util.StringTypes.MSCandidateWindowUpdate;

public class MSInputMethodContext extends EventTarget {
    public double compositionEndOffset;
    public double compositionStartOffset;
    public java.util.function.Function<Event,java.lang.Object> oncandidatewindowhide;
    public java.util.function.Function<Event,java.lang.Object> oncandidatewindowshow;
    public java.util.function.Function<Event,java.lang.Object> oncandidatewindowupdate;
    public HTMLElement target;
    native public ClientRect getCandidateWindowClientRect();
    native public java.lang.String[] getCompositionAlternatives();
    native public java.lang.Boolean hasComposition();
    native public java.lang.Boolean isCandidateWindowVisible();
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowHide type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowShow type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowUpdate type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static MSInputMethodContext prototype;
    public MSInputMethodContext(){}
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowHide type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowShow type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowUpdate type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

