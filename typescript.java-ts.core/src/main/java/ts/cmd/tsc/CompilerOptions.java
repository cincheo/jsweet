package ts.cmd.tsc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ts.cmd.AbstractOptions;
import ts.utils.BooleanUtils;

/**
 * Instructs the TypeScript compiler how to compile .ts files.
 * 
 * @see http://json.schemastore.org/tsconfig
 * @se https://www.typescriptlang.org/docs/handbook/compiler-html
 *
 */
public class CompilerOptions extends AbstractOptions {

	private Boolean allowJs;
	private Boolean allowSyntheticDefaultImports;
	private Boolean allowUnreachableCode;
	private Boolean allowUnusedLabels;
	private String charset;
	private Boolean declaration;
	private Boolean diagnostics;
	private Boolean emitBOM;
	private Boolean emitDecoratorMetadata;
	private Boolean experimentalDecorators;
	private Boolean forceConsistentCasingInFileNames;
	private Boolean help;
	private Boolean inlineSourceMap;
	private Boolean inlineSources;
	private Boolean init;
	private Boolean isolatedModules;
	private String jsx;
	private Boolean listEmittedFiles;
	private Boolean listFiles;
	private String locale;
	private String mapRoot;
	private String module;
	private String moduleResolution;
	private String newLine;
	private Boolean noEmit;
	private Boolean noEmitHelpers;
	private Boolean noEmitOnError;
	private Boolean noFallthroughCasesInSwitch;
	private Boolean noImplicitAny;
	private Boolean noImplicitReturns;
	private Boolean noImplicitUseStrict;
	private Boolean noLib;
	private Boolean noResolve;
	private String out;
	private String outDir;
	private String outFile;
	private Map<String, List<String>> paths;
	private Boolean preserveConstEnums;
	private Boolean pretty;
	private String project;
	private String reactNamespace;
	private Boolean removeComments;
	private String rootDir;
	private List<String> rootDirs;
	private Boolean skipDefaultLibCheck;
	private Boolean sourceMap;
	private String sourceRoot;
	private Boolean strictNullChecks;
	private Boolean stripInternal;
	private Boolean suppressExcessPropertyErrors;
	private Boolean suppressImplicitAnyIndexErrors;
	private String target;
	private Boolean traceResolution;
	private Boolean version;
	private Boolean watch;

	private List<Plugin> plugins;

	public CompilerOptions() {
	}

	public CompilerOptions(CompilerOptions options) {
		this.setAllowJs(options.allowJs);
		this.setAllowSyntheticDefaultImports(options.allowSyntheticDefaultImports);
		this.setAllowUnreachableCode(options.allowUnreachableCode);
		this.setAllowUnusedLabels(options.allowUnusedLabels);
		this.setCharset(options.charset);
		this.setDeclaration(options.declaration);
		this.setDiagnostics(options.diagnostics);
		this.setEmitBOM(options.emitBOM);
		this.setEmitDecoratorMetadata(options.emitDecoratorMetadata);
		this.setExperimentalDecorators(options.experimentalDecorators);
		this.setForceConsistentCasingInFileNames(options.forceConsistentCasingInFileNames);
		this.setHelp(options.help);
		this.setInlineSourceMap(options.inlineSourceMap);
		this.setInlineSources(options.inlineSources);
		this.setInit(options.init);
		this.setIsolatedModules(options.isolatedModules);
		this.setJsx(options.jsx);
		this.setListEmittedFiles(options.listEmittedFiles);
		this.setListFiles(options.listFiles);
		this.setLocale(options.locale);
		this.setMapRoot(options.mapRoot);
		this.setModule(options.module);
		this.setModuleResolution(options.moduleResolution);
		this.setNewLine(options.newLine);
		this.setNoEmit(options.noEmit);
		this.setNoEmitHelpers(options.noEmitHelpers);
		this.setNoEmitOnError(options.noEmitOnError);
		this.setNoFallthroughCasesInSwitch(options.noFallthroughCasesInSwitch);
		this.setNoImplicitAny(options.noImplicitAny);
		this.setNoImplicitReturns(options.noImplicitReturns);
		this.setNoImplicitUseStrict(options.noImplicitUseStrict);
		this.setNoLib(options.noLib);
		this.setNoResolve(options.noResolve);
		this.setOut(options.out);
		this.setOutDir(options.outDir);
		this.setOutFile(options.outFile);
		this.setPreserveConstEnums(options.preserveConstEnums);
		this.setPretty(options.pretty);
		this.setProject(options.project);
		this.setReactNamespace(options.reactNamespace);
		this.setRemoveComments(options.removeComments);
		this.setRootDir(options.rootDir);
		this.setSkipDefaultLibCheck(options.skipDefaultLibCheck);
		this.setSourceMap(options.sourceMap);
		this.setSourceRoot(options.sourceRoot);
		this.setStrictNullChecks(options.strictNullChecks);
		this.setStripInternal(options.stripInternal);
		this.setSuppressExcessPropertyErrors(options.suppressExcessPropertyErrors);
		this.setSuppressImplicitAnyIndexErrors(options.suppressImplicitAnyIndexErrors);
		this.setTarget(options.target);
		this.setTraceResolution(options.traceResolution);
		this.setVersion(options.version);
		this.setWatch(options.watch);
	}

