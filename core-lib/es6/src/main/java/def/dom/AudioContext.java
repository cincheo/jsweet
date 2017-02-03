package def.dom;

import def.js.ArrayBuffer;

public class AudioContext extends EventTarget {
    public double currentTime;
    public AudioDestinationNode destination;
    public AudioListener listener;
    public double sampleRate;
    native public AnalyserNode createAnalyser();
    native public BiquadFilterNode createBiquadFilter();
    native public AudioBuffer createBuffer(double numberOfChannels, double length, double sampleRate);
    native public AudioBufferSourceNode createBufferSource();
    native public ChannelMergerNode createChannelMerger(double numberOfInputs);
    native public ChannelSplitterNode createChannelSplitter(double numberOfOutputs);
    native public ConvolverNode createConvolver();
    native public DelayNode createDelay(double maxDelayTime);
    native public DynamicsCompressorNode createDynamicsCompressor();
    native public GainNode createGain();
    native public MediaElementAudioSourceNode createMediaElementSource(HTMLMediaElement mediaElement);
    native public OscillatorNode createOscillator();
    native public PannerNode createPanner();
    native public PeriodicWave createPeriodicWave(java.lang.Object real, java.lang.Object imag);
    native public ScriptProcessorNode createScriptProcessor(double bufferSize, double numberOfInputChannels, double numberOfOutputChannels);
    native public StereoPannerNode createStereoPanner();
    native public WaveShaperNode createWaveShaper();
    native public void decodeAudioData(ArrayBuffer audioData, DecodeSuccessCallback successCallback, DecodeErrorCallback errorCallback);
    public static AudioContext prototype;
    public AudioContext(){}
    native public ChannelMergerNode createChannelMerger();
    native public ChannelSplitterNode createChannelSplitter();
    native public DelayNode createDelay();
    native public ScriptProcessorNode createScriptProcessor(double bufferSize, double numberOfInputChannels);
    native public ScriptProcessorNode createScriptProcessor(double bufferSize);
    native public ScriptProcessorNode createScriptProcessor();
    native public void decodeAudioData(ArrayBuffer audioData, DecodeSuccessCallback successCallback);
}

