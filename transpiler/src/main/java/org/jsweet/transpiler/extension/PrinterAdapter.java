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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.model.CaseElement;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.IdentifierElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;
import org.jsweet.transpiler.model.SelectElement;
import org.jsweet.transpiler.model.Util;
import org.jsweet.transpiler.model.support.ExtendedElementSupport;
import org.jsweet.transpiler.model.support.MethodInvocationElementSupport;
import org.jsweet.transpiler.model.support.NewClassElementSupport;
import org.jsweet.transpiler.model.support.UtilSupport;
import org.jsweet.transpiler.util.AbstractTreePrinter;
import org.jsweet.transpiler.util.VariableKind;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

/**
 * A printer adapter, which can be overridden to change the default printer
 * behavior. Adapters are composable/chainable objects (decorator pattern).
 * 
 * @author Renaud Pawlak
 */
public class PrinterAdapter {

	private PrinterAdapter parentAdapter;

	private AbstractTreePrinter printer;

	protected JSweetContext context;

	/**
	 * Creates a root adapter (with no parent).
	 * 
	 * @param context
	 *            the transpilation context
	 */
	public PrinterAdapter(JSweetContext context) {
		this.context = context;
		this.parentAdapter = null;
	}

	/**
	 * Creates a new adapter that will try delegate to the given parent adapter
	 * when not implementing its own behavior.
	 * 
	 * @param parentAdapter
	 *            cannot be null: if no parent you must use the
	 *            {@link #AbstractPrinterAdapter(JSweetContext)} constructor
	 */
	public PrinterAdapter(PrinterAdapter parentAdapter) {
		if (parentAdapter == null) {
			throw new RuntimeException("cannot create an adatper with a null parent adapter: pass the context instead");
		}
		this.parentAdapter = parentAdapter;
		this.context = parentAdapter.getContext();
	}

	/**
	 * Gets the transpiler's context.
	 */
	public JSweetContext getContext() {
		if (context != null) {
			return context;
		} else {
			if (getParentAdapter() != null) {
				context = getParentAdapter().getContext();
			}
		}
		return context;
	}

	/**
	 * Adds a type mapping so that this adapter substitutes the source type with
	 * the target type during the transpilation process.
	 * 
	 * @param sourceTypeName
	 *            the fully qualified name of the type to be substituted
	 * @param targetTypeName
	 *            the fully Qualified name of the type the source type is mapped
	 *            to
	 */
	protected final void addTypeMapping(String sourceTypeName, String targetTypeName) {
		context.addTypeMapping(sourceTypeName, targetTypeName);
	}

	/**
	 * Adds a set of name-based type mappings. This method is equivalent to
	 * calling {@link #addTypeMapping(String, String)} for each entry of the
	 * given map.
	 */
	protected final void addTypeMappings(Map<String, String> nameMappings) {
		context.addTypeMappings(nameMappings);
	}

	/**
	 * Returns true if the given type name is mapped through the
	 * {@link #addTypeMapping(String, String)} or
	 * {@link #addTypeMapping(String, String)} function.
	 */
	protected final boolean isMappedType(String sourceTypeName) {
		return context.isMappedType(sourceTypeName);
	}

	/**
	 * Returns the type the given type name is mapped through the
	 * {@link #addTypeMapping(String, String)} or
	 * {@link #addTypeMapping(String, String)} function.
	 */
	protected final String getTypeMappingTarget(String sourceTypeName) {
		return context.getTypeMappingTarget(sourceTypeName);
	}

	/**
	 * Gets the functional type mappings.
	 */
	protected final List<BiFunction<ExtendedElement, String, Object>> getFunctionalTypeMappings() {
		return context.getFunctionalTypeMappings();
	}

	/**
	 * Adds a type mapping so that this adapter substitutes the source type tree
	 * with a target type during the transpilation process.
	 * 
	 * @param mappingFunction
	 *            a function that takes the type tree, the type name, and
	 *            returns a substitution (either under the form of a string, or
	 *            of a string, or of another type tree).
	 */
	public void addTypeMapping(BiFunction<ExtendedElement, String, Object> mappingFunction) {
		context.addTypeMapping(mappingFunction);
	}

