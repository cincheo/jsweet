/*
 * JSweet transpiler - http://www.jsweet.org
 * Copyright (C) 2019 CINCHEO SAS <renaud.pawlak@cincheo.fr>
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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ForeachLoopElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;
import org.jsweet.transpiler.model.support.ForeachLoopElementSupport;

import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;

/**
 * An adapter that removes many uses of Java APIs and replace them with
 * JavaScript ES6 equivalent when possible.
 */

public class RemoveJavaDependenciesES6Adapter extends RemoveJavaDependenciesAdapter {
	protected final static Set<String> SET_CLASS_NAMES = Stream
			.of(Set.class, HashSet.class, LinkedHashSet.class, TreeSet.class, AbstractSet.class).map(Class::getName)
			.collect(Collectors.toSet());

	protected final static Set<String> MAP_CLASS_NAMES = Stream
			.of(Map.class, HashMap.class, LinkedHashMap.class, TreeMap.class, AbstractMap.class).map(Class::getName)
			.collect(Collectors.toSet());

	public RemoveJavaDependenciesES6Adapter(PrinterAdapter parentAdapter) {
		super(parentAdapter);
	}

	@Override
	protected void initTypesMapping() {
		super.initTypesMapping();
		SET_CLASS_NAMES.forEach(name -> extTypesMapping.put(name, "Set"));
		MAP_CLASS_NAMES.forEach(name -> extTypesMapping.put(name, "Map"));
		extTypesMapping.put(Map.class.getName() + ".Entry", "any");
	}

	@Override
	public boolean substituteForEachLoop(ForeachLoopElement foreachLoop, boolean targetHasLength, String indexVarName) {
		JCEnhancedForLoop loop = ((ForeachLoopElementSupport) foreachLoop).getTree();
		if (!targetHasLength && SET_CLASS_NAMES.contains(context.types.erasure(loop.expr.type).toString())) {
			getPrinter().print(loop.expr).print(".forEach((" + loop.var.name.toString() + ")=>");
			getPrinter().printIndent().print(loop.body);
			endIndent().println().printIndent().print(")");
			return true;
		}
		return super.substituteForEachLoop(foreachLoop, targetHasLength, indexVarName);
	}

	@Override
	public boolean substituteNewClass(NewClassElement newClass) {
		String className = newClass.getTypeAsElement().toString();

		if (SET_CLASS_NAMES.contains(className)) {
			this.substituteNewSet(newClass);
			return true;
		}
		if (MAP_CLASS_NAMES.contains(className)) {
			this.substituteNewMap(newClass);
			return true;
		}

		return super.substituteNewClass(newClass);
	}

	protected void substituteNewSet(NewClassElement newClass) {
		TypeMirror genericIterable = context.modelTypes.erasure(context.symtab.iterableType);

		boolean ignoreArguments = newClass.getArgumentCount() == 0
				|| !(newClass.getArgument(0).getType() instanceof ArrayType || context.modelTypes
						.isAssignable(context.modelTypes.erasure(newClass.getArgument(0).getType()), genericIterable));

		if (ignoreArguments) {
			print("new Set()");
		} else {
			print("new Set(").print(newClass.getArgument(0)).print(")");
		}
	}

	protected void substituteNewMap(NewClassElement newClass) {
		boolean ignoreArguments = newClass.getArgumentCount() == 0
				|| !context.modelTypes.erasure(newClass.getArgument(0).getType()).toString().endsWith("Map");

		if (ignoreArguments) {
			print("new Map()");
		} else {
			print("new Map(").print(newClass.getArgument(0)).print(")");
		}
	}

	@Override
	public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
		String targetClassName = invocation.getMethod().getEnclosingElement().toString();
		ExtendedElement targetExpression = invocation.getTargetExpression();
		if (targetExpression != null) {
			targetClassName = targetExpression.getTypeAsElement().toString();
		}

