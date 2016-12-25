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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * @author Renaud Pawlak
 */
public class FieldTypeFunctionInjector extends Scanner {

	public FieldTypeFunctionInjector(Context context) {
		super(context);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {

		if ("constructor".equals(variableDeclaration.getName())
				|| JSweetDefTranslatorConfig.ANONYMOUS_FUNCTION_NAME.equals(variableDeclaration.getName())
				|| JSweetDefTranslatorConfig.NEW_FUNCTION_NAME.equals(variableDeclaration.getName())) {
			return;
		}

		Type type = lookupType(variableDeclaration.getType());
		boolean candidate = (type instanceof TypeDeclaration) && (getParent() instanceof TypeDeclaration);

		if (candidate) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) type;
			TypeDeclaration parentTypeDeclaration = (TypeDeclaration) getParent();

			if ("Function".equals(typeDeclaration.getName())) {
				String typeName = context.getTypeName(typeDeclaration);
				if ((JSweetDefTranslatorConfig.LANG_PACKAGE + ".Function").equals(typeName)
						|| "Function".equals(context.getLibRelativePath(context.getTypeName(typeDeclaration)))) {
					FunctionDeclaration newFunction = new FunctionDeclaration(null, variableDeclaration.getName(),
							new TypeReference(null, Object.class.getName(), null),
							new ParameterDeclaration[] { new ParameterDeclaration(null, "args",
									new TypeReference(null, Object.class.getName(), null), false, true) },
							null);
					if (variableDeclaration.hasModifier("static")) {
						newFunction.addModifier("static");
					}
					if (variableDeclaration.getStringAnnotations() != null) {
						newFunction.setStringAnnotations(
								new ArrayList<String>(variableDeclaration.getStringAnnotations()));
					}
					parentTypeDeclaration.addMember(newFunction);
				}
				return;
			}

			for (Declaration d : typeDeclaration.getMembers()) {
				if (d instanceof FunctionDeclaration && ((FunctionDeclaration) d).isAnonymous()
						&& !((FunctionDeclaration) d).hasModifier("static")) {
					FunctionDeclaration anonymousFunction = (FunctionDeclaration) d;
					FunctionDeclaration newFunction = new FunctionDeclaration(null, variableDeclaration.getName(),
							anonymousFunction.getType().copy(),
							DeclarationHelper.copy(anonymousFunction.getParameters()),
							DeclarationHelper.copy(anonymousFunction.getTypeParameters()));
					newFunction.setDocumentation(anonymousFunction.getDocumentation());
					if (variableDeclaration.hasModifier("static")) {
						newFunction.addModifier("static");
					}
					if (variableDeclaration.getStringAnnotations() != null) {
						if (anonymousFunction.getStringAnnotations() != null) {
							List<String> annotations = new ArrayList<String>(
									variableDeclaration.getStringAnnotations());
							annotations.addAll(anonymousFunction.getStringAnnotations());
							newFunction.setStringAnnotations(annotations);
						} else {
							newFunction.setStringAnnotations(
									new ArrayList<String>(variableDeclaration.getStringAnnotations()));
						}
					}
					// expand type references in the
					// function since it is not always
					// copied in the same module
					new Scanner(this) {
						@Override
						public void visitTypeReference(TypeReference typeReference) {
							Type t = null;
							// created inner type case (object type for
							// instance)
							t = typeDeclaration.findType(typeReference.getName());
							if (t == null) {
								t = lookupType(typeReference, context.getTypeModule(typeDeclaration));
							}
							if (t instanceof TypeDeclaration) {
								TypeDeclaration typeDeclaration = (TypeDeclaration) t;
								String typeName = context.getTypeName(typeDeclaration);
								if (!typeName.equals(typeReference.getName())) {
									if (context.verbose) {
										logger.debug("WARNING: rewriting type ref " + typeReference.getName() + " -> "
												+ typeName + " at "
												+ (getCurrentToken() == null ? "?" : getCurrentToken().getLocation()));
									}
									typeReference.setName(typeName);
								}
							}
						}
					}.visitFunctionDeclaration(newFunction);
					// substitute type parameters
					TypeParameterDeclaration[] usedTypeParametersInType = Util.findTypeParameters(context,
							typeDeclaration, anonymousFunction);
					String[] usedTypeParametersInFunction = Util.findTypeParameterNames(context, anonymousFunction,
							anonymousFunction);

					if (usedTypeParametersInType.length > 0) {
						Map<String, TypeReference> typeMapping = new HashMap<>();
						for (TypeParameterDeclaration usedTypeParameter : usedTypeParametersInType) {
							if (ArrayUtils.contains(usedTypeParametersInFunction, usedTypeParameter.getName())) {
								// type parameter is hidden by local type
								// parameter
								continue;
							}
							int index = ArrayUtils.indexOf(typeDeclaration.getTypeParameters(), usedTypeParameter);
							if (index >= 0) {
								typeMapping.put(usedTypeParameter.getName(),
										variableDeclaration.getType().getTypeArguments()[index]);
							}
						}
						Util.subtituteTypeReferences(newFunction, typeMapping);
					}
					parentTypeDeclaration.addMember(newFunction);
				}
			}
		}

	}

}
