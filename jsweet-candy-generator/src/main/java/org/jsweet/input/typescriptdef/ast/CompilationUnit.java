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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsweet.JSweetDefTranslatorConfig;

/**
 * Compilation units correspond to TypeScript definitions source files.
 * 
 * @author Renaud Pawlak
 */
public class CompilationUnit implements Visitable, DeclarationContainer {

	protected List<String> references = new ArrayList<String>();

	protected File file;

	protected Declaration[] declarations;

	private boolean hidden = false;

	private ModuleDeclaration mainModule;

	private boolean external = false;

	public CompilationUnit(File file) {
		super();
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Declaration[] getDeclarations() {
		return declarations;
	}

	public void setDeclarations(Declaration[] declarations) {
		this.declarations = declarations;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitCompilationUnit(this);
	}

	@Override
	public String toString() {
		return file.toString();
	}

	@Override
	public Declaration[] getMembers() {
		return getDeclarations();
	}

	@Override
	public void addMember(Declaration declaration) {
		declarations = DeclarationHelper.addMember(this, declaration);
	}

	@Override
	public void removeMember(Declaration declaration) {
		declarations = DeclarationHelper.removeMember(this, declaration);
	}

	@Override
	public void replaceMember(Declaration existingDeclaration, Declaration withNewDeclaration) {
		declarations = DeclarationHelper.replaceMember(this, existingDeclaration, withNewDeclaration);
	}

	@Override
	public void clearMembers() {
		declarations = new Declaration[0];
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public FunctionDeclaration findFirstFunction(String name) {
		return DeclarationHelper.findFirstFunction(this, name);
	}

	@Override
	public List<FunctionDeclaration> findFunctions(String name) {
		return DeclarationHelper.findFunctions(this, name);
	}

	@Override
	public VariableDeclaration findVariable(String name) {
		return DeclarationHelper.findVariable(this, name);
	}

	@Override
	public VariableDeclaration findVariableIgnoreCase(String name) {
		return DeclarationHelper.findVariableIgnoreCase(this, name);
	}
	
	@Override
	public TypeDeclaration findType(String name) {
		TypeDeclaration typeDecl = DeclarationHelper.findType(this, name);
		if (typeDecl == null) {
			typeDecl = getMainModule().findType(name);
		}

		return typeDecl;
	}

	@Override
	public TypeDeclaration findTypeIgnoreCase(String name) {
		TypeDeclaration typeDecl = DeclarationHelper.findTypeIgnoreCase(this, name);
		if (typeDecl == null) {
			typeDecl = getMainModule().findTypeIgnoreCase(name);
		}

		return typeDecl;
	}
	
	@Override
	public Declaration findDeclaration(String name) {
		Declaration declaration = DeclarationHelper.findDeclaration(this, name);
		if (declaration == null) {
			ModuleDeclaration mainModule = getMainModule();
			if (mainModule != null) {
				declaration = mainModule.findDeclaration(name);
			}
		}

		return declaration;
	}

	public ModuleDeclaration getMainModule() {
		if (mainModule == null) {
			for (Declaration decl : declarations) {
				if (decl instanceof ModuleDeclaration && (JSweetDefTranslatorConfig.isLibPath(decl.getName())
						|| JSweetDefTranslatorConfig.isJSweetPath(decl.getName()))) {
					mainModule = (ModuleDeclaration) decl;
					break;
				}
			}
		}
		return mainModule;
	}

	@Override
	public void addMembers(Declaration[] declarations) {
		DeclarationHelper.addMembers(this, declarations);
	}

	@Override
	public Declaration findDeclaration(Declaration declaration) {
		return DeclarationHelper.findDeclaration(this, declaration);
	}

	public void addReference(String reference) {
		references.add(reference.replace('\r', ' ').replace('\n', ' ').trim());
	}

	public List<String> getReferences() {
		return references;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CompilationUnit)) {
			return false;
		} else {
			return file.equals(((CompilationUnit) obj).file);
		}
	}

	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

}
