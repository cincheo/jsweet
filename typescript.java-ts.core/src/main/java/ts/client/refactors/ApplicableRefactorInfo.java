package ts.client.refactors;

import java.util.List;

/**
 * A set of one or more available refactoring actions, grouped under a parent
 * refactoring.
 */
public class ApplicableRefactorInfo {

	/**
	 * The programmatic name of the refactoring
	 */
	private String name;

	/**
	 * A description of this refactoring category to show to the user. If the
	 * refactoring gets inlined (see below), this text will not be visible.
	 */
	private String description;

	/**
	 * Inlineable refactorings can have their actions hoisted out to the top level
	 * of a context menu. Non-inlineanable refactorings should always be shown
	 * inside their parent grouping.
	 *
	 * If not specified, this value is assumed to be 'true'
	 */
	private Boolean inlineable;

	private List<RefactorActionInfo> actions;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getInlineable() {
		return inlineable;
	}

	public List<RefactorActionInfo> getActions() {
		return actions;
	}

	public boolean isInlineable() {
		return inlineable == null ? true : inlineable;
	}
}
