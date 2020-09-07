package org.jsweet.transpiler.model.support;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.ExecutableElement;

import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePath;

public class ExecutableElementSupport extends ExtendedElementSupport<MethodTree> implements ExecutableElement {

    public ExecutableElementSupport(TreePath treePath, MethodTree tree,
            javax.lang.model.element.ExecutableElement element, JSweetContext context) {
        super(treePath, tree, element, context);
    }

    @Override
    public javax.lang.model.element.ExecutableElement getStandardElement() {
        return (javax.lang.model.element.ExecutableElement) element;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return getStandardElement().accept(v, p);
    }

    @Override
    public TypeMirror asType() {
        return getStandardElement().asType();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return getStandardElement().getAnnotation(annotationType);
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return getStandardElement().getAnnotationMirrors();
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return getStandardElement().getAnnotationsByType(annotationType);
    }

    @Override
    public AnnotationValue getDefaultValue() {
        return getStandardElement().getDefaultValue();
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return getStandardElement().getEnclosedElements();
    }

    @Override
    public Element getEnclosingElement() {
        return getStandardElement().getEnclosingElement();
    }

    @Override
    public ElementKind getKind() {
        return getStandardElement().getKind();
    }

    @Override
    public Set<Modifier> getModifiers() {
        return getStandardElement().getModifiers();
    }

    @Override
    public List<? extends VariableElement> getParameters() {
        return getStandardElement().getParameters();
    }

    @Override
    public TypeMirror getReceiverType() {
        return getStandardElement().getReceiverType();
    }

    @Override
    public TypeMirror getReturnType() {
        return getStandardElement().getReturnType();
    }

    @Override
    public Name getSimpleName() {
        return getStandardElement().getSimpleName();
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        return getStandardElement().getThrownTypes();
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return getStandardElement().getTypeParameters();
    }

    @Override
    public boolean isVarArgs() {
        return getStandardElement().isVarArgs();
    }

    @Override
    public boolean isDefault() {
        return getStandardElement().isDefault();
    }
}