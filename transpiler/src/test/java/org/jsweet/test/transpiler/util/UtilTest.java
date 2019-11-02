package org.jsweet.test.transpiler.util;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.tuple.Pair;
import org.jsweet.test.transpiler.AbstractTest;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetOptions;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.Util;
import org.junit.Before;
import org.junit.Test;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;

import source.structural.ExtendsClassInSameFile;

public class UtilTest extends AbstractTest {

	private JSweetContext context;
	private Util util;

	@Before
	public void setUp() throws Exception {
		transpilerTest().getTranspiler().setupCompiler(new ArrayList<File>(),
				new ErrorCountTranspilationHandler(new ConsoleTranspilationHandler()));
		context = transpilerTest().getTranspiler().getContext();
		util = new Util(context);
	}

	@Test
	public void testConvertToRelativePath() {
		assertEquals("../c", util.getRelativePath("/a/b", "/a/c"));
		assertEquals("..", util.getRelativePath("/a/b", "/a"));
		assertEquals("../e", util.getRelativePath("/a/b/c", "/a/b/e"));
		assertEquals("d", util.getRelativePath("/a/b/c", "/a/b/c/d"));
		assertEquals("d/e", util.getRelativePath("/a/b/c", "/a/b/c/d/e"));
		assertEquals("../../../d/e/f", util.getRelativePath("/a/b/c", "/d/e/f"));
		assertEquals("../..", util.getRelativePath("/a/b/c", "/a"));
		assertEquals("..", util.getRelativePath("/a/b/c", "/a/b"));
	}

	@Test
	public void testIsCoreType() throws Exception {
		TypeMirror stringType = util.getType(String.class);
		TypeMirror floatType = util.getType(Float.class);
		assertTrue(util.isCoreType(stringType));
		assertTrue(util.isCoreType(floatType));
	}

	@Test
	public void testIsStringType() throws Exception {
		TypeMirror stringType = util.getType(String.class);
		assertTrue(util.isStringType(stringType));
	}

	@Test
	public void testIsDeclarationOrSubClassDeclaration() throws Exception {

		SourceFile sourceFile = getSourceFile(ExtendsClassInSameFile.class);
		Pair<CompilationUnitTree, ClassTree> classDeclarationWithCompilUnit = getSourcePublicClassDeclaration(
				sourceFile);
		TypeMirror classType = getClassType(classDeclarationWithCompilUnit);

		boolean isDeclaration;
		String searchedClassName = ExtendsClassInSameFile.class.getName();

		logger.info("search class name: " + searchedClassName);
		isDeclaration = util.isDeclarationOrSubClassDeclaration(classType, searchedClassName);
		assertTrue(isDeclaration);

		searchedClassName = ExtendsClassInSameFile.class.getName().replace(".ExtendsClassInSameFile", ".Foo1");
		logger.info("search class name: " + searchedClassName);
		isDeclaration = util.isDeclarationOrSubClassDeclaration(classType, searchedClassName);
		assertTrue(isDeclaration);

		searchedClassName = ExtendsClassInSameFile.class.getName().replace(".ExtendsClassInSameFile", ".TRULULU");
		logger.info("search class name: " + searchedClassName);
		isDeclaration = util.isDeclarationOrSubClassDeclaration(classType, searchedClassName);
		assertFalse(isDeclaration);
	}

	private TypeMirror getClassType(Pair<CompilationUnitTree, ClassTree> classDeclarationWithCompilUnit) {
		TypeMirror classType = trees().getElement(
				trees().getPath(classDeclarationWithCompilUnit.getKey(), classDeclarationWithCompilUnit.getRight()))
				.asType();
		return classType;
	}

	@Test
	public void testIsDeclarationOrSubClassDeclarationBySimpleName() throws Exception {

		SourceFile sourceFile = getSourceFile(ExtendsClassInSameFile.class);
		Pair<CompilationUnitTree, ClassTree> classDeclarationWithCompilUnit = getSourcePublicClassDeclaration(
				sourceFile);
		TypeMirror classType = getClassType(classDeclarationWithCompilUnit);

		boolean isDeclaration;

		String searchedClassName = "ExtendsClassInSameFile";
		logger.info("search class name: " + searchedClassName);
		isDeclaration = util.isDeclarationOrSubClassDeclarationBySimpleName(classType, searchedClassName);
		assertTrue(isDeclaration);

		searchedClassName = "Foo1";
		logger.info("search class name: " + searchedClassName);
		isDeclaration = util.isDeclarationOrSubClassDeclarationBySimpleName(classType, searchedClassName);
		assertTrue(isDeclaration);

		searchedClassName = "TRULULU";
		logger.info("search class name: " + searchedClassName);
		isDeclaration = util.isDeclarationOrSubClassDeclarationBySimpleName(classType, searchedClassName);
		assertFalse(isDeclaration);
	}
}
