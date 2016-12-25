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

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * Generates tuple types on the fly if built in Tuple[2-6] is not enough
 * 
 * @author Renaud Pawlak
 *
 */
public class TupleTypeCreator extends Scanner {

	public TupleTypeCreator(Context context) {
		super(context);
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		if (typeReference.isTuple() && typeReference.getTypeArguments().length > 6) {
			getOrCreateUnionType(typeReference);
		}
		super.visitTypeReference(typeReference);
	}

	private TypeDeclaration getOrCreateUnionType(TypeReference reference) {
		CompilationUnit compilUnit = getParent(CompilationUnit.class);
		ModuleDeclaration targetModule = getOrCreateModule(compilUnit, JSweetDefTranslatorConfig.TUPLE_CLASSES_PACKAGE);

		String name = reference.getName();
		TypeDeclaration tupleType = targetModule.findType(name);
		if (tupleType == null) {
			tupleType = createTupleType(reference);
			targetModule.addMember(tupleType);
			context.registerType(name, tupleType);
		}

		return tupleType;
	}

	private ModuleDeclaration getOrCreateModule(DeclarationContainer container, String name) {
		ModuleDeclaration m = DeclarationHelper.findModule(container, name);
		if (m == null) {
			m = new ModuleDeclaration(null, name, new Declaration[0]);
			container.addMember(m);
			context.registerModule(name, m);
		}
		return m;
	}

	private TypeDeclaration createTupleType(TypeReference reference) {
		TypeParameterDeclaration[] typeParameters = new TypeParameterDeclaration[reference.getTypeArguments().length];
		for (int i = 0; i < reference.getTypeArguments().length; i++) {
			typeParameters[i] = new TypeParameterDeclaration(null, "T" + i);
		}

		TypeDeclaration tupleType = new TypeDeclaration(null, "class", reference.getSimpleName(), typeParameters, null,
				null);
		for (int i = 0; i < reference.getTypeArguments().length; i++) {
			tupleType.addMember(
					new VariableDeclaration(null, "$" + i, new TypeReference(null, typeParameters[i], null), false, false));
		}

		ParameterDeclaration[] parameters = new ParameterDeclaration[reference.getTypeArguments().length];
		for (int i = 0; i < reference.getTypeArguments().length; i++) {
			parameters[i] = new ParameterDeclaration(null, "$" + i, new TypeReference(null, typeParameters[i], null),
					false, false);
		}
		tupleType.addMember(
				new FunctionDeclaration(null, FunctionDeclaration.NEW_FUNCTION_RESERVED_NAME, null, parameters, null));

		tupleType.setDocumentation("/** This tuple type was automatically generated in order to accept "
				+ reference.getTypeArguments().length + " types. @see Tuple2 */");
		return tupleType;
	}

}
