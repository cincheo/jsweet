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

import static org.jsweet.input.typescriptdef.util.Util.checkAndAdjustDeclarationName;
import static org.jsweet.input.typescriptdef.util.Util.toJavaName;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.ReferenceDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner checks that declaration names are valid Java identifiers and
 * change the names when not valid. The original names are kept in {@link Name}
 * annotations, which are added to the modified declarations.
 * 
 * @author Renaud Pawlak
 */
public class NameChecker extends Scanner {

	public NameChecker(Context context) {
		super(context);
	}

	public NameChecker(Scanner parentScanner) {
		super(parentScanner);
	}

	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		checkAndAdjustDeclarationName(moduleDeclaration);
		super.visitModuleDeclaration(moduleDeclaration);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		checkAndAdjustDeclarationName(typeDeclaration);
		super.visitTypeDeclaration(typeDeclaration);
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
		checkAndAdjustDeclarationName(variableDeclaration);
		super.visitVariableDeclaration(variableDeclaration);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		checkAndAdjustDeclarationName(functionDeclaration);
		super.visitFunctionDeclaration(functionDeclaration);
	}

	@Override
	public void visitReferenceDeclaration(ReferenceDeclaration referenceDeclaration) {
		referenceDeclaration.setName(toJavaName(referenceDeclaration.getName()));
		referenceDeclaration.setReferencedName(toJavaName(referenceDeclaration.getReferencedName()));
		super.visitReferenceDeclaration(referenceDeclaration);
	}

	@Override
	public void visitTypeParameterDeclaration(TypeParameterDeclaration typeParameterDeclaration) {
		typeParameterDeclaration.setName(toJavaName(typeParameterDeclaration.getName()));
		super.visitTypeParameterDeclaration(typeParameterDeclaration);
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		if (!"this".equals(typeReference.getName())) {
			String simpleName = typeReference.getSimpleName();
			String qualifier = typeReference.getQualifier();
			String newSimpleName = toJavaName(simpleName);
			if (qualifier == null) {
				typeReference.setName(newSimpleName);
			} else {
				String newQualifier = null;
				if (qualifier != null) {
					newQualifier = Arrays.stream(qualifier.split("\\.")).map(name -> toJavaName(name))
							.collect(Collectors.joining("."));
				}
				typeReference.setName(newQualifier + "." + newSimpleName);
			}
		}
		super.visitTypeReference(typeReference);
	}

}
