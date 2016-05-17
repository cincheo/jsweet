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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.util.DirectedGraph;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCWildcard;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

/**
 * The transpiler context, which is an extension of the Java compiler context.
 * 
 * @author Renaud Pawlak
 */
public class JSweetContext extends Context {

	/**
	 * Creates a new JSweet transpilation context.
	 * 
	 * @param options
	 *            the JSweet transpilation options
	 */
	public JSweetContext(JSweetOptions options) {
		this.options = options;
	}

	/**
	 * JSweet transpilation options.
	 */
	public final JSweetOptions options;

	/**
	 * A cache of method overloads.
	 * 
	 * @see OverloadScanner
	 * @see OverloadScanner.Overload
	 */
	private Map<ClassSymbol, Map<String, Overload>> overloads = new HashMap<>();

	/**
	 * A cache of static method overloads.
	 * 
	 * @see OverloadScanner
	 * @see OverloadScanner.Overload
	 */
	private Map<ClassSymbol, Map<String, Overload>> staticOverloads = new HashMap<>();

	/**
	 * Returns all the overloads in this context.
	 */
	public Set<Overload> getAllOverloads() {
		Set<Overload> result = new HashSet<>();
		overloads.values().forEach(m -> result.addAll(m.values()));
		staticOverloads.values().forEach(m -> result.addAll(m.values()));
		return result;
	}

	/**
	 * Gets or create an overload instance for the given class and method.
	 */
	public Overload getOrCreateOverload(ClassSymbol clazz, MethodSymbol method) {
		Map<ClassSymbol, Map<String, Overload>> actualOverloads = method.isStatic() ? staticOverloads : overloads;
		Map<String, Overload> m = actualOverloads.get(clazz);
		if (m == null) {
			m = new HashMap<>();
			actualOverloads.put(clazz, m);
		}
		String name = method.name.toString();
		Overload overload = m.get(name);
		if (overload == null) {
			overload = new Overload();
			overload.methodName = name;
			m.put(name, overload);
		}
		return overload;
	}

	/**
	 * Gets an overload instance for the given class and method.
	 */
	public Overload getOverload(ClassSymbol clazz, MethodSymbol method) {
		Map<ClassSymbol, Map<String, Overload>> actualOverloads = method.isStatic() ? staticOverloads : overloads;
		Map<String, Overload> m = actualOverloads.get(clazz);
		if (m == null) {
			return null;
		}
		Overload overload = m.get(method.name.toString());
		if (overload == null) {
			return null;
		}
		return overload;
	}

