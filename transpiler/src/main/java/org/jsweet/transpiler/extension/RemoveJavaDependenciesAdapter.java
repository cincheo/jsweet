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

import static org.jsweet.JSweetConfig.isJDKPath;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.FieldAccessElement;
import org.jsweet.transpiler.model.LiteralElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;

/**
 * An adapter that removes many uses of Java APIs and replace them with
 * JavaScript equivalent when possible.
 * 
 * @author Renaud Pawlak
 */
public class RemoveJavaDependenciesAdapter extends Java2TypeScriptAdapter {

	protected Map<String, String> extTypesMapping = new HashMap<>();

	public RemoveJavaDependenciesAdapter(JSweetContext context) {
		super(context);
		init();
	}

	public RemoveJavaDependenciesAdapter(PrinterAdapter parentAdapter) {
		super(parentAdapter);
		init();
	}

	private void init() {
		extTypesMapping.put(List.class.getName(), "Array");
		extTypesMapping.put(ArrayList.class.getName(), "Array");
		extTypesMapping.put(Collection.class.getName(), "Array");
		extTypesMapping.put(Set.class.getName(), "Array");
		extTypesMapping.put(HashSet.class.getName(), "Array");
		extTypesMapping.put(TreeSet.class.getName(), "Array");
		extTypesMapping.put(Vector.class.getName(), "Array");
		extTypesMapping.put(Enumeration.class.getName(), "any");
		extTypesMapping.put(Iterator.class.getName(), "any");
		extTypesMapping.put(Map.class.getName(), "Object");
		extTypesMapping.put(HashMap.class.getName(), "Object");
		extTypesMapping.put(WeakHashMap.class.getName(), "Object");
		extTypesMapping.put(Hashtable.class.getName(), "Object");
		extTypesMapping.put(Comparator.class.getName(), "any");
		extTypesMapping.put(Exception.class.getName(), "Error");
		extTypesMapping.put(RuntimeException.class.getName(), "Error");
		extTypesMapping.put(Throwable.class.getName(), "Error");
		extTypesMapping.put(Error.class.getName(), "Error");
		extTypesMapping.put(StringBuffer.class.getName(), "{ str: string }");
		extTypesMapping.put(StringBuilder.class.getName(), "{ str: string }");
		extTypesMapping.put(Collator.class.getName(), "any");
		extTypesMapping.put(Calendar.class.getName(), "Date");
		extTypesMapping.put(GregorianCalendar.class.getName(), "Date");
		extTypesMapping.put(TimeZone.class.getName(), "string");
		addTypeMappings(extTypesMapping);
		addTypeMapping(
				(typeTree,
						name) -> name.startsWith("java.")
								&& types().isSubtype(typeTree.asType(), util().getType(Throwable.class)) ? "Error"
										: null);
		// TODO: use standard API
		// addTypeMapping((typeTree,
		// name) -> typeTree.asType() instanceof DeclaredType
		// &&
		// WeakReference.class.getName().equals(types().getQualifiedName(typeTree.asType()))
		// ? ((DeclaredType) typeTree.asType()).getTypeArguments().get(0) :
		// null);
		addTypeMapping((typeTree,
				name) -> ExtendedElementFactory.toTree(typeTree) instanceof JCTypeApply && WeakReference.class.getName()
						.equals(ExtendedElementFactory.toTree(typeTree).type.tsym.getQualifiedName().toString())
								? ((JCTypeApply) ExtendedElementFactory.toTree(typeTree)).arguments.head : null);
	}

	@Override
	public String needsImport(JCImport importDecl, String qualifiedName) {
		if (isJDKPath(qualifiedName)) {
			return null;
		}
		return super.needsImport(importDecl, qualifiedName);
	}

