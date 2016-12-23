package def.dom;
import def.js.ArrayBuffer;
import def.js.ArrayBufferView;
public class SourceBuffer extends EventTarget {
    public double appendWindowEnd;
    public double appendWindowStart;
    public AudioTrackList audioTracks;
    public TimeRanges buffered;
    public String mode;
    public double timestampOffset;
    public Boolean updating;
    public VideoTrackList videoTracks;
    native public void abort();
    native public void appendBuffer(ArrayBuffer data);
    native public void appendStream(MSStream stream, double maxSize);
    native public void remove(double start, double end);
    public static SourceBuffer prototype;
    public SourceBuffer(){}
    native public void appendStream(MSStream stream);
    native public void appendBuffer(ArrayBufferView data);
}

