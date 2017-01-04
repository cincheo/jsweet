package def.dom;
import def.js.Float32Array;
public class WaveShaperNode extends AudioNode {
    public Float32Array curve;
    public String oversample;
    public static WaveShaperNode prototype;
    public WaveShaperNode(){}
}

