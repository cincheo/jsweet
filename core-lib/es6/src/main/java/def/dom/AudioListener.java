package def.dom;

import def.js.Object;

public class AudioListener extends def.js.Object {
    public double dopplerFactor;
    public double speedOfSound;
    native public void setOrientation(double x, double y, double z, double xUp, double yUp, double zUp);
    native public void setPosition(double x, double y, double z);
    native public void setVelocity(double x, double y, double z);
    public static AudioListener prototype;
    public AudioListener(){}
}

