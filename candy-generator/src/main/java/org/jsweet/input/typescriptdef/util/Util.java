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
package org.jsweet.input.typescriptdef.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.AnnotationUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.ArrayTypeReference;
import org.jsweet.input.typescriptdef.ast.AstNode;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterizedElement;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.TypedDeclaration;
import org.jsweet.input.typescriptdef.ast.Visitable;
import org.jsweet.util.DirectedGraph;

public class Util {

	protected final static Logger logger = Logger.getLogger(Util.class);
	public static Map<String, String> coreTypeMap = new HashMap<String, String>();
	public static Map<String, String> wrapTypeMap = new HashMap<String, String>();

	static {
		coreTypeMap.put("any", "Object");
		coreTypeMap.put("string", "String");
		coreTypeMap.put("boolean", "Boolean");
		coreTypeMap.put("String", "String");
		coreTypeMap.put("Boolean", "Boolean");
		coreTypeMap.put("Object", "Object");
	}

	static {
		wrapTypeMap.put("any", "java.lang.Object");
		wrapTypeMap.put("string", "java.lang.String");
		wrapTypeMap.put("String", "java.lang.String");
		wrapTypeMap.put("Boolean", "java.lang.Boolean");
		wrapTypeMap.put("boolean", "java.lang.Boolean");
		wrapTypeMap.put("double", "java.lang.Double");
		wrapTypeMap.put("number", "java.lang.Double");
		wrapTypeMap.put("int", "java.lang.Integer");
		wrapTypeMap.put("float", "java.lang.Float");
		wrapTypeMap.put("Object", "java.lang.Object");
	}

	public static <T extends AstNode> T wrapTypeReferences(T node) {
		// new Scanner((Context) null) {
		// @Override
		// public void visitTypeReference(TypeReference typeReference) {
		// typeReference.setName(typeReference.getWrappingTypeName());
		// }
		// }.scan(node);
		// return node;
		return subtituteTypeReferenceNames(node, wrapTypeMap);
	}

	public static <T extends AstNode> T subtituteTypeReferenceNames(T node, final Map<String, String> nameMap) {
		new Scanner((Context) null) {
			@Override
			public void visitTypeReference(TypeReference typeReference) {
				if (nameMap.containsKey(typeReference.getName())) {
					typeReference.setName(nameMap.get(typeReference.getName()));
				}
			}
		}.scan(node);
		return node;
	}

	public static <T extends AstNode> T subtituteTypeReferences(T node, final Map<String, TypeReference> nameMap) {
		new Scanner((Context) null) {
			@Override
			public void visitTypeReference(TypeReference typeReference) {
				if (nameMap.containsKey(typeReference.getName())) {
					TypeReference targetTypeReference = nameMap.get(typeReference.getName());
					if (getParent() instanceof TypedDeclaration
							&& ((TypedDeclaration) getParent()).getType() == typeReference) {
						((TypedDeclaration) getParent()).setType(targetTypeReference.copy());
					} else {
						if (typeReference instanceof ArrayTypeReference) {
							logger.error("cannot substitute array type reference");
						} else {
							typeReference.setName(targetTypeReference.getName());
							typeReference.setTypeArguments(targetTypeReference.getTypeArguments());
						}
					}
				}
			}
		}.scan(node);
		return node;
	}

	public static boolean hasTypeParameterReferences(Context context, AstNode node) {
		List<TypeParameterDeclaration> l = new ArrayList<TypeParameterDeclaration>();
		new Scanner(context) {
			@Override
			public void visitTypeReference(TypeReference typeReference) {
				Type t = lookupType(typeReference);
				if (t instanceof TypeParameterDeclaration) {
					l.add((TypeParameterDeclaration) t);
				}
				super.visitTypeReference(typeReference);
			}
		}.scan(node);
		return !l.isEmpty();
	}

	public static boolean substituteTypeReference(Scanner scanner, TypedDeclaration targetTypedElement,
			TypeReference targetType, TypeReference newType) {
		Visitable node = null;
		do {
			if (node == null) {
				node = scanner.getParent();
			} else {
				node = scanner.getParent(Visitable.class, node);
			}
			if (node instanceof TypeReference) {
				if (((TypeReference) node).substituteTypeReference(targetType, newType)) {
					return true;
				}
			}
		} while (!scanner.getStack().isEmpty() && node != targetTypedElement);
		if (node == targetTypedElement) {
			targetTypedElement.setType(newType);
			return true;
		}
		return false;
	}