	/**
	 * Returns true if allow JavaScript files to be compiled and false
	 * otherwise.
	 * 
	 * @return true if allow JavaScript files to be compiled and false
	 *         otherwise.
	 */
	public Boolean isAllowJs() {
		return BooleanUtils.toBoolean(allowJs);
	}

	/**
	 * Allow JavaScript files to be compiled.
	 * 
	 * @param allowJs
	 */
	public void setAllowJs(Boolean allowJs) {
		this.allowJs = allowJs;
	}

	/**
	 * Allow default imports from modules with no default export. This does not
	 * affect code emit, just typechecking.
	 * 
	 * @return
	 */
	public boolean isAllowSyntheticDefaultImports() {
		return BooleanUtils.toBoolean(allowSyntheticDefaultImports);
	}

	/**
	 * Allow default imports from modules with no default export. This does not
	 * affect code emit, just typechecking.
	 * 
	 * @param allowSyntheticDefaultImports
	 */
	public void setAllowSyntheticDefaultImports(Boolean allowSyntheticDefaultImports) {
		this.allowSyntheticDefaultImports = allowSyntheticDefaultImports;
	}

	/**
	 * Do not report errors on unreachable code.
	 * 
	 * @return
	 */
	public boolean isAllowUnreachableCode() {
		return BooleanUtils.toBoolean(allowUnreachableCode);
	}

	/**
	 * Do not report errors on unreachable code.
	 * 
	 * @param allowUnreachableCode
	 */
	public void setAllowUnreachableCode(Boolean allowUnreachableCode) {
		this.allowUnreachableCode = allowUnreachableCode;
	}

	/**
	 * Do not report errors on unused labels.
	 * 
	 * @return
	 */
	public boolean isAllowUnusedLabels() {
		return BooleanUtils.toBoolean(allowUnusedLabels);
	}

	/**
	 * Do not report errors on unused labels.
	 * 
	 * @param allowUnusedLabels
	 */
	public void setAllowUnusedLabels(Boolean allowUnusedLabels) {
		this.allowUnusedLabels = allowUnusedLabels;
	}

	/**
	 * Returns the character set of the input files.
	 * 
	 * @return the character set of the input files.
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * The character set of the input files.
	 * 
	 * @param charset
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * Returns true of generates corresponding d.ts files and false otherwise.
	 * 
	 * @return true of generates corresponding d.ts files and false otherwise.
	 */
	public boolean isDeclaration() {
		return BooleanUtils.toBoolean(declaration);
	}

	/**
	 * Set to true of generates corresponding d.ts files and false otherwise.
	 * 
	 * @param declaration
	 */
	public void setDeclaration(Boolean declaration) {
		this.declaration = declaration;
	}

	/**
	 * Show diagnostic information.
	 * 
	 * @return
	 */
	public boolean isDiagnostics() {
		return BooleanUtils.toBoolean(diagnostics);
	}

	/**
	 * Show diagnostic information.
	 * 
	 * @param diagnostics
	 */
	public void setDiagnostics(Boolean diagnostics) {
		this.diagnostics = diagnostics;
	}

	/**
	 * Emit a UTF-8 Byte Order Mark (BOM) in the beginning of output files.
	 * 
	 * @return
	 */
	public boolean isEmitBOM() {
		return BooleanUtils.toBoolean(emitBOM);
	}

	/**
	 * Emit a UTF-8 Byte Order Mark (BOM) in the beginning of output files.
	 * 
	 * @param emitBOM
	 */
	public void setEmitBOM(Boolean emitBOM) {
		this.emitBOM = emitBOM;
	}

	/**
	 * Emit design-type metadata for decorated declarations in source. See issue
	 * https://github.com/Microsoft/TypeScript/issues/2577 for details.
	 * 
	 * @return
	 */
	public boolean isEmitDecoratorMetadata() {
		return BooleanUtils.toBoolean(emitDecoratorMetadata);
	}

