package org.jsweet.transpiler.eval;

import java.io.File;

public class EvalOptions {
    final boolean useModules;
    final File workingDir;
    final File useJavaRuntime;

    public EvalOptions(boolean useModules, File workingDir, File pathToJ4TsJs) {
        this.useModules = useModules;
        this.workingDir = workingDir;
        this.useJavaRuntime = pathToJ4TsJs;
    }
}
