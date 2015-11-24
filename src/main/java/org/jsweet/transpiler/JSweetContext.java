/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.transpiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.util.DirectedGraph;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

/**
 * The transpiler context, which is an extension of the Java compiler context.
 * 
 * @author Renaud Pawlak
 */
public class JSweetContext extends Context {
	/**
	 * A cache of method overloads.
	 * 
	 * @see OverloadScanner
	 * @see OverloadScanner.Overload
	 */
	public Map<ClassSymbol, Map<String, Overload>> overloads = new HashMap<>();

	/**
	 * An overload is a container of methods have the same name but different
	 * signatures.
	 * 
	 * @param clazz
	 *            the class to look into
	 * @param methodName
	 *            the method name
	 * @return an overload that contains 0 to many methods matching the given
	 *         name
	 */
	public Overload getOverload(ClassSymbol clazz, String methodName) {
		Map<String, Overload> m = overloads.get(clazz);
		if (m == null) {
			return null;
		}
		return m.get(methodName);
	}

	/**
	 * The Java compiler symbol table for fast access.
	 */
	public Symtab symtab;

	/**
	 * The Java compiler names for fast access.
	 */
	public Names names;

	/**
	 * The Java compiler types for fast access.
	 */
	public Types types;

	/**
	 * A flag to tell if the transpiler is in module mode or not.
	 */
	public boolean useModules = false;

	/**
	 * The source files that are being transpiled.
	 */
	public SourceFile[] sourceFiles;

	private List<String> usedModules = new ArrayList<>();

	/**
	 * Register a module that is used by the transpiled program.
	 * 
	 * @param moduleName
	 *            the module being used
	 */
	public void registerUsedModule(String moduleName) {
		if (!usedModules.contains(moduleName)) {
			usedModules.add(moduleName);
		}
	}

	/**
	 * The list of modules used by the transpiled program.
	 */
	public List<String> getUsedModules() {
		return usedModules;
	}

	private Map<PackageSymbol, Set<String>> importedNamesInPackages = new HashMap<>();

	/**
	 * Register a name that is imported by the given package of the transpiled
	 * program.
	 * 
	 * @param package
	 *            the package that is importing the name
	 * @param name
	 *            the name being imported
	 */
	public void registerImportedName(PackageSymbol packageSymbol, String name) {
		Set<String> importedNames = importedNamesInPackages.get(packageSymbol);
		if (importedNames == null) {
			importedNames = new HashSet<>();
			importedNamesInPackages.put(packageSymbol, importedNames);
		}
		if (!importedNames.contains(name)) {
			importedNames.add(name);
		}
	}

	/**
	 * The list of names imported by the given package of the transpiled
	 * program.
	 */
	public Set<String> getImportedNames(PackageSymbol packageSymbol) {
		Set<String> importedNames = importedNamesInPackages.get(packageSymbol);
		if (importedNames == null) {
			importedNames = new HashSet<>();
			importedNamesInPackages.put(packageSymbol, importedNames);
		}
		return importedNames;
	}

	/**
	 * Clears the names imported by the given package.
	 */
	public void clearImportedNames(PackageSymbol packageSymbol) {
		Set<String> importedNames = new HashSet<>();
		importedNamesInPackages.put(packageSymbol, importedNames);
	}

	/**
	 * Source files containing a main method.
	 */
	public List<File> entryFiles = new ArrayList<>();

	/**
	 * A graph containing the module dependencies when using modules (empty
	 * otherwise).
	 */
	public DirectedGraph<PackageSymbol> packageDependencies = new DirectedGraph<>();

	/**
	 * Stores the root package namee (i.e. packages contained in the default
	 * package or in a package annotated with the {@link jsweet.lang.Root} annotation).
	 */
	public Set<String> topLevelPackageNames = new HashSet<>();

	/**
	 * Globally imported name (in the global namespace).
	 */
	public Set<String> globalImports = new HashSet<>();
	
}