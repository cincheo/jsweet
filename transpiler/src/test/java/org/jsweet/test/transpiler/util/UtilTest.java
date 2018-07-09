package org.jsweet.test.transpiler.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jsweet.test.transpiler.AbstractTest;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.Util;
import org.junit.Test;

import com.sun.tools.javac.code.Type.ClassType;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;

import source.structural.ExtendsClassInSameFile;

public class UtilTest extends AbstractTest {

	@Test
	public void testConvertToRelativePath() {
		assertEquals("../c", Util.getRelativePath("/a/b", "/a/c"));
		assertEquals("..", Util.getRelativePath("/a/b", "/a"));
		assertEquals("../e", Util.getRelativePath("/a/b/c", "/a/b/e"));
		assertEquals("d", Util.getRelativePath("/a/b/c", "/a/b/c/d"));
		assertEquals("d/e", Util.getRelativePath("/a/b/c", "/a/b/c/d/e"));
		assertEquals("../../../d/e/f", Util.getRelativePath("/a/b/c", "/d/e/f"));
		assertEquals("../..", Util.getRelativePath("/a/b/c", "/a"));
		assertEquals("..", Util.getRelativePath("/a/b/c", "/a/b"));
	}

	@Test
	public void testIsDeclarationOrSubClassDeclaration() throws Exception {

		SourceFile sourceFile = getSourceFile(ExtendsClassInSameFile.class);
		JCClassDecl classDeclaration = getSourcePublicClassDeclaration(sourceFile);

		boolean isDeclaration;
		String searchedClassName = ExtendsClassInSameFile.class.getName();

		logger.info("search class name: " + searchedClassName);
		isDeclaration = Util.isDeclarationOrSubClassDeclaration(types(), (ClassType) classDeclaration.type,
				searchedClassName);
		assertTrue(isDeclaration);

		searchedClassName = ExtendsClassInSameFile.class.getName().replace(".ExtendsClassInSameFile", ".Foo1");
		logger.info("search class name: " + searchedClassName);
		isDeclaration = Util.isDeclarationOrSubClassDeclaration(types(), (ClassType) classDeclaration.type,
				searchedClassName);
		assertTrue(isDeclaration);

		searchedClassName = ExtendsClassInSameFile.class.getName().replace(".ExtendsClassInSameFile", ".TRULULU");
		logger.info("search class name: " + searchedClassName);
		isDeclaration = Util.isDeclarationOrSubClassDeclaration(types(), (ClassType) classDeclaration.type,
				searchedClassName);
		assertFalse(isDeclaration);
	}

	@Test
	public void testIsDeclarationOrSubClassDeclarationBySimpleName() throws Exception {

		SourceFile sourceFile = getSourceFile(ExtendsClassInSameFile.class);
		JCClassDecl classDeclaration = getSourcePublicClassDeclaration(sourceFile);

		boolean isDeclaration;

		String searchedClassName = "ExtendsClassInSameFile";
		logger.info("search class name: " + searchedClassName);
		isDeclaration = Util.isDeclarationOrSubClassDeclarationBySimpleName(types(), (ClassType) classDeclaration.type,
				searchedClassName);
		assertTrue(isDeclaration);

		searchedClassName = "Foo1";
		logger.info("search class name: " + searchedClassName);
		isDeclaration = Util.isDeclarationOrSubClassDeclarationBySimpleName(types(), (ClassType) classDeclaration.type,
				searchedClassName);
		assertTrue(isDeclaration);

		searchedClassName = "TRULULU";
		logger.info("search class name: " + searchedClassName);
		isDeclaration = Util.isDeclarationOrSubClassDeclarationBySimpleName(types(), (ClassType) classDeclaration.type,
				searchedClassName);
		assertFalse(isDeclaration);
	}
}
