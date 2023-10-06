package ts.client.refactors;

/**
 * Represents a single refactoring action - for example, the "Extract Method..."
 * refactor might offer several actions, each corresponding to a surround class
 * or closure to extract into.
 */
public class RefactorActionInfo {

	/**
	 * The programmatic name of the refactoring action
	 */
	private String name;

	/**
	 * A description of this refactoring action to show to the user. If the parent
	 * refactoring is inlined away, this will be the only text shown, so this
	 * description should make sense by itself if the parent is inlineable=true
	 */
	private String description;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
