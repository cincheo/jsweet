package org.jsweet.transpiler.eval;

import java.io.File;

public class EvalOptions {
    public final boolean useModules;
    public final File workingDir;
    public final boolean useJavaRuntime;

    public EvalOptions(boolean useModules, File workingDir, boolean useJavaRuntime) {
        this.useModules = useModules;
        this.workingDir = workingDir;
        this.useJavaRuntime = useJavaRuntime;
    }

}
