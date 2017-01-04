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
package org.jsweet.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * A tree is an element container that can have several child trees.
 * 
 * @author Renaud Pawlak
 *
 * @param <E>
 *            the type of the element attached to the tree
 * @param <ID>
 *            the type of the id attached to the tree
 */
public class Tree<E, ID> {

	private E element;
	private ID id;
	private Tree<E, ID> parentTree;
	private List<Tree<E, ID>> childTrees = new ArrayList<Tree<E, ID>>();

	/**
	 * A default scanner, to be overriden to perform recursive operations on a
	 * tree.
	 * 
	 * @author Renaud Pawlak
	 */
	public static class Scanner<E, ID> {
		protected Stack<Tree<E, ID>> stack = new Stack<>();

		public final void scan(Tree<E, ID> tree) {
			if (!enter(tree)) {
				return;
			}
			stack.push(tree);
			try {
				for (Tree<E, ID> childTree : tree.getChildTrees()) {
					scan(childTree);
				}
			} finally {
				stack.pop();
				exit(tree);
			}
		}

		protected boolean enter(Tree<E, ID> tree) {
			return true;
		}

		protected void exit(Tree<E, ID> tree) {
		}
	}

	/**
	 * A helper to match a tree or a path on the to-stringed values of its
	 * elements.
	 * 
	 * @author Renaud Pawlak
	 */
	public static class Matcher {
		protected String[] expressions;

		/**
		 * Each expression is a matcher for an element in the tested path.
		 * <p>
		 * Not operators are allowed with expressions starting with !.
		 * <p>
		 * A path will match if all the first elements match each expressions of
		 * the matcher.
		 */
		public Matcher(String... expressions) {
			this.expressions = expressions;
		}

		/**
		 * Returns true if this matcher matches the given tree (i.e. if one path
		 * starting from that tree matches).
		 */
		public boolean matches(Tree<?, ?> tree) {
			return matches(Arrays.asList(this.expressions), tree);
		}

