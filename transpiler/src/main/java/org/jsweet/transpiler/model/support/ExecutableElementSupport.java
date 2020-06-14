package org.jsweet.transpiler.model.support;

import org.jsweet.transpiler.model.ExecutableElement;

import com.sun.tools.javac.tree.JCTree.JCMethodDecl;

public class ExecutableElementSupport extends ExtendedElementSupport<JCMethodDecl> implements ExecutableElement {

    
    public ExecutableElementSupport(JCMethodDecl tree) {
        super(tree);
    }

    @Override
    public javax.lang.model.element.ExecutableElement getStandardElement() {
        return tree.sym;
    }

}
