package org.jsweet.test.transpiler.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.List;

import org.jsweet.test.transpiler.AbstractTest;
import org.jsweet.test.transpiler.TestTranspilationHandler;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetOptions;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.Util;
import org.junit.Before;
import org.junit.Test;

import com.sun.source.doctree.ReturnTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;

import source.syntax.ExecutionPaths;

@SuppressWarnings("unchecked")
public class ExecutionPathTest extends AbstractTest {

	private ClassTree executionPathClassDecl;
	
	private Util util;

	@Before
	public void setUp() throws Throwable {
		JSweetTranspiler transpiler = new JSweetTranspiler(new JSweetFactory());
		TestTranspilationHandler testTranspilationHandler = new TestTranspilationHandler();
		List<File> javaFiles = asList(getSourceFile(ExecutionPaths.class).getJavaFile());
		System.out.println("java files = " + javaFiles);

		List<CompilationUnitTree> compilUnits = transpiler.setupCompiler(javaFiles,
				new ErrorCountTranspilationHandler(testTranspilationHandler));
		util = transpiler.getContext().util;
		List<ClassTree> typeDeclarations = util.findTypeDeclarationsInCompilationUnits(compilUnits);
		System.out.println("types=" + typeDeclarations.size());
		executionPathClassDecl = typeDeclarations.get(0);
	}

	@Test
	public void ifElseReturns() throws Throwable {
		MethodTree methodDeclaration = util.findFirstMethodDeclaration(executionPathClassDecl, "ifElseReturns");
		
		List<List<Tree>> executionPaths = util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);
		
