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

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.junit.Test;
import source.migration.QuickStart;

public class MigrationTest extends AbstractTest {

    public void transpile(SourceFile... files) throws Exception {
        File dir = Files.createTempDirectory("jsweet").toFile();
        System.out.println("Transpile directory: " + dir);
        JSweetTranspiler transpiler = new JSweetTranspiler(
            new File(dir, "wd"),
            new File(dir, "ts"),
            new File(dir, "js"),
            new File(dir, "cjs"),
            null
        );
        transpiler.setModuleKind(ModuleKind.none);
        FileUtils.deleteQuietly(transpiler.getWorkingDirectory());
//        transpiler.getCandiesProcessor().touch();
        TranspilationHandler logHandler = new ConsoleTranspilationHandler();
        transpiler.migrate(
            logHandler,
            files,
            (java, ts) -> true,
            (java, js) -> "\n/* Successfully transpiled to JS! JS code:\n"
            + Arrays.stream(js.split("\n"))
            .map(s -> " * " + s)
            .collect(Collectors.joining("\n"))
            + "\n */"
            + java,
            (java, errors) -> "\n/* Cannot translate to JS. Errors:\n"
            + errors.stream()
            .map(s -> " * " + s)
            .collect(Collectors.joining("\n"))
            + "\n */"
        );
    }

    @Test
    public void testQuickStart() throws Exception {
        transpile(getSourceFile(QuickStart.class));
    }

}
