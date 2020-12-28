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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.OverloadScanner.OverloadMethodEntry;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;

/**
 * This AST scanner performs propagates async methods automatically.
 * 
 * @author Renaud Pawlak
 */
public class AsyncAwaitPropagationScanner extends AbstractTreeScanner {

    boolean stillWorking = false;

    /**
     * Creates a new global scanner.
     */
    public AsyncAwaitPropagationScanner(JSweetContext context) {
        super(null, context, null);
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree topLevel, Trees trees) {
        String packageFullName = util().getPackageFullNameForCompilationUnit(topLevel);
        if (packageFullName.startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
            return null;
        }
        this.compilationUnit = topLevel;
        return super.visitCompilationUnit(topLevel, trees);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree invocation, Trees trees) {
        try {
            ExecutableElement method = Util.getElement(invocation.getMethodSelect());
            if (method == null) {
                return null;
            }
            
            if (context.hasAnnotationType(method, JSweetConfig.ANNOTATION_ASYNC)
                    && !"void".equals(method.getReturnType().toString())) {
                MethodTree parent = getParent(MethodTree.class);
                if (parent != null) {
                    ExecutableElement parentMethodElement = Util.getElement(parent);
                    if (!context.hasAnnotationType(parentMethodElement, JSweetConfig.ANNOTATION_ASYNC)) {
                        context.addExtraAnnotationType(parentMethodElement, JSweetConfig.ANNOTATION_ASYNC);

                        if (context.isInvalidOverload(parentMethodElement)) {
                            Overload overload = context.getOverload(
                                    (TypeElement) parentMethodElement.getEnclosingElement(), parentMethodElement);

                            for (OverloadMethodEntry overloadEntry : overload.getEntries()) {
                                context.addExtraAnnotationType(overloadEntry.methodElement,
                                        JSweetConfig.ANNOTATION_ASYNC);
                            }
                        }
                        List<ExecutableElement> candidates = new ArrayList<>();
                        util().collectMatchingMethodDeclarationsInType(
                                (TypeElement) parentMethodElement.getEnclosingElement(), parent.getName().toString(),
                                (ExecutableType) parentMethodElement.asType(), true, candidates);
                        for (ExecutableElement candidate : candidates) {
                            context.addExtraAnnotationType(candidate, JSweetConfig.ANNOTATION_ASYNC);
                        }

                        stillWorking = true;
                    }
                }
                Tree directParent = getParent();
                if (!(context.isAwaitInvocation(invocation) || (directParent instanceof MethodInvocationTree
                        && (Util.getElement(directParent).toString().equals("await")
                                || Util.getElement((MethodInvocationTree) directParent).toString().endsWith(".await"))))) {

                    context.addAwaitInvocation(invocation);
                    stillWorking = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.visitMethodInvocation(invocation, trees);
    }

    public void process(List<CompilationUnitTree> compilationUnits, Trees trees) {
        do {
            stillWorking = false;
            for (CompilationUnitTree compilationUnit : compilationUnits) {
                scan(compilationUnit, trees);
            }
        } while (stillWorking);
    }

}