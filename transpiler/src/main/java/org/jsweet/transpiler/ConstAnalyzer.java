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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;

import org.jsweet.transpiler.util.Util;

import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

/**
 * This AST scanner analyzes local variables to determine if they are locally
 * assigned and can be assumed constant or not.
 * 
 * @see JSweetContext
 * @author Renaud Pawlak
 */
public class ConstAnalyzer extends TreeScanner<Void, Trees> {

    private Set<VariableElement> modifiedVariables = new HashSet<>();

    /**
     * Gets all the local variables that are modified within the program. All other
     * local variables can be assumed as constant.
     */
    public Set<VariableElement> getModifiedVariables() {
        return modifiedVariables;
    }

    private ElementKind variableKind = ElementKind.LOCAL_VARIABLE;
    private boolean initializationOnly = false;

    /**
     * Create a constant variable analyzer on local variables only (default).
     */
    public ConstAnalyzer() {
    }

    /**
     * Create a constant variable analyzer on the given kind of variable.
     * 
     * @param variableKind       the variable kind to analyze (if null, analyzes all
     *                           kinds of variables)
     * @param initializationOnly only takes into account direct assignments and not
     *                           self-dependent modifications
     */
    public ConstAnalyzer(ElementKind variableKind, boolean initializationOnly) {
        this.variableKind = variableKind;
        this.initializationOnly = initializationOnly;
    }

    private void registerModification(Tree tree) {
    	if (tree instanceof ArrayAccessTree) {
    		return;
    	}
        Element element = Util.getElement(tree);
        if (element != null && (variableKind == null || element.getKind() == variableKind)) {
            modifiedVariables.add((VariableElement) element);
        }
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree compilationUnit, Trees trees) {
        JSweetContext.currentCompilationUnit.set(compilationUnit);
        return super.visitCompilationUnit(compilationUnit, trees);
    }

    @Override
    public Void visitAssignment(AssignmentTree assign, Trees trees) {
        // TODO: should check if rhs contains self reference for initialization only
        registerModification(assign.getVariable());
        return super.visitAssignment(assign, trees);
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree assign, Trees trees) {
        if (!this.initializationOnly) {
            registerModification(assign.getVariable());
        }
        return super.visitCompoundAssignment(assign, trees);
    }

    @Override
    public Void visitUnary(UnaryTree unary, Trees trees) {
        switch (unary.getKind()) {
        case POSTFIX_DECREMENT:
        case PREFIX_DECREMENT:
        case POSTFIX_INCREMENT:
        case PREFIX_INCREMENT:
            if (!this.initializationOnly) {
                registerModification(unary.getExpression());
            }
        default:
        }
        return super.visitUnary(unary, trees);
    }

    /**
     * Scans a given compilation unit list.
     */
    public void process(List<CompilationUnitTree> cuList, Trees trees) {
        for (CompilationUnitTree cu : cuList) {
            scan(cu, trees);
        }
    }
    
}