	/**
	 * Emit design-type metadata for decorated declarations in source. See issue
	 * https://github.com/Microsoft/TypeScript/issues/2577 for details.
	 * 
	 * @param emitDecoratorMetadata
	 */
	public void setEmitDecoratorMetadata(Boolean emitDecoratorMetadata) {
		this.emitDecoratorMetadata = emitDecoratorMetadata;
	}

	/**
	 * Enables experimental support for ES7 decorators.
	 * 
	 * @return
	 */
	public boolean isExperimentalDecorators() {
		return BooleanUtils.toBoolean(experimentalDecorators);
	}

	/**
	 * Enables experimental support for ES7 decorators.
	 * 
	 * @param experimentalDecorators
	 */
	public void setExperimentalDecorators(Boolean experimentalDecorators) {
		this.experimentalDecorators = experimentalDecorators;
	}

	/**
	 * Disallow inconsistently-cased references to the same file.
	 * 
	 * @return
	 */
	public boolean isForceConsistentCasingInFileNames() {
		return BooleanUtils.toBoolean(forceConsistentCasingInFileNames);
	}

	/**
	 * Disallow inconsistently-cased references to the same file.
	 * 
	 * @param forceConsistentCasingInFileNames
	 */
	public void setForceConsistentCasingInFileNames(Boolean forceConsistentCasingInFileNames) {
		this.forceConsistentCasingInFileNames = forceConsistentCasingInFileNames;
	}

	/**
	 * Print help message.
	 * 
	 * @return
	 */
	public boolean isHelp() {
		return BooleanUtils.toBoolean(help);
	}

	/**
	 * Print help message.
	 * 
	 * @param help
	 */
	public void setHelp(Boolean help) {
		this.help = help;
	}

	/**
	 * Emit a single file with source maps instead of having a separate file.
	 * 
	 * @return
	 */
	public boolean isInlineSourceMap() {
		return BooleanUtils.toBoolean(inlineSourceMap);
	}

	/**
	 * Emit a single file with source maps instead of having a separate file.
	 * 
	 * @param inlineSourceMap
	 */
	public void setInlineSourceMap(Boolean inlineSourceMap) {
		this.inlineSourceMap = inlineSourceMap;
	}

	/**
	 * Emit the source alongside the sourcemaps within a single file; requires
	 * --inlineSourceMap or --sourceMap to be set.
	 * 
	 * @return
	 */
	public boolean isInlineSources() {
		return BooleanUtils.toBoolean(inlineSources);
	}

	/**
	 * Emit the source alongside the sourcemaps within a single file; requires
	 * --inlineSourceMap or --sourceMap to be set.
	 * 
	 * @param inlineSources
	 */
	public void setInlineSources(Boolean inlineSources) {
		this.inlineSources = inlineSources;
	}

	/**
	 * Initializes a TypeScript project and creates a tsconfig.json file.
	 * 
	 * @return
	 */
	public boolean isInit() {
		return BooleanUtils.toBoolean(init);
	}

	/**
	 * Initializes a TypeScript project and creates a tsconfig.json file.
	 * 
	 * @param init
	 */
	public void setInit(Boolean init) {
		this.init = init;
	}

	/**
	 * Unconditionally emit imports for unresolved files.
	 * 
	 * @return
	 */
	public boolean isIsolatedModules() {
		return BooleanUtils.toBoolean(isolatedModules);
	}

	/**
	 * Unconditionally emit imports for unresolved files.
	 * 
	 * @param isolatedModules
	 */
	public void setIsolatedModules(Boolean isolatedModules) {
		this.isolatedModules = isolatedModules;
	}

	/**
	 * Support JSX in ‘.tsx’ files: 'React' or 'Preserve'.
	 * 
	 * @see https://www.typescriptlang.org/docs/handbook/jsx.html
	 * 
	 * @return
	 */
	public String getJsx() {
		return jsx;
	}

	/**
	 * Support JSX in ‘.tsx’ files: 'React' or 'Preserve'.
	 * 
	 * @see https://www.typescriptlang.org/docs/handbook/jsx.html
	 * 
	 * @param jsx
	 */
	public void setJsx(String jsx) {
		this.jsx = jsx;
	}

	/**
	 * Print names of generated files part of the compilation.
	 * 
	 * @return
	 */
	public boolean isListEmittedFiles() {
		return BooleanUtils.toBoolean(listEmittedFiles);
	}

