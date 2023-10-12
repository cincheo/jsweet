package ts.repository;

import java.io.File;

/**
 * Manager for keeping track of the available TypeScript repositories. Each
 * repository includes a TypeScript installation and additional software, such
 * as TSLint.
 * 
 * Each repository is automatically assigned a name according to the included
 * TypeScript version. No two repositories may share the same name.
 * 
 * One repository may be marked as the default (see
 * {@link #getDefaultRepository()}).
 * 
 */
public interface ITypeScriptRepositoryManager {

	/**
	 * Creates and adds a new repository. The new repository is also set as the
	 * default.
	 * 
	 * @param baseDir
	 *            base directory of the new repository.
	 * @return the created repository.
	 */
	ITypeScriptRepository createDefaultRepository(File baseDir) throws TypeScriptRepositoryException;

	/**
	 * Creates and adds a new repository.
	 * 
	 * @param baseDir
	 *            base directory of the new repository.
	 * @return the created repository.
	 */
	ITypeScriptRepository createRepository(File baseDir) throws TypeScriptRepositoryException;

	/**
	 * Removes a repository. If not present, nothing happens.
	 * 
	 * @param name
	 *            name of the repository to remove.
	 * @return the removed repository.
	 */
	ITypeScriptRepository removeRepository(String name);

	/**
	 * Gets the current default repository.
	 * 
	 * @return a repository or {@code null} if there is no default.
	 */
	ITypeScriptRepository getDefaultRepository();

	/**
	 * Gets a managed repository by name.
	 * 
	 * @param name
	 *            name of the repository to retrieve.
	 * @return a repository or {@code null} if there is no repository with the
	 *         requested name.
	 */
	ITypeScriptRepository getRepository(String name);

	/**
	 * Gets all registered repositories.
	 * 
	 * @return array of repositories.
	 */
	ITypeScriptRepository[] getRepositories();

}
