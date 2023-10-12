/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.resources.jsonconfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ts.cmd.tsc.CompilerOptions;
import ts.utils.BooleanUtils;
import ts.utils.FileUtils;
import ts.utils.StringUtils;

/**
 * Pojo for tsconfig.json
 * 
 * @see http://www.typescriptlang.org/docs/handbook/tsconfig-json.html
 *
 */
public class TsconfigJson {

	// See
	// https://github.com/SchemaStore/schemastore/blob/master/src/schemas/json/tsconfig.json

	private static final String DEFAULT_TARGET = "es3";
	private static final String[] AVAILABLE_TARGETS = new String[] { "es3", "es5", "es6", "es2015", "es2016", "es2017",
			"esnext" };
	private static final String[] AVAILABLE_MODULES = new String[] { "none", "commonjs", "amd", "umd", "system", "es6",
			"es2015" };
	private static final String DEFAULT_MODULE_RESOLUTION = "classic";
	private static final String[] AVAILABLE_MODULE_RESOLUTIONS = new String[] { "node", "classic" };

	private CompilerOptions compilerOptions;

	private Boolean compileOnSave;

	private Boolean buildOnSave;

	private List<String> files;

	private List<String> exclude;

	private List<String> defaultExclude;

	public TsconfigJson() {
	}

	public void setCompilerOptions(CompilerOptions compilerOptions) {
		this.compilerOptions = compilerOptions;
	}

	public CompilerOptions getCompilerOptions() {
		return compilerOptions;
	}

	public boolean isCompileOnSave() {
		return BooleanUtils.toBoolean(compileOnSave);
	}

	public void setCompileOnSave(Boolean compileOnSave) {
		this.compileOnSave = compileOnSave;
	}

	/**
	 * Returns true if build must be done on save and false otherwise. This
	 * property doesn't belong to the standard specification of tsconfig.json,
	 * it comes from the atom-typescript.
	 * 
	 * Build means compile all files. Useful if for some reason you are using
	 * --out. Default is false. Note that build is a slow process, therefore we
	 * recommend leaving it off. But in case this is the way you want to go its
	 * there for your convenience.
	 * 
	 * @see https://github.com/TypeStrong/atom-typescript/blob/master/docs/tsconfig.md#buildonsave
	 * @return true if build must ne done on save and false otherwise.
	 */
	public boolean isBuildOnSave() {
		return BooleanUtils.toBoolean(buildOnSave);
	}

	/**
	 * Set to true if build must be done on save and false otherwise. This
	 * property doesn't belong to the standard specification of tsconfig.json,
	 * it comes from the atom-typescript.
	 * 
	 * Build means compile all files. Useful if for some reason you are using
	 * --out. Default is false. Note that build is a slow process, therefore we
	 * recommend leaving it off. But in case this is the way you want to go its
	 * there for your convenience.
	 * 
	 * @see https://github.com/TypeStrong/atom-typescript/blob/master/docs/tsconfig.md#buildonsave
	 * 
	 * @param buildOnSave
	 */
	public void setBuildOnSave(boolean buildOnSave) {
		this.buildOnSave = buildOnSave;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public boolean hasFiles() {
		return files != null;
	}

	public List<String> getExclude() {
		return exclude;
	}

	public void setExclude(List<String> exclude) {
		this.exclude = exclude;
	}

	public boolean hasExclude() {
		return exclude != null;
	}

	/**
	 * Returns true if the "compilerOptions" defines "out" or "outFile" and
	 * false otherwise.
	 * 
	 * @return true if the "compilerOptions" defines "out" or "outFile" and
	 *         false otherwise.
	 */
	public boolean hasOutFile() {
		CompilerOptions options = getCompilerOptions();
		if (options == null) {
			return false;
		}
		return !StringUtils.isEmpty(options.getOutFile()) || !StringUtils.isEmpty(options.getOut());
	}

	/**
	 * Returns true if the "compilerOptions" defines "paths" and false
	 * otherwise.
	 * 
	 * @return true if the "compilerOptions" defines "paths" and false
	 *         otherwise.
	 */
	public boolean hasPaths() {
		CompilerOptions options = getCompilerOptions();
		if (options == null) {
			return false;
		}
		return !options.getPathsKeys().isEmpty();
	}

	/**
	 * Returns true if the "compilerOptions" defines "rootDirs" and false
	 * otherwise.
	 * 
	 * @return true if the "compilerOptions" defines "rootDirs" and false
	 *         otherwise.
	 */
	public boolean hasRootDirs() {
		CompilerOptions options = getCompilerOptions();
		if (options == null) {
			return false;
		}
		return !options.getRootDirs().isEmpty();
	}

	/**
	 * Returns the defined "exclude" list from the tsconfig.json other exclude
	 * by default "node_modules" and "bower_components".
	 * 
	 * @return the defined "exclude" list from the tsconfig.json other exclude
	 *         by default "node_modules" and "bower_components".
	 */
	protected List<String> getDefaultOrDefinedExclude() {
		if (exclude != null) {
			return exclude;
		}
		if (defaultExclude != null) {
			return defaultExclude;
		}
		// by default exclude node_modules, bower_components and any specificied
		// output directory (see this rule used in the tsc.js)
		this.defaultExclude = new ArrayList<String>(Arrays.asList(FileUtils.NODE_MODULES, FileUtils.BOWER_COMPONENTS));
		CompilerOptions options = getCompilerOptions();
		if (options != null && !StringUtils.isEmpty(options.getOutDir())) {
			defaultExclude.add(options.getOutDir());
		}
		return defaultExclude;
	}

	/**
	 * Load tsconfig.json instance from the given reader.
	 * 
	 * @param reader
	 * @return tsconfig.json instance from the given reader.
	 */
	public static TsconfigJson load(Reader reader) {
		return load(reader, TsconfigJson.class);
	}

	public static <T extends TsconfigJson> T load(Reader json, Class<T> classOfT) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		T o = gson.fromJson(json, classOfT);
		if (o == null) {
			throw new JsonSyntaxException("JSON Syntax error");
		}
		return o;
	}

	public static <T extends TsconfigJson> T load(InputStream in, Class<T> classOfT) {
		Reader isr = null;
		try {
			isr = new InputStreamReader(in);
			return load(isr, classOfT);
		} finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Load tsconfig.json instance from the given input stream.
	 * 
	 * @param in
	 * @return tsconfig.json instance from the given input stream
	 */
	public static TsconfigJson load(InputStream in) {
		return load(in, TsconfigJson.class);
	}

	/**
	 * Returns the available targets.
	 * 
	 * @return the available targets.
	 */
	public static String[] getAvailableTargets() {
		return AVAILABLE_TARGETS;
	}

	/**
	 * Returns the default target.
	 * 
	 * @return the default target.
	 */
	public static String getDefaultTarget() {
		return DEFAULT_TARGET;
	}

	/**
	 * Returns the available modules.
	 * 
	 * @return the available modules.
	 */
	public static String[] getAvailableModules() {
		return AVAILABLE_MODULES;
	}

	/**
	 * Returns the available module resolutions.
	 * 
	 * @return the available module resolutions.
	 */
	public static String[] getAvailableModuleResolutions() {
		return AVAILABLE_MODULE_RESOLUTIONS;
	}

	/**
	 * Returns the default module resolution.
	 * 
	 * @return the default module resolution.
	 */
	public static String getDefaultModuleResolution() {
		return DEFAULT_MODULE_RESOLUTION;
	}

}
