/* 
 * JSweet transpiler - http://www.jsweet.org
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
package org.jsweet.transpiler.util;

import static java.util.Arrays.asList;

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
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;

import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.code.Type.TypeVar;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCCatch;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLambda;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCUnary;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Name;

/**
 * Various utilities.
 * 
 * @author Renaud Pawlak
 */
public class Util {

	private final static Logger logger = Logger.getLogger(Util.class);

	private Util() {
	}

	private static long id = 121;

	/**
	 * Returns a unique id (incremental).
	 */
	public static long getId() {
		return id++;
	}

	/**
	 * Tells if the given element is within the Java sources being compiled.
	 */
	public static boolean isSourceElement(Element element) {
		if (element == null || element instanceof PackageSymbol) {
			return false;
		}
		if (element instanceof ClassSymbol) {
			ClassSymbol clazz = (ClassSymbol) element;
			// hack to know if it is a source file or a class file
			if (clazz.sourcefile != null
					&& clazz.sourcefile.getClass().getName().equals("com.sun.tools.javac.file.RegularFileObject")) {
				return true;
			}
		}
		return isSourceElement(element.getEnclosingElement());
	}

	/**
	 * Gets the source file path of the given element.
	 */
	public static String getSourceFilePath(Element element) {
		if (element == null || element instanceof PackageSymbol) {
			return null;
		}
		if (element instanceof ClassSymbol) {
			ClassSymbol clazz = (ClassSymbol) element;
			// hack to know if it is a source file or a class file
			if (clazz.sourcefile != null
					&& clazz.sourcefile.getClass().getName().equals("com.sun.tools.javac.file.RegularFileObject")) {
				return clazz.sourcefile.getName();
			}
		}
		return getSourceFilePath(element);
	}

	/**
	 * Gets the tree that corresponds to the given element (this is a slow
	 * implementation - do not use intensively).
	 * 
	 * @param context
	 *            the transpiler's context
	 * @param element
	 *            the element to lookup
	 * @return the javac AST that corresponds to that element
	 */
	public static JCTree lookupTree(JSweetContext context, Element element) {
		if (element == null || element instanceof PackageSymbol) {
			return null;
		}
		Element rootClass = getRootClassElement(element);
		if (rootClass instanceof ClassSymbol) {
			ClassSymbol clazz = (ClassSymbol) rootClass;
			// hack to know if it is a source file or a class file
			if (clazz.sourcefile != null
					&& clazz.sourcefile.getClass().getName().equals("com.sun.tools.javac.file.RegularFileObject")) {
				JCTree[] result = { null };
				for (int i = 0; i < context.sourceFiles.length; i++) {
					if (new File(clazz.sourcefile.getName()).equals(context.sourceFiles[i].getJavaFile())) {
						JCCompilationUnit cu = context.compilationUnits[i];
						new TreeScanner() {
							public void visitClassDef(JCClassDecl tree) {
								if (tree.sym == element) {
									result[0] = tree;
								} else {
									super.visitClassDef(tree);
								}
							}

							public void visitMethodDef(JCMethodDecl tree) {
								if (tree.sym == element) {
									result[0] = tree;
								}
							}

							public void visitVarDef(JCVariableDecl tree) {
								if (tree.sym == element) {
									result[0] = tree;
								} else {
									super.visitVarDef(tree);
								}
							}
						}.scan(cu);
						return result[0];
					}
				}
			}
		}
		return null;
	}

