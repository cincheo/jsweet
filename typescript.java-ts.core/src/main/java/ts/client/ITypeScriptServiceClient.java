/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import ts.TypeScriptException;
import ts.client.codefixes.CodeAction;
import ts.client.compileonsave.CompileOnSaveAffectedFileListSingleProject;
import ts.client.completions.CompletionEntry;
import ts.client.completions.CompletionEntryDetails;
import ts.client.completions.ICompletionEntryFactory;
import ts.client.configure.ConfigureRequestArguments;
import ts.client.diagnostics.DiagnosticEvent;
import ts.client.diagnostics.DiagnosticEventBody;
import ts.client.installtypes.IInstallTypesListener;
import ts.client.jsdoc.TextInsertion;
import ts.client.navbar.NavigationBarItem;
import ts.client.navto.NavtoItem;
import ts.client.occurrences.OccurrencesResponseItem;
import ts.client.projectinfo.ProjectInfo;
import ts.client.quickinfo.QuickInfo;
import ts.client.refactors.ApplicableRefactorInfo;
import ts.client.refactors.RefactorEditInfo;
import ts.client.references.ReferencesResponseBody;
import ts.client.rename.RenameResponseBody;
import ts.client.signaturehelp.SignatureHelpItems;
import ts.cmd.tsc.CompilerOptions;

/**
 * TypeScript client API which communicates with tsserver.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/client.ts
 *
 */
public interface ITypeScriptServiceClient {

	/**
	 * Open the given file name.
	 * 
	 * @param fileName
	 * @param content
	 * @throws TypeScriptException
	 */
	void openFile(String fileName, String content) throws TypeScriptException;

	/**
	 * Open the given file name.
	 * 
	 * @param fileName
	 * @param content
	 * @param scriptKindName
	 * @throws TypeScriptException
	 */
	void openFile(String fileName, String content, ScriptKindName scriptKindName) throws TypeScriptException;

	/**
	 * Open an external project based on a project name and its files with explicit
	 * options
	 * 
	 * @param projectFileName
	 * @param rootFiles
	 * @param options
	 * @throws TypeScriptException
	 */
	void openExternalProject(String projectFileName, List<ExternalFile> rootFiles, CompilerOptions options)
			throws TypeScriptException;

	/**
	 * Closes a previously opened external project
	 * 
	 * @see #openExternalProject(String, List, CompilerOptions)
	 * 
	 * @param projectFileName
	 * @throws TypeScriptException
	 */
	void closeExternalProject(String projectFileName) throws TypeScriptException;

	/**
	 * Close the given file name.
	 * 
	 * @param fileName
	 * @param content
	 * @throws TypeScriptException
	 */
	void closeFile(String fileName) throws TypeScriptException;

	/**
	 * Change file content at the given positions.
	 * 
	 * @param fileName
	 * @param position
	 * @param endPosition
	 * @param insertString
	 * @throws TypeScriptException
	 */
	// void changeFile(String fileName, int position, int endPosition, String
	// insertString) throws TypeScriptException;

	/**
	 * Change file content at the given lines/offsets.
	 * 
	 * @param fileName
	 * @param line
	 * @param offset
	 * @param endLine
	 * @param endOffset
	 * @param insertString
	 * @throws TypeScriptException
	 */
	void changeFile(String fileName, int line, int offset, int endLine, int endOffset, String insertString)
			throws TypeScriptException;

	void updateFile(String fileName, String newText) throws TypeScriptException;

	void updateFile(String fileName, String newText, long timeout, TimeUnit timeoutUnit) throws TypeScriptException;

	/**
	 * Completion for the given fileName at the given position.
	 * 
	 * @param fileName
	 * @param position
	 * @return completion for the given fileName at the given position.
	 * @throws TypeScriptException
	 */
	// CompletableFuture<List<CompletionEntry>> completions(String fileName, int
	// position) throws TypeScriptException;

