package org.jsweet.transpiler.model.support;

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

}
