package org.jsweet.transpiler.model.support;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.VariableElement;

import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;

public class VariableElementSupport extends ExtendedElementSupport<VariableTree> implements VariableElement {

    public VariableElementSupport(TreePath treePath, VariableTree tree,
            javax.lang.model.element.VariableElement variableElement, JSweetContext context) {
        super(treePath, tree, variableElement, context);
    }

    @Override
    public javax.lang.model.element.VariableElement getStandardElement() {
        return (javax.lang.model.element.VariableElement) element;
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
    public Name getSimpleName() {
        return getStandardElement().getSimpleName();
    }

    @Override
    public Object getConstantValue() {
        return getStandardElement().getConstantValue();
    }

}