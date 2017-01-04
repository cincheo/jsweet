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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetDefTranslatorConfig;

/**
 * A helper class to factorize common functions on declarations.
 * 
 * @author Renaud Pawlak
 */
public abstract class DeclarationHelper {

	public static final Map<String, int[]> JAVA_OBJECT_METHODS = new HashMap<String, int[]>();
	// public static final Set<String> JAVA_FINAL_OBJECT_METHODS = new
	// HashSet<String>();
	// public static final Set<String> JAVA_OBJECT_METHOD_NAMES = new
	// HashSet<String>();
	public static final Set<String> JS_OBJECT_METHOD_NAMES = new HashSet<String>();
	public static final Set<String> JS_PRIMITIVE_TYPE_NAMES = new HashSet<String>();

	static {
		JS_OBJECT_METHOD_NAMES.add("toString");

		JS_PRIMITIVE_TYPE_NAMES.add("boolean");
		JS_PRIMITIVE_TYPE_NAMES.add("number");

		if (!JSweetDefTranslatorConfig.isJDKReplacementMode()) {
			// toString is common to Java and Javascript
			// JAVA_OBJECT_METHOD_NAMES.add("finalize");
			// JAVA_OBJECT_METHOD_NAMES.add("clone");
			// JAVA_OBJECT_METHOD_NAMES.add("equals");
			// JAVA_OBJECT_METHOD_NAMES.add("hashCode");
			// JAVA_OBJECT_METHOD_NAMES.add("notify");
			// JAVA_OBJECT_METHOD_NAMES.add("notifyAll");
			// JAVA_OBJECT_METHOD_NAMES.add("getClass");

			JAVA_OBJECT_METHODS.put("finalize", new int[] { 0 });
			JAVA_OBJECT_METHODS.put("clone", new int[] { 0 });
			JAVA_OBJECT_METHODS.put("equals", new int[] { 1 });
			JAVA_OBJECT_METHODS.put("hashCode", new int[] { 0 });
			JAVA_OBJECT_METHODS.put("notify", new int[] { 0 });
			JAVA_OBJECT_METHODS.put("notifyAll", new int[] { 0 });
			JAVA_OBJECT_METHODS.put("getClass", new int[] { 0 });
			JAVA_OBJECT_METHODS.put("wait", new int[] { 0, 1, 2 });

			// JAVA_FINAL_OBJECT_METHODS.add("notify():void");
			// JAVA_FINAL_OBJECT_METHODS.add("notifyAll():void");
			// JAVA_FINAL_OBJECT_METHODS.add("wait():void");
			// JAVA_FINAL_OBJECT_METHODS.add("wait(long):void");
			// JAVA_FINAL_OBJECT_METHODS.add("wait(long,int):void");
			// JAVA_FINAL_OBJECT_METHODS.add("getClass():java.lang.Class");
		}
	}

	public static boolean isPrimitiveType(String name) {
		return JS_PRIMITIVE_TYPE_NAMES.contains(name);
	}

	public static String getActualFunctionName(FunctionDeclaration function) {
		if (JAVA_OBJECT_METHODS.keySet().contains(function.getName())) {
			if (ArrayUtils.contains(JAVA_OBJECT_METHODS.get(function.getName()), function.getParameters().length)) {
				return StringUtils.capitalize(function.getName());
			}
		}
		return function.getName();
	}

	public static String toJavaIdentifier(String identifier) {
		if (JSweetDefTranslatorConfig.JAVA_KEYWORDS.contains(identifier)) {
			return StringUtils.capitalize(identifier);
		}
		if (JSweetDefTranslatorConfig.JAVA_TS_KEYWORDS.contains(identifier)) {
			return StringUtils.capitalize(identifier);
		}

		// TODO: this is not what happens... it must have been transformed
		// before
		if (Character.isDigit(identifier.charAt(0))) {
			return "$$" + identifier.charAt(0) + "$$" + identifier.substring(1);
		}
		return identifier;
	}

	public static Declaration[] addMember(DeclarationContainer container, Declaration declaration) {
		return ArrayUtils.add(container.getMembers(), declaration);
	}

	public static Declaration[] replaceMember(DeclarationContainer container, Declaration existingDeclaration,
			Declaration withNewDeclaration) {
		int index = ArrayUtils.indexOf(container.getMembers(), existingDeclaration);
		if (index >= 0) {
			container.getMembers()[index] = withNewDeclaration;
		}
		return container.getMembers();
	}

	public static Declaration[] removeMember(DeclarationContainer container, Declaration declaration) {
		int index = ArrayUtils.indexOf(container.getMembers(), declaration);
		if (index < 0) {
			return container.getMembers();
		}
		return ArrayUtils.remove(container.getMembers(), index);
	}

