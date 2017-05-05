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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.Pretty;

import source.migration.QuickStart;

public class MigrationTest extends AbstractTest {

	@Ignore
	@Test
	public void test1() throws Exception {
		File dir = Files.createTempDirectory("jsweet").toFile();
		System.out.println("Transpile directory: " + dir);
		ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(new ConsoleTranspilationHandler());
		JSweetTranspiler transpiler = new JSweetTranspiler(new JSweetFactory(), new File(dir, "wd"),
				new File(dir, "ts"), new File(dir, "js"), new File(dir, "cjs"), null);
		File input = new File(TEST_DIRECTORY_NAME + "/" + QuickStart.class.getName().replace(".", "/") + ".java");
		transpiler.initNode(handler);
		List<JCTree.JCCompilationUnit> units = transpiler.setupCompiler(Arrays.asList(input), handler);
		Assert.assertEquals(1, units.size());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		Pretty printer = new Pretty(writer, true) {

			@Override
			public void visitMethodDef(JCTree.JCMethodDecl method) {
				if ("<init>".equals(method.getName().toString()) || "<clinit>".equals(method.getName().toString())) {
					super.visitMethodDef(method);
				} else {
					ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(
							new ConsoleTranspilationHandler());
					String jsCode;
					try {
						jsCode = transpiler.transpile(handler, method, "part");
					} catch (IOException ex) {
						throw new UncheckedIOException(ex);
					}
					if (handler.getErrorCount() == 0) {
						try {
							print("\n/*\n");
							print(Arrays
									.stream(String.format("java code:\n%s\njs code:\n%s\n", method, jsCode).split("\n"))
									.map(s -> " * " + s).collect(Collectors.joining("\n")));
							print("*/\n");
							print(method);
						} catch (IOException ex) {
							throw new UncheckedIOException(ex);
						}
					} else {
						super.visitMethodDef(method);
					}
				}
			}
		};
		units.get(0).accept(printer);
		writer.flush();
		// System.out.println(baos); // prints resulting code
		// verify that there are exactly two comments in the code
		Assert.assertEquals(4, baos.toString().split("/\\*").length);
		Assert.assertEquals(4, baos.toString().split("\\*/").length);
	}
}
