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
package org.jsweet.transpiler.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;

/**
 * A utility class to print JSDoc comments from regular Java comments.
 * 
 * @author Renaud Pawlak
 */
public class JSDoc {

	private JSDoc() {
	}

	/**
	 * A regexp that matches JavaDoc <code>@param</code> expressions.
	 */
	public static final Pattern paramPattern = Pattern.compile("(\\s*@param\\s+)(\\w+)(.*)");
	/**
	 * A regexp that matches JavaDoc <code>@author</code> expressions.
	 */
	public static final Pattern authorPattern = Pattern.compile("(\\s*@author\\s+)(\\w+)(.*)");
	/**
	 * A regexp that matches JavaDoc <code>@return</code> expression.
	 */
	public static final Pattern returnPattern = Pattern.compile("(\\s*@return\\s+)(.*)");
	/**
	 * A regexp that matches JavaDoc <code>@link</code> expressions.
	 */
	public static final Pattern linkPattern = Pattern.compile("(\\{@link\\s+)([\\w\\.#,]+)\\s+[^}]*(\\})");

	/**
	 * Gets the JSDoc type from a Java type tree and/or type.
	 * 
	 * @param typeTree        the Java type tree
	 * @param type            the Java type
	 * @param compilationUnit TODO
	 * 
	 * @return the JSDoc type
	 */
	public static String getMappedDocType(JSweetContext context, Tree typeTree, TypeMirror type,
			CompilationUnitTree compilationUnit) {
		String qualifiedName = type.toString();
		if (typeTree instanceof ParameterizedTypeTree) {
			TypeMirror parametrizedType = context.util.getTypeForTree(((ParameterizedTypeTree) typeTree).getType(),
					compilationUnit);
			qualifiedName = parametrizedType.toString();
		}
		if (type instanceof TypeVariable) {
			TypeVariable typeVar = (TypeVariable) type;
			if (typeVar.getUpperBound() == null) {
				return "*";
			} else {
				return getMappedDocType(context, null, typeVar.getUpperBound(), compilationUnit);
			}
		}
		boolean isMapped = false;
		if (typeTree != null) {
			ExtendedElement extendedElement = ExtendedElementFactory.INSTANCE
					.create(TreePath.getPath(compilationUnit, typeTree), context);
			for (BiFunction<ExtendedElement, String, Object> mapping : context.getFunctionalTypeMappings()) {
				Object mapped = mapping.apply(extendedElement, qualifiedName);
				if (mapped instanceof String) {
					isMapped = true;
					qualifiedName = (String) mapped;
				} else if (mapped instanceof Tree) {
					isMapped = true;
					qualifiedName = getMappedDocType(context, (Tree) mapped,
							context.util.getTypeForTree((Tree) mapped, compilationUnit), compilationUnit);
				}
			}
		}
		if (context.isMappedType(qualifiedName)) {
			isMapped = true;
			qualifiedName = context.getTypeMappingTarget(qualifiedName);
		}
		if (!isMapped && !context.util.isPrimitiveOrVoid(type) && context.types.asElement(type) != null) {
			qualifiedName = context.getRootRelativeName(null, context.types.asElement(type));
		}
		if ("Array".equals(qualifiedName) && typeTree instanceof ParameterizedTypeTree) {
			Tree firstTypeArgTree = ((ParameterizedTypeTree) typeTree).getTypeArguments().get(0);
			TypeMirror firstTypeArgType = context.util.getTypeForTree(firstTypeArgTree, compilationUnit);
			return getMappedDocType(context, firstTypeArgTree, firstTypeArgType, compilationUnit) + "[]";
		}
		if (typeTree instanceof ParameterizedTypeTree) {
			TypeMirror parametrizedType = context.util.getTypeForTree(((ParameterizedTypeTree) typeTree).getType(),
					compilationUnit);
			return getMappedDocType(context, ((ParameterizedTypeTree) typeTree).getType(), parametrizedType,
					compilationUnit);
		}
		if (!isMapped && context.isInterface((TypeElement) context.types.asElement(type))) {
			return "*";
		}
		return "any".equals(qualifiedName) ? "*" : qualifiedName;
	}

