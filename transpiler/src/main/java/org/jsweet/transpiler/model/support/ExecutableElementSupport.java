package org.jsweet.transpiler.model.support;

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

}