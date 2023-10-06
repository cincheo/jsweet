package ts.client.format;

public class EditorSettings {

	private Integer baseIndentSize;

	private Integer indentSize;
	private Integer tabSize;
	private String newLineCharacter;
	private Boolean convertTabsToSpaces;
	private IndentStyle indentStyle;

	public Integer getBaseIndentSize() {
		return baseIndentSize;
	}

	public void setBaseIndentSize(Integer baseIndentSize) {
		this.baseIndentSize = baseIndentSize;
	}

	public Integer getIndentSize() {
		return indentSize;
	}

	public void setIndentSize(Integer indentSize) {
		this.indentSize = indentSize;
	}

	public Integer getTabSize() {
		return tabSize;
	}

	public void setTabSize(Integer tabSize) {
		this.tabSize = tabSize;
	}

	public String getNewLineCharacter() {
		return newLineCharacter;
	}

	public void setNewLineCharacter(String newLineCharacter) {
		this.newLineCharacter = newLineCharacter;
	}

	public Boolean getConvertTabsToSpaces() {
		return convertTabsToSpaces;
	}

	public void setConvertTabsToSpaces(Boolean convertTabsToSpaces) {
		this.convertTabsToSpaces = convertTabsToSpaces;
	}

	public IndentStyle getIndentStyle() {
		return indentStyle;
	}

	public void setIndentStyle(IndentStyle indentStyle) {
		this.indentStyle = indentStyle;
	}

}