	/**
	 * Gets the string that corresponds to the given type, taking into account
	 * all type mappings.
	 * 
	 * <p>
	 * Some type mappings are set by default, some are added in the context by
	 * adapters.
	 */
	public final String getMappedType(TypeMirror type) {
		StringBuilder stringBuilder = new StringBuilder();
		buildMappedType(stringBuilder, type);
		return stringBuilder.toString();
	}

	private final void buildMappedType(StringBuilder stringBuilder, TypeMirror type) {
		switch (type.getKind()) {
		case DECLARED:
			DeclaredType declaredType = (DeclaredType) type;
			Element element = declaredType.asElement();
			String elementName = element.toString();
			String mapped = context.getTypeMappingTarget(elementName);
			if (mapped != null) {
				stringBuilder.append(mapped);
			} else {
				print(element.getSimpleName().toString());
			}
			if (!declaredType.getTypeArguments().isEmpty()) {
				print("<");
				for (TypeMirror arg : declaredType.getTypeArguments()) {
					buildMappedType(stringBuilder, arg);
					print(", ");
				}
				getPrinter().removeLastChars(2);
				print(">");
			}
			break;
		case ARRAY:
			buildMappedType(stringBuilder, ((javax.lang.model.type.ArrayType) type).getComponentType());
			stringBuilder.append("[]");
			break;
		case TYPEVAR:
		case WILDCARD:
			stringBuilder.append("any");
			break;
		default:
			if (context.isMappedType(type.toString())) {
				stringBuilder.append(context.getTypeMappingTarget(type.toString()));
			} else {
				stringBuilder.append(type.toString());
			}
		}
	}

	/**
	 * Adds an annotation on the AST through global filters.
	 * 
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * context.addAnnotation(FunctionalInterface.class, "**.MyInterface");
	 * </pre>
	 * 
	 * <p>
	 * Filters are simplified regular expressions matching on the Java AST.
	 * Special characters are the following:
	 * 
	 * <ul>
	 * <li>*: matches any token/identifier in the signature of the AST element
	 * </li>
	 * <li>**: matches any list of tokens in signature of the AST element (same
	 * as ..)</li>
	 * <li>..: matches any list of tokens in signature of the AST element (same
	 * as **)</li>
	 * <li>!: negates the filter (first character only)</li>
	 * </ul>
	 * 
	 * <p>
	 * For example, to match:
	 * 
	 * <ul>
	 * <li>all the elements in the x.y.z package: x.y.z.*</li>
	 * <li>all the elements and subelements (fields, methods, ...) in the x.y.z
	 * package: x.y.z.**</li>
	 * <li>all the methods in the x.y.z.A class: x.y.z.A.*(..)</li>
	 * <li>all the methods taking 2 arguments in the x.y.z.A class:
	 * x.y.z.A.*(*,*)</li>
	 * <li>all fields call aField in all the classes: **.aField</li>
	 * </ul>
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @param filters
	 *            the annotation is activated if one of the filters match and no
	 *            negative filter matches
	 */
	public final void addAnnotation(Class<? extends Annotation> annotationType, String... filters) {
		addAnnotation(annotationType.getName(), filters);
	}

	/**
	 * Adds an annotation on the AST through global filters.
	 * 
	 * The annotation to be added is described by its type and by a value, which
	 * is passed as is to the annotation's value. If the annotation type does
	 * not accept a value parameter, no annotations will be added.
	 * 
	 * @see #addAnnotation(String, String...)
	 */
	public final void addAnnotationWithValue(Class<? extends Annotation> annotationType, Object value,
			String... filters) {
		addAnnotation(annotationType.getName() + "('" + value.toString() + "')", filters);
	}

	/**
	 * Adds an annotation manager that will tune (add or remove) annotations on
	 * the AST. Lastly added managers have precedence over firstly added ones.
	 */
	public final void addAnnotationManager(AnnotationManager annotationManager) {
		context.addAnnotationManager(annotationManager);
	}

	/**
	 * Returns true if the given element is annotated with one of the given
	 * annotation types.
	 */
	public boolean hasAnnotationType(Element element, String... annotationTypes) {
		return context.hasAnnotationType((Symbol) element, annotationTypes);
	}

