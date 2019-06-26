package def.dom;

import jsweet.lang.Interface;
import jsweet.lang.Optional;
import jsweet.util.union.Union;

@Interface
public abstract class MediaStreamConstraints {
	@Optional
	Union<Boolean, MediaTrackConstraints> audio;

	@Optional
	public String peerIdentity;

	@Optional
	public Union<Boolean, MediaTrackConstraints> video;
}