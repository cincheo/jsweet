package def.dom;

public class DynamicsCompressorNode extends AudioNode {
    public AudioParam attack;
    public AudioParam knee;
    public AudioParam ratio;
    public AudioParam reduction;
    public AudioParam release;
    public AudioParam threshold;
    public static DynamicsCompressorNode prototype;
    public DynamicsCompressorNode(){}
}