		if (SET_CLASS_NAMES.contains(targetClassName)) {
			this.substituteMethodOnSet(invocation);
			return true;
		}
		if (MAP_CLASS_NAMES.contains(targetClassName)) {
			this.substituteMethodOnMap(invocation);
			return true;
		}

		return super.substituteMethodInvocation(invocation);
	}

	protected void substituteMethodOnSet(MethodInvocationElement invocation) {
		String targetMethodName = invocation.getMethodName();
		ExtendedElement targetExpression = invocation.getTargetExpression();

		switch (targetMethodName) {
		case "add":
			printMacroName(targetMethodName);
			print("((s, v) => { const n = s.size; s.add(v); return n !== s.size; })(").print(targetExpression)
					.print(",").print(invocation.getArgument(0)).print(")");
			break;
		case "addAll":
			printMacroName(targetMethodName);
			print("((s, c) => { const len = s.size; for (const e of c) s.add(e); return s.size !== len; })(")
					.print(targetExpression).print(",").print(invocation.getArgument(0)).print(")");
			break;
		case "clear":
			printMacroName(targetMethodName);
			print(targetExpression).print(".clear()");
			break;
		case "contains":
			printMacroName(targetMethodName);
			print(targetExpression).print(".has(").print(invocation.getArgument(0)).print(")");
			break;
		case "containsAll":
			printMacroName(targetMethodName);
			print("((s, c) => c.every(e => s.has(e)))(").print(targetExpression).print(",")
					.print(invocation.getArgument(0)).print(")");
			break;
		case "equals":
			print("((s1, s2) => { if (!s1 || !s2) return s1 === s2; return s1.size === s2.size && Array.from(s1).every(e => s2.has(e)) })(")
					.print(targetExpression).print(",").print(invocation.getArgument(0)).print(")");
			break;
		case "hashCode":
			printMacroName(targetMethodName);
			report(invocation, JSweetProblem.USER_ERROR, "hashCode() is not supported.");
			break;
		case "isEmpty":
			printMacroName(targetMethodName);
			print("(").print(targetExpression).print(".size === 0").print(")");
			break;
		case "iterator":
			printMacroName(targetMethodName);
			print(targetExpression).print(".values()");
			break;
		case "remove":
			printMacroName(targetMethodName);
			print("(").print(targetExpression).print(".delete(").print(invocation.getArgument(0)).print(")").print(")");
			break;
		case "removeAll":
			printMacroName(targetMethodName);
			print("((s, c) => { const len = s.size; for (const e of c) s.delete(e); return s.size !== len; })(")
					.print(targetExpression).print(",").print(invocation.getArgument(0)).print(")");
			break;
		case "retainAll":
			printMacroName(targetMethodName);
			print("((s, c) => { const len = s.size; const ca = Array.from(c); s.forEach(e => { if (ca.indexOf(e) == -1) s.delete(e) }); return len !== s.size; })(")
					.print(targetExpression).print(",").print(invocation.getArgument(0)).print(")");
			break;
		case "size":
			// size typing was so strong that it breaks unit tests checking size "type" (e.g
			// 0 !== 2)
			print("(<any>");
			printMacroName(targetMethodName);
			print(targetExpression).print(".size");
			print(")");
			break;
		case "toArray":
			printMacroName(targetMethodName);
			print("(").print("Array.from(").print(targetExpression).print(")").print(")");
			break;
		default:
			printCallToEponymMethod(invocation);
		}
	}

	protected void substituteMethodOnMap(MethodInvocationElement invocation) {
		String targetMethodName = invocation.getMethodName();
		ExtendedElement targetExpression = invocation.getTargetExpression();

		switch (targetMethodName) {
		case "put":
			printMacroName(targetMethodName);
			print("((m, k, v) => { const prev = m.get(k); m.set(k,v); return prev; })(");
			print(targetExpression).print(",");
			print(invocation.getArgument(0)).print(",");
			print(invocation.getArgument(1));
			print(")");
			break;
		case "putAll":
			printMacroName(targetMethodName);
			print("((m, m2) => { for (const e of m2) m.set(e[0], e[1]) })(").print(targetExpression).print(",")
					.print(invocation.getArgument(0)).print(")");
			break;
		case "containsKey":
			printMacroName(targetMethodName);
			print(targetExpression).print(".has(").print(invocation.getArgument(0)).print(")");
			break;
		case "containsValue":
			printMacroName(targetMethodName);
			print("((m, v) => Array.from(m.values()).indexOf(v)>-1)(");
			print(targetExpression).print(",").print(invocation.getArgument(0));
			print(")");
			break;

		case "entrySet":
			printMacroName(targetMethodName);
			print("((m) => new Set(Array.from(m.entries()).map(entry => ({ getKey: () => entry[0], getValue: () => entry[1] }) ) ))(");
			print(targetExpression);
			print(")");
			break;
		case "keySet":
			printMacroName(targetMethodName);
			print("((m) => new Set(m.keys()))(");
			print(targetExpression);
			print(")");
			break;
		case "values":
			printMacroName(targetMethodName);
			print("((m) => Array.from(m.values()))(");
			print(targetExpression);
			print(")");
			break;

		case "equals":
			printMacroName(targetMethodName);
			print("((s1, s2) => { if (!s1 || !s2) return s1 === s2; return s1.size === s2.size && Array.from(s1.keys()).every(i => x[i] == x2[i]) })(")
					.print(targetExpression).print(",").print(invocation.getArgument(0)).print(")");
			break;
		case "hashCode":
			printMacroName(targetMethodName);
			report(invocation, JSweetProblem.USER_ERROR, "hashCode() is not supported.");
			break;

		case "isEmpty":
			printMacroName(targetMethodName);
			print("(").print(targetExpression).print(".size === 0").print(")");
			break;

		case "remove":
			printMacroName(targetMethodName);
			print("((m, k) => { const v = m.get(k); return m.delete(k) ? v : undefined; })(");
			print(targetExpression).print(",").print(invocation.getArgument(0));
			print(")");
			break;

		case "getOrDefault":
			printMacroName(targetMethodName);
			print("(");
			print(targetExpression);
			print(" .get(");
			print(invocation.getArgument(0));
			print(" ) || ");
			print(invocation.getArgument(1));
			print(")");
			break;
			
		case "compute":
			printMacroName(targetMethodName);
			print("((m, k, f) => { m.set(k, f(k, m.get(k))); })(");
			print(targetExpression).print(",").print(invocation.getArgument(0)).print(",").print(invocation.getArgument(1));
			print(")");
			break;
		case "computeIfAbsent":
			printMacroName(targetMethodName);
			print("((m, k, f) => { if (m.get(k) == null) m.set(k, f(k)) })(");
			print(targetExpression).print(",").print(invocation.getArgument(0)).print(",").print(invocation.getArgument(1));
			print(")");
			break;
		case "computeIfPresent":
			printMacroName(targetMethodName);
			print("((m, k, f) => { if (m.get(k) != null) m.set(k, f(k, m.get(k))); })(");
			print(targetExpression).print(",").print(invocation.getArgument(0)).print(",").print(invocation.getArgument(1));
			print(")");
			break;

		case "size":
			printMacroName(targetMethodName);
			// size typing was so strong that it breaks unit tests checking size "type" (e.g
			// 0 !== 2)
			print("(<any>");
			print(targetExpression).print(".size");
			print(")");
			break;

		default:
			printCallToEponymMethod(invocation);
		}
	}

	private void printCallToEponymMethod(MethodInvocationElement invocation) {
		String targetMethodName = invocation.getMethodName();
		ExtendedElement targetExpression = invocation.getTargetExpression();

		printMacroName(targetMethodName);
		print(targetExpression).print(".").print(targetMethodName);
		print("(");
		for (int i = 0; i < invocation.getArgumentCount(); i++) {
			print(invocation.getArgument(i));
			if (i < invocation.getArgumentCount() - 1) {
				print(",");
			}
		}
		print(")");
	}
}
