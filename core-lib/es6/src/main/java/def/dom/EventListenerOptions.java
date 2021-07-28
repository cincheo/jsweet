package def.dom;

import jsweet.lang.Interface;
import jsweet.lang.Optional;

@Interface
public abstract class EventListenerOptions {
	@Optional
	Boolean capture;
}