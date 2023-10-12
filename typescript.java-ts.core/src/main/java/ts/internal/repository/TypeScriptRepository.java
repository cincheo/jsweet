package ts.internal.repository;

import java.io.File;

import ts.repository.ITypeScriptRepository;
import ts.repository.TypeScriptRepositoryException;
import ts.repository.TypeScriptRepositoryManager;

/**
 *
 *
 */
public class TypeScriptRepository implements ITypeScriptRepository {

	private final TypeScriptRepositoryManager manager;
	private File baseDir;
	private String name;
	private File typesScriptDir;
	private File tscFile;
	private File tslintFile;
	private String tslintName;
	private String typesScriptVersion;
	private String tslintVersion;
	private File tsserverPluginsFile;
	private String tslintLanguageServiceName;

	public TypeScriptRepository(File baseDir) throws TypeScriptRepositoryException {
		this(baseDir, null);
	}

	public TypeScriptRepository(File baseDir, TypeScriptRepositoryManager manager)
			throws TypeScriptRepositoryException {
		this.manager = manager;
		this.baseDir = baseDir;
		updateBaseDir(baseDir);
	}

	private void updateBaseDir(File baseDir) throws TypeScriptRepositoryException {
		this.typesScriptDir = new File(baseDir, "node_modules/typescript");
		TypeScriptRepositoryManager.validateTypeScriptDir(typesScriptDir);
		// tsc file
		this.tscFile = TypeScriptRepositoryManager.getTscFile(typesScriptDir);
		this.typesScriptVersion = TypeScriptRepositoryManager.getPackageJsonVersion(typesScriptDir);
		this.setName(generateName("TypeScript", typesScriptVersion));
		// tslint file
		File tslintBaseDir = new File(baseDir, "node_modules/tslint");
		if (tslintBaseDir.exists()) {
			this.tslintFile = TypeScriptRepositoryManager.getTslintFile(tslintBaseDir);
			this.tslintVersion = TypeScriptRepositoryManager.getPackageJsonVersion(tslintBaseDir);
			this.tslintName = generateName("tslint", tslintVersion);
		}
		// tslint-language-service file
		File tslintLanguageServiceBaseDir = new File(baseDir, "node_modules/tslint-language-service");
		if (tslintLanguageServiceBaseDir.exists()) {
			String tslintLanguageServiceVersion = TypeScriptRepositoryManager.getPackageJsonVersion(tslintLanguageServiceBaseDir);
			this.tslintLanguageServiceName= generateName("tslint-language-service", tslintLanguageServiceVersion);
		}
		// tsserver-plugins
		this.tsserverPluginsFile = new File(baseDir, "tsserver-plugins/bin/tsserver-plugins");
	}

	private String generateName(String prefix, String version) {
		StringBuilder name = new StringBuilder(prefix);
		name.append(" (");
		if (version != null) {
			name.append(version);
		}
		name.append(")");
		return name.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) throws TypeScriptRepositoryException {
		ITypeScriptRepository repository = manager != null ? manager.getRepository(name) : null;
		if (repository == null || repository.equals(this)) {
			this.name = name;
		} else {
			throw new TypeScriptRepositoryException("It already exists a TypeScript repository with the name " + name);
		}
	}

	@Override
	public File getBaseDir() {
		return baseDir;
	}

	@Override
	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public File getTypesScriptDir() {
		return typesScriptDir;
	}

	@Override
	public String getTypesScriptVersion() {
		return typesScriptVersion;
	}

	@Override
	public File getTscFile() {
		return tscFile;
	}

	@Override
	public String getTslintVersion() {
		return tslintVersion;
	}

	@Override
	public File getTslintFile() {
		return tslintFile;
	}

	@Override
	public String getTslintName() {
		return tslintName;
	}

	@Override
	public File getTsserverPluginsFile() {
		return tsserverPluginsFile;
	}
	
	@Override
	public String getTslintLanguageServiceName() {
		return tslintLanguageServiceName;
	}

}
