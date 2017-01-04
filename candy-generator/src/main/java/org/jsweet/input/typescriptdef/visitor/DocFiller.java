/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
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
package org.jsweet.input.typescriptdef.visitor;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner tries to fill the documentation from external sources, when it
 * does not pre-exist in the definition file.
 * 
 * @author Renaud Pawlak
 */
public class DocFiller extends Scanner {

	private final static Logger logger = Logger.getLogger(DocFiller.class);

	int domDocsCount = 0;
	int domDescriptionDocsCount = 0;
	int langDocsCount = 0;
	int langDescriptionDocsCount = 0;
	String currentModule;

	public DocFiller(Context context) {
		super(context);
	}

	@Override
	public void visitCompilationUnit(CompilationUnit compilationUnit) {
		if (context.isDependency(compilationUnit)) {
			return;
		}

		super.visitCompilationUnit(compilationUnit);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		if (typeDeclaration.getDocumentation() == null) {
			String moduleName = getCurrentModuleName();
			if (JSweetDefTranslatorConfig.LANG_PACKAGE.equals(moduleName)
					|| JSweetDefTranslatorConfig.DOM_PACKAGE.equals(moduleName)) {
				this.currentModule = moduleName;

				String content = getTypeContent(context.cacheDir, "mdn", moduleName, typeDeclaration.getName());
				if (content != null) {
					try {
						Document doc = Jsoup.parse(content, "UTF-8");
						NodeTraversor traversor;
						traversor = new NodeTraversor(new MdnTableFormatGrabber(this, typeDeclaration));
						traversor.traverse(doc.body());
						traversor = new NodeTraversor(new MdnDefinitionListFormatGrabber(this, typeDeclaration));
						traversor.traverse(doc.body());
						traversor = new NodeTraversor(new MdnMainDescriptionGrabber(this, typeDeclaration));
						traversor.traverse(doc.body());
					} catch (Throwable t) {
						context.reportError("cannot fill documentation for " + context.getTypeName(typeDeclaration),
								typeDeclaration.getToken(), t);
					}
				}
			}
		}
	}

	void countDoc(boolean description) {
		if (JSweetDefTranslatorConfig.LANG_PACKAGE.equals(currentModule)) {
			if (description) {
				langDescriptionDocsCount++;
			} else {
				langDocsCount++;
			}
		} else {
			if (description) {
				domDescriptionDocsCount++;
			} else {
				domDocsCount++;
			}
		}
	}

