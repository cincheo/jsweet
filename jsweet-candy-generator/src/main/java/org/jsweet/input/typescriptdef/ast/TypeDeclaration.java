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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Type declarations in source code of TypeScript definitions.
 * 
 * @author Renaud Pawlak
 */
public class TypeDeclaration extends AbstractDeclaration
		implements Type, TypeParameterizedElement, DeclarationContainer {

	private Declaration[] members;
	private TypeReference[] superTypes;
	private TypeReference[] mergedSuperTypes;
	private TypeParameterDeclaration[] typeParameters;
	private String kind;
	private String originalKind;
	private boolean external = false;

	public static TypeDeclaration createExternalTypeDeclaration(String simpleName) {
		return createExternalTypeDeclaration("class", simpleName);
	}

	public static TypeDeclaration createExternalTypeDeclaration(String kind, String simpleName) {
		TypeDeclaration t = new TypeDeclaration(null, kind, simpleName, null, null, new Declaration[0]);
		t.setExternal(true);
		return t;
	}

	public static TypeDeclaration createTypeDeclaration(String simpleName) {
		TypeDeclaration t = new TypeDeclaration(null, "class", simpleName, null, null, new Declaration[0]);
		return t;
	}

	public TypeDeclaration(Token token, String kind, String name, TypeParameterDeclaration[] typeParameters,
			TypeReference[] superTypes, Declaration[] members) {
		super(token, name);
		this.kind = this.originalKind = kind;
		this.typeParameters = typeParameters;
		this.setSuperTypes(superTypes);
		if (members != null) {
			this.members = members;
		} else {
			this.members = new Declaration[0];
		}
	}

	@Override
	public void accept(Visitor v) {
		v.visitTypeDeclaration(this);
	}

	@Override
	public Declaration[] getMembers() {
		return members;
	}

	@Override
	public void addMember(Declaration declaration) {
		members = DeclarationHelper.addMember(this, declaration);
	}

	@Override
	public void removeMember(Declaration declaration) {
		members = DeclarationHelper.removeMember(this, declaration);
	}

	@Override
	public void replaceMember(Declaration existingDeclaration, Declaration withNewDeclaration) {
		members = DeclarationHelper.replaceMember(this, existingDeclaration, withNewDeclaration);
	}

	@Override
	public void clearMembers() {
		members = new Declaration[0];
	}

	public TypeReference[] getSuperTypes() {
		return superTypes;
	}

	public void setSuperTypes(TypeReference[] superTypes) {
		this.superTypes = superTypes;
	}

	@Override
	public TypeParameterDeclaration[] getTypeParameters() {
		return this.typeParameters;
	}

	@Override
	public void setTypeParameters(TypeParameterDeclaration[] typeParameters) {
		this.typeParameters = typeParameters;
	}

	public FunctionDeclaration findFirstConstructor() {
		return DeclarationHelper.findFirstConstructor(this);
	}

	public List<FunctionDeclaration> findConstructors() {
		return DeclarationHelper.findConstructors(this);
	}

	public boolean isStatic() {
		return DeclarationHelper.isStatic(this);
	}

	public FunctionDeclaration findFirstFunction(String name) {
		return DeclarationHelper.findFirstFunction(this, name);
	}

	public List<FunctionDeclaration> findFunctions(String name) {
		return DeclarationHelper.findFunctions(this, name);
	}

	public VariableDeclaration findVariable(String name) {
		return DeclarationHelper.findVariable(this, name);
	}

	@Override
	public VariableDeclaration findVariableIgnoreCase(String name) {
		return DeclarationHelper.findVariableIgnoreCase(this, name);
	}

	@Override
	public TypeDeclaration findType(String name) {
		return DeclarationHelper.findType(this, name);
	}

	@Override
	public TypeDeclaration findTypeIgnoreCase(String name) {
		return DeclarationHelper.findTypeIgnoreCase(this, name);
	}

	@Override
	public Declaration findDeclaration(String name) {
		return DeclarationHelper.findDeclaration(this, name);
	}

	@Override
	public void addMembers(Declaration[] declarations) {
		DeclarationHelper.addMembers(this, declarations);
	}

	@Override
	public Declaration findDeclaration(Declaration declaration) {
		return DeclarationHelper.findDeclaration(this, declaration);
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.originalKind = this.kind;
		this.kind = kind;
	}

	@Override
	public TypeDeclaration copy() {
		TypeDeclaration copy = new TypeDeclaration(this.getToken(), getKind(), getName(),
				DeclarationHelper.copy(getTypeParameters()), DeclarationHelper.copyReferences(superTypes),
				DeclarationHelper.copy(members));
		copy.setDocumentation(getDocumentation());
		copy.setModifiers(this.getModifiers() == null ? null : new HashSet<String>(getModifiers()));
		copy.setStringAnnotations(
				this.getStringAnnotations() == null ? null : new ArrayList<String>(getStringAnnotations()));
		copy.originalKind = this.originalKind;
		return copy;
	}

	public boolean isExternal() {
		boolean external = this.external || (getDocumentation() != null && isInputAnnotatedWith("External"));
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

	@Override
	public boolean isSubtypeOf(Type type) {
		if (type == null) {
			return false;
		}
		if (getSuperTypes() != null) {
			for (TypeReference tref : getSuperTypes()) {
				TypeDeclaration t = (TypeDeclaration) tref.getDeclaration();
				if (type == t) {
					return true;
				} else {
					if (t != null) {
						return t.isSubtypeOf(type);
					}
				}
			}
		}
		return false;

	}

	public boolean isInterface() {
		return getKind() != null && getKind().equals("interface");
	}

	public boolean isFunctionalInterface() {
		return isInterface() && getMembers().length == 1;
	}

	@Override
	public String toString() {
		if (name != null) {
			return super.toString();
		} else {
			// print inlined toString (probably an object type)
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			for (Declaration d : getMembers()) {
				sb.append(d.toString()).append(";");
			}
			sb.append("}");
			return sb.toString();
		}
	}

	public TypeReference[] getMergedSuperTypes() {
		return mergedSuperTypes;
	}

	public void setMergedSuperTypes(TypeReference[] mergedSuperTypes) {
		this.mergedSuperTypes = mergedSuperTypes;
	}

	public void addMergedSuperType(TypeReference mergedSuperType) {
		if (this.mergedSuperTypes == null) {
			this.mergedSuperTypes = new TypeReference[] { mergedSuperType };
		} else {
			this.mergedSuperTypes = ArrayUtils.add(mergedSuperTypes, mergedSuperType);
		}
	}

	public final String getOriginalKind() {
		return originalKind;
	}

}
