package org.jsweet.input.typescriptdef.ast;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ModuleDeclaration extends AbstractDeclaration implements DeclarationContainer {

	Declaration[] members;

	public static ModuleDeclaration createQualifiedModuleDeclaration(Token token, String qualifiedName,
			Declaration[] members) {

		String[] subNames = qualifiedName.startsWith("\"") ? new String[] { qualifiedName }
				: qualifiedName.split("\\.");
		ModuleDeclaration module = new ModuleDeclaration(token, subNames[subNames.length - 1], members);
		if (subNames.length > 1) {
			return createQualifiedModuleDeclaration(token,
					StringUtils.join(ArrayUtils.subarray(subNames, 0, subNames.length - 1), "."),
					new Declaration[] { module });
		} else {
			return module;
		}

	}

	public ModuleDeclaration(Token token, String name, Declaration[] members) {
		super(token, name);
		this.members = members;
		// System.out.println("new " + this);
	}

	@Override
	public void accept(Visitor v) {
		v.visitModuleDeclaration(this);
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

	@Override
	public ModuleDeclaration copy() {
		ModuleDeclaration copy = new ModuleDeclaration(null, getName(), DeclarationHelper.copy(members));
		copy.setStringAnnotations(
				this.getStringAnnotations() == null ? null : new ArrayList<String>(getStringAnnotations()));
		copy.quotedName = this.quotedName;
		copy.originalName = this.originalName;
		return copy;
	}

}
