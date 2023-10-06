package ts.client.refactors;

import java.util.List;

import ts.client.Location;
import ts.client.codefixes.FileCodeEdits;

public class RefactorEditInfo {

	private List<FileCodeEdits> edits;

	/**
	 * An optional location where the editor should start a rename operation once
	 * the refactoring edits have been applied
	 */
	private Location renameLocation;

	private String renameFilename;

	public List<FileCodeEdits> getEdits() {
		return edits;
	}

	public Location getRenameLocation() {
		return renameLocation;
	}

	public String getRenameFilename() {
		return renameFilename;
	}
}
