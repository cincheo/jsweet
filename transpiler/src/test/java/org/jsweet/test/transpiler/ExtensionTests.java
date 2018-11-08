package org.jsweet.test.transpiler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.EventObject;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetConfig;
import org.jsweet.test.transpiler.util.TranspilerTestRunner;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.extension.AnnotationManager;
import org.jsweet.transpiler.extension.DisallowGlobalVariablesAdapter;
import org.jsweet.transpiler.extension.Java2TypeScriptAdapter;
import org.jsweet.transpiler.extension.MapAdapter;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.extension.RemoveJavaDependenciesAdapter;
import org.jsweet.transpiler.model.ImportElement;
import org.junit.Assert;
import org.junit.Test;

import source.extension.A1;
import source.extension.A2;
import source.extension.AbstractClassWithBigDec;
import source.extension.AnnotationTest;
import source.extension.HelloWorldDto;
import source.extension.HelloWorldService;
import source.extension.IAddNumber;
import source.extension.Maps;
import source.extension.UseOfGlobalVariable;

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

class HelloWorldAdapter extends PrinterAdapter {
	public HelloWorldAdapter(PrinterAdapter parent) {
		super(parent);
		addTypeMapping(java.util.Date.class.getName(), "string");
		addHeader("comment", "/* this is a header comment */");
		if (getHeader("comment") == null) {
			throw new RuntimeException("header not added");
		}
	}
}

class JaxRSStubAdapter extends PrinterAdapter {

	public JaxRSStubAdapter(PrinterAdapter parent) {
		super(parent);
		// erase service classes (server-side only)
		addAnnotationManager(new AnnotationManager() {
			@Override
			public Action manageAnnotation(Element element, String annotationType) {
				return JSweetConfig.ANNOTATION_ERASED.equals(annotationType)
						&& hasAnnotationType(element, Path.class.getName()) ? Action.ADD : Action.VOID;
			}
		});
	}

	@Override
	public void afterType(TypeElement type) {
		super.afterType(type);
		if (hasAnnotationType(type, Path.class.getName())) {
			println().printIndent();
			print("class ").print(type.getSimpleName()).print(" {");
			startIndent();
			String typePathAnnotationValue = getAnnotationValue(type, Path.class.getName(), String.class, null);
			String typePath = typePathAnnotationValue != null ? typePathAnnotationValue : "";
			for (Element e : type.getEnclosedElements()) {
				if (e instanceof ExecutableElement
						&& hasAnnotationType(e, GET.class.getName(), PUT.class.getName(), Path.class.getName())) {
					ExecutableElement method = (ExecutableElement) e;
					println().printIndent().print(method.getSimpleName().toString()).print("(");
					for (VariableElement parameter : method.getParameters()) {
						print(parameter.getSimpleName()).print(" : ").print(getMappedType(parameter.asType()))
								.print(", ");
					}
					print("successHandler : (");
					if (method.getReturnType().getKind() != TypeKind.VOID) {
						print("result : ").print(getMappedType(method.getReturnType()));
					}
					print(") => void, errorHandler?: () => void").print(") : void");
					print(" {").println().startIndent().printIndent();
					String pathAnnotationValue = getAnnotationValue(e, Path.class.getName(), String.class, null);
					String path = pathAnnotationValue != null ? pathAnnotationValue : "";
					String httpMethod = "POST";
					if (hasAnnotationType(e, GET.class.getName())) {
						httpMethod = "GET";
					}
					if (hasAnnotationType(e, POST.class.getName())) {
						httpMethod = "POST";
					}
					String[] consumes = getAnnotationValue(e, "javax.ws.rs.Consumes", String[].class, null);
					if (consumes == null) {
						consumes = new String[] { "application/json" };
					}
					print("// modify JaxRSStubAdapter to generate an HTTP invocation here").println().printIndent();
					print("//   - httpMethod: " + httpMethod).println().printIndent();
					print("//   - path: " + typePath + path).println().printIndent();
					print("//   - consumes: " + consumes[0]).println().printIndent();
					println().endIndent().printIndent().print("}");
				}
			}
			println().endIndent().printIndent().print("}");
		}
	}

}

public class ExtensionTests extends AbstractTest {

