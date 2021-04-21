package def.dom;

import jsweet.lang.Interface;
import jsweet.lang.Optional;

@Interface
public abstract class MediaTrackConstraints extends MediaTrackConstraintSet {
	@Optional
	public MediaTrackConstraintSet[] advanced;
}