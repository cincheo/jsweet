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

import static org.jsweet.JSweetConfig.ANNOTATION_ERASED;
import static org.jsweet.JSweetConfig.ANNOTATION_OBJECT_TYPE;
import static org.jsweet.JSweetConfig.ANNOTATION_STRING_TYPE;
import static org.jsweet.JSweetConfig.DEPRECATED_UTIL_CLASSNAME;
import static org.jsweet.JSweetConfig.GLOBALS_CLASS_NAME;
import static org.jsweet.JSweetConfig.GLOBALS_PACKAGE_NAME;
import static org.jsweet.JSweetConfig.INDEXED_DELETE_FUCTION_NAME;
import static org.jsweet.JSweetConfig.INDEXED_DELETE_STATIC_FUCTION_NAME;
import static org.jsweet.JSweetConfig.INDEXED_GET_FUCTION_NAME;
import static org.jsweet.JSweetConfig.INDEXED_GET_STATIC_FUCTION_NAME;
import static org.jsweet.JSweetConfig.INDEXED_SET_FUCTION_NAME;
import static org.jsweet.JSweetConfig.INDEXED_SET_STATIC_FUCTION_NAME;
import static org.jsweet.JSweetConfig.LANG_PACKAGE;
import static org.jsweet.JSweetConfig.LANG_PACKAGE_ALT;
import static org.jsweet.JSweetConfig.UTIL_CLASSNAME;
import static org.jsweet.JSweetConfig.UTIL_PACKAGE;
import static org.jsweet.JSweetConfig.isJSweetPath;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.Java2TypeScriptTranslator.ComparisonMode;
import org.jsweet.transpiler.TypeChecker;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ForeachLoopElement;
import org.jsweet.transpiler.model.IdentifierElement;
import org.jsweet.transpiler.model.ImportElement;
import org.jsweet.transpiler.model.InvocationElement;
import org.jsweet.transpiler.model.LiteralElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;
import org.jsweet.transpiler.model.VariableAccessElement;
import org.jsweet.transpiler.model.support.ForeachLoopElementSupport;
import org.jsweet.transpiler.model.support.IdentifierElementSupport;
import org.jsweet.transpiler.model.support.ImportElementSupport;
import org.jsweet.transpiler.model.support.MethodInvocationElementSupport;
import org.jsweet.transpiler.model.support.NewClassElementSupport;
import org.jsweet.transpiler.model.support.VariableAccessElementSupport;
import org.jsweet.transpiler.util.Util;

import com.sun.codemodel.internal.JJavaName;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.Tag;

/**
 * This is an adapter for the TypeScript code generator. It overrides the
 * default adapter's behavior.
 * 
 * @author Renaud Pawlak
 */
public class Java2TypeScriptAdapter extends PrinterAdapter {

	private final static String VAR_DECL_KEYWORD = Java2TypeScriptTranslator.VAR_DECL_KEYWORD;

	public Java2TypeScriptTranslator getPrinter() {
		return (Java2TypeScriptTranslator) super.getPrinter();
	}

	/**
	 * Creates a root adapter (with no parent).
	 * 
	 * @param context
	 *            the transpilation context
	 */
	public Java2TypeScriptAdapter(JSweetContext context) {
		super(context);
		init();
	}

	/**
	 * Creates a new adapter that will try delegate to the given parent adapter
	 * when not implementing its own behavior.
	 * 
	 * @param parentAdapter
	 *            cannot be null: if no parent you must use the
	 *            {@link #AbstractPrinterAdapter(JSweetContext)} constructor
	 */
	public Java2TypeScriptAdapter(PrinterAdapter parent) {
		super(parent);
		init();
	}

	private void init() {
		addTypeMapping(Object.class.getName(), "any");
		addTypeMapping(Runnable.class.getName(), "() => void");

		addTypeMapping(DoubleConsumer.class.getName(), "(number) => void");
		addTypeMapping(DoublePredicate.class.getName(), "(number) => boolean");
		addTypeMapping(DoubleSupplier.class.getName(), "() => number");
		addTypeMapping(DoubleBinaryOperator.class.getName(), "(number, number) => number");
		addTypeMapping(DoubleUnaryOperator.class.getName(), "(number) => number");
		addTypeMapping(DoubleToIntFunction.class.getName(), "(number) => number");
		addTypeMapping(DoubleToLongFunction.class.getName(), "(number) => number");

		addTypeMapping(IntConsumer.class.getName(), "(number) => void");
		addTypeMapping(IntPredicate.class.getName(), "(number) => boolean");
		addTypeMapping(IntSupplier.class.getName(), "() => number");
		addTypeMapping(IntBinaryOperator.class.getName(), "(number, number) => number");
		addTypeMapping(IntUnaryOperator.class.getName(), "(number) => number");
		addTypeMapping(IntToDoubleFunction.class.getName(), "(number) => number");
		addTypeMapping(IntToLongFunction.class.getName(), "(number) => number");

		addTypeMapping(LongConsumer.class.getName(), "(number) => void");
		addTypeMapping(LongPredicate.class.getName(), "(number) => boolean");
		addTypeMapping(LongSupplier.class.getName(), "() => number");
		addTypeMapping(LongBinaryOperator.class.getName(), "(number, number) => number");
		addTypeMapping(LongUnaryOperator.class.getName(), "(number) => number");
		addTypeMapping(LongToDoubleFunction.class.getName(), "(number) => number");
		addTypeMapping(LongToIntFunction.class.getName(), "(number) => number");

		addTypeMapping(BooleanSupplier.class.getName(), "() => boolean");

		addTypeMapping(String.class.getName(), "string");
		addTypeMapping(Number.class.getName(), "number");
		addTypeMapping(Integer.class.getName(), "number");
		addTypeMapping(Short.class.getName(), "number");
		addTypeMapping(Float.class.getName(), "number");
		addTypeMapping(Long.class.getName(), "number");
		addTypeMapping(Byte.class.getName(), "number");
		addTypeMapping(Double.class.getName(), "number");
		addTypeMapping(Boolean.class.getName(), "boolean");
		addTypeMapping(Character.class.getName(), "string");
		addTypeMapping(CharSequence.class.getName(), "any");
		addTypeMapping(Void.class.getName(), "void");
		addTypeMapping("double", "number");
		addTypeMapping("int", "number");
		addTypeMapping("float", "number");
		addTypeMapping("long", "number");
		addTypeMapping("byte", "number");
		addTypeMapping("short", "number");
		addTypeMapping("char", "string");
		addTypeMapping("Class", "any");
		addTypeMapping(LANG_PACKAGE + ".Object", "Object");
		addTypeMapping(LANG_PACKAGE + ".Boolean", "boolean");
		addTypeMapping(LANG_PACKAGE + ".String", "string");
		addTypeMapping(LANG_PACKAGE + ".Number", "number");
		addTypeMapping(LANG_PACKAGE_ALT + ".Object", "Object");
		addTypeMapping(LANG_PACKAGE_ALT + ".Boolean", "boolean");
		addTypeMapping(LANG_PACKAGE_ALT + ".String", "string");
		addTypeMapping(LANG_PACKAGE_ALT + ".Number", "number");

		context.getLangTypeMappings().put(Object.class.getName(), "Object");
		context.getLangTypeMappings().put(String.class.getName(), "String");
		context.getLangTypeMappings().put(Boolean.class.getName(), "Boolean");
		context.getLangTypeMappings().put(Number.class.getName(), "Number");
		context.getLangTypeMappings().put(Integer.class.getName(), "Number");
		context.getLangTypeMappings().put(Long.class.getName(), "Number");
		context.getLangTypeMappings().put(Short.class.getName(), "Number");
		context.getLangTypeMappings().put(Float.class.getName(), "Number");
		context.getLangTypeMappings().put(Double.class.getName(), "Number");
		context.getLangTypeMappings().put(Byte.class.getName(), "Number");
		context.getLangTypeMappings().put(Character.class.getName(), "String");
		context.getLangTypeMappings().put(Math.class.getName(), "Math");
		context.getLangTypeMappings().put(StrictMath.class.getName(), "Math");
		context.getLangTypeMappings().put(Exception.class.getName(), "Error");
		context.getLangTypeMappings().put(Throwable.class.getName(), "Error");
		context.getLangTypeMappings().put(Error.class.getName(), "Error");
		context.getLangTypeMappings().put(Date.class.getName(), "Date");

		for (String s : context.getLangTypeMappings().keySet()) {
			context.getLangTypesSimpleNames().add(s.substring(s.lastIndexOf('.') + 1));
		}

		context.getBaseThrowables().add(Throwable.class.getName());
		context.getBaseThrowables().add(Error.class.getName());
		context.getBaseThrowables().add(Exception.class.getName());

	}