	/**
	 * Gets the first value of the 'value' property for the given annotation
	 * type if found on the given symbol.
	 */
	public final String getAnnotationValue(Element element, String annotationType, String defaultValue) {
		return getAnnotationValue((Symbol) element, annotationType, null, defaultValue);
	}

	/**
	 * Gets the first value of the given property for the given annotation type
	 * if found on the given symbol.
	 */
	public String getAnnotationValue(Element element, String annotationType, String propertyName, String defaultValue) {
		return context.getAnnotationValue((Symbol) element, annotationType, propertyName, defaultValue);
	}

	/**
	 * Adds an annotation on the AST through global filters.
	 * 
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * context.addAnnotation("@Erased", "*.writeObject(*)");
	 * context.addAnnotation("@Name('newName')", "*.MyDeclarationToBeRenamed");
	 * </pre>
	 * 
	 * <p>
	 * Filters are simplified regular expressions matching on the Java AST.
	 * Special characters are the following:
	 * 
	 * <ul>
	 * <li>*: matches any character in the signature of the AST element</li>
	 * <li>!: negates the filter (first character only)</li>
	 * </ul>
	 * 
	 * @param annotationDescriptor
	 *            the annotation type name, optionally preceded with a @, and
	 *            optionally defining a value (fully qualified name is not
	 *            necessary for JSweet annotations)
	 * @param filters
	 *            the annotation is activated if one of the filters match and no
	 *            negative filter matches
	 */
	public final void addAnnotation(String annotationDescriptor, String... filters) {
		context.addAnnotation(annotationDescriptor, filters);
	}

	/**
	 * A list of type variables to be erased (mapped to any).
	 */
	public Set<TypeParameterElement> typeVariablesToErase = new HashSet<>();

	/**
	 * Prints a generic element by delegating to the printer.
	 */
	public PrinterAdapter print(ExtendedElement element) {
		printer.print(((ExtendedElementSupport) element).getTree());
		return this;
	}

	/**
	 * Prints a string by delegating to the printer.
	 */
	public PrinterAdapter print(String string) {
		printer.print(string);
		return this;
	}

	/**
	 * Prints a name by delegating to the printer.
	 */
	public PrinterAdapter print(Name name) {
		printer.print(name.toString());
		return this;
	}

	/**
	 * Prints a new line by delegating to the printer.
	 */
	public PrinterAdapter println() {
		printer.println();
		return this;
	}

	/**
	 * Prints an argument list by delegating to the printer.
	 */
	public PrinterAdapter printArgList(List<? extends ExtendedElement> args) {
		printer.printArgList(
				args.stream().map(a -> ((ExtendedElementSupport) a).getTree()).collect(Collectors.toList()));
		return this;
	}

	/**
	 * Print either a string, or a tree if the string is null.
	 */
	public void print(String exprStr, ExtendedElement expr) {
		if (exprStr == null) {
			print(expr);
		} else {
			print(exprStr);
		}
	}

	/**
	 * Prints an indentation for the current indentation value.
	 */
	public PrinterAdapter printIndent() {
		printer.printIndent();
		return this;
	}

	/**
	 * Increments the current indentation value.
	 */
	public PrinterAdapter startIndent() {
		printer.startIndent();
		return this;
	}

	/**
	 * Decrements the current indentation value.
	 */
	public PrinterAdapter endIndent() {
		printer.endIndent();
		return this;
	}

	/**
	 * Adds a space to the output.
	 */
	public PrinterAdapter space() {
		printer.space();
		return this;
	}

	/**
	 * removes last character if expectedChar
	 */
	public boolean removeLastChar(char expectedChar) {
		return printer.removeLastChar(expectedChar);
	}

	/**
	 * Removes the last output character.
	 */
	public PrinterAdapter removeLastChar() {
		printer.removeLastChar();
		return this;
	}

	/**
	 * Removes the last output characters.
	 */
	public PrinterAdapter removeLastChars(int count) {
		printer.removeLastChars(count);
		return this;
	}

	/**
	 * Removes the last printed indentation.
	 */
	public PrinterAdapter removeLastIndent() {
		printer.removeLastIndent();
		return this;
	}

	/**
	 * Gets the printer's stack.
	 */
	public Stack<JCTree> getStack() {
		return printer.getStack();
	}

	/**
	 * Gets the parent element in the printer's scanning stack.
	 */
	public JCTree getParent() {
		return printer.getParent();
	}

