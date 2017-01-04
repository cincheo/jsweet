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
import java.util.HashSet;
import java.util.Set;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.UnionTypeReference;
import org.jsweet.input.typescriptdef.ast.UnionTypeReference.Selected;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * @author Renaud Pawlak
 */
public class UnionTypesExpander extends Scanner {

	public UnionTypesExpander(Context context) {
		super(context);
	}

	class UnionScanner extends Scanner {

		Declaration declaration;
		Selected target;

		public UnionScanner(Context context, Declaration declaration, Selected target) {
			super(context);
			this.declaration = declaration;
			this.target = target;
			expandedDeclarations.add(declaration);
		}

		public void scan() {
			this.scan(this.declaration);
		}

		@Override
		public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
			scan(functionDeclaration.getTypeParameters());
			scan(functionDeclaration.getParameters());
		}

		@Override
		public void visitUnionTypeReference(UnionTypeReference unionTypeReference) {
			switch (unionTypeReference.getSelected()) {
			case NONE:
				unionTypeReference.setSelected(Selected.PENDING);
				new UnionScanner(context, declaration.copy(), target.inverse()).scan();
				unionTypeReference.setSelected(target);
				break;
			case PENDING:
				unionTypeReference.setSelected(target);
				break;
			default:
			}
			super.visitUnionTypeReference(unionTypeReference);
		}
	}

	class RevertUnionScanner extends Scanner {

		Declaration declaration;

		public RevertUnionScanner(Context context, Declaration declaration) {
			super(context);
			this.declaration = declaration;
		}

		public void scan() {
			this.scan(this.declaration);
		}

		@Override
		public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
			scan(functionDeclaration.getTypeParameters());
			scan(functionDeclaration.getParameters());
		}

		@Override
		public void visitUnionTypeReference(UnionTypeReference unionTypeReference) {
			unionTypeReference.setSelected(Selected.NONE);
			super.visitUnionTypeReference(unionTypeReference);
		}
	}

	private Declaration currentDeclaration;
	private Set<Declaration> expandedDeclarations = new HashSet<Declaration>();

	private void addExpandedDeclarations() {
		DeclarationContainer parent = getParent(DeclarationContainer.class);
		for (Declaration d : expandedDeclarations) {
			if (d != this.currentDeclaration) {
				if (context.verbose) {
					logger.info("adding union-expanded declaration: " + parent + "." + d);
				}
				parent.addMember(d);
			}
		}
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		this.currentDeclaration = functionDeclaration;
		expandedDeclarations.clear();
		new UnionScanner(context, functionDeclaration, Selected.LEFT).scan();
		if (expandedDeclarations.size() > 40) {
			// too much complexity... we revert expansion
			new RevertUnionScanner(context, functionDeclaration).scan();
			return;
		}
		for (Declaration d1 : new ArrayList<>(expandedDeclarations)) {
			FunctionDeclaration f1 = (FunctionDeclaration) d1;
			for (Declaration d2 : new ArrayList<>(expandedDeclarations)) {
				FunctionDeclaration f2 = (FunctionDeclaration) d2;
				if (f1 == f2) {
					continue;
				}
				boolean equal = true;
				for (int i = 0; i < f1.getParameters().length; i++) {
					TypeReference t1 = f1.getParameters()[i].getType();
					TypeReference t2 = f2.getParameters()[i].getType();
					if (!t1.toString().equals(t2.toString())) {
						equal = false;
						break;
					}
				}
				if (!equal) {
					continue;
				}
				if (!f1.getType().toString().equals(f2.getType().toString())) {
					continue;
				}
				expandedDeclarations.remove(f1);
				break;
			}
		}
		addExpandedDeclarations();
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

}
