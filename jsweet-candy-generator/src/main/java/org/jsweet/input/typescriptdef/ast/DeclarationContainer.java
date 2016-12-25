package org.jsweet.input.typescriptdef.ast;

import java.util.List;


public interface DeclarationContainer extends Visitable {

	Declaration[] getMembers();

	void addMember(Declaration declaration);

	void replaceMember(Declaration existingDeclaration, Declaration withNewDeclaration);
	
	void removeMember(Declaration declaration);
	
	void clearMembers();

	FunctionDeclaration findFirstFunction(String name);

	List<FunctionDeclaration> findFunctions(String name);
	
	VariableDeclaration findVariable(String name);

	VariableDeclaration findVariableIgnoreCase(String name);
	
	TypeDeclaration findType(String name);

	TypeDeclaration findTypeIgnoreCase(String name);
	
	Declaration findDeclaration(String name);

	Declaration findDeclaration(Declaration declaration);
	
	void addMembers(Declaration[] declarations);
}