		assertPaths(executionPaths, //
				executionPath(IfTree.class, BlockTree.class, ReturnTree.class), //
				executionPath(IfTree.class, BlockTree.class, ReturnTree.class));
	}
	
	@Test(expected = RuntimeException.class)
	public void testPerfsIfs() throws Throwable {
		MethodTree methodDeclaration = util.findFirstMethodDeclaration(executionPathClassDecl, "testPerfsIfs");

		List<List<Tree>> executionPaths = util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);
	}

	@Test
	public void ifReturnInElse() throws Throwable {
		MethodTree methodDeclaration = util.findFirstMethodDeclaration(executionPathClassDecl, "ifReturnInElse");

		List<List<Tree>> executionPaths = util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(IfTree.class, BlockTree.class, ExpressionStatementTree.class, ExpressionStatementTree.class,
						ReturnTree.class), //
				executionPath(IfTree.class, BlockTree.class, ExpressionStatementTree.class, ReturnTree.class));
	}

	@Test
	public void ifElseNoReturns() throws Throwable {
		MethodTree methodDeclaration = util.findFirstMethodDeclaration(executionPathClassDecl, "ifElseNoReturns");

		List<List<Tree>> executionPaths = util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(IfTree.class, BlockTree.class, ExpressionStatementTree.class, ExpressionStatementTree.class,
						ReturnTree.class), //
				executionPath(IfTree.class, BlockTree.class, ExpressionStatementTree.class, ExpressionStatementTree.class,
						ReturnTree.class));
	}

	@Test
	public void forIfElse() throws Throwable {
		MethodTree methodDeclaration = util.findFirstMethodDeclaration(executionPathClassDecl, "forIfElse");

		List<List<Tree>> executionPaths = util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(ForLoopTree.class, BlockTree.class, IfTree.class, BlockTree.class, ExpressionStatementTree.class,
						ExpressionStatementTree.class, ReturnTree.class), //
				executionPath(ForLoopTree.class, BlockTree.class, IfTree.class, BlockTree.class, ExpressionStatementTree.class,
						ReturnTree.class));
	}

	@Test
	public void switchWithTryCatch() throws Throwable {
		MethodTree methodDeclaration = util.findFirstMethodDeclaration(executionPathClassDecl, "switchWithTryCatch");

		List<List<Tree>> executionPaths = util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(SwitchTree.class, CaseTree.class, ExpressionStatementTree.class, ReturnTree.class), //
				executionPath(SwitchTree.class, CaseTree.class, ExpressionStatementTree.class, BreakTree.class,
						ExpressionStatementTree.class, ReturnTree.class), //
				executionPath(SwitchTree.class, CaseTree.class, ExpressionStatementTree.class, BreakTree.class,
						ExpressionStatementTree.class, ReturnTree.class), //
				executionPath(SwitchTree.class, CaseTree.class, TryTree.class, BlockTree.class, ExpressionStatementTree.class,
						BlockTree.class, ExpressionStatementTree.class, ReturnTree.class), //
				executionPath(SwitchTree.class, CaseTree.class, TryTree.class, BlockTree.class, ExpressionStatementTree.class,
						BreakTree.class, ExpressionStatementTree.class, ReturnTree.class));
	}

	@Test
	public void tryWithCatchesAndFinally() throws Throwable {
		MethodTree methodDeclaration = util.findFirstMethodDeclaration(executionPathClassDecl,
				"tryWithCatchesAndFinally");

		List<List<Tree>> executionPaths = util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(TryTree.class, BlockTree.class, ExpressionStatementTree.class, BlockTree.class,
						ExpressionStatementTree.class, ReturnTree.class), //
				executionPath(TryTree.class, BlockTree.class, ExpressionStatementTree.class, BlockTree.class,
						ExpressionStatementTree.class, ReturnTree.class), //
				executionPath(TryTree.class, BlockTree.class, ExpressionStatementTree.class, BlockTree.class,
						ExpressionStatementTree.class, ReturnTree.class));
	}

	@Test
	public void tryFinally() throws Throwable {
		MethodTree methodDeclaration = util.findFirstMethodDeclaration(executionPathClassDecl, "tryFinally");

		List<List<Tree>> executionPaths = util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(TryTree.class, BlockTree.class, BlockTree.class, ExpressionStatementTree.class, ReturnTree.class));
	}

	@Test
	public void ifElseDeepWithReturnsForSomePaths() throws Throwable {
		MethodTree methodDeclaration = util.findFirstMethodDeclaration(executionPathClassDecl,
				"ifElseDeepWithReturnsForSomePaths");

		List<List<Tree>> executionPaths = util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, // if
				executionPath(IfTree.class, BlockTree.class, ExpressionStatementTree.class, ExpressionStatementTree.class,
						ReturnTree.class), // else
				executionPath(IfTree.class, BlockTree.class, ExpressionStatementTree.class, ExpressionStatementTree.class,
						ReturnTree.class), // elseif
				executionPath(IfTree.class, IfTree.class, BlockTree.class, ExpressionStatementTree.class, IfTree.class,
						BlockTree.class, ExpressionStatementTree.class, ReturnTree.class), //
				executionPath(IfTree.class, IfTree.class, BlockTree.class, ExpressionStatementTree.class, IfTree.class,
						IfTree.class, BlockTree.class, ExpressionStatementTree.class, ExpressionStatementTree.class,
						ExpressionStatementTree.class, ReturnTree.class), //
				executionPath(IfTree.class, IfTree.class, BlockTree.class, ExpressionStatementTree.class, IfTree.class,
						IfTree.class, ExpressionStatementTree.class, ExpressionStatementTree.class, ReturnTree.class));
	}

	private void assertPaths(List<List<Tree>> executionPaths, List<Class<?>>... expectedPaths) {
		assertEquals("expected paths count differs", expectedPaths.length, executionPaths.size());

		for (List<Class<?>> expectedPath : expectedPaths) {
			boolean found = false;
			for (List<Tree> path : executionPaths) {
				if (doesPathMatch(path, expectedPath)) {
					found = true;
					break;
				}
			}

			assertTrue("execution path not found: "
					+ expectedPath.stream().map(clazz -> clazz.getSimpleName()).collect(toList()) + " \npaths="
					+ pathsToString(executionPaths), found);
		}

	}

	private String pathsToString(List<List<Tree>> executionPaths) {
		String description = "";
		for (List<Tree> path : executionPaths) {
			description += "* ";
			for (Tree node : path) {
				description += node.getClass().getSimpleName();
				description += ", ";
			}
			description += "\n";
		}

		return description;
	}

	private boolean doesPathMatch(List<Tree> path, List<Class<?>> expectedPath) {
		if (path.size() != expectedPath.size()) {
			return false;
		}

		for (int i = 0; i < path.size(); i++) {
			Tree currentNode = path.get(i);
			Class<?> expectedNodeClass = expectedPath.get(i);

			if (!expectedNodeClass.isAssignableFrom(currentNode.getClass())) {
				return false;
			}
		}

		return true;
	}

	private List<Class<?>> executionPath(Class<?>... nodeClasses) {
		return asList(nodeClasses);
	}

	private void printPaths(MethodTree methodDeclaration, List<List<Tree>> executionPaths) {
		int i = 0;
		for (List<Tree> executionPath : executionPaths) {
			System.out
					.println("************ " + methodDeclaration.getName().toString() + ": " + (++i) + " ************");
			for (Tree instruction : executionPath) {
				System.out.println(instruction);
			}
		}
	}
}
