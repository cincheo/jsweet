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

import static java.util.Arrays.asList;

import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner9;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.Tool;
import javax.tools.ToolProvider;

import com.google.common.collect.Iterables;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.PackageTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

/**
 * This class defines a directed graph collection type, that is to say a set of
 * elements (aka nodes) linked together with directed edges. It is particularly
 * useful for the <i>topological sort</i>, which is implemented as depicted in
 * Wikipedia and this <a href=
 * "http://stackoverflow.com/questions/2739392/sample-directed-graph-and-topological-sort-code"
 * >StackOverflow thread</a>
 * 
 * <p>
 * Example of use:
 * 
 * <pre>
 * Graph&lt;Integer&gt; g = new Graph&lt;Integer&gt;();
 * g.add(7, 5, 3, 11, 8, 2, 9, 10);
 * g.buildEdges(new Comparator&lt;Integer&gt;() {
 * 	&#064;Override
 * 	public int compare(Integer o1, Integer o2) {
 * 		if (o1 == 5 &amp;&amp; o2 == 3) {
 * 			return 1;
 * 		}
 * 		return 0;
 * 	}
 * });
 * System.out.println(g.topologicalSort());
 * </pre>
 * 
 * <p>
 * Prints out: <code>[7, 3, 11, 8, 2, 9, 10, 5]</code> (5 is pushed to the end
 * because it is after 3). Note that the elements that are already in the right
 * order remain unchanged.
 * 
 * <p>
 * The sorting method is deterministic, i.e. it always gives the same result and
 * tries to minimize necessary changes.
 * 
 * @author Renaud Pawlak
 * 
 * @param <T> the types of the nodes in the graph
 */
public class DirectedGraph<T> implements Collection<T> {

	private Map<T, Node<T>> nodes = new LinkedHashMap<T, Node<T>>();

	/**
	 * Constructs an empty graph collection.
	 */
	public DirectedGraph() {
	}

	/**
	 * Adds a set of elements to the nodes of this graph.
	 */
	@Override
	public boolean addAll(Collection<? extends T> elements) {
		for (T element : elements) {
			add(element);
		}
		return true;
	}

	/**
	 * Adds an element to the nodes.
	 */
	@Override
	public boolean add(T element) {
		if (nodes.containsKey(element)) {
			return false;
		}
		Node<T> node = new Node<T>(this, element);
		nodes.put(element, node);
		return true;
	}

	/**
	 * Clears all the nodes of this graph.
	 */
	@Override
	public void clear() {
		nodes.clear();
	}

	/**
	 * Tells if this graph contains the given object as a node.
	 */
	@Override
	public boolean contains(Object o) {
		return nodes.containsKey(o);
	}

	/**
	 * Tells if this graph contains the given collection as a node.
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Equals two graphs.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DirectedGraph)) {
			return false;
		} else {
			return nodes.equals(((DirectedGraph<?>) obj).nodes);
		}
	}

	@Override
	public int hashCode() {
		return nodes.hashCode();
	}

	/**
	 * Returns true if this graph has no nodes.
	 */
	@Override
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	/**
	 * Return an iterator to iterate on graph nodes.
	 */
	@Override
	public Iterator<T> iterator() {
		return nodes.keySet().iterator();
	}

	/**
	 * Removes an object from the graph nodes (if exists).
	 */
	@Override
	public boolean remove(Object o) {
		return nodes.remove(o) != null;
	}

	/**
	 * Removes all the objects from the graph nodes (if exists).
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object o : c) {
			remove(o);
		}
		return true;
	}

	/**
	 * Keeps only the elements of the given collection in the graph.
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean b = false;
		for (T element : nodes.keySet()) {
			if (!c.contains(element)) {
				remove(element);
				b = true;
			}
		}
		return b;
	}

	/**
	 * Returns the nodes count in this graph.
	 */
	@Override
	public int size() {
		return nodes.size();
	}

	/**
	 * Returns the graph's nodes as an array (not sorted).
	 */
	@Override
	public Object[] toArray() {
		return nodes.keySet().toArray();
	}

	/**
	 * Returns the graph's nodes as a well-typed array (not sorted).
	 */
	@Override
	public <U> U[] toArray(U[] a) {
		return toArray(a);
	}

	/**
	 * Add a set of elements to the nodes of this graph.
	 */
	@SuppressWarnings("unchecked")
	public void add(T... elements) {
		for (T element : elements) {
			add(element);
		}
	}

