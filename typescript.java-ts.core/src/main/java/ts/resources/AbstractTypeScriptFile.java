/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ts.TypeScriptException;
import ts.client.CodeEdit;
import ts.client.CommandNames;
import ts.client.FileSpan;
import ts.client.ITypeScriptServiceClient;
import ts.client.Location;
import ts.client.ScriptKindName;
import ts.client.codefixes.CodeAction;
import ts.client.completions.CompletionEntry;
import ts.client.completions.ICompletionEntryFactory;
import ts.client.configure.ConfigureRequestArguments;
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
import ts.internal.LocationReader;
import ts.utils.CompletableFutureUtils;

/**
 * Abstract TypeScript file.
 *
 */
public abstract class AbstractTypeScriptFile implements ITypeScriptFile {

	private final ITypeScriptProject tsProject;
	private final ScriptKindName scriptKind;

	private boolean dirty;
	protected final Object synchLock = new Object();
	private boolean opened;

	private final List<INavbarListener> listeners;
	private NavigationBarItemRoot navbar;
	private FormatCodeSettings formatOptions;
	private boolean configureAlreadyDone;
	private boolean disableChanged;

	private CompletableFuture navbarPromise;

	public AbstractTypeScriptFile(ITypeScriptProject tsProject, ScriptKindName scriptKind) {
		this.tsProject = tsProject;
		this.scriptKind = scriptKind;
		this.listeners = new ArrayList<INavbarListener>();
		this.setDirty(false);
		this.configureAlreadyDone = false;
	}

	@Override
	public ITypeScriptProject getProject() {
		return tsProject;
	}

	@Override
	public ScriptKindName getScriptKind() {
		return scriptKind;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public Location getLocation(int position) throws TypeScriptException {
		return new LocationReader(getContents(), position).getLineOffset();
	}

	@Override
	public int getPosition(int line, int offset) throws TypeScriptException {
		// TODO: implement that
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPosition(Location loc) throws TypeScriptException {
		return getPosition(loc.getLine(), loc.getOffset());
	}

	@Override
	public void open() throws TypeScriptException {
		((TypeScriptProject) tsProject).openFile(this);
		this.opened = true;
	}

	@Override
	public void close() throws TypeScriptException {
		((TypeScriptProject) tsProject).closeFile(this);
		this.opened = false;
	}

	@Override
	public boolean isOpened() {
		return opened;
	}

	void setOpened(boolean opened) {
		this.opened = opened;
	}

	@Override
	public CompletableFuture<List<CompletionEntry>> completions(int position, ICompletionEntryFactory factory)
			throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location location = this.getLocation(position);
		int line = location.getLine();
		int offset = location.getOffset();
		String prefix = null;
		return client.completions(this.getName(), line, offset, factory);
	}

	@Override
	public CompletableFuture<List<FileSpan>> definition(int position) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location location = this.getLocation(position);
		int line = location.getLine();
		int offset = location.getOffset();
		return client.definition(this.getName(), line, offset);
	}

