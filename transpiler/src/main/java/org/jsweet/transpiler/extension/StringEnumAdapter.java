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
package org.jsweet.transpiler.extension;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.model.CaseElement;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.VariableAccessElement;

/**
 * This optional adapter tunes the JavaScript generation to remove enums and
 * replace them with strings. It only applies to enums that are annotated with
 * <code>@StringType</code>.
 * 
 * <p>
 * For instance: <code>@StringType enum MyEnum { A, B, C }</code> will be erased
 * and all subsequent accesses to the enum will be mapped to simple strings.
 * 
 * <p>
 * Typically, the method declaration <code>void m(MyEnum e) {...}</code> will be
 * mapped to <code>void m(e : string) {...}</code>. And of course, the
 * invocation <code>xxx.m(MyEnum.A)</code> will be mapped to
 * <code>xxx.m("A")</code>.
 * 
 * <p>
 * Warning: this adapter is not activated by default. See JSweet specifications
 * to know how to activate this adapter.
 * 
 * @author Renaud Pawlak
 */
public class StringEnumAdapter extends PrinterAdapter {

	private boolean isStringEnum(Element element) {
		// note: this function could be improved to exclude enums that have
		// fields or methods other than the enum constants
		return element.getKind() == ElementKind.ENUM && hasAnnotationType(element, JSweetConfig.ANNOTATION_STRING_TYPE);
	}

	/**
	 * Default constructor.
	 * 
	 * <p>
	 * Performs the following initializations:
	 * 
	 * <ul>
	 * <li>Type mapping: eligible enum types -&gt; string</li>
	 * <li>Adds an annotation manager that will add <code>@Erased</code> to all
	 * eligible enum declarations</li>
	 * </ul>
	 * 
	 * <p>
	 * An eligible enum type is an enum type that has a <code>@StringType</code>
	 * annotation.
	 */
	public StringEnumAdapter(PrinterAdapter parent) {
		super(parent);
		// eligible enums will be translated to string in JS
		addTypeMapping((typeTree, name) -> isStringEnum(typeTree.getTypeAsElement()) ? "string" : null);

		// ignore enum declarations with a programmatic annotation manager
		addAnnotationManager(new AnnotationManager() {
			@Override
			public Action manageAnnotation(Element element, String annotationType) {
				// add the @Erased annotation to string enums
				return JSweetConfig.ANNOTATION_ERASED.equals(annotationType) && isStringEnum(element) ? Action.ADD
						: Action.VOID;
			}
		});

	}

	/**
	 * Uses of enum API need to be translated accordingly to strings.
	 */
	@Override
	public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
		if (invocation.getTargetExpression() != null) {
			Element targetType = invocation.getTargetExpression().getTypeAsElement();
			// enum API must be erased and use plain strings instead
			if (isStringEnum(targetType)) {
				switch (invocation.getMethodName()) {
				case "name":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression());
					return true;
				case "valueOf":
					printMacroName(invocation.getMethodName());
					print(invocation.getArgument(0));
					return true;
				case "equals":
					printMacroName(invocation.getMethodName());
					print("(").print(invocation.getTargetExpression()).print(" == ")
							.print(invocation.getArguments().get(0)).print(")");
					return true;
				}
			}
		}
		return super.substituteMethodInvocation(invocation);
	}

	/**
	 * Accessing an enum field is replaced by a simple string value (
	 * <code>MyEnum.A</code> =&gt; <code>"A"</code>).
	 */
	@Override
	public boolean substituteVariableAccess(VariableAccessElement variableAccess) {
		// accessing an enum field is replaced by a simple string value
		// (MyEnum.A => "A")
		if (isStringEnum(variableAccess.getTargetElement())) {
			print("\"" + variableAccess.getVariableName() + "\"");
			return true;
		}
		return super.substituteVariableAccess(variableAccess);
	}

	/**
	 * Special case for the case statement patter.
	 */
	@Override
	public boolean substituteCaseStatementPattern(CaseElement caseStatement, ExtendedElement pattern) {
		// map enums to strings in case statements
		if (isStringEnum(pattern.getTypeAsElement())) {
			print("\"" + pattern + "\"");
			return true;
		}
		return super.substituteCaseStatementPattern(caseStatement, pattern);
	}

}
