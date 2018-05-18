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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.model.BinaryOperatorElement;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.ForeachLoopElement;
import org.jsweet.transpiler.model.ImportElement;
import org.jsweet.transpiler.model.LiteralElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewArrayElement;
import org.jsweet.transpiler.model.NewClassElement;
import org.jsweet.transpiler.model.VariableAccessElement;
import org.jsweet.transpiler.model.support.ForeachLoopElementSupport;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
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
	private final String ERASED_CLASS_HIERARCHY_FIELD = "__classes";
	private Set<String> excludedJavaSuperTypes = new HashSet<>();

	public RemoveJavaDependenciesAdapter(JSweetContext context) {
		super(context);
		init();
	}

	public RemoveJavaDependenciesAdapter(PrinterAdapter parentAdapter) {
		super(parentAdapter);
		init();
	}

	private void init() {
		addTypeMapping(Class.class.getName(), "any");
		context.getLangTypeMappings().put(RuntimeException.class.getName(), "Error");
		context.getBaseThrowables().add(RuntimeException.class.getName());

		extTypesMapping.put(List.class.getName(), "Array");
		extTypesMapping.put(AbstractList.class.getName(), "Array");
		extTypesMapping.put(ArrayList.class.getName(), "Array");
		extTypesMapping.put(LinkedList.class.getName(), "Array");
		extTypesMapping.put(Collection.class.getName(), "Array");
		extTypesMapping.put(Set.class.getName(), "Array");
		extTypesMapping.put(EnumSet.class.getName(), "Array");
		extTypesMapping.put(Deque.class.getName(), "Array");
		extTypesMapping.put(Queue.class.getName(), "Array");
		extTypesMapping.put(Stack.class.getName(), "Array");
		extTypesMapping.put(HashSet.class.getName(), "Array");
		extTypesMapping.put(TreeSet.class.getName(), "Array");
		extTypesMapping.put(Vector.class.getName(), "Array");
		extTypesMapping.put(Enumeration.class.getName(), "any");
		extTypesMapping.put(Iterator.class.getName(), "any");
		extTypesMapping.put(ListIterator.class.getName(), "any");
		extTypesMapping.put(Map.class.getName(), "any");
		extTypesMapping.put(Properties.class.getName(), "any");
		extTypesMapping.put(AbstractMap.class.getName(), "any");
		extTypesMapping.put(HashMap.class.getName(), "any");
		extTypesMapping.put(TreeMap.class.getName(), "any");
		extTypesMapping.put(WeakHashMap.class.getName(), "any");
		extTypesMapping.put(LinkedHashMap.class.getName(), "any");
		extTypesMapping.put(Hashtable.class.getName(), "any");
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
		extTypesMapping.put(Locale.class.getName(), "string");
		extTypesMapping.put(Charset.class.getName(), "string");
		extTypesMapping.put(Reader.class.getName(), "{ str: string, cursor: number }");
		extTypesMapping.put(StringReader.class.getName(), "{ str: string, cursor: number }");
		extTypesMapping.put(InputStream.class.getName(), "{ str: string, cursor: number }");
		extTypesMapping.put(InputStreamReader.class.getName(), "{ str: string, cursor: number }");
		extTypesMapping.put(BufferedReader.class.getName(), "{ str: string, cursor: number }");

		extTypesMapping.put(Method.class.getName(), "{ owner: any, name: string, fn : Function }");

		addTypeMappings(extTypesMapping);
		addTypeMapping(
				(typeTree,
						name) -> name.startsWith("java.")
								&& types().isSubtype(typeTree.getType(), util().getType(Throwable.class)) ? "Error"
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
								? ((JCTypeApply) ExtendedElementFactory.toTree(typeTree)).arguments.head
								: null);
		excludedJavaSuperTypes.add(EventObject.class.getName());
	}

	@Override
	public String needsImport(ImportElement importElement, String qualifiedName) {
		if (isJDKPath(qualifiedName)) {
			return null;
		}
		return super.needsImport(importElement, qualifiedName);
	}

	protected RemoveJavaDependenciesAdapter print(ExtendedElement expression, boolean delegate) {
		if (delegate) {
			print("(<any>").print(expression).print(").__delegate");
		} else {
			print(expression);
		}
		delegate = false;
		return this;
	}

	@Override
	public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
		String targetMethodName = invocation.getMethodName();
		String targetClassName = invocation.getMethod().getEnclosingElement().toString();
		ExtendedElement targetExpression = invocation.getTargetExpression();
		if (targetExpression != null) {
			targetClassName = targetExpression.getTypeAsElement().toString();
		}
		TypeMirror jdkSuperclass = context.getJdkSuperclass(targetClassName, excludedJavaSuperTypes);
		boolean delegate = jdkSuperclass != null;
		if (delegate) {
			targetClassName = jdkSuperclass.toString();
		}

		if (targetClassName != null
				&& (targetExpression != null || invocation.getMethod().getModifiers().contains(Modifier.STATIC))) {
			switch (targetClassName) {

			case "java.lang.Float":
			case "java.lang.Double":
			case "java.lang.Integer":
			case "java.lang.Byte":
			case "java.lang.Long":
			case "java.lang.Short":
				if (substituteMethodInvocationOnNumber(invocation, targetMethodName)) {
					return true;
				}
				break;
			case "java.lang.Character":
				if (substituteMethodInvocationOnCharacter(invocation, targetMethodName)) {
					return true;
				}
				break;
			case "java.util.Collection":
			case "java.util.List":
			case "java.util.AbstractList":
			case "java.util.AbstractSet":
			case "java.util.AbstractCollection":
			case "java.util.Queue":
			case "java.util.Deque":
			case "java.util.LinkedList":
			case "java.util.ArrayList":
			case "java.util.Stack":
			case "java.util.Vector":
			case "java.util.Set":
			case "java.util.EnumSet":
			case "java.util.HashSet":
			case "java.util.TreeSet":
				if (substituteMethodInvocationOnArray(invocation, targetMethodName, targetClassName, delegate)) {
					return true;
				}
				break;

			case "java.util.Properties":
			case "java.util.Dictionary":
			case "java.util.Map":
			case "java.util.AbstractMap":
			case "java.util.HashMap":
			case "java.util.TreeMap":
			case "java.util.Hashtable":
			case "java.util.WeakHashMap":
			case "java.util.LinkedHashMap":
				if (substituteMethodInvocationOnMap(invocation, targetMethodName, targetExpression, delegate)) {
					return true;
				}
				break;
			case "java.util.Collections":
				if (substituteMethodInvocationOnCollections(invocation, targetMethodName, targetExpression, delegate)) {
					return true;
				}
				break;
			case "java.util.Arrays":
				if (substituteMethodInvocationOnArrays(invocation, targetMethodName, targetExpression, delegate)) {
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
				case "nanoTime":
					printMacroName(targetMethodName);
					print("(Date.now() * 1000000)");
					return true;
				}
				break;

			case "java.util.Objects":
				if (substituteMethodInvocationOnObjects(invocation, targetMethodName, delegate)) {
					return true;
				}
				break;
			case "java.lang.StringBuffer":
			case "java.lang.StringBuilder":
				if (substituteMethodInvocationOnStringBuilder(invocation, targetMethodName, delegate)) {
					return true;
				}
				break;
			case "java.lang.ref.WeakReference":
				switch (targetMethodName) {
				case "get":
					printMacroName(targetMethodName);
					print(invocation.getTargetExpression(), delegate);
					return true;
				}
				break;
			case "java.text.Collator":
				switch (targetMethodName) {
				case "getInstance":
					printMacroName(targetMethodName);
					print("((o1, o2) => o1.toString().localeCompare(o2.toString()))");
					return true;
				}
				break;
			case "java.nio.charset.Charset":
				switch (targetMethodName) {
				case "forName":
					print(invocation.getArgument(0));
					return true;
				}
				break;
			case "java.util.Locale":
				switch (targetMethodName) {
				case "getDefault":
					printMacroName(targetMethodName);
					getPrinter().print("(window.navigator['userLanguage'] || window.navigator.language)");
					return true;
				}
				break;
			case "java.util.TimeZone":
				switch (targetMethodName) {
				case "getTimeZone":
					if (invocation.getArgumentCount() == 1) {
						printMacroName(targetMethodName);
						print(invocation.getArgument(0));
						return true;
					}
					break;
				case "getDefault":
					printMacroName(targetMethodName);
					getPrinter().print("\"UTC\"");
					return true;
				case "getID":
					printMacroName(targetMethodName);
					print(invocation.getTargetExpression(), delegate);
					return true;
				}
				break;
			case "java.util.Calendar":
			case "java.util.GregorianCalendar":
				if (substituteMethodInvocationOnCalendar(invocation, targetMethodName, delegate)) {
					return true;
				}
				break;

			case "java.io.Reader":
			case "java.io.StringReader":
			case "java.io.InputStream":
			case "java.io.InputStreamReader":
			case "java.io.BufferedReader":
				switch (targetMethodName) {
				case "read":
					printMacroName(targetMethodName);
					print("(r => r.str.charCodeAt(r.cursor++))(");
					print(invocation.getTargetExpression(), delegate).print(")");
					return true;
				case "skip":
					printMacroName(targetMethodName);
					print(invocation.getTargetExpression(), delegate).print(".cursor+=")
					.print(invocation.getArgument(0));
					return true;
				case "reset":
					printMacroName(targetMethodName);
					print(invocation.getTargetExpression(), delegate).print(".cursor=0");
					return true;
				case "close":
					printMacroName(targetMethodName);
					// ignore but we could flag it and throw an error...
					return true;
				}
				break;
			case "java.lang.ThreadLocal":
				switch (targetMethodName) {
				case "get":
					printMacroName(targetMethodName);
					print(invocation.getTargetExpression());
					print(".initialValue()");
					return true;
				}
				break;

			case "java.lang.Class":
				if (substituteMethodInvocationOnClass(invocation, targetMethodName, delegate)) {
					return true;
				}
				break;

			case "java.lang.reflect.Method":
				if (substituteMethodInvocationOnMethod(invocation, targetMethodName, delegate)) {
					return true;
				}
				break;

			case "java.lang.reflect.Field":
				if (substituteMethodInvocationOnField(invocation, targetMethodName, delegate)) {
					return true;
				}
				break;

			case "java.lang.reflect.Array":
				switch (targetMethodName) {
				case "newInstance":
					printMacroName(targetMethodName);
					if (invocation.getArgumentCount() == 2) {
						print("new Array<any>(").print(invocation.getArgument(1)).print(")");
						return true;
					}
				case "getLength":
					printMacroName(targetMethodName);
					print(invocation.getArgument(0)).print(".length");
					return true;
				case "get":
					printMacroName(targetMethodName);
					print(invocation.getArgument(0)).print("[").print(invocation.getArgument(1)).print("]");
					return true;
				case "set":
					printMacroName(targetMethodName);
					print("(").print(invocation.getArgument(0)).print("[").print(invocation.getArgument(1)).print("]=")
							.print(invocation.getArgument(1)).print(")");
					return true;
				}
				break;

			case "java.lang.Math":
			case "java.lang.StrictMath":
				switch (targetMethodName) {
				case "ulp":
					printMacroName(targetMethodName);
					print("((x) => { let buffer = new ArrayBuffer(8); let dataView = new DataView(buffer); dataView.setFloat64(0, x); let first = dataView.getUint32(0); let second = dataView.getUint32(4); let rawExponent = first & 0x7ff00000; if (rawExponent == 0x7ff00000) { dataView.setUint32(0,first & 0x7fffffff); } else if (rawExponent == 0) { dataView.setUint32(4,1); dataView.setUint32(0,0); } else if (rawExponent >= (52 << 20) + 0x00100000) { dataView.setUint32(0,rawExponent - (52 << 20)); dataView.setUint32(4,0); } else if (rawExponent >= (33 << 20)) { dataView.setUint32(0,1 << ((rawExponent - (33 << 20))  >>> 20 )); dataView.setUint32(4,0); } else { dataView.setUint32(4,1 << ((rawExponent - 0x00100000)  >>> 20)); dataView.setUint32(0,0); } return dataView.getFloat64(0); })(")
							.printArgList(invocation.getArguments()).print(")");
					return true;
				case "IEEEremainder":
					printMacroName(targetMethodName);
					// credits: Ray Cromwell
					print("((f1, f2) => { let r = Math.abs(f1 % f2); if (isNaN(r) || r == f2 || r <= Math.abs(f2) / 2.0) { return r; } else { return (f1 > 0 ? 1 : -1) * (r - f2); } })(")
							.printArgList(invocation.getArguments()).print(")");
					return true;
				}
			}

			switch (targetMethodName) {
			case "clone":
				printMacroName(targetMethodName);
				if (targetExpression != null && invocation.getTargetExpression().getType() instanceof ArrayType) {
					print(invocation.getTargetExpression(), delegate).print(".slice(0)");
					return true;
				}
				break;
			}

		}

		return super.substituteMethodInvocation(invocation);
	}

	protected boolean substituteMethodInvocationOnObjects(MethodInvocationElement invocation, String targetMethodName,
			boolean delegate) {
		switch (targetMethodName) {
		case "hash":
			printMacroName(targetMethodName);
			print("0");
			return true;
		case "requireNonNull":
			printMacroName(targetMethodName);
			print("if(").print(invocation.getArgument(0)).print("==null){throw new Error('cannot be null')}");
			return true;
		}
		return false;
	}

	protected boolean substituteMethodInvocationOnField(MethodInvocationElement invocation, String targetMethodName,
			boolean delegate) {
		switch (targetMethodName) {
		case "getName":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression()).print(".name");
			return true;
		case "get":
			printMacroName(targetMethodName);
			print(invocation.getArgument(0)).print("[").print(invocation.getTargetExpression()).print(".name")
					.print("]");
			return true;
		case "set":
			printMacroName(targetMethodName);
			print("(").print(invocation.getArgument(0)).print("[").print(invocation.getTargetExpression())
					.print(".name").print("]=").print(invocation.getArgument(1)).print(")");
			return true;
		case "getDeclaringClass":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression()).print(".owner");
			return true;
		case "setAccessible":
			// ignore
			return true;
		}
		return false;
	}

	protected boolean substituteMethodInvocationOnMethod(MethodInvocationElement invocation, String targetMethodName,
			boolean delegate) {
		switch (targetMethodName) {
		case "getName":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression()).print(".name");
			return true;
		case "invoke":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression()).print(".fn.apply(").print(invocation.getArgument(0));
			if (invocation.getArgumentCount() > 1) {
				print(", [").printArgList(invocation.getArgumentTail()).print("]");
			}
			print(")");
			return true;
		case "getDeclaringClass":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression()).print(".owner");
			return true;
		case "setAccessible":
			// ignore
			return true;
		}
		return false;
	}

	protected boolean substituteMethodInvocationOnClass(MethodInvocationElement invocation, String targetMethodName,
			boolean delegate) {

		switch (targetMethodName) {
		case "forName":
			printMacroName(targetMethodName);
			if (getContext().options.getModuleKind() != ModuleKind.none) {
				print("eval(").print(invocation.getArgument(0)).print(".split('.').slice(-1)[0])");
			} else {
				print("eval(").print(invocation.getArgument(0)).print(")");
			}
			return true;
		case "newInstance":
			printMacroName(targetMethodName);
			print("new (");
			print(invocation.getTargetExpression(), delegate).print(")(").printArgList(invocation.getArguments())
					.print(")");
			return true;
		case "isInstance":
			printMacroName(targetMethodName);
			print("((c:any,o:any) => { if(typeof c === 'string') return (o.constructor && o.constructor")
					.print("[\"" + Java2TypeScriptTranslator.INTERFACES_FIELD_NAME + "\"] && o.constructor")
					.print("[\"" + Java2TypeScriptTranslator.INTERFACES_FIELD_NAME + "\"].indexOf(c) >= 0) || (o")
					.print("[\"" + Java2TypeScriptTranslator.INTERFACES_FIELD_NAME + "\"] && o")
					.print("[\"" + Java2TypeScriptTranslator.INTERFACES_FIELD_NAME
							+ "\"].indexOf(c) >= 0); else if(typeof c === 'function') return (o instanceof c) || (o.constructor && o.constructor === c); })(");
			print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
					.print(")");
			return true;
		case "isPrimitive":
			// primitive class types are never used in JSweet, so it
			// will always return false
			printMacroName(targetMethodName);
			print("(").print(invocation.getTargetExpression()).print(" === <any>'__erasedPrimitiveType__'").print(")");
			return true;
		case "getMethods":
		case "getDeclaredMethods":
			printMacroName(targetMethodName);
			print("(c => Object.getOwnPropertyNames(c.prototype).filter(n => typeof c.prototype[n] == 'function').map(n => ({owner:c,name:n,fn:c.prototype[n]}) ) )(")
					.print(invocation.getTargetExpression()).print(")");
			return true;
		case "getMethod":
		case "getDeclaredMethod":
			printMacroName(targetMethodName);
			print("((c,p) => { if(c.prototype.hasOwnProperty(p) && typeof c.prototype[p] == 'function') return {owner:c,name:p,fn:c.prototype[p]}; else return null; })(")
					.print(invocation.getTargetExpression()).print(",").print(invocation.getArgument(0)).print(")");
			return true;
		case "getField":
		case "getDeclaredField":
			printMacroName(targetMethodName);
			print("((c,p) => { return {owner:c,name:p}; })(").print(invocation.getTargetExpression()).print(",")
					.print(invocation.getArgument(0)).print(")");
			return true;
		}
		return false;
	}

	protected boolean substituteMethodInvocationOnArrays(MethodInvocationElement invocation, String targetMethodName,
			ExtendedElement targetExpression, boolean delegate) {
		switch (targetMethodName) {
		case "asList":
			printMacroName(targetMethodName);
			if (invocation.getArgumentCount() == 1 && invocation.getArgument(0).getType() instanceof ArrayType) {
				printArgList(invocation.getArguments()).print(".slice(0)");
			} else {
				print("[").printArgList(invocation.getArguments()).print("]");
			}
			return true;
		case "copyOf":
			printMacroName(targetMethodName);
			print(invocation.getArgument(0)).print(".slice(0,").print(invocation.getArgument(1)).print(")");
			return true;
		case "fill":
			printMacroName(targetMethodName);
			print("((a, v) => { for(let i=0;i<a.length;i++) a[i]=v; })(").printArgList(invocation.getArguments())
					.print(")");
			// ES6 implementation
			// print(invocation.getArgument(0)).print(".fill(").printArgList(invocation.getArgumentTail())
			// .print(")");
			return true;
		case "equals":
			printMacroName(targetMethodName);
			print("((a1, a2) => { if(a1==null && a2==null) return true; if(a1==null || a2==null) return false; if(a1.length != a2.length) return false; for(let i = 0; i < a1.length; i++) { if(<any>a1[i] != <any>a2[i]) return false; } return true; })(")
					.printArgList(invocation.getArguments()).print(")");
			return true;
		case "deepEquals":
			printMacroName(targetMethodName);
			print("(JSON.stringify(").print(invocation.getArgument(0)).print(") === JSON.stringify(")
					.print(invocation.getArgument(1)).print("))");
			return true;
		case "sort":
			printMacroName(targetMethodName);
			if (invocation.getArgumentCount() > 2) {
				print("((arr, start, end, f?) => ((arr1, arr2) => arr1.splice.apply(arr1, (<any[]>[start, arr2.length]).concat(arr2)))(")
						.print(invocation.getArgument(0)).print(", ").print(invocation.getArgument(0))
						.print(".slice(start, end).sort(f)))(").printArgList(invocation.getArguments()).print(")");
			} else if (invocation.getArgumentCount() == 2) {
				print("((l,c) => { if((<any>c).compare) l.sort((e1,e2)=>(<any>c).compare(e1,e2)); else l.sort(<any>c); })(")
						.print(invocation.getArgument(0)).print(",").print(invocation.getArgument(1)).print(")");
			} else {
				print("((l) => {l.sort(); })(").print(invocation.getArgument(0)).print(")");
			}
			return true;
		}

		return false;
	}

	protected boolean substituteMethodInvocationOnCollections(MethodInvocationElement invocation,
			String targetMethodName, ExtendedElement targetExpression, boolean delegate) {
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
			print("[").print(invocation.getArgument(0)).print("]");
			return true;
		case "singletonList":
			printMacroName(targetMethodName);
			print("[").print(invocation.getArgument(0)).print("]");
			return true;
		case "nCopies":
			printMacroName(targetMethodName);
			print("((n,v)=>{let c=[];for(let i=0;i<n;i++)c.push(v);return c;})(");
			print(invocation.getArgument(0));
			print(",");
			print(invocation.getArgument(1));
			print(")");
			return true;
		case "singletonMap":
			printMacroName(targetMethodName);
			if (types().isSameType(invocation.getArgument(0).getType(), util().getType(String.class))) {
				if (invocation.getArgument(0) instanceof JCLiteral) {
					print("{ ").print(invocation.getArgument(0)).print(": ").print(invocation.getArgument(1))
							.print(" }");
				} else {
					print("(k => { let o = {}; o[k] = ").print(invocation.getArgument(1)).print("; return o; })(")
							.print(invocation.getArgument(0)).print(")");
				}
			} else {
				print("(k => { let o = {entries: [{getKey: function() { return this.key }, getValue: function() { return this.value },key:k, value:")
						.print(invocation.getArgument(1)).print("}]}; return o; })(").print(invocation.getArgument(0))
						.print(")");
			}
			return true;
		case "binarySearch":
			printMacroName(targetMethodName);
			if (invocation.getArgumentCount() == 3) {
				print("((l, key, c) => { let comp : any = c; if(typeof c != 'function') { comp = (a,b)=>c.compare(a,b); } let low = 0; let high = l.length-1; while (low <= high) { let mid = (low + high) >>> 1; let midVal = l[mid]; "
						+ "let cmp = comp(midVal, key); if (cmp < 0) low = mid + 1; else if (cmp > 0) high = mid - 1; else return mid; } "
						+ "return -(low + 1); })(").printArgList(invocation.getArguments()).print(")");
				return true;
			}
			if (invocation.getArgumentCount() == 2) {
				if (util().isNumber(invocation.getArgument(1).getType())) {
					print("((l, key) => { let comp = (a,b)=>a-b; let low = 0; let high = l.length-1; while (low <= high) { let mid = (low + high) >>> 1; let midVal = l[mid]; "
							+ "let cmp = comp(midVal, key); if (cmp < 0) low = mid + 1; else if (cmp > 0) high = mid - 1; else return mid; } "
							+ "return -(low + 1); })(").printArgList(invocation.getArguments()).print(")");
					return true;
				} else {
					print("((l, key) => { let comp = (a,b)=>a.localeCompare(b); let low = 0; let high = l.length-1; while (low <= high) { let mid = (low + high) >>> 1; let midVal = l[mid]; "
							+ "let cmp = comp(midVal, key); if (cmp < 0) low = mid + 1; else if (cmp > 0) high = mid - 1; else return mid; } "
							+ "return -(low + 1); })(").printArgList(invocation.getArguments()).print(")");
					return true;
				}
			}
		case "sort":
			printMacroName(targetMethodName);
			if (invocation.getArgumentCount() == 2) {
				print("((l,c) => { if((<any>c).compare) l.sort((e1,e2)=>(<any>c).compare(e1,e2)); else l.sort(<any>c); })(")
						.print(invocation.getArgument(0)).print(",").print(invocation.getArgument(1)).print(")");
			} else {
				print(invocation.getArgument(0)).print(".sort(").printArgList(invocation.getArgumentTail()).print(")");
			}
			return true;
		case "reverse":
			printMacroName(targetMethodName);
			print(invocation.getArgument(0)).print(".reverse()");
			return true;
		case "disjoint":
			printMacroName(targetMethodName);
			print("((c1, c2) => { for(let i=0;i<c1.length;i++) { if(c2.indexOf(<any>c1[i])>=0) return false; } return true; } )(")
					.printArgList(invocation.getArguments()).print(")");
			return true;
		}

		return false;
	}

	private boolean substituteMethodInvocationOnStringBuilder(MethodInvocationElement invocation,
			String targetMethodName, boolean delegate) {
		switch (targetMethodName) {
		case "append":
			printMacroName(targetMethodName);
			if (invocation.getArgumentCount() == 1) {
				print("(sb => { sb.str = sb.str.concat(<any>").printArgList(invocation.getArguments())
						.print("); return sb; })(");
				print(invocation.getTargetExpression(), delegate).print(")");
			} else {
				print("(sb => { sb.str = sb.str.concat((<any>").print(invocation.getArgument(0)).print(").substr(")
						.printArgList(invocation.getArgumentTail()).print(")); return sb; })(");
				print(invocation.getTargetExpression(), delegate).print(")");
			}
			return true;
		case "insert":
			printMacroName(targetMethodName);
			print("((sb, index, c) => { sb.str = sb.str.substr(0, index) + c + sb.str.substr(index); return sb; })(");
			print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
					.print(")");
			return true;
		case "setCharAt":
			printMacroName(targetMethodName);
			print("((sb, index, c) => sb.str = sb.str.substr(0, index) + c + sb.str.substr(index + 1))(");
			print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
					.print(")");
			return true;
		case "deleteCharAt":
			printMacroName(targetMethodName);
			print("((sb, index) => { sb.str = sb.str.substr(0, index) + sb.str.substr(index + 1); return sb; })(");
			print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
					.print(")");
			return true;
		case "delete":
			printMacroName(targetMethodName);
			print("((sb, i1, i2) => { sb.str = sb.str.substr(0, i1) + sb.str.substr(i2); return sb; })(");
			print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
					.print(")");
			return true;
		case "length":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".str.length");
			return true;
		case "charAt":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".str.charAt(").print(invocation.getArgument(0))
					.print(")");
			return true;
		case "setLength":
			printMacroName(targetMethodName);
			print("((sb, length) => sb.str = sb.str.substring(0, length))(");
			print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
					.print(")");
			return true;
		case "toString":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".str");
			return true;
		}
		return false;
	}

	protected boolean substituteMethodInvocationOnCalendar(MethodInvocationElement invocation, String targetMethodName,
			boolean delegate) {
		switch (targetMethodName) {
		case "set":
			if (invocation.getArgumentCount() == 2) {
				String first = invocation.getArgument(0).toString();
				if (first.endsWith("YEAR")) {
					printMacroName(targetMethodName);
					print("((d, p) => d[\"UTC\"]?d.setUTCFullYear(p):d.setFullYear(p))(");
					print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(1))
							.print(")");
					return true;
				} else if (first.endsWith("DAY_OF_MONTH")) {
					printMacroName(targetMethodName);
					print("((d, p) => d[\"UTC\"]?d.setUTCDate(p):d.setDate(p))(");
					print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(1))
							.print(")");
					return true;
				} else if (first.endsWith("DAY_OF_WEEK")) {
					printMacroName(targetMethodName);
					print("((d, p) => d[\"UTC\"]?d.setUTCDay(p):d.setDay(p))(");
					print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(1))
							.print(")");
					return true;
				} else if (first.endsWith("MONTH")) {
					printMacroName(targetMethodName);
					print("((d, p) => d[\"UTC\"]?d.setUTCMonth(p):d.setMonth(p))(");
					print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(1))
							.print(")");
					return true;
				} else if (first.endsWith("HOUR_OF_DAY")) {
					printMacroName(targetMethodName);
					print("((d, p) => d[\"UTC\"]?d.setUTCHours(p):d.setHours(p))(");
					print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(1))
							.print(")");
					return true;
				} else if (first.endsWith("MINUTE")) {
					printMacroName(targetMethodName);
					print("((d, p) => d[\"UTC\"]?d.setUTCMinutes(p):d.setMinutes(p))(");
					print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(1))
							.print(")");
					return true;
				} else if (first.endsWith("MILLISECOND")) {
					printMacroName(targetMethodName);
					print("((d, p) => d[\"UTC\"]?d.setUTCMilliseconds(p):d.setMilliseconds(p))(");
					print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(1))
							.print(")");
					return true;
				} else if (first.endsWith("SECOND")) {
					printMacroName(targetMethodName);
					print("((d, p) => d[\"UTC\"]?d.setUTCSeconds(p):d.setSeconds(p))(");
					print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(1))
							.print(")");
					return true;
				}
			}
			break;
		case "get":
			if (invocation.getArgumentCount() == 1) {
				String first = invocation.getArgument(0).toString();
				if (first.endsWith("YEAR")) {
					printMacroName(targetMethodName);
					print("(d => d[\"UTC\"]?d.getUTCFullYear():d.getFullYear())(");
					print(invocation.getTargetExpression(), delegate).print(")");
					return true;
				} else if (first.endsWith("DAY_OF_MONTH")) {
					printMacroName(targetMethodName);
					print("(d => d[\"UTC\"]?d.getUTCDate():d.getDate())(");
					print(invocation.getTargetExpression(), delegate).print(")");
					return true;
				} else if (first.endsWith("DAY_OF_WEEK")) {
					printMacroName(targetMethodName);
					print("(d => d[\"UTC\"]?d.getUTCDay():d.getDay())(");
					print(invocation.getTargetExpression(), delegate).print(")");
					return true;
				} else if (first.endsWith("MONTH")) {
					printMacroName(targetMethodName);
					print("(d => d[\"UTC\"]?d.getUTCMonth():d.getMonth())(");
					print(invocation.getTargetExpression(), delegate).print(")");
					return true;
				} else if (first.endsWith("HOUR_OF_DAY")) {
					printMacroName(targetMethodName);
					print("(d => d[\"UTC\"]?d.getUTCHours():d.getHours())(");
					print(invocation.getTargetExpression(), delegate).print(")");
					return true;
				} else if (first.endsWith("MINUTE")) {
					printMacroName(targetMethodName);
					print("(d => d[\"UTC\"]?d.getUTCMinutes():d.getMinutes())(");
					print(invocation.getTargetExpression(), delegate).print(")");
					return true;
				} else if (first.endsWith("MILLISECOND")) {
					printMacroName(targetMethodName);
					print("(d => d[\"UTC\"]?d.getUTCMilliseconds():d.getMilliseconds())(");
					print(invocation.getTargetExpression(), delegate).print(")");
					return true;
				} else if (first.endsWith("SECOND")) {
					printMacroName(targetMethodName);
					print("(d => d[\"UTC\"]?d.getUTCSeconds():d.getSeconds())(");
					print(invocation.getTargetExpression(), delegate).print(")");
					return true;
				}
			}
			break;
		case "setTimeInMillis":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".setTime(").print(invocation.getArgument(0))
					.print(")");
			return true;
		case "getTimeInMillis":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".getTime()");
			return true;
		case "setTime":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".setTime(").print(invocation.getArgument(0))
					.print(".getTime())");
			return true;
		case "getTime":
			printMacroName(targetMethodName);
			print("(new Date(");
			print(invocation.getTargetExpression(), delegate).print(".getTime()))");
			return true;
		}
		return false;
	}

	protected boolean substituteMethodInvocationOnMap(MethodInvocationElement invocation, String targetMethodName,
			ExtendedElement targetExpression, boolean delegate) {
		if (targetExpression == null) {
			return false;
		}

		if (((DeclaredType) targetExpression.getType()).getTypeArguments().size() == 2 && types().isSameType(
				((DeclaredType) targetExpression.getType()).getTypeArguments().get(0), util().getType(String.class))) {
			switch (targetMethodName) {
			case "put":
				printMacroName(targetMethodName);
				print("(");
				print(invocation.getTargetExpression(), delegate).print("[").print(invocation.getArgument(0))
						.print("] = ").print(invocation.getArgument(1)).print(")");
				return true;
			case "get":
				printMacroName(targetMethodName);
				print("((m,k) => m[k]===undefined?null:m[k])(");
				print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(0))
						.print(")");
				return true;
			case "containsKey":
				printMacroName(targetMethodName);
				print(invocation.getTargetExpression(), delegate).print(".hasOwnProperty(")
						.print(invocation.getArgument(0)).print(")");
				return true;
			case "keySet":
				printMacroName(targetMethodName);
				print("Object.keys(");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			case "values":
				printMacroName(targetMethodName);
				print("(obj => Object.keys(obj).map(key => obj[key]))(");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			case "size":
				printMacroName(targetMethodName);
				print("Object.keys(");
				print(invocation.getTargetExpression(), delegate).print(").length");
				return true;
			case "isEmpty":
				printMacroName(targetMethodName);
				print("(Object.keys(");
				print(invocation.getTargetExpression(), delegate).print(").length == 0)");
				return true;
			case "remove":
				printMacroName(targetMethodName);
				print("delete ");
				print(invocation.getTargetExpression(), delegate).print("[").print(invocation.getArgument(0))
						.print("]");
				return true;
			case "clear":
				printMacroName(targetMethodName);
				print("(obj => { for (let member in obj) delete obj[member]; })(");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			case "entrySet":
				printMacroName(targetMethodName);
				print("(o => { let s = []; for (let e in o) s.push({ k: e, v: o[e], getKey: function() { return this.k }, getValue: function() { return this.v } }); return s; })(");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			case "clone":
				printMacroName(targetMethodName);
				print("(o => { let c = {}; for (let k in Object.keys(o)){ c[k] = o[k] } return c; })(");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			}
		} else {
			String newEntry = "{key:k,value:v,getKey: function() { return this.key }, getValue: function() { return this.value }}";
			switch (targetMethodName) {
			case "put":
			case "setProperty":
				printMacroName(targetMethodName);
				print("((m,k,v) => { if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) if(m.entries[i].key.equals!=null && m.entries[i].key.equals(k) || m.entries[i].key===k) { m.entries[i].value=v; return; } m.entries.push("
						+ newEntry + "); })(").print("<any>");
				print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
						.print(")");
				return true;
			case "get":
			case "getProperty":
				printMacroName(targetMethodName);
				print("((m,k) => { if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) if(m.entries[i].key.equals!=null && m.entries[i].key.equals(k) || m.entries[i].key===k) { return m.entries[i].value; } return null; })(")
						.print("<any>");
				print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
						.print(")");
				return true;
			case "containsKey":
				printMacroName(targetMethodName);
				print("((m,k) => { if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) if(m.entries[i].key.equals!=null && m.entries[i].key.equals(k) || m.entries[i].key===k) { return true; } return false; })(")
						.print("<any>");
				print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
						.print(")");
				return true;
			case "keySet":
			case "stringPropertyNames":
				printMacroName(targetMethodName);
				print("((m) => { let r=[]; if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) r.push(m.entries[i].key); return r; })(")
						.print("<any>");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			case "values":
				printMacroName(targetMethodName);
				print("((m) => { let r=[]; if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) r.push(m.entries[i].value); return r; })(")
						.print("<any>");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			case "size":
				printMacroName(targetMethodName);
				print("((m) => { if(m.entries==null) m.entries=[]; return m.entries.length; })(").print("<any>");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			case "isEmpty":
				printMacroName(targetMethodName);
				print("((m) => { if(m.entries==null) m.entries=[]; return m.entries.length == 0; })(").print("<any>");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			case "remove":
				printMacroName(targetMethodName);
				print("((m,k) => { if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) if(m.entries[i].key.equals!=null && m.entries[i].key.equals(k) || m.entries[i].key===k) { return m.entries.splice(i,1)[0]; } })(")
						.print("<any>");
				print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
						.print(")");
				return true;
			case "clear":
				printMacroName(targetMethodName);
				print("(<any>");
				print(invocation.getTargetExpression(), delegate).print(").entries=[]");
				return true;
			case "entrySet":
				printMacroName(targetMethodName);
				print("((m) => { if(m.entries==null) m.entries=[]; return m.entries; })(").print("<any>");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			case "clone":
				printMacroName(targetMethodName);
				print("(m => { if(m.entries==null) m.entries=[]; let c = {entries: []}; for(let i=0;i<m.entries.length;i++) { let k = m.entries[i].key, v = m.entries[i].value; c.entries[i] = "
						+ newEntry + "; } return c; })(");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;

			}
		}

		return false;
	}

	protected boolean substituteMethodInvocationOnArray(MethodInvocationElement invocation, String targetMethodName,
			String targetClassName, boolean delegate) {
		switch (targetMethodName) {
		case "add":
		case "addLast":
		case "push":
		case "addElement":
			printMacroName(targetMethodName);
			switch (targetClassName) {
			case "java.util.Set":
			case "java.util.HashSet":
			case "java.util.TreeSet":
				print("((s, e) => { if(s.indexOf(e)==-1) { s.push(e); return true; } else { return false; } })(");
				print(invocation.getTargetExpression(), delegate).print(", ").print(invocation.getArgument(0))
						.print(")");
				break;
			default:
				if (invocation.getArgumentCount() == 2) {
					print(invocation.getTargetExpression(), delegate).print(".splice(").print(invocation.getArgument(0))
							.print(", 0, ").print(invocation.getArgument(1)).print(")");
				} else {
					print("(");
					print(invocation.getTargetExpression(), delegate).print(".push(")
							.printArgList(invocation.getArguments()).print(")>0)");
				}
			}
			return true;
		case "addAll":
			printMacroName(targetMethodName);
			if (invocation.getArgumentCount() == 2) {
				print("((l1, ndx, l2) => { for(let i=l2.length-1;i>=0;i--) l1.splice(ndx,0,l2[i]); })(");
				print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
						.print(")");
			} else {
				print("((l1, l2) => l1.push.apply(l1, l2))(");
				print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
						.print(")");
			}
			return true;
		case "pop":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".pop(").printArgList(invocation.getArguments())
					.print(")");
			return true;
		case "peek":
		case "lastElement":
			printMacroName(targetMethodName);
			print("((s) => { return s[s.length-1]; })(");
			print(invocation.getTargetExpression(), delegate).print(")");
			return true;
		case "remove":
		case "removeElement":
			printMacroName(targetMethodName);
			if (Util.isNumber(invocation.getArgument(0).getType())
					&& types().isSubtype(types().erasure(invocation.getTargetExpression().getType()),
							types().erasure(util().getType(List.class)))) {
				print(invocation.getTargetExpression(), delegate).print(".splice(")
						.printArgList(invocation.getArguments()).print(", 1)");
			} else {
				print("(a => { let index = a.indexOf(").print(invocation.getArgument(0))
						.print("); if(index>=0) { a.splice(index").print(invocation.getArgumentCount() == 1 ? "" : ", ")
						.printArgList(invocation.getArgumentTail())
						.print(", 1); return true; } else { return false; }})(");
				print(invocation.getTargetExpression(), delegate).print(")");
			}
			return true;
		case "removeAll":
			printMacroName(targetMethodName);
			print("((a, r) => { let b=false; for(let i=0;i<r.length;i++) { let ndx=a.indexOf(r[i]); if(ndx>=0) { a.splice(ndx, 1); b=true; } } return b; })(");
			print(invocation.getTargetExpression(), delegate).print(",").print(invocation.getArgument(0)).print(")");
			return true;
		case "containsAll":
			printMacroName(targetMethodName);
			print("((a, r) => { for(let i=0;i<r.length;i++) { if(a.indexOf(<any>r[i])<0) return false; } return true; } )(");
			print(invocation.getTargetExpression(), delegate).print(",").print(invocation.getArgument(0)).print(")");
			return true;
		case "retainAll":
			printMacroName(targetMethodName);
			print("((a, r) => { let b=false; for(let i=0;i<a.length;i++) { let ndx=r.indexOf(a[i]); if(ndx<0) { a.splice(i, 1); i--; b=true; } } return b; })(");
			print(invocation.getTargetExpression(), delegate).print(",").print(invocation.getArgument(0)).print(")");
			return true;
		case "addFirst":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".unshift(").print(invocation.getArgument(0))
					.print(")");
			return true;
		case "poll":
		case "pollFirst":
			printMacroName(targetMethodName);
			print("(a => a.length==0?null:a.shift())(");
			print(invocation.getTargetExpression(), delegate).print(")");
			return true;
		case "pollLast":
			printMacroName(targetMethodName);
			print("(a => a.length==0?null:a.pop())(");
			print(invocation.getTargetExpression(), delegate).print(")");
			return true;
		case "removeElementAt":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".splice(").printArgList(invocation.getArguments())
					.print(", 1)");
			return true;
		case "subList":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print(".slice(").printArgList(invocation.getArguments())
					.print(")");
			return true;
		case "size":
			printMacroName(targetMethodName);
			print("(<number>");
			print(invocation.getTargetExpression(), delegate).print(".length)");
			return true;
		case "get":
		case "elementAt":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression(), delegate).print("[").printArgList(invocation.getArguments())
					.print("]");
			return true;
		case "set":
			printMacroName(targetMethodName);
			print("(");
			print(invocation.getTargetExpression(), delegate).print("[").print(invocation.getArgument(0)).print("] = ")
					.print(invocation.getArgument(1)).print(")");
			return true;
		case "clear":
			printMacroName(targetMethodName);
			print("(");
			print(invocation.getTargetExpression(), delegate).print(".length = 0)");
			return true;
		case "isEmpty":
			printMacroName(targetMethodName);
			print("(");
			print(invocation.getTargetExpression(), delegate).print(".length == 0)");
			return true;
		case "contains":
			printMacroName(targetMethodName);
			print("(");
			print(invocation.getTargetExpression(), delegate).print(".indexOf(<any>(").print(invocation.getArgument(0))
					.print(")) >= 0)");
			return true;
		case "toArray":
			printMacroName(targetMethodName);
			if (invocation.getArgumentCount() == 1) {
				ExtendedElement e = invocation.getArgument(0);
				if (invocation.getTargetExpression() instanceof VariableAccessElement && e instanceof NewArrayElement) {
					NewArrayElement newArray = (NewArrayElement) e;
					boolean simplified = false;
					if (newArray.getDimensionCount() == 1) {
						ExtendedElement d = newArray.getDimension(0);
						if (d.isConstant() && d.toString().equals("0")) {
							simplified = true;
						} else if (d instanceof MethodInvocationElement) {
							if (((MethodInvocationElement) d).getMethodName().equals("size")
									&& ((MethodInvocationElement) d).getTargetExpression().toString()
											.equals(invocation.getTargetExpression().toString())) {
								simplified = true;
							}
						}
					}
					if (simplified) {
						print(invocation.getTargetExpression(), delegate).print(".slice(0)");
						return true;
					}
				}
				print("((a1, a2) => { if(a1.length >= a2.length) { a1.length=0; a1.push.apply(a1, a2); return a1; } else { return a2.slice(0); } })(")
						.print(invocation.getArgument(0)).print(", ");
				print(invocation.getTargetExpression(), delegate).print(")");
				return true;
			} else {
				print(invocation.getTargetExpression(), delegate).print(".slice(0)");
				return true;
			}
		case "elements":
			printMacroName(targetMethodName);
			print("((a) => { var i = 0; return { nextElement: function() { return i<a.length?a[i++]:null; }, hasMoreElements: function() { return i<a.length; }}})(");
			print(invocation.getTargetExpression(), delegate).print(")");
			return true;
		case "iterator":
			printMacroName(targetMethodName);
			print("((a) => { var i = 0; return { next: function() { return i<a.length?a[i++]:null; }, hasNext: function() { return i<a.length; }}})(");
			print(invocation.getTargetExpression(), delegate).print(")");
			return true;
		case "listIterator":
			printMacroName(targetMethodName);
			print("((a) => { var i = 0; return { next: function() { return i<a.length?a[i++]:null; }, hasNext: function() { return i<a.length; }}})(");
			print(invocation.getTargetExpression(), delegate).print(")");
			return true;
		case "ensureCapacity":
			printMacroName(targetMethodName);
			return true;
		case "toString":
			printMacroName(targetMethodName);
			print("('['+");
			print(invocation.getTargetExpression(), delegate).print(".join(', ')+']')");
			return true;
		case "allOf":
			print("function() { " + Java2TypeScriptTranslator.VAR_DECL_KEYWORD + " result: number[] = []; for("
					+ Java2TypeScriptTranslator.VAR_DECL_KEYWORD + " val in ").print(invocation.getArgument(0))
							.print(") { if(!isNaN(<any>val)) { result.push(parseInt(val,10)); } } return result; }()");
			return true;
		case "equals":
			printMacroName(targetMethodName);
			print("((a1, a2) => { if(a1==null && a2==null) return true; if(a1==null || a2==null) return false; if(a1.length != a2.length) return false; for(let i = 0; i < a1.length; i++) { if(<any>a1[i] != <any>a2[i]) return false; } return true; })(");
			print(invocation.getTargetExpression(), delegate).print(", ").printArgList(invocation.getArguments())
					.print(")");
			return true;
		}

		return false;
	}

	protected boolean substituteMethodInvocationOnNumber(MethodInvocationElement invocation, String targetMethodName) {
		switch (targetMethodName) {
		case "parseInt":
		case "parseLong":
		case "parseShort":
		case "parseByte":
			printMacroName(targetMethodName);
			print("parseInt").print("(").printArgList(invocation.getArguments()).print(")");
			return true;
		case "parseFloat":
		case "parseDouble":
			printMacroName(targetMethodName);
			print("parseFloat").print("(").printArgList(invocation.getArguments()).print(")");
			return true;
		case "floatToIntBits":
		case "floatToRawIntBits":
			printMacroName(targetMethodName);
			print("((f) => { let buf = new ArrayBuffer(4); (new Float32Array(buf))[0]=f; return (new Uint32Array(buf))[0]; })(")
					.printArgList(invocation.getArguments()).print(")");
			return true;
		case "intBitsToFloat":
			print("((v) => { let buf = new ArrayBuffer(4); (new Uint32Array(buf))[0]=v; return (new Float32Array(buf))[0]; })(")
					.printArgList(invocation.getArguments()).print(")");
			return true;
		case "doubleToLongBits":
		case "doubleToRawLongBits":
			printMacroName(targetMethodName);
			print("((f) => { let buf = new ArrayBuffer(4); (new Float32Array(buf))[0]=f; return (new Uint32Array(buf))[0]; })((<any>Math).fround(")
					.printArgList(invocation.getArguments()).print("))");
			return true;
		case "longBitsToDouble":
			print("((v) => { let buf = new ArrayBuffer(4); (new Uint32Array(buf))[0]=v; return (new Float32Array(buf))[0]; })(")
					.printArgList(invocation.getArguments()).print(")");
			return true;
		case "valueOf":
			if (util().isNumber(invocation.getArgument(0).getType())) {
				print(invocation.getArgument(0));
				return true;
			} else {
				print("parseFloat").print("(").printArgList(invocation.getArguments()).print(")");
				return true;
			}
		}

		return false;
	}

	protected boolean substituteMethodInvocationOnCharacter(MethodInvocationElement invocation,
			String targetMethodName) {
		switch (targetMethodName) {
		case "isDigit":
			printMacroName(targetMethodName);
			print("/\\d/.test(").printArgList(invocation.getArguments()).print("[0])");
			return true;
		case "isLetter":
			printMacroName(targetMethodName);
			print("/[a-zA-Z]/.test(").printArgList(invocation.getArguments()).print("[0])");
			return true;
		case "isAlphabetic":
			printMacroName(targetMethodName);
			print("/[a-zA-Z]/.test(").printArgList(invocation.getArguments()).print("[0])");
			return true;
		case "isLetterOrDigit":
			printMacroName(targetMethodName);
			print("/[a-zA-Z\\d]/.test(").printArgList(invocation.getArguments()).print("[0])");
			return true;
		case "toLowerCase":
			printMacroName(targetMethodName);
			print(invocation.getArgument(0)).print(".toLowerCase()");
			return true;
		case "toUpperCase":
			printMacroName(targetMethodName);
			print(invocation.getArgument(0)).print(".toUpperCase()");
			return true;
		case "isLowerCase":
			printMacroName(targetMethodName);
			print("(s => s.toLowerCase() === s)(").print(invocation.getArgument(0)).print(")");
			return true;
		case "isUpperCase":
			printMacroName(targetMethodName);
			print("(s => s.toUpperCase() === s)(").print(invocation.getArgument(0)).print(")");
			return true;
		case "charValue":
			printMacroName(targetMethodName);
			print(invocation.getTargetExpression());
			return true;
		}

		return false;
	}

	@Override
	public boolean substituteVariableAccess(VariableAccessElement variableAccess) {
		String targetClassName = variableAccess.getTargetElement().toString();
		if (variableAccess.getVariable().getModifiers().contains(Modifier.STATIC) && isMappedType(targetClassName)
				&& targetClassName.startsWith("java.lang.") && !"class".equals(variableAccess.getVariableName())) {

			switch (targetClassName) {
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.lang.Integer":
			case "java.lang.Byte":
			case "java.lang.Long":
			case "java.lang.Short":
				switch (variableAccess.getVariableName()) {
				case "MIN_VALUE":
				case "MAX_VALUE":
				case "POSITIVE_INFINITY":
				case "NEGATIVE_INFINITY":
					print("Number." + variableAccess.getVariableName());
					return true;
				case "NaN":
					print("NaN");
					return true;
				}
				break;
			case "java.lang.Boolean":
				switch (variableAccess.getVariableName()) {
				case "TRUE":
					print("true");
					return true;
				case "FALSE":
					print("false");
					return true;
				}
			}

		}
		return super.substituteVariableAccess(variableAccess);
	}

	@Override
	public boolean substituteNewClass(NewClassElement newClass) {
		String className = newClass.getTypeAsElement().toString();

		TypeMirror jdkSuperclass = context.getJdkSuperclass(className, excludedJavaSuperTypes);
		boolean extendsJava = jdkSuperclass != null;
		if (extendsJava) {
			className = jdkSuperclass.toString();
			print("(() => { let __o : any = new ").print(newClass.getConstructorAccess()).print("(")
					.printArgList(newClass.getArguments()).print("); __o.__delegate = ");
		}
		boolean substitute = false;

		switch (className) {
		case "java.lang.Integer":
		case "java.lang.Long":
		case "java.lang.Double":
		case "java.lang.Float":
		case "java.long.Short":
		case "java.util.Byte":
			print("new Number(").print(newClass.getArgument(0)).print(").valueOf()");
			substitute = true;
			break;
		case "java.util.ArrayList":
		case "java.util.LinkedList":
		case "java.util.Vector":
		case "java.util.Stack":
		case "java.util.TreeSet":
		case "java.util.HashSet":
		case "java.util.AbstractSet":
		case "java.util.AbstractCollection":
		case "java.util.AbstractList":
		case "java.util.AbstractQueue":
			if (newClass.getArgumentCount() == 0) {
				print("[]");
			} else {
				if (Util.isNumber(newClass.getArgument(0).getType())
						|| (newClass.getArgument(0) instanceof LiteralElement)) {
					print("[]");
				} else {
					print(newClass.getArgument(0)).print(".slice(0)");
				}
			}
			substitute = true;
			break;
		case "java.util.HashMap":
		case "java.util.TreeMap":
		case "java.util.Hashtable":
		case "java.util.WeakHashMap":
		case "java.util.LinkedHashMap":
			if (newClass.getArgumentCount() == 0) {
				print("{}");
			} else {
				if (((DeclaredType) newClass.getType()).getTypeArguments().size() == 2 && types().isSameType(
						((DeclaredType) newClass.getType()).getTypeArguments().get(0), util().getType(String.class))) {
					print("((o) => { let r = {}; for(let p in o) r[p]=o[p]; return r; })(")
							.print(newClass.getArgument(0)).print(")");
				} else {
					print("((o) => { let r = {}; r['entries'] = o.entries!=null?o.entries.slice():null; return r; })(")
							.print(newClass.getArgument(0)).print(")");
				}
			}
			substitute = true;
			break;
		case "java.lang.String":
			if (newClass.getArgumentCount() == 0) {
				print("\"\"");
				return true;
			} else {
				ExtendedElement firstArgument = newClass.getArgument(0);
				if (firstArgument.getType() instanceof ArrayType) {
					if (util().isIntegral(((ArrayType) firstArgument.getType()).getComponentType())) {
						print("String.fromCharCode.apply(null, ").print(firstArgument).print(")");
						if (newClass.getArgumentCount() >= 3 && util().isIntegral(newClass.getArgument(1).getType())
								&& util().isIntegral(newClass.getArgument(2).getType())) {
							print(".substr(").print(newClass.getArgument(1)).print(", ").print(newClass.getArgument(2))
									.print(")");
						}
						return true;
					} else {
						print(firstArgument).print(".join('')");
						if (newClass.getArgumentCount() >= 3 && util().isIntegral(newClass.getArgument(1).getType())
								&& util().isIntegral(newClass.getArgument(2).getType())) {
							print(".substr(").print(newClass.getArgument(1)).print(", ").print(newClass.getArgument(2))
									.print(")");
						}
						return true;
					}
				} else if (StringBuffer.class.getName().equals(firstArgument.getTypeAsElement().toString())
						|| StringBuilder.class.getName().equals(firstArgument.getTypeAsElement().toString())) {
					print(firstArgument).print(".str");
					return true;
				}
			}
			break;
		case "java.lang.StringBuffer":
		case "java.lang.StringBuilder":
			if (newClass.getArgumentCount() == 0 || Util.isNumber(newClass.getArgument(0).getType())) {
				print("{ str: \"\", toString: function() { return this.str; } }");
			} else {
				print("{ str: ").print(newClass.getArgument(0)).print(", toString: function() { return this.str; } }");
			}
			substitute = true;
			break;
		case "java.lang.ref.WeakReference":
			print(newClass.getArgument(0));
			substitute = true;
			break;
		case "java.io.StringReader":
			print("{ str: ").print(newClass.getArgument(0)).print(", cursor: 0 }");
			substitute = true;
			break;
		case "java.io.InputStreamReader":
		case "java.io.BufferedReader":
			print(newClass.getArgument(0));
			substitute = true;
			break;
		case "java.util.GregorianCalendar":
			if (newClass.getArgumentCount() == 0) {
				getPrinter().print("new Date()");
				substitute = true;
				break;
			} else if (newClass.getArgumentCount() == 1
					&& TimeZone.class.getName().equals(newClass.getArgument(0).getType().toString())) {
				if (newClass.getArgument(0) instanceof MethodInvocationElement) {
					MethodInvocationElement inv = (MethodInvocationElement) newClass.getArgument(0);
					if (inv.getMethodName().equals("getTimeZone") && inv.getArgument(0) instanceof LiteralElement
							&& ((LiteralElement) inv.getArgument(0)).getValue().equals("UTC")) {
						getPrinter().print("(d => { d[\"UTC\"]=true; return d; })(new Date())");
						substitute = true;
						break;
					}
				}
			}
			break;
		}

		if (!extendsJava && className.startsWith("java.")) {
			if (types().isSubtype(newClass.getType(), context.symtab.throwableType)) {
				print("Object.defineProperty(");
				print("new Error(");
				if (newClass.getArgumentCount() > 0) {
					if (String.class.getName().equals(newClass.getArgument(0).getType().toString())) {
						print(newClass.getArgument(0));
					} else if (types().isSubtype(newClass.getArgument(0).getType(), context.symtab.throwableType)) {
						print(newClass.getArgument(0)).print(".message");
					}
				}
				print(")");
				Set<String> classes = new HashSet<>();
				context.grabSuperClassNames(classes, newClass.getTypeAsElement());
				print(", '" + ERASED_CLASS_HIERARCHY_FIELD + "', { configurable: true, value: [");
				for (String c : classes) {
					print("'" + c + "',");
				}
				if (!classes.isEmpty()) {
					removeLastChar();
				}
				print("] })");
				return true;
			}
		}

		if (!substitute) {
			substitute = super.substituteNewClass(newClass);
		}

		if (extendsJava) {
			print("; return __o; })()");
		}

		return substitute;

	}

	@Override
	public boolean substituteForEachLoop(ForeachLoopElement foreachLoop, boolean targetHasLength, String indexVarName) {
		JCEnhancedForLoop loop = ((ForeachLoopElementSupport) foreachLoop).getTree();
		if (!targetHasLength && !isJDKPath(loop.expr.type.toString())
				&& types().isSubtype(loop.expr.type, types().erasure(util().getType(Iterable.class)))) {
			printForEachLoop(loop, indexVarName);
			return true;
		}
		// return super.substituteForEachLoop(foreachLoop, targetHasLength,
		// indexVarName);
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
	public boolean substituteInstanceof(String exprStr, ExtendedElement expr, TypeMirror typeMirror) {
		com.sun.tools.javac.code.Type type = (com.sun.tools.javac.code.Type) typeMirror;
		String typeName = type.tsym.getQualifiedName().toString();
		if (typeName.startsWith("java.") && context.types.isSubtype(type, context.symtab.throwableType)) {
			print(exprStr, expr);
			print(" != null && ");
			print("(");
			print(exprStr, expr);
			print("[\"" + ERASED_CLASS_HIERARCHY_FIELD + "\"] && ");
			print(exprStr, expr);
			print("[\"" + ERASED_CLASS_HIERARCHY_FIELD + "\"].indexOf(\"" + type.tsym.getQualifiedName().toString()
					+ "\") >= 0");
			print(")");
			if (context.getBaseThrowables().contains(typeName)) {
				print(" || ");
				return false;
			}
			return true;
		}
		String mappedType = extTypesMapping.get(typeName);
		if ("string".equals(mappedType)) {
			mappedType = "String";
		}
		if ("boolean".equals(mappedType)) {
			mappedType = "Boolean";
		}
		if ("any".equals(mappedType) || (mappedType != null && mappedType.startsWith("{"))) {
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

	@Override
	public boolean substituteBinaryOperator(BinaryOperatorElement binaryOperator) {
		if ("+".equals(binaryOperator.getOperator())) {
			if (types().isSameType(util().getType(String.class), binaryOperator.getOperatorType().getReturnType())) {
				if ("Array".equals(
						extTypesMapping.get(types().erasure(binaryOperator.getLeftHandSide().getType()).toString()))) {
					print("/* implicit toString */ (a => a?'['+a.join(', ')+']':'null')(")
							.print(binaryOperator.getLeftHandSide()).print(") + ")
							.print(binaryOperator.getRightHandSide());
					return true;
				} else if ("Array".equals(
						extTypesMapping.get(types().erasure(binaryOperator.getRightHandSide().getType()).toString()))) {
					print(binaryOperator.getLeftHandSide())
							.print(" + /* implicit toString */ (a => a?'['+a.join(', ')+']':'null')(")
							.print(binaryOperator.getRightHandSide()).print(")");
					return true;
				}
			}
		}
		return super.substituteBinaryOperator(binaryOperator);
	}

}
