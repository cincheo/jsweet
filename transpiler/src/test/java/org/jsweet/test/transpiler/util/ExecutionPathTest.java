package org.jsweet.test.transpiler.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.jsweet.test.transpiler.AbstractTest;
import org.jsweet.test.transpiler.TestTranspilationHandler;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.Util;
import org.junit.Before;
import org.junit.Test;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCBreak;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCSwitch;
import com.sun.tools.javac.tree.JCTree.JCTry;

import source.syntax.ExecutionPaths;

@SuppressWarnings("unchecked")
public class ExecutionPathTest extends AbstractTest {

	private JCClassDecl executionPathClassDecl;

	@Before
	public void setUp() throws Throwable {
		JSweetTranspiler transpiler = new JSweetTranspiler(new JSweetFactory());
		TestTranspilationHandler testTranspilationHandler = new TestTranspilationHandler();
		List<File> javaFiles = asList(getSourceFile(ExecutionPaths.class).getJavaFile());
		System.out.println("java files = " + javaFiles);

		List<JCCompilationUnit> compilUnits = transpiler.setupCompiler(javaFiles,
				new ErrorCountTranspilationHandler(testTranspilationHandler));
		List<JCClassDecl> typeDeclarations = Util.findTypeDeclarationsInCompilationUnits(compilUnits);
		System.out.println("types=" + typeDeclarations.size());
		executionPathClassDecl = typeDeclarations.get(0);
	}

	@Test
	public void ifElseReturns() throws Throwable {
		JCMethodDecl methodDeclaration = Util.findFirstMethodDeclaration(executionPathClassDecl, "ifElseReturns");
		
		List<List<JCTree>> executionPaths = Util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);
		
