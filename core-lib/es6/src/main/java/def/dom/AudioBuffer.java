package def.dom;

import def.js.Object;

public class AudioBuffer extends def.js.Object {
    public double duration;
    public double length;
    public double numberOfChannels;
    public double sampleRate;
    native public java.lang.Object getChannelData(double channel);
    public static AudioBuffer prototype;
    public AudioBuffer(){}
}