	/**
	 * Print names of generated files part of the compilation.
	 * 
	 * @param listEmittedFiles
	 */
	public void setListEmittedFiles(Boolean listEmittedFiles) {
		this.listEmittedFiles = listEmittedFiles;
	}

	/**
	 * Returns Print names of files part of the compilation.
	 * 
	 * @return
	 * 
	 */
	public boolean isListFiles() {
		return BooleanUtils.toBoolean(listFiles);
	}

	/**
	 * Set Print names of files part of the compilation.
	 * 
	 * @param listFiles
	 */
	public void setListFiles(Boolean listFiles) {
		this.listFiles = listFiles;
	}

	/**
	 * The locale to use to show error messages, e.g. en-us.
	 * 
	 * @return
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * The locale to use to show error messages, e.g. en-us.
	 * 
	 * @param locale
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Specifies the location where debugger should locate map files instead of
	 * generated locations. Use this flag if the .map files will be located at
	 * run-time in a different location than than the .js files. The location
	 * specified will be embedded in the sourceMap to direct the debugger where
	 * the map files where be located.
	 * 
	 * @return
	 */
	public String getMapRoot() {
		return mapRoot;
	}

	/**
	 * Specifies the location where debugger should locate map files instead of
	 * generated locations. Use this flag if the .map files will be located at
	 * run-time in a different location than than the .js files. The location
	 * specified will be embedded in the sourceMap to direct the debugger where
	 * the map files where be located.
	 * 
	 * @param mapRoot
	 */
	public void setMapRoot(String mapRoot) {
		this.mapRoot = mapRoot;
	}

	/**
	 * Specify module code generation: 'none', 'commonjs', 'amd', 'system',
	 * 'umd', 'es6', or 'es2015'. ► Only 'amd' and 'system' can be used in
	 * conjunction with --outFile. ► 'es6' and 'es2015' values may not be used
	 * when targeting ES5 or lower.
	 * 
	 * @return
	 */
	public String getModule() {
		return module;
	}

	/**
	 * Specify module code generation: 'none', 'commonjs', 'amd', 'system',
	 * 'umd', 'es6', or 'es2015'. ► Only 'amd' and 'system' can be used in
	 * conjunction with --outFile. ► 'es6' and 'es2015' values may not be used
	 * when targeting ES5 or lower.
	 * 
	 * @param module
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * Determine how modules get resolved. Either 'node' for Node.js/io.js style
	 * resolution, or 'classic' (default). See
	 * https://www.typescriptlang.org/docs/handbook/module-resolution.html
	 * documentation for more details.
	 * 
	 * @return
	 */
	public String getModuleResolution() {
		return moduleResolution;
	}

	/**
	 * Determine how modules get resolved. Either 'node' for Node.js/io.js style
	 * resolution, or 'classic' (default). See
	 * https://www.typescriptlang.org/docs/handbook/module-resolution.html
	 * documentation for more details.
	 * 
	 * @param moduleResolution
	 */
	public void setModuleResolution(String moduleResolution) {
		this.moduleResolution = moduleResolution;
	}

	/**
	 * Use the specified end of line sequence to be used when emitting files:
	 * 'crlf' (windows) or 'lf' (unix).”
	 * 
	 * @return
	 */
	public String getNewLine() {
		return newLine;
	}

	/**
	 * Use the specified end of line sequence to be used when emitting files:
	 * 'crlf' (windows) or 'lf' (unix).”
	 * 
	 * @param newLine
	 */
	public void setNewLine(String newLine) {
		this.newLine = newLine;
	}

	/**
	 * Do not emit outputs.
	 * 
	 * @return
	 */
	public boolean isNoEmit() {
		return BooleanUtils.toBoolean(noEmit);
	}

	/**
	 * Do not emit outputs.
	 * 
	 * @param noEmit
	 */
	public void setNoEmit(Boolean noEmit) {
		this.noEmit = noEmit;
	}

	/**
	 * Do not generate custom helper functions like __extends in compiled
	 * output.
	 * 
	 * @return
	 */
	public boolean isNoEmitHelpers() {
		return BooleanUtils.toBoolean(noEmitHelpers);
	}

	/**
	 * Do not generate custom helper functions like __extends in compiled
	 * output.
	 * 
	 * @param noEmitHelpers
	 */
	public void setNoEmitHelpers(Boolean noEmitHelpers) {
		this.noEmitHelpers = noEmitHelpers;
	}

	/**
	 * Do not emit outputs if any errors were reported.
	 * 
	 * @return
	 */
	public boolean isNoEmitOnError() {
		return BooleanUtils.toBoolean(noEmitOnError);
	}

