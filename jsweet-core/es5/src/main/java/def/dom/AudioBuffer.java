package def.dom;
import def.js.Float32Array;
public class AudioBuffer extends def.js.Object {
    public double duration;
    public double length;
    public double numberOfChannels;
    public double sampleRate;
    native public Float32Array getChannelData(double channel);
    public static AudioBuffer prototype;
    public AudioBuffer(){}
}