	/**
	 * Gets the parent element in the printer's scanning stack.
	 */
	public ExtendedElement getParentElement() {
		return printer.getParentElement();
	}

	/**
	 * Gets the parent element in the printer's scanning stack.
	 */
	public <T extends JCTree> T getParent(Class<T> type) {
		return printer.getParent(type);
	}

	/**
	 * Gets the parent element in the printer's scanning stack.
	 */
	public <T extends Element> T getParentElement(Class<T> type) {
		return printer.getParentElement(type);
	}

	/**
	 * Looks-up the executable that is invoked by the given invocation.
	 */
	public ExecutableElement findExecutableDeclarationInType(TypeElement type, MethodInvocationElement invocation) {
		return org.jsweet.transpiler.util.Util.findMethodDeclarationInType(context.types, (TypeSymbol) type,
				((MethodInvocationElementSupport) invocation).getTree());
	}

	/**
	 * Gets the printer's current compilation unit.
	 */
	public JCCompilationUnit getCompilationUnit() {
		return printer.getCompilationUnit();
	}

	public String getRootRelativeName(Symbol symbol) {
		return printer.getRootRelativeName(symbol);
	}

	public String getRootRelativeName(Symbol symbol, boolean useJavaNames) {
		return printer.getRootRelativeName(symbol, useJavaNames);
	}

	/**
	 * Reports a problem during the printing phase.
	 * 
	 * @param tree
	 *            the code where the problem occurred
	 * @param problem
	 *            the reported problem
	 * @param params
	 *            the parameters if any
	 */
	protected void report(ExtendedElement element, JSweetProblem problem, Object... params) {
		printer.report(((ExtendedElementSupport) element).getTree(), problem, params);
	}

	/**
	 * Reports a problem during the printing phase.
	 * 
	 * @param tree
	 *            the code where the problem occurred
	 * @param name
	 *            the name of the element if any
	 * @param problem
	 *            the reported problem
	 * @param params
	 *            the parameters if any
	 */
	protected void report(ExtendedElement element, Name name, JSweetProblem problem, Object... params) {
		printer.report(((ExtendedElementSupport) element).getTree(), (com.sun.tools.javac.util.Name) name, problem,
				params);
	}

	/**
	 * Gets the print value of the given literal tree.
	 */
	public String getLiteralStringValue(JCTree tree) {
		return (String) ((JCLiteral) tree).value;
	}

	/**
	 * Substitutes the value of an array access expression.
	 * 
	 * @param arrayAccess
	 *            the array access being printed
	 * @return true if substituted
	 */
	public boolean substituteArrayAccess(JCArrayAccess arrayAccess) {
		return parentAdapter == null ? false : parentAdapter.substituteArrayAccess(arrayAccess);
	}

	/**
	 * Substitutes the value of an identifier.
	 * 
	 * @param identifier
	 *            the identifier being printed
	 * @return true if substituted
	 */
	public boolean substituteIdentifier(IdentifierElement identifier) {
		return parentAdapter == null ? false : parentAdapter.substituteIdentifier(identifier);
	}

	/**
	 * Substitutes the value of a <code>new</code> expression.
	 * 
	 * @param newClass
	 *            the new being printed
	 * @return true if substituted
	 */
	public final boolean substituteNewClass(JCNewClass newClass) {
		return substituteNewClass(new NewClassElementSupport(newClass), (TypeElement) newClass.type.tsym,
				newClass.type.tsym.getQualifiedName().toString());
	}

	/**
	 * To override to tune the printing of a new class expression.
	 * 
	 * @param newClass
	 *            the new class expression
	 * @param type
	 *            the type of the class being instantiated
	 * @param className
	 *            the fully qualified name of the class being instantiated
	 * @return true if substituted
	 */
	public boolean substituteNewClass(NewClassElement newClass, TypeElement type, String className) {
		return parentAdapter == null ? false : parentAdapter.substituteNewClass(newClass, type, className);
	}

	/**
	 * Substitutes the value of a <em>select</em> expression (of the form
	 * <code>target.name</code>).
	 * 
	 * @param select
	 *            the select being printed
	 * @return true if substituted
	 */
	public boolean substituteSelect(SelectElement select) {
		return parentAdapter == null ? false : parentAdapter.substituteSelect(select);

	}

