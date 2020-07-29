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

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;

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
    public void visitTopLevel(JCCompilationUnit topLevel) {
        if (topLevel.packge.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
            return;
        }
        this.compilationUnit = topLevel;
        super.visitTopLevel(topLevel);
    }

    @Override
    public void visitApply(JCMethodInvocation invocation) {
        try {
            MethodSymbol method = (MethodSymbol) invocation.meth.getClass().getField("sym").get(invocation.meth);
            if (context.hasAnnotationType(method, JSweetConfig.ANNOTATION_ASYNC)
                    && !"void".equals(method.getReturnType().toString())) {
                JCMethodDecl parent = getParent(JCMethodDecl.class);
                if (!context.hasAnnotationType(parent.sym, JSweetConfig.ANNOTATION_ASYNC)) {
                    context.addExtraAnnotationType(parent.sym, JSweetConfig.ANNOTATION_ASYNC);
                    if (context.isInvalidOverload(parent.sym)) {
                        Overload overload = context.getOverload((TypeElement) parent.sym.getEnclosingElement(), parent.sym);
                        for (ExecutableElement e : overload.getMethods()) {
                            context.addExtraAnnotationType(e, JSweetConfig.ANNOTATION_ASYNC);
                        }
                    }
                    List<MethodSymbol> candidates = new LinkedList<>();
                    Util.collectMatchingMethodDeclarationsInType(context.types, (TypeSymbol)parent.sym.getEnclosingElement(), parent.name.toString(), (MethodType)parent.sym.type, true, candidates);
                    for (MethodSymbol candidate : candidates) {
                        context.addExtraAnnotationType(candidate, JSweetConfig.ANNOTATION_ASYNC);
                    }
                    
                    stillWorking = true;
                }
                JCTree directParent = getParent();
                if (!(context.isAwaitInvocation(invocation) || (directParent instanceof JCMethodInvocation
                        && (((JCMethodInvocation) directParent).meth.toString().equals("await")
                                || ((JCMethodInvocation) directParent).meth.toString().endsWith(".await"))))) {
                    context.addAwaitInvocation(invocation);
                    stillWorking = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.visitApply(invocation);
    }

    public void process(List<JCCompilationUnit> compilationUnits) {
        do {
            stillWorking = false;
            for (JCCompilationUnit compilationUnit : compilationUnits) {
                scan(compilationUnit);
            }
        } while (stillWorking);
    }

}
