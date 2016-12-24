package org.jsweet.input.typescriptdef.ast;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Set;

public interface Declaration extends AstNode, NamedElement, AnnotatedElement {

	String getDocumentation();

	void setDocumentation(String documentation);

	void addModifier(String modifier);

	void removeModifier(String modifier);

	boolean hasModifier(String modifier);

	Set<String> getModifiers();

	void setModifiers(Set<String> modifiers);

	Declaration copy();

	boolean isInputAnnotatedWith(String annotation);

	void addAnnotation(Annotation annotation);

	<T extends Annotation> T removeAnnotation(Class<T> annotationClass);

	void addStringAnnotation(String annotation);

	void removeStringAnnotation(String annotation);

	List<String> getStringAnnotations();

	void setStringAnnotations(List<String> stringAnnotations);

	boolean hasStringAnnotation(String annotation);

	String getStringAnnotation(String annotation);

	boolean isQuotedName();

	void setQuotedName(boolean quotedName);
	
}
