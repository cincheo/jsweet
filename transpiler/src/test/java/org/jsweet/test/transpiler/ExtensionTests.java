package org.jsweet.test.transpiler;

import java.util.EventObject;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.extension.Java2TypeScriptAdapter;
import org.jsweet.transpiler.extension.MapAdapter;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.extension.RemoveJavaDependenciesAdapter;
import org.jsweet.transpiler.extension.StringEnumAdapter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import source.enums.StringEnums;
import source.extension.AnnotationTest;
import source.extension.Maps;

class TestFactory extends JSweetFactory {

	public TestFactory() {
	}

	@Override
	public Java2TypeScriptAdapter createAdapter(JSweetContext context) {
		return new TestAdapter(context);
	}

}

class TestAdapter extends RemoveJavaDependenciesAdapter {

	public TestAdapter(JSweetContext context) {
		super(context);
		addTypeMapping(EventObject.class.getName(), "source.extension.EventObject2");
		context.addAnnotation("@Replace('super();')",
				"source.extension.ReplaceConstructorTest.ReplaceConstructorTest(*,*)");
		context.addAnnotation("@Erased", "source.extension.ReplaceConstructorTest.ReplaceConstructorTest(*)");
		context.addAnnotation("@Erased", "**.toBeErased(..)");
		context.addAnnotation("@Name('_f2')", "**.f2");
		context.addAnnotation("@Name('_f4')", "**.f4(..)");
		context.addAnnotation("@Name('_f6')", "**.f6");
		context.addAnnotation("@Name('_f8')", "**.f8(..)");
	}

	@Override
	public void afterType(TypeElement type) {
		if ("AnnotationTest".equals(type.getSimpleName().toString())) {
			printIndent().print("class Dummy {").println().startIndent();
			for (Element e : type.getEnclosedElements()) {
				if (e instanceof VariableElement) {
					printIndent().print(e.getSimpleName()).print(": ").print(getMappedType(e.asType())).print(";")
							.println();
				}
			}
			endIndent().printIndent().print("}").println();
		}
	}

	@Override
	public boolean eraseSuperClass(TypeElement type, TypeElement superClass) {
		String name = superClass.getQualifiedName().toString();
		if (EventObject.class.getName().equals(name)) {
			return false;
		}
		return super.eraseSuperClass(type, superClass);
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

	@Test
	public void testMaps() {
		createTranspiler(new JSweetFactory() {
			@Override
			public PrinterAdapter createAdapter(JSweetContext context) {
				return new MapAdapter(super.createAdapter(context));
			}
		});
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(Maps.class));
		createTranspiler(new JSweetFactory());
		
	}
	
}