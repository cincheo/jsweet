package def.js;

import java.util.function.Function;

@jsweet.lang.Interface
public abstract class PromiseLike<T> extends def.js.Object {
	/**
	 * Attaches callbacks for the resolution and/or rejection of the Promise.
	 * 
	 * @param onfulfilled
	 *            The callback to execute when the Promise is resolved.
	 * @param onrejected
	 *            The callback to execute when the Promise is rejected.
	 * @returns A Promise for the completion of which ever callback is executed.
	 */
	native public <TResult> PromiseLike<TResult> then(Function<T, ?> onfulfilled, def.js.Function onrejected);
}
