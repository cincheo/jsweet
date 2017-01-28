package def.dom;

public class BiquadFilterNode extends AudioNode {
    public AudioParam Q;
    public AudioParam detune;
    public AudioParam frequency;
    public AudioParam gain;
    public java.lang.String type;
    native public void getFrequencyResponse(java.lang.Object frequencyHz, java.lang.Object magResponse, java.lang.Object phaseResponse);
    public static BiquadFilterNode prototype;
    public BiquadFilterNode(){}
}