	@Override
	public CompletableFuture<QuickInfo> quickInfo(int position) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location location = this.getLocation(position);
		int line = location.getLine();
		int offset = location.getOffset();
		return client.quickInfo(this.getName(), line, offset);
	}

	@Override
	public CompletableFuture<List<DiagnosticEvent>> geterr() throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		return client.geterr(new String[] { getName() }, 0);
	}

	@Override
	public CompletableFuture<List<CodeEdit>> format(int startPosition, int endPosition) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		this.ensureFormatCodeSettings(client);
		Location start = this.getLocation(startPosition);
		Location end = this.getLocation(endPosition);
		return client.format(this.getName(), start.getLine(), start.getOffset(), end.getLine(), end.getOffset());
	}

	private void ensureFormatCodeSettings(ITypeScriptServiceClient client) throws TypeScriptException {
		FormatCodeSettings oldFormatOptions = formatOptions;
		FormatCodeSettings newFormatOptions = getFormatOptions();
		if (!configureAlreadyDone || !newFormatOptions.equals(oldFormatOptions)) {
			configureAlreadyDone = true;
			client.configure(new ConfigureRequestArguments().setFile(getName()).setFormatOptions(formatOptions));
		}
	}

	@Override
	public CompletableFuture<DiagnosticEventBody> semanticDiagnosticsSync(Boolean includeLinePosition)
			throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		return client.semanticDiagnosticsSync(getName(), includeLinePosition);
	}

	@Override
	public CompletableFuture<DiagnosticEventBody> syntacticDiagnosticsSync(Boolean includeLinePosition)
			throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		return client.syntacticDiagnosticsSync(getName(), includeLinePosition);
	}

	// @Override
	// public CompletableFuture<DiagnosticEventBody> diagnostics(Boolean
	// includeLinePosition) throws TypeScriptException {
	// this.synch();
	// ITypeScriptServiceClient client = tsProject.getClient();
	// if (tsProject.canSupport(CommandNames.SemanticDiagnosticsSync)) {
	// // TypeScript >=2.0.3, uses syntactic/semantic command names which
	// // seems having better performance.
	// return client.syntacticDiagnosticsSync(getName(), includeLinePosition)
	// .thenApply(client.semanticDiagnosticsSync(getName(),
	// includeLinePosition));
	// } else {
	// // FIXME
	// // client.geterr(new String[] { file.getName() }, 0, collector);
	// }
	// }

	@Override
	public FormatCodeSettings getFormatOptions() {
		formatOptions = tsProject.getProjectSettings().getFormatOptions();
		return formatOptions;
	}

	public void setFormatOptions(FormatCodeSettings formatOptions) {
		this.formatOptions = formatOptions;
	}

	@Override
	public CompletableFuture<ReferencesResponseBody> references(int position) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location location = this.getLocation(position);
		int line = location.getLine();
		int offset = location.getOffset();
		return client.references(this.getName(), line, offset);
	}

	@Override
	public CompletableFuture<List<OccurrencesResponseItem>> occurrences(int position) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location location = this.getLocation(position);
		int line = location.getLine();
		int offset = location.getOffset();
		return client.occurrences(this.getName(), line, offset);
	}

	@Override
	public CompletableFuture<RenameResponseBody> rename(int position, Boolean findInComments, Boolean findInStrings)
			throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location location = this.getLocation(position);
		int line = location.getLine();
		int offset = location.getOffset();
		return client.rename(this.getName(), line, offset, findInComments, findInStrings);
	}

	@Override
	public CompletableFuture<List<FileSpan>> implementation(int position) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location location = this.getLocation(position);
		int line = location.getLine();
		int offset = location.getOffset();
		return client.implementation(this.getName(), line, offset);
	}

	@Override
	public CompletableFuture<TextInsertion> docCommentTemplate(int position) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location location = this.getLocation(position);
		int line = location.getLine();
		int offset = location.getOffset();
		return client.docCommentTemplate(this.getName(), line, offset);
	}

	@Override
	public CompletableFuture<List<CodeAction>> getCodeFixes(int startPosition, int endPosition,
			List<Integer> errorCodes) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location startLocation = this.getLocation(startPosition);
		int startLine = startLocation.getLine();
		int startOffset = startLocation.getOffset();
		Location endLocation = this.getLocation(endPosition);
		int endLine = endLocation.getLine();
		int endOffset = endLocation.getOffset();
		return client.getCodeFixes(this.getName(), this, startLine, startOffset, endLine, endOffset, errorCodes);
	}

	@Override
	public void compileOnSaveEmitFile(Boolean forced) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		// FIXME client.compileOnSaveEmitFile(getName(), forced);
	}

	@Override
	public CompletableFuture<List<ApplicableRefactorInfo>> getApplicableRefactors(int startPosition,
			Integer endPosition) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location startLocation = this.getLocation(startPosition);
		int line = startLocation.getLine();
		int offset = startLocation.getOffset();
		if (endPosition == null) {
			return client.getApplicableRefactors(this.getName(), line, offset);
		}
		Location endLocation = this.getLocation(endPosition);
		int endLine = endLocation.getLine();
		int endOffset = endLocation.getOffset();
		return client.getApplicableRefactors(this.getName(), line, offset, endLine, endOffset);
	}

	@Override
	public CompletableFuture<RefactorEditInfo> getEditsForRefactor(int startPosition, Integer endPosition,
			String refactor, String action) throws TypeScriptException {
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		Location location = this.getLocation(startPosition);
		int line = location.getLine();
		int offset = location.getOffset();
		if (endPosition == null) {
			return client.getEditsForRefactor(this.getName(), line, offset, refactor, action);
		}
		Location endLocation = this.getLocation(endPosition);
		int endLine = endLocation.getLine();
		int endOffset = endLocation.getOffset();
		return client.getEditsForRefactor(this.getName(), line, offset, endLine, endOffset, refactor, action);
	}

	@Override
	public void addNavbarListener(INavbarListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
		if (navbar != null) {
			listener.navBarChanged(navbar);
		}
	}

	private void fireNavBarListeners(NavigationBarItemRoot navbar) {
		synchronized (listeners) {
			for (INavbarListener listener : listeners) {
				listener.navBarChanged(navbar);
			}
		}
	}

	@Override
	public void removeNavbarListener(INavbarListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public void refreshNavBar() throws TypeScriptException {
		if (listeners.isEmpty()) {
			return;
		}
		this.synch();
		ITypeScriptServiceClient client = tsProject.getClient();
		// cancel last navigation bar/tree if needed.
		CompletableFutureUtils.cancel(navbarPromise);
		if (tsProject.canSupport(CommandNames.NavTree)) {
			// when TypeScript 2.0.6 is consummed, use "navtree" to fill the
			// Outline
			// see
			// https://github.com/Microsoft/TypeScript/pull/11532#issuecomment-254804923
			navbarPromise = client.navtree(this.getName(), this).thenAccept(item -> {
				AbstractTypeScriptFile.this.navbar = new NavigationBarItemRoot(item);
				fireNavBarListeners(navbar);
				navbarPromise = null;
			});
		} else {
			navbarPromise = client.navbar(this.getName(), this).thenAccept(item -> {
				AbstractTypeScriptFile.this.navbar = new NavigationBarItemRoot(item);
				fireNavBarListeners(navbar);
				navbarPromise = null;
			});
		}
	}

	@Override
	public NavigationBarItemRoot getNavBar() {
		return navbar;
	}

	@Override
	public synchronized void synch() throws TypeScriptException {
		if (!isDirty()) {
			// no need to synchronize the file content with tsserver.
			return;
		}
		switch (tsProject.getProjectSettings().getSynchStrategy()) {
		case RELOAD:
			// reload strategy : store the content of the ts file in a temporary
			// file and call reload command.
			tsProject.getClient().updateFile(this.getName(), this.getContents());
			setDirty(false);
			break;
		case CHANGE:
			// change strategy: wait until "change" command is not finished.
			while (isDirty()) {
				try {
					synchronized (synchLock) {
						synchLock.wait(5);
					}
				} catch (InterruptedException e) {
					throw new TypeScriptException(e);
				}
			}
			break;
		}

	}

	public void setDisableChanged(boolean disableChanged) {
		this.disableChanged = disableChanged;
	}

	public boolean isDisableChanged() {
		return disableChanged;
	}
}
