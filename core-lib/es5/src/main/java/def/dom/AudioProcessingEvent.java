package def.dom;
public class AudioProcessingEvent extends Event {
    public AudioBuffer inputBuffer;
    public AudioBuffer outputBuffer;
    public double playbackTime;
    public static AudioProcessingEvent prototype;
    public AudioProcessingEvent(){}
}