	/**
	 * Add an edge between the given elements (nodes).
	 * 
	 * @param sourceElement      the source element/node
	 * @param destinationElement the destination element/node
	 */
	public void addEdge(T sourceElement, T destinationElement) {
		if (sourceElement.equals(destinationElement)) {
			return;
		}
		if (hasEdge(sourceElement, destinationElement)) {
			return;
		}
		nodes.get(sourceElement).addEdge(destinationElement);
	}

	/**
	 * Automatically builds the edges between all the nodes of the graph by using
	 * the given comparator. If the comparator returns 0, then no edge is
	 * constructor between the compared nodes.
	 * 
	 * @param nodeComparator a comparator which is used to build the edges
	 */
	public <U extends T> void buildEdges(Comparator<U> nodeComparator) {
		for (T e1 : nodes.keySet()) {
			for (T e2 : nodes.keySet()) {
				@SuppressWarnings("unchecked")
				int i = nodeComparator.compare((U) e1, (U) e2);
				if (i < 0) {
					addEdge(e1, e2);
				}
				if (i > 0) {
					addEdge(e2, e1);
				}
			}
		}
	}

	/**
	 * Adds some edges form the source elements to the given destination elements.
	 * 
	 * @param sourceElement       the source of the edges
	 * @param destinationElements the destination elements of the edges
	 */
	@SuppressWarnings("unchecked")
	public void addEdges(T sourceElement, T... destinationElements) {
		nodes.get(sourceElement).addEdges(destinationElements);
	}

	/**
	 * Tells if this graph contains an edge between the given source and the
	 * destination elements/nodes.
	 * 
	 * @param sourceElement      the source node
	 * @param destinationElement the destination node
	 * @return true if an edge is found, false otherwise
	 */
	public boolean hasEdge(T sourceElement, T destinationElement) {
		if (nodes.get(sourceElement) == null) {
			return false;
		}

		for (Edge<T> edge : nodes.get(sourceElement).outEdges) {
			if (edge.to.element == destinationElement) {
				return true;
			}
		}
		return false;
	}

	public List<T> getDestinationElements(T sourceElement) {
		List<T> l = new ArrayList<T>();
		if (nodes.get(sourceElement) == null) {
			return null;
		}
		for (Edge<T> edge : nodes.get(sourceElement).outEdges) {
			l.add(edge.to.element);
		}
		return l;
	}

	public List<T> getSourceElements(T destinationElement) {
		if (nodes.get(destinationElement) == null) {
			return null;
		}

		List<T> l = new ArrayList<T>();
		for (Edge<T> edge : nodes.get(destinationElement).inEdges) {
			l.add(edge.from.element);
		}
		return l;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("[");
		for (T element : nodes.keySet()) {
			s.append(element.toString());
			s.append("->");
			s.append(getDestinationElements(element));
			s.append(",");
		}
		if (!nodes.isEmpty()) {
			s.deleteCharAt(s.length() - 1);
		}
		s.append("]");
		return s.toString();
	}

	/**
	 * An object representing the nodes of the graph.
	 * 
	 * <p>
	 * It stores elements and remembers edges.
	 * 
	 * @author Renaud Pawlak
	 *
	 * @param <T> the type of object hold in the graph
	 */
	public static class Node<T> {
		private DirectedGraph<T> graph;
		/**
		 * The actual element (data) hold by this node.
		 */
		public final T element;
		/**
		 * The edges entering this node.
		 */
		public final LinkedHashSet<Edge<T>> inEdges;
		/**
		 * Information used for sorting only.
		 */
		public final LinkedHashSet<Edge<T>> usedInEdges;
		/**
		 * The edges leaving from this node.
		 */
		public final LinkedHashSet<Edge<T>> outEdges;
		/**
		 * Information used for sorting only.
		 */
		public final LinkedHashSet<Edge<T>> usedOutEdges;

		/**
		 * Creates a new node for the given graph and holding the given element.
		 * 
		 * @param graph   the graph this node belongs to
		 * @param element the element hold by this node
		 */
		public Node(DirectedGraph<T> graph, T element) {
			this.graph = graph;
			this.element = element;
			inEdges = new LinkedHashSet<Edge<T>>();
			usedInEdges = new LinkedHashSet<Edge<T>>();
			outEdges = new LinkedHashSet<Edge<T>>();
			usedOutEdges = new LinkedHashSet<Edge<T>>();
		}