	/**
	 * Tells if that method is part of an invalid overload in its declaring
	 * class.
	 */
	public boolean isInvalidOverload(MethodSymbol method) {
		Overload overload = getOverload((ClassSymbol) method.getEnclosingElement(), method);
		return overload != null && !overload.isValid;
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
	 * A flag to make sure that all type names are expanded (fully qualified).
	 */
	public boolean expandTypeNames = false;

	/**
	 * Holds all the static fields that are lazy intitialized.
	 */
	public Set<VarSymbol> lazyInitializedStatics = new HashSet<>();

	private Map<ClassSymbol, Integer> staticInitializerCounts = new HashMap<>();

	/**
	 * Increments the count of static initialization blocks for the given class.
	 */
	public void countStaticInitializer(ClassSymbol clazz) {
		staticInitializerCounts.put(clazz, (staticInitializerCounts.containsKey(clazz) ? staticInitializerCounts.get(clazz) : 0) + 1);
	}

	/**
	 * Gets the static initializer count for the given class.
	 */
	public int getStaticInitializerCount(ClassSymbol clazz) {
		Integer count = null;
		return (count = staticInitializerCounts.get(clazz)) == null ? 0 : count;
	}

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
	private Map<PackageSymbol, Map<Symbol, String>> importedElementsInPackages = new HashMap<>();

	/**
	 * Register a name that is imported by the given package of the transpiled
	 * program.
	 * 
	 * <pre>
	 * import targetName = require("sourceName");
	 * </pre>
	 * 
	 * @param package
	 *            the package that is importing the name
	 * @param sourceElement
	 *            the source element if any (null if not applicable)
	 * @param targetName
	 *            the target name being imported
	 */
	public void registerImportedName(PackageSymbol packageSymbol, Symbol sourceElement, String targetName) {
		Set<String> importedNames = importedNamesInPackages.get(packageSymbol);
		if (importedNames == null) {
			importedNames = new HashSet<>();
			importedNamesInPackages.put(packageSymbol, importedNames);
		}
		if (!importedNames.contains(targetName)) {
			importedNames.add(targetName);
		}
		if (sourceElement != null) {
			Map<Symbol, String> importedElements = importedElementsInPackages.get(packageSymbol);
			if (importedElements == null) {
				importedElements = new HashMap<>();
				importedElementsInPackages.put(packageSymbol, importedElements);
			}
			if (!importedElements.containsKey(sourceElement)) {
				importedElements.put(sourceElement, targetName);
			}

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
	 * The list of package names imported by the given package of the transpiled
	 * program.
	 */
	public Map<Symbol, String> getImportedElements(PackageSymbol packageSymbol) {
		Map<Symbol, String> importedElements = importedElementsInPackages.get(packageSymbol);
		if (importedElements == null) {
			importedElements = new HashMap<>();
			importedElementsInPackages.put(packageSymbol, importedElements);
		}
		return importedElements;
	}

	/**
	 * Clears the names imported by the given package.
	 */
	public void clearImportedNames(PackageSymbol packageSymbol) {
		Set<String> importedNames = new HashSet<>();
		importedNamesInPackages.put(packageSymbol, importedNames);
		Map<Symbol, String> importedPackagesForNames = new HashMap<>();
		importedElementsInPackages.put(packageSymbol, importedPackagesForNames);
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
	 * Stores the root package names (i.e. packages contained in the default
	 * package or in a package annotated with the {@link jsweet.lang.Root}
	 * annotation).
	 */
	public Set<String> topLevelPackageNames = new HashSet<>();

	/**
	 * Store root packages (i.e. packages contained in the default package or in
	 * a package annotated with the {@link jsweet.lang.Root} annotation,
	 * including null, i.e. default package).
	 */
	public HashSet<PackageSymbol> rootPackages = new HashSet<>();

	/**
	 * A flag to keep track of wether a multiple root packages problem was
	 * already reported (shall report only once).
	 */
	public boolean reportedMultipleRootPackages = false;

	/**
	 * Globally imported name (in the global namespace).
	 */
	public Set<String> globalImports = new HashSet<>();

	/**
	 * A flag that indicates if the transpilation is in "strict" mode, which
	 * means that the <code>jsweet-core-strict</code> jar is in the classpath.
	 */
	public boolean strictMode = false;

	private List<String> footerStatements = new LinkedList<String>();

	/**
	 * Gets and clears the footer statements.
	 */
	public String poolFooterStatements() {
		StringBuilder sb = new StringBuilder();
		for (String footerStatement : footerStatements) {
			sb.append("\n");
			sb.append(footerStatement);
			sb.append("\n");
		}
		footerStatements.clear();
		return sb.toString();
	}

	/**
	 * Adds a footer statement.
	 */
	public void addFooterStatement(String footerStatement) {
		footerStatements.add(footerStatement);
	}

	private Map<TypeSymbol, Set<Entry<JCClassDecl, JCMethodDecl>>> defaultMethods = new HashMap<>();
	private Map<JCMethodDecl, JCCompilationUnit> defaultMethodsCompilationUnits = new HashMap<>();

	/**
	 * Gets the default methods declared in the given type.
	 */
	public Set<Entry<JCClassDecl, JCMethodDecl>> getDefaultMethods(TypeSymbol type) {
		return defaultMethods.get(type);
	}

	/**
	 * Stores a default method AST for the given type.
	 */
	public void addDefaultMethod(JCCompilationUnit compilationUnit, JCClassDecl type, JCMethodDecl defaultMethod) {
		Set<Entry<JCClassDecl, JCMethodDecl>> methods = defaultMethods.get(type.sym);
		if (methods == null) {
			methods = new HashSet<>();
			defaultMethods.put(type.sym, methods);
		}
		methods.add(new AbstractMap.SimpleEntry<>(type, defaultMethod));
		defaultMethodsCompilationUnits.put(defaultMethod, compilationUnit);
	}

	/**
	 * Gets the compilation unit the given default method belongs to.
	 */
	public JCCompilationUnit getDefaultMethodCompilationUnit(JCMethodDecl defaultMethod) {
		return defaultMethodsCompilationUnits.get(defaultMethod);
	}

	private Map<VarSymbol, String> fieldNameMapping = new HashMap<>();

	/**
	 * Adds a name mapping to a field (rename it to avoid name clashes).
	 */
	public void addFieldNameMapping(VarSymbol field, String name) {
		fieldNameMapping.put(field, name);
	}

	/**
	 * Gets a field name mapping if any (null otherwise).
	 */
	public String getFieldNameMapping(Symbol field) {
		return fieldNameMapping.get(field);
	}

	/**
	 * Tells JSweet to ignore wildcard bounds. For instance if ignored:
	 * 
	 * <pre>
	 * void f(C<? extends String> c)
	 * </pre>
	 * 
	 * will transpile to:
	 * 
	 * <pre>
	 * f(c: C<any>)
	 * </pre>
	 * 
	 * otherwise:
	 * 
	 * <pre>
	 * void f(C<? extends String> c)
	 * </pre>
	 * 
	 * will transpile to:
	 * 
	 * <pre>
	 * f<__T1 extends string>(c: C<__T1>)
	 * </pre>
	 * 
	 */
	public boolean ignoreWildcardBounds = true;

	private Map<JCWildcard, String> wildcardNames = new HashMap<>();

	private Map<Symbol, List<JCWildcard>> wildcards = new HashMap<>();

	/**
	 * Registers a wilcard for a given container (type parameterized element).
	 */
	public void registerWildcard(Symbol holder, JCWildcard wildcard) {
		if (wildcard.getBound() == null) {
			return;
		}
		List<JCWildcard> l = wildcards.get(holder);
		if (l == null) {
			l = new ArrayList<>();
			wildcards.put(holder, l);
		}
		l.add(wildcard);
		wildcardNames.put(wildcard, "__T" + l.size());
	}

	/**
	 * Gets the wildcard name if any.
	 */
	public String getWildcardName(JCWildcard wildcard) {
		return wildcardNames.get(wildcard);
	}

	/**
	 * Gets the registered wildcards for the given type parameter holder.
	 */
	public List<JCWildcard> getWildcards(Symbol holder) {
		return wildcards.get(holder);
	}

}