	public static List<FunctionDeclaration> findConstructors(TypeDeclaration typeDeclaration) {
		if (typeDeclaration.getMembers() == null) {
			return null;
		}
		List<FunctionDeclaration> constructors = new ArrayList<FunctionDeclaration>();
		for (Declaration m : typeDeclaration.getMembers()) {
			if (m instanceof FunctionDeclaration) {
				if (((FunctionDeclaration) m).isConstructor()) {
					constructors.add((FunctionDeclaration) m);
				}
			}
		}
		return constructors;
	}

	public static FunctionDeclaration findFirstConstructor(TypeDeclaration typeDeclaration) {
		if (typeDeclaration.getMembers() == null) {
			return null;
		}
		for (Declaration m : typeDeclaration.getMembers()) {
			if (m instanceof FunctionDeclaration) {
				if (((FunctionDeclaration) m).isConstructor()) {
					return (FunctionDeclaration) m;
				}
			}
		}
		return null;
	}

	public static boolean isStatic(TypeDeclaration typeDeclaration) {
		if (typeDeclaration.getMembers() == null) {
			return true;
		}
		if (!typeDeclaration.isInterface()) {
			return false;
		}
		for (Declaration m : typeDeclaration.getMembers()) {
			if (!m.hasModifier("static")) {
				return false;
			}
		}
		return true;
	}

	public static TypeReference[] toTypeArguments(TypeParameterDeclaration[] typeParameterDeclarations) {
		if (typeParameterDeclarations == null) {
			return null;
		}
		TypeReference[] args = new TypeReference[typeParameterDeclarations.length];
		for (int i = 0; i < typeParameterDeclarations.length; i++) {
			args[i] = new TypeReference(null, typeParameterDeclarations[i].getName(), null);
		}
		return args;
	}

	public static List<FunctionDeclaration> findFunctions(DeclarationContainer container, String name) {
		if (container.getMembers() == null) {
			return null;
		}
		List<FunctionDeclaration> functions = new ArrayList<FunctionDeclaration>();
		for (Declaration m : container.getMembers()) {
			if (m instanceof FunctionDeclaration) {
				if (((FunctionDeclaration) m).getName().equals(name)) {
					functions.add((FunctionDeclaration) m);
				}
			}
		}
		return functions;
	}

	public static FunctionDeclaration findFirstFunction(DeclarationContainer container, String name) {
		if (container.getMembers() == null) {
			return null;
		}
		for (Declaration m : container.getMembers()) {
			if (m instanceof FunctionDeclaration) {
				if (((FunctionDeclaration) m).getName().equals(name)) {
					return (FunctionDeclaration) m;
				}
			}
		}
		return null;
	}

	public static VariableDeclaration findVariable(DeclarationContainer container, String name) {
		if (container.getMembers() == null) {
			return null;
		}
		for (Declaration m : container.getMembers()) {
			if (m instanceof VariableDeclaration) {
				if (((VariableDeclaration) m).getName().equals(name)) {
					return (VariableDeclaration) m;
				}
			}
		}
		return null;
	}

	public static VariableDeclaration findVariableIgnoreCase(DeclarationContainer container, String name) {
		if (container.getMembers() == null) {
			return null;
		}
		for (Declaration m : container.getMembers()) {
			if (m instanceof VariableDeclaration) {
				if (((VariableDeclaration) m).getName().equalsIgnoreCase(name)) {
					return (VariableDeclaration) m;
				}
			}
		}
		return null;
	}

	public static TypeDeclaration findType(DeclarationContainer container, String name) {
		if (container.getMembers() == null) {
			return null;
		}
		for (Declaration m : container.getMembers()) {
			if (m instanceof TypeDeclaration) {
				if (((TypeDeclaration) m).getName().equals(name)) {
					return (TypeDeclaration) m;
				}
			}
		}
		return null;
	}

	public static TypeDeclaration findTypeIgnoreCase(DeclarationContainer container, String name) {
		if (container.getMembers() == null) {
			return null;
		}
		for (Declaration m : container.getMembers()) {
			if (m instanceof TypeDeclaration) {
				if (((TypeDeclaration) m).getName().equalsIgnoreCase(name)) {
					return (TypeDeclaration) m;
				}
			}
		}
		return null;
	}

	public static ModuleDeclaration findModule(DeclarationContainer container, String name) {
		if (container.getMembers() == null) {
			return null;
		}
		for (Declaration m : container.getMembers()) {
			if (m instanceof ModuleDeclaration) {
				if (((ModuleDeclaration) m).getName().equals(name)) {
					return (ModuleDeclaration) m;
				}
			}
		}
		return null;
	}
	
	public static ModuleDeclaration getOrCreateModule(DeclarationContainer container, String name) {
		ModuleDeclaration m = findModule(container, name);
		if (m == null) {
			m = new ModuleDeclaration(null, name, new Declaration[0]);
			container.addMember(m);
		}
		return m;
	}