	/**
	 * Replaces Java links by JSDoc links in a doc text.
	 */
	private static String replaceLinks(JSweetContext context, String text) {
		Matcher linkMatcher = linkPattern.matcher(text);
		boolean result = linkMatcher.find();
		int lastMatch = 0;
		if (result) {
			StringBuffer sb = new StringBuffer();
			do {
				sb.append(text.substring(lastMatch, linkMatcher.start()));
				sb.append(linkMatcher.group(1));
				TypeElement type = context.util.getTypeElementByName(context, linkMatcher.group(2));
				sb.append(type == null ? linkMatcher.group(2)
						: getMappedDocType(context, null, type.asType(), context.util.getCompilationUnit(type)));
				sb.append(linkMatcher.group(3));
				lastMatch = linkMatcher.end();
				result = linkMatcher.find();
			} while (result);
			sb.append(text.substring(lastMatch));
			return sb.toString();
		}
		return text;
	}

	/**
	 * Adapts the JavaDoc comment for a given element to conform to JSDoc.
	 * 
	 * <p>
	 * By default, this implementation does not auto-generate comments. This
	 * behavior can be overridden by adding a line in the comment before calling
	 * this method. For example:
	 * 
	 * <pre>
	 * public String adaptDocComment(Tree element, String commentText) {
	 * 	if (commentText == null) {
	 * 		return "My default comment";
	 * 	}
	 * 	super(element, commentText);
	 * }
	 * </pre>
	 * 
	 * @param tree        the documented element
	 * @param commentText the comment text if any (null when no comment)
	 * @return the adapted comment (null will remove the JavaDoc comment)
	 */
	public static String adaptDocComment(JSweetContext context, TreePath treePath, Tree tree, String commentText) {
		if (tree instanceof ClassTree) {
			MethodTree mainConstructor = null;
			for (Tree member : ((ClassTree) tree).getMembers()) {
				if (member instanceof MethodTree) {

					TreePath memberTreePath = TreePath.getPath(treePath, member);
					ExecutableElement methodElement = context.util.getElementForTreePath(memberTreePath);

					if (methodElement.getKind() == ElementKind.CONSTRUCTOR
							&& ((MethodTree) member).getModifiers().getFlags().contains(Modifier.PUBLIC)) {
						if (mainConstructor == null || mainConstructor.getParameters().size() < ((MethodTree) member)
								.getParameters().size()) {
							mainConstructor = ((MethodTree) member);
						}
					}
				}
			}
			if (mainConstructor != null) {
				TreePath mainConstructorTreePath = TreePath.getPath(treePath, mainConstructor);
				String docComment = context.trees.getDocComment(mainConstructorTreePath);
				String author = null;
				if (docComment != null) {
					List<String> commentLines = commentText == null ? null
							: new ArrayList<>(Arrays.asList(commentText.split("\n")));
					// replace the class comment with the main constructor's
					commentText = docComment;
					// gets the author for further insertion
					if (commentLines != null) {
						for (String line : commentLines) {
							if (authorPattern.matcher(line).matches()) {
								author = line;
								break;
							}
						}
					}
				}
				if (commentText != null) {
					commentText = replaceLinks(context, commentText);
					List<String> commentLines = new ArrayList<>(Arrays.asList(commentText.split("\n")));
					applyForMethod(context, mainConstructor, mainConstructorTreePath, commentLines);
					ClassTree clazz = (ClassTree) tree;
					TypeElement classElement = context.util.getElementForTreePath(treePath);
					if (classElement.getKind() == ElementKind.ENUM) {
						commentLines.add(" @enum");
					}
					if (clazz.getExtendsClause() != null) {
						commentLines.add(" @extends " + getMappedDocType(context, clazz.getExtendsClause(),
								classElement.getSuperclass(), treePath.getCompilationUnit()));
					}
					if (author != null) {
						commentLines.add(author);
					}
					return String.join("\n", commentLines);
				}
			}
		}
		Element element = context.util.getElementForTreePath(treePath);
		List<String> commentLines = null;
		if (commentText == null) {
			if (tree instanceof MethodTree && context.hasAnnotationType(element, Override.class.getName())) {
				commentText = "";
				commentLines = new ArrayList<>();
				applyForMethod(context, (MethodTree) tree, treePath, commentLines);
			}
		}
		if (commentText == null) {
			return null;
		}
		commentText = replaceLinks(context, commentText);
		commentLines = new ArrayList<>(Arrays.asList(commentText.split("\n")));
		if (tree instanceof MethodTree) {
			MethodTree method = (MethodTree) tree;
			if (element.getKind() != ElementKind.CONSTRUCTOR) {
				applyForMethod(context, method, treePath, commentLines);
			} else {
				// erase constructor comments because jsDoc uses class comments
				return null;
			}
		} else if (tree instanceof ClassTree) {
			ClassTree clazz = (ClassTree) tree;
			if (element.getKind() == ElementKind.ENUM) {
				commentLines.add(" @enum");
				for (Tree def : clazz.getMembers()) {
					if (def instanceof VariableTree) {
						VariableTree var = (VariableTree) def;
						TreePath varTreePath = TreePath.getPath(treePath, var);
						VariableElement varElement = context.util.getElementForTreePath(varTreePath);
						if (varElement.getModifiers() != null && varElement.getModifiers().contains(Modifier.PUBLIC)
								&& varElement.getModifiers().contains(Modifier.STATIC)) {
							commentLines
									.add("@property {"
											+ getMappedDocType(context, var.getType(),
													context.util.getTypeForTreePath(
															TreePath.getPath(varTreePath, var.getType())),
													varTreePath.getCompilationUnit())
											+ "} " + var.getName().toString());

							String varComment = context.trees.getDocComment(varTreePath);
							if (varComment != null) {
								varComment = replaceLinks(context, varComment);
								commentLines.addAll(new ArrayList<>(Arrays.asList(varComment.split("\n"))));
							}
						}
					}
				}
			}
			if (clazz.getExtendsClause() != null) {
				TreePath extendsTreePath = TreePath.getPath(treePath, clazz.getExtendsClause());
				commentLines.add(" @extends " + getMappedDocType(context, clazz.getExtendsClause(),
						context.util.getTypeForTreePath(extendsTreePath), treePath.getCompilationUnit()));
			}
			commentLines.add(" @class");
		}

		return String.join("\n", commentLines);
	}

