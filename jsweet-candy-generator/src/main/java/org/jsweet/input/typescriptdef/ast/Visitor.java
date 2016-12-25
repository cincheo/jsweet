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

/**
 * The core interface for visiting all the visitable elements.
 * 
 * @author Renaud Pawlak
 */
public interface Visitor {

	void visitCompilationUnit(CompilationUnit compilationUnit);

	void visitModuleDeclaration(ModuleDeclaration moduleDeclaration);

	void visitReferenceDeclaration(ReferenceDeclaration referenceDeclaration);

	void visitTypeDeclaration(TypeDeclaration typeDeclaration);

	void visitFunctionDeclaration(FunctionDeclaration functionDeclaration);

	void visitVariableDeclaration(VariableDeclaration variableDeclaration);

	void visitParameterDeclaration(ParameterDeclaration parameterDeclaration);

	void visitTypeParameterDeclaration(TypeParameterDeclaration typeParameterDeclaration);

	void visitTypeReference(TypeReference typeReference);

	void visitFunctionalTypeReference(FunctionalTypeReference functionalTypeReference);

	void visitArrayTypeReference(ArrayTypeReference arrayTypeReference);

	void visitUnionTypeReference(UnionTypeReference unionTypeReference);

	void visitLiteral(Literal literal);

	void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration);
}
