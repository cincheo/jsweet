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
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.TypeElement;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;

public class TypeElementSupport extends ExtendedElementSupport<Tree> implements TypeElement {

    public TypeElementSupport(TreePath treePath, Tree tree,
            javax.lang.model.element.TypeElement typeElement, JSweetContext context) {
        super(treePath, tree, typeElement, context);
    }

    @Override
    public javax.lang.model.element.TypeElement getStandardElement() {
        return (javax.lang.model.element.TypeElement) element;
    }

    @Override
    public TypeMirror asType() {
        return getStandardElement().asType();
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return getStandardElement().getEnclosedElements();
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
    public NestingKind getNestingKind() {
        return getStandardElement().getNestingKind();
    }

    @Override
    public Name getQualifiedName() {
        return getStandardElement().getQualifiedName();
    }

    @Override
    public Name getSimpleName() {
        return getStandardElement().getSimpleName();
    }

    @Override
    public TypeMirror getSuperclass() {
        return getStandardElement().getSuperclass();
    }

    @Override
    public List<? extends TypeMirror> getInterfaces() {
        return getStandardElement().getInterfaces();
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return getStandardElement().getTypeParameters();
    }

    @Override
    public Element getEnclosingElement() {
        return getStandardElement().getEnclosingElement();
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return getStandardElement().getAnnotationsByType(annotationType);
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return getStandardElement().getAnnotationMirrors();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return getStandardElement().getAnnotation(annotationType);
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return getStandardElement().accept(v, p);
    }

}