		private static boolean matches(List<String> expressions, Tree<?, ?> tree) {
			if (expressions.isEmpty()) {
				return true;
			}

			String expr = expressions.get(0);
			if (expr.startsWith("!")) {
				expr = expr.substring(1);
				if (("" + tree.getElement()).equals(expr)) {
					return false;
				}
			} else {
				if (!("" + tree.getElement()).equals(expr)) {
					return false;
				}
			}

			if (tree.getChildTrees().isEmpty()) {
				return true;
			}

			for (Tree<?, ?> child : tree.getChildTrees()) {
				if (matches(expressions.subList(1, expressions.size()), child)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Returns true if this matcher matches the given path.
		 */
		public boolean matches(List<?> path) {
			return matches(Arrays.asList(this.expressions), path);
		}

		private static boolean matches(List<String> expressions, List<?> path) {
			if (expressions.isEmpty()) {
				return true;
			}
			if (path.isEmpty()) {
				return false;
			}

			String expr = expressions.get(0);
			if (expr.startsWith("!")) {
				expr = expr.substring(1);
				if (("" + path.get(0)).equals(expr)) {
					return false;
				}
			} else {
				if (!("" + path.get(0)).equals(expr)) {
					return false;
				}
			}

			return matches(expressions.subList(1, expressions.size()), path.subList(1, path.size()));
		}

	}

	/**
	 * Constructs a tree with a single element (no parent, no children).
	 */
	public Tree(E element) {
		this.element = element;
	}

	/**
	 * Gets the id attached to this tree (optional).
	 */
	public ID getID() {
		return id;
	}

	/**
	 * Sets the id attached to this tree.
	 */
	public void setID(ID id) {
		this.id = id;
	}

	/**
	 * Add some children elements to this tree.
	 * 
	 * @param element
	 *            the element to add children to (root node if null)
	 * @param childElements
	 *            the elements to add to the parent
	 */
	public void addChildElements(List<E> childElements) {
		for (E child : childElements) {
			Tree<E, ID> childTree = new Tree<E, ID>(child);
			childTree.parentTree = this;
			this.childTrees.add(childTree);
		}
	}

	public void clearChildTrees() {
		this.childTrees.clear();
	}

	public void addChildTrees(List<Tree<E, ID>> childTrees) {
		for (Tree<E, ID> childTree : childTrees) {
			childTree.parentTree = this;
			this.childTrees.add(childTree);
		}
	}

	public void addChildTree(Tree<E, ID> childTree) {
		childTree.parentTree = this;
		this.childTrees.add(childTree);
	}

	/**
	 * Adds a child to this tree (add the end of the current children).
	 */
	public Tree<E, ID> addChildElement(E childElement) {
		Tree<E, ID> childTree = new Tree<E, ID>(childElement);
		childTree.parentTree = this;
		this.childTrees.add(childTree);
		return childTree;
	}

	/**
	 * Gets the parent tree of this tree.
	 */
	public Tree<E, ID> getParentTree() {
		return parentTree;
	}

	/**
	 * Gets the parent of this tree.
	 */
	public E getParentElement() {
		return parentTree != null ? parentTree.element : null;
	}

	/**
	 * Gets the children of this tree.
	 */
	public List<Tree<E, ID>> getChildTrees() {
		return this.childTrees;
	}

	/**
	 * Gets the child trees that match the given element.
	 */
	public List<Tree<E, ID>> getChildTrees(E e) {
		return childTrees.stream().filter(t -> e.equals(t.element)).collect(Collectors.toList());
	}

	/**
	 * Get the elements attached to the child trees.
	 */
	public List<E> getChildElements() {
		return childTrees.stream().map(t -> t.element).collect(Collectors.toList());
	}

	/**
	 * Gets the element contained in this tree.
	 */
	public E getElement() {
		return this.element;
	}

	/**
	 * Sets the element contained in this tree;
	 */
	public void setElement(E element) {
		this.element = element;
	}

	/**
	 * Adds a path to the current tree.
	 */
	final public Tree<E, ID> addPath(List<E> path) {
		if (path.size() == 0) {
			return this;
		}
		List<E> childElements = getChildElements();
		int index = childElements.indexOf(path.get(0));
		Tree<E, ID> nextTree;
		if (index < 0) {
			nextTree = addChildElement(path.get(0));
		} else {
			nextTree = getChildTrees().get(index);
		}
		return nextTree.addPath(path.subList(1, path.size()));
	}

	public boolean isLeaf() {
		return childTrees.isEmpty();
	}

	@Override
	public String toString() {
		final StringBuffer output = new StringBuffer();
		new Tree.Scanner<E, ID>() {
			protected boolean enter(Tree<E, ID> tree) {
				output.append("[");
				output.append(tree.getElement());
				if (tree.getID() != null) {
					output.append("*");
				}
				return true;
			}

			protected void exit(Tree<E, ID> tree) {
				output.append("]");
			}
		}.scan(this);
		return output.toString();
	}

	public List<List<Tree<E, ID>>> findElement(final E element) {
		final List<List<Tree<E, ID>>> result = new ArrayList<>();
		final List<Tree<E, ID>> path = new ArrayList<>();
		new Tree.Scanner<E, ID>() {
			protected boolean enter(Tree<E, ID> tree) {
				if (element.equals(tree.getElement())) {
					path.clear();
					for (Tree<E, ID> t : stack) {
						path.add(t);
					}
					path.add(tree);
					result.add(new ArrayList<>(path));
				}
				return true;
			}
		}.scan(this);
		return result;
	}

	public List<Tree<E, ID>> findFirstElement(final E element) {
		final List<Tree<E, ID>> path = new ArrayList<>();
		new Tree.Scanner<E, ID>() {
			protected boolean enter(Tree<E, ID> tree) {
				if (!path.isEmpty()) {
					return false;
				}
				if (element.equals(tree.getElement())) {
					path.clear();
					for (Tree<E, ID> t : stack) {
						path.add(t);
					}
					path.add(tree);
					return false;
				}
				return true;
			}
		}.scan(this);
		return path;
	}

	public List<Tree<E, ID>> find(final ID id) {
		final List<Tree<E, ID>> path = new ArrayList<>();
		new Tree.Scanner<E, ID>() {
			protected boolean enter(Tree<E, ID> tree) {
				if (!path.isEmpty()) {
					return false;
				}
				if (id.equals(tree.getID())) {
					for (Tree<E, ID> t : stack) {
						path.add(t);
					}
					path.add(tree);
					return false;
				}
				return true;
			}
		}.scan(this);
		return path;
	}

	public static List<String> toStringPath(List<Tree<String, File>> path) {
		return path.stream().map(t -> t.getElement()).collect(Collectors.toList());
	}

}