	@Test
	public void testAnnotations() {
		TranspilerTestRunner transpilerExtensionTest = new TranspilerTestRunner(getCurrentTestOutDir(),
				new TestFactory());
		transpilerExtensionTest.transpile(ModuleKind.none, (logHandler) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			try {
				String generated = FileUtils.readFileToString(
						transpilerExtensionTest.getTranspiler().getContext().sourceFiles[0].getTsFile());
				Assert.assertEquals(4, StringUtils.countMatches(generated, "_f1"));
				Assert.assertEquals(4, StringUtils.countMatches(generated, "_f2"));
				Assert.assertEquals(2, StringUtils.countMatches(generated, "_f3"));
				Assert.assertEquals(2, StringUtils.countMatches(generated, "_f4"));
				Assert.assertEquals(4, StringUtils.countMatches(generated, "_f5"));
				Assert.assertEquals(4, StringUtils.countMatches(generated, "_f6"));
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
		TranspilerTestRunner transpilerExtensionTest = new TranspilerTestRunner(getCurrentTestOutDir(),
				new JSweetFactory() {
					@Override
					public PrinterAdapter createAdapter(JSweetContext context) {
						return new MapAdapter(super.createAdapter(context));
					}
				});
		transpilerExtensionTest.eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(Maps.class));

	}

	@Test
	public void testJaxRSStubs() {

		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new JSweetFactory() {
			@Override
			public PrinterAdapter createAdapter(JSweetContext context) {
				return new JaxRSStubAdapter(super.createAdapter(context));
			}
		});
		transpilerTest.transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(HelloWorldService.class), getSourceFile(HelloWorldDto.class));

	}

	@Test
	public void testHelloWorldAdapter() throws IOException {
		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new JSweetFactory() {

			@Override
			public PrinterAdapter createAdapter(JSweetContext context) {
				return new HelloWorldAdapter(super.createAdapter(context));
			}
		});
		SourceFile f = getSourceFile(HelloWorldDto.class);
		transpilerTest.transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, f);
		String generatedCode = FileUtils.readFileToString(f.getTsFile());
		Assert.assertTrue(generatedCode.contains("this is a header comment"));
		Assert.assertTrue(generatedCode.contains("date : string"));
		Assert.assertFalse(generatedCode.contains("date : Date"));
	}

	@Test
	public void testDisallowGlobalVariablesAdapter() {
		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new JSweetFactory() {
			@Override
			public PrinterAdapter createAdapter(JSweetContext context) {
				return new DisallowGlobalVariablesAdapter(super.createAdapter(context));
			}
		});
		transpilerTest.transpile(logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.USER_ERROR);
		}, getSourceFile(UseOfGlobalVariable.class));

	}

	@Test
	public void testErasedImportAdapter() {
		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new JSweetFactory() {
			@Override
			public PrinterAdapter createAdapter(JSweetContext context) {
				return new PrinterAdapter(super.createAdapter(context)) {
					{
						addAnnotation("@Erased", A2.class.getName());
						addAnnotation("@Erased", A1.class.getName() + ".main(..)");
					}
				};
			}
		});
		transpilerTest.eval((logHandler, result) -> {
			logHandler.assertNoProblems();
			try {
				String generatedCode = FileUtils
						.readFileToString(transpilerTest.getTranspiler().getContext().sourceFiles[1].getTsFile());
				Assert.assertFalse(generatedCode.contains("A2"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}, getSourceFile(A2.class), getSourceFile(A1.class));

	}

	@Test
	public void testRemoveImportAdapter2() {
	
		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new JSweetFactory() {
			@Override
			public PrinterAdapter createAdapter(JSweetContext context) {
				return new PrinterAdapter(super.createAdapter(context)) {
					{
						addAnnotation("@Replace('')", A1.class.getName() + ".main(..)");
					}

					@Override
					public String needsImport(ImportElement importElement, String qualifiedName) {
						if (qualifiedName.startsWith(A2.class.getName())) {
							// remove the import
							return null;
						} else {
							return super.needsImport(importElement, qualifiedName);
						}
					}
				};
			}
		});
		transpilerTest.eval((logHandler, result) -> {
			logHandler.assertNoProblems();
			if (transpilerTest.getTranspiler().getContext().options.getModuleKind() == ModuleKind.commonjs) {
				try {
					String generatedCode = FileUtils
							.readFileToString(transpilerTest.getTranspiler().getContext().sourceFiles[1].getTsFile());
					// it is still there because it is used in the main function body
					Assert.assertTrue(generatedCode.contains("A2"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, getSourceFile(A2.class), getSourceFile(A1.class));
	}
	
	@Test
	public void testRemoveImportAdapter() {
		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new JSweetFactory() {
			@Override
			public PrinterAdapter createAdapter(JSweetContext context) {
				return new PrinterAdapter(super.createAdapter(context)) {
					{
						addAnnotation("@Replace('')", A1.class.getName() + ".main(..)");
					}
				};
			}
		});
		transpilerTest.eval((logHandler, result) -> {
			logHandler.assertNoProblems();
			if (transpilerTest.getTranspiler().getContext().options.getModuleKind() == ModuleKind.commonjs) {
				try {
					String generatedCode = FileUtils
							.readFileToString(transpilerTest.getTranspiler().getContext().sourceFiles[1].getTsFile());
					Assert.assertTrue(generatedCode.contains("A2"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, getSourceFile(A2.class), getSourceFile(A1.class));
	}

	@Test
	public void testTypeMappingForImportAndInterface() {
		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new JSweetFactory() {
			@Override
			public PrinterAdapter createAdapter(JSweetContext context) {
				return new PrinterAdapter(super.createAdapter(context)) {
					{
						addTypeMapping(BigDecimal.class.getName(), "number");
						addTypeMapping(IAddNumber.class.getName(), "Object");
					}
				};
			}
		});
		transpilerTest.eval((logHandler, result) -> {
			logHandler.assertNoProblems();
			if (transpilerTest.getTranspiler().getContext().options.getModuleKind() == ModuleKind.commonjs) {
				try {
					String generatedCode = FileUtils
							.readFileToString(transpilerTest.getTranspiler().getContext().sourceFiles[1].getTsFile());
					// "BigDecimal" should never occur, not even in an import statement
					Assert.assertFalse(generatedCode.contains("BigDecimal"));
					// the method signatures from the mapped interface should not be added automatically
					Assert.assertFalse(generatedCode.contains("addNumber"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, getSourceFile(IAddNumber.class), getSourceFile(AbstractClassWithBigDec.class));
	}

}