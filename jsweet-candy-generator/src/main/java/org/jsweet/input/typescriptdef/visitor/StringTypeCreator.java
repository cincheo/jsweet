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
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
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
	public void visitTypeReference(TypeReference typeReference) {
		if (typeReference.isStringType()) {
			TypedDeclaration parentDeclaration = getParent(TypedDeclaration.class);

			String stripped = strip(typeReference.getName(), "['\"]");
			String name = toJavaStringType(typeReference.getName());

			DeclarationContainer stringsContainer = getStringsDeclarationContainer();
			TypeDeclaration newInterface = stringsContainer.findTypeIgnoreCase(name);
			if (newInterface == null) {
				newInterface = new TypeDeclaration(null, "interface", name, null, null, new Declaration[0]);
				newInterface.setDocumentation("/**\n" + " * Generated to type the string " + typeReference.getName()
						+ ".\n" + " * @exclude\n" + " */");
				stringsContainer.addMember(newInterface);
				context.registerType(
						JSweetDefTranslatorConfig.UTIL_PACKAGE + "."
								+ JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME + "." + newInterface.getName(),
						newInterface);
				if (stripped.equals(name)) {
					newInterface.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE);
				} else {
					newInterface.addStringAnnotation(
							JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE + "(\"" + stripped + "\")");
				}
			}
			VariableDeclaration var = null;
			if ((var = stringsContainer.findVariableIgnoreCase(name)) == null) {
				VariableDeclaration value = new VariableDeclaration(null, name, new TypeReference(null,
						JSweetDefTranslatorConfig.UTIL_PACKAGE + "."
								+ JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME + "." + newInterface.getName(),
						null), false, false);
				value.setDocumentation("/**\n" + " * Generated to type the string " + typeReference.getName() + ".\n"
						+ " * @exclude\n" + " */");
				value.addModifier("static");
				stringsContainer.addMember(value);
				if (stripped.equals(name)) {
					value.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE);
				} else {
					value.addStringAnnotation(
							JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE + "(\"" + stripped + "\")");
				}
			}
			Util.substituteTypeReference(this, parentDeclaration, typeReference,
					new TypeReference(null,
							JSweetDefTranslatorConfig.UTIL_PACKAGE + "."
									+ JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME + "."
									+ (var == null ? name : var.getName()),
							null));

		}
		super.visitTypeReference(typeReference);
	}

	private DeclarationContainer getStringsDeclarationContainer() {
		// string declarations are all merged in the same package / class:
		// jsweet util
		CompilationUnit destinationCompilUnit = context.getTranslatedCompilationUnits().get(0);

		ModuleDeclaration destinationModule = getOrCreateModule(destinationCompilUnit,
				JSweetDefTranslatorConfig.UTIL_PACKAGE);
		String name = JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME;
		TypeDeclaration candidate = destinationModule.findType(name);
		if (candidate == null) {
			candidate = new TypeDeclaration(null, "interface", JSweetDefTranslatorConfig.STRING_TYPES_INTERFACE_NAME,
					null, null, new Declaration[0]);
			candidate.addModifier("public");
			destinationModule.addMember(candidate);
			context.registerType(destinationModule + "." + name, candidate);
		}
		return candidate;
	}

	private ModuleDeclaration getOrCreateModule(DeclarationContainer container, String name) {
		ModuleDeclaration m = DeclarationHelper.findModule(container, name);
		if (m == null) {
			m = new ModuleDeclaration(null, name, new Declaration[0]);
			container.addMember(m);
		}
		return m;
	}

}
