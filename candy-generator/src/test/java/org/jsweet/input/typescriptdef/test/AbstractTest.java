package org.jsweet.input.typescriptdef.test;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.jsweet.input.typescriptdef.TypescriptDef2Java;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.parser.TypescriptDefParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

public abstract class AbstractTest {

	protected final Logger logger = Logger.getLogger(getClass());

	@Rule
	public final TestName testNameRule = new TestName();

	protected File getTestFile(String name) {
		return new File("./src/test/java/" + getClass().getPackage().getName().replace('.', '/'), name + ".d.ts");
	}

	protected final String getCurrentTestName() {
		return getClass().getSimpleName() + "." + testNameRule.getMethodName();
	}

	protected Context parseAndTranslateTestFile(String testFilePath) throws FileNotFoundException {
		return parseAndTranslateTestFile(testFilePath, null);
	}

	protected Context parseAndTranslateTestFile(String testFilePath, Consumer<Context> afterParse)
			throws FileNotFoundException {
		return parseAndTranslateTestFile(testFilePath, afterParse, false);
	}

	protected Context parseTestFile(String testFilePath) throws FileNotFoundException {
		return parseTestFile(testFilePath, false);
	}

	protected Context parseTestFile(String testFilePath, boolean includeCoreLib) throws FileNotFoundException {
		File testFile = getTestFile(testFilePath);

		TypescriptDefParser parser = TypescriptDefParser.parseFile(testFile);
		Assert.assertEquals("unexpected syntax errors", 0, parser.errors.size());

		List<CompilationUnit> compilationUnits = new ArrayList<>();
		compilationUnits.add(parser.compilationUnit);
		List<File> dependencies = new ArrayList<>();
		if (includeCoreLib) {
			try {
				URL libCoreURL = getClass().getClassLoader().getResource(TypescriptDef2Java.TS_CORE_LIB_DIR);
				File libCoreDir = new File(libCoreURL.toURI());
				for (File lib : libCoreDir.listFiles()) {
					if (lib.isFile()) {
						dependencies.add(lib);
						compilationUnits.add(TypescriptDefParser.parseFile(lib).compilationUnit);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		Context context = new Context(asList(testFile), dependencies, false);
		context.verbose = false;
		context.compilationUnits = compilationUnits;
		return context;
	}

	protected Context parseAndTranslateTestFile(String testFilePath, Consumer<Context> afterParse,
			boolean includeCoreLib) throws FileNotFoundException {
		Context context = parseTestFile(testFilePath, includeCoreLib);
		if (afterParse != null) {
			afterParse.accept(context);
		}
		translateAst(context);
		return context;
	}

	protected void translateAst(Context context) {
		TypescriptDef2Java.translateAst(context);
		Assert.assertEquals("unexpected translation errors", 0, context.getErrorCount());
	}

	@Before
	public void setUp() {
		logger.info("*********************** " + getCurrentTestName() + " ***********************");
	}

}
