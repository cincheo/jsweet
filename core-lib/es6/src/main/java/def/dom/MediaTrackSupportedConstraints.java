package def.dom;

import jsweet.lang.Interface;
import jsweet.lang.Optional;

@Interface
public abstract class MediaTrackSupportedConstraints {
	@Optional
	public boolean aspectRatio;
	@Optional
	public boolean deviceId;
	@Optional
	public boolean echoCancellation;
	@Optional
	public boolean facingMode;
	@Optional
	public boolean frameRate;
	@Optional
	public boolean groupId;
	@Optional
	public boolean height;
	@Optional
	public boolean sampleRate;
	@Optional
	public boolean sampleSize;
	@Optional
	public boolean volume;
	@Optional
	public boolean width;
}