	@Override
	public boolean substituteMethodInvocation(MethodInvocationElement invocation, FieldAccessElement fieldAccess,
			Element targetType, String targetClassName, String targetMethodName) {

		// JCMethodInvocation invocation = invocationElement.getTree();
		// JCFieldAccess fieldAccess = fieldAccessElement.getTree();

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
								.print(", ").print(invocation.getArguments().get(0)).print(")");
						break;
					default:
						if (invocation.getArgumentCount() == 2) {
							print(fieldAccess.getExpression()).print(".splice(").print(invocation.getArguments().get(0))
									.print(", 0, ").print(invocation.getArguments().get(1)).print(")");
						} else {
							print(fieldAccess.getExpression()).print(".push(").printArgList(invocation.getArguments())
									.print(")");
						}
					}
					return true;
				case "addAll":
					printMacroName(targetMethodName);
					print("((l1, l2) => l1.push.apply(l1, l2))(").print(fieldAccess.getExpression()).print(", ")
							.printArgList(invocation.getArguments()).print(")");
					return true;
				case "remove":
					printMacroName(targetMethodName);
					if (Util.isNumber(invocation.getArguments().get(0).asType())) {
						print(fieldAccess.getExpression()).print(".splice(").printArgList(invocation.getArguments())
								.print(", 1)");
					} else {
						print("(a => a.splice(a.indexOf(").print(invocation.getFirstArgument()).print(")")
								.print(invocation.getArgumentCount() == 1 ? "" : ", ")
								.printArgList(invocation.getArgumentTail()).print(", 1))(")
								.print(fieldAccess.getExpression()).print(")");
					}
					return true;
				case "subList":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".slice(").printArgList(invocation.getArguments())
							.print(")");
					return true;
				case "size":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".length");
					return true;
				case "get":
				case "elementAt":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print("[").printArgList(invocation.getArguments()).print("]");
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
					print("(").print(fieldAccess.getExpression()).print(".indexOf(")
							.print(invocation.getArguments().get(0)).print(") >= 0)");
					return true;
				case "toArray":
					printMacroName(targetMethodName);
					if (invocation.getArgumentCount() == 1) {
						print("((a1, a2) => { if(a1.length >= a2.length) { a1.length=0; a1.push.apply(a1, a2); return a1; } else { return a2.slice(0); } })(")
								.print(invocation.getArguments().get(0)).print(", ").print(fieldAccess.getExpression())
								.print(")");
						return true;
					} else {
						print(fieldAccess.getExpression()).print(".slice(0)");
						return true;
					}
				case "elements":
					printMacroName(targetMethodName);
					print("((a) => { var i = 0; return { nextElement: function() { return i<a.length?a[i++]:null; }, hasMoreElements: function() { return i<a.length; }}})(")
							.print(fieldAccess.getExpression()).print(")");
					return true;
				case "iterator":
					printMacroName(targetMethodName);
					print("((a) => { var i = 0; return { next: function() { return i<a.length?a[i++]:null; }, hasNext: function() { return i<a.length; }}})(")
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
					print("(").print(fieldAccess.getExpression()).print("[").print(invocation.getArguments().get(0))
							.print("] = ").print(invocation.getArguments().get(1)).print(")");
					return true;
				case "get":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print("[").print(invocation.getArguments().get(0)).print("]");
					return true;
				case "containsKey":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".hasOwnProperty(").print(invocation.getArguments().get(0))
							.print(")");
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
					print("delete ").print(fieldAccess.getExpression()).print("[")
							.print(invocation.getArguments().get(0)).print("]");
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
				case "unmodifiableCollection":
				case "unmodifiableSet":
				case "unmodifiableSortedSet":
					printMacroName(targetMethodName);
					printArgList(invocation.getArguments()).print(".slice(0)");
					return true;
				case "singleton":
					printMacroName(targetMethodName);
					print("[").print(invocation.getArguments().get(0)).print("]");
					return true;
				case "singletonList":
					printMacroName(targetMethodName);
					print("[").print(invocation.getArguments().get(0)).print("]");
					return true;
				case "singletonMap":
					printMacroName(targetMethodName);
					if (invocation.getArguments().get(0) instanceof JCLiteral) {
						getPrinter().print("{ ").print(invocation.getArguments().get(0)).print(": ")
								.print(invocation.getArguments().get(1)).print(" }");
					} else {
						getPrinter().print("(k => { let o = {}; o[k] = ").print(invocation.getArguments().get(1))
								.print("; return o; })(").print(invocation.getArguments().get(0)).print(")");
					}
					return true;
				case "binarySearch":
					printMacroName(targetMethodName);
					print(invocation.getFirstArgument()).print(".indexOf(").printArgList(invocation.getArgumentTail())
							.print(")");
					return true;
				case "sort":
					printMacroName(targetMethodName);
					print(invocation.getFirstArgument()).print(".sort(").printArgList(invocation.getArgumentTail())
							.print(")");
					return true;
				}
				break;
			case "java.util.Arrays":
				switch (targetMethodName) {
				case "asList":
					printMacroName(targetMethodName);
					if (invocation.getArgumentCount() == 1
							&& invocation.getFirstArgument().asType() instanceof ArrayType) {
						printArgList(invocation.getArguments()).print(".slice(0)");
					} else {
						print("[").printArgList(invocation.getArguments()).print("]");
					}
					return true;
				case "copyOf":
					printMacroName(targetMethodName);
					print(invocation.getFirstArgument()).print(".slice(0,").print(invocation.getArguments().get(1))
							.print(")");
					return true;
				case "equals":
					printMacroName(targetMethodName);
					print("((a1, a2) => { if(a1==null && a2==null) return true; if(a1==null || a2==null) return false; if(a1.length != a2.length) return false; for(let i = 0; i < a1.length; i++) { if(<any>a1[i] != <any>a2[i]) return false; } return true; })(")
							.printArgList(invocation.getArguments()).print(")");
					return true;
				case "deepEquals":
					printMacroName(targetMethodName);
					print("(JSON.stringify(").print(invocation.getFirstArgument()).print(") === JSON.stringify(")
							.print(invocation.getArguments().get(1)).print("))");
					return true;
				case "sort":
					printMacroName(targetMethodName);
					if (invocation.getArgumentCount() > 2) {
						print("((arr, start, end, f?) => ((arr1, arr2) => arr1.splice.apply(arr1, (<any[]>[start, arr2.length]).concat(arr2)))(")
								.print(invocation.getFirstArgument()).print(", ").print(invocation.getFirstArgument())
								.print(".slice(start, end).sort(f)))(").printArgList(invocation.getArguments())
								.print(")");
					} else {
						print(invocation.getFirstArgument()).print(".sort(").printArgList(invocation.getArgumentTail())
								.print(")");
					}
					return true;
				}
				break;
			case "java.lang.System":
				switch (targetMethodName) {
				case "arraycopy":
					printMacroName(targetMethodName);
					print("((srcPts, srcOff, dstPts, dstOff, size) => { if(srcPts !== dstPts || dstOff >= srcOff + size) { while (--size >= 0) dstPts[dstOff++] = srcPts[srcOff++];"
							+ "} else { let tmp = srcPts.slice(srcOff, srcOff + size); for (let i = 0; i < size; i++) dstPts[dstOff++] = tmp[i]; }})(")
									.printArgList(invocation.getArguments()).print(")");
					return true;
				case "currentTimeMillis":
					printMacroName(targetMethodName);
					print("Date.now()");
					return true;
				}
				break;
			case "java.lang.StringBuffer":
			case "java.lang.StringBuilder":
				switch (targetMethodName) {
				case "append":
					printMacroName(targetMethodName);
					print("(sb => sb.str = sb.str.concat(").printArgList(invocation.getArguments()).print("))(")
							.print(fieldAccess.getExpression()).print(")");
					return true;
				case "toString":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".str");
					return true;
				}
				break;
			case "java.lang.ref.WeakReference":
				switch (targetMethodName) {
				case "get":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression());
					return true;
				}
			case "java.text.Collator":
				switch (targetMethodName) {
				case "getInstance":
					printMacroName(targetMethodName);
					print("((o1, o2) => o1.toString().localeCompare(o2.toString()))");
					return true;
				}
			case "java.util.TimeZone":
				switch (targetMethodName) {
				case "getTimeZone":
					if (invocation.getArgumentCount() == 1) {
						printMacroName(targetMethodName);
						getPrinter().print(invocation.getFirstArgument());
						return true;
					}
					break;
				case "getDefault":
					printMacroName(targetMethodName);
					getPrinter().print("\"UTC\"");
					return true;
				case "getID":
					printMacroName(targetMethodName);
					getPrinter().print(fieldAccess.getExpression());
					return true;
				}
			case "java.util.Calendar":
			case "java.util.GregorianCalendar":
				switch (targetMethodName) {
				case "set":
					if (invocation.getArgumentCount() == 2) {
						String first = invocation.getFirstArgument().toString();
						if (first.endsWith("YEAR")) {
							printMacroName(targetMethodName);
							print("((d, p) => d[\"UTC\"]?d.setUTCFullYear(p):d.setFullYear(p))(")
									.print(fieldAccess.getExpression()).print(", ")
									.print(invocation.getArguments().get(1)).print(")");
							return true;
						} else if (first.endsWith("DAY_OF_MONTH")) {
							printMacroName(targetMethodName);
							print("((d, p) => d[\"UTC\"]?d.setUTCDate(p):d.setDate(p))(")
									.print(fieldAccess.getExpression()).print(", ")
									.print(invocation.getArguments().get(1)).print(")");
							return true;
						} else if (first.endsWith("DAY_OF_WEEK")) {
							printMacroName(targetMethodName);
							print("((d, p) => d[\"UTC\"]?d.setUTCDay(p):d.setDay(p))(")
									.print(fieldAccess.getExpression()).print(", ")
									.print(invocation.getArguments().get(1)).print(")");
							return true;
						} else if (first.endsWith("MONTH")) {
							printMacroName(targetMethodName);
							print("((d, p) => d[\"UTC\"]?d.setUTCMonth(p):d.setMonth(p))(")
									.print(fieldAccess.getExpression()).print(", ")
									.print(invocation.getArguments().get(1)).print(")");
							return true;
						} else if (first.endsWith("HOUR_OF_DAY")) {
							printMacroName(targetMethodName);
							print("((d, p) => d[\"UTC\"]?d.setUTCHours(p):d.setHours(p))(")
									.print(fieldAccess.getExpression()).print(", ")
									.print(invocation.getArguments().get(1)).print(")");
							return true;
						} else if (first.endsWith("MINUTE")) {
							printMacroName(targetMethodName);
							print("((d, p) => d[\"UTC\"]?d.setUTCMinutes(p):d.setMinutes(p))(")
									.print(fieldAccess.getExpression()).print(", ")
									.print(invocation.getArguments().get(1)).print(")");
							return true;
						} else if (first.endsWith("MILLISECOND")) {
							printMacroName(targetMethodName);
							print("((d, p) => d[\"UTC\"]?d.setUTCMilliseconds(p):d.setMilliseconds(p))(")
									.print(fieldAccess.getExpression()).print(", ")
									.print(invocation.getArguments().get(1)).print(")");
							return true;
						} else if (first.endsWith("SECOND")) {
							printMacroName(targetMethodName);
							print("((d, p) => d[\"UTC\"]?d.setUTCSeconds(p):d.setSeconds(p))(")
									.print(fieldAccess.getExpression()).print(", ")
									.print(invocation.getArguments().get(1)).print(")");
							return true;
						}
					}
					break;
				case "get":
					if (invocation.getArgumentCount() == 1) {
						String first = invocation.getFirstArgument().toString();
						if (first.endsWith("YEAR")) {
							printMacroName(targetMethodName);
							getPrinter().print("(d => d[\"UTC\"]?d.getUTCFullYear():d.getFullYear())(")
									.print(fieldAccess.getExpression()).print(")");
							return true;
						} else if (first.endsWith("DAY_OF_MONTH")) {
							printMacroName(targetMethodName);
							getPrinter().print("(d => d[\"UTC\"]?d.getUTCDate():d.getDate())(")
									.print(fieldAccess.getExpression()).print(")");
							return true;
						} else if (first.endsWith("DAY_OF_WEEK")) {
							printMacroName(targetMethodName);
							getPrinter().print("(d => d[\"UTC\"]?d.getUTCDay():d.getDay())(")
									.print(fieldAccess.getExpression()).print(")");
							return true;
						} else if (first.endsWith("MONTH")) {
							printMacroName(targetMethodName);
							getPrinter().print("(d => d[\"UTC\"]?d.getUTCMonth():d.getMonth())(")
									.print(fieldAccess.getExpression()).print(")");
							return true;
						} else if (first.endsWith("HOUR_OF_DAY")) {
							printMacroName(targetMethodName);
							getPrinter().print("(d => d[\"UTC\"]?d.getUTCHours():d.getHours())(")
									.print(fieldAccess.getExpression()).print(")");
							return true;
						} else if (first.endsWith("MINUTE")) {
							printMacroName(targetMethodName);
							getPrinter().print("(d => d[\"UTC\"]?d.getUTCMinutes():d.getMinutes())(")
									.print(fieldAccess.getExpression()).print(")");
							return true;
						} else if (first.endsWith("MILLISECOND")) {
							printMacroName(targetMethodName);
							getPrinter().print("(d => d[\"UTC\"]?d.getUTCMilliseconds():d.getMilliseconds())(")
									.print(fieldAccess.getExpression()).print(")");
							return true;
						} else if (first.endsWith("SECOND")) {
							printMacroName(targetMethodName);
							getPrinter().print("(d => d[\"UTC\"]?d.getUTCSeconds():d.getSeconds())(")
									.print(fieldAccess.getExpression()).print(")");
							return true;
						}
					}
					break;
				case "setTimeInMillis":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".setTime(").print(invocation.getFirstArgument())
							.print(")");
					return true;
				case "getTimeInMillis":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".getTime()");
					return true;
				case "setTime":
					printMacroName(targetMethodName);
					print(fieldAccess.getExpression()).print(".setTime(").print(invocation.getFirstArgument())
							.print(".getTime())");
					return true;
				case "getTime":
					printMacroName(targetMethodName);
					getPrinter().print("(new Date(").print(fieldAccess.getExpression()).print(".getTime()))");
					return true;
				}
			}

			switch (targetMethodName) {
			case "clone":
				printMacroName(targetMethodName);
				if (fieldAccess != null && fieldAccess.getExpression().asType() instanceof ArrayType) {
					print(fieldAccess.getExpression()).print(".slice(0)");
					return true;
				}
				break;
			}

		}

		return super.substituteMethodInvocation(invocation, fieldAccess, targetType, targetClassName, targetMethodName);
	}

	@Override
	public boolean substituteFieldAccess(FieldAccessElement fieldAccess, Element targetType, String targetClassName,
			String targetFieldName) {
		if (fieldAccess.getField().getModifiers().contains(Modifier.STATIC) && isMappedType(targetClassName)
				&& targetClassName.startsWith("java.lang.") && !"class".equals(targetFieldName)) {

			switch (targetClassName) {
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.lang.Integer":
			case "java.lang.Byte":
			case "java.lang.Long":
			case "java.lang.Short":
				switch (targetFieldName) {
				case "MIN_VALUE":
				case "MAX_VALUE":
					print("Number." + targetFieldName);
					return true;
				}
			}
		}
		return super.substituteFieldAccess(fieldAccess, targetType, targetClassName, targetFieldName);
	}

	@Override
	public boolean substituteNewClass(NewClassElement newClass, TypeElement type, String className) {
		switch (className) {
		case "java.util.ArrayList":
		case "java.util.Vector":
			if (newClass.getArgumentCount() == 0) {
				print("[]");
			} else {
				if (Util.isNumber(newClass.getFirstArgument().asType())
						|| (newClass.getFirstArgument() instanceof LiteralElement)) {
					print("[]");
				} else {
					getPrinter().print(newClass.getFirstArgument()).print(".slice(0)");
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
			if (newClass.getArgumentCount() == 0 || Util.isNumber(newClass.getFirstArgument().asType())) {
				getPrinter().print("{ str: \"\", toString: function() { return this.str; } }");
			} else {
				getPrinter().print("{ str: ").print(newClass.getFirstArgument())
						.print(", toString: function() { return this.str; } } }");
			}
			return true;
		case "java.lang.ref.WeakReference":
			getPrinter().print(newClass.getFirstArgument());
			return true;
		case "java.util.GregorianCalendar":
			if (newClass.getArgumentCount() == 0) {
				getPrinter().print("new Date()");
				return true;
			} else if (newClass.getArgumentCount() == 1
					&& TimeZone.class.getName().equals(newClass.getFirstArgument().asType().toString())) {
				if (newClass.getFirstArgument() instanceof MethodInvocationElement) {
					MethodInvocationElement inv = (MethodInvocationElement) newClass.getFirstArgument();
					if (inv.getMethodName().equals("getTimeZone") && inv.getFirstArgument() instanceof LiteralElement
							&& ((LiteralElement) inv.getFirstArgument()).getValue().equals("UTC")) {
						getPrinter().print("(d => { d[\"UTC\"]=true; return d; })(new Date())");
						return true;
					}
				}
			}
			break;
		}

		if (className.startsWith("java.")) {
			if (types().isSubtype(type.asType(), context.symtab.throwableType)) {
				print("Object.defineProperty(");
				print("new Error(");
				if (newClass.getArgumentCount() > 0) {
					if (String.class.getName().equals(newClass.getFirstArgument().asType().toString())) {
						getPrinter().print(newClass.getFirstArgument());
					} else if (types().isSubtype(newClass.getFirstArgument().asType(), context.symtab.throwableType)) {
						getPrinter().print(newClass.getFirstArgument()).print(".message");
					}
				}
				print(")");
				print(", '" + Java2TypeScriptTranslator.CLASS_NAME_IN_CONSTRUCTOR + "', { configurable: true, value: '")
						.print(className).print("'").print(" })");
				return true;
			}
		}

		return super.substituteNewClass(newClass, type, className);
	}

	@Override
	public boolean substituteForEachLoop(JCEnhancedForLoop foreachLoop, boolean targetHasLength, String indexVarName) {
		return false;
	}

	@Override
	public boolean eraseSuperClass(TypeElement classdecl, TypeElement superClass) {
		return superClass.getQualifiedName().toString().startsWith("java.")
				&& !(superClass.asType().equals(context.symtab.throwableType)
						|| superClass.asType().equals(context.symtab.exceptionType)
						|| superClass.asType().equals(context.symtab.runtimeExceptionType)
						|| superClass.asType().equals(context.symtab.errorType))
				&& !Util.isSourceElement(superClass);
	}

	@Override
	public boolean eraseSuperInterface(TypeElement classdecl, TypeElement superInterface) {
		return superInterface.getQualifiedName().toString().startsWith("java.")
				&& !Util.isSourceElement(superInterface);
	}

	@Override
	public boolean isSubstituteSuperTypes() {
		return true;
	}

	@Override
	public boolean substituteInstanceof(String exprStr, ExtendedElement expr, Type type) {
		String typeName = type.tsym.getQualifiedName().toString();
		if (typeName.startsWith("java.") && context.types.isSubtype(type, context.symtab.throwableType)) {
			print(exprStr, expr);
			print(" != null && ");
			print("(");
			print(exprStr, expr);
			print("[\"" + Java2TypeScriptTranslator.CLASS_NAME_IN_CONSTRUCTOR + "\"]").print(" == ")
					.print("\"" + type.tsym.getQualifiedName().toString() + "\"");
			print(")");
			return true;
		}
		String mappedType = extTypesMapping.get(typeName);
		if ("string".equals(mappedType)) {
			mappedType = "String";
		}
		if ("any".equals(mappedType)) {
			mappedType = "Object";
		}
		if (mappedType != null) {
			if ("String".equals(mappedType)) {
				print("typeof ");
				print(exprStr, expr);
				print(" === ").print("'string'");
				return true;
			} else {
				print(exprStr, expr);
				print(" != null && ");
				print("(");
				print(exprStr, expr);
				print(" instanceof " + mappedType);
				print(")");
				return true;
			}
		}

		return super.substituteInstanceof(exprStr, expr, type);
	}

}
