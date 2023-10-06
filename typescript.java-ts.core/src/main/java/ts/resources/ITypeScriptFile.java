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
package ts.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ts.TypeScriptException;
import ts.client.CodeEdit;
import ts.client.FileSpan;
import ts.client.IPositionProvider;
import ts.client.ScriptKindName;
import ts.client.codefixes.CodeAction;
import ts.client.completions.CompletionEntry;
import ts.client.completions.ICompletionEntryFactory;
import ts.client.diagnostics.DiagnosticEvent;
import ts.client.diagnostics.DiagnosticEventBody;
import ts.client.format.FormatCodeSettings;
import ts.client.jsdoc.TextInsertion;
import ts.client.navbar.NavigationBarItemRoot;
import ts.client.occurrences.OccurrencesResponseItem;
import ts.client.quickinfo.QuickInfo;
import ts.client.refactors.ApplicableRefactorInfo;
import ts.client.refactors.RefactorEditInfo;
import ts.client.references.ReferencesResponseBody;
import ts.client.rename.RenameResponseBody;

/**
 * TypeScript file API.
 *
 */
public interface ITypeScriptFile extends IPositionProvider {

	/**
	 * Returns the owner project of the file.
	 * 
	 * @return the owner project of the file.
	 */
	ITypeScriptProject getProject();

	/**
	 * Returns the full path of the file.
	 * 
	 * @return the full path of the file.
	 */
	String getName();

	/**
	 * Returns true if the file is flagged as "open" in tsserver side and false
	 * otherwise. In the case where tsserver is not started and the file is opened
	 * in the IDE editor, this method returns false.
	 * 
	 * @return true if the file is flagged as "open" in tsserver side and false
	 *         otherwise.
	 */
	boolean isOpened();

	/**
	 * Returns true if file content has changed and must be synchronized with
	 * tsserver and false otherwise.
	 * 
	 * @return true if file content has changed and must be synchronized with
	 *         tsserver and false otherwise.
	 */
	boolean isDirty();

	String getPrefix(int position);

	/**
	 * Returns the contents of the file.
	 * 
	 * @return the contents of the file.
	 */
	String getContents();

	/**
	 * Returns the script kind (ts, tsx, js or jsx) of the file.
	 * 
	 * @return the script kind (ts, tsx, js or jsx) of the file.
	 */
	ScriptKindName getScriptKind();

	/**
	 * Flag the file as "opened" into tsserver side.
	 * 
	 * @throws TypeScriptException
	 */
	void open() throws TypeScriptException;

	/**
	 * Flag the file as "closed" into tsserver side.
	 * 
	 * @throws TypeScriptException
	 */
	void close() throws TypeScriptException;

	/**
	 * Synchronize file content with tsserver according the {@link SynchStrategy}.
	 * 
	 * @throws TypeScriptException
	 */
	void synch() throws TypeScriptException;

	/**
	 * Call completions from the tsserver.
	 * 
	 * @param position
	 * @param instanceCreator
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<CompletionEntry>> completions(int position, ICompletionEntryFactory factory)
			throws TypeScriptException;

	/**
	 * Call definition from the tsserver.
	 * 
	 * @param position
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<FileSpan>> definition(int position) throws TypeScriptException;

	/**
	 * Call quickInfo from the tsserver.
	 * 
	 * @param position
	 * @throws TypeScriptException
	 */
	CompletableFuture<QuickInfo> quickInfo(int position) throws TypeScriptException;

	/**
	 * Call getErr from the tsserver.
	 * 
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<DiagnosticEvent>> geterr() throws TypeScriptException;

	/**
	 * Format the file content according start/end position.
	 * 
	 * @param startPosition
	 * @param endPosition
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<CodeEdit>> format(int startPosition, int endPosition) throws TypeScriptException;

	/**
	 * Execute semantic diagnostics.
	 * 
	 * @param includeLinePosition
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<DiagnosticEventBody> semanticDiagnosticsSync(Boolean includeLinePosition)
			throws TypeScriptException;

	/**
	 * Execute syntactic diagnostics.
	 * 
	 * @param includeLinePosition
	 * @return
	 * @throws TypeScriptException
	 */
	CompletableFuture<DiagnosticEventBody> syntacticDiagnosticsSync(Boolean includeLinePosition)
			throws TypeScriptException;

	// /**
	// * Execute semantic and syntactic both diagnostics.
	// *
	// * @param includeLinePosition
	// * @return
	// * @throws TypeScriptException
	// */
	// CompletableFuture<DiagnosticEventBody> diagnostics(Boolean
	// includeLinePosition) throws TypeScriptException;

	/**
	 * Find references of the given position.
	 * 
	 * @param position
	 * @throws TypeScriptException
	 */
	public CompletableFuture<ReferencesResponseBody> references(int position) throws TypeScriptException;

	/**
	 * Find occurrences of the given position.
	 * 
	 * @param position
	 * @throws TypeScriptException
	 */
	public CompletableFuture<List<OccurrencesResponseItem>> occurrences(int position) throws TypeScriptException;

	CompletableFuture<RenameResponseBody> rename(int position, Boolean findInComments, Boolean findInStrings)
			throws TypeScriptException;

	/**
	 * Call implementation from the tsserver.
	 * 
	 * @param position
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<FileSpan>> implementation(int position) throws TypeScriptException;

	CompletableFuture<TextInsertion> docCommentTemplate(int position) throws TypeScriptException;

	/**
	 * Get code fixes.
	 * 
	 * @param startPosition
	 * @param endPosition
	 * @param collector
	 * @throws TypeScriptException
	 */
	CompletableFuture<List<CodeAction>> getCodeFixes(int startPosition, int endPosition, List<Integer> errorCodes)
			throws TypeScriptException;

	CompletableFuture<List<ApplicableRefactorInfo>> getApplicableRefactors(int startPosition, Integer endPosition)
			throws TypeScriptException;

	CompletableFuture<RefactorEditInfo> getEditsForRefactor(int startPosition, Integer endPosition, String refactor,
			String action) throws TypeScriptException;

	/**
	 * Returns the navigation bar root.
	 * 
	 * @return the navigation bar root.
	 */
	NavigationBarItemRoot getNavBar();

	/**
	 * Refresh the navigation bar root.
	 * 
	 * @throws TypeScriptException
	 */
	void refreshNavBar() throws TypeScriptException;

	void compileOnSaveEmitFile(Boolean forced) throws TypeScriptException;

	void addNavbarListener(INavbarListener listener);

	void removeNavbarListener(INavbarListener listener);

	FormatCodeSettings getFormatOptions();

	void setFormatOptions(FormatCodeSettings formatOptions);

	void setDisableChanged(boolean disableChanged);

	boolean isDisableChanged();
}