		/**
		 * Adds an edge starting from this node and going to a node holding the given
		 * element.
		 */
		public void addEdge(T destinationElement) {
			Node<T> node = graph.nodes.get(destinationElement);
			if (node == null) {
				graph.add(destinationElement);
				node = graph.nodes.get(destinationElement);
			}
			Edge<T> e = new Edge<T>(this, node);
			outEdges.add(e);
			node.inEdges.add(e);
		}

		/**
		 * Adds edges starting from this node and going to nodes holding the given
		 * elements.
		 */
		@SuppressWarnings("unchecked")
		public void addEdges(T... destinationElements) {
			for (T destinationElement : destinationElements) {
				addEdge(destinationElement);
			}
		}

		/**
		 * Used for sorting only.
		 */
		public void useInEdge(Edge<T> edge) {
			if (inEdges.remove(edge)) {
				usedInEdges.add(edge);
			}
		}

		/**
		 * Used for sorting only.
		 */
		public void useOutEdge(Edge<T> edge) {
			if (outEdges.remove(edge)) {
				usedOutEdges.add(edge);
			}
		}

		/**
		 * Clears sorting info.
		 */
		public void resetEdges() {
			inEdges.addAll(usedInEdges);
			usedInEdges.clear();
			outEdges.addAll(usedOutEdges);
			usedOutEdges.clear();
		}

		@Override
		public String toString() {
			return "Node[" + element + "]";
		}
	}

	/**
	 * A generic object representing an edge in the graph.
	 * 
	 * @author Renaud Pawlak
	 *
	 * @param <T> the type of the objects being stored in the graph
	 */
	public static class Edge<T> {
		/**
		 * The node this edge starts from
		 */
		public final Node<T> from;
		/**
		 * The node this edge goes to
		 */
		public final Node<T> to;

		/**
		 * Creates a new edge.
		 * 
		 * @param from the start node
		 * @param to   the end node
		 */
		public Edge(Node<T> from, Node<T> to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public String toString() {
			return "Edge[" + from + "->" + to + "]";
		}

		@Override
		public boolean equals(Object obj) {
			@SuppressWarnings("unchecked")
			Edge<T> e = (Edge<T>) obj;
			return e.from == from && e.to == to;
		}
	}

	private List<T> toElements(List<Node<T>> nodes) {
		List<T> elements = new ArrayList<T>();
		for (Node<T> node : nodes) {
			elements.add(node.element);
		}
		return elements;
	}

	/**
	 * Sorts this graph using a topological sort algorithm given in this <a href=
	 * "http://stackoverflow.com/questions/2739392/sample-directed-graph-and-topological-sort-code"
	 * >StackOverflow thread</a>.
	 * 
	 * @return the list of nodes, sorted according to the topological sort
	 * @throws CycleFoundException thrown if a cycle is found in the graph (in that
	 *                             case no topological sort is possible)
	 */
	public List<T> topologicalSort(Consumer<Node<T>> cycleHandler) {
		List<Node<T>> allNodes = new ArrayList<Node<T>>(nodes.values());
		// L <- Empty list that will contain the sorted elements
		ArrayList<Node<T>> L = new ArrayList<Node<T>>();

		// S <- Set of all nodes with no incoming edges
		LinkedHashSet<Node<T>> S = new LinkedHashSet<Node<T>>();
		for (Node<T> n : allNodes) {
			if (n.inEdges.size() == 0) {
				S.add(n);
			}
		}

		// while S is non-empty do
		while (!S.isEmpty()) {
			// remove a node n from S
			Node<T> n = S.iterator().next();
			S.remove(n);

			// insert n into L
			L.add(n);

			// for each node m with an edge e from n to m do
			for (Iterator<Edge<T>> it = new ArrayList<>(n.outEdges).iterator(); it.hasNext();) {
				// remove edge e from the graph
				Edge<T> e = it.next();
				Node<T> m = e.to;
				n.useOutEdge(e); // Remove edge from n
				m.useInEdge(e); // Remove edge from m

				// if m has no other incoming edges then insert m into S
				if (m.inEdges.isEmpty()) {
					S.add(m);
				}
			}
		}
		// Check to see if all edges are removed
		for (Node<T> n : allNodes) {
			if (!n.inEdges.isEmpty()) {
				if (cycleHandler != null) {
					cycleHandler.accept(n);
				}
			}
		}
		for (Node<T> n : allNodes) {
			n.resetEdges();
		}
		return toElements(L);
	}