	static String getTypeContent(File cacheDir, String provider, String moduleName, String typeName) {
		String content = null;
		try {
			File cachedFile = cacheDir == null ? null : new File(cacheDir, provider + File.separator + typeName);
			if (cachedFile != null && cachedFile.exists()) {
				content = FileUtils.readFileToString(cachedFile);
			} else {
				try {
					if (JSweetDefTranslatorConfig.DOM_PACKAGE.equals(moduleName)) {
						content = IOUtils
								.toString(new URL("https://developer.mozilla.org/en-US/docs/Web/API/" + typeName));
					}
					if (JSweetDefTranslatorConfig.LANG_PACKAGE.equals(moduleName)) {
						content = IOUtils.toString(
								new URL("https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/"
										+ typeName));
					}
					logger.info("content downloaded for " + moduleName + "." + typeName);
				} catch (Exception e) {
					content = "";
				}
				if (cachedFile != null) {
					logger.info("writing cache to " + cachedFile);
					FileUtils.write(cachedFile, content, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String removeTags(String html) {
		return removeTags(html, new String[] { "a", "body" });
	}

	public static String removeTags(String html, String[] tagsToBeRemoved) {
		StringBuilder sb = new StringBuilder();
		NodeTraversor traversor = new NodeTraversor(new TagRemover(sb, tagsToBeRemoved));
		traversor.traverse(Jsoup.parse(html).body());
		return sb.toString().replace("<p></p>", "");
	}

	public static void main(String[] args) {
		System.out.println(removeTags("Ceci est un test <a href=\"tutu\">slurp</a> hop <code>arlgs</code>.",
				new String[] { "a", "body" }));
		String content = getTypeContent(null, "mdn", JSweetDefTranslatorConfig.LANG_PACKAGE, "Array");
		Document doc = Jsoup.parse(content, "UTF-8");
		System.out.println(doc.toString());
	}

	static <T> void addToMap(Map<String, List<T>> map, String key, T value) {
		List<T> l = map.get(key);
		if (l == null) {
			l = new ArrayList<>();
			map.put(key, l);
		}
		l.add(value);
	}

	static List<Node> getSiblingTags(Node node) {
		List<Node> siblings = new ArrayList<>();
		for (Node n : node.siblingNodes()) {
			if (!(n instanceof TextNode)) {
				siblings.add(n);
			}
		}
		return siblings;
	}

	static boolean isLastSiblingTag(Node node) {
		List<Node> siblings = new ArrayList<>();
		for (Node n : node.parent().childNodes()) {
			if (!(n instanceof TextNode)) {
				siblings.add(n);
			}
		}
		return (node == siblings.get(siblings.size() - 1));
	}

	@Override
	public void onScanEnded() {
		System.out.println("Package lang: " + langDescriptionDocsCount + " type descriptions and " + langDocsCount
				+ " member docs added.");
		System.out.println("Package dom: " + domDescriptionDocsCount + " type descriptions and " + domDocsCount
				+ " member docs added.");
	}

}

class MdnTableFormatGrabber implements NodeVisitor {

	private Map<String, List<Declaration>> members = new HashMap<>();
	private DocFiller docFiller;

	public MdnTableFormatGrabber(DocFiller docFiller, TypeDeclaration typeDeclaration) {
		this.docFiller = docFiller;
		for (Declaration member : typeDeclaration.getMembers()) {
			if (member.getDocumentation() != null
					|| member.hasStringAnnotation(org.jsweet.JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE)) {
				continue;
			}
			DocFiller.addToMap(members, member.getName(), member);
		}
	}

	@Override
	public void head(Node node, int depth) {
		if (node instanceof TextNode) {
			String text = ((TextNode) node).text().trim();
			if (members.containsKey(text)) {
				Node parent = node.parent();
				if (parent != null && !"td".equals(parent.nodeName())) {
					parent = parent.parent();
				}
				if (parent != null && !"td".equals(parent.nodeName())) {
					parent = parent.parent();
				}
				if (parent != null && "td".equals(parent.nodeName())) {
					List<Node> siblings = parent.parent().childNodes();
					List<Node> tdSiblings = new ArrayList<Node>();
					siblings.forEach(n -> {
						if ("td".equals(n.nodeName()))
							tdSiblings.add(n);
					});

					if (tdSiblings.get(0) == parent && tdSiblings.size() == 3) {
						Node td = tdSiblings.get(2);
						String s = td.toString();
						String doc = "/** " + DocFiller.removeTags(s.substring(4, s.length() - 5)) + " */";
						for (Declaration d : members.get(text)) {
							d.setDocumentation(doc);
						}
						docFiller.countDoc(false);
					}
				}
			}
		}
	}

	@Override
	public void tail(Node node, int depth) {
	}

}

class MdnDefinitionListFormatGrabber implements NodeVisitor {

	private Map<String, List<Declaration>> members = new HashMap<>();
	private DocFiller docFiller;

	public MdnDefinitionListFormatGrabber(DocFiller docFiller, TypeDeclaration typeDeclaration) {
		this.docFiller = docFiller;
		for (Declaration member : typeDeclaration.getMembers()) {
			if (member.getDocumentation() != null
					|| member.hasStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_STRING_TYPE)) {
				continue;
			}
			if (member instanceof FunctionDeclaration) {
				DocFiller.addToMap(members, typeDeclaration.getName() + "." + member.getName() + "()", member);
			} else {
				DocFiller.addToMap(members, typeDeclaration.getName() + "." + member.getName(), member);
			}
		}
	}

	@Override
	public void head(Node node, int depth) {
		if (node instanceof TextNode) {
			String text = ((TextNode) node).text().trim();
			if (members.containsKey(text)) {
				Node parent = node.parent();
				if (parent != null && !"dt".equals(parent.nodeName())) {
					parent = parent.parent();
				}
				if (parent != null && !"dt".equals(parent.nodeName())) {
					parent = parent.parent();
				}
				if (parent != null && "dt".equals(parent.nodeName())) {
					List<Node> siblings = parent.parent().childNodes();
					List<Node> dlSiblings = new ArrayList<Node>();
					siblings.forEach(n -> {
						if ("dt".equals(n.nodeName()) || "dd".equals(n.nodeName()))
							dlSiblings.add(n);
					});
					for (int i = 0; i < dlSiblings.size(); i++) {
						if (dlSiblings.get(i) == parent) {
							if (i < dlSiblings.size() - 1 && "dd".equals(dlSiblings.get(i + 1).nodeName())) {
								String s = dlSiblings.get(i + 1).toString();
								String doc = "/** " + DocFiller.removeTags(s.substring(4, s.length() - 5)) + " */";
								for (Declaration d : members.get(text)) {
									d.setDocumentation(doc);
								}
								docFiller.countDoc(false);
							}
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void tail(Node node, int depth) {
	}

}

class MdnMainDescriptionGrabber implements NodeVisitor {

	private StringBuffer description = new StringBuffer();
	private boolean found = false;
	private TypeDeclaration typeDeclaration;
	private DocFiller docFiller;

	public MdnMainDescriptionGrabber(DocFiller docFiller, TypeDeclaration typeDeclaration) {
		this.docFiller = docFiller;
		this.typeDeclaration = typeDeclaration;
	}

	@Override
	public void head(Node node, int depth) {
		if (!found) {
			if (node.attr("id") != null && node.attr("id").equalsIgnoreCase("Quick_Links")) {
				found = true;
				while (DocFiller.isLastSiblingTag(node)) {
					node = node.parent();
				}

				Node n = node;

				while ((n = n.nextSibling()) != null && !("h2".equals(n.nodeName()) || "h3".equals(n.nodeName()))) {
					description.append(n.outerHtml());
				}

				String s = Jsoup.parse(description.toString()).text();
				if ("".equals(s.trim())) {
					if (n == null) {
						n = node;
					}
					while ((n = n.nextSibling()) != null && !("h2".equals(n.nodeName()) || "h3".equals(n.nodeName()))) {
						description.append(n.outerHtml());
					}
				}

				typeDeclaration.setDocumentation("/** " + DocFiller.removeTags(description.toString()) + " */");
				docFiller.countDoc(true);
			}
		}
	}

	@Override
	public void tail(Node node, int depth) {
	}

}

class TagRemover implements NodeVisitor {

	private StringBuilder out;
	private Set<String> tagsToBeRemoved;

	public TagRemover(StringBuilder out, String[] tagsToBeRemoved) {
		this.out = out;
		this.tagsToBeRemoved = new HashSet<String>(Arrays.asList(tagsToBeRemoved));
	}

	public void head(Node node, int depth) {
		String name = node.nodeName();
		if (!tagsToBeRemoved.contains(name)) {
			if (node instanceof TextNode) {
				TextNode tn = (TextNode) node;
				out.append(tn.toString());
			} else {
				out.append("<" + name + ">");
			}
		}
	}

	public void tail(Node node, int depth) {
		String name = node.nodeName();
		if (!tagsToBeRemoved.contains(name)) {
			if (!(node instanceof TextNode)) {
				out.append("</" + name + ">");
			}
		}
	}

}
