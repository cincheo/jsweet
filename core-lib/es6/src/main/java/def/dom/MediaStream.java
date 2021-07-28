package def.dom;

import java.util.function.Consumer;

import jsweet.lang.Interface;
import jsweet.util.union.Union;

@Interface
public abstract class MediaStream {
	public final boolean active = false;
	public final String id = null;

	public Consumer<Event> onactive;
	public Consumer<Event> onaddtrack;
	public Consumer<Event> oninactive;
	public Consumer<Event> onremovetrack;

	public native void addTrack(MediaStreamTrack track);

	public native MediaStream clone();

	public native MediaStreamTrack[] getAudioTracks();

	public native MediaStreamTrack[] getVideoTracks();

	public native MediaStreamTrack[] getTracks();

	public native MediaStreamTrack getTrackById(String trackId);

	public native void removeTrack(MediaStreamTrack track);

	public native void stop();

	public native void addEventListener(String type, Consumer<java.lang.Object> listener,
			Union<Boolean, AddEventListenerOptions> options);

	public native void removeEventListener(String type, EventListener listener,
			Union<Boolean, EventListenerOptions> options);

	public native void removeEventListener(String type, EventListenerObject listener,
			Union<Boolean, EventListenerOptions> options);
}