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
package org.jsweet.transpiler.extensions;

import static org.jsweet.JSweetConfig.isJDKPath;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.typescript.Java2TypeScriptAdapter;
import org.jsweet.transpiler.typescript.Java2TypeScriptTranslator;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;

/**
 * An adapter that removes many uses of Java APIs and replace them with
 * JavaScript equivalent when possible.
 * 
 * @author Renaud Pawlak
 */
public class RemoveJavaDependenciesAdapter<C extends JSweetContext> extends Java2TypeScriptAdapter<C> {

	public RemoveJavaDependenciesAdapter(C context) {
		super(context);
		typesMapping.put(List.class.getName(), "Array");
		typesMapping.put(ArrayList.class.getName(), "Array");
		typesMapping.put(Collection.class.getName(), "Array");
		typesMapping.put(Set.class.getName(), "Array");
		typesMapping.put(HashSet.class.getName(), "Array");
		typesMapping.put(TreeSet.class.getName(), "Array");
		typesMapping.put(Vector.class.getName(), "Array");
		typesMapping.put(Enumeration.class.getName(), "any");
		typesMapping.put(Iterator.class.getName(), "any");
		typesMapping.put(Map.class.getName(), "Object");
		typesMapping.put(HashMap.class.getName(), "Object");
		typesMapping.put(WeakHashMap.class.getName(), "Object");
		typesMapping.put(Hashtable.class.getName(), "Object");
		typesMapping.put(Comparator.class.getName(), "any");
		typesMapping.put(Exception.class.getName(), "Error");
		typesMapping.put(RuntimeException.class.getName(), "Error");
		typesMapping.put(Throwable.class.getName(), "Error");
		typesMapping.put(Error.class.getName(), "Error");
		typesMapping.put(StringBuffer.class.getName(), "{ str: string }");
		typesMapping.put(StringBuilder.class.getName(), "{ str: string }");
		typesMapping.put(Collator.class.getName(), "any");
		complexTypesMapping
				.add((typeTree,
						name) -> name.startsWith("java.")
								&& context.types.isSubtype(typeTree.type, context.symtab.throwableType) ? "Error"
										: null);
		complexTypesMapping
				.add((typeTree,
						name) -> typeTree instanceof JCTypeApply && WeakReference.class.getName()
								.equals(typeTree.type.tsym.getQualifiedName().toString())
										? ((JCTypeApply) typeTree).arguments.head : null);
	}

	@Override
	public String needsImport(JCImport importDecl, String qualifiedName) {
		if (isJDKPath(qualifiedName)) {
			return null;
		}
		return super.needsImport(importDecl, qualifiedName);
	}

