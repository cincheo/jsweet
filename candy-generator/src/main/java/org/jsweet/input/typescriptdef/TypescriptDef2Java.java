/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.input.typescriptdef;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Token;
import org.jsweet.input.typescriptdef.parser.TypescriptDefParser;
import org.jsweet.input.typescriptdef.util.Util;
import org.jsweet.input.typescriptdef.visitor.ConstructorInterfacesMerger;
import org.jsweet.input.typescriptdef.visitor.ConstructorTypeReferenceReplacer;
import org.jsweet.input.typescriptdef.visitor.DeclarationBinder;
import org.jsweet.input.typescriptdef.visitor.DocFiller;
import org.jsweet.input.typescriptdef.visitor.DuplicateMethodsCleaner;
import org.jsweet.input.typescriptdef.visitor.DynamicTypeParametersExpander;
import org.jsweet.input.typescriptdef.visitor.EmptyConstructorAdder;
import org.jsweet.input.typescriptdef.visitor.EmptyModulesCleaner;
import org.jsweet.input.typescriptdef.visitor.FactoryMethodsCreator;
import org.jsweet.input.typescriptdef.visitor.FieldTypeFunctionInjector;
import org.jsweet.input.typescriptdef.visitor.ForEachClashRemover;
import org.jsweet.input.typescriptdef.visitor.FunctionKindAdapter;
import org.jsweet.input.typescriptdef.visitor.FunctionTypeOfReplacer;
import org.jsweet.input.typescriptdef.visitor.FunctionalInterfacesCreator;
import org.jsweet.input.typescriptdef.visitor.GlobalsCreator;
import org.jsweet.input.typescriptdef.visitor.ImportedAndExportedReferenceExpander;
import org.jsweet.input.typescriptdef.visitor.IterableInjector;
import org.jsweet.input.typescriptdef.visitor.JavaDefModelPrinter;
import org.jsweet.input.typescriptdef.visitor.ModuleToTypeMerger;
import org.jsweet.input.typescriptdef.visitor.NameAdapter;
import org.jsweet.input.typescriptdef.visitor.NameChecker;
import org.jsweet.input.typescriptdef.visitor.ObjectTypeCreator;
import org.jsweet.input.typescriptdef.visitor.ObjectTypeDuplicateMerger;
import org.jsweet.input.typescriptdef.visitor.OptionalParametersExpander;
import org.jsweet.input.typescriptdef.visitor.OptionalParametersInFunctionalTypesExpander;
import org.jsweet.input.typescriptdef.visitor.PackageOrganizer;
import org.jsweet.input.typescriptdef.visitor.ParentMethodReturnTypeSusbtitutor;
import org.jsweet.input.typescriptdef.visitor.SerializableHandler;
import org.jsweet.input.typescriptdef.visitor.StringTypeCreator;
import org.jsweet.input.typescriptdef.visitor.SuperTypesMerger;
import org.jsweet.input.typescriptdef.visitor.TupleTypeCreator;
import org.jsweet.input.typescriptdef.visitor.TypeKindChooser;
import org.jsweet.input.typescriptdef.visitor.TypeMacroReplacer;
import org.jsweet.input.typescriptdef.visitor.TypeMerger;
import org.jsweet.input.typescriptdef.visitor.TypeParametersSubstitutor;
import org.jsweet.input.typescriptdef.visitor.TypeReferenceChecker;
import org.jsweet.input.typescriptdef.visitor.TypeReferenceExpander;
import org.jsweet.input.typescriptdef.visitor.UnionInterfacesCreator;
import org.jsweet.input.typescriptdef.visitor.UnionTypesEraser;
import org.jsweet.input.typescriptdef.visitor.UnionTypesExpander;

/**
 * Entry point for candy generation from TypeScript definition files
 * 
 * @author Louis Grignon
 * @author Renaud Pawlak
 *
 */
public class TypescriptDef2Java {

	private final static Logger logger = Logger.getLogger(TypescriptDef2Java.class);

	public static final String TS_CORE_LIB_DIR = "lib.core";
	public static final String TS_DOM_LIB_DIR = "lib.dom";
	public static boolean generateMissingTypes = false;

	private static void addTsDefFiles(File input, ArrayList<File> files, Predicate<File> libFilter) {
		if (input.isDirectory()) {
			File[] subFiles = input.listFiles();
			if (subFiles != null) {
				for (File file : subFiles) {
					addTsDefFiles(file, files, libFilter);
				}
			}
		} else {
			if (input.getName().endsWith(".d.ts") && !input.getPath().contains("legacy") && libFilter.test(input)) {
				files.add(input);
			}
		}
	}

