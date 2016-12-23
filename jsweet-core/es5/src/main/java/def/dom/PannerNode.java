package def.dom;
public class PannerNode extends AudioNode {
    public double coneInnerAngle;
    public double coneOuterAngle;
    public double coneOuterGain;
    public String distanceModel;
    public double maxDistance;
    public String panningModel;
    public double refDistance;
    public double rolloffFactor;
    native public void setOrientation(double x, double y, double z);
    native public void setPosition(double x, double y, double z);
    native public void setVelocity(double x, double y, double z);
    public static PannerNode prototype;
    public PannerNode(){}
}

