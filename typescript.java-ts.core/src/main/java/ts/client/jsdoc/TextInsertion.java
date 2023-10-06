package ts.client.jsdoc;

/**
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/services/types.ts
 * 
 */
public class TextInsertion {

	private String newText;

	/**
	 * The position in newText the caret should point to after the insertion.
	 */
	private int caretOffset;

	public String getNewText() {
		return newText;
	}

	public int getCaretOffset() {
		return caretOffset;
	}
}
