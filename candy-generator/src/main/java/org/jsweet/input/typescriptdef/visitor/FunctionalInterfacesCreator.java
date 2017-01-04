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

import static org.jsweet.JSweetDefTranslatorConfig.FUNCTION_CLASSES_PACKAGE;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.ArrayTypeReference;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FunctionalTypeReference;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.TypedDeclaration;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * Subtitutes functional types by corresponding Java/JSweet types.
 * 
 * @author Renaud Pawlak
 */
public class FunctionalInterfacesCreator extends Scanner {

	public static final String RUNNABLE = Runnable.class.getName();
	public static final String SUPPLIER = Supplier.class.getName();
	public static final String FUNCTION = Function.class.getName();
	public static final String BI_FUNCTION = BiFunction.class.getName();
	public static final String TRI_FUNCTION = FUNCTION_CLASSES_PACKAGE + ".TriFunction";
	public static final String CONSUMER = Consumer.class.getName();
	public static final String BI_CONSUMER = BiConsumer.class.getName();;
	public static final String TRI_CONSUMER = FUNCTION_CLASSES_PACKAGE + ".TriConsumer";

	public FunctionalInterfacesCreator(Context context) {
		super(context);
	}

	@Override
	public void visitFunctionalTypeReference(FunctionalTypeReference functionalTypeReference) {
		super.visitFunctionalTypeReference(functionalTypeReference);
		substituteTypeParameters(functionalTypeReference);

		DeclarationContainer container = getParent(DeclarationContainer.class);
		if (container instanceof TypeDeclaration) {
			if ("interface".equals(((TypeDeclaration) container).getKind())) {
				container = getParent(DeclarationContainer.class, container);
			}
		}
		TypedDeclaration currentTypedElement = getParent(TypedDeclaration.class);
		ParameterDeclaration lastParam = functionalTypeReference.getParameters().length == 0 ? null
				: functionalTypeReference.getParameters()[functionalTypeReference.getParameters().length - 1];
		if (lastParam != null && lastParam.isVarargs()) {
			ParameterDeclaration[] parameters = DeclarationHelper.copy(functionalTypeReference.getParameters());
			parameters[parameters.length - 1]
					.setType(((ArrayTypeReference) parameters[parameters.length - 1].getType()).getComponentType());
			replaceFunctionnalType(container, currentTypedElement, functionalTypeReference, parameters,
					functionalTypeReference.getReturnType());
		} else {
			replaceFunctionnalType(container, currentTypedElement, functionalTypeReference,
					functionalTypeReference.getParameters(), functionalTypeReference.getReturnType());
		}
	}

	private void substituteTypeParameters(final FunctionalTypeReference functionalTypeReference) {
		if (functionalTypeReference.getTypeParameters() != null) {
			new Scanner(context) {
				@Override
				public void visitTypeReference(TypeReference typeReference) {
					Type t = lookupType(typeReference);
					if (t instanceof TypeParameterDeclaration) {
						typeReference.setDeclaration(null);
						TypeReference upperBound = ((TypeParameterDeclaration) t).getUpperBound();
						if (upperBound != null) {
							typeReference.setName(upperBound.getName());
							if (upperBound.getTypeArguments() != null) {
								typeReference.setTypeArguments(DeclarationHelper.copyReferences(upperBound
										.getTypeArguments()));
							}
						} else {
							typeReference.setName("any");
						}
					}
					super.visitTypeReference(typeReference);
				}
			}.scan(functionalTypeReference);
			;
		}
	}

	private void replaceFunctionnalType(DeclarationContainer container, TypedDeclaration currentTypedElement,
			TypeReference currentType, ParameterDeclaration[] parameters, TypeReference returnType) {
		if (parameters.length <= 3) {
			TypeReference functionType = new TypeReference(null, "unamed", null);
			if (!"void".equals(returnType.getName())) {
				switch (parameters.length) {
				case 0:
					functionType.setName(SUPPLIER);
					functionType.setTypeArguments(new TypeReference[] { returnType });
					break;
				case 1:
					functionType.setName(FUNCTION);
					functionType.setTypeArguments(new TypeReference[] { parameters[0].getType(), returnType });
					break;
				case 2:
					functionType.setName(BI_FUNCTION);
					functionType.setTypeArguments(new TypeReference[] { parameters[0].getType(),
							parameters[1].getType(), returnType });
					break;
				case 3:
					functionType.setName(TRI_FUNCTION);
					functionType.setTypeArguments(new TypeReference[] { parameters[0].getType(),
							parameters[1].getType(), parameters[2].getType(), returnType });
					break;
				}
			} else {
				switch (parameters.length) {
				case 0:
					functionType.setName(RUNNABLE);
					break;
				case 1:
					functionType.setName(CONSUMER);
					functionType.setTypeArguments(new TypeReference[] { parameters[0].getType() });
					break;
				case 2:
					functionType.setName(BI_CONSUMER);
					functionType.setTypeArguments(new TypeReference[] { parameters[0].getType(),
							parameters[1].getType() });
					break;
				case 3:
					functionType.setName(TRI_CONSUMER);
					functionType.setTypeArguments(new TypeReference[] { parameters[0].getType(),
							parameters[1].getType(), parameters[2].getType() });
					break;
				}
			}
			Util.substituteTypeReference(this, currentTypedElement, currentType, functionType);
		} else {
			// TODO: generate functions with more than 3 parameters in the
			// util.function package so that we don't loose override links.
			// At the moment we choose not to support functions with more than 3
			// parameteres

			TypeDeclaration t = getOrCreateFunctionalType(parameters, returnType);

			TypeReference[] typeArgs = new TypeReference[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				typeArgs[i] = parameters[i].getType();
			}
			if (!"void".equals(returnType.getName())) {
				typeArgs = ArrayUtils.add(typeArgs, returnType);
			}

			Util.substituteTypeReference(this, currentTypedElement, currentType, new TypeReference(null, t, typeArgs));

			// // recursively apply to the generated functional interface
			// scan(newInterface);
		}
	}

	private TypeDeclaration getOrCreateFunctionalType(ParameterDeclaration[] parameters, TypeReference returnType) {
		CompilationUnit cu = getParent(CompilationUnit.class);
		ModuleDeclaration m = getOrCreateModule(cu, JSweetDefTranslatorConfig.UTIL_PACKAGE);
		m = getOrCreateModule(m, "function");
		boolean hasResult = !"void".equals(returnType.getName());
		String name = (hasResult ? "Function" : "Consumer") + parameters.length;
		TypeDeclaration functionalType = m.findType(name);
		if (functionalType == null) {
			functionalType = DeclarationHelper.createFunctionalType(name, parameters.length, hasResult, false);
			m.addMember(functionalType);
			context.registerType(JSweetDefTranslatorConfig.UTIL_PACKAGE + ".function." + functionalType.getName(), functionalType);
			// recursively apply to the generated functional interface
			// scan(newInterface);
		}
		return functionalType;
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