	/**
	 * Completion for the given fileName at the given line/offset.
	 * 
	 * @param fileName
	 * @param line
	 * @param offset
	 * @return completion for the given fileName at the given line/offset
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<CompletionEntry>> completions(String fileName, int line, int offset)
			throws TypeScriptException;

	CompletableFuture<List<CompletionEntry>> completions(String name, int line, int offset,
			ICompletionEntryFactory instanceCreator) throws TypeScriptException;

	CompletableFuture<List<CompletionEntryDetails>> completionEntryDetails(String fileName, int line, int offset,
			String[] entryNames, CompletionEntry completionEntry) throws TypeScriptException;

	/**
	 * Definition for the given fileName at the given line/offset.
	 * 
	 * @param fileName
	 * @param line
	 * @param offset
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<FileSpan>> definition(String fileName, int line, int offset) throws TypeScriptException;

	/**
	 * Signature help for the given fileName at the given line/offset.
	 * 
	 * @param fileName
	 * @param line
	 * @param offset
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<SignatureHelpItems> signatureHelp(String fileName, int line, int offset)
			throws TypeScriptException;

	/**
	 * Quick info for the given fileName at the given line/offset.
	 * 
	 * @param fileName
	 * @param line
	 * @param offset
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<QuickInfo> quickInfo(String fileName, int line, int offset) throws TypeScriptException;

	CompletableFuture<List<DiagnosticEvent>> geterr(String[] files, int delay) throws TypeScriptException;

	CompletableFuture<List<DiagnosticEvent>> geterrForProject(String file, int delay, ProjectInfo projectInfo)
			throws TypeScriptException;

	/**
	 * Format for the given fileName at the given line/offset.
	 * 
	 * @param fileName
	 * @param line
	 * @param offset
	 * @param endLine
	 * @param endOffset
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<CodeEdit>> format(String fileName, int line, int offset, int endLine, int endOffset)
			throws TypeScriptException;

	/**
	 * Find references for the given fileName at the given line/offset.
	 * 
	 * @param fileName
	 * @param line
	 * @param offset
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<ReferencesResponseBody> references(String fileName, int line, int offset)
			throws TypeScriptException;

	/**
	 * Find occurrences for the given fileName at the given line/offset.
	 * 
	 * @param fileName
	 * @param line
	 * @param offset
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<OccurrencesResponseItem>> occurrences(String fileName, int line, int offset)
			throws TypeScriptException;

	CompletableFuture<RenameResponseBody> rename(String file, int line, int offset, Boolean findInComments,
			Boolean findInStrings) throws TypeScriptException;

	CompletableFuture<List<NavtoItem>> navto(String fileName, String searchValue, Integer maxResultCount,
			Boolean currentFileOnly, String projectFileName) throws TypeScriptException;

	CompletableFuture<List<NavigationBarItem>> navbar(String fileName, IPositionProvider positionProvider)
			throws TypeScriptException;

	void configure(ConfigureRequestArguments arguments) throws TypeScriptException;

	CompletableFuture<ProjectInfo> projectInfo(String file, String projectFileName, boolean needFileNameList)
			throws TypeScriptException;

	// Since 2.0.3

	/**
	 * Execute semantic diagnostics for the given file.
	 * 
	 * @param includeLinePosition
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<DiagnosticEventBody> semanticDiagnosticsSync(String file, Boolean includeLinePosition)
			throws TypeScriptException;

	/**
	 * Execute syntactic diagnostics for the given file.
	 * 
	 * @param includeLinePosition
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<DiagnosticEventBody> syntacticDiagnosticsSync(String file, Boolean includeLinePosition)
			throws TypeScriptException;

	// Since 2.0.5

	CompletableFuture<Boolean> compileOnSaveEmitFile(String fileName, Boolean forced) throws TypeScriptException;

	CompletableFuture<List<CompileOnSaveAffectedFileListSingleProject>> compileOnSaveAffectedFileList(String fileName)
			throws TypeScriptException;

	// Since 2.0.6

	CompletableFuture<NavigationBarItem> navtree(String fileName, IPositionProvider positionProvider)
			throws TypeScriptException;

	CompletableFuture<TextInsertion> docCommentTemplate(String fileName, int line, int offset)
			throws TypeScriptException;

	// Since 2.1.0

	CompletableFuture<List<CodeAction>> getCodeFixes(String fileName, IPositionProvider positionProvider, int startLine,
			int startOffset, int endLine, int endOffset, List<Integer> errorCodes) throws TypeScriptException;

	CompletableFuture<List<String>> getSupportedCodeFixes() throws TypeScriptException;

	/**
	 * Definition for the given fileName at the given line/offset.
	 * 
	 * @param fileName
	 * @param line
	 * @param offset
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<FileSpan>> implementation(String fileName, int line, int offset) throws TypeScriptException;

	// Since 2.4.0

	CompletableFuture<List<ApplicableRefactorInfo>> getApplicableRefactors(String fileName, int line, int offset)
			throws TypeScriptException;

	CompletableFuture<List<ApplicableRefactorInfo>> getApplicableRefactors(String fileName, int startLine,
			int startOffset, int endLine, int endOffset) throws TypeScriptException;

	CompletableFuture<RefactorEditInfo> getEditsForRefactor(String fileName, int line, int offset, String refactor,
			String action) throws TypeScriptException;

	CompletableFuture<RefactorEditInfo> getEditsForRefactor(String fileName, int startLine, int startOffset,
			int endLine, int endOffset, String refactor, String action) throws TypeScriptException;

	void addClientListener(ITypeScriptClientListener listener);

	void removeClientListener(ITypeScriptClientListener listener);

	void addInstallTypesListener(IInstallTypesListener listener);

	void removeInstallTypesListener(IInstallTypesListener listener);

	void addInterceptor(IInterceptor interceptor);

	void removeInterceptor(IInterceptor interceptor);

	void join() throws InterruptedException;

	boolean isDisposed();

	void dispose();

}