	/**
	 * Returns the import qualified id if the given import requires an import
	 * statement to be printed.
	 * 
	 * @param importDecl
	 *            the given import declaration
	 * @param qualifiedName
	 *            the qualified import id
	 * @return the possibly adapted qualified id or null if the import should be
	 *         ignored by the printer
	 */
	public String needsImport(JCImport importDecl, String qualifiedName) {
		if (importDecl.isStatic()) {
			return null;
		} else {
			return getPrinter().getRootRelativeName(importDecl.getQualifiedIdentifier().type.tsym);
		}
	}

	/**
	 * Tells if this cast is required to be printed or not.
	 */
	public boolean needsTypeCast(JCTypeCast cast) {
		return parentAdapter == null ? true : parentAdapter.needsTypeCast(cast);
	}

	/**
	 * Tells if the printer needs to print the given variable declaration.
	 */
	public boolean needsVariableDecl(JCVariableDecl variableDecl, VariableKind kind) {
		return parentAdapter == null ? true : parentAdapter.needsVariableDecl(variableDecl, kind);
	}

	/**
	 * Substitutes the value of a <em>method invocation</em> expression.
	 * 
	 * @param invocation
	 *            the invocation being printed
	 * @return true if substituted
	 */
//	final public boolean substituteMethodInvocation(JCMethodInvocation invocation) {
//		JCFieldAccess fieldAccess = null;
//		Element targetType = null;
//		String targetClassName = null;
//		String targetMethodName = null;
//		if (invocation.getMethodSelect() instanceof JCFieldAccess) {
//			fieldAccess = (JCFieldAccess) invocation.getMethodSelect();
//			targetType = fieldAccess.selected.type.tsym;
//			targetClassName = ((Symbol) targetType).getQualifiedName().toString();
//			targetMethodName = fieldAccess.name.toString();
//		} else {
//			targetMethodName = invocation.getMethodSelect().toString();
//			System.out.println(((JCIdent) invocation.meth).sym.getEnclosingElement());
//			if (getPrinter().getStaticImports().containsKey(targetMethodName)) {
//				JCImport i = getPrinter().getStaticImports().get(targetMethodName);
//				targetType = ((JCFieldAccess) i.qualid).selected.type.tsym;
//				targetClassName = ((Symbol) targetType).getQualifiedName().toString();
//			}
//		}
//		return substituteMethodInvocation(new MethodInvocationElementSupport(invocation),
//				new SelectElementSupport(fieldAccess), targetType, targetClassName, targetMethodName);
//	}