	@Override
	public String needsImport(ImportElement importElement, String qualifiedName) {
		JCImport importDecl = ((ImportElementSupport) importElement).getTree();
		if (isJSweetPath(qualifiedName) || isMappedType(qualifiedName)
				|| context.getLangTypeMappings().containsKey(qualifiedName)
				|| qualifiedName.startsWith("java.util.function.")
				|| qualifiedName.endsWith(GLOBALS_PACKAGE_NAME + "." + GLOBALS_CLASS_NAME)) {
			return null;
		}
		if (importDecl.qualid.type != null) {
			if (context.hasAnnotationType(importDecl.qualid.type.tsym, ANNOTATION_ERASED, ANNOTATION_OBJECT_TYPE)) {
				return null;
			}
			if (importDecl.qualid.type.tsym.getKind() == ElementKind.ANNOTATION_TYPE
					&& !context.hasAnnotationType(importDecl.qualid.type.tsym, JSweetConfig.ANNOTATION_DECORATOR)) {
				return null;
			}
		}
		if (importDecl.isStatic()) {
			if (importDecl.getQualifiedIdentifier() instanceof JCFieldAccess) {
				JCFieldAccess fa = (JCFieldAccess) importDecl.getQualifiedIdentifier();
				switch (fa.selected.toString()) {
				case "java.lang.Math":
					return null;
				}
				String name = getPrinter().getRootRelativeName(fa.selected.type.tsym, false);
				String methodName = fa.name.toString();

				// function is a top-level global function (no need to import)
				if (GLOBALS_CLASS_NAME.equals(name)) {
					return null;
				}
				if (!context.useModules && name.endsWith(GLOBALS_PACKAGE_NAME + "." + GLOBALS_CLASS_NAME)) {
					return null;
				}

				if (JSweetConfig.TS_STRICT_MODE_KEYWORDS.contains(methodName.toLowerCase())) {
					// if method name is a reserved ts keyword, we have to fully
					// qualify calls to it (hence discarding any import)
					return null;
				}
				boolean globals = name.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME);
				if (globals) {
					name = name.substring(0, name.length() - JSweetConfig.GLOBALS_CLASS_NAME.length() - 1);
				}
				// function belong to the current package (no need to
				// import)
				String current = getPrinter().getRootRelativeName(getPrinter().getCompilationUnit().packge,
						context.useModules);
				if (context.useModules) {
					if (current.equals(name)) {
						return null;
					}
				} else {
					if (current.startsWith(name)) {
						return null;
					}
				}
				Symbol nameSymbol = fa.sym;
				if (nameSymbol == null) {
					TypeSymbol t = fa.selected.type.tsym;
					nameSymbol = Util.findFirstDeclarationInType(t, methodName);
				}

				return StringUtils.isBlank(name) ? null
						: name + "." + (nameSymbol == null ? methodName : getPrinter().getIdentifier(nameSymbol));
			} else {
				return null;
			}
		} else {
			if (context.useModules) {
				// check if inner class and do not import
				if (importDecl.qualid instanceof JCFieldAccess) {
					JCFieldAccess qualified = (JCFieldAccess) importDecl.qualid;
					if (qualified.sym instanceof ClassSymbol
							&& qualified.sym.getEnclosingElement() instanceof ClassSymbol) {
						return null;
					}
				}
			}
		}
		if (importElement.isStatic()) {
			return null;
		} else {
			return super.needsImport(importElement, qualifiedName);
		}
	}

	private boolean isWithinGlobals(String targetClassName) {
		if (targetClassName == null
				|| (targetClassName.equals(UTIL_CLASSNAME) || targetClassName.equals(DEPRECATED_UTIL_CLASSNAME))) {
			JCClassDecl c = getPrinter().getParent(JCClassDecl.class);
			return c != null && c.sym.getQualifiedName().toString().endsWith("." + GLOBALS_CLASS_NAME);
		} else {
			return false;
		}
	}

	@Override
	public boolean substituteMethodInvocation(MethodInvocationElement invocationElement) {

		Element targetType = invocationElement.getMethod().getEnclosingElement();
		// This is some sort of hack to avoid invoking erased methods.
		// If the containing class is erased, we still invoke it because we
		// don't know if the class may be provided externally.
		// Pitfalls: (1) may erase invocations that are provided externally, (2)
		// if the invocation is the target of an enclosing invocation or field
		// access, TS compilation will fail.
		// So, we should probably find a better way to erase invocations (or at
		// least do it conditionally).
		if (hasAnnotationType(invocationElement.getMethod(), ANNOTATION_ERASED)
				&& !isAmbientDeclaration(invocationElement.getMethod())) {
			print("null");
			return true;
		}
		if (invocationElement.getTargetExpression() != null) {
			targetType = invocationElement.getTargetExpression().getTypeAsElement();
		}
		String targetMethodName = invocationElement.getMethodName();
		String targetClassName = targetType.toString();

		if ("println".equals(targetMethodName)) {
			if (invocationElement.getTargetExpression() != null) {
				if ("System.out".equals(invocationElement.getTargetExpression().toString())) {
					PrinterAdapter print = print("console.info(");
					if (invocationElement.getArgumentCount() > 0)
						print.print(invocationElement.getArgument(0));
					print.print(")");
					return true;
				}
				if ("System.err".equals(invocationElement.getTargetExpression().toString())) {
					PrinterAdapter print = print("console.error(");
					if (invocationElement.getArgumentCount() > 0)
						print.print(invocationElement.getArgument(0));
					print.print(")");
					return true;
				}
			}
		}

		if ("super".equals(invocationElement.getMethodName())) {
			// we omit call to super if class extends nothing or if parent is an
			// interface
			if (getPrinter().getParent(JCClassDecl.class).extending == null //
					|| context.isInterface(getPrinter().getParent(JCClassDecl.class).extending.type.tsym)) {
				return true;
			}
			// special case when subclassing a Java exception type
			if (((MethodInvocationElementSupport) invocationElement).getTree().meth instanceof JCIdent) {
				String superClassName = ((JCIdent) ((MethodInvocationElementSupport) invocationElement)
						.getTree().meth).sym.getEnclosingElement().getQualifiedName().toString();
				if (context.getBaseThrowables().contains(superClassName)) {
					// ES6 would take the cause, but we ignore it so far for
					// backward compatibility
					// PATCH:
					// https://github.com/Microsoft/TypeScript/issues/5069
					if (invocationElement.getArgumentCount() > 0) {
						print("super(").print(invocationElement.getArgument(0)).print(")");
						print("; this.message=").print(invocationElement.getArgument(0));
					} else {
						print("super()");
					}
					return true;
				}
			}
		}

		if (targetType != null && targetType.getKind() == ElementKind.ENUM
				&& (invocationElement.getTargetExpression() != null
						&& !"this".equals(invocationElement.getTargetExpression().toString()))) {
			String relTarget = getRootRelativeName((Symbol) targetType);
			switch (targetMethodName) {
			case "name":
				printMacroName("Enum." + targetMethodName);
				print(relTarget).print("[").print(invocationElement.getTargetExpression()).print("]");
				return true;
			case "ordinal":
				printMacroName("Enum." + targetMethodName);
				print(relTarget).print("[").print(relTarget).print("[").print(invocationElement.getTargetExpression())
						.print("]").print("]");
				return true;
			case "valueOf":
				printMacroName("Enum." + targetMethodName);
				if (invocationElement.getArgumentCount() == 1) {
					print("<any>").print(invocationElement.getTargetExpression()).print("[")
							.print(invocationElement.getArgument(0)).print("]");
					return true;
				}
				break;
			case "values":
				printMacroName("Enum." + targetMethodName);
				print("function() { " + VAR_DECL_KEYWORD + " result: number[] = []; for(" + VAR_DECL_KEYWORD
						+ " val in ").print(relTarget).print(
								") { if(!isNaN(<any>val)) { result.push(parseInt(val,10)); } } return result; }()");
				return true;
			case "equals":
				printMacroName("Enum." + targetMethodName);
				print("(<any>(").print(invocationElement.getTargetExpression()).print(") === <any>(")
						.print(invocationElement.getArgument(0)).print("))");
				return true;
			}
			// enum objets wrapping
			if (invocationElement.getTargetExpression() != null) {
				if (invocationElement.getMethod().getModifiers().contains(Modifier.STATIC)) {
					print(invocationElement.getTargetExpression())
							.print(Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_SUFFIX + ".")
							.print(invocationElement.getMethodName()).print("(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				}
			}
			print(relTarget).print("[\"" + Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_WRAPPERS + "\"][")
					.print(invocationElement.getTargetExpression()).print("].").print(invocationElement.getMethodName())
					.print("(").printArgList(invocationElement.getArguments()).print(")");
			return true;
		}

		if (targetClassName != null && targetMethodName != null) {
			switch (targetClassName) {
			case UTIL_CLASSNAME:
			case DEPRECATED_UTIL_CLASSNAME:
				switch (targetMethodName) {
				case "$export":
					if (!invocationElement.getArgument(0).isStringLiteral()) {
						report(invocationElement.getArgument(0), JSweetProblem.STRING_LITERAL_EXPECTED);
					}
					String varName = "_exportedVar_"
							+ StringUtils.strip(invocationElement.getArgument(0).toString(), "\"");
					getPrinter().footer.append(VAR_DECL_KEYWORD + " " + varName + ";\n");
					if (invocationElement.getArgumentCount() == 1) {
						print(varName);
					} else {
						print("{ " + varName + " = ").print(invocationElement.getArgument(1)).print("; ");
						print("console.log('" + JSweetTranspiler.EXPORTED_VAR_BEGIN
								+ StringUtils.strip(invocationElement.getArgument(0).toString(), "\"") + "='+")
										.print(varName).print("+'" + JSweetTranspiler.EXPORTED_VAR_END + "') }");
					}
					return true;

				case "array":
				case "function":
				case "string":
				case "bool":
				case "number":
				case "integer":
				case "object":
					printCastMethodInvocation(invocationElement);
					return true;

				case "any":
					print("(<any>");
					printCastMethodInvocation(invocationElement);
					print(")");
					return true;

				case "async":
					print("async ");
					print(invocationElement.getArgument(0));
					return true;

				case "await":
					print("await ");
					printCastMethodInvocation(invocationElement);
					return true;
					
				case "asyncReturn":
					printCastMethodInvocation(invocationElement);
					return true;

				case "union":
					getPrinter().typeChecker.checkUnionTypeAssignment(context.types, getPrinter().getParent(),
							((MethodInvocationElementSupport) invocationElement).getTree());
					print("(<any>");
					printCastMethodInvocation(invocationElement);
					print(")");
					return true;

				case "typeof":
					print("typeof ").print(invocationElement.getArgument(0));
					return true;

				case "$noarrow":
					print(invocationElement.getArgument(0));
					return true;

				case "equalsStrict":
					print("(").print(invocationElement.getArgument(0)).print(" === ")
							.print(invocationElement.getArgument(1)).print(")");
					return true;

				case "notEqualsStrict":
					print("(").print(invocationElement.getArgument(0)).print(" !== ")
							.print(invocationElement.getArgument(1)).print(")");
					return true;

				case "equalsLoose":
					print("(").print(invocationElement.getArgument(0)).print(" == ")
							.print(invocationElement.getArgument(1)).print(")");
					return true;

				case "notEqualsLoose":
					print("(").print(invocationElement.getArgument(0)).print(" != ")
							.print(invocationElement.getArgument(1)).print(")");
					return true;

				case "$strict":
					getPrinter().enterComparisonMode(ComparisonMode.STRICT);
					print(invocationElement.getArgument(0));
					getPrinter().exitComparisonMode();
					return true;

				case "$loose":
					getPrinter().enterComparisonMode(ComparisonMode.LOOSE);
					print(invocationElement.getArgument(0));
					getPrinter().exitComparisonMode();
					return true;

				case "$insert":
					if (invocationElement.getArgument(0) instanceof LiteralElement) {
						print(((LiteralElement) invocationElement.getArgument(0)).getValue().toString());
						return true;
					} else {
						report(invocationElement, JSweetProblem.MISUSED_INSERT_MACRO,
								invocationElement.getMethodName());
					}

				case "$template":
					if (invocationElement.getArgumentCount() == 1) {
						if (invocationElement.getArgument(0) instanceof LiteralElement) {
							print("`" + ((LiteralElement) invocationElement.getArgument(0)).getValue().toString()
									+ "`");
							return true;
						} else {
							if (invocationElement.getArgument(1) instanceof LiteralElement) {
								print(invocationElement.getArgument(0)).print(
										"`" + ((LiteralElement) invocationElement.getArgument(1)).getValue().toString()
												+ "`");
								return true;
							}
						}
					}
					report(invocationElement, JSweetProblem.MISUSED_INSERT_MACRO, invocationElement.getMethodName());

				case "$map":
					if (invocationElement.getArgumentCount() % 2 != 0) {
						report(invocationElement, JSweetProblem.UNTYPED_OBJECT_ODD_PARAMETER_COUNT);
					}
					print("{");
					com.sun.tools.javac.util.List<JCExpression> args = ((MethodInvocationElementSupport) invocationElement)
							.getTree().args;
					while (args != null && args.head != null) {
						String key = args.head.toString();
						if (args.head.getTag() == Tag.LITERAL && key.startsWith("\"")) {
							key = key.substring(1, key.length() - 1);
							if (JJavaName.isJavaIdentifier(key)) {
								print(key);
							} else {
								print("\"" + key + "\"");
							}
						} else {
							report(invocationElement.getArgument(0), JSweetProblem.UNTYPED_OBJECT_WRONG_KEY,
									args.head.toString());
						}
						print(": ");
						getPrinter().print(args.tail.head);
						;
						args = args.tail.tail;
						if (args != null && args.head != null) {
							print(",");
						}
					}
					print("}");
					return true;

				case "$array":
					print("[").printArgList(invocationElement.getArguments()).print("]");
					return true;

				case "$apply":
					print("(<any>").print(invocationElement.getArgument(0)).print(")(")
							.printArgList(invocationElement.getArgumentTail()).print(")");
					return true;
				case "$new":
					print("new (<any>").print(invocationElement.getArgument(0)).print(")(")
							.printArgList(invocationElement.getArgumentTail()).print(")");
					return true;
				}
			}
		}

		if (targetMethodName != null) {
			switch (targetMethodName) {
			case INDEXED_GET_FUCTION_NAME:
				if (isWithinGlobals(targetClassName)) {
					if (invocationElement.getArgumentCount() == 1) {
						report(invocationElement, JSweetProblem.GLOBAL_INDEXER_GET);
						return true;
					} else {
						if (invocationElement.getArgument(0).toString().equals(GLOBALS_CLASS_NAME + ".class")
								|| invocationElement.getArgument(0).toString()
										.endsWith("." + GLOBALS_CLASS_NAME + ".class")) {
							report(invocationElement, JSweetProblem.GLOBAL_INDEXER_GET);
							return true;
						}
					}
				}

				if (invocationElement.getTargetExpression() != null && !(UTIL_CLASSNAME.equals(targetClassName)
						|| DEPRECATED_UTIL_CLASSNAME.equals(targetClassName))) {
					print(invocationElement.getTargetExpression()).print("[").print(invocationElement.getArgument(0))
							.print("]");
				} else {
					if (invocationElement.getArgumentCount() == 1) {
						print("this[").print(invocationElement.getArguments().get(0)).print("]");
					} else {
						print(invocationElement.getArguments().get(0)).print("[")
								.print(invocationElement.getArguments().get(1)).print("]");
					}
				}
				return true;
			case INDEXED_GET_STATIC_FUCTION_NAME:
				if (invocationElement.getArgumentCount() == 1 && isWithinGlobals(targetClassName)) {
					report(invocationElement, JSweetProblem.GLOBAL_INDEXER_GET);
					return true;
				}

				print(invocationElement.getTargetExpression()).print("[").print(invocationElement.getArgument(0))
						.print("]");
				return true;

			case INDEXED_SET_FUCTION_NAME:
				if (isWithinGlobals(targetClassName)) {
					if (invocationElement.getArgumentCount() == 2) {
						report(invocationElement, JSweetProblem.GLOBAL_INDEXER_SET);
						return true;
					} else {
						if (invocationElement.getArgument(0).toString().equals(GLOBALS_CLASS_NAME + ".class")
								|| invocationElement.getArgument(0).toString()
										.endsWith(GLOBALS_CLASS_NAME + ".class")) {
							report(invocationElement, JSweetProblem.GLOBAL_INDEXER_SET);
							return true;
						}
					}
				}

				if (invocationElement.getTargetExpression() != null && !(UTIL_CLASSNAME.equals(targetClassName)
						|| DEPRECATED_UTIL_CLASSNAME.equals(targetClassName))) {
					// check the type through the getter
					for (Element e : invocationElement.getTargetExpression().getTypeAsElement().getEnclosedElements()) {
						if (e instanceof ExecutableElement
								&& INDEXED_GET_FUCTION_NAME.equals(e.getSimpleName().toString())) {
							ExecutableElement getter = (ExecutableElement) e;
							TypeMirror getterType = getter.getReturnType();
							TypeMirror getterIndexType = getter.getParameters().get(0).asType();

							TypeMirror invokedIndexType = invocationElement.getArgument(0).getType();
							TypeMirror invokedValueType = invocationElement.getArgument(1).getType();

							boolean sameIndexType = types().isSameType(getterIndexType, invokedIndexType);

							if (sameIndexType && !types().isAssignable(invokedValueType, types().erasure(getterType))) {
								report(invocationElement.getArgument(1), JSweetProblem.INDEXED_SET_TYPE_MISMATCH,
										getterType);
							}
						}
					}

					print(invocationElement.getTargetExpression()).print("[").print(invocationElement.getArgument(0))
							.print("] = ").print(invocationElement.getArgument(1));
				} else {
					if (invocationElement.getArgumentCount() == 2) {
						print("this[").print(invocationElement.getArgument(0)).print("] = <any>")
								.print(invocationElement.getArgument(1));
					} else {
						print(invocationElement.getArgument(0)).print("[").print(invocationElement.getArgument(1))
								.print("] = <any>").print(invocationElement.getArgument(2));
					}
				}
				return true;

			case INDEXED_SET_STATIC_FUCTION_NAME:

				if (invocationElement.getArgumentCount() == 2 && isWithinGlobals(targetClassName)) {
					report(invocationElement, JSweetProblem.GLOBAL_INDEXER_SET);
					return true;
				}

				print(invocationElement.getTargetExpression()).print("[").print(invocationElement.getArguments().get(0))
						.print("] = ").print(invocationElement.getArguments().get(1));
				return true;

			case INDEXED_DELETE_FUCTION_NAME:
				if (isWithinGlobals(targetClassName)) {
					if (invocationElement.getArgumentCount() == 1) {
						report(invocationElement, JSweetProblem.GLOBAL_DELETE);
						return true;
					} else {
						if (invocationElement.getArgument(0).toString().equals(GLOBALS_CLASS_NAME + ".class")
								|| invocationElement.getArguments().get(0).toString()
										.endsWith(GLOBALS_CLASS_NAME + ".class")) {
							report(invocationElement, JSweetProblem.GLOBAL_DELETE);
							return true;
						}
					}
				}

				if (invocationElement.getTargetExpression() != null && !(UTIL_CLASSNAME.equals(targetClassName)
						|| DEPRECATED_UTIL_CLASSNAME.equals(targetClassName))) {
					print("delete ").print(invocationElement.getTargetExpression()).print("[")
							.print(invocationElement.getArguments().get(0)).print("]");
				} else {
					if (invocationElement.getArgumentCount() == 1) {
						print("delete this[").print(invocationElement.getArgument(0)).print("]");
					} else {
						print("delete ").print(invocationElement.getArgument(0)).print("[")
								.print(invocationElement.getArgument(1)).print("]");
					}
				}
				return true;

			case INDEXED_DELETE_STATIC_FUCTION_NAME:
				if (invocationElement.getArgumentCount() == 1 && isWithinGlobals(targetClassName)) {
					report(invocationElement, JSweetProblem.GLOBAL_DELETE);
					return true;
				}

				if (invocationElement.getTargetExpression() != null && !(UTIL_CLASSNAME.equals(targetClassName)
						|| DEPRECATED_UTIL_CLASSNAME.equals(targetClassName))) {
					print("delete ").print(invocationElement.getTargetExpression()).print("[")
							.print(invocationElement.getArgument(0)).print("]");
				} else {
					if (invocationElement.getArgumentCount() == 1) {
						print("delete ").print("this[").print(invocationElement.getArgument(0)).print("]");
					} else {
						print("delete ").print(invocationElement.getArgument(0)).print("[")
								.print(invocationElement.getArgument(1)).print("]");
					}
				}
				return true;
			}

		}

		if (invocationElement.getTargetExpression() == null && "$super".equals(targetMethodName)) {
			print("super(").printArgList(invocationElement.getArguments()).print(")");
			return true;
		}
		if (invocationElement.getTargetExpression() != null && targetClassName != null
				&& (targetClassName.startsWith(UTIL_PACKAGE + ".function.")
						|| targetClassName.startsWith(Function.class.getPackage().getName()))) {
			if (!TypeChecker.jdkAllowed && targetClassName.startsWith(Function.class.getPackage().getName())
					&& TypeChecker.FORBIDDEN_JDK_FUNCTIONAL_METHODS.contains(targetMethodName)) {
				report(invocationElement, JSweetProblem.JDK_METHOD, targetMethodName);
			}
			printFunctionalInvocation(invocationElement.getTargetExpression(), targetMethodName,
					invocationElement.getArguments());
			return true;
		}
		if (invocationElement.getTargetExpression() != null && targetClassName != null
				&& targetClassName.equals(java.lang.Runnable.class.getName())) {
			printFunctionalInvocation(invocationElement.getTargetExpression(), targetMethodName,
					invocationElement.getArguments());
			return true;
		}

		// built-in Java support

		if (targetClassName != null) {

			// expand macros
			switch (targetMethodName) {
			case "getMessage":
				if (targetType instanceof TypeElement) {
					if (types().isAssignable(targetType.asType(), util().getType(Throwable.class))) {
						printTarget(invocationElement.getTargetExpression()).print(".message");
						return true;
					}
				}
				break;
			case "getCause":
				if (targetType instanceof TypeElement) {
					if (types().isAssignable(targetType.asType(), util().getType(Throwable.class))) {
						print("(<Error>null)");
						return true;
					}
				}
				break;
			case "printStackTrace":
				if (targetType instanceof TypeElement) {
					if (types().isAssignable(targetType.asType(), util().getType(Throwable.class))) {
						print("console.error(").print(invocationElement.getTargetExpression()).print(".message, ")
								.print(invocationElement.getTargetExpression()).print(")");
						return true;
					}
				}
				break;
			}

			switch (targetClassName) {
			case "java.lang.String":
			case "java.lang.CharSequence":
				switch (targetMethodName) {
				case "valueOf":
					printMacroName(targetMethodName);
					if (invocationElement.getArgumentCount() == 3) {
						print("((str, index, len) => str.join('').substring(index, index + len))(")
								.printArgList(invocationElement.getArguments()).print(")");
					} else {
						print("new String(").printArgList(invocationElement.getArguments()).print(").toString()");
					}
					return true;
				case "subSequence":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression()).print(".substring(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				// this macro should use 'includes' in ES6
				case "contains":
					printMacroName(targetMethodName);
					print("(").print(invocationElement.getTargetExpression()).print(".indexOf(")
							.printArgList(invocationElement.getArguments()).print(") != -1)");
					return true;
				case "length":
					print(invocationElement.getTargetExpression()).print(".length");
					return true;
				// this macro is not needed in ES6
				case "startsWith":
					printMacroName(targetMethodName);
					print("((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(")
							.print(invocationElement.getTargetExpression()).print(", ")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "endsWith":
					printMacroName(targetMethodName);
					print("((str, searchString) => { " + VAR_DECL_KEYWORD + " pos = str.length - searchString.length; "
							+ VAR_DECL_KEYWORD
							+ " lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(")
									.print(invocationElement.getTargetExpression()).print(", ")
									.printArgList(invocationElement.getArguments()).print(")");
					return true;
				// this macro is not needed in ES6
				case "codePointAt":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression()).print(".charCodeAt(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "isEmpty":
					printMacroName(targetMethodName);
					print("(").print(invocationElement.getTargetExpression()).print(".length === 0)");
					return true;
				case "compareToIgnoreCase":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression()).print(".toUpperCase().localeCompare(")
							.printArgList(invocationElement.getArguments()).print(".toUpperCase())");
					return true;
				case "compareTo":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression()).print(".localeCompare(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "equalsIgnoreCase":
					printMacroName(targetMethodName);
					print("((o1, o2) => o1.toUpperCase() === (o2===null?o2:o2.toUpperCase()))(")
							.print(invocationElement.getTargetExpression()).print(", ")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "toChars":
					printMacroName(targetMethodName);
					print("String.fromCharCode(").printArgList(invocationElement.getArguments()).print(")");
					return true;
				// In ES6, we can use the Array.from method
				case "getBytes":
					printMacroName(targetMethodName);
					print("(").print(invocationElement.getTargetExpression())
							.print(").split('').map(s => s.charCodeAt(0))");
					return true;
				// In ES6, we can use the Array.from method
				case "toCharArray":
					printMacroName(targetMethodName);
					print("(").print(invocationElement.getTargetExpression()).print(").split('')");
					return true;
				case "getChars":
					printMacroName(targetMethodName);
					print("((a, s, e, d, l) => { d.splice.apply(d, [l, e-s].concat(<any>a.substring(s, e).split(''))); })(")
							.print(invocationElement.getTargetExpression()).print(", ")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "replaceAll":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression()).print(".replace(new RegExp(")
							.print(invocationElement.getArguments().get(0)).print(", 'g'),")
							.print(invocationElement.getArguments().get(1)).print(")");
					return true;
				case "replace":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression()).print(".split(")
							.print(invocationElement.getArguments().get(0)).print(").join(")
							.print(invocationElement.getArguments().get(1)).print(")");
					return true;
				case "lastIndexOf":
					print(invocationElement.getTargetExpression()).print(".lastIndexOf(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "indexOf":
					if (invocationElement.getArgumentCount() == 1
							&& util().isNumber(invocationElement.getArgument(0).getType())) {
						print(invocationElement.getTargetExpression()).print(".indexOf(String.fromCharCode(")
								.print(invocationElement.getArgument(0)).print("))");
					} else {
						print(invocationElement.getTargetExpression()).print(".indexOf(")
								.printArgList(invocationElement.getArguments()).print(")");
					}
					return true;
				case "toLowerCase":
					if (invocationElement.getArgumentCount() > 0) {
						printMacroName(targetMethodName);
						print(invocationElement.getTargetExpression()).print(".toLowerCase()");
						return true;
					}
					break;
				case "toUpperCase":
					if (invocationElement.getArgumentCount() > 0) {
						printMacroName(targetMethodName);
						print(invocationElement.getTargetExpression()).print(".toUpperCase()");
						return true;
					}
					break;
				}
				break;
			case "java.lang.Character":
				switch (targetMethodName) {
				case "toChars":
					printMacroName(targetMethodName);
					print("String.fromCharCode(").printArgList(invocationElement.getArguments()).print(")");
					return true;
				}
				break;
			case "java.lang.Number":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.lang.Integer":
			case "java.lang.Byte":
			case "java.lang.Long":
			case "java.lang.Short":
				switch (targetMethodName) {
				case "isNaN":
					printMacroName(targetMethodName);
					if (invocationElement.getArgumentCount() > 0) {
						print("isNaN(").printArgList(invocationElement.getArguments()).print(")");
						return true;
					} else {
						print("isNaN(").print(invocationElement.getTargetExpression()).print(")");
						return true;
					}
				case "isInfinite":
					printMacroName(targetMethodName);
					if (invocationElement.getArgumentCount() > 0) {
						print("((value) => Number.NEGATIVE_INFINITY === value || Number.POSITIVE_INFINITY === value)(")
								.printArgList(invocationElement.getArguments()).print(")");
						return true;
					} else {
						print("((value) => Number.NEGATIVE_INFINITY === value || Number.POSITIVE_INFINITY === value)(")
								.print(invocationElement.getTargetExpression()).print(")");
						return true;
					}
				case "intValue":
					printMacroName(targetMethodName);
					print("(").print(invocationElement.getTargetExpression()).print("|0").print(")");
					return true;
				case "shortValue":
					printMacroName(targetMethodName);
					print("(").print(invocationElement.getTargetExpression()).print("|0").print(")");
					return true;
				case "byteValue":
					printMacroName(targetMethodName);
					print("(").print(invocationElement.getTargetExpression()).print("|0").print(")");
					return true;
				case "floatValue":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression());
					return true;
				case "doubleValue":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression());
					return true;
				case "longValue":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression());
					return true;
				case "compare":
					if (invocationElement.getArgumentCount() == 2) {
						printMacroName(targetMethodName);
						print("(").print(invocationElement.getArgument(0)).print(" - ")
								.print(invocationElement.getArgument(1)).print(")");
						return true;
					}
					break;
				case "toString":
					if (invocationElement.getArgumentCount() > 0) {
						printMacroName(targetMethodName);
						print("(''+(").print(invocationElement.getArgument(0)).print("))");
						return true;
					}
				}
				break;
			case "java.lang.Boolean":
				switch (targetMethodName) {
				case "booleanValue":
					printMacroName(targetMethodName);
					print(invocationElement.getTargetExpression());
					return true;
				}
				break;
			case "java.lang.StrictMath":
			case "java.lang.Math":
				switch (targetMethodName) {
				case "cbrt":
					printMacroName(targetMethodName);
					print("Math.pow(").printArgList(invocationElement.getArguments()).print(", 1/3)");
					return true;
				case "copySign":
					printMacroName(targetMethodName);
					print("((magnitude, sign) => { if (sign < 0) { return (magnitude < 0) ? magnitude : -magnitude; } else { return (magnitude > 0) ? magnitude : -magnitude; } })(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "cosh":
					printMacroName(targetMethodName);
					print("(x => (Math.exp(x) + Math.exp(-x)) / 2)(").printArgList(invocationElement.getArguments())
							.print(")");
					return true;
				case "expm1":
					printMacroName(targetMethodName);
					print("(d => { if (d == 0.0 || d === Number.NaN) { return d; } else if (!Number.POSITIVE_INFINITY === d && !Number.NEGATIVE_INFINITY === d) { if (d < 0) { return -1; } else { return Number.POSITIVE_INFINITY; } } })(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "hypot":
					printMacroName(targetMethodName);
					print("(x => Math.sqrt(x * x + y * y))(").printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "log10":
					printMacroName(targetMethodName);
					print("(x => Math.log(x) * Math.LOG10E)(").printArgList(invocationElement.getArguments())
							.print(")");
					return true;
				case "log1p":
					printMacroName(targetMethodName);
					print("(x => Math.log(x + 1))(").printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "rint":
					printMacroName(targetMethodName);
					print("(d => { if (d === Number.NaN) { return d; } else if (Number.POSITIVE_INFINITY === d || Number.NEGATIVE_INFINITY === d) { return d; } else if(d == 0) { return d; } else { return Math.round(d); } })(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "scalb":
					printMacroName(targetMethodName);
					print("((d, scaleFactor) => { if (scaleFactor >= 31 || scaleFactor <= -31) { return d * Math.pow(2, scaleFactor); } else if (scaleFactor > 0) { return d * (1 << scaleFactor); } else if (scaleFactor == 0) { return d; } else { return d * 1 / (1 << -scaleFactor); } })(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "signum":
					printMacroName(targetMethodName);
					print("(f => { if (f > 0) { return 1; } else if (f < 0) { return -1; } else { return 0; } })(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "sinh":
					printMacroName(targetMethodName);
					print("(x => (Math.exp(x) - Math.exp(-x)) / 2)(").printArgList(invocationElement.getArguments())
							.print(")");
					return true;
				case "tanh":
					printMacroName(targetMethodName);
					print("(x => { if (x == Number.POSITIVE_INFINITY) { return 1; } else if (x == Number.NEGATIVE_INFINITY) { return -1; } double e2x = Math.exp(2 * x); return (e2x - 1) / (e2x + 1); })(")
							.printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "toDegrees":
					printMacroName(targetMethodName);
					print("(x => x * 180 / Math.PI)(").printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "toRadians":
					printMacroName(targetMethodName);
					print("(x => x * Math.PI / 180)(").printArgList(invocationElement.getArguments()).print(")");
					return true;
				case "nextUp":
					delegateToEmulLayer(targetClassName, targetMethodName, invocationElement);
					return true;
				case "nextDown":
					delegateToEmulLayer(targetClassName, targetMethodName, invocationElement);
					return true;
				case "ulp":
					delegateToEmulLayer(targetClassName, targetMethodName, invocationElement);
					return true;
				case "IEEEremainder":
					delegateToEmulLayer(targetClassName, targetMethodName, invocationElement);
					return true;
				default:
					print("Math." + targetMethodName + "(").printArgList(invocationElement.getArguments()).print(")");
					return true;
				}

			case "java.lang.Class":
				switch (targetMethodName) {
				case "getName":
					printMacroName(targetMethodName);
					getPrinter().print("(c => c[\"" + Java2TypeScriptTranslator.CLASS_NAME_IN_CONSTRUCTOR + "\"]?c[\""
							+ Java2TypeScriptTranslator.CLASS_NAME_IN_CONSTRUCTOR + "\"]:c[\"name\"])(");
					printTarget(invocationElement.getTargetExpression());
					print(")");
					return true;
				case "getSimpleName":
					printMacroName(targetMethodName);
					print("(c => c[\"" + Java2TypeScriptTranslator.CLASS_NAME_IN_CONSTRUCTOR + "\"]?c[\""
							+ Java2TypeScriptTranslator.CLASS_NAME_IN_CONSTRUCTOR + "\"].substring(c[\""
							+ Java2TypeScriptTranslator.CLASS_NAME_IN_CONSTRUCTOR
							+ "\"].lastIndexOf('.')+1):c[\"name\"].substring(c[\"name\"].lastIndexOf('.')+1))(");
					printTarget(invocationElement.getTargetExpression());
					print(")");
					return true;
				}
				break;

			// case "java.util.Date":
			// switch (targetMethodName) {
			// case "setFullYear":
			// printMacroName(targetMethodName);
			// print(fieldAccess.getExpression()).print(".setYear(").printArgList(invocation.args).print(")");
			//
			// }
			// break;
			}

			if (invocationElement.getTargetExpression() != null && isMappedType(targetClassName)
					&& targetClassName.startsWith("java.lang.")) {
				if (invocationElement.getMethod().getModifiers().contains(Modifier.STATIC)) {
					// delegation to javaemul
					delegateToEmulLayer(targetClassName, targetMethodName, invocationElement);
					return true;
				} else {
					switch (targetMethodName) {
					case "equals":
						printMacroName(targetMethodName);
						print("(<any>((o1: any, o2: any) => { if(o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(");
						printTarget(invocationElement.getTargetExpression()).print(",")
								.print(invocationElement.getArgument(0));
						print("))");
						return true;
					}
				}
			}

		}

		switch (targetMethodName) {
		case "getClass":
			print("(<any>");
			printTarget(invocationElement.getTargetExpression());
			print(".constructor)");
			return true;
		case "hashCode":
			printMacroName(targetMethodName);
			print("(<any>((o: any) => { if(o.hashCode) { return o.hashCode(); } else { return o.toString(); } })(");
			printTarget(invocationElement.getTargetExpression());
			print("))");
			return true;
		case "equals":
			if (invocationElement.getTargetExpression() != null) {
				MethodSymbol methSym = Util.findMethodDeclarationInType(context.types,
						(TypeSymbol) invocationElement.getTargetExpression().getTypeAsElement(), targetMethodName,
						(MethodType) invocationElement.getMethod().asType());
				if (methSym != null
						&& (Object.class.getName().equals(methSym.getEnclosingElement().toString())
								|| methSym.getEnclosingElement().isInterface())
						|| invocationElement.getTargetExpression().getTypeAsElement()
								.getKind() == ElementKind.INTERFACE) {
					printMacroName(targetMethodName);
					print("(<any>((o1: any, o2: any) => { if(o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(");
					printTarget(invocationElement.getTargetExpression()).print(",")
							.print(invocationElement.getArgument(0));
					print("))");
					return true;
				}
			}
			break;
		case "clone":
			if (!invocationElement.getMethod().getModifiers().contains(Modifier.STATIC)
					&& invocationElement.getArgumentCount() == 0) {
				printMacroName(targetMethodName);
				if (invocationElement.getTargetExpression() != null
						&& "super".equals(invocationElement.getTargetExpression().toString())) {
					JCClassDecl parent = getPrinter().getParent(JCClassDecl.class);
					if (parent.sym.getSuperclass() != null
							&& !context.symtab.objectType.equals(parent.sym.getSuperclass())) {
						print("((o:any) => { if(super.clone!=undefined) { return super.clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(this)");
					} else {
						print("((o:any) => { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; })(this)");
					}
				} else {
					print("((o:any) => { if(o.clone!=undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(");
					printTarget(invocationElement.getTargetExpression());
					print(")");
				}
				return true;
			}
		}

		getPrinter().printDefaultMethodInvocation(((MethodInvocationElementSupport) invocationElement).getTree());

		return true;

	}

	protected void printFunctionalInvocation(ExtendedElement target, String functionName,
			List<ExtendedElement> arguments) {
		if (target instanceof IdentifierElement) {
			print("(typeof ").print(target).print(" === 'function'?target").print("(").printArgList(arguments)
					.print("):(<any>target).").print(functionName).print("(").printArgList(arguments).print("))");
		} else {
			print("(target => (typeof target === 'function')?target").print("(").printArgList(arguments)
					.print("):(<any>target).").print(functionName).print("(").printArgList(arguments).print("))(")
					.print(target).print(")");
		}
	}

	protected final PrinterAdapter printTarget(ExtendedElement target) {
		if (target == null) {
			return print("this");
		} else if ("super".equals(target.toString())) {
			return print("this");
		} else {
			return print(target);
		}
	}

	protected final void delegateToEmulLayer(String targetClassName, String targetMethodName,
			InvocationElement invocation) {
		print("javaemul.internal." + targetClassName.substring(10) + "Helper.").print(targetMethodName).print("(")
				.printArgList(invocation.getArguments()).print(")");
	}

	protected final void delegateToEmulLayerStatic(String targetClassName, String targetMethodName,
			ExtendedElement target) {
		print("javaemul.internal." + targetClassName.substring(10) + "Helper.").print(targetMethodName).print("(");
		printTarget(target).print(")");
	}

	protected final void printCastMethodInvocation(InvocationElement invocation) {
		boolean needsParens =getPrinter().getParent() instanceof JCMethodInvocation;
		if (needsParens) {
			// async needs no parens to work
			JCMethodInvocation parentInvocation = (JCMethodInvocation)getPrinter().getParent();
			if (parentInvocation.meth instanceof JCIdent) {
				needsParens = !((JCIdent)parentInvocation.meth).getName().toString().equals("async");
			}
		}
		if (needsParens) {
			print("(");
		}
		print(invocation.getArgument(0));
		if (needsParens) {
			print(")");
		}
	}

	@Override
	public boolean substituteVariableAccess(VariableAccessElement variableAccess) {
		if (variableAccess.getTypeAsElement().getKind() == ElementKind.ENUM
				&& "this".equals(variableAccess.getVariableName())
				&& !(getParentElement() instanceof VariableAccessElement
						|| getParentElement() instanceof MethodInvocationElement)) {
			print("this.").print(Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_ORDINAL);
			return true;
		}

		if (variableAccess.getTargetExpression() != null) {
			JCFieldAccess fieldAccess = (JCFieldAccess) ((VariableAccessElementSupport) variableAccess).getTree();
			String targetFieldName = variableAccess.getVariableName();
			Element targetType = variableAccess.getTargetElement();

			// automatic static field access target redirection
			if (!"class".equals(variableAccess.getVariableName())
					&& variableAccess.getVariable().getModifiers().contains(Modifier.STATIC)) {
				if (isMappedType(targetType.toString())
						&& !context.getLangTypeMappings().containsKey(targetType.toString())) {
					print(getTypeMappingTarget(targetType.toString()) + ".").print(variableAccess.getVariableName());
					return true;
				}
			}

			// translate tuple accesses
			if (targetFieldName.startsWith("$") && targetFieldName.length() > 1
					&& Character.isDigit(targetFieldName.charAt(1))) {
				try {
					int i = Integer.parseInt(targetFieldName.substring(1));
					print(variableAccess.getTargetExpression());
					print("[" + i + "]");
					return true;
				} catch (NumberFormatException e) {
					// swallow
				}
			}

			if (hasAnnotationType(variableAccess.getVariable(), ANNOTATION_STRING_TYPE)) {
				print("\"");
				print(getAnnotationValue(variableAccess.getVariable(), ANNOTATION_STRING_TYPE, String.class,
						variableAccess.getVariableName()));
				print("\"");
				return true;
			}

			if (fieldAccess.selected.toString().equals("this")) {
				if (fieldAccess.sym.isStatic()) {
					report(variableAccess, JSweetProblem.CANNOT_ACCESS_STATIC_MEMBER_ON_THIS, fieldAccess.name);
				}
			}

			// enum objects wrapping
			if (targetType != null && targetType.getKind() == ElementKind.ENUM && !fieldAccess.sym.isEnum()
					&& !"this".equals(fieldAccess.selected.toString()) && !"class".equals(targetFieldName)) {
				String relTarget = getRootRelativeName((Symbol) targetType);
				getPrinter().print(relTarget)
						.print("[\"" + Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_WRAPPERS + "\"][")
						.print(fieldAccess.selected).print("].").print(fieldAccess.name.toString());
				return true;
			}

			// built-in Java support
			String accessedType = ((Symbol) targetType).getQualifiedName().toString();
			if (fieldAccess.sym.isStatic() && isMappedType(accessedType) && accessedType.startsWith("java.lang.")
					&& !"class".equals(fieldAccess.name.toString())) {
				delegateToEmulLayer(accessedType, variableAccess);
				return true;
			}
		} else {
			if (JSweetConfig.UTIL_CLASSNAME.equals(variableAccess.getTargetElement().toString())) {
				if ("$this".equals(variableAccess.getVariableName())) {
					print("this");
					return true;
				}
			}
			JCIdent identifier = (JCIdent) ((VariableAccessElementSupport) variableAccess).getTree();
			if (context.hasAnnotationType(identifier.sym, ANNOTATION_STRING_TYPE)) {
				print("\"");
				getPrinter().print((String) context.getAnnotationValue(identifier.sym, ANNOTATION_STRING_TYPE,
						String.class, identifier.toString()));
				print("\"");
				return true;
			}
		}
		return super.substituteVariableAccess(variableAccess);
	}

	protected final void delegateToEmulLayer(String targetClassName, VariableAccessElement fieldAccess) {
		print("javaemul.internal." + targetClassName.substring(10) + "Helper.").print(fieldAccess.getVariableName());
	}

	@Override
	public boolean substituteNewClass(NewClassElement newClassElement) {
		JCNewClass newClass = ((NewClassElementSupport) newClassElement).getTree();
		String className = newClassElement.getTypeAsElement().toString();
		if (className.startsWith(JSweetConfig.TUPLE_CLASSES_PACKAGE + ".")) {
			getPrinter().print("[").printArgList(null, newClass.args).print("]");
			return true;
		}

		if (isMappedType(className)) {

			print("<").print(getTypeMappingTarget(className));
			if (newClass.clazz instanceof JCTypeApply) {
				List<JCExpression> typeArgs = ((JCTypeApply) newClass.clazz).arguments;
				if (typeArgs.size() > 0) {
					getPrinter().print("<").printTypeArgList(typeArgs).print(">");
				}
			}
			print(">");
		}
		// macros
		if (newClass.clazz.type.equals(context.symtab.stringType)) {
			if (newClass.args.length() >= 3) {
				getPrinter().print("((str, index, len) => ").print("str.substring(index, index + len))((")
						.print(newClass.args.head).print(")");
				if ("byte[]".equals(newClass.args.get(0).type.toString())) {
					print(".map(s => String.fromCharCode(s))");
				}
				print(".join(''), ");
				getPrinter().print(newClass.args.tail.head).print(", ").print(newClass.args.tail.tail.head).print(")");
				return true;
			}
		}

		getPrinter().printDefaultNewClass(newClass);
		return true;

	}

	@Override
	public boolean substituteIdentifier(IdentifierElement identifierElement) {
		JCIdent identifier = ((IdentifierElementSupport) identifierElement).getTree();
		if (identifier.type != null) {
			if (context.getLangTypesSimpleNames().contains(identifier.toString())
					&& context.getLangTypeMappings().containsKey(identifier.type.toString())) {
				print(context.getLangTypeMappings().get(identifier.type.toString()));
				return true;
			}
			if (identifier.type.toString().startsWith("java.lang.")) {
				if (("java.lang." + identifier.toString()).equals(identifier.type.toString())) {
					// it is a java.lang class being referenced, so we expand
					// its name
					print(identifier.type.toString());
					return true;
				}
			}
		}
		return super.substituteIdentifier(identifierElement);
	}

	@Override
	public Set<String> getErasedTypes() {
		return context.getLangTypeMappings().keySet();
	}

	protected void printForEachLoop(JCEnhancedForLoop loop, String indexVarName) {
		getPrinter().print("for(" + VAR_DECL_KEYWORD + " " + indexVarName + "=").print(loop.expr)
				.print(".iterator();" + indexVarName + ".hasNext();) {").println().startIndent().printIndent();
		getPrinter().print(VAR_DECL_KEYWORD + " " + loop.var.name.toString() + " = ").print(indexVarName + ".next();")
				.println();
		getPrinter().printIndent().print(loop.body);
		endIndent().println().printIndent().print("}");
	}

	@Override
	public boolean substituteForEachLoop(ForeachLoopElement foreachLoop, boolean targetHasLength, String indexVarName) {
		if (!targetHasLength) {
			printForEachLoop(((ForeachLoopElementSupport) foreachLoop).getTree(), indexVarName);
			return true;
		}
		return super.substituteForEachLoop(foreachLoop, targetHasLength, indexVarName);
	}

}
