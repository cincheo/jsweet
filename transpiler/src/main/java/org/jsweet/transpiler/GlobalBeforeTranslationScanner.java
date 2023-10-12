/* 
 * JSweet transpiler - http://www.jsweet.org
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
package org.jsweet.transpiler;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;

import standalone.com.sun.source.tree.BlockTree;
import standalone.com.sun.source.tree.ClassTree;
import standalone.com.sun.source.tree.CompilationUnitTree;
import standalone.com.sun.source.tree.LiteralTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.Tree;
import standalone.com.sun.source.tree.VariableTree;
import standalone.com.sun.source.tree.WildcardTree;
import standalone.com.sun.source.util.Trees;

/**
 * This AST scanner performs global analysis and fills up the context with
 * information that will be used by the translator.
 * 
 * @see JSweetContext
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class GlobalBeforeTranslationScanner extends AbstractTreeScanner {

    private Map<VariableTree, TypeElement> lazyInitializedStaticCandidates = new HashMap<>();

    /**
     * Creates a new global scanner.
     */
    public GlobalBeforeTranslationScanner(TranspilationHandler logHandler, JSweetContext context) {
        super(logHandler, context, null);
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree compilationUnit, Trees trees) {
        if (util().getPackageFullNameForCompilationUnit(compilationUnit).startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
            return null;
        }
        this.compilationUnit = compilationUnit;
        return super.visitCompilationUnit(compilationUnit, trees);
    }

    @Override
    public Void visitClass(ClassTree classDeclaration, Trees trees) {
        TypeElement classElement = Util.getElement(classDeclaration);

        String docComment = trees.getDocComment(trees.getPath(compilationUnit, classDeclaration));
        if (isNotBlank(docComment)) {
            context.docComments.put(classElement, docComment);
        }

        TypeMirror superClassType = context.types.erasure(classElement.getSuperclass());
        if (superClassType.toString().startsWith("java.") && !superClassType.toString().equals(Object.class.getName())
                && !types().isSubtype(classElement.asType(), util().getType(Throwable.class))
                && !util().isSourceElement(types().asElement(superClassType))) {
            // the class extends a JDK class
            context.addJdkSubclass(classElement.toString(), superClassType);
        }

        transposeInnerClassNameIfClash(classDeclaration, classElement);

        if (!(classElement.getEnclosingElement() instanceof TypeElement)) {
            if (classElement.getKind() == ElementKind.ANNOTATION_TYPE
                    && context.hasAnnotationType(classElement, JSweetConfig.ANNOTATION_DECORATOR)) {
                context.registerDecoratorAnnotation(classDeclaration, compilationUnit);
            }
        }

        boolean globals = false;
        if (JSweetConfig.GLOBALS_CLASS_NAME.equals(classDeclaration.getSimpleName().toString())) {
            globals = true;
        }

        for (Tree def : classDeclaration.getMembers()) {
            if (def instanceof VariableTree) {
                VariableTree fieldDeclaration = (VariableTree) def;
                VariableElement fieldElement = Util.getElement(fieldDeclaration);

                String fieldDocComment = trees.getDocComment(trees.getPath(compilationUnit, fieldDeclaration));
                if (isNotBlank(docComment)) {
                    context.docComments.put(fieldElement, fieldDocComment);
                }

                transposeFieldNameIfClash(classElement, fieldDeclaration, fieldElement);

                if (fieldDeclaration.getModifiers().getFlags().contains(Modifier.STATIC)) {
                    if (!(fieldDeclaration.getModifiers().getFlags().contains(Modifier.FINAL)
                            && fieldDeclaration.getInitializer() != null
                            && fieldDeclaration.getInitializer() instanceof LiteralTree)) {
                        lazyInitializedStaticCandidates.put(fieldDeclaration, classElement);
                    }
                }
            } else if (def instanceof BlockTree) {
                if (((BlockTree) def).isStatic()) {
                    context.countStaticInitializer(classElement);
                }
            }
            if (def instanceof MethodTree) {
                Element methodElement = Util.getElement(def);
                if (methodElement != null) {
                    if (globals && methodElement.getModifiers().contains(Modifier.STATIC)) {
                        context.registerGlobalMethod(classDeclaration, (MethodTree) def, compilationUnit);
                    } else {

                        MethodTree method = ((MethodTree) def);
                        TypeElement superClassTypeElement = (TypeElement) types()
                                .asElement(classElement.getSuperclass());
                        if (methodElement instanceof ExecutableElement
                                && methodElement.getModifiers().contains(Modifier.PRIVATE)
                                && !(methodElement.getKind() == ElementKind.STATIC_INIT
                                        || methodElement.getKind() == ElementKind.INSTANCE_INIT)
                                && !methodElement.getModifiers().contains(Modifier.STATIC)
                                && methodElement.getKind() != ElementKind.CONSTRUCTOR) {
                            ExecutableElement clashingMethod = util().findMethodDeclarationInType2(
                                    superClassTypeElement, method.getName().toString(),
                                    (ExecutableType) methodElement.asType());
                            if (clashingMethod != null && clashingMethod.getModifiers().contains(Modifier.PRIVATE)) {
                                context.addMethodNameMapping((ExecutableElement) methodElement,
                                        JSweetConfig.FIELD_METHOD_CLASH_RESOLVER_PREFIX
                                                + classElement.toString().replace(".", "_") + "_"
                                                + method.getName().toString());
                            }
                        }

                        VariableElement clashingField = null;
                        clashingField = util().findFieldDeclaration(superClassTypeElement, method.getName());
                        if (clashingField != null) {
                            if (clashingField.getModifiers().contains(Modifier.PRIVATE)
                                    && !context.hasFieldNameMapping(clashingField)) {
                                context.addFieldNameMapping(clashingField,
                                        JSweetConfig.FIELD_METHOD_CLASH_RESOLVER_PREFIX
                                                + classElement.toString().replace(".", "_") + "_"
                                                + method.getName().toString());
                            }
                        }
                    }
                }
            }
        }

        return super.visitClass(classDeclaration, trees);
    }

    private void transposeFieldNameIfClash(TypeElement classElement, VariableTree fieldDeclaration,
            VariableElement fieldElement) {

        if (!context.hasFieldNameMapping(fieldElement)) {
            if (classElement.getSuperclass() != null) {
                Element superClassElement = types().asElement(classElement.getSuperclass());
                if (superClassElement instanceof TypeElement) {
                    VariableElement clashingField = util().findFieldDeclaration((TypeElement) superClassElement,
                            fieldDeclaration.getName());
                    if (clashingField != null) {
                        if (clashingField.getModifiers().contains(Modifier.PRIVATE)
                                && !context.hasFieldNameMapping(clashingField)) {
                            context.addFieldNameMapping(fieldElement,
                                    JSweetConfig.FIELD_METHOD_CLASH_RESOLVER_PREFIX
                                            + classElement.toString().replace(".", "_") + "_"
                                            + fieldDeclaration.getName().toString());
                        }
                    }

                    ExecutableElement m = util().findMethodDeclarationInType(classElement,
                            fieldDeclaration.getName().toString(), null);
                    if (m != null) {
                        context.addFieldNameMapping(fieldElement, JSweetConfig.FIELD_METHOD_CLASH_RESOLVER_PREFIX
                                + fieldDeclaration.getName().toString());
                    }
                }
            }
        }
    }

    private void transposeInnerClassNameIfClash(ClassTree classDeclaration, TypeElement classElement) {
        if (classElement.getEnclosingElement() instanceof TypeElement) {
            TypeElement enclosingClassElement = (TypeElement) classElement.getEnclosingElement();
            TypeMirror superClass = enclosingClassElement.getSuperclass();
            if (superClass != null) {
                TypeElement superClassElement = (TypeElement) types().asElement(superClass);
                TypeElement clashingInnerClass = util().findInnerClassDeclaration(superClassElement,
                        classDeclaration.getSimpleName().toString());

                // rename inner class is enclosing class' super class has an inner class of the
                // same name
                if (clashingInnerClass != null && !context.hasClassNameMapping(clashingInnerClass)) {
                    context.addClassNameMapping(classElement,
                            "__" + enclosingClassElement.getQualifiedName().toString().replace('.', '_') + "_"
                                    + clashingInnerClass.getSimpleName());
                }
            }
        }
    }

    @Override
    public Void visitMethod(MethodTree methodDeclaration, Trees trees) {

        String docComment = trees.getDocComment(trees.getPath(compilationUnit, methodDeclaration));
        if (isNotBlank(docComment)) {
            ExecutableElement methodElement = Util.getElement(methodDeclaration);
            context.docComments.put(methodElement, docComment);
        }

        if (methodDeclaration.getModifiers().getFlags().contains(Modifier.DEFAULT)) {
            getContext().addDefaultMethod(compilationUnit, getParent(ClassTree.class), methodDeclaration);
        }
        if (!getContext().ignoreWildcardBounds) {
            scan(methodDeclaration.getParameters(), trees);
        }

        return null;
    }

    @Override
    public Void visitWildcard(WildcardTree wildcard, Trees trees) {
        ExecutableElement container = null;
        MethodTree method = getParent(MethodTree.class);
        if (method != null) {
            container = Util.getElement(method);
        }
        if (container != null) {
            getContext().registerWildcard(container, wildcard);
            scan(wildcard.getBound(), trees);
        }

        return null;
    }

    public void process(List<CompilationUnitTree> compilationUnits) {
        for (CompilationUnitTree compilationUnit : compilationUnits) {
            scan(compilationUnit, trees());
        }
        for (Entry<VariableTree, TypeElement> lazyStaticCandidate : lazyInitializedStaticCandidates.entrySet()) {

            VariableTree var = lazyStaticCandidate.getKey();
            TypeElement enclosingClass = lazyStaticCandidate.getValue();

            if (context.getStaticInitializerCount(enclosingClass) == 0 && var.getInitializer() == null
                    || util().isLiteralExpression(var.getInitializer())) {
                continue;
            }

            VariableElement varElement = Util.getElement(var);
            context.lazyInitializedStatics.add(varElement);
        }
    }

}
