package def.dom;

import jsweet.lang.Interface;
import jsweet.lang.Optional;

@Interface
public abstract class AddEventListenerOptions extends EventListenerOptions {
	@Optional
	Boolean once;
	@Optional
	Boolean passive;
}