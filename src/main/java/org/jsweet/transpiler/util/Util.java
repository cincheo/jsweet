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
package org.jsweet.transpiler.util;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;

import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Attribute.Compound;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCCatch;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLambda;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Name;

/**
 * Various utilities.
 * 
 * @author Renaud Pawlak
 */
public class Util {

	private static long id = 121;

	/**
	 * Returns a unique id (incremental).
	 */
	public static long getId() {
		return id++;
	}

	/**
	 * Tells if the given type is within the Java sources being compiled.
	 */
	public static boolean isSourceType(ClassSymbol clazz) {
		// hack to know if it is a source file or a class file
		return (clazz.sourcefile != null && clazz.sourcefile.getClass().getName().equals("com.sun.tools.javac.file.RegularFileObject"));
	}

	/**
	 * Recursively adds files to the given list.
	 * 
	 * @param extension
	 *            the extension to filter with
	 * @param file
	 *            the root file/directory to look into recursively
	 * @param files
	 *            the list to add the files matching the extension
	 */
	public static void addFiles(String extension, File file, LinkedList<File> files) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(extension, f, files);
			}
		} else if (file.getName().endsWith(extension)) {
			files.add(file);
		}
	}

	/**
	 * Gets the full signature of the given method.
	 */
	public static String getFullMethodSignature(MethodSymbol method) {
		return method.getEnclosingElement().getQualifiedName() + "." + method.toString();
	}

	/**
	 * Tells if the given symbol is annotated with one of the given annotation
	 * type names.
	 */
	public static boolean hasAnnotationType(Symbol symbol, String... annotationTypes) {
		for (Compound a : symbol.getAnnotationMirrors()) {
			for (String annotationType : annotationTypes) {
				if (annotationType.equals(a.type.toString())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the symbol's annotation that correspond to the given annotation type
	 * name if exists.
	 */
	public static AnnotationMirror getAnnotation(Symbol symbol, String annotationType) {
		for (Compound a : symbol.getAnnotationMirrors()) {
			if (annotationType.equals(a.type.toString())) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Gets repeatable annotations. By convention, the container must be named
	 * after the contained name + 's'.
	 * 
	 * @param symbol
	 *            the annotated symbol
	 * @param annotationType
	 *            the qualified name of the repeated annotation
	 * @return
	 */
	public static List<AnnotationMirror> getAnnotations(Symbol symbol, String annotationType) {
		List<AnnotationMirror> annotations = new ArrayList<>();
		for (Compound a : symbol.getAnnotationMirrors()) {
			if ((annotationType + "s").equals(a.type.toString())) {
				Attribute.Array array = (Attribute.Array) a.values.head.snd;
				for (Attribute attr : array.values) {
					annotations.add((AnnotationMirror) attr);
				}
				return annotations;
			} else if (annotationType.equals(a.type.toString())) {
				annotations.add(a);
				return annotations;
			}
		}
		return annotations;
	}

	/**
	 * Gets the annotation tree that matches the given type name.
	 */
	public static JCAnnotation getAnnotation(List<JCAnnotation> annotations, String annotationType) {
		for (JCAnnotation a : annotations) {
			if (annotationType.equals(a.type.toString())) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Tells if the given type declaration contains some method declarations.
	 */
	public static boolean containsMethods(JCClassDecl classDeclaration) {
		for (JCTree member : classDeclaration.getMembers()) {
			if (member instanceof JCMethodDecl) {
				JCMethodDecl method = (JCMethodDecl) member;
				if (method.pos == classDeclaration.pos) {
					continue;
				}
				return true;
				// if (method.body != null) {
				// return true;
				// }
			} else if (member instanceof JCVariableDecl) {
				if (((JCVariableDecl) member).mods.getFlags().contains(Modifier.STATIC)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tells if the given type is a Java interface.
	 */
	public static boolean isInterface(TypeSymbol typeSymbol) {
		return (typeSymbol.type.isInterface() || Util.hasAnnotationType(typeSymbol, JSweetConfig.ANNOTATION_INTERFACE));
	}

	private static void putVar(Map<String, VarSymbol> vars, VarSymbol varSymbol) {
		vars.put(varSymbol.getSimpleName().toString(), varSymbol);
	}

	/**
	 * Finds all the variables accessible within the current scanning scope.
	 */
	public static void fillAllVariablesInScope(Map<String, VarSymbol> vars, Stack<JCTree> scanningStack, JCTree from, JCTree to) {
		if (from == to) {
			return;
		}
		int i = scanningStack.indexOf(from);
		if (i == -1 || i == 0) {
			return;
		}
		JCTree parent = scanningStack.get(i - 1);
		List<JCStatement> statements = null;
		switch (parent.getKind()) {
		case BLOCK:
			statements = ((JCBlock) parent).stats;
			break;
		case CASE:
			statements = ((JCCase) parent).stats;
			break;
		case CATCH:
			putVar(vars, ((JCCatch) parent).param.sym);
			break;
		case FOR_LOOP:
			if (((JCForLoop) parent).init != null) {
				for (JCStatement s : ((JCForLoop) parent).init) {
					if (s instanceof JCVariableDecl) {
						putVar(vars, ((JCVariableDecl) s).sym);
					}
				}
			}
			break;
		case ENHANCED_FOR_LOOP:
			putVar(vars, ((JCEnhancedForLoop) parent).var.sym);
			break;
		case METHOD:
			for (JCVariableDecl var : ((JCMethodDecl) parent).params) {
				putVar(vars, var.sym);
			}
			break;
		default:

		}
		if (statements != null) {
			for (JCStatement s : statements) {
				if (s == from) {
					break;
				} else if (s instanceof JCVariableDecl) {
					putVar(vars, ((JCVariableDecl) s).sym);
				}
			}
		}
		fillAllVariablesInScope(vars, scanningStack, parent, to);
	}

	/**
	 * Fills the given map with all the variables beeing accessed within the
	 * given code tree.
	 */
	public static void fillAllVariableAccesses(final Map<String, VarSymbol> vars, final JCTree tree) {
		new TreeScanner() {
			@Override
			public void visitIdent(JCIdent ident) {
				if (ident.sym.getKind() == ElementKind.LOCAL_VARIABLE) {
					putVar(vars, (VarSymbol) ident.sym);
				}
			}

			@Override
			public void visitLambda(JCLambda lambda) {
				if (lambda == tree) {
					super.visitLambda(lambda);
				}
			}
		}.scan(tree);
	}

	/**
	 * Finds the method declaration within the given type, for the given
	 * invocation.
	 */
	public static MethodSymbol findMethodDeclarationInType(Types types, TypeSymbol typeSymbol, JCMethodInvocation invocation) {
		String meth = invocation.meth.toString();
		String methName = meth.substring(meth.lastIndexOf('.') + 1);
		return findMethodDeclarationInType(types, typeSymbol, methName, (MethodType) invocation.meth.type);
	}

	/**
	 * Finds the method in the given type that matches the given name and
	 * signature.
	 */
	public static MethodSymbol findMethodDeclarationInType(Types types, TypeSymbol typeSymbol, String methodName, MethodType methodType) {
		if (typeSymbol == null) {
			return null;
		}
		if (typeSymbol.getEnclosedElements() != null) {
			for (Element element : typeSymbol.getEnclosedElements()) {
				if ((element instanceof MethodSymbol) && (methodName.equals(element.getSimpleName().toString())
						|| ((MethodSymbol) element).getKind() == ElementKind.CONSTRUCTOR && "this".equals(methodName))) {
					MethodSymbol methodSymbol = (MethodSymbol) element;
					if (methodType == null) {
						return methodSymbol;
					}
					if (isInvocable(types, methodType, methodSymbol.type.asMethodType())) {
						return methodSymbol;
					}
				}
			}
		}
		MethodSymbol result = null;
		if (typeSymbol instanceof ClassSymbol && ((ClassSymbol) typeSymbol).getSuperclass() != null) {
			result = findMethodDeclarationInType(types, ((ClassSymbol) typeSymbol).getSuperclass().tsym, methodName, methodType);
		}
		if (result == null) {
			if (typeSymbol instanceof ClassSymbol && ((ClassSymbol) typeSymbol).getInterfaces() != null) {
				for (Type t : ((ClassSymbol) typeSymbol).getInterfaces()) {
					result = findMethodDeclarationInType(types, t.tsym, methodName, methodType);
					if (result != null) {
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Finds methods by name.
	 */
	public static void findMethodDeclarationsInType(TypeSymbol typeSymbol, Collection<String> methodNames, Set<String> ignoredTypeNames,
			List<MethodSymbol> result) {
		if (typeSymbol == null) {
			return;
		}
		if (ignoredTypeNames.contains(typeSymbol.getQualifiedName())) {
			return;
		}
		if (typeSymbol.getEnclosedElements() != null) {
			for (Element element : typeSymbol.getEnclosedElements()) {
				if ((element instanceof MethodSymbol) && (methodNames.contains(element.getSimpleName().toString()))) {
					result.add((MethodSymbol) element);
				}
			}
		}
		if (typeSymbol instanceof ClassSymbol && ((ClassSymbol) typeSymbol).getSuperclass() != null) {
			findMethodDeclarationsInType(((ClassSymbol) typeSymbol).getSuperclass().tsym, methodNames, ignoredTypeNames, result);
		}
		if (result == null) {
			if (typeSymbol instanceof ClassSymbol && ((ClassSymbol) typeSymbol).getInterfaces() != null) {
				for (Type t : ((ClassSymbol) typeSymbol).getInterfaces()) {
					findMethodDeclarationsInType(t.tsym, methodNames, ignoredTypeNames, result);
				}
			}
		}
	}

	/**
	 * Scans member declarations in type hierachy.
	 */
	public static boolean scanMemberDeclarationsInType(TypeSymbol typeSymbol, Set<String> ignoredTypeNames, Function<Element, Boolean> scanner) {
		if (typeSymbol == null) {
			return true;
		}
		if (ignoredTypeNames.contains(typeSymbol.getQualifiedName())) {
			return true;
		}
		if (typeSymbol.getEnclosedElements() != null) {
			for (Element element : typeSymbol.getEnclosedElements()) {
				if (!scanner.apply(element)) {
					return false;
				}
			}
		}
		if (typeSymbol instanceof ClassSymbol && ((ClassSymbol) typeSymbol).getSuperclass() != null) {
			if (!scanMemberDeclarationsInType(((ClassSymbol) typeSymbol).getSuperclass().tsym, ignoredTypeNames, scanner)) {
				return false;
			}
		}
		if (typeSymbol instanceof ClassSymbol && ((ClassSymbol) typeSymbol).getInterfaces() != null) {
			for (Type t : ((ClassSymbol) typeSymbol).getInterfaces()) {
				if (!scanMemberDeclarationsInType(t.tsym, ignoredTypeNames, scanner)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Tells if a method can be invoked with some given parameter types.
	 * 
	 * @param types
	 *            a reference to the types in the compilation scope
	 * @param from
	 *            the caller method signature to test (contains the parameter
	 *            types)
	 * @param target
	 *            the callee method signature
	 * @return true if the callee can be invoked by the caller
	 */
	public static boolean isInvocable(Types types, MethodType from, MethodType target) {
		if (from.getParameterTypes().length() != target.getParameterTypes().length()) {
			return false;
		}
		for (int i = 0; i < from.getParameterTypes().length(); i++) {
			if (!types.isAssignable(types.erasure(from.getParameterTypes().get(i)), types.erasure(target.getParameterTypes().get(i)))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the TypeScript initial default value for a type.
	 */
	public String getTypeInitalValue(String typeName) {
		if (typeName == null) {
			return "null";
		}
		switch (typeName) {
		case "void":
			return null;
		case "boolean":
			return "false";
		case "number":
			return "0";
		default:
			return "null";
		}
	}

	/**
	 * Fills the given set with all the default methods found in the current
	 * type and super interfaces.
	 */
	public static void findDefaultMethodsInType(Set<Entry<JCClassDecl, JCMethodDecl>> defaultMethods, JSweetContext context, ClassSymbol classSymbol) {
		if (context.getDefaultMethods(classSymbol) != null) {
			defaultMethods.addAll(context.getDefaultMethods(classSymbol));
		}
		for (Type t : classSymbol.getInterfaces()) {
			findDefaultMethodsInType(defaultMethods, context, (ClassSymbol) t.tsym);
		}
	}

	/**
	 * Finds the field in the given type that matches the given name.
	 */
	public static VarSymbol findFieldDeclaration(ClassSymbol classSymbol, Name name) {
		Iterator<Symbol> it = classSymbol.members_field.getElementsByName(name, (symbol) -> {
			return symbol instanceof VarSymbol;
		}).iterator();
		if (it.hasNext()) {
			return (VarSymbol) it.next();
		} else {
			if (classSymbol.getSuperclass().tsym instanceof ClassSymbol) {
				return findFieldDeclaration((ClassSymbol) classSymbol.getSuperclass().tsym, name);
			} else {
				return null;
			}
		}
	}

	/**
	 * Gets the actual name of a symbol from a JSweet convention, so including
	 * potential <code>jsweet.lang.Name</code> annotation.
	 */
	public static String getActualName(Symbol symbol) {
		String name = symbol.getSimpleName().toString();
		if (Util.hasAnnotationType(symbol, JSweetConfig.ANNOTATION_NAME)) {
			String originalName = Util.getAnnotationValue(symbol, JSweetConfig.ANNOTATION_NAME, null);
			if (!isBlank(originalName)) {
				name = originalName;
			}
		}
		return name;
	}

	private static void getRootRelativeName(Map<Symbol, String> nameMapping, StringBuilder sb, Symbol symbol) {
		if (!Util.hasAnnotationType(symbol, JSweetConfig.ANNOTATION_ROOT)) {
			if (sb.length() > 0 && !"".equals(symbol.toString())) {
				sb.insert(0, ".");
			}

			String name = symbol.getSimpleName().toString();

			if (nameMapping != null && nameMapping.containsKey(symbol)) {
				name = nameMapping.get(symbol);
			} else {
				if (Util.hasAnnotationType(symbol, JSweetConfig.ANNOTATION_NAME)) {
					String originalName = Util.getAnnotationValue(symbol, JSweetConfig.ANNOTATION_NAME, null);
					if (!isBlank(originalName)) {
						name = originalName;
					}
				}
			}
			sb.insert(0, name);
			symbol = (symbol instanceof PackageSymbol) ? ((PackageSymbol) symbol).owner : symbol.getEnclosingElement();
			if (symbol != null) {
				getRootRelativeName(nameMapping, sb, symbol);
			}
		}
	}

	/**
	 * Gets the top-level package enclosing the given symbol. The top-level
	 * package is the one that is enclosed within a root package (see
	 * <code>jsweet.lang.Root</code>) or the one in the default (unnamed)
	 * package.
	 */
	public static PackageSymbol getTopLevelPackage(Symbol symbol) {
		if ((symbol instanceof PackageSymbol) && Util.hasAnnotationType(symbol, JSweetConfig.ANNOTATION_ROOT)) {
			return null;
		}
		Symbol parent = (symbol instanceof PackageSymbol) ? ((PackageSymbol) symbol).owner : symbol.getEnclosingElement();
		if (parent != null && Util.hasAnnotationType(parent, JSweetConfig.ANNOTATION_ROOT)) {
			if (symbol instanceof PackageSymbol) {
				return (PackageSymbol) symbol;
			} else {
				return null;
			}
		} else {
			if (parent == null || (parent instanceof PackageSymbol && StringUtils.isBlank(parent.getSimpleName()))) {
				if (symbol instanceof PackageSymbol) {
					return (PackageSymbol) symbol;
				} else {
					return null;
				}
			} else {
				return getTopLevelPackage(parent);
			}
		}
	}

	/**
	 * Finds the first (including itself) enclosing package annotated
	 * with @Root.
	 */
	public static PackageSymbol getFirstEnclosingRootPackage(PackageSymbol packageSymbol) {
		if (packageSymbol == null) {
			return null;
		}
		if (Util.hasAnnotationType(packageSymbol, JSweetConfig.ANNOTATION_ROOT)) {
			return packageSymbol;
		}
		return getFirstEnclosingRootPackage((PackageSymbol) packageSymbol.owner);
	}

	private static void getRootRelativeJavaName(StringBuilder sb, Symbol symbol) {
		if (!Util.hasAnnotationType(symbol, JSweetConfig.ANNOTATION_ROOT)) {
			if (sb.length() > 0 && !"".equals(symbol.toString())) {
				sb.insert(0, ".");
			}

			String name = symbol.getSimpleName().toString();

			sb.insert(0, name);
			symbol = (symbol instanceof PackageSymbol) ? ((PackageSymbol) symbol).owner : symbol.getEnclosingElement();
			if (symbol != null) {
				getRootRelativeJavaName(sb, symbol);
			}
		}
	}

	/**
	 * Tells if this qualified name denotes a JSweet globals class.
	 */
	public static boolean isGlobalsClassName(String qualifiedName) {
		return qualifiedName != null
				&& (JSweetConfig.GLOBALS_CLASS_NAME.equals(qualifiedName) || qualifiedName.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME));
	}

	/**
	 * Gets the qualified name of a symbol relatively to the root package
	 * (potentially annotated with <code>jsweet.lang.Root</code>).
	 * 
	 * @param nameMapping
	 *            a map to redirect names
	 * @param symbol
	 *            the symbol to get the name of
	 * @param useJavaNames
	 *            if true uses plain Java names, if false uses
	 *            <code>jsweet.lang.Name</code> annotations
	 * @return
	 */
	public static String getRootRelativeName(Map<Symbol, String> nameMapping, Symbol symbol, boolean useJavaNames) {
		if (useJavaNames) {
			return getRootRelativeJavaName(symbol);
		} else {
			return getRootRelativeName(nameMapping, symbol);
		}
	}

	/**
	 * Gets the qualified name of a symbol relatively to the root package
	 * (potentially annotated with <code>jsweet.lang.Root</code>). This function
	 * takes into account potential <code>jsweet.lang.Name</code> annotations).
	 */
	public static String getRootRelativeName(Map<Symbol, String> nameMapping, Symbol symbol) {
		StringBuilder sb = new StringBuilder();
		getRootRelativeName(nameMapping, sb, symbol);
		if (sb.length() > 0 && sb.charAt(0) == '.') {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Gets the qualified name of a symbol relatively to the root package
	 * (potentially annotated with <code>jsweet.lang.Root</code>). This function
	 * ignores <code>jsweet.lang.Name</code> annotations).
	 */
	public static String getRootRelativeJavaName(Symbol symbol) {
		StringBuilder sb = new StringBuilder();
		getRootRelativeJavaName(sb, symbol);
		return sb.toString();
	}

	/**
	 * Gets the first value of the 'value' property.
	 */
	public static Object getFirstAnnotationValue(AnnotationMirror annotation, Object defaultValue) {
		for (AnnotationValue value : annotation.getElementValues().values()) {
			return value.getValue();
		}
		return defaultValue;
	}

	/**
	 * Gets the value of the given annotation property.
	 * 
	 * @param annotation
	 *            the annotation
	 * @param propertyName
	 *            the name of the annotation property to get the value of
	 * @param defaultValue
	 *            the value to return if not found
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAnnotationValue(AnnotationMirror annotation, String propertyName, T defaultValue) {
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> annoProperty : annotation.getElementValues().entrySet()) {
			if (annoProperty.getKey().getSimpleName().toString().equals(propertyName)) {
				return (T) annoProperty.getValue().getValue();
			}
		}
		return defaultValue;
	}

	/**
	 * Gets the first value of the 'value' property for the given annotation
	 * type if found on the given symbol.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAnnotationValue(Symbol symbol, String annotationType, T defaultValue) {
		AnnotationMirror anno = getAnnotation(symbol, annotationType);
		T val = defaultValue;
		if (anno != null) {
			val = (T) getFirstAnnotationValue(anno, defaultValue);
		}
		return val;
	}

	/**
	 * Tells if this parameter declaration is varargs.
	 */
	public static boolean isVarargs(JCVariableDecl varDecl) {
		return (varDecl.mods.flags & Flags.VARARGS) == Flags.VARARGS;
	}

	/**
	 * Gets the file from a Java file object.
	 */
	public static File toFile(JavaFileObject javaFileObject) {
		return new File(javaFileObject.getName());
	}

	/**
	 * Transforms a Java iterable to a javac list.
	 */
	public static <T> com.sun.tools.javac.util.List<T> toJCList(Iterable<T> collection) {
		return com.sun.tools.javac.util.List.from(collection);
	}

	/**
	 * Transforms a list of source files to Java file objects (used by javac).
	 */
	public static com.sun.tools.javac.util.List<JavaFileObject> toJavaFileObjects(JavaFileManager fileManager, Collection<File> sourceFiles)
			throws IOException {
		com.sun.tools.javac.util.List<JavaFileObject> fileObjects = com.sun.tools.javac.util.List.nil();
		JavacFileManager javacFileManager = (JavacFileManager) fileManager;
		for (JavaFileObject fo : javacFileManager.getJavaFileObjectsFromFiles(sourceFiles)) {
			fileObjects = fileObjects.append(fo);
		}
		if (fileObjects.length() != sourceFiles.size()) {
			throw new IOException("invalid file list");
		}
		return fileObjects;
	}

	/**
	 * Transforms a source file to a Java file object (used by javac).
	 */
	public static JavaFileObject toJavaFileObject(JavaFileManager fileManager, File sourceFile) throws IOException {
		List<JavaFileObject> javaFileObjects = toJavaFileObjects(fileManager, asList(sourceFile));
		return javaFileObjects.isEmpty() ? null : javaFileObjects.get(0);
	}

	private final static Pattern REGEX_CHARS = Pattern.compile("([\\\\*+\\[\\](){}\\$.?\\^|])");

	/**
	 * This function will escape special characters within a string to ensure
	 * that the string will not be parsed as a regular expression. This is
	 * helpful with accepting using input that needs to be used in functions
	 * that take a regular expression as an argument (such as
	 * String.replaceAll(), or String.split()).
	 * 
	 * @param regex
	 *            - argument which we wish to escape.
	 * @return - Resulting string with the following characters escaped:
	 *         [](){}+*^?$.\
	 */
	public static String escapeRegex(final String regex) {
		Matcher match = REGEX_CHARS.matcher(regex);
		return match.replaceAll("\\\\$1");
	}

	/**
	 * Varargs to mutable list.
	 */
	@SafeVarargs
	public static final <T> List<T> list(T... items) {
		ArrayList<T> list = new ArrayList<T>(items.length);
		for (T item : items) {
			list.add(item);
		}
		return list;
	}

	/**
	 * Tells if two type symbols are assignable.
	 */
	public static boolean isAssignable(Types types, TypeSymbol to, TypeSymbol from) {
		if (to.equals(from)) {
			return true;
		} else {
			return types.isAssignable(from.asType(), to.asType());
		}
	}

	/**
	 * Tells if the given list contains an type which is assignable from type.
	 */
	public static boolean containsAssignableType(Types types, List<Type> list, Type type) {
		for (Type t : list) {
			if (types.isAssignable(t, type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the relative path to reach a symbol from another one.
	 * 
	 * @param fromSymbol
	 *            the start path
	 * @param toSymbol
	 *            the end path
	 * @return the '/'-separated path
	 * @see #getRelativePath(String, String)
	 */
	public static String getRelativePath(Symbol fromSymbol, Symbol toSymbol) {
		return Util.getRelativePath("/" + fromSymbol.getQualifiedName().toString().replace('.', '/'),
				"/" + toSymbol.getQualifiedName().toString().replace('.', '/'));
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

	/**
	 * Removes the extensions of the given file name.
	 * 
	 * @param fileName
	 *            the given file name (can contain path)
	 * @return the file name without the extension
	 */
	public static String removeExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) {
			return fileName;
		} else {
			return fileName.substring(0, index);
		}
	}

	/**
	 * Tells if the given directory or any of its sub-directory contains one of
	 * the given files.
	 * 
	 * @param dir
	 *            the directory to look into
	 * @param files
	 *            the files to be found
	 * @return true if one of the given files is found
	 */
	public static boolean containsFile(File dir, File[] files) {
		for (File child : dir.listFiles()) {
			if (child.isDirectory()) {
				if (containsFile(child, files)) {
					return true;
				}
			} else {
				for (File file : files) {
					if (child.getAbsolutePath().equals(file.getAbsolutePath())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns true is the type is an integral numeric value.
	 */
	public static boolean isIntegral(Type type) {
		if (type == null) {
			return false;
		}
		switch (type.getKind()) {
		case BYTE:
		case SHORT:
		case INT:
		case LONG:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns true is the type is a number.
	 */
	public static boolean isNumber(Type type) {
		if (type == null) {
			return false;
		}
		switch (type.getKind()) {
		case BYTE:
		case SHORT:
		case INT:
		case LONG:
		case DOUBLE:
		case FLOAT:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns true is an arithmetic operator.
	 */
	public static boolean isArithmeticOperator(Kind kind) {
		switch (kind) {
		case MINUS:
		case PLUS:
		case MULTIPLY:
		case DIVIDE:
		case AND:
		case OR:
		case XOR:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns true is an comparison operator.
	 */
	public static boolean isComparisonOperator(Kind kind) {
		switch (kind) {
		case GREATER_THAN:
		case GREATER_THAN_EQUAL:
		case LESS_THAN:
		case LESS_THAN_EQUAL:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Grabs the names of all the support interfaces in the class and interface
	 * hierarchy.
	 */
	public static void grabSupportedInterfaceNames(Set<String> interfaces, TypeSymbol type) {
		if (type == null) {
			return;
		}
		if (Util.isInterface(type)) {
			interfaces.add(type.getQualifiedName().toString());
		}
		if (type instanceof ClassSymbol) {
			for (Type t : ((ClassSymbol) type).getInterfaces()) {
				grabSupportedInterfaceNames(interfaces, t.tsym);
			}
			grabSupportedInterfaceNames(interfaces, ((ClassSymbol) type).getSuperclass().tsym);
		}
	}

	public static void grabMethodsToBeImplemented(List<MethodSymbol> methods, TypeSymbol type) {
		if (type == null) {
			return;
		}
		if (Util.isInterface(type)) {
			for (Symbol s : type.getEnclosedElements()) {
				if (s instanceof MethodSymbol) {
					methods.add((MethodSymbol) s);
				}
			}
		}
		if (type instanceof ClassSymbol) {
			for (Type t : ((ClassSymbol) type).getInterfaces()) {
				grabMethodsToBeImplemented(methods, t.tsym);
			}
			grabMethodsToBeImplemented(methods, ((ClassSymbol) type).getSuperclass().tsym);
		}
	}

	/**
	 * Looks up a class in the given class hierarchy and returns true if found.
	 */
	public static boolean isParent(ClassSymbol clazz, ClassSymbol toFind) {
		if (clazz == null) {
			return false;
		}
		if (clazz == toFind) {
			return true;
		}
		if (isParent((ClassSymbol) clazz.getSuperclass().tsym, toFind)) {
			return true;
		}
		for (Type t : clazz.getInterfaces()) {
			if (isParent((ClassSymbol) t.tsym, toFind)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Recursively looks up one of the given types in the type hierachy of the
	 * given class.
	 * 
	 * @return true if one of the given names is found as a superclass or a
	 *         superinterface
	 */
	public static boolean hasParent(ClassSymbol clazz, String... qualifiedNamesToFind) {
		if (clazz == null) {
			return false;
		}
		if (ArrayUtils.contains(qualifiedNamesToFind, clazz.getQualifiedName().toString())) {
			return true;
		}
		if (hasParent((ClassSymbol) clazz.getSuperclass().tsym, qualifiedNamesToFind)) {
			return true;
		}
		for (Type t : clazz.getInterfaces()) {
			if (hasParent((ClassSymbol) t.tsym, qualifiedNamesToFind)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Looks up a package element from its qualified name.
	 * 
	 * @return null if not found
	 */
	public static PackageSymbol getPackageByName(JSweetContext context, String qualifiedName) {
		return context.symtab.packages.get(context.names.fromString(qualifiedName));
	}

	/**
	 * Tells if the given method has varargs.
	 */
	public static boolean hasVarargs(MethodSymbol methodSymbol) {
		return methodSymbol != null && methodSymbol.getParameters().length() > 0 && (methodSymbol.flags() & Flags.VARARGS) != 0;
	}

	/**
	 * Tells if the given type is imported in the given compilation unit.
	 */
	public static boolean isImported(JCCompilationUnit compilationUnit, TypeSymbol type) {
		for (JCImport i : compilationUnit.getImports()) {
			if (i.isStatic()) {
				continue;
			}
			if (i.qualid.type.tsym == type) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tells if the given symbol is statically imported in the given compilation
	 * unit.
	 */
	public static TypeSymbol getStaticImportTarget(JCCompilationUnit compilationUnit, String name) {
		for (JCImport i : compilationUnit.getImports()) {
			if (!i.isStatic()) {
				continue;
			}
			if (!i.qualid.toString().endsWith("." + name)) {
				continue;
			}
			if (i.qualid instanceof JCFieldAccess) {
				JCFieldAccess qualified = (JCFieldAccess) i.qualid;
				if (qualified.selected instanceof JCFieldAccess) {
					qualified = (JCFieldAccess) qualified.selected;
				}
				if (qualified.sym instanceof TypeSymbol) {
					return (TypeSymbol) qualified.sym;
				}
			}
			return null;
		}
		return null;
	}

}