	private static Element getRootClassElement(Element element) {
		if (element == null) {
			return null;
		}
		if (element instanceof ClassSymbol
				&& (element.getEnclosingElement() == null || element.getEnclosingElement() instanceof PackageElement)) {
			return element;
		} else {
			return getRootClassElement(element.getEnclosingElement());
		}
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
	public static void addFiles(String extension, File file, Collection<File> files) {
		addFiles(f -> f.getName().endsWith(extension), file, files);
	}

	/**
	 * Recursively adds files to the given list.
	 * 
	 * @param filter
	 *            the filter predicate to apply (only files matching the predicate
	 *            will be added)
	 * @param file
	 *            the root file/directory to look into recursively
	 * @param files
	 *            the list to add the files matching the extension
	 */
	public static void addFiles(Predicate<File> filter, File file, Collection<File> files) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(filter, f, files);
			}
		} else if (filter.test(file)) {
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

	private static void putVar(Map<String, VarSymbol> vars, VarSymbol varSymbol) {
		vars.put(varSymbol.getSimpleName().toString(), varSymbol);
	}

	/**
	 * Finds all the variables accessible within the current scanning scope.
	 */
	public static void fillAllVariablesInScope(Map<String, VarSymbol> vars, Stack<JCTree> scanningStack, JCTree from,
			JCTree to) {
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
	 * Fills the given map with all the variables beeing accessed within the given
	 * code tree.
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
	 * Finds the method declaration within the given type, for the given invocation.
	 */
	public static MethodSymbol findMethodDeclarationInType(Types types, TypeSymbol typeSymbol,
			JCMethodInvocation invocation) {
		String meth = invocation.meth.toString();
		String methName = meth.substring(meth.lastIndexOf('.') + 1);
		return findMethodDeclarationInType(types, typeSymbol, methName, (MethodType) invocation.meth.type);
	}

	/**
	 * Finds the method in the given type that matches the given name and signature.
	 */
	public static MethodSymbol findMethodDeclarationInType(Types types, TypeSymbol typeSymbol, String methodName,
			MethodType methodType) {
		return findMethodDeclarationInType(types, typeSymbol, methodName, methodType, false);
	}

	/**
	 * Finds the method in the given type that matches the given name and signature.
	 */
	public static MethodSymbol findMethodDeclarationInType(Types types, TypeSymbol typeSymbol, String methodName,
			MethodType methodType, boolean overrides) {

		// gathers all the potential method matches
		List<MethodSymbol> candidates = new LinkedList<>();
		collectMatchingMethodDeclarationsInType(types, typeSymbol, methodName, methodType, overrides, candidates);

		// score them
		MethodSymbol bestMatch = null;
		int lastScore = Integer.MIN_VALUE;
		for (MethodSymbol candidate : candidates) {
			int currentScore = getCandidateMethodMatchScore(candidate, methodType, types);
			if (bestMatch == null || currentScore > lastScore) {
				bestMatch = candidate;
				lastScore = currentScore;
			}
		}

		// return the best match
		if (logger.isTraceEnabled()) {
			logger.trace("method declaration match for " + typeSymbol + "." + methodName + " - " + methodType + " : "
					+ bestMatch + " score=" + lastScore);
		}
		return bestMatch;
	}

	private static int getCandidateMethodMatchScore(MethodSymbol candidate, MethodType methodType, Types types) {

		if (methodType == null || candidate.getParameters().size() != methodType.argtypes.size()) {
			return -50;
		}
		
		int score = 0;
		
		boolean isAbstract = (candidate.flags() & Flags.ABSTRACT) != 0;
		if (isAbstract) {
			score -= 30;
		}
		for (int i = 0; i < candidate.getParameters().size(); i++) {
			Type candidateParamType = candidate.getParameters().get(i).type;
			Type paramType = methodType.argtypes.get(i);

			if (!candidateParamType.equals(paramType)) {
				score--;
			}
		}

		return score;
	}

	private static void collectMatchingMethodDeclarationsInType(Types types, TypeSymbol typeSymbol, String methodName,
			MethodType methodType, boolean overrides, List<MethodSymbol> collector) {
		if (typeSymbol == null) {
			return;
		}
		if (typeSymbol.getEnclosedElements() != null) {
			for (Element element : typeSymbol.getEnclosedElements()) {
				if ((element instanceof MethodSymbol) && (methodName.equals(element.getSimpleName().toString())
						|| ((MethodSymbol) element).getKind() == ElementKind.CONSTRUCTOR
								&& ("this".equals(methodName) /* || "super".equals(methodName) */))) {
					MethodSymbol methodSymbol = (MethodSymbol) element;
					if (methodType == null) {
						collector.add(methodSymbol);
					} else if (overrides ? isInvocable(types, methodSymbol.type.asMethodType(), methodType)
							: isInvocable(types, methodType, methodSymbol.type.asMethodType())) {
						collector.add(methodSymbol);
					}
				}
			}
		}
		if (typeSymbol instanceof ClassSymbol && ((ClassSymbol) typeSymbol).getSuperclass() != null) {
			if (!overrides || !Object.class.getName().equals(((ClassSymbol) typeSymbol).getSuperclass().toString())) {
				collectMatchingMethodDeclarationsInType(types, ((ClassSymbol) typeSymbol).getSuperclass().tsym,
						methodName, methodType, overrides, collector);
			}
		}
		if (typeSymbol instanceof ClassSymbol && ((ClassSymbol) typeSymbol).getInterfaces() != null) {
			for (Type t : ((ClassSymbol) typeSymbol).getInterfaces()) {
				collectMatchingMethodDeclarationsInType(types, t.tsym, methodName, methodType, overrides, collector);
			}
		}
	}

	/**
	 * Finds methods by name.
	 */
	public static void findMethodDeclarationsInType(TypeSymbol typeSymbol, Collection<String> methodNames,
			Set<String> ignoredTypeNames, List<MethodSymbol> result) {
		if (typeSymbol == null) {
			return;
		}
		if (ignoredTypeNames.contains(typeSymbol.getQualifiedName().toString())) {
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
			findMethodDeclarationsInType(((ClassSymbol) typeSymbol).getSuperclass().tsym, methodNames, ignoredTypeNames,
					result);
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
	 * Finds first method matching name (no super types lookup).
	 */
	public static MethodSymbol findFirstMethodDeclarationInType(Element typeSymbol, String methodName) {
		if (typeSymbol == null) {
			return null;
		}
		if (typeSymbol.getEnclosedElements() != null) {
			for (Element element : typeSymbol.getEnclosedElements()) {
				if ((element instanceof MethodSymbol) && (methodName.equals(element.getSimpleName().toString()))) {
					return (MethodSymbol) element;
				}
			}
		}
		return null;
	}

	public static boolean isDeprecated(Element element) {
		return ((Symbol) element).isDeprecated();
	}

	/**
	 * Find first declaration (of any kind) matching the given name.
	 */
	public static Symbol findFirstDeclarationInType(Element typeSymbol, String name) {
		if (typeSymbol == null) {
			return null;
		}
		if (typeSymbol.getEnclosedElements() != null) {
			for (Element element : typeSymbol.getEnclosedElements()) {
				if (name.equals(element.getSimpleName().toString())) {
					return (Symbol) element;
				}
			}
		}
		return null;
	}

	/**
	 * Find first declaration (of any kind) matching the given name.
	 */
	public static Symbol findFirstDeclarationInClassAndSuperClasses(TypeSymbol typeSymbol, String name,
			ElementKind kind) {
		if (typeSymbol == null) {
			return null;
		}
		if (typeSymbol.getEnclosedElements() != null) {
			for (Element element : typeSymbol.getEnclosedElements()) {
				if (name.equals(element.getSimpleName().toString()) && element.getKind() == kind) {
					return (Symbol) element;
				}
			}
		}
		if (typeSymbol instanceof ClassSymbol) {
			Symbol s = findFirstDeclarationInClassAndSuperClasses(((ClassSymbol) typeSymbol).getSuperclass().tsym, name,
					kind);
			if (s == null && kind == ElementKind.METHOD) {
				// also looks up in interfaces for methods
				for (Type type : ((ClassSymbol) typeSymbol).getInterfaces()) {
					s = findFirstDeclarationInClassAndSuperClasses(type.tsym, name, kind);
					if (s != null) {
						break;
					}
				}
			}
			return s;
		} else {
			return null;
		}
	}

	/**
	 * Scans member declarations in type hierachy.
	 */
	public static boolean scanMemberDeclarationsInType(TypeSymbol typeSymbol, Set<String> ignoredTypeNames,
			Function<Element, Boolean> scanner) {
		if (typeSymbol == null) {
			return true;
		}
		if (ignoredTypeNames.contains(typeSymbol.getQualifiedName().toString())) {
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
			if (!scanMemberDeclarationsInType(((ClassSymbol) typeSymbol).getSuperclass().tsym, ignoredTypeNames,
					scanner)) {
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
	 * Finds and returns the parameter matching the given name if any.
	 */
	public static JCVariableDecl findParameter(JCMethodDecl method, String name) {
		for (JCVariableDecl parameter : method.getParameters()) {
			if (name.equals(parameter.name.toString())) {
				return parameter;
			}
		}
		return null;
	}

	/**
	 * Tells if a method can be invoked with some given parameter types.
	 * 
	 * @param types
	 *            a reference to the types in the compilation scope
	 * @param from
	 *            the caller method signature to test (contains the parameter types)
	 * @param target
	 *            the callee method signature
	 * @return true if the callee can be invoked by the caller
	 */
	public static boolean isInvocable(Types types, MethodType from, MethodType target) {
		if (from.getParameterTypes().length() != target.getParameterTypes().length()) {
			return false;
		}
		for (int i = 0; i < from.getParameterTypes().length(); i++) {
			if (!types.isAssignable(types.erasure(from.getParameterTypes().get(i)),
					types.erasure(target.getParameterTypes().get(i)))) {
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
	 * Fills the given set with all the default methods found in the current type
	 * and super interfaces.
	 */
	public static void findDefaultMethodsInType(Set<Entry<JCClassDecl, JCMethodDecl>> defaultMethods,
			JSweetContext context, ClassSymbol classSymbol) {
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
		if (classSymbol == null) {
			return null;
		}
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
	 * Tells if this qualified name denotes a JSweet globals class.
	 */
	public static boolean isGlobalsClassName(String qualifiedName) {
		return qualifiedName != null && (JSweetConfig.GLOBALS_CLASS_NAME.equals(qualifiedName)
				|| qualifiedName.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME));
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
	public static com.sun.tools.javac.util.List<JavaFileObject> toJavaFileObjects(JavaFileManager fileManager,
			Collection<File> sourceFiles) throws IOException {
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
	 * This function will escape special characters within a string to ensure that
	 * the string will not be parsed as a regular expression. This is helpful with
	 * accepting using input that needs to be used in functions that take a regular
	 * expression as an argument (such as String.replaceAll(), or String.split()).
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
	 * Tells if the given directory or any of its sub-directory contains one of the
	 * given files.
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
	public static boolean isNumber(TypeMirror type) {
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
	 * Returns true is the type is a core.
	 */
	public static boolean isCoreType(TypeMirror type) {
		if (type == null) {
			return false;
		}
		if (String.class.getName().equals(type.toString())) {
			return true;
		}
		switch (type.getKind()) {
		case BYTE:
		case SHORT:
		case INT:
		case LONG:
		case DOUBLE:
		case FLOAT:
		case BOOLEAN:
		case CHAR:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns true is an arithmetic operator.
	 */
	public static boolean isArithmeticOrLogicalOperator(Kind kind) {
		switch (kind) {
		case MINUS:
		case PLUS:
		case MULTIPLY:
		case DIVIDE:
		case AND:
		case AND_ASSIGNMENT:
		case OR_ASSIGNMENT:
		case DIVIDE_ASSIGNMENT:
		case REMAINDER_ASSIGNMENT:
		case LEFT_SHIFT_ASSIGNMENT:
		case RIGHT_SHIFT_ASSIGNMENT:
		case MINUS_ASSIGNMENT:
		case MULTIPLY_ASSIGNMENT:
		case PLUS_ASSIGNMENT:
		case XOR_ASSIGNMENT:
		case LEFT_SHIFT:
		case RIGHT_SHIFT:
		case OR:
		case XOR:
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
		case EQUAL_TO:
		case NOT_EQUAL_TO:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Looks up a class in the given class hierarchy and returns true if found.
	 */
	public static boolean isParent(TypeSymbol type, TypeSymbol toFind) {
		if (!(type instanceof ClassSymbol)) {
			return false;
		}
		ClassSymbol clazz = (ClassSymbol) type;
		if (clazz.equals(toFind)) {
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
	 * Recursively looks up one of the given types in the type hierachy of the given
	 * class.
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
	 * Looks up a type element from its qualified name.
	 * 
	 * @return null if not found
	 */
	public static ClassSymbol getTypeByName(JSweetContext context, String qualifiedName) {
		return context.symtab.classes.get(context.names.fromString(qualifiedName));
	}

	/**
	 * Tells if the given method has varargs.
	 */
	public static boolean hasVarargs(MethodSymbol methodSymbol) {
		return methodSymbol != null && methodSymbol.getParameters().length() > 0
				&& (methodSymbol.flags() & Flags.VARARGS) != 0;
	}

	/**
	 * Tells if the method uses a type parameter.
	 */
	public static boolean hasTypeParameters(MethodSymbol methodSymbol) {
		if (methodSymbol != null && methodSymbol.getParameters().length() > 0) {
			for (VarSymbol p : methodSymbol.getParameters()) {
				if (p.type instanceof TypeVar) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tells if the given type is imported in the given compilation unit.
	 */
	public static boolean isImported(JCCompilationUnit compilationUnit, TypeSymbol type) {
		for (JCImport i : compilationUnit.getImports()) {
			if (i.isStatic() || i.qualid.type == null) {
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
		if (compilationUnit == null) {
			return null;
		}
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

	/**
	 * Gets the imported type (wether statically imported or not).
	 */
	public static TypeSymbol getImportedType(JCImport i) {
		if (!i.isStatic()) {
			return i.qualid.type == null ? null : i.qualid.type.tsym;
		} else {
			if (i.qualid instanceof JCFieldAccess) {
				JCFieldAccess qualified = (JCFieldAccess) i.qualid;
				if (qualified.selected instanceof JCFieldAccess) {
					qualified = (JCFieldAccess) qualified.selected;
				}
				if (qualified.sym instanceof TypeSymbol) {
					return (TypeSymbol) qualified.sym;
				}
			}
		}
		return null;
	}

	/**
	 * Tells if the given expression is a constant.
	 */
	public static boolean isConstant(JCExpression expr) {
		boolean constant = false;
		if (expr instanceof JCLiteral) {
			constant = true;
		} else if (expr instanceof JCFieldAccess) {
			if (((JCFieldAccess) expr).sym.isStatic()
					&& ((JCFieldAccess) expr).sym.getModifiers().contains(Modifier.FINAL)) {
				constant = true;
			}
		} else if (expr instanceof JCIdent) {
			if (((JCIdent) expr).sym.isStatic() && ((JCIdent) expr).sym.getModifiers().contains(Modifier.FINAL)) {
				constant = true;
			}
		}
		return constant;
	}

	/**
	 * Tells if that tree is the null literal.
	 */
	public static boolean isNullLiteral(JCTree tree) {
		return tree instanceof JCLiteral && ((JCLiteral) tree).getValue() == null;
	}

	/**
	 * Tells if that variable is a non-static final field initialized with a literal
	 * value.
	 */
	public static boolean isConstantOrNullField(JCVariableDecl var) {
		return !var.getModifiers().getFlags().contains(Modifier.STATIC) && (var.init == null
				|| var.getModifiers().getFlags().contains(Modifier.FINAL) && var.init instanceof JCLiteral);
	}

	/**
	 * Returns the literal for a given type inital value.
	 */
	public static String getTypeInitialValue(Type type) {
		if (type == null) {
			return "null";
		}
		if (isNumber(type)) {
			return "0";
		} else if (type.getKind() == TypeKind.BOOLEAN) {
			return "false";
		} else if (type.getKind() == TypeKind.VOID) {
			return null;
		} else {
			return "null";
		}
	}

	/**
	 * Gets the symbol on an access if exists/possible, or return null.
	 */
	public static Symbol getSymbol(JCTree tree) {
		if (tree instanceof JCFieldAccess) {
			return ((JCFieldAccess) tree).sym;
		} else if (tree instanceof JCIdent) {
			return ((JCIdent) tree).sym;
		}
		return null;
	}

	/**
	 * Returns true if the given class declares an abstract method.
	 */
	public static boolean hasAbstractMethod(ClassSymbol classSymbol) {
		for (Element member : classSymbol.getEnclosedElements()) {
			if (member instanceof MethodSymbol && ((MethodSymbol) member).getModifiers().contains(Modifier.ABSTRACT)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isOverridingBuiltInJavaObjectMethod(MethodSymbol method) {
		switch (method.toString()) {
		case "toString()":
		case "hashCode()":
		case "equals(java.lang.Object)":
			return true;
		}
		return false;
	}

	/**
	 * Gets the inheritance-based sorted class declarations.
	 * 
	 * <p>
	 * This method aims at overcoming TypeScrit limitation that forces a parent
	 * class to be declared before its child class (it is not the case in Java). So
	 * far this is a partial implementation that should cover most cases... for a
	 * 100% coverage we would need a much more complicated implementation that is
	 * probably not worth it.
	 */
	public static List<JCClassDecl> getSortedClassDeclarations(List<JCTree> decls) {
		// return (List<JCClassDecl>)(Object)decls;
		List<JCClassDecl> classDecls = decls.stream().filter(d -> d instanceof JCClassDecl).map(d -> (JCClassDecl) d)
				.collect(Collectors.toList());

		DirectedGraph<JCClassDecl> defs = new DirectedGraph<>();
		List<ClassSymbol> symbols = classDecls.stream().map(d -> ((JCClassDecl) d).sym).collect(Collectors.toList());
		defs.add(classDecls.toArray(new JCClassDecl[0]));
		for (int i = 0; i < symbols.size(); i++) {
			int superClassIndex = indexOfSuperclass(symbols, symbols.get(i)); // symbols.indexOf(symbols.get(i).getSuperclass().tsym);
			if (superClassIndex >= 0) {
				defs.addEdge(classDecls.get(superClassIndex), classDecls.get(i));
			}
		}
		// we assume no cycles are possible
		return defs.topologicalSort(null);
	}

	private static int indexOfSuperclass(List<ClassSymbol> symbols, ClassSymbol clazz) {
		int superClassIndex = symbols.indexOf(clazz.getSuperclass().tsym);
		// looks up also if any inner class extends a class in the list
		if (superClassIndex < 0) {
			for (Symbol s : clazz.getEnclosedElements()) {
				if (s instanceof ClassSymbol) {
					return indexOfSuperclass(symbols, ((ClassSymbol) s));
				}
			}
		}
		return superClassIndex;
	}

	/**
	 * Finds the first inner class declaration of the given name in the given class
	 * hierarchy.
	 */
	public static ClassSymbol findInnerClassDeclaration(ClassSymbol clazz, String name) {
		if (clazz == null) {
			return null;
		}
		for (Symbol s : clazz.getEnclosedElements()) {
			if (s instanceof ClassSymbol && s.getSimpleName().toString().equals(name)) {
				return (ClassSymbol) s;
			}
		}
		if (clazz.getSuperclass() != null) {
			return findInnerClassDeclaration((ClassSymbol) clazz.getSuperclass().tsym, name);
		}
		return null;
	}

	public static boolean isLiteralExpression(JCExpression expression) {
		if (expression == null) {
			return false;
		}
		
		if (expression instanceof JCLiteral) {
			return true;
		}
		
		if (expression instanceof JCBinary) {
			return isLiteralExpression(((JCBinary)expression).lhs)
					&& isLiteralExpression(((JCBinary)expression).rhs);
		}
		
		if (expression instanceof JCUnary) {
			return isLiteralExpression(((JCUnary)expression).arg);
		}
		
		return false;
	}

}
