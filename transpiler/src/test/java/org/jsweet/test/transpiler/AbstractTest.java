/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.test.transpiler;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.lang.model.element.Modifier;
import javax.lang.model.util.Types;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.jsweet.test.transpiler.util.TranspilerTestRunner;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import standalone.com.sun.source.tree.ClassTree;
import standalone.com.sun.source.tree.CompilationUnitTree;
import standalone.com.sun.source.util.Trees;

import ts.nodejs.NodejsProcess;

public class AbstractTest {

	protected static final String TEST_DIRECTORY_NAME = "src/test/java";

	protected final Logger logger = Logger.getLogger(getClass());

	private static int testCount;

	@Rule
	public final TestName testNameRule = new TestName();

	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			logger.info("*********************** " + description.getMethodName() + " #" + (++testCount)
					+ " ***********************");
		}
	};

	protected File getTestFile(String name) {
		return new File(TEST_DIRECTORY_NAME + "/" + getClass().getPackage().getName().replace('.', '/'),
				name + ".d.ts");
	}

	protected final String getCurrentTestName() {
		return getClass().getSimpleName() + "." + testNameRule.getMethodName();
	}

	private TranspilerTestRunner transpilerTest;

	protected static final String TMPOUT_DIR = "tempOut";

	protected Types types() {
		return transpilerTest.types();
	}

	protected Trees trees() {
		return transpilerTest().getTranspiler().getContext().trees;
	}

	protected TranspilerTestRunner transpilerTest() {
		return transpilerTest;
	}

	@Before
	public void setUpAbstractTest() {
		NodejsProcess.logProcessStopStack = false;

		logger.info("*** init transpiler test  ***");
		File baseTsOutDir = getCurrentTestOutDir();
		FileUtils.deleteQuietly(baseTsOutDir);
		transpilerTest = new TranspilerTestRunner(baseTsOutDir, new JSweetFactory());
	}

	@After
	public void tearDownAbstractTest() throws Exception {
		transpilerTest.close();
	}

	protected File getCurrentTestOutDir() {
		return new File(new File(TMPOUT_DIR), getCurrentTestName());
	}

	protected Pair<CompilationUnitTree, ClassTree> getSourcePublicClassDeclaration(SourceFile sourceFile)
			throws IOException {
		Map<CompilationUnitTree, List<ClassTree>> declarations = getSourceClassesDeclarations(sourceFile);
		if (declarations.isEmpty()) {
			return null;
		}

		Entry<CompilationUnitTree, List<ClassTree>> singleCompilUnitEntry = declarations.entrySet().iterator().next();

		return Pair.of( //
				singleCompilUnitEntry.getKey(), //
				singleCompilUnitEntry.getValue().stream() //
						.filter(classDecl -> classDecl.getModifiers().getFlags().contains(Modifier.PUBLIC)).findFirst()
						.get() //
		);
	}

	protected Map<CompilationUnitTree, List<ClassTree>> getSourceClassesDeclarations(SourceFile... sourceFiles)
			throws IOException {
		JSweetContext context = transpilerTest.getTranspiler().prepareForJavaFiles(
				Stream.of(sourceFiles).map(sourceFile -> sourceFile.getJavaFile()).collect(toList()),
				new ErrorCountTranspilationHandler(new ConsoleTranspilationHandler()));

		return context.compilationUnits.stream() //
				.collect(toMap( //
						Function.identity(), //
						compilUnit -> compilUnit //
								.getTypeDecls().stream() //
								.filter(declaration -> declaration instanceof ClassTree) //
								.map(declaration -> (ClassTree) declaration) //
								.collect(toList())));
	}

	protected SourceFile getSourceFile(Class<?> mainClass) {
		return new SourceFile(new File(TEST_DIRECTORY_NAME + "/" + mainClass.getName().replace(".", "/") + ".java"));
	}

	protected SourceFile getSourceFile(int testDirIndex, String className) {
		return new SourceFile(
				new File(TEST_DIRECTORY_NAME + testDirIndex + "/" + className.replace(".", "/") + ".java"));
	}

	protected void transpile(Consumer<TestTranspilationHandler> assertions, SourceFile... files) {
		transpilerTest.transpile(assertions, files);
	}

	protected void transpile(ModuleKind[] moduleKinds, Consumer<TestTranspilationHandler> assertions,
			SourceFile... files) {
		transpilerTest.transpile(moduleKinds, assertions, files);
	}

	protected void transpile(ModuleKind moduleKind, Consumer<TestTranspilationHandler> assertions,
			SourceFile... files) {
		transpilerTest.transpile(moduleKind, assertions, files);
	}

	protected void eval(BiConsumer<TestTranspilationHandler, EvaluationResult> assertions, SourceFile... files) {
		transpilerTest.eval(assertions, files);
	}

	protected void eval(ModuleKind[] moduleKinds, BiConsumer<TestTranspilationHandler, EvaluationResult> assertions,
			SourceFile... files) {
		transpilerTest.eval(moduleKinds, assertions, files);
	}

	protected void eval(ModuleKind moduleKind, BiConsumer<TestTranspilationHandler, EvaluationResult> assertions,
			SourceFile... files) {
		transpilerTest.eval(moduleKind, assertions, files);
	}

	protected void eval(ModuleKind moduleKind, boolean testBundle,
			BiConsumer<TestTranspilationHandler, EvaluationResult> assertions, SourceFile... files) {
		transpilerTest.eval(moduleKind, testBundle, assertions, files);

	}

}