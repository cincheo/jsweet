package def.dom;

import java.util.function.Consumer;

import def.js.Object;
import def.js.Promise;
import jsweet.lang.Interface;
import jsweet.lang.Name;
import jsweet.util.union.Union;

@Interface
public abstract class MediaStreamTrack {
	public boolean enabled;
	public final String id = null;
	public final boolean isolated = false;
	public final String kind = null;
	public final String label = null;
	public final boolean muted = false;
	@Name("final")
	public final boolean Final = false;
	public final String readyState = null;
	public final boolean remote = false;
	public Consumer<Event> onended;
	public Consumer<Event> onisolationchange;
	public Consumer<Event> onmute;
	public Consumer<Event> onoverconstrained;
	public Consumer<Event> onunmute;

	public native Promise<Void> applyConstraints(MediaTrackConstraints constraints);

	public native MediaStreamTrack clone();

	public native MediaTrackConstraints getConstraints();

	public native Object getSettings();

	public native void stop();

	public native void addEventListener(String type, Consumer<java.lang.Object> listener,
			Union<Boolean, AddEventListenerOptions> options);

	public native void removeEventListener(String type, EventListener listener,
			Union<Boolean, EventListenerOptions> options);

	public native void removeEventListener(String type, EventListenerObject listener,
			Union<Boolean, EventListenerOptions> options);
}