		assertPaths(executionPaths, //
				executionPath(JCIf.class, JCBlock.class, JCReturn.class), //
				executionPath(JCIf.class, JCBlock.class, JCReturn.class));
	}
	
	@Test(expected = RuntimeException.class)
	public void testPerfsIfs() throws Throwable {
		JCMethodDecl methodDeclaration = Util.findFirstMethodDeclaration(executionPathClassDecl, "testPerfsIfs");

		List<List<JCTree>> executionPaths = Util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);
	}

	@Test
	public void ifReturnInElse() throws Throwable {
		JCMethodDecl methodDeclaration = Util.findFirstMethodDeclaration(executionPathClassDecl, "ifReturnInElse");

		List<List<JCTree>> executionPaths = Util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(JCIf.class, JCBlock.class, JCExpressionStatement.class, JCExpressionStatement.class,
						JCReturn.class), //
				executionPath(JCIf.class, JCBlock.class, JCExpressionStatement.class, JCReturn.class));
	}

	@Test
	public void ifElseNoReturns() throws Throwable {
		JCMethodDecl methodDeclaration = Util.findFirstMethodDeclaration(executionPathClassDecl, "ifElseNoReturns");

		List<List<JCTree>> executionPaths = Util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(JCIf.class, JCBlock.class, JCExpressionStatement.class, JCExpressionStatement.class,
						JCReturn.class), //
				executionPath(JCIf.class, JCBlock.class, JCExpressionStatement.class, JCExpressionStatement.class,
						JCReturn.class));
	}

	@Test
	public void forIfElse() throws Throwable {
		JCMethodDecl methodDeclaration = Util.findFirstMethodDeclaration(executionPathClassDecl, "forIfElse");

		List<List<JCTree>> executionPaths = Util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(JCForLoop.class, JCBlock.class, JCIf.class, JCBlock.class, JCExpressionStatement.class,
						JCExpressionStatement.class, JCReturn.class), //
				executionPath(JCForLoop.class, JCBlock.class, JCIf.class, JCBlock.class, JCExpressionStatement.class,
						JCReturn.class));
	}

	@Test
	public void switchWithTryCatch() throws Throwable {
		JCMethodDecl methodDeclaration = Util.findFirstMethodDeclaration(executionPathClassDecl, "switchWithTryCatch");

		List<List<JCTree>> executionPaths = Util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(JCSwitch.class, JCCase.class, JCExpressionStatement.class, JCReturn.class), //
				executionPath(JCSwitch.class, JCCase.class, JCExpressionStatement.class, JCBreak.class,
						JCExpressionStatement.class, JCReturn.class), //
				executionPath(JCSwitch.class, JCCase.class, JCExpressionStatement.class, JCBreak.class,
						JCExpressionStatement.class, JCReturn.class), //
				executionPath(JCSwitch.class, JCCase.class, JCTry.class, JCBlock.class, JCExpressionStatement.class,
						JCBlock.class, JCExpressionStatement.class, JCReturn.class), //
				executionPath(JCSwitch.class, JCCase.class, JCTry.class, JCBlock.class, JCExpressionStatement.class,
						JCBreak.class, JCExpressionStatement.class, JCReturn.class));
	}

	@Test
	public void tryWithCatchesAndFinally() throws Throwable {
		JCMethodDecl methodDeclaration = Util.findFirstMethodDeclaration(executionPathClassDecl,
				"tryWithCatchesAndFinally");

		List<List<JCTree>> executionPaths = Util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(JCTry.class, JCBlock.class, JCExpressionStatement.class, JCBlock.class,
						JCExpressionStatement.class, JCReturn.class), //
				executionPath(JCTry.class, JCBlock.class, JCExpressionStatement.class, JCBlock.class,
						JCExpressionStatement.class, JCReturn.class), //
				executionPath(JCTry.class, JCBlock.class, JCExpressionStatement.class, JCBlock.class,
						JCExpressionStatement.class, JCReturn.class));
	}

	@Test
	public void tryFinally() throws Throwable {
		JCMethodDecl methodDeclaration = Util.findFirstMethodDeclaration(executionPathClassDecl, "tryFinally");

		List<List<JCTree>> executionPaths = Util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, //
				executionPath(JCTry.class, JCBlock.class, JCBlock.class, JCExpressionStatement.class, JCReturn.class));
	}

	@Test
	public void ifElseDeepWithReturnsForSomePaths() throws Throwable {
		JCMethodDecl methodDeclaration = Util.findFirstMethodDeclaration(executionPathClassDecl,
				"ifElseDeepWithReturnsForSomePaths");

		List<List<JCTree>> executionPaths = Util.getExecutionPaths(methodDeclaration);
		printPaths(methodDeclaration, executionPaths);

		assertPaths(executionPaths, // if
				executionPath(JCIf.class, JCBlock.class, JCExpressionStatement.class, JCExpressionStatement.class,
						JCReturn.class), // else
				executionPath(JCIf.class, JCBlock.class, JCExpressionStatement.class, JCExpressionStatement.class,
						JCReturn.class), // elseif
				executionPath(JCIf.class, JCIf.class, JCBlock.class, JCExpressionStatement.class, JCIf.class,
						JCBlock.class, JCExpressionStatement.class, JCReturn.class), //
				executionPath(JCIf.class, JCIf.class, JCBlock.class, JCExpressionStatement.class, JCIf.class,
						JCIf.class, JCBlock.class, JCExpressionStatement.class, JCExpressionStatement.class,
						JCExpressionStatement.class, JCReturn.class), //
				executionPath(JCIf.class, JCIf.class, JCBlock.class, JCExpressionStatement.class, JCIf.class,
						JCIf.class, JCExpressionStatement.class, JCExpressionStatement.class, JCReturn.class));
	}

	private void assertPaths(List<List<JCTree>> executionPaths, List<Class<?>>... expectedPaths) {
		assertEquals("expected paths count differs", expectedPaths.length, executionPaths.size());

		for (List<Class<?>> expectedPath : expectedPaths) {
			boolean found = false;
			for (List<JCTree> path : executionPaths) {
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

	private String pathsToString(List<List<JCTree>> executionPaths) {
		String description = "";
		for (List<JCTree> path : executionPaths) {
			description += "* ";
			for (JCTree node : path) {
				description += node.getClass().getSimpleName();
				description += ", ";
			}
			description += "\n";
		}

		return description;
	}

	private boolean doesPathMatch(List<JCTree> path, List<Class<?>> expectedPath) {
		if (path.size() != expectedPath.size()) {
			return false;
		}

		for (int i = 0; i < path.size(); i++) {
			JCTree currentNode = path.get(i);
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

	private void printPaths(JCMethodDecl methodDeclaration, List<List<JCTree>> executionPaths) {
		int i = 0;
		for (List<JCTree> executionPath : executionPaths) {
			System.out
					.println("************ " + methodDeclaration.getName().toString() + ": " + (++i) + " ************");
			for (JCTree instruction : executionPath) {
				System.out.println(instruction);
			}
		}
	}
}
