package ts.cmd.tsc;

import ts.cmd.ITypeScriptLinterHandler;

public interface ITypeScriptCompilerMessageHandler extends ITypeScriptLinterHandler {

	void addFile(String file, boolean emitted);

	void onCompilationCompleteWatchingForFileChanges();

}
