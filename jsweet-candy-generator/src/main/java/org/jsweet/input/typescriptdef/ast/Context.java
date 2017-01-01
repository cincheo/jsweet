/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.input.typescriptdef.ast;

import static org.jsweet.JSweetDefTranslatorConfig.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.util.DeclarationFinder;
import org.jsweet.util.DirectedGraph;

/**
 * The TypeScript definitions to Java translation context.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class Context {

	private final static Logger logger = Logger.getLogger(Context.class);

	private List<File> dependenciesDefinitions;
	private List<File> librariesDefinitions;

	// contructor type -> target type
	public Map<TypeDeclaration, TypeDeclaration> mergedContructors = new HashMap<TypeDeclaration, TypeDeclaration>();

	public Context(List<File> libraries, List<File> dependencies, boolean fetchJavadoc) {
		this.librariesDefinitions = libraries;
		this.dependenciesDefinitions = dependencies;
		this.fetchJavadoc = fetchJavadoc;
		initCoreTypes();
	}

	public boolean verbose;

	public File cacheDir = new File("cache");

	public List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();

	public DirectedGraph<String> dependencyGraph;

	public List<String> libModules = new ArrayList<String>();

	public Map<String, CompilationUnit> libModulesCompilationUnits = new HashMap<>();

	/**
	 * Lib module names -> Mixin declaration.
	 */
	public Map<String, List<TypeDeclaration>> mixins = new HashMap<>();

	public Map<String, String> moduleDocumentations = new HashMap<>();

	private Map<String, TypeDeclaration> types = new HashMap<String, TypeDeclaration>();

	private Set<String> clashingWithModulesTypes = new HashSet<String>();

	private Map<TypeDeclaration, String> typeNames = new HashMap<TypeDeclaration, String>();

	private Map<ModuleDeclaration, String> moduleNames = new HashMap<ModuleDeclaration, String>();

	public Map<DeclarationContainer, List<TypeDeclaration>> generatedObjectTypes = new HashMap<>();

	/**
	 * Holds the (fully qualified module names -> original name) that are
	 * external (can be imported with require).
	 */
	public Map<String, String> externalModules = new HashMap<>();

	public List<TypeDeclaration> arrayTypes = new ArrayList<TypeDeclaration>();

	public Map<FullFunctionDeclaration, FullFunctionDeclaration> overrides = new HashMap<FullFunctionDeclaration, FullFunctionDeclaration>();
	public Map<FullFunctionDeclaration, FullFunctionDeclaration> overridens = new HashMap<FullFunctionDeclaration, FullFunctionDeclaration>();
	public List<Set<FullFunctionDeclaration>> duplicates = new ArrayList<Set<FullFunctionDeclaration>>();

	private List<String> errors = new ArrayList<>();
	private List<String> warnings = new ArrayList<>();

	public final boolean fetchJavadoc;

	public void reportError(String errorMessage, Token token) {
		reportError(errorMessage, token, null);
	}

	public void reportError(String errorMessage, Token token, Throwable cause) {
		reportError(errorMessage + (token == null || token.getLocation() == null ? " (undefined position)"
				: " at " + token.getLocation()), cause);
	}

	private void reportError(String errorMessage, Throwable cause) {
		errors.add(errorMessage);
		if (cause == null) {
			logger.error(errorMessage);
		} else {
			logger.error(errorMessage, cause);
		}
	}

	public void reportWarning(String errorMessage) {
		warnings.add(errorMessage);
		logger.warn(errorMessage);
	}

	public int getErrorCount() {
		return errors.size();
	}

	public int getWarningCount() {
		return warnings.size();
	}

	public List<String> getErrors() {
		return errors;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void registerType(String name, TypeDeclaration declaration) {
		typeNames.put(declaration, name);
		// first type wins (duplicate declarations will merge to the first
		// one, see TypeMerger)
		if (!types.containsKey(name)) {
			types.put(name, declaration);
		}
	}

	public void unregisterType(TypeDeclaration declaration) {
		String typeName = getTypeName(declaration);
		if (declaration == types.get(typeName)) {
			types.remove(typeName);
		}
		typeNames.remove(declaration);
	}

	public List<CompilationUnit> getTranslatedCompilationUnits() {
		List<CompilationUnit> compUnits = new LinkedList<>();
		for (CompilationUnit candidate : compilationUnits) {
			if (!isDependency(candidate)) {
				compUnits.add(candidate);
			}
		}

		return compUnits;
	}

	public TypeDeclaration getTypeDeclaration(String name) {
		return types.get(name);
	}

	public boolean setTypeClashingWithModule(String typeName) {
		return clashingWithModulesTypes.add(typeName);
	}

	public boolean isTypeClashingWithModule(String typeName) {
		return clashingWithModulesTypes.contains(typeName);
	}

	public String getTypeName(TypeDeclaration typeDeclaration) {
		return typeNames.get(typeDeclaration);
	}

	public String getModuleName(ModuleDeclaration moduleDeclaration) {
		return moduleNames.get(moduleDeclaration);
	}

	public String getTypeModule(TypeDeclaration typeDeclaration) {
		String name = typeNames.get(typeDeclaration);
		if (name == null) {
			return null;
		}
		int i = name.lastIndexOf('.');
		return i > 0 ? name.substring(0, i) : null;
	}

	/**
	 * Checks that all modules and types are registered in the context
	 */
	public void checkConsistency() {
		logger.info("context consistency check");
		for (QualifiedDeclaration<ModuleDeclaration> m : findDeclarations(ModuleDeclaration.class, "*")) {
			if (getModuleName(m.getDeclaration()) == null) {
				reportError("unregistered module: " + m, (Token) null);
			}
		}
		for (QualifiedDeclaration<TypeDeclaration> t : findDeclarations(TypeDeclaration.class, "*")) {
			if (!t.getDeclaration().isAnonymous() && getTypeName(t.getDeclaration()) == null) {
				reportError("unregistered type: " + t, t.getDeclaration().getToken());
			}
		}
	}

	public void dump(Logger logger) {
		List<QualifiedDeclaration<ModuleDeclaration>> modules = findDeclarations(ModuleDeclaration.class, "*");
		logger.info(modules.size() + " module" + (modules.size() > 1 ? "s" : "") + " and " + types.size() + " type"
				+ (types.size() > 1 ? "s" : "") + " found.");
		logger.info("modules: ");
		for (QualifiedDeclaration<ModuleDeclaration> m : modules) {
			logger.info("   * " + getModuleName(m.getDeclaration()));
		}
		// logger.info("types: ");
		// for (TypeDeclaration t : findDeclarations(TypeDeclaration.class,
		// "*")) {
		// if (!t.isAnonymous()) {
		// logger.info(" * " + getTypeName(t));
		// }
		// }
		// logger.info("registered types: ");
		// for (TypeDeclaration t : types.values()) {
		// logger.info(" * " + getTypeName(t));
		// }
	}

	public void calculateArrayTypes() {
		for (TypeDeclaration t : types.values()) {
			if (t.getTypeParameters() == null || t.getTypeParameters().length != 1) {
				continue;
			}
			Declaration length = t.findDeclaration("length");
			if (length == null) {
				continue;
			}
			Declaration get = t.findDeclaration(JSweetDefTranslatorConfig.INDEXED_GET_FUCTION_NAME);
			if (get == null || !(get instanceof FunctionDeclaration)) {
				continue;
			}
			FunctionDeclaration getFunction = (FunctionDeclaration) get;
			if (getFunction.getParameters().length == 1
					&& "number".equals(getFunction.getParameters()[0].getType().getName())) {
				if (t.getTypeParameters()[0].getName() != null
						&& t.getTypeParameters()[0].getName().equals(getFunction.getType().getName())) {
					arrayTypes.add(t);
				}
			}
		}
	}

	protected void initCoreTypes() {
		if (JSweetDefTranslatorConfig.isJDKReplacementMode()) {
			registerType("java.util.function.Function", TypeDeclaration.createExternalTypeDeclaration("Function"));
			registerType("java.util.function.BiFunction", TypeDeclaration.createExternalTypeDeclaration("BiFunction"));
			registerType("java.util.function.TriFunction",
					TypeDeclaration.createExternalTypeDeclaration("TriFunction"));
			registerType("java.util.function.Supplier", TypeDeclaration.createExternalTypeDeclaration("Supplier"));
			registerType("java.util.function.Consumer", TypeDeclaration.createExternalTypeDeclaration("Consumer"));
			registerType("java.util.function.BiConsumer", TypeDeclaration.createExternalTypeDeclaration("BiConsumer"));
			registerType("java.util.function.TriConsumer",
					TypeDeclaration.createExternalTypeDeclaration("TriConsumer"));
		} else {
			registerType("java.lang.Object", TypeDeclaration.createExternalTypeDeclaration("Object"));
			registerType("java.lang.Boolean", TypeDeclaration.createExternalTypeDeclaration("Boolean"));
			registerType("java.lang.String", TypeDeclaration.createExternalTypeDeclaration("String"));
			registerType("java.util.function.Function", TypeDeclaration.createExternalTypeDeclaration("Function"));
			registerType("java.util.function.BiFunction", TypeDeclaration.createExternalTypeDeclaration("BiFunction"));
			registerType("jsweet.util.function.TriFunction",
					TypeDeclaration.createExternalTypeDeclaration("TriFunction"));
			registerType("java.util.function.Supplier", TypeDeclaration.createExternalTypeDeclaration("Supplier"));
			registerType("java.util.function.Consumer", TypeDeclaration.createExternalTypeDeclaration("Consumer"));
			registerType("java.util.function.BiConsumer", TypeDeclaration.createExternalTypeDeclaration("BiConsumer"));
			registerType("jsweet.util.function.TriConsumer",
					TypeDeclaration.createExternalTypeDeclaration("TriConsumer"));
		}
		registerType("java.lang.Double", TypeDeclaration.createExternalTypeDeclaration("Double"));
		registerType("java.lang.Runnable", TypeDeclaration.createExternalTypeDeclaration("Runnable"));
		registerType("java.lang.Void", TypeDeclaration.createExternalTypeDeclaration("Void"));
		registerType("double", TypeDeclaration.createExternalTypeDeclaration("double"));
		registerType("boolean", TypeDeclaration.createExternalTypeDeclaration("boolean"));
		registerType("void", TypeDeclaration.createExternalTypeDeclaration("void"));
		registerType("any", TypeDeclaration.createExternalTypeDeclaration("any"));
		registerType("string", TypeDeclaration.createExternalTypeDeclaration("string"));
		registerType("number", TypeDeclaration.createExternalTypeDeclaration("number"));
		registerType("symbol", TypeDeclaration.createExternalTypeDeclaration("symbol"));
		registerType(JSweetDefTranslatorConfig.UNION_CLASS_NAME,
				TypeDeclaration.createExternalTypeDeclaration("interface", "Union"));
		for (int i = 2; i <= 6; i++) {
			registerType(
					JSweetDefTranslatorConfig.TUPLE_CLASSES_PACKAGE + "."
							+ JSweetDefTranslatorConfig.TUPLE_CLASSES_PREFIX + i,
					TypeDeclaration.createExternalTypeDeclaration(JSweetDefTranslatorConfig.TUPLE_CLASSES_PREFIX + i));
		}
	}

	public void addDuplicate(FullFunctionDeclaration function1, FullFunctionDeclaration function2) {
		boolean added = false;
		for (Set<FullFunctionDeclaration> s : duplicates) {
			if (s.contains(function1) || s.contains(function2)) {
				s.add(function1);
				s.add(function2);
				added = true;
				break;
			}
		}
		if (!added) {
			Set<FullFunctionDeclaration> s = new HashSet<FullFunctionDeclaration>();
			s.add(function1);
			s.add(function2);
			duplicates.add(s);
		}
	}

	public void addOverride(FullFunctionDeclaration override, FullFunctionDeclaration overridden) {
		overrides.put(override, overridden);
		overridens.put(overridden, override);
	}

	public String getFullTypeNameNoErasure(TypeReference t) {
		if (t.isArray()) {
			return getFullTypeNameNoErasure(t.getComponentType()) + "[]";
		} else {
			Type type = t.getDeclaration();
			if (type instanceof TypeDeclaration) {
				StringBuilder sb = new StringBuilder();
				sb.append(((TypeDeclaration) type).getName());
				if (t.getTypeArguments() != null) {
					sb.append("<");
					for (int i = 0; i < t.getTypeArguments().length; i++) {
						sb.append(getFullTypeNameNoErasure(t.getTypeArguments()[i]));
						if (i < t.getTypeArguments().length) {
							sb.append(",");
						}
					}
					if (t.getTypeArguments().length > 0) {
						sb.deleteCharAt(sb.length() - 1);
					}
					sb.append(">");
				}
				return sb.toString();
			} else if (type instanceof TypeParameterDeclaration) {
				return type.getName();
			} else {
				return t.getWrappingTypeName();
			}
		}
	}

	public String getShortTypeNameNoErasure(TypeReference t) {
		if (t.isArray()) {
			return getShortTypeNameNoErasure(t.getComponentType()) + "[]";
		} else {
			Type type = t.getDeclaration();
			if (type instanceof TypeDeclaration) {
				StringBuilder sb = new StringBuilder();
				String s = ((TypeDeclaration) type).getName();
				// todo: wrap type (no primitive types)
				sb.append(s);
				if (t.getTypeArguments() != null) {
					sb.append("<");
					for (int i = 0; i < t.getTypeArguments().length; i++) {
						sb.append(getShortTypeNameNoErasure(t.getTypeArguments()[i]));
						if (i < t.getTypeArguments().length) {
							sb.append(",");
						}
					}
					if (t.getTypeArguments().length > 0) {
						sb.deleteCharAt(sb.length() - 1);
					}
					sb.append(">");
				}
				return sb.toString();
			} else if (type instanceof TypeParameterDeclaration) {
				return type.getName();
			} else {
				return t.getWrappingTypeName();
			}
		}
	}

	public String getShortTypeNameErased(TypeReference t) {
		if (t.isArray()) {
			return getShortTypeNameErased(t.getComponentType()) + "[]";
		} else {
			Type type = t.getDeclaration();
			if (type instanceof TypeDeclaration) {
				// todo: wrap type (no primitive types)
				return ((TypeDeclaration) type).getName();
			} else if (type instanceof TypeParameterDeclaration) {
				return type.getName();
			} else {
				return t.getWrappingTypeName();
			}
		}
	}

	public String getTypeNameErased(TypeReference t) {
		if (t.isArray()) {
			return getTypeNameErased(t.getComponentType()) + "[]";
		}
		if (t instanceof FunctionalTypeReference) {
			throw new RuntimeException("invalid functional type" + t);
		}
		if ("any".equals(t.getName())) {
			return "java.lang.Object";
		}
		Type type = t.getDeclaration();
		if (type instanceof TypeDeclaration) {
			return getTypeName((TypeDeclaration) type);
		} else if (type instanceof TypeParameterDeclaration) {
			TypeParameterDeclaration tpd = (TypeParameterDeclaration) type;
			if (tpd.getUpperBound() == null) {
				return "java.lang.Object";
			} else {
				return getTypeNameErased(tpd.getUpperBound());
			}
		} else {
			return t.getWrappingTypeName();
			// Token token = getCurrentToken();
			// System.err.println("ERROR: invalid type "+t.getName() + (token ==
			// null ? "" : " at " + token.getLocation()));
			// throw new RuntimeException("invalid type");
		}
	}

	public String getSignature(FunctionDeclaration function) {
		StringBuilder sb = new StringBuilder();
		sb.append(function.getName());
		sb.append("(");
		for (ParameterDeclaration p : function.getParameters()) {
			sb.append(getFullTypeNameNoErasure(p.getType()));
			sb.append(",");
		}
		if (function.getParameters().length > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(")");
		return sb.toString();
	}

	public List<File> getDependenciesDefinitions() {
		return dependenciesDefinitions;
	}

	public List<File> getLibrariesDefinitions() {
		return librariesDefinitions;
	}

	public List<File> getAllDefinitions() {
		List<File> allFiles = new LinkedList<>(getDependenciesDefinitions());
		allFiles.addAll(getLibrariesDefinitions());

		return allFiles;
	}

	public boolean isDependency(CompilationUnit compilationUnit) {
		if (compilationUnit == null) {
			return false;
		}
		String currentLibPath = compilationUnit.getFile().getAbsolutePath();
		for (File dependencyFile : getLibrariesDefinitions()) {
			if (currentLibPath.equals(dependencyFile.getAbsolutePath())) {
				return false;
			}
		}
		return true;
	}

	public void registerModule(String name, ModuleDeclaration moduleDeclaration) {
		moduleNames.put(moduleDeclaration, name);
	}

	/**
	 * Gets the module that contains the belonging Javascript library definition
	 * from a fully qualified name. If the given name does not belong to any
	 * library, returns null.
	 * 
	 * @param qualifiedName
	 *            ex: def.xxx
	 */
	public String getLibModule(String qualifiedName) {
		if (libModules.contains(qualifiedName)) {
			return qualifiedName;
		}

		if (qualifiedName.equals(LANG_PACKAGE) //
				|| qualifiedName.equals(UTIL_PACKAGE) //
				|| qualifiedName.equals(DOM_PACKAGE)) {
			return qualifiedName;
		}

		// TODO: we need a faster way
		for (String m : libModules) {
			if (qualifiedName.startsWith(m + ".")) {
				return m;
			}
		}
		return null;
	}

	public String getLibRelativePath(String qualifiedName) {
		if (JSweetDefTranslatorConfig.isLibPath(qualifiedName)) {
			String m = getLibModule(qualifiedName);
			if (m != null) {
				if (qualifiedName.length() > m.length()) {
					return qualifiedName.substring(m.length() + 1);
				}
			}
		}
		return qualifiedName;
	}

	/**
	 * Finds in the AST all the declarations for a given type and matching the
	 * given full name.
	 * 
	 * <p>
	 * A declaration expression supports the * wildcard to allow any path in the
	 * expression. Examples of declaration expressions:
	 * <ul>
	 * <li>"a.d.c": matches all declarations having this exact full name</li>
	 * <li>"*.c": matches all the declarations named "c"</li>
	 * <li>"a.*": matches all the declarations in the "a" container</li>
	 * <li>"a.*.c": matches all the declarations named "c" in the "a" container,
	 * with any path between "a" and "c"</li>
	 * </ul>
	 * 
	 * @param type
	 *            the declarations type
	 * @param declExpression
	 *            an expression to match the full name, supporting wildcards
	 * @param compilationUnits
	 *            the compilation unit(s) to search in (search in all available
	 *            compilation units if omitted)
	 * @return the list of all matching declarations by match order
	 */
	public <T extends Declaration> List<QualifiedDeclaration<T>> findDeclarations(Class<T> type, String declFullName,
			CompilationUnit... compilationUnits) {

		final Pattern pattern = declFullName.contains("*")
				? Pattern.compile(declFullName.replace(".", "\\.").replace("*", ".*")) : null;

		DeclarationFinder<T> finder = new DeclarationFinder<>(this, new DeclarationFinder.Matcher<T>() {
			@Override
			public void matches(DeclarationFinder<T> scanner, Visitable node) {
				String declarationName = scanner.getCurrentDeclarationName();
				if (pattern != null) {
					if (type.isInstance(node) && pattern.matcher(declarationName).matches()) {
						scanner.setMatchState(true, true);
					} else {
						scanner.setMatchState(false, true);
					}
				} else {
					if (declFullName.equals(declarationName)) {
						if (type.isInstance(node)) {
							scanner.setMatchState(true, false);
						} else {
							scanner.setMatchState(false, false);
						}
					} else if (declFullName.startsWith(declarationName)) {
						scanner.setMatchState(false, true);
					} else {
						scanner.setMatchState(false, false);
					}
				}
			}

		});

		finder.scan(compilationUnits.length == 0 ? this.compilationUnits : Arrays.asList(compilationUnits));
		return finder.getMatches();
	}

	/**
	 * Finds in the AST the first declaration for a given type and matching the
	 * given full name.
	 * 
	 * <p>
	 * A declaration expression supports the * wildcard to allow any path in the
	 * expression. Examples of declaration expressions:
	 * <ul>
	 * <li>"a.d.c": matches all declarations having this exact full name</li>
	 * <li>"*.c": matches all the declarations named "c"</li>
	 * <li>"a.*": matches all the declarations in the "a" container</li>
	 * <li>"a.*.c": matches all the declarations named "c" in the "a" container,
	 * with any path between "a" and "c"</li>
	 * </ul>
	 * 
	 * @param type
	 *            the declarations type
	 * @param declExpression
	 *            an expression to match the full name, supporting wildcards
	 * @param compilationUnits
	 *            the compilation unit(s) to search in (search in all available
	 *            compilation units if omitted)
	 * @return the first matching declaration
	 */
	public <T extends Declaration> QualifiedDeclaration<T> findFirstDeclaration(Class<T> type, String declFullName,
			CompilationUnit... compilationUnits) {
		List<QualifiedDeclaration<T>> matches = findDeclarations(type, declFullName, compilationUnits);
		if (matches.isEmpty()) {
			return null;
		} else {
			return matches.get(0);
		}
	}

	/**
	 * Registers a mixin (type declaration) for a given lib. Registered mixins
	 * will be reflected in the @Root annotation of the lib.
	 */
	public void resiterMixin(String libModule, TypeDeclaration typeDeclaration) {
		List<TypeDeclaration> mixinsForLib = mixins.get(libModule);
		if (mixinsForLib == null) {
			mixinsForLib = new ArrayList<>();
			mixins.put(libModule, mixinsForLib);
		}
		mixinsForLib.add(typeDeclaration);
	}

	/**
	 * Returns the registered mixins for the given lib.
	 */
	public List<TypeDeclaration> getMixins(String libModule) {
		return mixins.get(libModule);
	}

	public CompilationUnit getCompilationUnitForLibModule(String libModule) {
		return libModulesCompilationUnits.get(libModule);
	}

	public CompilationUnit getCompilationUnit(File tsDefFile) {
		for (CompilationUnit compilUnit : compilationUnits) {
			if (tsDefFile.equals(compilUnit.file)) {
				return compilUnit;
			}
		}

		return null;
	}

	public final Map<TypeDeclaration, String> getTypeNames() {
		return typeNames;
	}

	/**
	 * @return true if the given TypeDeclaration comes from a dependency (a
	 *         compilation unit which won't be generated)
	 */
	public boolean isInDependency(TypeDeclaration typeDeclaration) {
		String libModule = getLibModule(getTypeName(typeDeclaration));
		return isBlank(libModule) || isDependency(getCompilationUnitForLibModule(libModule));
	}

	/**
	 * @return true if the given TypeDeclaration comes from a dependency (a
	 *         compilation unit which won't be generated)
	 */
	public boolean isInDependency(ModuleDeclaration moduleDeclaration) {
		String libModule = getLibModule(getModuleName(moduleDeclaration));
		return isBlank(libModule) || isDependency(getCompilationUnitForLibModule(libModule));
	}

	public boolean isDependency(String libModule) {
		return isDependency(getCompilationUnitForLibModule(libModule));
	}
}
