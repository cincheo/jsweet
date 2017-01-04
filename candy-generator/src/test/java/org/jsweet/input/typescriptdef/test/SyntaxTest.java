package org.jsweet.input.typescriptdef.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.QualifiedDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.junit.Assert;
import org.junit.Test;

public class SyntaxTest extends AbstractTest {

	@Test
	public void defaultTest() throws FileNotFoundException {
		Context context = parseTestFile("syntax/default-test");
		assertEquals("boolean", context.findFirstDeclaration(FunctionDeclaration.class, "*.Array.isArray")
				.getDeclaration().getType().getName());
	}

	@Test
	public void enumTest() throws FileNotFoundException {
		parseTestFile("syntax/enum-test");
	}

	@Test
	public void comment1Test() throws FileNotFoundException {
		Context context = parseTestFile("syntax/comment1-test");
		Declaration declaration = context.findFirstDeclaration(TypeDeclaration.class, "I").getDeclaration();
		Assert.assertTrue(declaration.getDocumentation() != null
				&& declaration.getDocumentation().contains("Comment I"));
		declaration = context.findFirstDeclaration(TypeDeclaration.class, "Navigator").getDeclaration();
		Assert.assertNull(declaration.getDocumentation());
		declaration = context.findFirstDeclaration(VariableDeclaration.class, "Navigator.accelerometer")
				.getDeclaration();
		Assert.assertTrue(declaration.getDocumentation() != null
				&& declaration.getDocumentation().contains("Comment 1"));
		declaration = context.findFirstDeclaration(VariableDeclaration.class, "Navigator.camera").getDeclaration();
		Assert.assertNull(declaration.getDocumentation());
	}

	@Test
	public void comment2Test() throws FileNotFoundException {
		Context context = parseTestFile("syntax/comment2-test");
		for (QualifiedDeclaration<Declaration> declaration : context.findDeclarations(Declaration.class, "*")) {
			if (!declaration.getDeclaration().isAnonymous()) {
				Assert.assertNotNull("member '" + declaration.getDeclaration().getName()
						+ "' should have a documentation", declaration.getDeclaration().getDocumentation());
			}
		}
	}

	@Test
	public void comment3Test() throws FileNotFoundException {
		Context context = parseTestFile("syntax/comment3-test");
		for (QualifiedDeclaration<Declaration> declaration : context.findDeclarations(Declaration.class, "*")) {
			Assert.assertTrue(
					"member '" + declaration.getDeclaration().getName() + "' has wrong documentation",
					declaration.getDeclaration().getDocumentation() != null
							&& declaration.getDeclaration().getDocumentation()
									.contains("$" + declaration.getDeclaration().getName() + "$"));
		}
	}

	@Test
	public void keywordTest() throws FileNotFoundException {
		parseTestFile("syntax/keyword-test");
	}

	@Test
	public void lfTest() throws FileNotFoundException {
		parseTestFile("syntax/lf-test");
	}

	@Test
	public void identifierTest() throws FileNotFoundException {
		parseTestFile("syntax/identifier-test");
	}
	
	@Test
	public void exportTest() throws FileNotFoundException {
		parseTestFile("syntax/export-test");
	}
}