	/**
	 * Dumps the found cycles to System.out.
	 * 
	 * @param nodes    the nodes in which to look for cycles
	 * @param toString the element's toString function
	 */
	public static <T> void dumpCycles(List<Node<T>> nodes, Function<T, String> toString) {
		for (Node<T> node : nodes) {
			Stack<Node<T>> path = new Stack<Node<T>>();
			path.add(node);
			dumpCycles(nodes, path, toString);
		}
	}

	private static <T> void dumpCycles(List<Node<T>> nodes, Stack<Node<T>> path, Function<T, String> toString) {
		path.peek().outEdges.stream().map(e -> e.to).forEach(node -> {
			if (nodes.contains(node)) {
				if (path.contains(node)) {
					System.out.println(
							"cycle: " + path.stream().map(n -> toString.apply(n.element)).collect(Collectors.toList()));
				} else {
					path.push(node);
					dumpCycles(nodes, path, toString);
					path.pop();
				}
			}
		});
		;
	}

	static class Scanner1 extends ElementScanner9<Void, Void> {
		private int numberOfClasses;
		private int numberOfMethods;
		private int numberOfFields;

		public Void visitType(final TypeElement type, final Void p) {
			++numberOfClasses;

			return super.visitType(type, p);
		}

		public Void visitExecutable(final ExecutableElement executable, final Void p) {
			++numberOfMethods;
			return super.visitExecutable(executable, p);
		}

		public Void visitVariable(final VariableElement variable, final Void p) {
			if (variable.getEnclosingElement().getKind() == ElementKind.CLASS) {
				++numberOfFields;
			}

			return super.visitVariable(variable, p);
		}
	}

	@SupportedSourceVersion(SourceVersion.RELEASE_11)
	@SupportedAnnotationTypes("*")
	static class Processor1 extends AbstractProcessor {
		private final Scanner1 scanner;

		public Processor1(final Scanner1 scanner) {
			this.scanner = scanner;
		}

		public boolean process(final Set<? extends TypeElement> types, final RoundEnvironment environment) {

			if (!environment.processingOver()) {
				for (final Element element : environment.getRootElements()) {
					scanner.scan(element);
				}
			}

			return false;
		}
	}

	static class Scanner2 extends TreePathScanner<Object, Trees> {
		private int count;
		private ProcessingEnvironment processingEnvironment;

		public Scanner2(ProcessingEnvironment processingEnvironment) {
			this.processingEnvironment = processingEnvironment;
		}

		Types types() {
			return processingEnvironment.getTypeUtils();
		}

		Elements elements() {
			return processingEnvironment.getElementUtils();
		}

		@Override
		public Object visitMethodInvocation(MethodInvocationTree node, Trees p) {

			++count;

			System.out.println(node.getKind().asInterface());

			return super.visitMethodInvocation(node, p);
		}

		@Override
		public Object visitUnary(UnaryTree node, Trees p) {
			
			ExpressionTree e = node.getExpression();
			TreePath tp =  p.getPath(getCurrentPath().getCompilationUnit(), e);
			Element el = p.getElement(tp);
			TreePath tp2 = p.getPath(el);
			TreePath tp3 =  p.getPath(tp2.getCompilationUnit(), e);
			
			
			return super.visitUnary(node, p);
		}
		
		@Override
		public Object visitMemberSelect(MemberSelectTree node, Trees p) {

			System.out.println("== visitMemberSelect ==");

			System.out.println(node.getKind().asInterface());
			System.out.println(node.getExpression());
			System.out.println(node.getIdentifier());
			System.out.println(p.getTypeMirror(getCurrentPath()));
			System.out.println(p.getTypeMirror(getCurrentPath()) != null);

			System.out.println("via element ==");

			Element element = p.getElement(getCurrentPath());
			elements().printElements(new OutputStreamWriter(System.out), element);

			TypeMirror typeMirror = element.asType();
			System.out.println(typeMirror);
			System.out.println(types().asElement(typeMirror));
			System.out.println(typeMirror.getClass());
			if (typeMirror instanceof DeclaredType) {
				System.out.println("decl");
				System.out.println(((DeclaredType) typeMirror).asElement());
			} else if (typeMirror instanceof PrimitiveType) {
				System.out.println("prim");
				System.out.println(((PrimitiveType) typeMirror).toString());
			}
//			com.sun.tools.javac.code.Types t;
			// System.out.println(elements().asElement(typeMirror));

//			System.out.println(elements().element.asType());

//			TypeElement tt = elements().getTypeElement(Throwable.class.getName());
//			System.out.println(tt);
			System.out.println("== END visitMemberSelect END ==");

			return super.visitMemberSelect(node, p);
		}

