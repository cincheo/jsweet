package ts.repository;

import java.io.File;

/**
 * TypeScript repository which hosts typescript bundle:
 * 
 * <ul>
 * <li>node_modules/typescript/bin/tsc and node_modules/typescript/bin/tsserver
 * </li>
 * <li>Or bin/tsc and bin/tsserver</li>
 * </ul>
 *
 */
public interface ITypeScriptRepository {

	/**
	 * Returns the TypeScript repository name.
	 * 
	 * @return the TypeScript repository name.
	 */
	String getName();

	/**
	 * Returns the base directory of the TypeScript repository.
	 * 
	 * @return the base directory of the TypeScript repository.
	 */
	File getBaseDir();

	/**
	 * Update the base directory of the TypeScript repository.
	 * 
	 * @param baseDir
	 */
	void setBaseDir(File baseDir);

	/**
	 * The TypeScript base directory.
	 * 
	 * @return the TypeScript base directory.
	 */
	File getTypesScriptDir();

	/**
	 * Returns the TypeScript version and null otherwise.
	 * 
	 * @return the TypeScript version and null otherwise.
	 */
	String getTypesScriptVersion();

	/**
	 * Returns the tsc file.
	 * 
	 * @return the tsc file.
	 */
	File getTscFile();

	/**
	 * Returns the tslint version and null otherwise.
	 * 
	 * @return the tslint version and null otherwise.
	 */
	String getTslintVersion();

	/**
	 * Returns the tslint file.
	 * 
	 * @return the tslint file.
	 */
	File getTslintFile();

	/**
	 * Returns the tslint repository name.
	 * 
	 * @return the tslint repository name.
	 */
	String getTslintName();

	File getTsserverPluginsFile();

	String getTslintLanguageServiceName();

}
