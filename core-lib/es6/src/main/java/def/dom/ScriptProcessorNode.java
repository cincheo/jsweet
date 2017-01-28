package def.dom;

import def.js.StringTypes;
import def.js.StringTypes.audioprocess;

public class ScriptProcessorNode extends AudioNode {
    public double bufferSize;
    public java.util.function.Function<AudioProcessingEvent,java.lang.Object> onaudioprocess;
    native public void addEventListener(def.js.StringTypes.audioprocess type, java.util.function.Function<AudioProcessingEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static ScriptProcessorNode prototype;
    public ScriptProcessorNode(){}
    native public void addEventListener(def.js.StringTypes.audioprocess type, java.util.function.Function<AudioProcessingEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