	public static Declaration findDeclaration(DeclarationContainer container, String name) {
		if (container.getMembers() == null) {
			return null;
		}
		for (Declaration d : container.getMembers()) {
			if (d.getName().equals(name)) {
				return d;
			}
		}
		return null;
	}

	public static Declaration findDeclaration(DeclarationContainer container, Declaration declaration) {
		if (container.getMembers() == null) {
			return null;
		}
		int i = ArrayUtils.indexOf(container.getMembers(), declaration);
		if (i < 0) {
			return null;
		} else {
			return container.getMembers()[i];
		}
	}

	public static Declaration[] findAllVisibleDeclarations(DeclarationContainer container, Declaration declaration) {
		Declaration[] result = {};
		if (container.getMembers() == null) {
			return result;
		}
		for (Declaration d : container.getMembers()) {
			if (!d.isHidden() && d.equals(declaration)) {
				result = ArrayUtils.add(result, d);
			}
		}
		return result;
	}

	public static void addMembers(DeclarationContainer targetContainer, Declaration[] declarations) {
		for (Declaration d : declarations) {
			if (findDeclaration(targetContainer, d) == null) {
				targetContainer.addMember(d);
			}
		}
	}

	public static boolean areDeclarationsEqual(Declaration[] declarations1, Declaration[] declarations2) {
		for (Declaration d : declarations1) {
			if (!ArrayUtils.contains(declarations2, d)) {
				return false;
			}
		}
		for (Declaration d : declarations2) {
			if (!ArrayUtils.contains(declarations1, d)) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Declaration> T[] copy(T[] declarations) {
		if (declarations == null) {
			return null;
		}
		T[] to = Arrays.copyOf(declarations, declarations.length);
		for (int i = 0; i < declarations.length; i++) {
			to[i] = (T) declarations[i].copy();
		}
		return (T[]) to;
	}

	public static <T extends TypeReference> T[] copyReferences(T[] references) {
		return copyReferences(references, false);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TypeReference> T[] copyReferences(T[] references, boolean copyDeclarations) {
		if (references == null) {
			return null;
		}
		T[] to = Arrays.copyOf(references, references.length);
		for (int i = 0; i < references.length; i++) {
			to[i] = (T) references[i].copy(copyDeclarations);
		}
		return (T[]) to;
	}

	public static <T extends Type> TypeReference[] toReferences(T[] types) {
		if (types == null) {
			return null;
		}
		TypeReference[] to = new TypeReference[types.length];
		for (int i = 0; i < types.length; i++) {
			to[i] = new TypeReference(null, types[i], null);
		}
		return to;
	}

	public static TypeDeclaration createFunctionalType(String name, int parameterCount, boolean hasResult,
			boolean disambiguation) {
		TypeDeclaration functionalType = null;
		TypeParameterDeclaration[] typeParameters = new TypeParameterDeclaration[parameterCount];
		for (int i = 0; i < parameterCount; i++) {
			typeParameters[i] = new TypeParameterDeclaration(null, "T" + (i + 1));
		}
		TypeParameterDeclaration resultType = new TypeParameterDeclaration(null, "R");
		ParameterDeclaration[] parameters = new ParameterDeclaration[typeParameters.length];
		for (int i = 0; i < typeParameters.length; i++) {
			parameters[i] = new ParameterDeclaration(null, "p" + (i + 1),
					new TypeReference(null, typeParameters[i], null), false, false);
		}
		FunctionDeclaration newFunction = new FunctionDeclaration(null,
				JSweetDefTranslatorConfig.ANONYMOUS_FUNCTION_NAME,
				hasResult ? new TypeReference(null, resultType, null) : new TypeReference(null, "void", null),
				parameters, null);

		if (hasResult) {
			typeParameters = ArrayUtils.add(typeParameters, resultType);
		}
		functionalType = new TypeDeclaration(null, "interface", name, typeParameters, null,
				new FunctionDeclaration[] { newFunction });
		functionalType.addAnnotation(new FunctionalInterface() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return FunctionalInterface.class;
			}
		});
		if (disambiguation) {
			functionalType.setDocumentation(
					"/** This functional interface should be used for disambiguating lambdas in function parameters (by casting to this interface)."
							+ "<p>It was automatically generated for functions (taking lambdas) that lead to the same erased signature. */");
		} else {
			functionalType.setDocumentation(
					"/** This functional interface was automatically generated for allowing lambdas taking "
							+ parameterCount + " parameters " + (hasResult ? " and returning a result." : ".") + " */");
		}
		return functionalType;
	}

	public static Type getTypeOrComponentType(TypeReference typeReference) {
		if (typeReference.isArray()) {
			return getTypeOrComponentType(typeReference.getComponentType());
		} else {
			return typeReference.getDeclaration();
		}
	}

}
