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

import java.math.BigDecimal;

import javax.lang.model.element.Element;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;

/**
 * This optional adapter tunes the JavaScript generation to map the Java's
 * BigDecimal API to the Big JavaScript library.
 * 
 * <p>
 * Warning: this adapter is not activated by default. See JSweet specifications
 * to know how to activate this adapter.
 * 
 * <p>
 * This extension requires the big.js candy to be available in the JSweet
 * classpath: https://github.com/jsweet-candies/candy-bigjs.
 * 
 * @author Renaud Pawlak
 */
public class BigDecimalAdapter extends PrinterAdapter {

	public BigDecimalAdapter(PrinterAdapter parent) {
		super(parent);
		// all BigDecimal types are mapped to Big
		addTypeMapping(BigDecimal.class.getName(), "Big");
	}

	@Override
	public boolean substituteNewClass(NewClassElement newClass) {
		String className = newClass.getTypeAsElement().toString();
		// map the BigDecimal constructors
		if (BigDecimal.class.getName().equals(className)) {
			print("new Big(").printArgList(newClass.getArguments()).print(")");
			return true;
		}
		// delegate to the adapter chain
		return super.substituteNewClass(newClass);
	}

	@Override
	public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
		if (invocation.getTargetExpression() != null) {
			Element targetType = invocation.getTargetExpression().getTypeAsElement();
			if (BigDecimal.class.getName().equals(targetType.toString())) {
				// BigDecimal methods are mapped to their Big.js equivalent
				switch (invocation.getMethodName()) {
				case "multiply":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression()).print(".times(").printArgList(invocation.getArguments())
							.print(")");
					return true;
				case "add":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression()).print(".plus(").printArgList(invocation.getArguments())
							.print(")");
					return true;
				case "scale":
					printMacroName(invocation.getMethodName());
					// we assume that we always have a scale of 2, which is a
					// good default if we deal with currencies...
					// to be changed/implemented further
					print("2");
					return true;
				case "setScale":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression()).print(".round(").print(invocation.getArguments().get(0))
							.print(")");
					return true;
				case "compareTo":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression()).print(".cmp(").print(invocation.getArguments().get(0))
							.print(")");
					return true;
				case "equals":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression()).print(".eq(").print(invocation.getArguments().get(0))
							.print(")");
					return true;
				}
			}

		}
		// delegate to the adapter chain
		return super.substituteMethodInvocation(invocation);
	}

}