	private static void applyForMethod( //
			JSweetContext context, //
			MethodTree method, //
			TreePath methodTreePath, //
			List<String> commentLines) {

		ExecutableElement methodElement = context.util.getElementForTreePath(methodTreePath);

		Set<String> params = new HashSet<>();
		boolean hasReturn = false;
		for (int i = 0; i < commentLines.size(); i++) {
			Matcher m = paramPattern.matcher(commentLines.get(i));
			if (m.matches()) {
				String name = m.group(2);
				params.add(name);
				VariableTree parameter = context.util.findParameter(method, name);
				if (parameter != null) {
					TreePath parameterTypeTreePath = TreePath.getPath(methodTreePath, parameter.getType());
					commentLines.set(i,
							m.group(1) + "{"
									+ getMappedDocType(context, parameter.getType(),
											context.util.getTypeForTreePath(parameterTypeTreePath),
											methodTreePath.getCompilationUnit())
									+ "} " + m.group(2) + m.group(3));
				}
			} else if ((m = returnPattern.matcher(commentLines.get(i))).matches()) {
				hasReturn = true;
				if (method.getReturnType() != null) {
					TreePath returnTypeTreePath = TreePath.getPath(methodTreePath, method.getReturnType());
					commentLines.set(i,
							m.group(1) + "{"
									+ getMappedDocType(context, method.getReturnType(),
											context.util.getTypeForTreePath(returnTypeTreePath),
											returnTypeTreePath.getCompilationUnit())
									+ "} " + m.group(2));
				}
			}
		}
		for (VariableTree parameter : method.getParameters()) {
			String name = parameter.getName().toString();
			if (!params.contains(name)) {
				TreePath parameterTypeTreePath = TreePath.getPath(methodTreePath, parameter.getType());
				commentLines.add(" @param {" + getMappedDocType(context, parameter.getType(),
						context.util.getTypeForTreePath(parameterTypeTreePath),
						parameterTypeTreePath.getCompilationUnit()) + "} " + name);
			}
		}

		if (!hasReturn && !(method.getReturnType() == null
				|| context.util.getTypeForTreePath(TreePath.getPath(methodTreePath, method.getReturnType()))
						.getKind() == TypeKind.VOID)
				&& methodElement.getKind() != ElementKind.CONSTRUCTOR) {
			TreePath returnTypeTreePath = TreePath.getPath(methodTreePath, method.getReturnType());
			commentLines.add(" @return {" + getMappedDocType(context, method.getReturnType(),
					context.util.getTypeForTreePath(returnTypeTreePath), returnTypeTreePath.getCompilationUnit())
					+ "}");
		}
		if (methodElement.getModifiers().contains(Modifier.PRIVATE)) {
			commentLines.add(" @private");
		}
		if (methodElement.getKind() == ElementKind.CONSTRUCTOR) {
			commentLines.add(" @class");
		}
	}

}
