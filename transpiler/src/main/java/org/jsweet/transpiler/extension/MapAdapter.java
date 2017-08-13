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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import javax.lang.model.element.Element;

import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;

/**
 * This optional adapter tunes the JavaScript generation to use JavaScript ES6
 * maps to implement Java maps.
 * 
 * <p>
 * Note that this is a partial implementation that shall be extended to support
 * more cases and adapted to your own requirements. Additionally, this
 * implementation generates untyped JavaScript in order to avoid having to have
 * the ES6 API in the compilation path.
 * 
 * @author Renaud Pawlak
 */
public class MapAdapter extends PrinterAdapter {

	/**
	 * Java types that need to be transpiled to ES6 maps.
	 */
	protected static String[] mapTypes = { Map.class.getName(), HashMap.class.getName(), TreeMap.class.getName(),
			Hashtable.class.getName() };

	/**
	 * Creates the adapter and initialize all type mappings.
	 */
	public MapAdapter(PrinterAdapter parent) {
		super(parent);
		// rewrite all Java map and compatible map implementations types
		// note that we rewrite to 'any' because we don't want to require the
		// ES6 API to compile (all subsequent accesses will be untyped)
		for (String mapType : mapTypes) {
			addTypeMapping(mapType, "any");
		}
	}

	/**
	 * When one of the types in {@link #mapTypes} is found, instantiates an ES6
	 * map. Delegate to the parent otherwise.
	 */
	@Override
	public boolean substituteNewClass(NewClassElement newClass) {
		String className = newClass.getTypeAsElement().toString();
		// map the map constructor to the global 'Map' variable (untyped access)
		if (Arrays.binarySearch(mapTypes, className) >= 0) {
			// this access is browser/node-compatible
			print("new (typeof window == 'undefined'?global:window)['Map'](").printArgList(newClass.getArguments())
					.print(")");
			return true;
		}
		// delegate to the adapter chain
		return super.substituteNewClass(newClass);
	}

	/**
	 * This where the actual API bridge is implemented. Each well-known Java map
	 * method invocation is translated to the corresponding ES6 map method
	 * invocation.
	 */
	@Override
	public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
		if (invocation.getTargetExpression() != null) {
			Element targetType = invocation.getTargetExpression().getTypeAsElement();
			if (Arrays.binarySearch(mapTypes, targetType.toString()) >= 0) {
				// Java Map methods are mapped to their JavaScript equivalent
				switch (invocation.getMethodName()) {
				case "put":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression()).print(".set(").printArgList(invocation.getArguments())
							.print(")");
					return true;
				// although 'get' has the same name, we still rewrite it in case
				// another extension would provide it's own implementation
				case "get":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression()).print(".get(").printArgList(invocation.getArguments())
							.print(")");
					return true;
				case "containsKey":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression()).print(".has(").printArgList(invocation.getArguments())
							.print(")");
					return true;
				// we use the ES6 'Array.from' method in an untyped way to
				// transform the iterator in an array
				case "keySet":
					printMacroName(invocation.getMethodName());
					print("(<any>Array).from(").print(invocation.getTargetExpression()).print(".keys())");
					return true;
				case "values":
					printMacroName(invocation.getMethodName());
					print("(<any>Array).from(").print(invocation.getTargetExpression()).print(".values())");
					return true;
				// in ES6 maps, 'size' is a property, not a method
				case "size":
					printMacroName(invocation.getMethodName());
					print(invocation.getTargetExpression()).print(".size");
					return true;
				}
			}

		}
		// delegate to the adapter chain
		return super.substituteMethodInvocation(invocation);
	}

}
