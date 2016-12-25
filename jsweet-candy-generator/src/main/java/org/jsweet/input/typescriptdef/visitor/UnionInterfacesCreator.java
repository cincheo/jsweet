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

import java.util.List;

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.TypedDeclaration;
import org.jsweet.input.typescriptdef.ast.UnionTypeReference;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * Generates union types on the fly if built in Union<T1, T2> for two types is
 * not enough
 * 
 * @author Louis Grignon
 *
 */
public class UnionInterfacesCreator extends Scanner {

	public UnionInterfacesCreator(Context context) {
		super(context);
	}

	@Override
	public void visitUnionTypeReference(UnionTypeReference unionTypeReference) {

		boolean substituted = false;
		List<TypeReference> possibleTypes = unionTypeReference.getTypes();
		if (possibleTypes.size() > 1) {
			logger.trace("replace union in " + getCurrentContainerName() + "." + getParent() + " types: "
					+ possibleTypes);
			TypeReference parameterizedUnion = getUnionTypeReference(possibleTypes);
			substituted = Util.substituteTypeReference(this, getParent(TypedDeclaration.class), unionTypeReference,
					parameterizedUnion);

			if (substituted) {
				scan(parameterizedUnion);
			} else {
				logger.warn("cannot replace: " + getCurrentContainerName() + "." + getParent() + " ===> union::"
						+ possibleTypes);
			}
		}

		if (!substituted) {
			super.visitUnionTypeReference(unionTypeReference);
		}
	}

	private TypeReference getUnionTypeReference(List<TypeReference> possibleTypes) {
		if (possibleTypes.size() > 2) {
			TypeDeclaration unionType = getOrCreateUnionType(possibleTypes);
			return new TypeReference(null, unionType, possibleTypes.toArray(new TypeReference[0]));
		} else {
			// use fully qualified name for Union references to avoid clashes
			// with user types
			TypeReference t = new TypeReference(null, context.getTypeDeclaration(JSweetDefTranslatorConfig.UNION_CLASS_NAME),
					possibleTypes.toArray(new TypeReference[0]));
			t.setName(JSweetDefTranslatorConfig.UNION_CLASS_NAME);
			return t;
		}
	}

	private TypeDeclaration getOrCreateUnionType(List<TypeReference> possibleTypes) {
		CompilationUnit compilUnit = getParent(CompilationUnit.class);
		ModuleDeclaration targetModule = getOrCreateModule(compilUnit, JSweetDefTranslatorConfig.UNION_PACKAGE);

		String name = "Union" + possibleTypes.size();
		TypeDeclaration unionType = targetModule.findType(name);
		if (unionType == null) {
			unionType = createUnionType(name, possibleTypes.size());
			targetModule.addMember(unionType);
			context.registerType(JSweetDefTranslatorConfig.UNION_PACKAGE + "." + name, unionType);
		}

		return unionType;
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

	private TypeDeclaration createUnionType(String name, int parameterCount) {
		TypeParameterDeclaration[] typeParameters = new TypeParameterDeclaration[parameterCount];
		for (int i = 0; i < parameterCount; i++) {
			typeParameters[i] = new TypeParameterDeclaration(null, "T" + (i + 1));
		}

		TypeReference superType = new TypeReference(null, "Union", new TypeReference[] {
				new TypeReference(null, "any", null), new TypeReference(null, "any", null) });

		TypeDeclaration unionType = new TypeDeclaration(null, "interface", name, typeParameters,
				new TypeReference[] { superType }, null);
		unionType.setDocumentation("/** This union type was automatically generated in order to accept "
				+ parameterCount + " types. @see Union */");
		return unionType;
	}

}
