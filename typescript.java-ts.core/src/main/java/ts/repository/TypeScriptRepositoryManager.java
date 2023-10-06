/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Lorenzo Dalla Vecchia <lorenzo.dallavecchia@webratio.com> - protected API for setting default
 */
package ts.repository;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import ts.internal.repository.TypeScriptRepository;
import ts.utils.FileUtils;
import ts.utils.IOUtils;
import ts.utils.VersionHelper;

/**
 * TypeScript repository manager implementation.
 *
 */
public class TypeScriptRepositoryManager implements ITypeScriptRepositoryManager {

	private final Map<String, ITypeScriptRepository> repositories;
	private ITypeScriptRepository[] sortedRepositories;
	private ITypeScriptRepository defaultRepository;

	private static final Comparator<ITypeScriptRepository> REPOSITORY_COMPARATOR = new Comparator<ITypeScriptRepository>() {

		@Override
		public int compare(ITypeScriptRepository r1, ITypeScriptRepository r2) {
			return VersionHelper.versionCompare(r2.getTypesScriptVersion(), r1.getTypesScriptVersion());
		}
	};

	public TypeScriptRepositoryManager() {
		this.repositories = new HashMap<String, ITypeScriptRepository>();
	}

	@Override
	public ITypeScriptRepository createDefaultRepository(File baseDir) throws TypeScriptRepositoryException {
		return this.defaultRepository = createRepository(baseDir);
	}

	protected final void setDefaultRepository(ITypeScriptRepository repository) {
		this.defaultRepository = repository;
	}

	@Override
	public ITypeScriptRepository createRepository(File baseDir) throws TypeScriptRepositoryException {
		synchronized (repositories) {
			ITypeScriptRepository repository = new TypeScriptRepository(baseDir, this);
			repositories.put(repository.getName(), repository);
			reset();
			return repository;
		}
	}

	public void reset() {
		sortedRepositories = null;
	}

	@Override
	public ITypeScriptRepository removeRepository(String name) {
		synchronized (repositories) {
			reset();
			return repositories.remove(name);
		}
	}

	@Override
	public ITypeScriptRepository getDefaultRepository() {
		return defaultRepository;
	}

	@Override
	public ITypeScriptRepository getRepository(String name) {
		return repositories.get(name);
	}

	@Override
	public ITypeScriptRepository[] getRepositories() {
		if (sortedRepositories == null) {
			List<ITypeScriptRepository> reps = new ArrayList<ITypeScriptRepository>(repositories.values());
			Collections.sort(reps, REPOSITORY_COMPARATOR);
			sortedRepositories = reps.toArray(new ITypeScriptRepository[reps.size()]);
		}
		return sortedRepositories;
	}

	public static File getTsserverFile(File typesScriptDir) {
		if (typesScriptDir.getName().equals("tsserver")) {
			return typesScriptDir;
		}
		return new File(typesScriptDir, "bin/tsserver");
	}

	public static File getTscFile(File typesScriptDir) {
		if (typesScriptDir.getName().equals("tsc")) {
			return typesScriptDir;
		}
		return new File(typesScriptDir, "bin/tsc");
	}

	public static File getTslintFile(File tslintScriptDir) {
		return new File(tslintScriptDir, "bin/tslint");
	}

	public static String getPackageJsonVersion(File baseDir) {
		File packageJsonFile = new File(baseDir, "package.json");
		try {
			JsonObject json = Json.parse(IOUtils.toString(new FileInputStream(packageJsonFile))).asObject();
			return json.getString("version", null);
		} catch (Exception e) {
			return null;
		}
	}

	public static void validateTypeScriptDir(File typesScriptDir) throws TypeScriptRepositoryException {
		File tsserverFile = TypeScriptRepositoryManager.getTsserverFile(typesScriptDir);
		if (!tsserverFile.exists()) {
			throw new TypeScriptRepositoryException(FileUtils.getPath(typesScriptDir)
					+ " is not a valid TypeScript repository. Check the directory contains bin/tsserver.");
		}
	}

	public static File getTsserverPluginsFile(File typesScriptDir) {
		if (typesScriptDir.getName().equals("tsserver-plugins")) {
			return typesScriptDir;
		}
		return new File(typesScriptDir, "bin/tsserver-plugins");
	}
}
