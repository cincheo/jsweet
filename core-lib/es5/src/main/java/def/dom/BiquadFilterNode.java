package def.dom;
import def.js.Float32Array;
public class BiquadFilterNode extends AudioNode {
    public AudioParam Q;
    public AudioParam detune;
    public AudioParam frequency;
    public AudioParam gain;
    public String type;
    native public void getFrequencyResponse(Float32Array frequencyHz, Float32Array magResponse, Float32Array phaseResponse);
    public static BiquadFilterNode prototype;
    public BiquadFilterNode(){}
}

