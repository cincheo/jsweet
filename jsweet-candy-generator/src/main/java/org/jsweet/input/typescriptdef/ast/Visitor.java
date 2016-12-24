package org.jsweet.input.typescriptdef.ast;


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
