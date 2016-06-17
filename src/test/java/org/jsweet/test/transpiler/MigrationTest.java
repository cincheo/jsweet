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
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.TreeScanner;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.tools.JavaFileObject;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.typescript.Java2TypeScriptTranslator;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
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

//    @Test
    public void testQuickStart() throws Exception {
        transpile(getSourceFile(QuickStart.class));
    }

    void printSym(JCTree tree) {
        JCTree.Visitor visitor1 = new TreeScanner() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl tree) {
                System.out.println(tree.getClass().getName() + ": " + tree.sym);
                super.visitClassDef(tree);
            }

            @Override
            public void visitIdent(JCTree.JCIdent tree) {
                System.out.println(tree.getClass().getName() + ": " + tree.sym);
                super.visitIdent(tree);
            }

            @Override
            public void visitMethodDef(JCTree.JCMethodDecl tree) {
                System.out.println(tree.getClass().getName() + ": " + tree.sym);
                super.visitMethodDef(tree);
            }

            @Override
            public void visitReference(JCTree.JCMemberReference tree) {
                System.out.println(tree.getClass().getName() + ": " + tree.sym);
                super.visitReference(tree);
            }

            @Override
            public void visitSelect(JCTree.JCFieldAccess tree) {
                System.out.println(tree.getClass().getName() + ": " + tree.sym);
                super.visitSelect(tree);
            }

            @Override
            public void visitVarDef(JCTree.JCVariableDecl tree) {
                System.out.println(tree.getClass().getName() + ": " + tree.sym);
                super.visitVarDef(tree);
            }

        };
        tree.accept(visitor1);
    }

    @Test
    public void test0() throws Exception {
        File dir = Files.createTempDirectory("jsweet").toFile();
        System.out.println("Transpile directory: " + dir);
        JSweetTranspiler transpiler = new JSweetTranspiler(
            new File(dir, "wd"),
            new File(dir, "ts"),
            new File(dir, "js"),
            new File(dir, "cjs"),
            null
        );
        transpiler.logger.setLevel(Level.WARN);
        TranspilationHandler handler0 = new ConsoleTranspilationHandler();
        transpiler.initJavac(handler0);
        transpiler.initNode(handler0);
        transpiler.initNodeCommands(handler0);
        JSweetTranspiler.RecordTranspilationHandler rhandler = new JSweetTranspiler.RecordTranspilationHandler();
        ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(rhandler);
        Java2TypeScriptTranslator translator = new Java2TypeScriptTranslator(
            handler,
            transpiler.getContext(),
            null,
            false
        );

        SourceFile file = getSourceFile(QuickStart.class);

        Iterable<? extends JavaFileObject> io = transpiler
            .getFileManager()
            .getJavaFileObjectsFromFiles(Arrays.asList(file.getJavaFile()));
        List<JCCompilationUnit> compilationUnits = transpiler
            .getCompiler()
            .enterTrees(transpiler.getCompiler().parseFiles((Iterable<JavaFileObject>) io));
        transpiler.getCompiler().attribute(transpiler.getCompiler().todo);
        transpiler.getContext().useModules = false;
        transpiler.getContext().sourceFiles = new SourceFile[]{file};
        System.out.println("trees count: " + compilationUnits.size());
        JCCompilationUnit cu = compilationUnits.get(0);

        List<JCTree.JCMethodDecl> methods = new ArrayList<>();
        JCTree.Visitor visitor0 = new TreeScanner() {

            @Override
            public void visitMethodDef(JCTree.JCMethodDecl tree) {
                methods.add(tree);
            }
        };
        cu.accept(visitor0);
        methods.forEach(t -> System.out.println(t.getName()));
        System.out.println("methods count: " + methods.size());

//        JCTree.JCMethodDecl method = methods.stream()
//            .filter(m -> "concat".equals(m.getName().toString()))
//            .findAny()
//            .get();
//        transpiler.transpile(handler, file);
        methods.remove(0);
        int p = 0;
        for (JCTree.JCMethodDecl method : methods) {
            System.out.println("java code:" + method);

            translator.replaceBuilder(new StringBuilder());
            rhandler.getProblems().clear();
            translator.enterScope();
            translator.scan(method);
            translator.exitScope();
            String tsCode = translator.getResult();
            System.out.println("resulting ts:\n" + tsCode);

            String jsCode = transpiler.tspart2js(method, tsCode, handler, "part" + p++);
            System.out.println("resulting js:\n" + jsCode);
            System.out.println("errors: {" + rhandler.getProblems().stream().collect(Collectors.joining("\n")) + "}");
        }
    }

}