	private static void scan(CompilationUnit compilationUnit, Scanner scanner) {
		try {
			scanner.scan(compilationUnit);
		} catch (Exception e) {
			scanner.printStackTrace(System.err);
			logger.error(e.getMessage(), e);
		}
	}

	private static void scan(List<CompilationUnit> compilationUnits, Scanner... scanners) {
		scan(null, compilationUnits, scanners);
	}

	private static void scan(BiConsumer<CompilationUnit, Scanner> onScannedCallback,
			List<CompilationUnit> compilationUnits, Scanner... scanners) {
		for (Scanner scanner : scanners) {
			scanner.onScanStart();
		}
		for (CompilationUnit compilationUnit : compilationUnits) {
			for (Scanner scanner : scanners) {
				scan(compilationUnit, scanner);
				if (onScannedCallback != null) {
					onScannedCallback.accept(compilationUnit, scanner);
				}
			}
		}
		for (Scanner scanner : scanners) {
			scanner.onScanEnded();
		}
	}

	public static void main(String[] args) throws Throwable {
		translate( //
				asList(new File("typings/globals/jquery/index.d.ts")), //
				asList(new File("typings/lib.core/lib.core.d.ts"), new File("typings/lib.core/lib.core.ext.d.ts"),
						new File("typings/lib.core/lib.dom.d.ts")), //
				new File("../../jsweet-examples/src/main/jsweetdef"), //
				null, //
				false, true);
	}

	private static Pattern refPattern = Pattern.compile("^///\\p{Blank}*<.*$");

	private static void grabReferences(CompilationUnit cu) throws IOException {
		for (String s : FileUtils.readLines(cu.getFile())) {
			Matcher m = refPattern.matcher(s);
			if (m.matches()) {
				cu.addReference(s);
			}
		}
	}

	@SuppressWarnings("serial")
	private static final Map<String, String[]> IGNORED_REFERENCES = new HashMap<String, String[]>() {
		{
			put("cordova/cordova.d.ts", new String[] { "plugins/.*" });
		}
	};

