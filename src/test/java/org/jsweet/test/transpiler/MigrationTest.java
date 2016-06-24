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

import com.sun.tools.javac.tree.JCTree;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.JSweetTranspiler.TranspiledPartsPrinter;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.junit.Test;
import source.migration.QuickStart;

public class MigrationTest extends AbstractTest {

    @Test
    public void test1() throws Exception {
        File dir = Files.createTempDirectory("jsweet").toFile();
        System.out.println("Transpile directory: " + dir);
        JSweetTranspiler transpiler = new JSweetTranspiler(
            new File(dir, "wd"),
            new File(dir, "ts"),
            new File(dir, "js"),
            new File(dir, "cjs"),
            null
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TranspiledPartsPrinter printer = new TranspiledPartsPrinter(baos, transpiler) {

            @Override
            public void visitMethodDef(JCTree.JCMethodDecl method) {
                if ("<init>".equals(method.getName().toString()) || "<clinit>".equals(method.getName().toString())) {
                    super.visitMethodDef(method);
                } else {
                    ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(new ConsoleTranspilationHandler());
                    String jsCode = transpile(method, handler);
                    if (handler.getErrorCount() == 0) {
                        try {
                            print("\n/*\n");
                            print(Arrays.stream(String.format(
                                "java code:\n%s\njs code:\n%s\n",
                                method,
                                jsCode
                            ).split("\n")).map(s -> " * " + s).collect(Collectors.joining("\n")));
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
        baos.reset();
        transpiler.migrate(
            new ConsoleTranspilationHandler(),
            new File(TEST_DIRECTORY_NAME + "/" + QuickStart.class.getName().replace(".", "/") + ".java"),
            printer);
        printer.flush();
        System.out.println(baos);

    }
}
