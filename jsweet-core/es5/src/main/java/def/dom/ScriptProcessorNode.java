package def.dom;
public class ScriptProcessorNode extends AudioNode {
    public double bufferSize;
    public java.util.function.Function<AudioProcessingEvent,Object> onaudioprocess;
    native public void addEventListener(jsweet.util.StringTypes.audioprocess type, java.util.function.Function<AudioProcessingEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static ScriptProcessorNode prototype;
    public ScriptProcessorNode(){}
    native public void addEventListener(jsweet.util.StringTypes.audioprocess type, java.util.function.Function<AudioProcessingEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

