package def.dom;

import def.js.ArrayBuffer;
import def.js.ArrayBufferView;

public class SourceBuffer extends EventTarget {
    public double appendWindowEnd;
    public double appendWindowStart;
    public AudioTrackList audioTracks;
    public TimeRanges buffered;
    public java.lang.String mode;
    public double timestampOffset;
    public java.lang.Boolean updating;
    public VideoTrackList videoTracks;
    native public void abort();
    native public void appendBuffer(ArrayBuffer data);
    native public void appendBuffer(ArrayBufferView data);
    native public void appendStream(MSStream stream, double maxSize);
    native public void remove(double start, double end);
    public static SourceBuffer prototype;
    public SourceBuffer(){}
    native public void appendStream(MSStream stream);
}

