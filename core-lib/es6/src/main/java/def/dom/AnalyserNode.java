package def.dom;

import def.js.Uint8Array;

public class AnalyserNode extends AudioNode {
    public double fftSize;
    public double frequencyBinCount;
    public double maxDecibels;
    public double minDecibels;
    public double smoothingTimeConstant;
    native public void getByteFrequencyData(Uint8Array array);
    native public void getByteTimeDomainData(Uint8Array array);
    native public void getFloatFrequencyData(java.lang.Object array);
    native public void getFloatTimeDomainData(java.lang.Object array);
    public static AnalyserNode prototype;
    public AnalyserNode(){}
}