	public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
		return parentAdapter == null ? false
				: parentAdapter.substituteMethodInvocation(invocation);
	}

	/**
	 * Substitutes the value of a <em>field assignment</em> expression.
	 * 
	 * @param assign
	 *            the field assignment being printed
	 * @return true if substituted
	 */
	public boolean substituteAssignment(JCAssign assign) {
		return parentAdapter == null ? false : parentAdapter.substituteAssignment(assign);
	}

	/**
	 * Gets the printer attached to this adapter.
	 */
	public AbstractTreePrinter getPrinter() {
		return printer;
	}

	/**
	 * Sets the printer attached to this adapter.
	 */
	public void setPrinter(AbstractTreePrinter printer) {
		this.printer = printer;
		if (parentAdapter != null) {
			parentAdapter.setPrinter(printer);
		}
	}

	/**
	 * Gets the adapted identifier string.
	 */
	public String getIdentifier(Symbol symbol) {
		return parentAdapter == null ? symbol.name.toString() : parentAdapter.getIdentifier(symbol);
	}

	/**
	 * Returns the qualified type name.
	 */
	public String getQualifiedTypeName(TypeSymbol type, boolean globals) {
		return parentAdapter == null ? getPrinter().getRootRelativeName(type)
				: parentAdapter.getQualifiedTypeName(type, globals);
	}

	public Set<String> getErasedTypes() {
		if (parentAdapter == null) {
			throw new RuntimeException("unimplemented behavior");
		} else {
			return parentAdapter.getErasedTypes();
		}
	}

	/**
	 * Substitutes if required an expression that is being assigned to a given
	 * type.
	 */
	public boolean substituteAssignedExpression(Type assignedType, JCExpression expression) {
		return parentAdapter == null ? false : parentAdapter.substituteAssignedExpression(assignedType, expression);
	}

	/**
	 * Substitutes if necessary the given foreach loop.
	 * 
	 * @param the
	 *            foreach loop to print
	 * @param targetHasLength
	 *            true if the iterable defines a public length field
	 * @param indexVarName
	 *            a possible (fresh) variable name that can used to iterate
	 */
	public boolean substituteForEachLoop(JCEnhancedForLoop foreachLoop, boolean targetHasLength, String indexVarName) {
		return parentAdapter == null ? false
				: parentAdapter.substituteForEachLoop(foreachLoop, targetHasLength, indexVarName);
	}

	/**
	 * Tells if a super class has to be erased in the generated source.
	 */
	public boolean eraseSuperClass(TypeElement type, TypeElement superClass) {
		return parentAdapter == null ? false : parentAdapter.eraseSuperClass(type, superClass);
	}

	/**
	 * Tells if a super interface has to be erased in the generated source.
	 */
	public boolean eraseSuperInterface(TypeElement type, TypeElement superInterface) {
		return parentAdapter == null ? false : parentAdapter.eraseSuperInterface(type, superInterface);
	}

	/**
	 * Tells if this adapter substitutes types in extends or implements clauses.
	 */
	public boolean isSubstituteSuperTypes() {
		return parentAdapter == null ? false : parentAdapter.isSubstituteSuperTypes();
	}

	/**
	 * Substitutes if necessary an instanceof expression.
	 * 
	 * @param exprStr
	 *            the expression being tested as a string (null if provided as a
	 *            tree)
	 * @param expr
	 *            the expression being tested as a tree (null if provided as a
	 *            string)
	 * @param type
	 *            the type of the instanceof expression
	 * @return true if substituted
	 */
	public boolean substituteInstanceof(String exprStr, ExtendedElement expr, Type type) {
		return parentAdapter == null ? false : parentAdapter.substituteInstanceof(exprStr, expr, type);
	}

	/**
	 * Substitutes if necessary the pattern of a case statement.
	 */
	public boolean substituteCaseStatementPattern(CaseElement caseStatement, ExtendedElement pattern) {
		return parentAdapter == null ? false : parentAdapter.substituteCaseStatementPattern(caseStatement, pattern);
	}

	/**
	 * This method is called after a type was printed.
	 */
	public void afterType(TypeElement type) {
		if (parentAdapter != null) {
			parentAdapter.afterType(type);
		}
	}

	/**
	 * Adapts the JavaDoc comment for a given element.
	 * 
	 * @param element
	 *            the documented element ({@link JCClassDecl},
	 *            {@link JCMethodDecl}, or {@link JCVariableDecl})
	 * @param commentText
	 *            the comment text if any (null when no comment)
	 * @return the adapted comment (null will remove the JavaDoc comment)
	 */
	public String adaptDocComment(JCTree element, String commentText) {
		return parentAdapter == null ? commentText : parentAdapter.adaptDocComment(element, commentText);
	}

	/**
	 * Gets the parent adapter. By default, an adapter delegates to the parent
	 * adapter when the behavior is not overridden.
	 */
	public PrinterAdapter getParentAdapter() {
		return parentAdapter;
	}

	/**
	 * Sets the parent adapter. By default, an adapter delegates to the parent
	 * adapter when the behavior is not overridden.
	 */
	public void setParentAdapter(PrinterAdapter parentAdapter) {
		this.parentAdapter = parentAdapter;
	}

	private Types types;

	/**
	 * Gets the types API, which provides a set of utilities on TypeMirror.
	 * 
	 * @see TypeMirror
	 * @see Element#asType()
	 * @see ExtendedElement#getType()
	 */
	public Types types() {
		if (types == null) {
			types = JavacTypes.instance(context);
		}
		return types;
	}

	private Util util;

	/**
	 * Gets the util API, which provides a set of utilities.
	 */
	public Util util() {
		if (util == null) {
			util = new UtilSupport(context);
		}
		return util;
	}

	/**
	 * Print the macro name in the code.
	 */
	protected final void printMacroName(String macroName) {
		print("/* " + macroName + " */");
	}

}
