package ts.resources;

import java.io.IOException;

import ts.internal.resources.DefaultTypeScriptResourcesManager;

public class ConfigurableTypeScriptResourcesManager {

	private static final ConfigurableTypeScriptResourcesManager INSTANCE = new ConfigurableTypeScriptResourcesManager();

	private ITypeScriptResourcesManagerDelegate typeScriptResourcesManagerDelegate = new DefaultTypeScriptResourcesManager();

	public static synchronized ConfigurableTypeScriptResourcesManager getInstance() {
		return INSTANCE;
	}

	public ITypeScriptProject getTypeScriptProject(Object project, boolean force) throws IOException {
		return typeScriptResourcesManagerDelegate.getTypeScriptProject(project, force);
	}

	public void setTypeScriptResourcesManagerDelegate(
			ITypeScriptResourcesManagerDelegate typeScriptResourcesManagerDelegate) {
		this.typeScriptResourcesManagerDelegate = typeScriptResourcesManagerDelegate;
	}

	public boolean isTSFile(Object fileObject) {
		return typeScriptResourcesManagerDelegate.isTsFile(fileObject);
	}

	public boolean isJsFile(Object fileObject) {
		return typeScriptResourcesManagerDelegate.isJsFile(fileObject);
	}
}
