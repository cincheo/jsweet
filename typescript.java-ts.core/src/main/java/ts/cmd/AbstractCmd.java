package ts.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ts.TypeScriptException;
import ts.nodejs.INodejsLaunchConfiguration;
import ts.nodejs.INodejsProcess;
import ts.nodejs.INodejsProcessListener;
import ts.nodejs.NodejsProcess;
import ts.nodejs.NodejsProcessManager;

public class AbstractCmd<T extends IOptions> {

	private final File binFile;
	private final File nodejsFile;
	private final String binFileType;

	public AbstractCmd(File binFile, File nodejsFile, String binFileType) {
		this.binFile = binFile;
		this.nodejsFile = nodejsFile;
		this.binFileType = binFileType;
	}

	public List<String> createCommands(T options, List<String> filenames) {
		List<String> cmds = NodejsProcess.createNodeCommands(nodejsFile, binFile);
		fillOptions(options, filenames, cmds);
		return cmds;
	}

	public INodejsProcess execute(File baseDir, final T options, final List<String> filenames,
			INodejsProcessListener listener) throws TypeScriptException {
		INodejsProcess process = NodejsProcessManager.getInstance().create(baseDir, binFile, nodejsFile,
				new INodejsLaunchConfiguration() {

					@Override
					public List<String> createNodeArgs() {
						List<String> args = new ArrayList<String>();
						fillOptions(options, filenames, args);
						return args;
					}
					
					@Override
					public Map<String, String> createNodeEnvironmentVariables() {
						return null;
					}
				}, binFileType);

		if (listener != null) {
			process.addProcessListener(listener);
		}
		process.start();
		try {
			process.join();
		} catch (InterruptedException e) {
			throw new TypeScriptException(e);
		}
		return process;
	}

	private void fillOptions(T options, List<String> filenames, List<String> args) {
		if (filenames != null) {
			args.addAll(filenames);
		}
		if (options != null) {
			options.fillOptions(args);
		}
	}
}
