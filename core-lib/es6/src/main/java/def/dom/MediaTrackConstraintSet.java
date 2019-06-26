package def.dom;

import jsweet.lang.Interface;
import jsweet.lang.Optional;

@Interface
public abstract class MediaTrackConstraintSet {
	@Optional
	public Number aspectRatio;
	@Optional
	public Number channelCount;
	@Optional
	public String deviceId;
	@Optional
	public String displaySurface;
	@Optional
	public Boolean echoCancellation;
	@Optional
	public String facingMode;
	@Optional
	public Number frameRate;

	@Optional
	public String groupId;
	@Optional
	public Number height;
	@Optional
	public Number latency;
	@Optional
	public Boolean logicalSurface;
	@Optional
	public Number sampleRate;
	@Optional
	public Number sampleSize;
	@Optional
	public Number volume;
	@Optional
	public Number width;
}