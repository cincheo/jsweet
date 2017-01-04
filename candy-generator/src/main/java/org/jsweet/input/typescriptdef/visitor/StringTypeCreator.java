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
package org.jsweet.input.typescriptdef.visitor;

import static org.apache.commons.lang3.StringUtils.strip;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.QualifiedDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.TypedDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * Creates explict Java string types from TypeScript ones.
 * 
 * @author Renaud Pawlak
 */
public class StringTypeCreator extends Scanner {

	/**
	 * Dashes can be used in Javascript identifiers, and will be replaced by
	 * this string.
	 */
	public static final String DASH_STRING_IN_JAVA_IDENTIFIERS = "_";

	public static String toJavaStringType(String stringType) {
		String stripped = strip(stringType, "\"");
		if (Character.isDigit(stripped.charAt(0))) {
			stripped = "_" + stripped;
		}
		stripped = stripped.replaceAll("[-:.!,;]", DASH_STRING_IN_JAVA_IDENTIFIERS);
		if (JSweetDefTranslatorConfig.JAVA_KEYWORDS.contains(stripped)) {
			stripped = StringUtils.capitalize(stripped);
		}
		return stripped;
	}

	public StringTypeCreator(Context context) {
		super(context);
	}

	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		if (context.isInDependency(moduleDeclaration)) {
			logger.info("ignore StringType creation for dependency: "  + moduleDeclaration);
			return;
		}

		logger.info("enter StringType creation for: "  + moduleDeclaration);
		super.visitModuleDeclaration(moduleDeclaration);
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		super.visitTypeReference(typeReference);
		if (typeReference.isStringType()) {
			TypedDeclaration parentDeclaration = getParent(TypedDeclaration.class);

			String jsName = strip(typeReference.getName(), "['\"]");
			String javaName = toJavaStringType(typeReference.getName());
			TypeReference stringTypeReference = null;

			// try to get parent type reference
			FunctionDeclaration method = getParent(FunctionDeclaration.class, parentDeclaration);
			TypeDeclaration declaringType = getParent(TypeDeclaration.class, method);
			if (method != null && declaringType != null) {
				Pair<TypeDeclaration, FunctionDeclaration> parentMethod = findSuperMethod(declaringType, method);
				if (parentMethod != null) {
					String superMethodQualifiedName = context.getTypeName(parentMethod.getKey()) + "."
							+ parentMethod.getValue().getName();
					QualifiedDeclaration<TypedDeclaration> superDeclaration = context.findFirstDeclaration(
							TypedDeclaration.class, superMethodQualifiedName + "." + parentDeclaration.getName());

					logger.info("\n\n)))))) => " + superMethodQualifiedName + "." + parentDeclaration.getName() + " =>  "
							+ superDeclaration);

					stringTypeReference = superDeclaration.getDeclaration().getType();
				}
			}

			// if type is not resolved, we add it
			if (stringTypeReference == null) {
				stringTypeReference = addStringType(typeReference, jsName, javaName);
			}

			assert !stringTypeReference.isStringType() : "String type should be expanded by now";

			Util.substituteTypeReference(this, parentDeclaration, typeReference, stringTypeReference);
		}
	}

	private TypeReference addStringType(TypeReference typeReference, String jsName, String javaName) {

		String containerName = getCurrentContainerName();
		String libModule = context.getLibModule(containerName);

		logger.info("add string type '" + javaName + "' IN " + libModule + " for " + containerName);

		TypeDeclaration stringsContainer = getStringsDeclarationContainer(libModule);

		// 1. create string type
		TypeDeclaration newInterface = stringsContainer.findTypeIgnoreCase(javaName);
		if (newInterface == null) {
			newInterface = new TypeDeclaration(null, "interface", javaName, null, null, new Declaration[0]);
			newInterface.setDocumentation("/**\n" + " * Generated to type the string " + typeReference.getName() + ".\n"
					+ " * @exclude\n" + " */");
			stringsContainer.addMember(newInterface);
			context.registerType(JSweetDefTranslatorConfig.UTIL_PACKAGE + "."
					+ JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME + "." + newInterface.getName(),
					newInterface);
			if (jsName.equals(javaName)) {
				newInterface.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE);
			} else {
				newInterface
						.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE + "(\"" + jsName + "\")");
			}
		}

		// 2. create string type static variable
		VariableDeclaration var = null;
		if ((var = stringsContainer.findVariableIgnoreCase(javaName)) == null) {
			VariableDeclaration value = new VariableDeclaration(null, javaName, new TypeReference(
					null, JSweetDefTranslatorConfig.UTIL_PACKAGE + "."
							+ JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME + "." + newInterface.getName(),
					null), false, false);
			value.setDocumentation("/**\n" + " * Generated to type the string " + typeReference.getName() + ".\n"
					+ " * @exclude\n" + " */");
			value.addModifier("static");
			stringsContainer.addMember(value);
			if (jsName.equals(javaName)) {
				value.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE);
			} else {
				value.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE + "(\"" + jsName + "\")");
			}
		}

		String stringTypeFullName = context.getTypeName(stringsContainer) + "."
				+ (var == null ? javaName : var.getName());
		return new TypeReference(null, stringTypeFullName, null);
	}

	private TypeDeclaration getStringsDeclarationContainer(String libModule) {
		// string declarations are all merged in the same package / class:
		// jsweet util
		CompilationUnit destinationCompilUnit = context.getCompilationUnitForLibModule(libModule);
		ModuleDeclaration destinationModule = destinationCompilUnit.getMainModule();

		TypeDeclaration candidate = destinationModule.findType(JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME);
		if (candidate == null) {
			candidate = new TypeDeclaration(null, "interface", JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME,
					null, null, new Declaration[0]);
			candidate.addModifier("public");
			destinationModule.addMember(candidate);
			context.registerType(destinationModule + "." + JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME,
					candidate);
		}
		return candidate;
	}

}