	static Pattern referencePathMatcher = Pattern.compile("/// *<reference .*path *= *\"(.*)\".*");

	public static String getLibPathFromReference(String reference) {
		Matcher m = referencePathMatcher.matcher(reference);
		if (m.matches()) {
			return m.group(1);
		}
		return null;
	}

	/**
	 * Gets the relative path that links the two given paths.
	 * 
	 * <pre>
	 * assertEquals("../c", Util.getRelativePath("/a/b", "/a/c"));
	 * assertEquals("..", Util.getRelativePath("/a/b", "/a"));
	 * assertEquals("../e", Util.getRelativePath("/a/b/c", "/a/b/e"));
	 * assertEquals("d", Util.getRelativePath("/a/b/c", "/a/b/c/d"));
	 * assertEquals("d/e", Util.getRelativePath("/a/b/c", "/a/b/c/d/e"));
	 * assertEquals("../../../d/e/f", Util.getRelativePath("/a/b/c", "/d/e/f"));
	 * assertEquals("../..", Util.getRelativePath("/a/b/c", "/a"));
	 * assertEquals("..", Util.getRelativePath("/a/b/c", "/a/b"));
	 * </pre>
	 * 
	 * <p>
	 * Thanks to:
	 * http://mrpmorris.blogspot.com/2007/05/convert-absolute-path-to-relative-
	 * path.html
	 * 
	 * <p>
	 * Bug fix: Renaud Pawlak
	 * 
	 * @param fromPath
	 *            the path to start from
	 * @param toPath
	 *            the path to reach
	 */
	public static String getRelativePath(String fromPath, String toPath) {
		StringBuilder relativePath = null;

		fromPath = fromPath.replaceAll("\\\\", "/");
		toPath = toPath.replaceAll("\\\\", "/");

		if (!fromPath.equals(toPath)) {
			String[] fromSegments = fromPath.split("/");
			String[] toSegments = toPath.split("/");

			// Get the shortest of the two paths
			int length = fromSegments.length < toSegments.length ? fromSegments.length : toSegments.length;

			// Use to determine where in the loop we exited
			int lastCommonRoot = -1;
			int index;

			// Find common root
			for (index = 0; index < length; index++) {
				if (fromSegments[index].equals(toSegments[index])) {
					lastCommonRoot = index;
				} else {
					break;
					// If we didn't find a common prefix then throw
				}
			}
			if (lastCommonRoot != -1) {
				// Build up the relative path
				relativePath = new StringBuilder();
				// Add on the ..
				for (index = lastCommonRoot + 1; index < fromSegments.length; index++) {
					if (fromSegments[index].length() > 0) {
						relativePath.append("../");
					}
				}
				for (index = lastCommonRoot + 1; index < toSegments.length - 1; index++) {
					relativePath.append(toSegments[index] + "/");
				}
				if (!fromPath.startsWith(toPath)) {
					relativePath.append(toSegments[toSegments.length - 1]);
				} else {
					if (relativePath.length() > 0) {
						relativePath.deleteCharAt(relativePath.length() - 1);
					}
				}
			}
		}
		return relativePath == null ? null : relativePath.toString();
	}

	private static String getReferenceMainModuleName(Context context, CompilationUnit compilationUnit, String reference)
			throws IOException {
		if (reference == null) {
			return null;
		}
		File referenceFile = new File(reference);
		if (!referenceFile.isAbsolute()) {
			referenceFile = new File(compilationUnit.getFile().getParentFile(), reference);
			referenceFile = referenceFile.getCanonicalFile();
		}
		CompilationUnit referencedCompilationUnit = null;
		for (CompilationUnit cu : context.compilationUnits) {
			if (cu.getFile().getCanonicalFile().equals(referenceFile)) {
				referencedCompilationUnit = cu;
			}
		}
		return referencedCompilationUnit == null ? null : referencedCompilationUnit.getMainModule().getName();
	}