	@Override
	protected boolean substituteMethodInvocation(JCMethodInvocation invocation, JCFieldAccess fieldAccess,
			TypeSymbol targetType, String targetClassName, String targetMethodName) {
		if (targetClassName != null && fieldAccess != null) {
			switch (targetClassName) {

			case "java.util.Collection":
			case "java.util.List":
			case "java.util.ArrayList":
			case "java.util.Vector":
			case "java.util.Set":
			case "java.util.HashSet":
			case "java.util.TreeSet":
				switch (targetMethodName) {
				case "add":
					printMacroName(targetMethodName);
					switch (targetClassName) {
					case "java.util.Set":
					case "java.util.HashSet":
					case "java.util.TreeSet":
						print("((s, e) => { if(s.indexOf(e)==-1) s.push(e); })(").print(fieldAccess.getExpression())
								.print(", ").print(invocation.args.head).print(")");
						break;
					default:
						if (invocation.args.size() == 2) {
							print(fieldAccess.getExpression()).print(".splice(").print(invocation.args.head)
									.print(", 0, ").print(invocation.args.tail.head).print(")");
						} else {
							print(fieldAccess.getExpression()).print(".push(").printArgList(invocation.args).print(")");
						}
					}
					return true;
				case "addAll":
					printMacroName(targetMethodName);
					print("((l1, l2) => l1.push.apply(l1, l2))(").print(fieldAccess.getExpression()).print(", ")
							.printArgList(invocation.args).print(")");
					return true;
				case "remove":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".splice(").printArgList(invocation.args).print(", 1)");
					return true;
				case "subList":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".slice(").printArgList(invocation.args).print(")");
					return true;
				case "size":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".length");
					return true;
				case "get":
				case "elementAt":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print("[").printArgList(invocation.args).print("]");
					return true;
				case "clear":
					printMacroName(targetMethodName);
					print("(").print(fieldAccess.getExpression()).print(".length = 0)");
					return true;
				case "isEmpty":
					printMacroName(targetMethodName);
					print("(").print(fieldAccess.getExpression()).print(".length == 0)");
					return true;
				case "contains":
					printMacroName(targetMethodName);
					print("(").print(fieldAccess.getExpression()).print(".indexOf(").print(invocation.args.head)
							.print(") >= 0)");
					return true;
				case "toArray":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression());
					return true;
				case "elements":
					printMacroName(targetMethodName);
					getPrinter()
							.print("((a) => { var i = 0; return { nextElement: function() { return i<a.length?a[i++]:null; }, hasMoreElements: function() { return i<a.length; }}})(")
							.print(fieldAccess.getExpression()).print(")");
					return true;
				case "iterator":
					printMacroName(targetMethodName);
					getPrinter()
							.print("((a) => { var i = 0; return { next: function() { return i<a.length?a[i++]:null; }, hasNext: function() { return i<a.length; }}})(")
							.print(fieldAccess.getExpression()).print(")");
					return true;
				}
				break;
			case "java.util.Map":
			case "java.util.HashMap":
			case "java.util.Hashtable":
			case "java.util.WeakHashMap":
				switch (targetMethodName) {
				case "put":
					printMacroName(targetMethodName);
					print("(").print(fieldAccess.getExpression()).print("[").print(invocation.args.head).print("] = ")
							.print(invocation.args.tail.head).print(")");
					return true;
				case "get":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print("[").print(invocation.args.head).print("]");
					return true;
				case "containsKey":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".hasOwnProperty(").print(invocation.args.head).print(")");
					return true;
				case "keySet":
					printMacroName(targetMethodName);
					print("Object.keys(").print(fieldAccess.getExpression()).print(")");
					return true;
				case "values":
					printMacroName(targetMethodName);
					print("(obj => Object.keys(obj).map(key => obj[key]))(").print(fieldAccess.getExpression())
							.print(")");
					return true;
				case "size":
					printMacroName(targetMethodName);
					print("Object.keys(").print(fieldAccess.getExpression()).print(").length");
					return true;
				case "remove":
					printMacroName(targetMethodName);
					print("delete ").print(fieldAccess.getExpression()).print("[").print(invocation.args.head)
							.print("]");
					return true;
				}
				break;
			case "java.util.Collections":
				switch (targetMethodName) {
				case "emptyList":
					printMacroName(targetMethodName);
					print("[]");
					return true;
				case "emptySet":
					printMacroName(targetMethodName);
					print("[]");
					return true;
				case "emptyMap":
					printMacroName(targetMethodName);
					print("{}");
					return true;
				case "unmodifiableList":
					printMacroName(targetMethodName);
					printArgList(invocation.args).print(".slice(0)");
					return true;
				case "singleton":
					printMacroName(targetMethodName);
					print("[").print(invocation.args.head).print("]");
					return true;
				case "singletonList":
					printMacroName(targetMethodName);
					print("[").print(invocation.args.head).print("]");
					return true;
				case "singletonMap":
					printMacroName(targetMethodName);
					if (invocation.args.head instanceof JCLiteral) {
						print("{ ").print(invocation.args.head).print(": ").print(invocation.args.tail.head)
								.print(" }");
					} else {
						print("(k => { let o = {}; o[k] = ").print(invocation.args.tail.head).print("; return o; })(")
								.print(invocation.args.head).print(")");
					}
					return true;
				}
				break;
			case "java.util.Arrays":
				switch (targetMethodName) {
				case "asList":
					printMacroName(targetMethodName);
					printArgList(invocation.args).print(".slice(0)");
					return true;
				case "copyOf":
					printMacroName(targetMethodName);
					print(invocation.args.head).print(".slice(0,").print(invocation.args.tail.head).print(")");
					return true;
				case "equals":
					printMacroName(targetMethodName);
					getPrinter()
							.print("((a1, a2) => { if(a1.length != a2.length) return false; for(let i = 0; i < a1.length; i++) { if(<any>a1[i] != <any>a2[i]) return false; } return true; })(")
							.printArgList(invocation.args).print(")");
					return true;
				case "sort":
					printMacroName(targetMethodName);
					if (invocation.args.length() > 2) {
						getPrinter()
								.print("((arr, start, end, f?) => ((arr1, arr2) => arr1.splice.apply(arr1, (<any[]>[start, arr2.length]).concat(arr2)))(")
								.print(invocation.args.head).print(", ").print(invocation.args.head)
								.print(".slice(start, end).sort(f)))(").printArgList(invocation.args).print(")");
					} else {
						print(invocation.args.head).print(".sort(").printArgList(invocation.args.tail).print(")");
					}
					return true;
				}
				break;
			case "java.lang.System":
				switch (targetMethodName) {
				case "arraycopy":
					printMacroName(targetMethodName);
					getPrinter()
							.print("((srcPts, srcOff, dstPts, dstOff, size) => { if(srcPts !== dstPts || dstOff >= srcOff + size) { while (--size >= 0) dstPts[dstOff++] = srcPts[srcOff++];"
									+ "} else { let tmp = srcPts.slice(srcOff, srcOff + size); for (let i = 0; i < size; i++) dstPts[dstOff++] = tmp[i]; }})(")
							.printArgList(invocation.args).print(")");
					return true;
				}
				break;
			case "java.lang.StringBuffer":
			case "java.lang.StringBuilder":
				switch (targetMethodName) {
				case "append":
					printMacroName(targetMethodName);
					getPrinter().print("(sb => sb.str = sb.str.concat(").printArgList(invocation.args).print("))(")
							.print(fieldAccess.getExpression()).print(")");
					return true;
				case "toString":
					printMacroName(targetMethodName);
					getPrinter().print(fieldAccess.getExpression()).print(".str");
					return true;
				}
				break;
			case "java.lang.ref.WeakReference":
				switch (targetMethodName) {
				case "get":
					getPrinter().print(fieldAccess.getExpression());
					return true;
				}
			case "java.text.Collator":
				switch (targetMethodName) {
				case "getInstance":
					getPrinter().print("((o1, o2) => o1.toString().localeCompare(o2.toString()))");
					return true;
				}
			}

			switch (targetMethodName) {
			case "clone":
				printMacroName(targetMethodName);
				if (fieldAccess != null && fieldAccess.getExpression().type instanceof Type.ArrayType) {
					print(fieldAccess.getExpression()).print(".slice(0)");
					return true;
				}
				break;
			}

			if (typesMapping.containsKey(targetClassName) && targetClassName.startsWith("java.lang.")) {
				if ("clone".equals(targetMethodName)) {
					// TODO: this is very slow (but at least it is
					// compact and simple)...
					print("JSON.parse(JSON.stringify(");
					printTarget(fieldAccess.getExpression());
					print("))");
					return true;
				}
			}
		}

		return super.substituteMethodInvocation(invocation, fieldAccess, targetType, targetClassName, targetMethodName);
	}

	@Override
	protected boolean substituteFieldAccess(JCFieldAccess fieldAccess, TypeSymbol targetType, String accessedType) {
		if (fieldAccess.sym.isStatic() && typesMapping.containsKey(accessedType)
				&& accessedType.startsWith("java.lang.") && !"class".equals(fieldAccess.name.toString())) {

			switch (accessedType) {
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.lang.Integer":
			case "java.lang.Byte":
			case "java.lang.Long":
			case "java.lang.Short":
				switch (fieldAccess.name.toString()) {
				case "MIN_VALUE":
				case "MAX_VALUE":
					print("Number." + fieldAccess.name.toString());
					return true;
				}
			}
		}
		return super.substituteFieldAccess(fieldAccess, targetType, accessedType);
	}

	@Override
	protected boolean substituteNewClass(JCNewClass newClass, String className) {
		switch (className) {
		case "java.util.ArrayList":
		case "java.util.Vector":
			if (newClass.args.isEmpty()) {
				print("[]");
			} else {
				if (Util.isNumber(newClass.args.head.type) || (newClass.args.head instanceof JCLiteral)) {
					print("[]");
				} else {
					print(newClass.args.head).print(".slice(0)");
				}
			}
			return true;
		case "java.util.HashMap":
		case "java.util.Hashtable":
		case "java.util.WeakHashMap":
			print("{}");
			return true;
		case "java.lang.StringBuffer":
		case "java.lang.StringBuilder":
			if (newClass.args.isEmpty() || Util.isNumber(newClass.args.head.type)) {
				getPrinter().print("{ str: \"\", toString: function() { return this.str; } }");
			} else {
				getPrinter().print("{ str: ").print(newClass.args.head)
						.print(", toString: function() { return this.str; } } }");
			}
			return true;
		case "java.lang.ref.WeakReference":
			getPrinter().print(newClass.args.head);
			return true;
		}

		if (className.startsWith("java.")) {
			if (context.types.isSubtype(newClass.clazz.type, context.symtab.throwableType)) {
				print("Object.defineProperty(");
				print("new Error(");
				if (newClass.args.size() > 0) {
					if (String.class.getName().equals(newClass.args.head.type.toString())) {
						print(newClass.args.head);
					} else if (context.types.isSubtype(newClass.args.head.type, context.symtab.throwableType)) {
						print(newClass.args.head).print(".message");
					}
				}
				print(")");
				print(", '" + Java2TypeScriptTranslator.CLASS_NAME_IN_CONSTRUCTOR + "', { configurable: true, value: '")
						.print(className).print("'").print(" })");
				return true;
			}
		}

		return super.substituteNewClass(newClass, className);
	}

	@Override
	public boolean substituteForEachLoop(JCEnhancedForLoop foreachLoop, boolean targetHasLength, String indexVarName) {
		return false;
	}

	@Override
	public boolean eraseSuperClass(JCClassDecl classdecl, ClassSymbol superClass) {
		return superClass.getQualifiedName().toString().startsWith("java.")
				&& !(superClass.type.equals(context.symtab.throwableType)
						|| superClass.type.equals(context.symtab.exceptionType)
						|| superClass.type.equals(context.symtab.runtimeExceptionType)
						|| superClass.type.equals(context.symtab.errorType))
				&& !Util.isSourceType(superClass);
	}

	@Override
	public boolean eraseSuperInterface(JCClassDecl classdecl, ClassSymbol superInterface) {
		return superInterface.getQualifiedName().toString().startsWith("java.") && !Util.isSourceType(superInterface);
	}

	@Override
	public boolean isSubstituteSuperTypes() {
		return true;
	}

	@Override
	public boolean substituteInstanceof(String exprStr, JCTree expr, Type type) {
		if (type.tsym.getQualifiedName().toString().startsWith("java.")
				&& context.types.isSubtype(type, context.symtab.throwableType)) {
			print(exprStr, expr);
			print(" != null && ");
			print("(");
			print(exprStr, expr);
			print("[\"" + Java2TypeScriptTranslator.CLASS_NAME_IN_CONSTRUCTOR + "\"]").print(" == ")
					.print("\"" + type.tsym.getQualifiedName().toString() + "\"");
			print(")");
			return true;
		}

		return super.substituteInstanceof(exprStr, expr, type);
	}

}
