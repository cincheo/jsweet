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
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.SourcePosition;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PackageTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

/**
 * Various utilities.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class Util {

	private final static Logger logger = Logger.getLogger(Util.class);

	protected JSweetContext context;

	public Util(JSweetContext context) {
		this.context = context;
	}

	/**
	 * Gets the qualified name for the given type.
	 */
	public String getQualifiedName(TypeMirror type) {
		if (type instanceof DeclaredType) {
			Element e = ((DeclaredType) type).asElement();
			if (e instanceof TypeElement) {
				return ((TypeElement) e).getQualifiedName().toString();
			}
		}
		return type.toString();
	}

	/**
	 * Gets the type from an existing runtime class when possible (return null when
	 * the type cannot be found in the compiler's symbol table).
	 */
	public TypeMirror getType(Class<?> clazz) {
		TypeElement typeElement = getTypeElementByName(context, clazz.getName());
		return typeElement == null ? null : typeElement.asType();
	}

	private static long id = 121;

	/**
	 * Returns a unique id (incremental).
	 */
	public long getId() {
		return id++;
	}

	/**
	 * Tells if the given element is within the Java sources being compiled.
	 */
	public boolean isSourceElement(Element element) {
		if (element == null || element instanceof PackageTree) {
			return false;
		}
		if (element instanceof ClassTree) {
			ClassTree clazz = (ClassTree) element;
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
	public String getSourceFilePath(Element element) {
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
	 * @param context the transpiler's context
	 * @param element the element to lookup
	 * @return the javac AST that corresponds to that element
	 */
	public Tree lookupTree(JSweetContext context, Element element) {
		if (element == null || element instanceof PackageSymbol) {
			return null;
		}
		Element rootClass = getRootClassElement(element);
		if (rootClass instanceof ClassSymbol) {
			ClassSymbol clazz = (ClassSymbol) rootClass;
			// hack to know if it is a source file or a class file
			if (clazz.sourcefile != null
					&& clazz.sourcefile.getClass().getName().equals("com.sun.tools.javac.file.RegularFileObject")) {
				Tree[] result = { null };
				for (int i = 0; i < context.sourceFiles.length; i++) {
					if (new File(clazz.sourcefile.getName()).equals(context.sourceFiles[i].getJavaFile())) {
						CompilationUnitTree cu = context.compilationUnits[i];
						new TreeScanner() {
							public void visitClassDef(ClassTree tree) {
								if (tree.sym == element) {
									result[0] = tree;
								} else {
									super.visitClassDef(tree);
								}
							}

							public void visitMethodDef(MethodTree tree) {
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

	public static class Static {

		/**
		 * Recursively adds files to the given list.
		 * 
		 * @param extension the extension to filter with
		 * @param file      the root file/directory to look into recursively
		 * @param files     the list to add the files matching the extension
		 */
		public static void addFiles(String extension, File file, Collection<File> files) {
			addFiles(f -> f.getName().endsWith(extension), file, files);
		}

		/**
		 * Recursively adds files to the given list.
		 * 
		 * @param filter the filter predicate to apply (only files matching the
		 *               predicate will be added)
		 * @param file   the root file/directory to look into recursively
		 * @param files  the list to add the files matching the extension
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

		public static <T> T newInstance(String fullClassName) {
			String errorMessage = "cannot find or instantiate class: " + fullClassName
					+ " (make sure the class is in the plugin's classpath and that it defines an empty public constructor)";

			Class<T> clazz = null;
			try {
				clazz = (Class) Thread.currentThread().getContextClassLoader().loadClass(fullClassName);
			} catch (Exception e) {
				try {
					// try forName just in case
					clazz = (Class) Class.forName(fullClassName);
				} catch (Exception e2) {
					throw new RuntimeException(errorMessage, e2);
				}
			}

			try {
				Constructor<T> constructor = clazz.getConstructor();
				return constructor.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(errorMessage, e);
			}
		}
	}

	/**
	 * Recursively adds files to the given list.
	 * 
	 * @param extension the extension to filter with
	 * @param file      the root file/directory to look into recursively
	 * @param files     the list to add the files matching the extension
	 */
	public void addFiles(String extension, File file, Collection<File> files) {
		Static.addFiles(extension, file, files);
	}

	/**
	 * Recursively adds files to the given list.
	 * 
	 * @param filter the filter predicate to apply (only files matching the
	 *               predicate will be added)
	 * @param file   the root file/directory to look into recursively
	 * @param files  the list to add the files matching the extension
	 */
	public void addFiles(Predicate<File> filter, File file, Collection<File> files) {
		Static.addFiles(filter, file, files);
	}

	/**
	 * Gets the full signature of the given method.
	 */
	public String getFullMethodSignature(ExecutableElement method) {
		return method.getEnclosingElement().getQualifiedName() + "." + method.toString();
	}

	/**
	 * Tells if the given type declaration contains some method declarations.
	 */
	public boolean containsMethods(ClassTree classDeclaration) {
		for (Tree member : classDeclaration.getMembers()) {
			if (member instanceof MethodTree) {
				MethodTree method = (MethodTree) member;
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
	public void fillAllVariablesInScope(Map<String, VarSymbol> vars, Stack<Tree> scanningStack, Tree from, Tree to) {
		if (from == to) {
			return;
		}
		int i = scanningStack.indexOf(from);
		if (i == -1 || i == 0) {
			return;
		}
		Tree parent = scanningStack.get(i - 1);
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
			for (JCVariableDecl var : ((MethodTree) parent).params) {
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

	public List<ClassTree> findTypeDeclarationsInCompilationUnits(List<CompilationUnitTree> compilationUnits) {
		List<ClassTree> symbols = new LinkedList<>();
		for (CompilationUnitTree compilationUnit : compilationUnits) {
			for (Tree definition : compilationUnit.defs) {
				if (definition instanceof ClassTree) {
					symbols.add((ClassTree) definition);
				}
			}
		}

		return symbols;
	}

	public List<MethodTree> findMethodDeclarations(ClassTree typeDeclaration) {
		List<MethodTree> methods = new LinkedList<>();
		for (Tree definition : typeDeclaration.defs) {
			if (definition instanceof MethodTree) {
				methods.add((MethodTree) definition);
			}
		}

		return methods;
	}

	public MethodTree findFirstMethodDeclaration(ClassTree typeDeclaration, String methodName) {
		return findMethodDeclarations(typeDeclaration).stream() //
				.filter(methodDecl -> methodDecl.getName().toString().equals(methodName)) //
				.findFirst().orElse(null);
	}

	/**
	 * Fills the given map with all the variables beeing accessed within the given
	 * code tree.
	 */
	public void fillAllVariableAccesses(final Map<String, VarSymbol> vars, final Tree tree) {
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
	public ExecutableElement findMethodDeclarationInType(TypeElement typeSymbol, MethodInvocationTree invocation) {
		ExpressionTree method = invocation.getMethodSelect();
		String methName = method.toString().substring(method.toString().lastIndexOf('.') + 1);

		TypeMirror methodType = getTypeForTree(method, trees().getPath(typeSymbol).getCompilationUnit());

		return findMethodDeclarationInType(typeSymbol, methName, (ExecutableType) methodType);
	}

	/**
	 * Finds the method in the given type that matches the given name and signature.
	 */
	public ExecutableElement findMethodDeclarationInType(TypeElement typeSymbol, String methodName,
			ExecutableType methodType) {
		return findMethodDeclarationInType(typeSymbol, methodName, methodType, false);
	}

	/**
	 * Finds the method in the given type that matches the given name and signature.
	 */
	public ExecutableElement findMethodDeclarationInType(TypeElement typeSymbol, String methodName,
			ExecutableType methodType, boolean overrides) {

		// gathers all the potential method matches
		List<ExecutableElement> candidates = new LinkedList<>();
		collectMatchingMethodDeclarationsInType(typeSymbol, methodName, methodType, overrides, candidates);

		// score them
		ExecutableElement bestMatch = null;
		int lastScore = Integer.MIN_VALUE;
		for (ExecutableElement candidate : candidates) {
			int currentScore = getCandidateMethodMatchScore(candidate, methodType);
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

	private static int getCandidateMethodMatchScore(ExecutableElement candidate, ExecutableType methodType) {

		if (methodType == null || candidate.getParameters().size() != methodType.getParameterTypes().size()) {
			return -50;
		}

		int score = 0;

		boolean isAbstract = candidate.getModifiers().contains(Modifier.ABSTRACT);
		if (isAbstract) {
			score -= 30;
		}
		for (int i = 0; i < candidate.getParameters().size(); i++) {
			TypeMirror candidateParamType = candidate.getParameters().get(i).asType();
			TypeMirror paramType = methodType.getParameterTypes().get(i);

			if (!candidateParamType.equals(paramType)) {
				score--;
			}
		}

		return score;
	}

	private void collectMatchingMethodDeclarationsInType(TypeElement typeSymbol, String methodName,
			ExecutableType methodType, boolean overrides, List<ExecutableElement> collector) {
		if (typeSymbol == null) {
			return;
		}
		if (typeSymbol.getEnclosedElements() != null) {
			for (Element element : typeSymbol.getEnclosedElements()) {
				if ((element instanceof ExecutableElement) && (methodName.equals(element.getSimpleName().toString())
						|| ((ExecutableElement) element).getKind() == ElementKind.CONSTRUCTOR
								&& ("this".equals(methodName) /* || "super".equals(methodName) */))) {
					ExecutableElement methodSymbol = (ExecutableElement) element;
					if (methodType == null) {
						collector.add(methodSymbol);
					} else if (overrides ? isInvocable((ExecutableType) methodSymbol.asType(), methodType)
							: isInvocable(methodType, (ExecutableType) methodSymbol.asType())) {
						collector.add(methodSymbol);
					}
				}
			}
		}
		if (typeSymbol.getSuperclass() != null) {
			if (!overrides || !Object.class.getName().equals(typeSymbol.getSuperclass().toString())) {

				collectMatchingMethodDeclarationsInType((TypeElement) types().asElement(typeSymbol.getSuperclass()),
						methodName, methodType, overrides, collector);
			}
		}
		if (typeSymbol.getInterfaces() != null) {
			for (TypeMirror interfaceType : typeSymbol.getInterfaces()) {
				collectMatchingMethodDeclarationsInType((TypeElement) types().asElement(interfaceType), methodName,
						methodType, overrides, collector);
			}
		}
	}

	/**
	 * Finds methods by name.
	 */
	public void findMethodDeclarationsInType(TypeSymbol typeSymbol, Collection<String> methodNames,
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
	public MethodSymbol findFirstMethodDeclarationInType(Element typeSymbol, String methodName) {
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

	/**
	 * Tells if the given element is deprecated.
	 */
	public boolean isDeprecated(Element element) {
		return ((Symbol) element).isDeprecated();
	}

	/**
	 * Find first declaration (of any kind) matching the given name.
	 */
	public Symbol findFirstDeclarationInType(Element typeSymbol, String name) {
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
	 * @see #findFirstDeclarationInClassAndSuperClasses(TypeSymbol, String,
	 *      ElementKind, Integer)
	 */
	public Symbol findFirstDeclarationInClassAndSuperClasses(TypeSymbol typeSymbol, String name, ElementKind kind) {
		return findFirstDeclarationInClassAndSuperClasses(typeSymbol, name, kind, null);
	}

	/**
	 * Find first declaration (of any kind) matching the given name (and optionally
	 * the given number of arguments for methods)
	 */
	public Symbol findFirstDeclarationInClassAndSuperClasses(TypeSymbol typeSymbol, String name, ElementKind kind,
			Integer methodArgsCount) {
		if (typeSymbol == null) {
			return null;
		}
		if (typeSymbol.getEnclosedElements() != null) {
			for (Element element : typeSymbol.getEnclosedElements()) {
				if (name.equals(element.getSimpleName().toString()) && element.getKind() == kind
						&& (methodArgsCount == null
								|| methodArgsCount.equals(((ExecutableElement) element).getParameters().size()))) {
					return (Symbol) element;
				}
			}
		}
		if (typeSymbol instanceof ClassSymbol) {
			Symbol s = findFirstDeclarationInClassAndSuperClasses(((ClassSymbol) typeSymbol).getSuperclass().tsym, name,
					kind, methodArgsCount);
			if (s == null && kind == ElementKind.METHOD) {
				// also looks up in interfaces for methods
				for (Type type : ((ClassSymbol) typeSymbol).getInterfaces()) {
					s = findFirstDeclarationInClassAndSuperClasses(type.tsym, name, kind, methodArgsCount);
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
	public boolean scanMemberDeclarationsInType(TypeSymbol typeSymbol, Set<String> ignoredTypeNames,
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
	public JCVariableDecl findParameter(MethodTree method, String name) {
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
	 * @param types  a reference to the types in the compilation scope
	 * @param from   the caller method signature to test (contains the parameter
	 *               types)
	 * @param target the callee method signature
	 * @return true if the callee can be invoked by the caller
	 */
	public boolean isInvocable(ExecutableType from, ExecutableType target) {
		if (from.getParameterTypes().size() != target.getParameterTypes().size()) {
			return false;
		}
		for (int i = 0; i < from.getParameterTypes().size(); i++) {
			if (!types().isAssignable(types().erasure(from.getParameterTypes().get(i)),
					types().erasure(target.getParameterTypes().get(i)))) {
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
	public void findDefaultMethodsInType(Set<Entry<ClassTree, MethodTree>> defaultMethods, JSweetContext context,
			ClassSymbol classSymbol) {
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
	public VariableElement findFieldDeclaration(TypeElement classSymbol, Name name) {
		if (classSymbol == null) {
			return null;
		}

		for (Element member : classSymbol.getEnclosedElements()) {
			if (member instanceof VariableElement) {
				VariableElement field = (VariableElement) member;
				if (field.getSimpleName().toString().equals(name.toString())) {
					return field;
				}
			}
		}

		if (classSymbol.getSuperclass() != null) {
			Element superClassElement = types().asElement(classSymbol.getSuperclass());
			if (superClassElement instanceof TypeElement) {
				return findFieldDeclaration((TypeElement) superClassElement, name);
			}
		}

		return null;
	}

	/**
	 * Tells if this qualified name denotes a JSweet globals class.
	 */
	public boolean isGlobalsClassName(String qualifiedName) {
		return qualifiedName != null && (JSweetConfig.GLOBALS_CLASS_NAME.equals(qualifiedName)
				|| qualifiedName.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME));
	}

	/**
	 * Tells if this parameter declaration is varargs.
	 */
	public boolean isVarargs(JCVariableDecl varDecl) {
		return (varDecl.mods.flags & Flags.VARARGS) == Flags.VARARGS;
	}

	/**
	 * Gets the file from a Java file object.
	 */
	public File toFile(JavaFileObject javaFileObject) {
		return new File(javaFileObject.getName());
	}

	/**
	 * Transforms a list of source files to Java file objects (used by javac).
	 */
	public List<JavaFileObject> toJavaFileObjects(StandardJavaFileManager fileManager, Collection<File> sourceFiles) {
		Iterable<? extends JavaFileObject> javaFileObjectsIterable = fileManager
				.getJavaFileObjectsFromFiles(sourceFiles);
		return iterableToList(javaFileObjectsIterable);
	}

	public <T> List<T> iterableToList(Iterable<? extends T> iterable) {
		List<T> result = new ArrayList<T>();
		iterable.forEach(result::add);
		return result;
	}

	/**
	 * Transforms a source file to a Java file object (used by javac).
	 */
	public JavaFileObject toJavaFileObject(StandardJavaFileManager fileManager, File sourceFile) throws IOException {
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
	 * @param regex - argument which we wish to escape.
	 * @return - Resulting string with the following characters escaped:
	 *         [](){}+*^?$.\
	 */
	public String escapeRegex(final String regex) {
		Matcher match = REGEX_CHARS.matcher(regex);
		return match.replaceAll("\\\\$1");
	}

	/**
	 * Varargs to mutable list.
	 */
	@SafeVarargs
	public final <T> List<T> list(T... items) {
		ArrayList<T> list = new ArrayList<T>(items.length);
		for (T item : items) {
			list.add(item);
		}
		return list;
	}

	/**
	 * Tells if two type symbols are assignable.
	 */
	public boolean isAssignable(Types types, TypeSymbol to, TypeSymbol from) {
		if (to.equals(from)) {
			return true;
		} else {
			return types.isAssignable(from.asType(), to.asType());
		}
	}

	/**
	 * Tells if the given list contains an type which is assignable from type.
	 */
	public boolean containsAssignableType(Types types, List<Type> list, Type type) {
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
	 * @param fromSymbol the start path
	 * @param toSymbol   the end path
	 * @return the '/'-separated path
	 * @see #getRelativePath(String, String)
	 */
	public String getRelativePath(Symbol fromSymbol, Symbol toSymbol) {
		return getRelativePath("/" + fromSymbol.getQualifiedName().toString().replace('.', '/'),
				"/" + toSymbol.getQualifiedName().toString().replace('.', '/'));
	}

	/**
	 * Gets the relative path that links the two given paths.
	 * 
	 * <pre>
	 * assertEquals("../c", util.getRelativePath("/a/b", "/a/c"));
	 * assertEquals("..", util.getRelativePath("/a/b", "/a"));
	 * assertEquals("../e", util.getRelativePath("/a/b/c", "/a/b/e"));
	 * assertEquals("d", util.getRelativePath("/a/b/c", "/a/b/c/d"));
	 * assertEquals("d/e", util.getRelativePath("/a/b/c", "/a/b/c/d/e"));
	 * assertEquals("../../../d/e/f", util.getRelativePath("/a/b/c", "/d/e/f"));
	 * assertEquals("../..", util.getRelativePath("/a/b/c", "/a"));
	 * assertEquals("..", util.getRelativePath("/a/b/c", "/a/b"));
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
	 * @param fromPath the path to start from
	 * @param toPath   the path to reach
	 */
	public String getRelativePath(String fromPath, String toPath) {
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
	 * @param fileName the given file name (can contain path)
	 * @return the file name without the extension
	 */
	public String removeExtension(String fileName) {
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
	 * @param dir   the directory to look into
	 * @param files the files to be found
	 * @return true if one of the given files is found
	 */
	public boolean containsFile(File dir, File[] files) {
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
	public boolean isIntegral(Type type) {
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
	public boolean isNumber(TypeMirror type) {
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
	public boolean isCoreType(TypeMirror type) {
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
	 * Returns operator equivalent of given tree's kind. For instance, PLUS would
	 * return "+"
	 */
	public String toOperator(Kind kind) {
		switch (kind) {
		case MINUS:
		case UNARY_MINUS:
			return "-";
		case PLUS:
		case UNARY_PLUS:
			return "+";
		case MULTIPLY:
			return "*";
		case DIVIDE:
			return "/";
		case AND:
			return "&";
		case AND_ASSIGNMENT:
			return "&=";
		case OR_ASSIGNMENT:
			return "|=";
		case DIVIDE_ASSIGNMENT:
			return "/=";
		case REMAINDER_ASSIGNMENT:
			return "%=";
		case LEFT_SHIFT_ASSIGNMENT:
			return "<<=";
		case RIGHT_SHIFT_ASSIGNMENT:
			return ">>=";
		case MINUS_ASSIGNMENT:
			return "-=";
		case MULTIPLY_ASSIGNMENT:
			return "*=";
		case PLUS_ASSIGNMENT:
			return "+=";
		case XOR_ASSIGNMENT:
			return "^=";
		case LEFT_SHIFT:
			return "<<";
		case RIGHT_SHIFT:
			return ">>";
		case OR:
			return "|";
		case XOR:
			return "^";
		case ASSIGNMENT:
			return "=";
		case BITWISE_COMPLEMENT:
			return "~";
		case CONDITIONAL_AND:
			return "&&";
		case CONDITIONAL_OR:
			return "||";
		case EQUAL_TO:
			return "==";
		case GREATER_THAN:
			return ">";
		case GREATER_THAN_EQUAL:
			return ">=";
		case LESS_THAN:
			return "<";
		case LESS_THAN_EQUAL:
			return "<=";
		case LOGICAL_COMPLEMENT:
			return "!";
		case NOT_EQUAL_TO:
			return "!=";
		case POSTFIX_DECREMENT:
		case PREFIX_DECREMENT:
			return "--";
		case POSTFIX_INCREMENT:
		case PREFIX_INCREMENT:
			return "++";
		case REMAINDER:
			return "%";
		case UNSIGNED_RIGHT_SHIFT:
			return ">>>";
		case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
			return ">>>>=";
		}
	}

	/**
	 * Returns true is an arithmetic operator.
	 */
	public boolean isArithmeticOrLogicalOperator(Kind kind) {
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
	public boolean isArithmeticOperator(Kind kind) {
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
	public boolean isComparisonOperator(Kind kind) {
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
	public boolean isParent(TypeSymbol type, TypeSymbol toFind) {
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
	public boolean hasParent(ClassSymbol clazz, String... qualifiedNamesToFind) {
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
	public PackageElement getPackageByName(JSweetContext context, String qualifiedName) {
		return context.symtab.packages.get(context.names.fromString(qualifiedName));
	}
	
	public String getPackageFullNameForCompilationUnit(CompilationUnitTree compilationUnitTree) {
		return compilationUnitTree.getPackage().getPackageName().toString();
	}

	/**
	 * Looks up a type element from its qualified name.
	 * 
	 * @return null if not found
	 */
	public TypeElement getTypeElementByName(JSweetContext context, String qualifiedName) {
		return elements().getTypeElement(qualifiedName);
	}

	/**
	 * Tells if the given method has varargs.
	 */
	public boolean hasVarargs(ExecutableElement methodSymbol) {
		return methodSymbol != null && methodSymbol.getParameters().size() > 0 && methodSymbol.isVarArgs();
	}

	/**
	 * Tells if the method uses a type parameter.
	 */
	public boolean hasTypeParameters(ExecutableElement methodSymbol) {
		if (methodSymbol != null && methodSymbol.getParameters().size() > 0) {
			for (VariableElement p : methodSymbol.getParameters()) {
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
	public boolean isImported(CompilationUnitTree compilationUnit, TypeSymbol type) {
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
	public TypeSymbol getStaticImportTarget(CompilationUnitTree compilationUnit, String name) {
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
	public TypeSymbol getImportedType(JCImport i) {
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
	public boolean isConstant(ExpressionTree expr) {
		boolean constant = false;
		if (expr instanceof LiteralTree) {
			constant = true;
		} else if (expr instanceof MemberSelectTree) {
			if (((MemberSelectTree) expr).sym.isStatic()
					&& ((JCFieldAccess) expr).sym.getModifiers().contains(Modifier.FINAL)) {
				constant = true;
			}
		} else if (expr instanceof IdentifierTree) {
			if (((IdentifierTree) expr).sym.isStatic()
					&& ((IdentifierTree) expr).sym.getModifiers().contains(Modifier.FINAL)) {
				constant = true;
			}
		}
		return constant;
	}

	/**
	 * Tells if that tree is the null literal.
	 */
	public boolean isNullLiteral(Tree tree) {
		return tree instanceof JCLiteral && ((JCLiteral) tree).getValue() == null;
	}

	/**
	 * Tells if that variable is a non-static final field initialized with a literal
	 * value.
	 */
	public boolean isConstantOrNullField(JCVariableDecl var) {
		return !var.getModifiers().getFlags().contains(Modifier.STATIC) && (var.init == null
				|| var.getModifiers().getFlags().contains(Modifier.FINAL) && var.init instanceof JCLiteral);
	}

	/**
	 * Returns the literal for a given type inital value.
	 */
	public String getTypeInitialValue(Type type) {
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
	 * Gets the symbol on JCFieldAccess or JCIdent if possible, or return null.
	 * Could return either a MethodSymbol, or VariableSymbol
	 */
	public Symbol getAccessedSymbol(Tree tree) {
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
	public boolean hasAbstractMethod(ClassSymbol classSymbol) {
		for (Element member : classSymbol.getEnclosedElements()) {
			if (member instanceof MethodSymbol && ((MethodSymbol) member).getModifiers().contains(Modifier.ABSTRACT)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOverridingBuiltInJavaObjectMethod(MethodSymbol method) {
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
	public List<ClassTree> getSortedClassDeclarations(List<Tree> decls) {
		// return (List<ClassTree>)(Object)decls;
		List<ClassTree> classDecls = decls.stream().filter(d -> d instanceof ClassTree).map(d -> (ClassTree) d)
				.collect(Collectors.toList());

		DirectedGraph<ClassTree> defs = new DirectedGraph<>();
		List<ClassSymbol> symbols = classDecls.stream().map(d -> ((ClassTree) d).sym).collect(Collectors.toList());
		defs.add(classDecls.toArray(new ClassTree[0]));
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
	public TypeElement findInnerClassDeclaration(TypeElement clazz, String name) {
		if (clazz == null) {
			return null;
		}
		for (Element s : clazz.getEnclosedElements()) {
			if (s instanceof TypeElement && s.getSimpleName().toString().equals(name)) {
				return (TypeElement) s;
			}
		}
		if (clazz.getSuperclass() != null) {
			return findInnerClassDeclaration((TypeElement) clazz.getSuperclass(), name);
		}
		return null;
	}

	public boolean isLiteralExpression(ExpressionTree expression) {
		if (expression == null) {
			return false;
		}

		if (expression instanceof LiteralTree) {
			return true;
		}

		if (expression instanceof BinaryTree) {
			return isLiteralExpression(((BinaryTree) expression).getLeftOperand())
					&& isLiteralExpression(((BinaryTree) expression).getRightOperand());
		}

		if (expression instanceof UnaryTree) {
			return isLiteralExpression(((UnaryTree) expression).getExpression());
		}

		return false;
	}

	public List<List<Tree>> getExecutionPaths(MethodTree methodDeclaration) {

		List<List<Tree>> executionPaths = new LinkedList<>();
		executionPaths.add(new LinkedList<>());
		List<List<Tree>> currentPaths = new LinkedList<>(executionPaths);
		List<JCBreak> activeBreaks = new LinkedList<>();
		for (JCStatement statement : methodDeclaration.body.stats) {
			collectExecutionPaths(statement, executionPaths, currentPaths, activeBreaks);
		}

		return executionPaths;
	}

	private static void collectExecutionPaths(JCStatement currentNode, List<List<Tree>> allExecutionPaths,
			List<List<Tree>> currentPaths, List<JCBreak> activeBreaks) {

		for (List<Tree> currentPath : new ArrayList<>(currentPaths)) {
			Tree lastStatement = currentPath.isEmpty() ? null : currentPath.get(currentPath.size() - 1);
			if (lastStatement instanceof JCReturn
					|| (lastStatement instanceof JCBreak && activeBreaks.contains(lastStatement))) {
				continue;
			}

			if (allExecutionPaths.size() > 20000) {
				throw new RuntimeException("too many execution paths, aborting");
			}

			currentPath.add(currentNode);

			List<List<Tree>> currentPathForksList = pathsList(currentPath);
			if (currentNode instanceof JCIf) {
				JCIf ifNode = (JCIf) currentNode;

				boolean lastIsCurrentPath = true;
				JCStatement[] forks = { ifNode.thenpart, ifNode.elsepart };

				evaluateForksExecutionPaths(allExecutionPaths, currentPathForksList, currentPath, lastIsCurrentPath,
						activeBreaks, forks);

			} else if (currentNode instanceof JCBlock) {
				for (JCStatement statement : ((JCBlock) currentNode).stats) {
					collectExecutionPaths(statement, allExecutionPaths, currentPathForksList, activeBreaks);
				}
			} else if (currentNode instanceof JCLabeledStatement) {
				collectExecutionPaths(((JCLabeledStatement) currentNode).body, allExecutionPaths, currentPaths,
						activeBreaks);
			} else if (currentNode instanceof JCForLoop) {
				collectExecutionPaths(((JCForLoop) currentNode).body, allExecutionPaths, currentPaths, activeBreaks);

				activeBreaks.clear();
			} else if (currentNode instanceof JCEnhancedForLoop) {
				collectExecutionPaths(((JCEnhancedForLoop) currentNode).body, allExecutionPaths, currentPaths,
						activeBreaks);

				activeBreaks.clear();
			} else if (currentNode instanceof JCWhileLoop) {
				collectExecutionPaths(((JCWhileLoop) currentNode).body, allExecutionPaths, currentPaths, activeBreaks);

				activeBreaks.clear();
			} else if (currentNode instanceof JCDoWhileLoop) {
				collectExecutionPaths(((JCDoWhileLoop) currentNode).body, allExecutionPaths, currentPaths,
						activeBreaks);

				activeBreaks.clear();
			} else if (currentNode instanceof JCSynchronized) {
				collectExecutionPaths(((JCSynchronized) currentNode).body, allExecutionPaths, currentPaths,
						activeBreaks);
			} else if (currentNode instanceof JCTry) {
				JCTry tryNode = (JCTry) currentNode;
				collectExecutionPaths(tryNode.getBlock(), allExecutionPaths, currentPaths, activeBreaks);

				JCStatement[] catchForks = (JCStatement[]) tryNode.getCatches().stream().map(catchExp -> catchExp.body)
						.collect(toList()).toArray(new JCStatement[0]);

				evaluateForksExecutionPaths(allExecutionPaths, currentPathForksList, currentPath, false, activeBreaks,
						catchForks);
				if (tryNode.getFinallyBlock() != null) {
					collectExecutionPaths(tryNode.getFinallyBlock(), allExecutionPaths, currentPathForksList,
							activeBreaks);
				}
			} else if (currentNode instanceof JCSwitch) {
				JCSwitch switchNode = (JCSwitch) currentNode;
				evaluateForksExecutionPaths(allExecutionPaths, currentPathForksList, currentPath, true, activeBreaks,
						switchNode.cases.toArray(new JCCase[0]));

				activeBreaks.clear();
			} else if (currentNode instanceof JCCase) {
				for (JCStatement statement : ((JCCase) currentNode).stats) {
					collectExecutionPaths(statement, allExecutionPaths, currentPathForksList, activeBreaks);
				}
			}

			addAllWithoutDuplicates(currentPaths, currentPathForksList);
		}
	}

	/**
	 * @return generated paths
	 */
	private static void evaluateForksExecutionPaths( //
			List<List<Tree>> allExecutionPaths, //
			List<List<Tree>> currentPaths, //
			List<Tree> currentPath, //
			boolean lastIsCurrentPath, //
			List<JCBreak> activeBreaks, //
			JCStatement[] forks) {
		int i = 0;
		List<List<Tree>> generatedExecutionPaths = new LinkedList<>();
		for (JCStatement fork : forks) {
			if (fork != null) {
				List<List<Tree>> currentPathsForFork;
				if (lastIsCurrentPath && ++i == forks.length) {
					currentPathsForFork = pathsList(currentPath);
				} else {
					List<Tree> forkedPath = new LinkedList<>(currentPath);
					allExecutionPaths.add(forkedPath);
					currentPathsForFork = pathsList(forkedPath);
				}
				collectExecutionPaths(fork, allExecutionPaths, currentPathsForFork, activeBreaks);

				generatedExecutionPaths.addAll(currentPathsForFork);
			}
		}

		// we need to merge with paths created by fork
		addAllWithoutDuplicates(currentPaths, generatedExecutionPaths);
	}

	private static List<List<Tree>> addAllWithoutDuplicates(List<List<Tree>> pathsListUnique,
			List<List<Tree>> listToBeAdded) {
		for (List<Tree> path : listToBeAdded) {
			boolean pathAlreadyAdded = false;
			for (List<Tree> pathFromUnique : pathsListUnique) {
				if (path == pathFromUnique) {
					pathAlreadyAdded = true;
				}
			}
			if (!pathAlreadyAdded) {
				pathsListUnique.add(path);
			}
		}
		return pathsListUnique;
	}

	@SafeVarargs
	private static List<List<Tree>> pathsList(List<Tree>... executionPaths) {
		List<List<Tree>> pathsList = new LinkedList<>();
		for (List<Tree> path : executionPaths) {
			pathsList.add(path);
		}
		return pathsList;
	}

	public boolean isDeclarationOrSubClassDeclaration(javax.lang.model.util.Types types, ClassType classType,
			String searchedClassName) {

		while (classType != null) {
			if (classType.tsym.getQualifiedName().toString().equals(searchedClassName)) {
				return true;
			}
			List<? extends TypeMirror> superTypes = types.directSupertypes(classType);
			classType = superTypes == null || superTypes.isEmpty() ? null : (ClassType) superTypes.get(0);
		}

		return false;
	}

	public boolean isDeclarationOrSubClassDeclarationBySimpleName(javax.lang.model.util.Types types,
			ClassType classType, String searchedClassSimpleName) {

		while (classType != null) {
			if (classType.tsym.getSimpleName().toString().equals(searchedClassSimpleName)) {
				return true;
			}
			List<? extends TypeMirror> superTypes = types.directSupertypes(classType);
			classType = superTypes == null || superTypes.isEmpty() ? null : (ClassType) superTypes.get(0);
		}

		return false;
	}

	public SourcePosition getSourcePosition(CompilationUnitTree compilationUnit, Tree tree) {
		// map offsets to line numbers in source file
		LineMap lineMap = compilationUnit.getLineMap();
		if (lineMap == null) {
			return null;
		}
		// find offset of the specified AST node
		long startPosition = sourcePositions().getStartPosition(compilationUnit, tree);
		long endPosition = sourcePositions().getEndPosition(compilationUnit, tree);
		if (endPosition == -1) {
			endPosition = startPosition;
		}

		return new SourcePosition( //
				new File(compilationUnit.getSourceFile().getName()), //
				tree, //
				lineMap.getLineNumber(startPosition), lineMap.getColumnNumber(startPosition), //
				lineMap.getLineNumber(endPosition), lineMap.getColumnNumber(endPosition));
	}

	private SourcePositions sourcePositions() {
		return trees().getSourcePositions();
	}

	private Trees trees() {
		return context.trees;
	}

	private Types types() {
		return context.types;
	}

	private Elements elements() {
		return context.elements;
	}

	public <T extends Element> T getElementForTree(Tree tree, CompilationUnitTree compilationUnit) {
		return (T) trees().getElement(trees().getPath(compilationUnit, tree));
	}

	public TypeMirror getTypeForTree(Tree tree, CompilationUnitTree compilationUnit) {
		return getElementForTree(tree, compilationUnit).asType();
	}
}