	public static void createDependencyGraph(Context context) throws IOException {
		DirectedGraph<String> dependencyGraph = new DirectedGraph<String>();
		for (CompilationUnit compilationUnit : context.compilationUnits) {
			String lib = compilationUnit.getMainModule().getName();
			if (lib == null) {
				continue;
			}
			if (!dependencyGraph.contains(lib)) {
				dependencyGraph.add(lib);
			}
			for (String ref : compilationUnit.getReferences()) {
				String refLib = getReferenceMainModuleName(context, compilationUnit, Util.getLibPathFromReference(ref));
				if (refLib == null) {
					continue;
				}
				if (refLib.equals(lib)) {
					// ignore self references
					continue;
				}
				if (!dependencyGraph.contains(refLib)) {
					dependencyGraph.add(refLib);
				}
				dependencyGraph.addEdge(lib, refLib);
			}
			if (context.getLibrariesDefinitions().contains(compilationUnit.getFile())) {
				for (File depFile : context.getDependenciesDefinitions()) {
					String refLib = context.getCompilationUnit(depFile).getMainModule().getName();
					if (refLib == null) {
						continue;
					}
					if (refLib.equals(lib)) {
						// ignore self references
						continue;
					}
					if (!dependencyGraph.contains(refLib)) {
						dependencyGraph.add(refLib);
					}
					dependencyGraph.addEdge(lib, refLib);
				}
			}
		}
		context.dependencyGraph = dependencyGraph;
	}

	public static String toJavaName(String name) {
		return toJavaName(name, false);
	}

	public static String toJavaName(String name, boolean forceLowerCase) {
		if (name == null) {
			return null;
		}
		if (forceLowerCase) {
			if (!StringUtils.isAllLowerCase(name)) {
				name = name.toLowerCase();
			}
		}
		if (name.contains("-") || name.contains("/")) {
			return name.replace('/', '_').replace('-', '_');
		}
		if (Character.isDigit(name.charAt(0))) {
			return "_" + name;
		}
		if (JSweetDefTranslatorConfig.JAVA_KEYWORDS.contains(name)) {
			if (forceLowerCase) {
				return "_" + name;
			} else {
				return StringUtils.capitalize(name);
			}
		}
		return name;
	}