		@Override
		public Object visitEnhancedForLoop(EnhancedForLoopTree node, Trees p) {
			return super.visitEnhancedForLoop(node, p);
		}

		@Override
		public Object visitMemberReference(MemberReferenceTree node, Trees p) {
			System.out.println("visitMemberReference");
			System.out.println(node.getKind().asInterface());
			return super.visitMemberReference(node, p);
		}

		@Override
		public Object visitAssignment(AssignmentTree node, Trees p) {
			return super.visitAssignment(node, p);
		}

		@Override
		public Object visitPackage(PackageTree node, Trees p) {
			System.out.println("visitPackage");
			return super.visitPackage(node, p);
		}

		@Override
		public Object visitCompoundAssignment(CompoundAssignmentTree node, Trees p) {
			return super.visitCompoundAssignment(node, p);
		}

		@Override
		public Object visitImport(ImportTree node, Trees p) {
			System.out.println("visitImport");
			return super.visitImport(node, p);
		}

		public int getCount() {
			return count;
		}
	}

	@SupportedSourceVersion(SourceVersion.RELEASE_11)
	@SupportedAnnotationTypes("*")
	static class Processor2 extends AbstractProcessor {
		private Trees trees;
		Scanner2 scanner;

		@Override
		public synchronized void init(final ProcessingEnvironment processingEnvironment) {
			super.init(processingEnvironment);
			trees = Trees.instance(processingEnvironment);
			scanner = new Scanner2(processingEnvironment);
		}

		public boolean process(final Set<? extends TypeElement> types, final RoundEnvironment environment) {

			if (!environment.processingOver()) {
				for (final Element element : environment.getRootElements()) {
					scanner.scan(trees.getPath(element), trees);
				}
			}

			return true;
		}
	}

	/**
	 * Just for testing.
	 */
	public static void main(String[] args) throws Exception {

		for (Tool tool : ServiceLoader.loadInstalled(Tool.class)) {
			System.out.println("Found tool: " + tool);
		}
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		for (final SourceVersion version : compiler.getSourceVersions()) {
			System.out.println("javac source version: " + version);
		}

//		System.out.println(compiler.isSupportedOption("-cp"));
//		System.out.println(compiler.isSupportedOption("-Xlint"));
//		System.out.println(compiler.isSupportedOption("-bootclasspath"));
//		System.out.println(compiler.isSupportedOption("-encoding"));

		File f = new File("src/main/resources/Test.java");
		File f2 = new File("src/main/resources/Test2.java");

		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.getDefault(),
				Charset.forName("UTF-8"))) {
			final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
			Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjectsFromFiles(asList(f, f2));
			System.out.println("f=" + f);
			System.out.println("f2=" + f2);
			System.out.println("fileObjects=" + sources);

			final Scanner1 scanner1 = new Scanner1();
			final Processor1 processor1 = new Processor1(scanner1);
			final Processor2 processor2 = new Processor2();

			final CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, sources);
			JavacTask javacTask = (JavacTask) task;
			javacTask.setProcessors(asList(processor1, processor2));
			Iterable<? extends CompilationUnitTree> compilUnits = javacTask.parse();
			javacTask.analyze();

//			fileManager.inferModuleName(location)
//			javacTask.getElements().getModuleElement("test").

			System.out.println("compilUnits=" + Iterables.size(compilUnits));

			System.out.format("Classes %d, methods/constructors %d, fields %d\n", scanner1.numberOfClasses,
					scanner1.numberOfMethods, scanner1.numberOfFields);

			System.out.format("meth count: %d \n", processor2.scanner.getCount());

			for (final Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				System.out.format("DIAG: %s, line %d in %s\n", diagnostic.getMessage(null), diagnostic.getLineNumber(),
						diagnostic.getSource().getName());
			}
		}

		if (true) {
			System.exit(0);
		}

		DirectedGraph<Integer> g = new DirectedGraph<Integer>();
		g.add(7, 5, 3, 11, 8, 2, 9, 10);
		System.out.println(g.nodes.values());
		System.out.println(g.nodes.keySet());
		g.buildEdges(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 == 5 && o2 == 3) {
					return 1;
				}
				return 0;
			}
		});
		System.out.println(g.topologicalSort(null));
	}
}
