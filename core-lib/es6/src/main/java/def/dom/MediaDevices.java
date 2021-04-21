package def.dom;

import java.util.function.Consumer;

import def.js.Promise;
import jsweet.lang.Interface;
import jsweet.util.union.Union;

@Interface
public abstract class MediaDevices {
	public native Promise<MediaDeviceInfo[]> enumerateDevices();

	public native MediaTrackSupportedConstraints getSupportedConstraints();

	public native Promise<MediaStream> getUserMedia(MediaStreamConstraints constraints);

	public native void addEventListener(String type, Consumer<java.lang.Object> listener,
			Union<Boolean, AddEventListenerOptions> options);

	public native void removeEventListener(String type, EventListener listener,
			Union<Boolean, EventListenerOptions> options);

	public native void removeEventListener(String type, EventListenerObject listener,
			Union<Boolean, EventListenerOptions> options);
}