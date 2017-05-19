package org.jsweet.test.transpiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
		context.addAnnotation("@Erased", "**.toBeErased(..)");
		context.addAnnotation("@Name('_f2')", "**.f2");
		context.addAnnotation("@Name('_f4')", "**.f4(..)");
		context.addAnnotation("@Name('_f6')", "**.f6");
		context.addAnnotation("@Name('_f8')", "**.f8(..)");
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
			try {
				String generated = FileUtils.readFileToString(transpiler.getContext().sourceFiles[0].getTsFile());
				Assert.assertEquals(3, StringUtils.countMatches(generated, "_f1"));
				Assert.assertEquals(3, StringUtils.countMatches(generated, "_f2"));
				Assert.assertEquals(2, StringUtils.countMatches(generated, "_f3"));
				Assert.assertEquals(2, StringUtils.countMatches(generated, "_f4"));
				Assert.assertEquals(3, StringUtils.countMatches(generated, "_f5"));
				Assert.assertEquals(3, StringUtils.countMatches(generated, "_f6"));
				Assert.assertEquals(2, StringUtils.countMatches(generated, "_f7"));
				Assert.assertEquals(2, StringUtils.countMatches(generated, "_f8"));
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}, getSourceFile(AnnotationTest.class));
	}

}