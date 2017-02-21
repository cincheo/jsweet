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
import java.util.Set;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.util.AbstractTreePrinter;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ClassType;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.Name;

/**
 * This helper class performs extra type checking for the JSweet transpiler
 * (additionally to Java default type checking).
 * 
 * <p>
 * It checks that JSweet authorized APIs are used, and that auxiliary types such
 * as unions are valid.
 * 
 * @author Renaud Pawlak
 */
public class TypeChecker {

	public static boolean jdkAllowed = true;
	static final Set<String> AUTHORIZED_ACCESSED_TYPES = new HashSet<String>();
	static final Set<String> AUTHORIZED_DECLARED_TYPES = new HashSet<String>();
	/**
	 * Java types that represent numbers (qualified names).
	 */
	public static final Set<String> NUMBER_TYPES = new HashSet<String>();
	/**
	 * Java types that represent numbers (simple names).
	 */
	public static final Set<String> NUMBER_TYPE_NAMES = new HashSet<String>();
	/**
	 * Methods that can be invoked on <code>java.lang.Object</code> from JSweet.
	 */
	public static final Set<String> AUTHORIZED_OBJECT_METHODS = new HashSet<String>();
	/**
	 * Methods that can be invoked on <code>java.lang.String</code> from JSweet.
	 */
	public static final Set<String> AUTHORIZED_STRING_METHODS = new HashSet<String>();
	/**
	 * Methods that cannot be invoked on <code>java.util.function</code> classes
	 * from JSweet.
	 */
	public static final Set<String> FORBIDDEN_JDK_FUNCTIONAL_METHODS = new HashSet<String>();

	static {
		// AUTHORIZED_ACCESSED_TYPES.add(String.class.getName());

		AUTHORIZED_DECLARED_TYPES.add(String.class.getName());
		AUTHORIZED_DECLARED_TYPES.add(Object.class.getName());
		AUTHORIZED_DECLARED_TYPES.add(Class.class.getName());
		AUTHORIZED_DECLARED_TYPES.add(Boolean.class.getName());
		AUTHORIZED_DECLARED_TYPES.add(Void.class.getName());

		NUMBER_TYPES.add(Integer.class.getName());
		NUMBER_TYPES.add(Double.class.getName());
		NUMBER_TYPES.add(Number.class.getName());
		NUMBER_TYPES.add(Float.class.getName());
		NUMBER_TYPES.add(Byte.class.getName());
		NUMBER_TYPES.add(Short.class.getName());

		NUMBER_TYPE_NAMES.add(Integer.class.getSimpleName());
		NUMBER_TYPE_NAMES.add(Double.class.getSimpleName());
		NUMBER_TYPE_NAMES.add(Number.class.getSimpleName());
		NUMBER_TYPE_NAMES.add(Float.class.getSimpleName());
		NUMBER_TYPE_NAMES.add(Byte.class.getSimpleName());
		NUMBER_TYPE_NAMES.add(Short.class.getSimpleName());

		AUTHORIZED_DECLARED_TYPES.add(Runnable.class.getName());

		AUTHORIZED_OBJECT_METHODS.add("toString");
		AUTHORIZED_STRING_METHODS.add("charAt(int)");
		AUTHORIZED_STRING_METHODS.add("concat(java.lang.String)");
		AUTHORIZED_STRING_METHODS.add("indexOf(java.lang.String)");
		AUTHORIZED_STRING_METHODS.add("lastIndexOf(java.lang.String)");
		AUTHORIZED_STRING_METHODS.add("lastIndexOf(java.lang.String,int)");
		AUTHORIZED_STRING_METHODS.add("substring(int)");
		AUTHORIZED_STRING_METHODS.add("substring(int,int)");
		AUTHORIZED_STRING_METHODS.add("replace(java.lang.CharSequence,java.lang.CharSequence)");
		AUTHORIZED_STRING_METHODS.add("split(java.lang.String)");
		AUTHORIZED_STRING_METHODS.add("trim()");
		AUTHORIZED_STRING_METHODS.add("toLowerCase()");
		AUTHORIZED_STRING_METHODS.add("toUpperCase()");
		AUTHORIZED_STRING_METHODS.add("length()");

		FORBIDDEN_JDK_FUNCTIONAL_METHODS.add("and");
		FORBIDDEN_JDK_FUNCTIONAL_METHODS.add("negate");
		FORBIDDEN_JDK_FUNCTIONAL_METHODS.add("or");
		FORBIDDEN_JDK_FUNCTIONAL_METHODS.add("andThen");
	}

	private AbstractTreePrinter translator;

	/**
	 * Creates a new type checker object.
	 */
	public TypeChecker(AbstractTreePrinter translator) {
		this.translator = translator;
	}

