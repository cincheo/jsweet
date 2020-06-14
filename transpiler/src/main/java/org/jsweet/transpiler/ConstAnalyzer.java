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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ElementKind;

import org.jsweet.transpiler.util.AbstractTreeScanner;

import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCAssignOp;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCUnary;

/**
 * This AST scanner analyzes local variables to determine if they are locally
 * assigned and can be assumed constant or not.
 * 
 * @see JSweetContext
 * @author Renaud Pawlak
 */
public class ConstAnalyzer extends AbstractTreeScanner {

    private Set<VarSymbol> modifiedVariables = new HashSet<>();

    /**
     * Gets all the local variables that are modified within the program. All other
     * local variables can be assumed as constant.
     */
    public Set<VarSymbol> getModifiedVariables() {
        return modifiedVariables;
    }

    /**
     * Creates a new constant variable analyzer.
     */
    public ConstAnalyzer(TranspilationHandler logHandler, JSweetContext context) {
        super(logHandler, context, null);
    }

    private void registerModification(JCTree tree) {
        if (tree instanceof JCIdent && ((JCIdent) tree).sym.getKind() == ElementKind.LOCAL_VARIABLE) {
            modifiedVariables.add((VarSymbol) ((JCIdent) tree).sym);
        }
    }

    @Override
    public void visitAssign(JCAssign assign) {
        registerModification(assign.lhs);
        super.visitAssign(assign);
    }

    @Override
    public void visitAssignop(JCAssignOp assignOp) {
        registerModification(assignOp.lhs);
    }

    @Override
    public void visitUnary(JCUnary unary) {
        switch (unary.getTag()) {
        case PREINC:
        case POSTINC:
        case PREDEC:
        case POSTDEC:
            registerModification(unary.arg);
        default:
        }
        super.visitUnary(unary);
    }

    public void process(List<JCCompilationUnit> compilationUnits) {
        for (JCCompilationUnit compilationUnit : compilationUnits) {
            scan(compilationUnit);
        }
    }

}
