package org.jsweet.transpiler.eval;

import java.io.File;

public class EvalOptions {
	public final boolean useModules;
	public final File workingDir;

	public EvalOptions(boolean useModules, File workingDir) {
		this.useModules = useModules;
		this.workingDir = workingDir;
	}

}
