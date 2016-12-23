package def.dom;
public class MSInputMethodContext extends EventTarget {
    public double compositionEndOffset;
    public double compositionStartOffset;
    public java.util.function.Function<Event,Object> oncandidatewindowhide;
    public java.util.function.Function<Event,Object> oncandidatewindowshow;
    public java.util.function.Function<Event,Object> oncandidatewindowupdate;
    public HTMLElement target;
    native public ClientRect getCandidateWindowClientRect();
    native public String[] getCompositionAlternatives();
    native public Boolean hasComposition();
    native public Boolean isCandidateWindowVisible();
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowHide type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowShow type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowUpdate type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static MSInputMethodContext prototype;
    public MSInputMethodContext(){}
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowHide type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowShow type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSCandidateWindowUpdate type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