	/**
	 * Do not emit outputs if any errors were reported.
	 * 
	 * @param noEmitOnError
	 */
	public void setNoEmitOnError(Boolean noEmitOnError) {
		this.noEmitOnError = noEmitOnError;
	}

	/**
	 * Report errors for fallthrough cases in switch statement.
	 * 
	 * @return
	 */
	public boolean isNoFallthroughCasesInSwitch() {
		return BooleanUtils.toBoolean(noFallthroughCasesInSwitch);
	}

	/**
	 * Report errors for fallthrough cases in switch statement.
	 * 
	 * @param noFallthroughCasesInSwitch
	 */
	public void setNoFallthroughCasesInSwitch(Boolean noFallthroughCasesInSwitch) {
		this.noFallthroughCasesInSwitch = noFallthroughCasesInSwitch;
	}

	/**
	 * Raise error on expressions and declarations with an implied ‘any’ type.
	 * 
	 * @return
	 */
	public boolean isNoImplicitAny() {
		return BooleanUtils.toBoolean(noImplicitAny);
	}

	/**
	 * Raise error on expressions and declarations with an implied ‘any’ type.
	 * 
	 * @param noImplicitAny
	 */
	public void setNoImplicitAny(Boolean noImplicitAny) {
		this.noImplicitAny = noImplicitAny;
	}

	/**
	 * Report error when not all code paths in function return a value.
	 * 
	 * @return
	 */
	public boolean isNoImplicitReturns() {
		return BooleanUtils.toBoolean(noImplicitReturns);
	}

	/**
	 * Report error when not all code paths in function return a value.
	 * 
	 * @param noImplicitReturns
	 */
	public void setNoImplicitReturns(Boolean noImplicitReturns) {
		this.noImplicitReturns = noImplicitReturns;
	}

	/**
	 * Do not emit "use strict" directives in module output.
	 * 
	 * @return
	 */
	public boolean isNoImplicitUseStrict() {
		return BooleanUtils.toBoolean(noImplicitUseStrict);
	}

	/**
	 * Do not emit "use strict" directives in module output.
	 * 
	 * @param noImplicitUseStrict
	 */
	public void setNoImplicitUseStrict(Boolean noImplicitUseStrict) {
		this.noImplicitUseStrict = noImplicitUseStrict;
	}

	/**
	 * Do not include the default library file (lib.d.ts).
	 * 
	 * @return
	 */
	public boolean isNoLib() {
		return BooleanUtils.toBoolean(noLib);
	}

	/**
	 * Do not include the default library file (lib.d.ts).
	 * 
	 * @param noLib
	 */
	public void setNoLib(Boolean noLib) {
		this.noLib = noLib;
	}

	/**
	 * Do not add triple-slash references or module import targets to the list
	 * of compiled files.
	 * 
	 * @return
	 */
	public boolean isNoResolve() {
		return BooleanUtils.toBoolean(noResolve);
	}

	/**
	 * Do not add triple-slash references or module import targets to the list
	 * of compiled files.
	 * 
	 * @param noResolve
	 */
	public void setNoResolve(Boolean noResolve) {
		this.noResolve = noResolve;
	}

	/**
	 * Same thing than outFile but deprectaed.
	 * 
	 * @return
	 */
	public String getOut() {
		return out;
	}

	/**
	 * Same thing than outFile but deprectaed.
	 * 
	 * @param out
	 */
	public void setOut(String out) {
		this.out = out;
	}

	/**
	 * Redirect output structure to the directory.
	 * 
	 * @return
	 */
	public String getOutDir() {
		return outDir;
	}

	/**
	 * Redirect output structure to the directory.
	 * 
	 * @param outDir
	 */
	public void setOutDir(String outDir) {
		this.outDir = outDir;
	}

	/**
	 * Concatenate and emit output to single file. The order of concatenation is
	 * determined by the list of files passed to the compiler on the command
	 * line along with triple-slash references and imports. See output file
	 * order documentation for more details.
	 * 
	 * @return
	 */
	public String getOutFile() {
		return outFile;
	}

