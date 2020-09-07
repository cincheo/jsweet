package org.jsweet.transpiler.model.support;

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

}