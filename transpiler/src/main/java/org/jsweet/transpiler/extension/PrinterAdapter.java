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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetOptions;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.SourcePosition;
import org.jsweet.transpiler.model.ArrayAccessElement;
import org.jsweet.transpiler.model.AssignmentElement;
import org.jsweet.transpiler.model.AssignmentWithOperatorElement;
import org.jsweet.transpiler.model.BinaryOperatorElement;
import org.jsweet.transpiler.model.CaseElement;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.ForeachLoopElement;
import org.jsweet.transpiler.model.IdentifierElement;
import org.jsweet.transpiler.model.ImportElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewArrayElement;
import org.jsweet.transpiler.model.NewClassElement;
import org.jsweet.transpiler.model.TypeCastElement;
import org.jsweet.transpiler.model.UnaryOperatorElement;
import org.jsweet.transpiler.model.VariableAccessElement;
import org.jsweet.transpiler.model.support.CompilationUnitElementSupport;
import org.jsweet.transpiler.model.support.ExtendedElementSupport;
import org.jsweet.transpiler.model.support.MethodInvocationElementSupport;
import org.jsweet.transpiler.util.AbstractTreePrinter;
import org.jsweet.transpiler.util.Util;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;

/**
 * A printer adapter, which can be overridden to change the default printer
 * behavior. Adapters are composable/chainable objects (decorator pattern).
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class PrinterAdapter {

    protected Logger logger = Logger.getLogger(getClass());

    private PrinterAdapter parentAdapter;

    private AbstractTreePrinter printer;

    protected JSweetContext context;

    /**
     * Creates a root adapter (with no parent).
     * 
     * @param context the transpilation context
     */
    public PrinterAdapter(JSweetContext context) {
        this.context = context;
        this.parentAdapter = null;
    }

    /**
     * Creates a new adapter that will try delegate to the given parent adapter when
     * not implementing its own behavior.
     * 
     * @param parentAdapter cannot be null: if no parent you must use the
     *                      {@link #AbstractPrinterAdapter(JSweetContext)}
     *                      constructor
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
     * This hook is called when the transpilation starts. Overloads must call super
     * to forward this hook to the adapters chain.
     */
    public void onTranspilationStarted() {
        if (getParentAdapter() != null) {
            getParentAdapter().onTranspilationStarted();
        }
    }

    /**
     * This hook is called when the transpilation finishes. Overloads must call
     * super to forward this hook to the adapters chain.
     */
    public void onTranspilationFinished() {
        if (getParentAdapter() != null) {
            getParentAdapter().onTranspilationFinished();
        }
    }

    /**
     * Adds a type mapping so that this adapter substitutes the source type with the
     * target type during the transpilation process.
     * 
     * @param sourceTypeName the fully qualified name of the type to be substituted
     * @param targetTypeName the fully Qualified name of the type the source type is
     *                       mapped to
     */
    protected final void addTypeMapping(String sourceTypeName, String targetTypeName) {
        context.addTypeMapping(sourceTypeName, targetTypeName);
    }

    /**
     * Adds a set of name-based type mappings. This method is equivalent to calling
     * {@link #addTypeMapping(String, String)} for each entry of the given map.
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
     * @param mappingFunction a function that takes the type tree, the type name,
     *                        and returns a substitution (either under the form of a
     *                        string, or of a string, or of another type tree).
     */
    public void addTypeMapping(BiFunction<ExtendedElement, String, Object> mappingFunction) {
        context.addTypeMapping(mappingFunction);
    }

    /**
     * Adds a functional type mapping. <br />
     * NOTE: If TypeMirror is generic, the mapper should include generic
     * specification if relevant
     * 
     * @param mappingFunction a function that takes the type , and returns a mapped
     *                        type as string.
     */
    protected final void addTypeMapping(Function<TypeMirror, String> mappingFunction) {
        context.addTypeMapping(mappingFunction);
    }

    /**
     * Returns the functional type mappings.
     */
    protected final List<Function<TypeMirror, String>> getTypeMirrorMappings() {
        return context.getFunctionalTypeMirrorMappings();
    }

    /**
     * Gets the string that corresponds to the given type, taking into account all
     * type mappings.
     * 
     * <p>
     * Some type mappings are set by default, some are added in the context by
     * adapters.
     */
    public final String getMappedType(TypeMirror type) {
        return getMappedType(type, null);
    }

    /**
     * Gets the string that corresponds to the given type, taking into account all
     * type mappings.
     * 
     * <p>
     * Some type mappings are set by default, some are added in the context by
     * adapters.
     * 
     * @param resolvedTypeArgs Optional (can be null) map of generic type to
     *                         resolved type. For instance Map<T => String> for a
     *                         GenericType<T>
     */
    public final String getMappedType(TypeMirror type, Map<TypeMirror, TypeMirror> resolvedTypeArgs) {
        StringBuilder stringBuilder = new StringBuilder();
        buildMappedType(stringBuilder, type, resolvedTypeArgs);
        return stringBuilder.toString();
    }

    private final void buildMappedType(StringBuilder stringBuilder, TypeMirror type,
            Map<TypeMirror, TypeMirror> resolvedTypeArgs) {
        switch (type.getKind()) {
        case DECLARED:
            DeclaredType declaredType = (DeclaredType) type;
            Element element = declaredType.asElement();
            String elementName = element.toString();
            String mapped = context.getTypeMappingTarget(elementName);
            if (mapped != null) {
                stringBuilder.append(mapped);
            } else {
                stringBuilder.append(element.getSimpleName().toString());
            }
            if (!"any".equals(mapped) && !declaredType.getTypeArguments().isEmpty()) {
                stringBuilder.append("<");
                for (TypeMirror arg : declaredType.getTypeArguments()) {
                    buildMappedType(stringBuilder, arg, resolvedTypeArgs);
                    stringBuilder.append(", ");
                }
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                stringBuilder.append(">");
            }
            break;
        case ARRAY:
            buildMappedType(stringBuilder, ((javax.lang.model.type.ArrayType) type).getComponentType(),
                    resolvedTypeArgs);
            stringBuilder.append("[]");
            break;
        case TYPEVAR:
        case WILDCARD:
            if (resolvedTypeArgs != null && resolvedTypeArgs.get(type) != null && resolvedTypeArgs.get(type) != type) {
                buildMappedType(stringBuilder, resolvedTypeArgs.get(type), resolvedTypeArgs);
            } else {
                stringBuilder.append("any");
            }
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
     * Filters are simplified regular expressions matching on the Java AST. Special
     * characters are the following:
     * 
     * <ul>
     * <li>*: matches any token/identifier in the signature of the AST element</li>
     * <li>**: matches any list of tokens in signature of the AST element (same as
     * ..)</li>
     * <li>..: matches any list of tokens in signature of the AST element (same as
     * **)</li>
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
     * @param annotationType the annotation type
     * @param filters        the annotation is activated if one of the filters match
     *                       and no negative filter matches
     */
    public final void addAnnotation(Class<? extends Annotation> annotationType, String... filters) {
        addAnnotation(annotationType.getName(), filters);
    }

    /**
     * Adds an annotation on the AST through global filters.
     * 
     * The annotation to be added is described by its type and by a value, which is
     * passed as is to the annotation's value. If the annotation type does not
     * accept a value parameter, no annotations will be added.
     * 
     * @see #addAnnotation(String, String...)
     */
    public final void addAnnotationWithValue(Class<? extends Annotation> annotationType, Object value,
            String... filters) {
        addAnnotation(annotationType.getName() + "('" + value.toString() + "')", filters);
    }

    /**
     * Adds an annotation manager that will tune (add or remove) annotations on the
     * AST. Lastly added managers have precedence over firstly added ones.
     */
    public final void addAnnotationManager(AnnotationManager annotationManager) {
        context.addAnnotationManager(annotationManager);
    }

    /**
     * Returns true if the given element is annotated with one of the given
     * annotation types.
     * 
     * To change the behavior of this method in a composable way, use
     * {@link #addAnnotation(Class, String...)} or
     * {@link #addAnnotationManager(AnnotationManager)}.
     */
    public final boolean hasAnnotationType(Element element, String... annotationTypes) {
        return context.hasAnnotationType((Element) element, annotationTypes);
    }

    /**
     * Gets the first value of the 'value' property for the given annotation type if
     * found on the given element.
     * 
     * To change the behavior of this method in a composable way, use
     * {@link #addAnnotation(Class, String...)} or
     * {@link #addAnnotationManager(AnnotationManager)}.
     * 
     * @param element        the element holding the annotation
     * @param annotationType the fully qualified name of the value property type
     * @param propertyClass  the expected class of the property (String.class,
     *                       TypeMirror.class, Number.class, and arrays such as
     *                       String[].class...)
     * @param defaultValue   the default value if the property is not found
     */
    public final <T> T getAnnotationValue(Element element, String annotationType, Class<T> propertyClass,
            T defaultValue) {
        return getAnnotationValue((Element) element, annotationType, null, propertyClass, defaultValue);
    }

    /**
     * Gets the first value of the given property for the given annotation type if
     * found on the given element.
     * 
     * @param element        the element holding the annotation
     * @param annotationType the fully qualified name of the value property type
     * @param propertyName   the name of the property in the annotation
     *                       (<code>null</code> will look up the <code>value</code>
     *                       property)
     * @param propertyClass  the expected class of the property (String.class,
     *                       TypeMirror.class, Number.class, and arrays such as
     *                       String[].class...)
     * @param defaultValue   the default value if the property is not found
     */
    public final <T> T getAnnotationValue(Element element, String annotationType, String propertyName,
            Class<T> propertyClass, T defaultValue) {
        return context.getAnnotationValue((Element) element, annotationType, propertyName, propertyClass, defaultValue);
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
     * Filters are simplified regular expressions matching on the Java AST. Special
     * characters are the following:
     * 
     * <ul>
     * <li>*: matches any character in the signature of the AST element</li>
     * <li>!: negates the filter (first character only)</li>
     * </ul>
     * 
     * @param annotationDescriptor the annotation type name, optionally preceded
     *                             with a @, and optionally defining a value (fully
     *                             qualified name is not necessary for JSweet
     *                             annotations)
     * @param filters              the annotation is activated if one of the filters
     *                             match and no negative filter matches
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
        printer.print(((ExtendedElementSupport<?>) element).getTree());
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
     * Prints a tree by delegating to the printer.
     */
    protected final PrinterAdapter print(Tree tree) {
        printer.print(tree);
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
        printer.printArgList(null,
                args.stream().map(a -> ((ExtendedElementSupport<?>) a).getTree()).collect(Collectors.toList()));
        return this;
    }

    /**
     * Prints a comma-separated, zero-indexed, generated identifier list.
     * 
     * <p>
     * For instance <code>printIdentifierList("x", 4)</code> will print:
     * <code>x0, x1, x2, x3</code>.
     * 
     * @param prefix the prefix of the identifiers
     * @param count  the number of identifiers in the list
     * @return this printer adapter
     */
    public PrinterAdapter printIdentifierList(String prefix, int count) {
        for (int i = 0; i < count; i++) {
            print(prefix + i + ", ");
        }
        if (count > 0) {
            removeLastChars(2);
        }
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
    public final PrinterAdapter startIndent() {
        printer.startIndent();
        return this;
    }

    /**
     * Decrements the current indentation value.
     */
    public final PrinterAdapter endIndent() {
        printer.endIndent();
        return this;
    }

    /**
     * Adds a space to the output.
     */
    public final PrinterAdapter space() {
        printer.space();
        return this;
    }

    /**
     * removes last character if expectedChar
     */
    public final boolean removeLastChar(char expectedChar) {
        return printer.removeLastChar(expectedChar);
    }

    /**
     * Removes the last output character.
     */
    public final PrinterAdapter removeLastChar() {
        printer.removeLastChar();
        return this;
    }

    /**
     * Removes the last output characters.
     */
    public final PrinterAdapter removeLastChars(int count) {
        printer.removeLastChars(count);
        return this;
    }

    /**
     * Removes the last printed indentation.
     */
    public final PrinterAdapter removeLastIndent() {
        printer.removeLastIndent();
        return this;
    }

	/**
	* Gets currently scanned element.
	*/
	public final ExtendedElement getCurrentElement() {
		return ExtendedElementFactory.INSTANCE.create(printer.getCurrent());
	}
    
    /**
     * Gets the parent element in the printer's scanning stack.
     */
    public final ExtendedElement getParentElement() {
        return printer.getParentElement();
    }

    /**
     * Gets the parent element in the printer's scanning stack.
     */
    public final <T extends Element> T getParentElement(Class<T> type) {
        return printer.getParentElement(type);
    }

    /**
     * Looks-up the executable that is invoked by the given invocation.
     */
    public final ExecutableElement findExecutableDeclarationInType(TypeElement type,
            MethodInvocationElement invocation) {
        return util().findMethodDeclarationInType((TypeElement) type,
                ((MethodInvocationElementSupport) invocation).getTree());
    }

    /**
     * Gets the qualified name of an element, relatively to a possible
     * <code>@Root</code> annotation.
     */
    public final String getRootRelativeName(Element element) {
        return printer.getRootRelativeName((Element) element);
    }

    /**
     * Reports a problem during the printing phase.
     * 
     * @param element the code where the problem occurred
     * @param problem the reported problem
     * @param params  the parameters if any
     */
    protected void report(ExtendedElement element, JSweetProblem problem, Object... params) {
        printer.report(((ExtendedElementSupport<?>) element).getTree(), problem, params);
    }

    /**
     * Reports a problem during the printing phase.
     * 
     * @param element the code where the problem occurred
     * @param problem the reported problem
     * @param params  the parameters if any
     */
    protected void report(Element element, JSweetProblem problem, Object... params) {
        printer.report(util().lookupTree(context, element), problem, params);
    }

    /**
     * Substitutes the value of an array access expression.
     * 
     * @param arrayAccess the array access being printed
     * @return true if substituted
     */
    public boolean substituteArrayAccess(ArrayAccessElement arrayAccess) {
        return parentAdapter == null ? false : parentAdapter.substituteArrayAccess(arrayAccess);
    }

    /**
     * Substitutes the value of a binary operator.
     * 
     * @param binaryOperator the binary operator being printed
     * @return true if substituted
     */
    public boolean substituteBinaryOperator(BinaryOperatorElement binaryOperator) {
        return parentAdapter == null ? false : parentAdapter.substituteBinaryOperator(binaryOperator);
    }

    /**
     * Substitutes the value of a unary operator.
     * 
     * @param unaryOperator the unary operator being printed
     * @return true if substituted
     */
    public boolean substituteUnaryOperator(UnaryOperatorElement unaryOperator) {
        return parentAdapter == null ? false : parentAdapter.substituteUnaryOperator(unaryOperator);
    }

    /**
     * Substitutes the value of an identifier.
     * 
     * @param identifier the identifier being printed
     * @return true if substituted
     */
    public boolean substituteIdentifier(IdentifierElement identifier) {
        return parentAdapter == null ? false : parentAdapter.substituteIdentifier(identifier);
    }

    /**
     * Manually substitutes the super class of a given type.
     * <p/>
     * This method should print " extends X" and return true to force the
     * substitution of the extends clause of a given class. This is a low-level
     * operation and it is not encouraged to use it except for very specific
     * contexts.
     */
    public boolean substituteExtends(TypeElement type) {
        return parentAdapter == null ? false : parentAdapter.substituteExtends(type);
    }

    /**
     * Manually substitutes the super interfaces of a given type.
     * <p/>
     * This method should print " implements X,Y,Z" and return true to force the
     * substitution of the implements clause of a given class. This is a low-level
     * operation and it is not encouraged to use it except for very specific
     * contexts.
     */
    public boolean substituteImplements(TypeElement type) {
        return parentAdapter == null ? false : parentAdapter.substituteImplements(type);
    }

    /**
     * To override to tune the printing of a new class expression.
     * 
     * @param newClass the new class expression
     * @return true if substituted
     */
    public boolean substituteNewClass(NewClassElement newClass) {
        return parentAdapter == null ? false : parentAdapter.substituteNewClass(newClass);
    }

    /**
     * Substitutes an assigned expression if necessary.
     * <p>
     * Expressions are assigned to a type when used in an assignment expression (=)
     * or as function arguments. JSweet implements a default behavior to adapt the
     * expression when necessary (for instance to implement implicit type conversion
     * that would be necessary in TypeScript). One can override or extend the
     * default behavior by overriding this method.
     * 
     * @param type               the type the expression is being assigned to
     * @param assignedExpression the assigned expression
     * @return true if the expression has been substituted
     */
    public boolean substituteAssignedExpression(TypeMirror type, ExtendedElement assignedExpression) {
        return parentAdapter == null ? false : parentAdapter.substituteAssignedExpression(type, assignedExpression);
    }

    /**
     * Upcalled by the transpiler to forward to the right subtitution method
     * depending on the actual extended element type.
     */
    public final boolean substitute(ExtendedElement extendedElement) {
        if (extendedElement instanceof VariableAccessElement) {
            return substituteVariableAccess((VariableAccessElement) extendedElement);
        } else if (extendedElement instanceof IdentifierElement) {
            return substituteIdentifier((IdentifierElement) extendedElement);
        } else {
            return false;
        }
    }

    /**
     * Substitutes the given variable access.
     * 
     * @param variableAccess the variable access being printed
     * @return true if substituted
     */
    public boolean substituteVariableAccess(VariableAccessElement variableAccess) {
        return parentAdapter == null ? false : parentAdapter.substituteVariableAccess(variableAccess);
    }

    /**
     * Substitutes overloaded method implementation
     * 
     * @param parentTypeElement parent class
     * @param overload          overloaded method descriptors
     * @return true if substituted
     */
    public boolean substituteOverloadMethodBody(TypeElement parentTypeElement, Overload overload) {
        return parentAdapter == null ? false : parentAdapter.substituteOverloadMethodBody(parentTypeElement, overload);
    }

    /**
     * Substitutes method's body
     * 
     * @param parentTypeElement parent class
     * @param method            method's symbol
     * @return true if substituted
     */
    public boolean substituteMethodBody(TypeElement parentTypeElement, ExecutableElement method) {
        return parentAdapter == null ? false : parentAdapter.substituteMethodBody(parentTypeElement, method);
    }

    /**
     * Called if the initializer of a variable is undefined (in order to force a
     * default value).
     * 
     * @param variable the variable to return an initializer for
     * @return an initializer expression, null to keep undefined
     */
    public String getVariableInitialValue(VariableElement variable) {
        return parentAdapter == null ? util().getTypeInitialValue(variable.asType())
                : parentAdapter.getVariableInitialValue(variable);
    }

    /**
     * Returns the import qualified id if the given import requires an import
     * statement to be printed.
     * 
     * @param importElement the given import declaration
     * @param qualifiedName the qualified import id
     * @return the possibly adapted qualified id or null if the import should be
     *         ignored by the printer
     */
    public String needsImport(ImportElement importElement, String qualifiedName) {
        return parentAdapter == null
                ? (importElement.getImportedType() == null ? null
                        : getRootRelativeName(importElement.getImportedType()))
                : parentAdapter.needsImport(importElement, qualifiedName);
    }
    

    /**
     * This method implements the default behavior to generate module imports. It
     * may be overridden by subclasses to implement specific behaviors.
     * 
     * @param currentCompilationUnit the currently transpiled compilation unit
     * @param importedName           the name to be imported
     * @param importedClass          the class being imported
     * @return a {@link ModuleImportDescriptor} instance that will be used to
     *         generate the TypeScript import statement
     */
    public ModuleImportDescriptor getModuleImportDescriptor(CompilationUnitElement currentCompilationUnit,
            String importedName, TypeElement importedClass) {

        if (parentAdapter != null) {
            return parentAdapter.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
        } else {

            if (util().isSourceElement(importedClass)
                    && !importedClass.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
                String importedModule = util().getSourceFilePath(importedClass);
                if (importedModule.equals(currentCompilationUnit.getSourceFilePath())) {
                    return null;
                }
                Element parent = importedClass.getEnclosingElement();
                while (!(parent instanceof PackageElement)) {
                    importedName = parent.getSimpleName().toString();
                    parent = parent.getEnclosingElement();
                }
                while (importedClass.getEnclosingElement() instanceof TypeElement) {
                    importedClass = (TypeElement) importedClass.getEnclosingElement();
                }

                if (parent != null && !hasAnnotationType(importedClass, JSweetConfig.ANNOTATION_ERASED)
                        && !(importedClass.getKind() == ElementKind.ANNOTATION_TYPE
                        && !context.elementHasAnnotationType(importedClass, JSweetConfig.ANNOTATION_DECORATOR))) {
                    // '@' represents a common root in case there is no common root
                    // package => pathToImportedClass cannot be null because of the
                    // common '@' root
                    String pathToImportedClass = util().getRelativePath(
                            "@/" + currentCompilationUnit.getPackage().toString().replace('.', '/'),
                            "@/" + importedClass.toString().replace('.', '/'));
                    if (!pathToImportedClass.startsWith(".")) {
                        pathToImportedClass = "./" + pathToImportedClass;
                    }

                    return new ModuleImportDescriptor(false, (PackageElement) parent, importedName,
                            pathToImportedClass.replace('\\', '/'), importedClass);
                }
            }
            return null;
        }
    }

    /**
     * Substitutes the value of a <em>method invocation</em> expression.
     * 
     * @param invocation the invocation being printed
     * @return true if substituted
     */
    public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
        return parentAdapter == null ? false : parentAdapter.substituteMethodInvocation(invocation);
    }
    
    /**
     * Substitutes <em>new array</em> expressions, which length are initialized with a variable.
     * 
     * @param newArray
     *            the new array being printed
     * @return true if substituted
     */
    public boolean substituteNewArrayWithVariableLength(NewArrayElement newArray) {
        return parentAdapter == null ? false : parentAdapter.substituteNewArrayWithVariableLength(newArray);
    }

    /**
     * Substitutes a given type declaration.
     * 
     * @param type the type being printed
     * @return true if substituted
     */
    public boolean substituteType(TypeElement type) {
        return parentAdapter == null ? false : parentAdapter.substituteType(type);
    }

    /**
     * Substitutes a given executable declaration.
     * 
     * @param executable the executable being printed
     * @return true if substituted
     */
    public boolean substituteExecutable(ExecutableElement executable) {
        return parentAdapter == null ? false : parentAdapter.substituteExecutable(executable);
    }

    /**
     * Substitutes a given variable declaration.
     * 
     * @param variable the variable being printed
     * @return true if substituted
     */
    public boolean substituteVariable(VariableElement variable) {
        return parentAdapter == null ? false : parentAdapter.substituteVariable(variable);
    }

    /**
     * Substitutes the value of a <em>field assignment</em> expression.
     * 
     * @param assignment the field assignment being printed
     * @return true if substituted
     */
    public boolean substituteAssignment(AssignmentElement assignment) {
        return parentAdapter == null ? false : parentAdapter.substituteAssignment(assignment);
    }

    /**
     * Substitutes the value of an assignment with operator (a += b) expression.
     * 
     * @param assignment the assignment being printed
     * @return true if substituted
     */
    public boolean substituteAssignmentWithOperator(AssignmentWithOperatorElement assignment) {
        return parentAdapter == null ? false : parentAdapter.substituteAssignmentWithOperator(assignment);
    }

    /**
     * Gets the printer on which rely the adapter (this is not recommended).
     * 
     * <p>
     * Accessing the printer with this method allows the user to access the internal
     * javac API directly ({@link com.sun.tools.javac}), which is non-standard and
     * may get deprecated in future Java versions. As a consequence, to write
     * sustainable adapters, it is not recommended to use the printer API.
     * 
     * <p>
     * Instead, use the adapter's API directly, which relies on an abstraction of
     * the AST: {@link javax.lang.model} and {@link org.jsweet.transpiler.model}. If
     * some feature seems to be missing, please contact JSweet.org to help improving
     * this API.
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

    public Set<String> getErasedTypes() {
        if (parentAdapter == null) {
            throw new RuntimeException("unimplemented behavior");
        } else {
            return parentAdapter.getErasedTypes();
        }
    }

    /**
     * Substitutes if necessary the given foreach loop.
     * 
     * @param foreachLoop     the foreach loop to print
     * @param targetHasLength true if the iterable defines a public length field
     * @param indexVarName    a possible (fresh) variable name that can used to
     *                        iterate
     */
    public boolean substituteForEachLoop(ForeachLoopElement foreachLoop, boolean targetHasLength, String indexVarName) {
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
     * @param exprStr the expression being tested as a string (null if provided as a
     *                tree)
     * @param expr    the expression being tested as a tree (null if provided as a
     *                string)
     * @param type    the type of the instanceof expression
     * @return true if substituted
     */
    public boolean substituteInstanceof(String exprStr, ExtendedElement expr, TypeMirror type) {
        return parentAdapter == null ? false : parentAdapter.substituteInstanceof(exprStr, expr, type);
    }

    /**
     * Substitutes if necessary a type cast expression.
     */
    public boolean substituteTypeCast(TypeCastElement castExpression) {
        return parentAdapter == null ? false : parentAdapter.substituteTypeCast(castExpression);
    }

    /**
     * Substitutes if necessary the pattern of a case statement.
     */
    public boolean substituteCaseStatementPattern(CaseElement caseStatement, ExtendedElement pattern) {
        return parentAdapter == null ? false : parentAdapter.substituteCaseStatementPattern(caseStatement, pattern);
    }

    /**
     * Substitutes if necessary the selector expression of a switch statement.
     */
    public boolean substituteSwitchStatementSelector(ExtendedElement selector) {
        return parentAdapter == null ? false : parentAdapter.substituteSwitchStatementSelector(selector);
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
     * This method is called before starting printing a compilation unit (its namespace and content).
     * getCompilationUnit() is available on this adapter at this stage.
     */
    public void beforeCompilationUnit() {
        if (parentAdapter != null) {
            parentAdapter.beforeCompilationUnit();
        }
    }
    
    /**
     * This method is called before starting printing the body of a type.
     */
    public void beforeTypeBody(TypeElement type) {
        if (parentAdapter != null) {
            parentAdapter.beforeTypeBody(type);
        }
    }

    /**
     * This method is called after printing the body of a type.
     */
    public void afterTypeBody(TypeElement type) {
        if (parentAdapter != null) {
            parentAdapter.afterTypeBody(type);
        }
    }

    /**
     * Adapts the JavaDoc comment for a given element.
     * 
     * @param element     the documented element
     * @param commentText the comment text if any (null when no comment)
     * @return the adapted comment (null will remove the JavaDoc comment)
     */
    public String adaptDocComment(Element element, String commentText) {
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

    /**
     * Gets the types API, which provides a set of utilities on TypeMirror.
     * 
     * @see TypeMirror
     * @see Element#asType()
     * @see ExtendedElement#getType()
     */
    public Types types() {
        return context.types;
    }

    /**
     * Gets the util API, which provides a set of utilities.
     */
    public Util util() {
        return context.util;
    }

    protected Trees trees() {
        return context.trees;
    }

    /**
     * Print the macro name in the code.
     */
    protected final void printMacroName(String macroName) {
        print("/* " + macroName + " */");
    }

    /**
     * Tells if the given element is ambient (part of a def.* package or within an
     * <code>@Ambient</code>-annotated scope).
     */
    public final boolean isAmbientDeclaration(Element element) {
        return context.isAmbientDeclaration((Element) element);
    }

    /**
     * Gets the transpiler options.
     */
    public final JSweetOptions getTranspilerOptions() {
        return context.options;
    }

    /**
     * This method sets a header to the currently printed file. This header can be
     * TypeScript code, but use with caution since it may raise compilation errors.
     * 
     * <p>
     * Several headers can be added to the same file. Note that a new line will be
     * automatically added at the end of the last header (if any), but not between
     * each header. Headers will be printer in the order they have been added to the
     * file. Headers are reset for each new file.
     * 
     * @param key    a key to identify the header (see {@link #getHeader(String)})
     * @param header any string that will be printed at the beginning of the file
     *               (only when not in bundle mode)
     * 
     * @see #getHeader(String)
     */
    public final void addHeader(String key, String header) {
        context.addHeader(key, header);
    }

    /**
     * Gets the header associated to the given key (null if non-existing key).
     * 
     * @param key the header's key as set by {@link #addHeader(String, String)}
     * @return the associated header (null if non-existing key)
     * 
     * @see #addHeader(String, String)
     */
    public final String getHeader(String key) {
        return context.getHeader(key);
    }

    /**
     * Tells if this element is an inlined expression. An inlined expression
     * typically requires parenthesis (on contrary to top-level statement for
     * instance).
     * 
     * @param element the element to check
     * @return true if an inlined expression
     */
    public final boolean isInlinedExpression(ExtendedElement element) {
        return printer.isInlinedExpression(((ExtendedElementSupport<?>) element).getTree());
    }

    /**
     * Returns a comparator that will control the order of class members for output
     * (default will keep order of appearance in the source code).
     */
    public Comparator<ExtendedElement> getClassMemberComparator() {
        if (parentAdapter == null) {
            return new Comparator<ExtendedElement>() {
                @Override
                public int compare(ExtendedElement e1, ExtendedElement e2) {
                    SourcePosition sourcePosition1 = e1.getSourcePosition();
                    SourcePosition sourcePosition2 = e2.getSourcePosition();

                    return sourcePosition1.getStartPosition().compareTo(sourcePosition2.getStartPosition());
                }
            };
        } else {
            return parentAdapter.getClassMemberComparator();
        }
    }

    /**
     * Optionally substitutes the default variable declaration keyword
     * (<code>var</code>, <code>let</code>, <code>const</code>) for the given
     * variable. By default, regular variables are declared with the
     * <code>let</code> keyword, except for unmodified variables, which are declared
     * with the <code>const</code> keyword.
     * 
     * @param variable the target variable
     * @return true if the default keyword has been substituted, false otherwise
     *         (default is false)
     */
    public boolean substituteVariableDeclarationKeyword(VariableElement variable) {
        return parentAdapter == null ? false : parentAdapter.substituteVariableDeclarationKeyword(variable);
    }

    /**
     * Returns the current compilation unit element.
     */
    public CompilationUnitElement getCompilationUnit() {
        return new CompilationUnitElementSupport(getCompilationUnitTree());
    }

    protected final CompilationUnitTree getCompilationUnitTree() {
        return printer.getCompilationUnit();
    }

    protected final PackageElement getPackageElement() {
        return Util.getElement(getCompilationUnitTree().getPackage());
    }

//    protected final <T extends Element> T toElement(Tree tree) {
//        return Util.getElement(tree);
//    }
//
//    @SuppressWarnings("unchecked")
//    protected final <T extends TypeMirror> T toType(Tree tree) {
//        return (T) util().getTypeForTree(tree, getCompilationUnitTree());
//    }
}
