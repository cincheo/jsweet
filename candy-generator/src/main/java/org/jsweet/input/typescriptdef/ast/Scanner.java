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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.jsweet.JSweetDefTranslatorConfig;

/**
 * The root scanner for visiting an AST of TypeScript definitions.
 * 
 * @author Renaud Pawlak
 */
public abstract class Scanner implements Visitor {

	private Stack<Visitable> stack = new Stack<Visitable>();

	protected final Logger logger = Logger.getLogger(getClass());

	protected Context context;

	public Scanner(Context context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public Scanner(Scanner parentScanner) {
		this.context = parentScanner.context;
		this.stack = (Stack<Visitable>) parentScanner.stack.clone();
	}

	public void onScanStart() {
	}

	public void onScanEnded() {
	}

	protected String getCurrentContainerName() {
		return getContainerNameAtIndex(0);
	}

	/**
	 * Gets the container name at the stack.size()-i level.
	 * 
	 * @param i
	 *            the reversed index (0=top of the stack)
	 */
	protected String getContainerNameAtIndex(int i) {
		List<String> modules = new ArrayList<String>();
		for (int j = 0; j < getStack().size() - i; j++) {
			Visitable v = getStack().get(j);
			if (v instanceof ModuleDeclaration) {
				modules.add(((ModuleDeclaration) v).getName());
			}
			if (v instanceof TypeDeclaration && !((TypeDeclaration) v).isAnonymous()) {
				modules.add(((TypeDeclaration) v).getName());
			}
		}
		return StringUtils.join(modules.iterator(), ".");
	}

	protected String getCurrentModuleName() {
		List<String> modules = new ArrayList<String>();
		for (Visitable v : getStack()) {
			if (v instanceof ModuleDeclaration) {
				modules.add(((ModuleDeclaration) v).getName());
			}
		}
		return StringUtils.join(modules.iterator(), ".");
	}

	/**
	 * Tells if the given declaration belongs to the current scanning stack.
	 */
	protected boolean isInScope(Declaration declaration) {
		boolean inScope = false;
		for (int i = 0; i < getStack().size(); i++) {
			if (getStack().get(i) == declaration) {
				inScope = true;
				break;
			}
		}
		return inScope;
	}

	protected String getCurrentDeclarationName() {
		StringBuffer sb = new StringBuffer();
		for (Visitable v : getStack()) {
			if (v instanceof Declaration) {
				sb.append(((Declaration) v).getName());
				sb.append('.');
			}
		}
		if (!getStack().isEmpty() && !(sb.length() == 0)) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	protected Visitable getRoot() {
		if (getStack().isEmpty()) {
			return null;
		}

		return getStack().get(0);
	}

	protected QualifiedDeclaration<TypeDeclaration> lookupTypeDeclaration(String name) {
		if (name == null) {
			return null;
		}
		// NOTE: it is possible to search using the context find methods... it
		// would be nicer but it may be slower, so we do it this way... to be
		// thought of
		Set<String> possibleNames = new LinkedHashSet<String>();
		String mainModuleName = "";
		if (getRoot() instanceof CompilationUnit) {
			mainModuleName = ((CompilationUnit) getRoot()).getMainModule().getName();
		}

		// lookup in current compilation unit
		for (int i = 0; i < getStack().size(); i++) {
			String containerName = getContainerNameAtIndex(i);
			String declFullName = StringUtils.isBlank(containerName) ? name : containerName + "." + name;
			if (declFullName.startsWith(mainModuleName)) {
				possibleNames.add(declFullName.substring(mainModuleName.length() + 1));
			}
			TypeDeclaration match = context.getTypeDeclaration(declFullName);
			if (match != null) {
				return new QualifiedDeclaration<>(match, declFullName);
			}
		}

		// lookup in all compilation units
		for (CompilationUnit compilUnit : context.compilationUnits) {
			for (String rootRelativeName : possibleNames) {
				TypeDeclaration match = context
						.getTypeDeclaration(compilUnit.getMainModule().getName() + "." + rootRelativeName);
				if (match != null) {
					return new QualifiedDeclaration<>(match,
							compilUnit.getMainModule().getName() + "." + rootRelativeName);
				}
			}
		}

		return null;
	}

	protected QualifiedDeclaration<ModuleDeclaration> lookupModuleDeclaration(String name) {
		Set<String> possibleNames = new LinkedHashSet<String>();
		String mainModuleName = "";
		if (getRoot() instanceof CompilationUnit) {
			mainModuleName = ((CompilationUnit) getRoot()).getMainModule().getName();
		}

		// lookup in current compilation unit
		for (int i = 0; i < getStack().size(); i++) {
			String containerName = getContainerNameAtIndex(i);
			String declFullName = StringUtils.isBlank(containerName) ? name : containerName + "." + name;

			if (declFullName.startsWith(mainModuleName)) {
				possibleNames.add(declFullName.substring(mainModuleName.length() + 1));
			}
			List<QualifiedDeclaration<ModuleDeclaration>> matches = context.findDeclarations(ModuleDeclaration.class,
					declFullName);
			for (QualifiedDeclaration<ModuleDeclaration> m : matches) {
				return m;
			}
		}

		// lookup in all compilation units
		for (CompilationUnit compilUnit : context.compilationUnits) {
			for (String rootRelativeName : possibleNames) {
				List<QualifiedDeclaration<ModuleDeclaration>> matches = context.findDeclarations(
						ModuleDeclaration.class, compilUnit.getMainModule().getName() + "." + rootRelativeName);
				for (QualifiedDeclaration<ModuleDeclaration> m : matches) {
					return m;
				}
			}
		}

		return null;
	}

	protected Type lookupType(TypeReference reference) {
		return lookupType(reference, null, false, false);
	}

	protected Type lookupType(TypeReference reference, String modName) {
		return lookupType(reference, modName, false, false);
	}

	protected boolean isFunctionalTypeReference(TypeReference typeReference) {
		Type t = lookupType(typeReference, null);
		if (t == null || !(t instanceof TypeDeclaration)) {
			return false;
		}
		TypeDeclaration td = (TypeDeclaration) t;
		if (context.getTypeName(td).startsWith(JSweetDefTranslatorConfig.FUNCTION_CLASSES_PACKAGE)
				|| context.getTypeName(td).startsWith("java.util.function")) {
			return true;
		}
		if (td.isAnnotationPresent(FunctionalInterface.class)) {
			return true;
		}
		return false;
	}

	protected boolean isSuperTypeReference(TypeReference typeReference) {
		if (!(getParent() instanceof TypeDeclaration)) {
			return false;
		}

		TypeDeclaration parentDeclaration = (TypeDeclaration) getParent();
		if (!ArrayUtils.contains(parentDeclaration.getSuperTypes(), typeReference)) {
			return false;
		}

		return true;
	}

	protected boolean isTypeArgumentTypeReference(TypeReference typeReference) {
		if (!(getParent() instanceof TypeReference)) {
			return false;
		}

		TypeReference parentReference = (TypeReference) getParent();
		if (!ArrayUtils.contains(parentReference.typeArguments, typeReference)) {
			return false;
		}

		return true;
	}

	protected TypeDeclaration extraLookup(TypeReference reference, String modName) {
		if (reference.getName() == null) {
			return null;
		}

		QualifiedDeclaration<TypeDeclaration> type = context.findFirstDeclaration(TypeDeclaration.class,
				reference.getName(), getParent(CompilationUnit.class));
		if (type != null) {
			return type.getDeclaration();
		}
		type = context.findFirstDeclaration(TypeDeclaration.class, "*." + reference.getName(),
				getParent(CompilationUnit.class));
		if (type != null) {
			return type.getDeclaration();
		}
		return null;

		// return lookupInLibModules(context.getLibModule(modName),
		// context.getLibRelativePath(modName), reference);
	}

	protected Type lookupType(TypeReference reference, String modName, boolean createIfNotFound,
			boolean verboseIfNotFound) {
		if (reference.getDeclaration() != null) {
			return reference.getDeclaration();
		}
		if (reference.isTypeOf()) {
			return null;
		}
		if (reference.isObjectType()) {
			reference.setDeclaration(reference.getObjectType());
			return reference.getObjectType();
		} else {
			// lookup in the global type repository
			if (modName == null) {
				modName = getCurrentModuleName();
			}
			TypeDeclaration t = context.getTypeDeclaration(modName + "." + reference.getName());
			if (t == null) {
				t = context.getTypeDeclaration(reference.getName());
				if (t == null) {
					String containerName = getCurrentContainerName();
					t = context.getTypeDeclaration(containerName + "." + reference.getName());
					if (t == null) {
						if (!JSweetDefTranslatorConfig.isJDKReplacementMode()) {
							t = context.getTypeDeclaration("java.lang." + reference.getName());
						}
						if (t == null) {
							t = context.getTypeDeclaration(
									JSweetDefTranslatorConfig.GLOBALS_PACKAGE_NAME + "." + reference.getName());
							if (t == null) {
								t = context.getTypeDeclaration(
										JSweetDefTranslatorConfig.LANG_PACKAGE + "." + reference.getName());
								if (t == null) {
									t = context.getTypeDeclaration(
											JSweetDefTranslatorConfig.DOM_PACKAGE + "." + reference.getName());
									if (t == null) {
										String[] subNames = modName.split("\\.");
										String partialModName;
										for (int i = subNames.length - 1; i > 0; i--) {
											String[] a = ArrayUtils.subarray(subNames, 0, i);
											partialModName = StringUtils.join(a, ".");
											t = context.getTypeDeclaration(partialModName + "." + reference.getName());
											if (t != null) {
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			// lookup in type parameters
			if (t == null) {
				TypeParameterizedElement tpe = getParent(TypeParameterizedElement.class, true);
				while (tpe != null) {
					if (tpe.getTypeParameters() != null) {
						for (TypeParameterDeclaration d : tpe.getTypeParameters()) {
							if (d.getName() != null && d.getName().equals(reference.getName())) {
								reference.setDeclaration(d);
								return d;
							}
						}
					}
					tpe = getParent(TypeParameterizedElement.class, tpe);
				}
			}

			if (t == null) {
				t = extraLookup(reference, modName);
			}

			if (t == null) {
				QualifiedDeclaration<TypeDeclaration> match = lookupTypeDeclaration(reference.getName());
				if (match != null) {
					t = match.getDeclaration();
				}
			}

			if (t == null) {
				if (createIfNotFound) {
					Token token = getCurrentToken();
					System.err.println("WARNING: creating unknown reference " + reference + " at "
							+ (token == null ? "" : token.getLocation()));
					String[] names = reference.getName().split("\\.");
					TypeDeclaration type = new TypeDeclaration(null, "class", names[names.length - 1], null, null,
							null);
					ModuleDeclaration module;
					if (names.length > 1) {
						module = new ModuleDeclaration(null,
								StringUtils.join(ArrayUtils.subarray(names, 0, names.length - 1), "."),
								new Declaration[] { type });
						context.registerModule(module.getName(), module);
					} else {
						module = new ModuleDeclaration(null, JSweetDefTranslatorConfig.GLOBALS_PACKAGE_NAME,
								new Declaration[] { type });
					}
					context.compilationUnits.get(0).addMember(module);
					context.registerType(module.getName() + "." + type.getName(), type);
				} else {
					if (verboseIfNotFound) {
						Token token = getCurrentToken();
						context.reportError("cannot find reference " + reference + " (" + getCurrentContainerName()
								+ "." + reference.getName() + ")", token);
					}
				}
			}

			reference.setDeclaration(t);
			return t;
		}
	}

	protected QualifiedDeclaration<FunctionDeclaration> lookupFunctionDeclaration(String name) {
		Set<String> possibleNames = new LinkedHashSet<String>();
		String mainModuleName = "";
		if (getRoot() instanceof CompilationUnit) {
			ModuleDeclaration mainModule = ((CompilationUnit) getRoot()).getMainModule();
			if (mainModule != null) {
				mainModuleName = mainModule.getName();
			}
		}

		// lookup in current compilation unit
		for (int i = 0; i < getStack().size(); i++) {
			String containerName = getContainerNameAtIndex(i);
			String declFullName = StringUtils.isBlank(containerName) ? name : containerName + "." + name;

			if (declFullName.startsWith(mainModuleName)) {
				possibleNames.add(declFullName.substring(mainModuleName.length() + 1));
			}
			List<QualifiedDeclaration<FunctionDeclaration>> matches = context
					.findDeclarations(FunctionDeclaration.class, declFullName);
			if (matches.size() > 0) {
				return matches.get(0);
			}
		}

		// lookup in all compilation units
		for (CompilationUnit compilUnit : context.compilationUnits) {
			if (compilUnit.getMainModule() == null) {
				continue;
			}
			for (String rootRelativeName : possibleNames) {
				List<QualifiedDeclaration<FunctionDeclaration>> matches = context.findDeclarations(
						FunctionDeclaration.class, compilUnit.getMainModule().getName() + "." + rootRelativeName);
				if (matches.size() > 0) {
					return matches.get(0);
				}
			}
		}

		return null;
	}

	protected QualifiedDeclaration<VariableDeclaration> lookupVariableDeclaration(String name) {
		Set<String> possibleNames = new LinkedHashSet<String>();
		String mainModuleName = "";
		if (getRoot() instanceof CompilationUnit) {
			ModuleDeclaration mainModule = ((CompilationUnit) getRoot()).getMainModule();
			if (mainModule != null) {
				mainModuleName = mainModule.getName();
			}
		}

		// lookup in current compilation unit
		for (int i = 0; i < getStack().size(); i++) {
			String containerName = getContainerNameAtIndex(i);
			String declFullName = StringUtils.isBlank(containerName) ? name : containerName + "." + name;

			if (declFullName.startsWith(mainModuleName)) {
				possibleNames.add(declFullName.substring(mainModuleName.length() + 1));
			}
			List<QualifiedDeclaration<VariableDeclaration>> matches = context
					.findDeclarations(VariableDeclaration.class, declFullName);
			if (matches.size() > 0) {
				return matches.get(0);
			}
		}

		// lookup in all compilation units
		for (CompilationUnit compilUnit : context.compilationUnits) {
			if (compilUnit.getMainModule() == null) {
				continue;
			}
			for (String rootRelativeName : possibleNames) {
				List<QualifiedDeclaration<VariableDeclaration>> matches = context.findDeclarations(
						VariableDeclaration.class, compilUnit.getMainModule().getName() + "." + rootRelativeName);
				if (matches.size() > 0) {
					return matches.get(0);
				}
			}
		}

		return null;
	}

	protected FunctionDeclaration lookupFunctionDeclaration(TypeReference typeReference, String name,
			TypeReference... argTypes) {
		TypeDeclaration type = (TypeDeclaration) lookupType(typeReference, null);
		if (type == null) {
			return null;
		}
		boolean found = false;
		for (Declaration d : type.getMembers()) {
			if (name.equals(d.getName())) {
				if (d instanceof FunctionDeclaration) {
					FunctionDeclaration function = (FunctionDeclaration) d;
					if (argTypes.length == function.getParameters().length) {
						found = true;
						for (int i = 0; i < argTypes.length; i++) {
							if (!argTypes[i].equals(function.getParameters()[i].getType())) {
								found = false;
							}
						}
						if (found) {
							return function;
						}
					}
				}
			}
		}
		if (type.getSuperTypes() != null) {
			for (TypeReference t : type.getSuperTypes()) {
				FunctionDeclaration f = lookupFunctionDeclaration(t, name, argTypes);
				if (f != null) {
					return f;
				}
			}
		}
		return null;
	}

	public void printStackTrace(PrintStream out) {
		out.println("Dumping scanner stack: " + this.getClass().getSimpleName() + " - " + stack.size());
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i) instanceof AstNode) {
				AstNode node = (AstNode) stack.get(i);
				out.println("   " + node.getClass().getSimpleName() + " - "
						+ (node.getToken() == null ? "N/A" : node.getToken() + " " + node.getToken().getLocation()));
			}
		}
	}

	public Token getCurrentToken() {
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i) instanceof AstNode) {
				AstNode node = (AstNode) stack.get(i);
				if (node.getToken() != null) {
					return node.getToken();
				}
			}
		}
		return null;
	}

	protected void enter(Visitable element) {
		if (!stack.isEmpty() && stack.peek() == element) {
			printStackTrace(System.err);
			logger.error("FATAL ERROR: duplicate entry: " + element);
			throw new RuntimeException("FATAL ERROR: duplicate entry: " + element);
		}
		stack.push(element);
	}

	protected void exit() {
		stack.pop();
	}

	public Stack<Visitable> getStack() {
		return this.stack;
	}

	/**
	 * Gets the current parent AST node from the stack.
	 */
	public Visitable getParent() {
		return this.stack.get(this.stack.size() - 2);
	}

	/**
	 * Gets the nth level parent AST node from the stack (getParent(1) ==
	 * getParent()).
	 */
	public Visitable getParent(int level) {
		return this.stack.get(this.stack.size() - (level + 1));
	}

	public <T extends Visitable> T getParent(Predicate<Visitable> predicate) {
		return getParent(predicate, false);
	}

	public <T extends Visitable> T getParent(Class<T> type) {
		return getParent(type, false);
	}

	@SuppressWarnings("unchecked")
	public <T extends Visitable> T getParent(Class<T> type, boolean includeCurrent) {
		for (int i = this.stack.size() - (includeCurrent ? 1 : 2); i >= 0; i--) {
			if (type.isAssignableFrom(this.stack.get(i).getClass())) {
				return (T) this.stack.get(i);
			}
		}
		return null;
	}

	public <T extends Visitable> List<T> getParents(Class<T> type) {
		List<T> parents = new ArrayList<T>();
		for (int i = this.stack.size() - 1; i >= 0; i--) {
			if (type.isAssignableFrom(this.stack.get(i).getClass())) {
				@SuppressWarnings("unchecked")
				T t = (T) this.stack.get(i);
				parents.add(t);
			}
		}
		return parents;
	}

	@SuppressWarnings("unchecked")
	public <T extends Visitable> List<T> getParents(Predicate<Visitable> predicate) {
		List<T> parents = new ArrayList<T>();
		for (int i = this.stack.size() - 1; i >= 0; i--) {
			if (predicate.test(this.stack.get(i))) {
				T t = (T) this.stack.get(i);
				parents.add(t);
			}
		}
		return parents;
	}

	@SuppressWarnings("unchecked")
	public <T extends Visitable> T getParent(Predicate<Visitable> predicate, boolean includeCurrent) {
		for (int i = this.stack.size() - (includeCurrent ? 1 : 2); i >= 0; i--) {
			if (predicate.test(this.stack.get(i))) {
				return (T) this.stack.get(i);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Visitable> T getParent(Class<T> type, Visitable from) {
		for (int i = this.stack.size() - 1; i >= 0; i--) {
			if (this.stack.get(i) == from) {
				for (int j = i - 1; j >= 0; j--) {
					if (type.isAssignableFrom(this.stack.get(j).getClass())) {
						return (T) this.stack.get(j);
					}
				}
				return null;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Visitable> T getParent(Predicate<Visitable> predicate, Visitable from) {
		for (int i = this.stack.size() - 1; i >= 0; i--) {
			if (this.stack.get(i) == from) {
				for (int j = i - 1; j >= 0; j--) {
					if (predicate.test(this.stack.get(j))) {
						return (T) this.stack.get(j);
					}
				}
				return null;
			}
		}
		return null;
	}

	public void scan(Visitable visitable) {
		if (visitable != null && !visitable.isHidden()) {
			enter(visitable);
			try {
				visitable.accept(this);
			} finally {
				exit();
			}
		}
	}

	public void scan(Visitable[] visitables) {
		if (visitables != null) {
			for (Visitable visitable : visitables) {
				scan(visitable);
			}
		}
	}

	public void scan(List<? extends Visitable> visitables) {
		if (visitables != null) {
			for (Visitable visitable : visitables) {
				scan(visitable);
			}
		}
	}

	@Override
	public void visitCompilationUnit(CompilationUnit compilationUnit) {
		scan(compilationUnit.getDeclarations());
	}

	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		scan(moduleDeclaration.getMembers());
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		scan(typeDeclaration.getTypeParameters());
		scan(typeDeclaration.getSuperTypes());
		scan(typeDeclaration.getMergedSuperTypes());
		scan(typeDeclaration.getMembers());
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		scan(functionDeclaration.getTypeParameters());
		scan(functionDeclaration.getType());
		scan(functionDeclaration.getParameters());
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
		scan(variableDeclaration.getType());
		scan(variableDeclaration.getInitializer());
	}

	@Override
	public void visitParameterDeclaration(ParameterDeclaration parameterDeclaration) {
		scan(parameterDeclaration.getType());
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		scan(typeReference.getObjectType());
		scan(typeReference.getTypeArguments());
	}

	@Override
	public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
		scan(typeMacroDeclaration.getTypeParameters());
		scan(typeMacroDeclaration.getType());
	}

	@Override
	public void visitFunctionalTypeReference(FunctionalTypeReference functionalTypeReference) {
		scan(functionalTypeReference.getReturnType());
		scan(functionalTypeReference.getParameters());
	}

	@Override
	public void visitArrayTypeReference(ArrayTypeReference arrayTypeReference) {
		scan(arrayTypeReference.getComponentType());
	}

	@Override
	public void visitUnionTypeReference(UnionTypeReference unionTypeReference) {
		switch (unionTypeReference.getSelected()) {
		case LEFT:
			scan(unionTypeReference.getLeftType());
			break;
		case RIGHT:
			scan(unionTypeReference.getRightType());
			break;
		default:
			scan(unionTypeReference.getLeftType());
			scan(unionTypeReference.getRightType());
		}
	}

	@Override
	public void visitTypeParameterDeclaration(TypeParameterDeclaration typeParameterDeclaration) {
		scan(typeParameterDeclaration.getUpperBound());
	}

	@Override
	public void visitLiteral(Literal literal) {
	}

	@Override
	public void visitReferenceDeclaration(ReferenceDeclaration referenceDeclaration) {
	}

	protected Pair<TypeDeclaration, FunctionDeclaration> findSuperMethod(TypeDeclaration declaringType,
			FunctionDeclaration method) {
		MutablePair<TypeDeclaration, FunctionDeclaration> superMethodInfos = new MutablePair<>();
		applyToSuperMethod(declaringType, method, (superType, superMethod) -> {
			superMethodInfos.setLeft(superType);
			superMethodInfos.setRight(superMethod);
		});
		
		return superMethodInfos.getRight() == null ? null : superMethodInfos;
	}

	protected void applyToSuperMethod(TypeDeclaration declaringType, FunctionDeclaration childFunction,
			BiConsumer<TypeDeclaration, FunctionDeclaration> apply) {
		applyToSuperMethod(declaringType, childFunction, declaringType, apply);
	}

	private void applyToSuperMethod(TypeDeclaration declaringType, FunctionDeclaration childFunction,
			TypeDeclaration parentType, BiConsumer<TypeDeclaration, FunctionDeclaration> apply) {
		int index = -1;
		if (declaringType != parentType) {
			index = ArrayUtils.indexOf(parentType.getMembers(), childFunction);
		}
		if (index != -1) {
			apply.accept(parentType, (FunctionDeclaration) parentType.getMembers()[index]);
		} else {
			if (parentType.getSuperTypes() != null && parentType.getSuperTypes().length > 0) {
				for (TypeReference ref : parentType.getSuperTypes()) {
					Type decl = lookupType(ref, null);
					if (decl instanceof TypeDeclaration) {
						applyToSuperMethod(declaringType, childFunction, (TypeDeclaration) decl, apply);
					}
				}
			} else if (!JSweetDefTranslatorConfig.getObjectClassName().equals(context.getTypeName(parentType))) {
				TypeDeclaration decl = context.getTypeDeclaration(JSweetDefTranslatorConfig.getObjectClassName());
				if (decl != null) {
					applyToSuperMethod(declaringType, childFunction, (TypeDeclaration) decl, apply);
				}
			}
		}
	}
}
