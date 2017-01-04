package org.jsweet.input.typescriptdef.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.junit.Test;

public class StructureTest extends AbstractTest {

	@Test
	public void globalsTest() throws FileNotFoundException {
		Context context = parseAndTranslateTestFile("structure/globals-test", (c) -> {
			assertNotNull(c.findFirstDeclaration(FunctionDeclaration.class, "m1.m11.f111"));
		} , false);

		assertEquals(1, context.findDeclarations(ModuleDeclaration.class, "def.structure.m1").size());
		assertEquals(1, context.findDeclarations(ModuleDeclaration.class, "def.structure.m1.m11").size());

		assertEquals(1,
				context.findDeclarations(FunctionDeclaration.class, "def.structure.m1.m11.Globals.f111").size());

		assertEquals(1, context.findDeclarations(FunctionDeclaration.class, "def.structure.m1.Utils.f").size());

		assertEquals(0, context.findDeclarations(FunctionDeclaration.class, "def.structure.m1.Utils2.f").size());

	}

	@Test
	public void constructorInterfaceTest() throws FileNotFoundException {
		Context context = parseAndTranslateTestFile("structure/constructor-interface-test", (c) -> {
			assertNotNull(c.findFirstDeclaration(FunctionDeclaration.class, "test.Math.abs"));
		} , false);

		assertEquals(1, context.findDeclarations(TypeDeclaration.class, "def.structure.test.Math").size());
		assertEquals(true, context.findFirstDeclaration(FunctionDeclaration.class, "def.structure.test.Math.abs")
				.getDeclaration().hasModifier("static"));
		assertEquals(0, context.findDeclarations(VariableDeclaration.class, "def.structure.test.Globals.Math").size());
	}

}
