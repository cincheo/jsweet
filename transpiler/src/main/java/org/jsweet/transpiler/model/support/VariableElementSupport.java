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

import org.jsweet.transpiler.model.VariableElement;

import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

public class VariableElementSupport extends ExtendedElementSupport<JCVariableDecl> implements VariableElement {

    public VariableElementSupport(JCVariableDecl tree) {
        super(tree);
    }

    @Override
    public javax.lang.model.element.VariableElement getStandardElement() {
        return tree.sym;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return tree.sym.accept(v, p);
    }

    @Override
    public TypeMirror asType() {
        return tree.sym.asType();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return tree.sym.getAnnotation(annotationType);
    }
    
    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return tree.sym.getAnnotationMirrors();
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return tree.sym.getAnnotationsByType(annotationType);
    }
    
    @Override
    public List<? extends Element> getEnclosedElements() {
        return tree.sym.getEnclosedElements();
    }

    @Override
    public Element getEnclosingElement() {
        return tree.sym.getEnclosingElement();
    }
    
    @Override
    public ElementKind getKind() {
        return tree.sym.getKind();
    }
    
    @Override
    public Set<Modifier> getModifiers() {
        return tree.sym.getModifiers();
    }
    
    @Override
    public Name getSimpleName() {
        return tree.sym.getSimpleName();
    }

    @Override
    public Object getConstantValue() {
        return tree.sym.getConstantValue();
    }
    
}
