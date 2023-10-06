package ts.client;

public class ExternalFile {
	/**
	 * Name of file file
	 */
	String fileName;
	/**
	 * Script kind of the file
	 */
	ScriptKindName scriptKind;
	/**
	 * Whether file has mixed content (i.e. .cshtml file that combines html markup
	 * with C#/JavaScript)
	 */
	Boolean hasMixedContent;
	/**
	 * Content of the file
	 */
	String content;

	public ExternalFile(String fileName, ScriptKindName scriptKind, Boolean hasMixedContent, String content) {
		this.fileName = fileName;
		this.scriptKind = scriptKind;
		this.hasMixedContent = hasMixedContent;
		this.content = content;
	}

}