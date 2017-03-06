package org.jsweet.test.transpiler;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.extension.Java2TypeScriptAdapter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import source.extension.AnnotationTest;

class TestFactory extends JSweetFactory {

	public TestFactory() {
	}

	@Override
	public Java2TypeScriptAdapter createAdapter(JSweetContext context) {
		return new TestAdapter(context);
	}

}

class TestAdapter extends Java2TypeScriptAdapter {

	public TestAdapter(JSweetContext context) {
		super(context);
		context.addAnnotation("@Erased", "*.toBeErased(*)");
	}

}

public class ExtensionTests extends AbstractTest {

	@BeforeClass
	public static void start() {
		createTranspiler(new TestFactory());
	}

	@AfterClass
	public static void end() {
		createTranspiler(new JSweetFactory());
	}

	@Test
	public void testAnnotations() {
		transpile(ModuleKind.none, (logHandler) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(AnnotationTest.class));
	}

}