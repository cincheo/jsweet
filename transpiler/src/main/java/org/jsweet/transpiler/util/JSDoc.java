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

import javax.lang.model.element.Modifier;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.support.ExtendedElementSupport;

import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.TypeVar;
import com.sun.tools.javac.parser.Tokens.Comment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

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
	 * @param typeTree
	 *            the Java type tree
	 * @param type
	 *            the Java type
	 * @return the JSDoc type
	 */
	public static String getMappedDocType(JSweetContext context, JCTree typeTree, Type type) {
		String qualifiedName = type.toString();
		if (typeTree instanceof JCTypeApply) {
			qualifiedName = ((JCTypeApply) typeTree).clazz.type.toString();
		}
		if (type instanceof TypeVar) {
			TypeVar typeVar = (TypeVar) typeTree.type;
			if (typeVar.getUpperBound() == null) {
				return "*";
			} else {
				return getMappedDocType(context, null, typeVar.getUpperBound());
			}
		}
		boolean isMapped = false;
		if (typeTree != null) {
			for (BiFunction<ExtendedElement, String, Object> mapping : context.getFunctionalTypeMappings()) {
				Object mapped = mapping.apply(new ExtendedElementSupport<JCTree>(typeTree), qualifiedName);
				if (mapped instanceof String) {
					isMapped = true;
					qualifiedName = (String) mapped;
				} else if (mapped instanceof JCTree) {
					isMapped = true;
					qualifiedName = getMappedDocType(context, (JCTree) mapped, ((JCTree) mapped).type);
				}
			}
		}
		if (context.isMappedType(qualifiedName)) {
			isMapped = true;
			qualifiedName = context.getTypeMappingTarget(qualifiedName);
		}
		if (!isMapped && !type.isPrimitiveOrVoid()) {
			qualifiedName = context.getRootRelativeName(null, type.tsym);
		}
		if ("Array".equals(qualifiedName) && typeTree instanceof JCTypeApply) {
			return getMappedDocType(context, ((JCTypeApply) typeTree).arguments.head,
					((JCTypeApply) typeTree).arguments.head.type) + "[]";
		}
		if (typeTree instanceof JCTypeApply) {
			return getMappedDocType(context, ((JCTypeApply) typeTree).clazz, ((JCTypeApply) typeTree).clazz.type);
		}
		if (!isMapped && context.isInterface(type.tsym)) {
			return "*";
		}
		return "any".equals(qualifiedName) ? "*" : qualifiedName;
	}

	/**
	 * Replaces Java links by JSDoc links in a doc text.
	 */
	public static String replaceLinks(JSweetContext context, String text) {
		Matcher linkMatcher = linkPattern.matcher(text);
		boolean result = linkMatcher.find();
		int lastMatch = 0;
		if (result) {
			StringBuffer sb = new StringBuffer();
			do {
				sb.append(text.substring(lastMatch, linkMatcher.start()));
				sb.append(linkMatcher.group(1));
				TypeSymbol type = Util.getTypeByName(context, linkMatcher.group(2));
				sb.append(type == null ? linkMatcher.group(2) : getMappedDocType(context, null, type.type));
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
	 * public String adaptDocComment(JCTree element, String commentText) {
	 * 	if (commentText == null) {
	 * 		return "My default comment";
	 * 	}
	 * 	super(element, commentText);
	 * }
	 * </pre>
	 * 
	 * @param element
	 *            the documented element
	 * @param commentText
	 *            the comment text if any (null when no comment)
	 * @return the adapted comment (null will remove the JavaDoc comment)
	 */
	public static String adaptDocComment(JSweetContext context, JCCompilationUnit compilationUnit, JCTree element,
			String commentText) {
		if (element instanceof JCClassDecl) {
			JCMethodDecl mainConstructor = null;
			for (JCTree member : ((JCClassDecl) element).defs) {
				if (member instanceof JCMethodDecl) {
					if (((JCMethodDecl) member).sym.isConstructor()
							&& ((JCMethodDecl) member).getModifiers().getFlags().contains(Modifier.PUBLIC)) {
						if (mainConstructor == null || mainConstructor.getParameters().size() < ((JCMethodDecl) member)
								.getParameters().size()) {
							mainConstructor = ((JCMethodDecl) member);
						}
					}
				}
			}
			if (mainConstructor != null) {
				Comment comment = compilationUnit.docComments.getComment(mainConstructor);
				String author = null;
				if (comment != null) {
					List<String> commentLines = commentText == null ? null
							: new ArrayList<>(Arrays.asList(commentText.split("\n")));
					// replace the class comment with the main constructor's
					commentText = comment.getText();
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
					applyForMethod(context, mainConstructor, commentLines);
					JCClassDecl clazz = (JCClassDecl) element;
					if (clazz.sym.isEnum()) {
						commentLines.add(" @enum");
					}
					if (clazz.extending != null) {
						commentLines
								.add(" @extends " + getMappedDocType(context, clazz.extending, clazz.extending.type));
					}
					if (author != null) {
						commentLines.add(author);
					}
					return String.join("\n", commentLines);
				}
			}
		}
		List<String> commentLines = null;
		if (commentText == null) {
			if (element instanceof JCMethodDecl
					&& context.hasAnnotationType(((JCMethodDecl) element).sym, Override.class.getName())) {
				commentText = "";
				commentLines = new ArrayList<>();
				applyForMethod(context, (JCMethodDecl) element, commentLines);
			}
		}
		if (commentText == null) {
			return null;
		}
		commentText = replaceLinks(context, commentText);
		commentLines = new ArrayList<>(Arrays.asList(commentText.split("\n")));
		if (element instanceof JCMethodDecl) {
			JCMethodDecl method = (JCMethodDecl) element;
			if (!method.sym.isConstructor()) {
				applyForMethod(context, method, commentLines);
			} else {
				// erase constructor comments because jsDoc uses class comments
				return null;
			}
		} else if (element instanceof JCClassDecl) {
			JCClassDecl clazz = (JCClassDecl) element;
			if (clazz.sym.isEnum()) {
				commentLines.add(" @enum");
				for(JCTree def : clazz.defs) {
					if(def instanceof JCVariableDecl) {
						JCVariableDecl var = (JCVariableDecl) def;
						if (var.sym.getModifiers() != null && var.sym.getModifiers().contains(Modifier.PUBLIC)
								&& var.sym.getModifiers().contains(Modifier.STATIC)) {
							commentLines.add("@property {" + getMappedDocType(context, var.getType(), var.getType().type) + "} "
									+ var.getName().toString());
							Comment varComment = compilationUnit.docComments.getComment(var);
							if(varComment!=null) {
								String text = varComment.getText();
								if(text != null) {
									text = replaceLinks(context, text);
									commentLines.addAll(new ArrayList<>(Arrays.asList(text.split("\n"))));
								}
							}
						}
					}
				}
			}
			if (clazz.extending != null) {
				commentLines.add(" @extends " + getMappedDocType(context, clazz.extending, clazz.extending.type));
			}
			commentLines.add(" @class");
		}

		return String.join("\n", commentLines);
	}

	private static void applyForMethod(JSweetContext context, JCMethodDecl method, List<String> commentLines) {
		Set<String> params = new HashSet<>();
		boolean hasReturn = false;
		for (int i = 0; i < commentLines.size(); i++) {
			Matcher m = paramPattern.matcher(commentLines.get(i));
			if (m.matches()) {
				String name = m.group(2);
				params.add(name);
				JCVariableDecl parameter = Util.findParameter(method, name);
				if (parameter != null) {
					commentLines.set(i,
							m.group(1) + "{" + getMappedDocType(context, parameter.vartype, parameter.vartype.type)
									+ "} " + m.group(2) + m.group(3));
				}
			} else if ((m = returnPattern.matcher(commentLines.get(i))).matches()) {
				hasReturn = true;
				if (method.restype != null) {
					commentLines.set(i, m.group(1) + "{"
							+ getMappedDocType(context, method.restype, method.restype.type) + "} " + m.group(2));
				}
			}
		}
		for (JCVariableDecl parameter : method.getParameters()) {
			String name = parameter.name.toString();
			if (!params.contains(name)) {
				commentLines.add(" @param {" + getMappedDocType(context, parameter.vartype, parameter.vartype.type)
						+ "} " + name);
			}
		}
		if (!hasReturn && !(method.restype == null || context.symtab.voidType.equals(method.restype.type))
				&& !method.sym.isConstructor()) {
			commentLines.add(" @return {" + getMappedDocType(context, method.restype, method.restype.type) + "}");
		}
		if (method.sym.isPrivate()) {
			commentLines.add(" @private");
		}
		if (method.sym.isConstructor()) {
			commentLines.add(" @class");
		}
	}

}
