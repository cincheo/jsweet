package ts.cmd.tslint;

import java.io.File;

import ts.cmd.AbstractCmd;

public class TypeScriptLint extends AbstractCmd<TSLintOptions> implements ITypeScriptLint {

	private static final String TSLINT_FILE_TYPE = "tslint";

	private final File tslintJsonFile;

	public TypeScriptLint(File tslintFile, File tslintJsonFile, File nodejsFile) {
		super(tslintFile, nodejsFile, TSLINT_FILE_TYPE);
		this.tslintJsonFile = tslintJsonFile;
	}

	public File getTslintJsonFile() {
		return tslintJsonFile;
	}

}
