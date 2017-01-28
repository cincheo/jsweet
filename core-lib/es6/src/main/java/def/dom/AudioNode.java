package def.dom;

public class AudioNode extends EventTarget {
    public double channelCount;
    public java.lang.String channelCountMode;
    public java.lang.String channelInterpretation;
    public AudioContext context;
    public double numberOfInputs;
    public double numberOfOutputs;
    native public void connect(AudioNode destination, double output, double input);
    native public void disconnect(double output);
    public static AudioNode prototype;
    public AudioNode(){}
    native public void connect(AudioNode destination, double output);
    native public void connect(AudioNode destination);
    native public void disconnect();
}