	private static void parse(Context context, File f) throws IOException {
		// context.compilationUnits.stream().map((cu) -> { return
		// cu.getFile();}).
		if (context.compilationUnits.contains(new CompilationUnit(f))) {
			// logger.info("skipping: " + f);
			return;
		}
		logger.info("parsing: " + f);
		// This class is automatically generated by CUP (please generate to compile)
		TypescriptDefParser parser = TypescriptDefParser.parseFile(f);
		context.compilationUnits.add(parser.compilationUnit);
		grabReferences(parser.compilationUnit);
		for (String reference : parser.compilationUnit.getReferences()) {
			String path = Util.getLibPathFromReference(reference);
			if (path != null) {
				File dep = new File(f.getParent(), path);
				if (!dep.exists()) {
					context.reportError("dependency '" + dep + "' does not exist", (Token) null);
				} else {
					File tsDefFile = parser.compilationUnit.getFile();
					boolean ignored = isIgnoredReference(tsDefFile, path);
					if (dep.getPath().contains("..")) {
						try {
							Path currentPath = new File("").getAbsoluteFile().toPath();
							Path depPath = dep.getCanonicalFile().toPath();
							logger.debug("depPath: " + depPath);
							Path relPath = currentPath.relativize(depPath);
							if (!relPath.toString().contains("..")) {
								dep = relPath.toFile();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					logger.info("handling dependency: " + dep);
					if (ignored) {
						context.getDependenciesDefinitions().add(dep);
					} else {
						parse(context, dep);
					}
				}
			}
		}
	}

	private static boolean isIgnoredReference(File tsDefFile, String path) {

		for (Map.Entry<String, String[]> ignoredReferenceEntry : IGNORED_REFERENCES.entrySet()) {

			if (tsDefFile.getPath().endsWith(ignoredReferenceEntry.getKey())) {

				String[] ignoredPaths = ignoredReferenceEntry.getValue();
				for (String ignoredPathRegex : ignoredPaths) {
					if (Pattern.matches(ignoredPathRegex, path)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static Context translate( //
			File inputDir, //
			File outputDir, //
			String cacheDirPath, //
			boolean fetchJavadoc, //
			boolean copyTsDefs, //
			Predicate<File> libFilter, //
			Predicate<File> dependenciesFilter) throws Throwable {

		logger.info("typings directory: " + inputDir);
		logger.info("output directory: " + outputDir);

		ArrayList<File> tsDefFiles = new ArrayList<File>();
		addTsDefFiles(inputDir, tsDefFiles, libFilter);

		ArrayList<File> tsDefDependencies = new ArrayList<File>();
		addTsDefFiles(inputDir, tsDefDependencies, dependenciesFilter);

		File cacheDir = null;
		if (cacheDirPath != null) {
			cacheDir = new File(cacheDirPath);
		}

		return translate(tsDefFiles, tsDefDependencies, outputDir, cacheDir, fetchJavadoc, copyTsDefs);
	}

	public static Context translate( //
			List<File> tsDefFiles, //
			List<File> tsDefDependencies, //
			File outputDir, //
			File cacheDir, //
			boolean fetchJavadoc, //
			boolean copyTsDefs) throws Throwable {

		// comp.compile(fileObjects);
		logger.info("input files: " + tsDefFiles);
		logger.info("dependencies: " + tsDefDependencies);
		logger.info("output directory: " + outputDir.getAbsolutePath());

		outputDir.mkdirs();

		Context context = new Context(tsDefFiles, tsDefDependencies, fetchJavadoc);
		context.verbose = false;
		context.cacheDir = cacheDir;

		logger.info("all definitions: " + context.getAllDefinitions());

		for (File f : context.getAllDefinitions()) {
			parse(context, f);
		}

		translateAst(context);

		printAst(outputDir, context);

		// copies core built-in srcs
		// if (context.getLibrariesDefinitions()
		// .contains(new File(JSweetDefTranslatorConfig.TS_LIBS_DIR_NAME +
		// "/lib.core/lib.core.d.ts"))) {
		// logger.info("copying built-in core '" + getBuiltInDir() + "' -> '" +
		// outputDir);
		// FileUtils.copyDirectory(new File(getBuiltInDir()), outputDir, new
		// FileFilter() {
		//
		// @Override
		// public boolean accept(File file) {
		// return !file.getName().startsWith(".");
		// }
		// });
		// }

		// copies core packages' package-info if necessary
		// File jsweetRootPackageDir = new File(outputDir, "jsweet");
		// if (jsweetRootPackageDir.isDirectory()) {
		// for (File jsweetSubSourceDir : jsweetRootPackageDir.listFiles()) {
		// File packageInfo = new File(BUILT_IN_DIR,
		// "jsweet/" + jsweetSubSourceDir.getName() + "/package-info.java");
		// logger.info("copying package-info for '" + jsweetSubSourceDir + "'
		// from " + packageInfo);
		// FileUtils.copyFileToDirectory(packageInfo, jsweetSubSourceDir);
		// }
		// }

		// =====

		if (copyTsDefs) {
			File tsdefDir = new File(outputDir, JSweetDefTranslatorConfig.TS_LIBS_DIR_NAME);
			logger.info("copying tsdef: '" + context.getAllDefinitions() + "' -> '" + tsdefDir);
			tsdefDir.mkdirs();

			for (File tsDefFile : context.getAllDefinitions()) {

				File destDir = new File( //
						tsdefDir, //
						tsDefFile.getParentFile().getName() + "_" + tsDefFile.getName());
				FileUtils.copyFileToDirectory(tsDefFile, destDir);
				File extFile = new File(tsDefFile.getParentFile(), tsDefFile.getName().replace(".d.ts", ".ext.d.ts"));
				if (extFile.isFile() && extFile.canRead()) {
					FileUtils.copyFileToDirectory(tsDefFile, destDir);
					logger.info("ext file '" + extFile + "' copied as well");
				}
			}

			mergeExtFiles(tsdefDir);
			// createLibFile(tsdefDir);
			// mergeDomToCore(tsdefDir);
		}
		return context;
	}

	public static void printAst(File outputDir, Context context) {
		outputDir.mkdirs();
		try {
			FileUtils.cleanDirectory(outputDir);
		} catch (IOException e) {
			logger.error("did not clean output directory: " + outputDir, e);
		}
		scan((cu, scanner) -> {
			logger.info("translated " + cu);
		}, context.compilationUnits, new JavaDefModelPrinter(context, outputDir));
	}

	public static void translateAst(Context context) {

		// List<QualifiedDeclaration<TypeDeclaration>> l =
		// context.findDeclarations(TypeDeclaration.class, "*.HTMLAttributes");
		// System.out.println();

		scan(context.compilationUnits, new NameChecker(context));

		scan(context.compilationUnits, new FunctionTypeOfReplacer(context));

		scan(context.compilationUnits, new ImportedAndExportedReferenceExpander(context, false));
		scan(context.compilationUnits, new ImportedAndExportedReferenceExpander(context, true));

		scan(context.compilationUnits, new EmptyModulesCleaner(context));

		// assign module names
		scan(context.compilationUnits, new PackageOrganizer(context), new TypeKindChooser(context),
				new DeclarationBinder(context));

		context.checkConsistency();

		logger.info("dumping initial state");
		context.dump(logger);

		try {
			Util.createDependencyGraph(context);
			logger.info("dependency graph: " + context.dependencyGraph);
		} catch (IOException e) {
			logger.error("error when contructing dependency graph", e);
		}

		// scan(context.compilationUnits, new GlobalsCreator(context));

		scan(context.compilationUnits, new ModuleToTypeMerger(context));
		scan(context.compilationUnits, new ObjectTypeCreator(context), new ObjectTypeDuplicateMerger(context));
		scan(context.compilationUnits, new ConstructorInterfacesMerger(context));
		scan(context.compilationUnits, new TypeMerger(context));

		scan(context.compilationUnits, new SuperTypesMerger(context));
		// does not work (prototype clashing)
		// scan(context.compilationUnits, new
		// FunctionalParametersExpander(context));
		
		logger.info("TypeReferenceExpander");
		scan(context.compilationUnits, new TypeReferenceExpander(context));

		context.calculateArrayTypes();

		scan(context.compilationUnits, new TypeMacroReplacer(context));

		scan(context.compilationUnits, new UnionTypesEraser(context));

		// scan(context.compilationUnits, new TypeReferenceChecker(context));

		scan(context.compilationUnits, new OptionalParametersExpander(context),
				new OptionalParametersInFunctionalTypesExpander(context), new UnionTypesExpander(context),
				new StringTypeCreator(context), new FunctionalInterfacesCreator(context),
				new TypeParametersSubstitutor(context), new DynamicTypeParametersExpander(context),
				new TupleTypeCreator(context));

		// optional parameters must have been fully expanded in super classes
		scan(context.compilationUnits, new ParentMethodReturnTypeSusbtitutor(context));

		scan(context.compilationUnits, new GlobalsCreator(context), new FieldTypeFunctionInjector(context));

		// binds declarations again to grab newly created inner types
		scan(context.compilationUnits, new DeclarationBinder(context));

		scan(context.compilationUnits, new FactoryMethodsCreator(context));

		// run type expansion again for partial names that would reference an
		// import
		scan(context.compilationUnits, new TypeReferenceExpander(context));

		// context.dump(System.out);
		scan(context.compilationUnits, new NameAdapter(context), new DeclarationBinder(context));
		// TODO : this should be the last and it should ignore java.util.* and
		// so on
		scan(context.compilationUnits, new TypeReferenceChecker(context));

		scan(context.compilationUnits, new IterableInjector(context));

		scan(context.compilationUnits, new FunctionKindAdapter(context));

		scan(context.compilationUnits, new UnionInterfacesCreator(context));

		scan(context.compilationUnits, new EmptyConstructorAdder(context), new SerializableHandler(context));

		scan(context.compilationUnits, new ConstructorTypeReferenceReplacer(context));

		scan(context.compilationUnits, new DuplicateMethodsCleaner(context));

		scan(context.compilationUnits, new ForEachClashRemover(context));

		if (context.fetchJavadoc) {
			scan(context.compilationUnits, new DocFiller(context));
		}

		// l = context.findDeclarations(TypeDeclaration.class,
		// "*.HTMLAttributes");
		// System.out.println();

		context.checkConsistency();

		logger.info("dumping final state");
		context.dump(logger);

	}

	private static void mergeExtFiles(File dir) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				mergeExtFiles(f);
			} else {
				if (f.getName().endsWith(".ext.d.ts")) {
					logger.info("merging ext file: " + f);
					File target = new File(dir, f.getName().replace(".ext.d.ts", ".d.ts"));
					if (target.exists()) {
						try {
							FileUtils.writeByteArrayToFile(target, FileUtils.readFileToByteArray(f), true);
							FileUtils.deleteQuietly(f);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