	/**
	 * Checks that the given invocation conforms to JSweet contraints.
	 */
	public boolean checkApply(JCMethodInvocation invocation, MethodSymbol methSym) {
		// if (Util.hasAnnotationType(methSym, JSweetConfig.ANNOTATION_ERASED))
		// {
		// translator.report(invocation, JSweetProblem.ERASED_METHOD, methSym);
		// }
		if (!JSweetConfig.isJDKReplacementMode() && !jdkAllowed) {
			if (methSym.owner.toString().startsWith("java.")) {
				if (invocation.meth instanceof JCFieldAccess
						&& "super".equals(((JCFieldAccess) invocation.meth).selected.toString())) {
					translator.report(invocation, JSweetProblem.JDK_METHOD, methSym);
					return false;
				}
				if (translator.getContext().strictMode || AUTHORIZED_OBJECT_METHODS.contains(methSym.name.toString())) {
					return true;
				}
				if (methSym.owner.toString().equals(String.class.getName())
						&& AUTHORIZED_STRING_METHODS.contains(methSym.toString())) {
					return true;
				}
				translator.report(invocation, JSweetProblem.JDK_METHOD, methSym);
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks that the given type is JSweet compatible.
	 */
	public boolean checkType(JCTree declaringElement, Name declaringElementName, JCExpression typeExpression) {
		if (!JSweetConfig.isJDKReplacementMode()) {
			if (typeExpression instanceof JCArrayTypeTree) {
				return checkType(declaringElement, declaringElementName, ((JCArrayTypeTree) typeExpression).elemtype);
			}
			String type = typeExpression.type.tsym.toString();
			if (!jdkAllowed && !translator.getContext().strictMode && type.startsWith("java.")) {
				if (!(AUTHORIZED_DECLARED_TYPES.contains(type) || NUMBER_TYPES.contains(type)
						|| type.startsWith("java.util.function"))) {
					translator.report(declaringElement, declaringElementName, JSweetProblem.JDK_TYPE, type);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks that the given field access conforms to JSweet contraints.
	 */
	public boolean checkSelect(JCFieldAccess select) {
		if (!JSweetConfig.isJDKReplacementMode()) {
			if (select.selected.type instanceof ClassType) {
				String type = select.selected.type.tsym.toString();
				if (type.startsWith("java.")) {
					if (!jdkAllowed
							&& !(AUTHORIZED_ACCESSED_TYPES.contains(type) || type.startsWith("java.util.function"))) {
						translator.report(select, JSweetProblem.JDK_TYPE, type);
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean checkUnionTypeAssignment(Types types, JCTree parent, Type assigned, JCMethodInvocation union) {
		if (union.args.head.type.tsym.getQualifiedName().toString().startsWith(JSweetConfig.UNION_CLASS_NAME)) {
			// union type -> simple type
			if (!Util.containsAssignableType(types, union.args.head.type.getTypeArguments(), assigned)) {
				translator.report(parent, JSweetProblem.UNION_TYPE_MISMATCH);
				return false;
			}
		} else {
			// simple type -> union type
			String typeName = union.args.head.type.toString();
			if ((JSweetConfig.LANG_PACKAGE_ALT + ".Function").equals(typeName)
					|| (JSweetConfig.LANG_PACKAGE + ".Function").equals(typeName)) {
				// HACK: type checking is ignored here!
				// TODO: test better (see Backbone test)
				// comparator = union(function((Todo todo) -> {
				// return (Integer) todo.get("order");
				// }));
				return true;
			}
			if (!Util.containsAssignableType(types, assigned.getTypeArguments(), union.args.head.type)) {
				translator.report(parent, JSweetProblem.UNION_TYPE_MISMATCH);
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks that the given union type assignment conforms to JSweet
	 * contraints.
	 */
	public boolean checkUnionTypeAssignment(Types types, JCTree parent, JCMethodInvocation union) {
		if (parent instanceof JCVariableDecl) {
			JCVariableDecl decl = (JCVariableDecl) parent;
			if (decl.init == union) {
				return checkUnionTypeAssignment(types, parent, decl.type, union);
			}
		} else if (parent instanceof JCAssign) {
			JCAssign assign = (JCAssign) parent;
			if (assign.rhs == union) {
				return checkUnionTypeAssignment(types, parent, assign.lhs.type, union);
			}
		} else if (parent instanceof JCMethodInvocation) {
			JCMethodInvocation invocation = (JCMethodInvocation) parent;
			for (JCTree arg : invocation.args) {
				if (arg == union) {
					return checkUnionTypeAssignment(types, parent, arg.type, union);
				}
			}
		}
		return true;
	}

}