	/**
	 * <p>
	 * Generate a string representation of an Annotation, as suggested by
	 * {@link Annotation#toString()}.
	 * </p>
	 *
	 * @param a
	 *            the annotation of which a string representation is desired
	 * @return the standard string representation of an annotation, not
	 *         {@code null}
	 */
	public static String toString(final Annotation a) {
		final ToStringBuilder builder = new ToStringBuilder(a, TO_STRING_STYLE);
		for (final Method m : a.annotationType().getDeclaredMethods()) {
			if (m.getParameterTypes().length > 0) {
				continue; // wtf?
			}
			try {
				Object val = m.invoke(a);
				if (val instanceof String) {
					val = "\"" + val + "\"";
				}
				builder.append(m.getName(), val);
			} catch (final RuntimeException ex) {
				throw ex;
			} catch (final Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return builder.build();
	}

	private static final ToStringStyle TO_STRING_STYLE = new ToStringStyle() {
		/** Serialization version */
		private static final long serialVersionUID = 1L;

		{
			setDefaultFullDetail(true);
			setArrayContentDetail(true);
			setUseClassName(true);
			setUseShortClassName(true);
			setUseIdentityHashCode(false);
			setContentStart("(");
			setContentEnd(")");
			setFieldSeparator(", ");
			setArrayStart("[");
			setArrayEnd("]");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getShortClassName(final java.lang.Class<?> cls) {
			Class<? extends Annotation> annotationType = null;
			for (final Class<?> iface : ClassUtils.getAllInterfaces(cls)) {
				if (Annotation.class.isAssignableFrom(iface)) {
					@SuppressWarnings("unchecked")
					// OK because we just checked the assignability
					final Class<? extends Annotation> found = (Class<? extends Annotation>) iface;
					annotationType = found;
					break;
				}
			}
			return new StringBuilder(annotationType == null ? "" : annotationType.getName()).insert(0, '@').toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void appendDetail(final StringBuffer buffer, final String fieldName, Object value) {
			if (value instanceof Annotation) {
				value = AnnotationUtils.toString((Annotation) value);
			}
			super.appendDetail(buffer, fieldName, value);
		}

	};

	public static void checkAndAdjustDeclarationName(Declaration declaration) {
		checkAndAdjustDeclarationName(declaration, false);
	}

	public static void checkAndAdjustDeclarationName(Declaration declaration, boolean forceLowerCase) {
		String oldName = declaration.getName();

		if (StringUtil.isBlank(oldName)) {
			return;
		}
		String newName = toJavaName(oldName, forceLowerCase);
		if (declaration.isQuotedName() && newName.contains(".")) {
			newName = newName.replace('.', '_');
		}
		if (!oldName.equals(newName)) {
			declaration.removeStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_NAME);
			declaration.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_NAME + "(\"" + oldName + "\")");
			declaration.setOriginalName(declaration.getName());
			declaration.setName(newName);
		}
	}

	/**
	 * Returns true if the container or any subnode of that AST contains the
	 * exact given node object. Note that this function uses object reference
	 * comparison (==).
	 */
	public static boolean containsAstNode(AstNode container, AstNode node) {
		final boolean[] result = { false };
		new Scanner((Context) null) {
			@Override
			public void scan(Visitable visitable) {
				if (node == visitable) {
					result[0] = true;
				} else {
					super.scan(visitable);
				}
			}
		}.scan(container);
		return result[0];
	}

	/**
	 * Gets the qualifier of the given qualified name.
	 */
	public static String getQualifier(String qualifiedName) {
		if (qualifiedName == null) {
			return null;
		}
		int index = qualifiedName.lastIndexOf('.');
		if (index < 0) {
			return null;
		}
		return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
	}

	public static boolean isQualified(String name) {
		return name.indexOf('.') != -1;
	}

	/**
	 * Gets the simple name of the given qualified name.
	 */
	public static String getSimpleName(String qualifiedName) {
		return qualifiedName == null ? null : qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
	}

	private static TypeDeclaration createGlobalsClass(String name) {
		TypeDeclaration globalsClass = new TypeDeclaration(null, "class", name, null, null, new Declaration[0]);
		globalsClass.addModifier("final");
		FunctionDeclaration constructor = new FunctionDeclaration(null, "constructor", null,
				new ParameterDeclaration[0], null);
		constructor.addModifier("private");
		globalsClass.addMember(constructor);
		return globalsClass;
	}

	public static TypeDeclaration getOrCreateGlobalsType(Context context, ModuleDeclaration moduleDeclaration,
			DeclarationContainer container) {
		String modName = moduleDeclaration.getName();
		TypeDeclaration globalsClass = null;
		if (container != null && Util.elligibleToClass(moduleDeclaration)) {
			globalsClass = container.findType(modName);
			if (globalsClass == null) {
				globalsClass = createGlobalsClass(modName);
				context.registerType(
						((container instanceof ModuleDeclaration)
								? context.getModuleName((ModuleDeclaration) container) + "." : "") + modName,
						globalsClass);
				globalsClass.setDocumentation("/** Utility class. */");
				container.addMember(globalsClass);
			}
		} else {
			globalsClass = context.getTypeDeclaration(
					context.getModuleName(moduleDeclaration) + "." + JSweetDefTranslatorConfig.GLOBALS_CLASS_NAME);
			if (globalsClass == null) {
				globalsClass = createGlobalsClass(JSweetDefTranslatorConfig.GLOBALS_CLASS_NAME);
				context.registerType(
						context.getModuleName(moduleDeclaration) + "." + JSweetDefTranslatorConfig.GLOBALS_CLASS_NAME,
						globalsClass);
				globalsClass.setDocumentation("/** This class holds all the global functions and variables of the "
						+ moduleDeclaration.getName() + " package. */");
				moduleDeclaration.addMember(globalsClass);
			}
		}
		return globalsClass;
	}

	public static TypeParameterDeclaration[] findTypeParameters(Scanner parent, Visitable visitable) {
		return new TypeParameterFinder(parent).find(visitable);
	}

	public static TypeParameterDeclaration[] findTypeParameters(Context context,
			TypeParameterizedElement typeParameterHolder, Visitable visitable) {
		return findTypeParameters(context, typeParameterHolder, visitable, null);
	}

	public static TypeParameterDeclaration[] findTypeParameters(Context context,
			TypeParameterizedElement typeParameterHolder, Visitable visitable,
			BiConsumer<TypeReference, TypeParameterDeclaration> referenceHandler) {
		return new TypeParameterFinder2(context, typeParameterHolder, referenceHandler).find(visitable);
	}

	public static String[] findTypeParameterNames(Context context, TypeParameterizedElement typeParameterHolder,
			Visitable visitable) {
		return Arrays.asList(findTypeParameters(context, typeParameterHolder, visitable)).stream().map(t -> t.getName())
				.toArray(size -> new String[size]);
	}

	static class TypeParameterFinder extends Scanner {
		private List<TypeParameterDeclaration> declarations = new ArrayList<TypeParameterDeclaration>();

		public TypeParameterFinder(Scanner parent) {
			super(parent);
		}

		public TypeParameterDeclaration[] find(Visitable visitable) {
			visitable.accept(this);
			return declarations.toArray(new TypeParameterDeclaration[0]);
		}

		@Override
		public void visitTypeReference(TypeReference typeReference) {
			Type t = lookupType(typeReference, null);
			if (t instanceof TypeParameterDeclaration) {
				if (!declarations.contains(t)) {
					declarations.add((TypeParameterDeclaration) t);
				}
			}
			super.visitTypeReference(typeReference);
		}

	}

	static class TypeParameterFinder2 extends Scanner {
		private List<TypeParameterDeclaration> declarations = new ArrayList<TypeParameterDeclaration>();

		private TypeParameterizedElement typeParameterHolder;

		private BiConsumer<TypeReference, TypeParameterDeclaration> referenceHandler;

		public TypeParameterFinder2(Context context, TypeParameterizedElement typeParameterHolder,
				BiConsumer<TypeReference, TypeParameterDeclaration> referenceHandler) {
			super(context);
			this.typeParameterHolder = typeParameterHolder;
			this.referenceHandler = referenceHandler;
		}

		public TypeParameterDeclaration[] find(Visitable visitable) {
			if (typeParameterHolder.getTypeParameters() != null && typeParameterHolder.getTypeParameters().length > 0) {
				visitable.accept(this);
			}
			return declarations.toArray(new TypeParameterDeclaration[0]);
		}

		@Override
		public void visitTypeReference(TypeReference typeReference) {
			for (TypeParameterDeclaration t : typeParameterHolder.getTypeParameters()) {
				if (typeReference.getName().equals(t.getName())) {
					if (referenceHandler != null) {
						referenceHandler.accept(typeReference, t);
					}
					if (!declarations.contains(t)) {
						declarations.add(t);
					}
				}
			}
			super.visitTypeReference(typeReference);
		}

	}

	public static Entry<String, Integer> splitIntSuffix(String string) {
		for (int i = 0; i < string.length(); i++) {
			try {
				int suffix = Integer.parseInt(string.substring(i));
				return new AbstractMap.SimpleEntry<String, Integer>(string.substring(0, i), suffix);
			} catch (NumberFormatException e) {
				// swallow
			}
		}
		return null;
	}

	public static boolean elligibleToClass(ModuleDeclaration module) {
		if (Character.isUpperCase(module.getName().charAt(0)) && !StringUtils.isAllUpperCase(module.getName())) {
			for (Declaration declaration : module.getMembers()) {
				if (declaration instanceof TypeDeclaration || declaration instanceof ModuleDeclaration) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static String getLibPackageNameFromTsDefFile(File libFile) {
		File parent = libFile.getParentFile();
		String pack = parent.getName().toLowerCase().replaceAll("[.-]", "_");
		parent = parent.getParentFile();

		String packageName;
		if (parent.getParentFile() != null && !parent.getName().equals("test") && !parent.getName().equals("typings")
				&& !parent.getName().equals("globals")) {
			pack = parent.getName().toLowerCase().replaceAll("[.-]", "_") + (pack == null ? "" : "." + pack);
			packageName = JSweetDefTranslatorConfig.LIBS_PACKAGE + "." + pack;
		} else {
			packageName = JSweetDefTranslatorConfig.LIBS_PACKAGE + "."
					+ libFile.getParentFile().getName().toLowerCase().replaceAll("[.-]", "_");
		}

		logger.debug(libFile.getPath() + " ==> " + packageName);

		return packageName;
	}
}