	/**
	 * Concatenate and emit output to single file. The order of concatenation is
	 * determined by the list of files passed to the compiler on the command
	 * line along with triple-slash references and imports. See output file
	 * order documentation for more details.
	 * 
	 * @param outFile
	 */
	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	/**
	 * Specify path mapping to be computed relative to baseUrl option.
	 * 
	 * @return key patterns to which paths are mapped.
	 */
	public Set<String> getPathsKeys() {
		if (paths == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(paths.keySet());
	}

	/**
	 * Specify path mapping to be computed relative to baseUrl option.
	 * 
	 * @param pathsKey
	 *            a path key pattern.
	 * @return paths mapped to the key pattern.
	 */
	public List<String> getPathsKeyValues(String pathsKey) {
		if (paths == null) {
			return Collections.emptyList();
		}
		List<String> values = paths.get(pathsKey);
		if (values == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(values);
	}

	/**
	 * Specify path mapping to be computed relative to baseUrl option.
	 * 
	 * @param paths
	 */
	public void setPaths(Map<String, List<String>> paths) {
		Map<String, List<String>> pathsCopy = new HashMap<>(paths.size());
		for (Map.Entry<String, List<String>> entry : paths.entrySet()) {
			pathsCopy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		this.paths = pathsCopy;
	}

	/**
	 * Do not erase const enum declarations in generated code. See const enums
	 * https://github.com/Microsoft/TypeScript/blob/master/doc/spec.md#94-constant-enum-declarations
	 * documentation for more details.
	 * 
	 * @return
	 */
	public boolean isPreserveConstEnums() {
		return BooleanUtils.toBoolean(preserveConstEnums);
	}

	/**
	 * Do not erase const enum declarations in generated code. See const enums
	 * https://github.com/Microsoft/TypeScript/blob/master/doc/spec.md#94-constant-enum-declarations
	 * documentation for more details.
	 * 
	 * @param preserveConstEnums
	 */
	public void setPreserveConstEnums(Boolean preserveConstEnums) {
		this.preserveConstEnums = preserveConstEnums;
	}

	/**
	 * Stylize errors and messages using color and context.
	 * 
	 * @return
	 */
	public boolean isPretty() {
		return BooleanUtils.toBoolean(pretty);
	}

	/**
	 * Stylize errors and messages using color and context.
	 * 
	 * @param pretty
	 */
	public void setPretty(Boolean pretty) {
		this.pretty = pretty;
	}

	/**
	 * Compile the project in the given directory. The directory needs to
	 * contain a tsconfig.json file to direct compilation. See tsconfig.json
	 * documentation for more details.
	 * 
	 * @return
	 */
	public String getProject() {
		return project;
	}

	/**
	 * Compile the project in the given directory. The directory needs to
	 * contain a tsconfig.json file to direct compilation. See tsconfig.json
	 * documentation for more details.
	 * 
	 * @param project
	 */
	public void setProject(String project) {
		this.project = project;
	}

	/**
	 * Specifies the object invoked for createElement and __spread when
	 * targeting ‘react’ JSX emit.
	 * 
	 * @return
	 */
	public String getReactNamespace() {
		return reactNamespace;
	}

	/**
	 * Specifies the object invoked for createElement and __spread when
	 * targeting ‘react’ JSX emit.
	 * 
	 * @param reactNamespace
	 */
	public void setReactNamespace(String reactNamespace) {
		this.reactNamespace = reactNamespace;
	}

	/**
	 * Remove all comments except copy-right header comments beginning with /*!
	 * 
	 * @return
	 */
	public boolean isRemoveComments() {
		return BooleanUtils.toBoolean(removeComments);
	}

	/**
	 * Remove all comments except copy-right header comments beginning with /*!
	 * 
	 * @param removeComments
	 */
	public void setRemoveComments(Boolean removeComments) {
		this.removeComments = removeComments;
	}

	/**
	 * Specifies the root directory of input files. Only use to control the
	 * output directory structure with --outDir.
	 * 
	 * @return
	 */
	public String getRootDir() {
		return rootDir;
	}

	/**
	 * Specifies the root directory of input files. Only use to control the
	 * output directory structure with --outDir.
	 * 
	 * @param rootDir
	 */
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	/**
	 * Specify list of root directory to be used when resolving modules.
	 * 
	 * @return
	 */
	public List<String> getRootDirs() {
		if (rootDirs == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(rootDirs);
	}

	/**
	 * Specify list of root directory to be used when resolving modules.
	 * 
	 * @param rootDirs
	 */
	public void setRootDirs(List<String> rootDirs) {
		this.rootDirs = new ArrayList<>(rootDirs);
	}

	/**
	 * Don’t check a user-defined default lib file’s valitidy.
	 * 
	 * @return
	 */
	public boolean isSkipDefaultLibCheck() {
		return BooleanUtils.toBoolean(skipDefaultLibCheck);
	}

	/**
	 * Don’t check a user-defined default lib file’s valitidy.
	 * 
	 * @param skipDefaultLibCheck
	 */
	public void setSkipDefaultLibCheck(Boolean skipDefaultLibCheck) {
		this.skipDefaultLibCheck = skipDefaultLibCheck;
	}

	/**
	 * Generates corresponding '.map' file.
	 * 
	 * @return
	 */
	public boolean isSourceMap() {
		return BooleanUtils.toBoolean(sourceMap);
	}

	/**
	 * Generates corresponding '.map' file.
	 * 
	 * @param sourceMap
	 */
	public void setSourceMap(Boolean sourceMap) {
		this.sourceMap = sourceMap;
	}

	/**
	 * Specifies the location where debugger should locate TypeScript files
	 * instead of source locations. Use this flag if the sources will be located
	 * at run-time in a different location than that at design-time. The
	 * location specified will be embedded in the sourceMap to direct the
	 * debugger where the source files where be located.
	 * 
	 * @return
	 */
	public String getSourceRoot() {
		return sourceRoot;
	}

	/**
	 * Specifies the location where debugger should locate TypeScript files
	 * instead of source locations. Use this flag if the sources will be located
	 * at run-time in a different location than that at design-time. The
	 * location specified will be embedded in the sourceMap to direct the
	 * debugger where the source files where be located.
	 * 
	 * @param sourceRoot
	 */
	public void setSourceRoot(String sourceRoot) {
		this.sourceRoot = sourceRoot;
	}

	/**
	 * In strict null checking mode, the null and undefined values are not in
	 * the domain of every type and are only assignable to themselves and any
	 * (the one exception being that undefined is also assignable to void).
	 * 
	 * @return
	 */
	public boolean isStrictNullChecks() {
		return BooleanUtils.toBoolean(strictNullChecks);
	}

	/**
	 * In strict null checking mode, the null and undefined values are not in
	 * the domain of every type and are only assignable to themselves and any
	 * (the one exception being that undefined is also assignable to void).
	 * 
	 * @param strictNullChecks
	 */
	public void setStrictNullChecks(Boolean strictNullChecks) {
		this.strictNullChecks = strictNullChecks;
	}

	/**
	 * Do not emit declarations for code that has an @internal JSDoc annotation.
	 * 
	 * @return
	 */
	public boolean isStripInternal() {
		return BooleanUtils.toBoolean(stripInternal);
	}

	/**
	 * Do not emit declarations for code that has an @internal JSDoc annotation.
	 * 
	 * @param stripInternal
	 */
	public void setStripInternal(Boolean stripInternal) {
		this.stripInternal = stripInternal;
	}

	/**
	 * Suppress excess property checks for object literals.
	 * 
	 * @return
	 */
	public boolean isSuppressExcessPropertyErrors() {
		return BooleanUtils.toBoolean(suppressExcessPropertyErrors);
	}

	/**
	 * Suppress excess property checks for object literals.
	 * 
	 * @param suppressExcessPropertyErrors
	 */
	public void setSuppressExcessPropertyErrors(Boolean suppressExcessPropertyErrors) {
		this.suppressExcessPropertyErrors = suppressExcessPropertyErrors;
	}

	/**
	 * Suppress --noImplicitAny errors for indexing objects lacking index
	 * signatures. See issue #1232
	 * https://github.com/Microsoft/TypeScript/issues/1232#issuecomment-64510362
	 * for more details.
	 * 
	 * @return
	 */
	public boolean isSuppressImplicitAnyIndexErrors() {
		return BooleanUtils.toBoolean(suppressImplicitAnyIndexErrors);
	}

	/**
	 * Suppress --noImplicitAny errors for indexing objects lacking index
	 * signatures. See issue #1232
	 * https://github.com/Microsoft/TypeScript/issues/1232#issuecomment-64510362
	 * for more details.
	 * 
	 * @param suppressImplicitAnyIndexErrors
	 */
	public void setSuppressImplicitAnyIndexErrors(Boolean suppressImplicitAnyIndexErrors) {
		this.suppressImplicitAnyIndexErrors = suppressImplicitAnyIndexErrors;
	}

	/**
	 * Specify ECMAScript target version: 'es3' (default), 'es5', or 'es6'.
	 * 
	 * @return
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Specify ECMAScript target version: 'es3' (default), 'es5', or 'es6'.
	 * 
	 * @param target
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Report module resolution log messages.
	 * 
	 * @return
	 */
	public boolean isTraceResolution() {
		return BooleanUtils.toBoolean(traceResolution);
	}

	/**
	 * Report module resolution log messages.
	 * 
	 * @param traceResolution
	 */
	public void setTraceResolution(Boolean traceResolution) {
		this.traceResolution = traceResolution;
	}

	/**
	 * Print the compiler’s version.
	 * 
	 * @return
	 */
	public boolean isVersion() {
		return BooleanUtils.toBoolean(version);
	}

	/**
	 * Print the compiler’s version.
	 * 
	 * @param version
	 */
	public void setVersion(Boolean version) {
		this.version = version;
	}

	/**
	 * Run the compiler in watch mode. Watch input files and trigger
	 * recompilation on changes.
	 * 
	 * @param watch
	 */
	public void setWatch(Boolean watch) {
		this.watch = watch;
	}

	/**
	 * Run the compiler in watch mode. Watch input files and trigger
	 * recompilation on changes.
	 * 
	 * @return
	 */
	public Boolean isWatch() {
		return BooleanUtils.toBoolean(watch);
	}

	public List<Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<Plugin> plugins) {
		this.plugins = plugins;
	}

	public void fillOptions(List<String> args) {
		if (isHelp()) {
			args.add("--help");
			return;
		}
		if (isVersion()) {
			args.add("--version");
			return;
		}
		fillOption("--allowJs", isAllowJs(), args);
		fillOption("--allowSyntheticDefaultImports", isAllowSyntheticDefaultImports(), args);
		fillOption("--allowUnreachableCode", isAllowUnreachableCode(), args);
		fillOption("--allowUnusedLabels", isAllowUnusedLabels(), args);
		fillOption("--charset", getCharset(), args);
		fillOption("--declaration", isDeclaration(), args);
		fillOption("--diagnostics", isDiagnostics(), args);
		fillOption("--emitBOM", isEmitBOM(), args);
		fillOption("--emitDecoratorMetadata", isEmitDecoratorMetadata(), args);
		fillOption("--experimentalDecorators", isExperimentalDecorators(), args);
		fillOption("--forceConsistentCasingInFileNames", isForceConsistentCasingInFileNames(), args);
		fillOption("--inlineSourceMap", isInlineSourceMap(), args);
		fillOption("--inlineSources", isInlineSources(), args);
		fillOption("--init", isInit(), args);
		fillOption("--isolatedModules", isIsolatedModules(), args);
		fillOption("--jsx", getJsx(), args);
		fillOption("--listEmittedFiles", isListEmittedFiles(), args);
		fillOption("--listFiles", isListFiles(), args);
		fillOption("--locale", getLocale(), args);
		fillOption("--mapRoot", getMapRoot(), args);
		fillOption("--module", getModule(), args);
		fillOption("--moduleResolution", getModuleResolution(), args);
		fillOption("--newLine", getNewLine(), args);
		fillOption("--noEmit", isNoEmit(), args);
		fillOption("--noEmitHelpers", isNoEmitHelpers(), args);
		fillOption("--noEmitOnError", isNoEmitOnError(), args);
		fillOption("--noEmitOnError", isNoEmitOnError(), args);
		fillOption("--noFallthroughCasesInSwitch", isNoFallthroughCasesInSwitch(), args);
		fillOption("--noImplicitAny", isNoImplicitAny(), args);
		fillOption("--noImplicitReturns", isNoImplicitReturns(), args);
		fillOption("--noImplicitUseStrict", isNoImplicitUseStrict(), args);
		fillOption("--noLib", isNoLib(), args);
		fillOption("--noResolve", isNoResolve(), args);
		fillOption("--out", getOut(), args);
		fillOption("--outDir", getOutDir(), args);
		fillOption("--outFile", getOutFile(), args);
		fillOption("--preserveConstEnums", isPreserveConstEnums(), args);
		fillOption("--pretty", isPretty(), args);
		fillOption("--project", getProject(), args);
		fillOption("--reactNamespace", getReactNamespace(), args);
		fillOption("--removeComments", isRemoveComments(), args);
		fillOption("--rootDir", getRootDir(), args);
		fillOption("--skipDefaultLibCheck", isSkipDefaultLibCheck(), args);
		fillOption("--sourceMap", isSourceMap(), args);
		fillOption("--strictNullChecks", isStrictNullChecks(), args);
		fillOption("--stripInternal", isStripInternal(), args);
		fillOption("--suppressExcessPropertyErrors", isSuppressExcessPropertyErrors(), args);
		fillOption("--suppressImplicitAnyIndexErrors", isSuppressImplicitAnyIndexErrors(), args);
		fillOption("--target", getTarget(), args);
		fillOption("--traceResolution", isTraceResolution(), args);
		fillOption("--version", isVersion(), args);
		fillOption("--watch", isWatch(), args);
	}

}
