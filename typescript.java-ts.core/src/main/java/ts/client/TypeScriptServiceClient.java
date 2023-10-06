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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ts.TypeScriptException;
import ts.TypeScriptNoContentAvailableException;
import ts.client.codefixes.CodeAction;
import ts.client.compileonsave.CompileOnSaveAffectedFileListSingleProject;
import ts.client.completions.CompletionEntry;
import ts.client.completions.CompletionEntryDetails;
import ts.client.completions.ICompletionEntryFactory;
import ts.client.completions.ICompletionEntryMatcherProvider;
import ts.client.configure.ConfigureRequestArguments;
import ts.client.diagnostics.DiagnosticEvent;
import ts.client.diagnostics.DiagnosticEventBody;
import ts.client.diagnostics.IDiagnostic;
import ts.client.installtypes.BeginInstallTypesEventBody;
import ts.client.installtypes.EndInstallTypesEventBody;
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
import ts.internal.FileTempHelper;
import ts.internal.SequenceHelper;
import ts.internal.client.protocol.ChangeRequest;
import ts.internal.client.protocol.CloseExternalProjectRequest;
import ts.internal.client.protocol.CloseRequest;
import ts.internal.client.protocol.CodeFixRequest;
import ts.internal.client.protocol.CompileOnSaveAffectedFileListRequest;
import ts.internal.client.protocol.CompileOnSaveEmitFileRequest;
import ts.internal.client.protocol.CompletionDetailsRequest;
import ts.internal.client.protocol.CompletionsRequest;
import ts.internal.client.protocol.ConfigureRequest;
import ts.internal.client.protocol.DefinitionRequest;
import ts.internal.client.protocol.DocCommentTemplateRequest;
import ts.internal.client.protocol.FormatRequest;
import ts.internal.client.protocol.GetApplicableRefactorsRequest;
import ts.internal.client.protocol.GetEditsForRefactorRequest;
import ts.internal.client.protocol.GetSupportedCodeFixesRequest;
import ts.internal.client.protocol.GeterrForProjectRequest;
import ts.internal.client.protocol.GeterrRequest;
import ts.internal.client.protocol.GsonHelper;
import ts.internal.client.protocol.IRequestEventable;
import ts.internal.client.protocol.ImplementationRequest;
import ts.internal.client.protocol.MessageType;
import ts.internal.client.protocol.NavBarRequest;
import ts.internal.client.protocol.NavToRequest;
import ts.internal.client.protocol.NavTreeRequest;
import ts.internal.client.protocol.OccurrencesRequest;
import ts.internal.client.protocol.OpenExternalProjectRequest;
import ts.internal.client.protocol.OpenRequest;
import ts.internal.client.protocol.ProjectInfoRequest;
import ts.internal.client.protocol.QuickInfoRequest;
import ts.internal.client.protocol.ReferencesRequest;
import ts.internal.client.protocol.ReloadRequest;
import ts.internal.client.protocol.RenameRequest;
import ts.internal.client.protocol.Request;
import ts.internal.client.protocol.Response;
import ts.internal.client.protocol.SemanticDiagnosticsSyncRequest;
import ts.internal.client.protocol.SignatureHelpRequest;
import ts.internal.client.protocol.SyntacticDiagnosticsSyncRequest;
import ts.nodejs.INodejsLaunchConfiguration;
import ts.nodejs.INodejsProcess;
import ts.nodejs.INodejsProcessListener;
import ts.nodejs.NodejsProcess;
import ts.nodejs.NodejsProcessAdapter;
import ts.nodejs.NodejsProcessManager;
import ts.repository.TypeScriptRepositoryManager;
import ts.utils.FileUtils;

/**
 * TypeScript service client implementation.
 *
 */
public class TypeScriptServiceClient implements ITypeScriptServiceClient {

	private static final String NO_CONTENT_AVAILABLE = "No content available.";
	private static final String TSSERVER_FILE_TYPE = "tsserver";

	private INodejsProcess process;
	private List<INodejsProcessListener> nodeListeners;
	private final List<ITypeScriptClientListener> listeners;
	private final List<IInstallTypesListener> installTypesListener;
	private final ReentrantReadWriteLock stateLock;
	private boolean dispose;

	private final Map<Integer, PendingRequestInfo> sentRequestMap;
	private final Map<String, PendingRequestEventInfo> receivedRequestMap;
	private List<IInterceptor> interceptors;

	private ICompletionEntryMatcherProvider completionEntryMatcherProvider;

	private final INodejsProcessListener listener = new NodejsProcessAdapter() {

		@Override
		public void onStart(INodejsProcess process) {
			TypeScriptServiceClient.this.fireStartServer();
		}

		@Override
		public void onStop(INodejsProcess process) {
			dispose();
			fireEndServer();
		}

		public void onMessage(INodejsProcess process, String message) {
			if (message.startsWith("{")) {
				TypeScriptServiceClient.this.dispatchMessage(message);
			}
		};

	};
	private String cancellationPipeName;

	private static class PendingRequestInfo {
		Request<?> requestMessage;
		Consumer<Response<?>> responseHandler;
		long startTime;

		PendingRequestInfo(Request<?> requestMessage, Consumer<Response<?>> responseHandler) {
			this.requestMessage = requestMessage;
			this.responseHandler = responseHandler;
			this.startTime = System.nanoTime();
		}
	}

	private static class PendingRequestEventInfo {
		Request<?> requestMessage;
		Consumer<Event<?>> eventHandler;
		long startTime;

		PendingRequestEventInfo(Request<?> requestMessage, Consumer<Event<?>> eventHandler) {
			this.requestMessage = requestMessage;
			this.eventHandler = eventHandler;
			this.startTime = System.nanoTime();
		}
	}

	public TypeScriptServiceClient(final File projectDir, File tsserverFile, File nodeFile) throws TypeScriptException {
		this(projectDir, tsserverFile, nodeFile, false, false, null, null, null);
	}

	public TypeScriptServiceClient(final File projectDir, File typescriptDir, File nodeFile, boolean enableTelemetry,
			boolean disableAutomaticTypingAcquisition, String cancellationPipeName, File tsserverPluginsFile,
			TypeScriptServiceLogConfiguration logConfiguration) throws TypeScriptException {
		this(NodejsProcessManager.getInstance().create(projectDir,
				tsserverPluginsFile != null ? tsserverPluginsFile
						: TypeScriptRepositoryManager.getTsserverFile(typescriptDir),
				nodeFile, new INodejsLaunchConfiguration() {

					@Override
					public List<String> createNodeArgs() {
						List<String> args = new ArrayList<String>();
						// args.add("-p");
						// args.add(FileUtils.getPath(projectDir));
						if (enableTelemetry) {
							args.add("--enableTelemetry");
						}
						if (disableAutomaticTypingAcquisition) {
							args.add("--disableAutomaticTypingAcquisition");
						}
						if (tsserverPluginsFile != null) {
							args.add("--typescriptDir");
							args.add(FileUtils.getPath(typescriptDir));
						}
						if (cancellationPipeName != null) {
							args.add("--cancellationPipeName");
							args.add(cancellationPipeName + "*");
						}
						// args.add("--useSingleInferredProject");
						return args;
					}

					@Override
					public Map<String, String> createNodeEnvironmentVariables() {
						Map<String, String> environmentVariables = new HashMap<>();
						if (logConfiguration != null) {
							environmentVariables.put("TSS_LOG",
									"-level " + logConfiguration.level.name() + " -file " + logConfiguration.file);
						}
						return environmentVariables;
					}
				}, TSSERVER_FILE_TYPE), cancellationPipeName);
	}

	public TypeScriptServiceClient(INodejsProcess process, String cancellationPipeName) {
		this.listeners = new ArrayList<>();
		this.installTypesListener = new ArrayList<>();
		this.stateLock = new ReentrantReadWriteLock();
		this.dispose = false;
		this.sentRequestMap = new LinkedHashMap<>();
		this.receivedRequestMap = new LinkedHashMap<>();
		this.process = process;
		process.addProcessListener(listener);
		setCompletionEntryMatcherProvider(ICompletionEntryMatcherProvider.LCS_PROVIDER);
		this.cancellationPipeName = cancellationPipeName;
	}

	public static enum TypeScriptServiceLogLevel {
		verbose, normal, terse, requestTime
	}

	public static class TypeScriptServiceLogConfiguration {
		String file;
		TypeScriptServiceLogLevel level;

		public TypeScriptServiceLogConfiguration(String file, TypeScriptServiceLogLevel level) {
			this.file = file;
			this.level = level;
		}
	}

	private void dispatchMessage(String message) {
		JsonObject json = GsonHelper.parse(message).getAsJsonObject();
		JsonElement typeElement = json.get("type");
		if (typeElement != null) {
			MessageType messageType = MessageType.getType(typeElement.getAsString());
			if (messageType == null) {
				throw new IllegalStateException("Unknown response type message " + json);
			}
			switch (messageType) {
			case response:
				int seq = json.get("request_seq").getAsInt();
				PendingRequestInfo pendingRequestInfo;
				synchronized (sentRequestMap) {
					pendingRequestInfo = sentRequestMap.remove(seq);
				}
				if (pendingRequestInfo == null) {
					// throw new IllegalStateException("Unmatched response
					// message " + json);
					return;
				}
				Response responseMessage = pendingRequestInfo.requestMessage.parseResponse(json);
				try {
					handleResponse(responseMessage, message, pendingRequestInfo.startTime);
					pendingRequestInfo.responseHandler.accept(responseMessage);
				} catch (RuntimeException e) {
					// LOG.log(Level.WARNING, "Handling repsonse
					// "+responseMessage+" threw an exception.", e);
				}

				break;
			case event:
				String event = json.get("event").getAsString();
				if ("syntaxDiag".equals(event) || "semanticDiag".equals(event)) {
					DiagnosticEvent response = GsonHelper.DEFAULT_GSON.fromJson(json, DiagnosticEvent.class);
					PendingRequestEventInfo pendingRequestEventInfo;
					synchronized (receivedRequestMap) {
						pendingRequestEventInfo = receivedRequestMap.remove(response.getKey());
					}
					if (pendingRequestEventInfo != null) {
						pendingRequestEventInfo.eventHandler.accept(response);
					}
				} else if ("telemetry".equals(event)) {
					// TelemetryEventBody telemetryData =
					// GsonHelper.DEFAULT_GSON.fromJson(json,
					// TelemetryEvent.class)
					// .getBody();
					//
					JsonObject telemetryData = json.get("body").getAsJsonObject();
					JsonObject payload = telemetryData.has("payload") ? telemetryData.get("payload").getAsJsonObject()
							: null;
					if (payload != null) {
						String telemetryEventName = telemetryData.get("telemetryEventName").getAsString();
						fireLogTelemetry(telemetryEventName, payload);
					}
				} else if ("beginInstallTypes".equals(event)) {
					BeginInstallTypesEventBody data = GsonHelper.DEFAULT_GSON.fromJson(json,
							BeginInstallTypesEventBody.class);
					fireBeginInstallTypes(data);
				} else if ("endInstallTypes".equals(event)) {
					EndInstallTypesEventBody data = GsonHelper.DEFAULT_GSON.fromJson(json,
							EndInstallTypesEventBody.class);
					fireEndInstallTypes(data);
				}
				break;
			default:
				// Do nothing
			}
		}
	}

	@Override
	public void openFile(String fileName, String content) throws TypeScriptException {
		openFile(fileName, content, null);
	}

	@Override
	public void openFile(String fileName, String content, ScriptKindName scriptKindName) throws TypeScriptException {
		execute(new OpenRequest(fileName, null, content, scriptKindName), false);
	}

	@Override
	public void openExternalProject(String projectFileName, List<ExternalFile> rootFiles, CompilerOptions options)
			throws TypeScriptException {
		execute(new OpenExternalProjectRequest(projectFileName, rootFiles, options), false);
	}

	@Override
	public void closeExternalProject(String projectFileName) throws TypeScriptException {
		execute(new CloseExternalProjectRequest(projectFileName), false);
	}

	@Override
	public void closeFile(String fileName) throws TypeScriptException {
		execute(new CloseRequest(fileName), false);
	}

	@Override
	public void changeFile(String fileName, int line, int offset, int endLine, int endOffset, String insertString)
			throws TypeScriptException {
		execute(new ChangeRequest(fileName, line, offset, endLine, endOffset, insertString), false);
	}

	/**
	 * Write the buffer of editor content to a temporary file and have the server
	 * reload it
	 * 
	 * @param fileName
	 * @param newText
	 */
	@Override
	public void updateFile(String fileName, String newText) throws TypeScriptException {
	    updateFile(fileName, newText,10,TimeUnit.SECONDS);
    }
	/**
	 * Write the buffer of editor content to a temporary file and have the server
	 * reload it
	 * 
	 * @param fileName
	 * @param newText
	 * @param timeout
	 * @param timeoutUnit
	 */
	@Override
	public void updateFile(String fileName, String newText, long timeout, TimeUnit timeoutUnit) throws TypeScriptException {
		int seq = SequenceHelper.getRequestSeq();
		String tempFileName = null;
		if (newText != null) {
			tempFileName = FileTempHelper.updateTempFile(newText, seq);
		}
		try {
			execute(new ReloadRequest(fileName, tempFileName, seq), true).get(timeout, timeoutUnit);
		} catch (Exception e) {
			if (e instanceof TypeScriptException) {
				throw (TypeScriptException) e;
			}
			throw new TypeScriptException(e);
		}
	}

	@Override
	public CompletableFuture<List<CompletionEntry>> completions(String fileName, int line, int offset)
			throws TypeScriptException {
		return completions(fileName, line, offset, ICompletionEntryFactory.DEFAULT);
	}

	@Override
	public CompletableFuture<List<CompletionEntry>> completions(String fileName, int line, int offset,
			ICompletionEntryFactory factory) throws TypeScriptException {
		return execute(
				new CompletionsRequest(fileName, line, offset, getCompletionEntryMatcherProvider(), this, factory),
				true);
	}

	@Override
	public CompletableFuture<List<CompletionEntryDetails>> completionEntryDetails(String fileName, int line, int offset,
			String[] entryNames, CompletionEntry completionEntry) throws TypeScriptException {
		return execute(new CompletionDetailsRequest(fileName, line, offset, null, entryNames), true);
	}

	@Override
	public CompletableFuture<List<FileSpan>> definition(String fileName, int line, int offset)
			throws TypeScriptException {
		return execute(new DefinitionRequest(fileName, line, offset), true);
	}

	@Override
	public CompletableFuture<SignatureHelpItems> signatureHelp(String fileName, int line, int offset)
			throws TypeScriptException {
		return execute(new SignatureHelpRequest(fileName, line, offset), true);
	}

	@Override
	public CompletableFuture<QuickInfo> quickInfo(String fileName, int line, int offset) throws TypeScriptException {
		return execute(new QuickInfoRequest(fileName, line, offset), true);
	}

	@Override
	public CompletableFuture<List<DiagnosticEvent>> geterr(String[] files, int delay) throws TypeScriptException {
		return execute(new GeterrRequest(files, delay), true);
	}

	@Override
	public CompletableFuture<List<DiagnosticEvent>> geterrForProject(String file, int delay, ProjectInfo projectInfo)
			throws TypeScriptException {
		return execute(new GeterrForProjectRequest(file, delay, projectInfo), true);
	}

	@Override
	public CompletableFuture<List<CodeEdit>> format(String fileName, int line, int offset, int endLine, int endOffset)
			throws TypeScriptException {
		return execute(new FormatRequest(fileName, line, offset, endLine, endOffset), true);
	}

	@Override
	public CompletableFuture<ReferencesResponseBody> references(String fileName, int line, int offset)
			throws TypeScriptException {
		return execute(new ReferencesRequest(fileName, line, offset), true);
	}

	@Override
	public CompletableFuture<List<OccurrencesResponseItem>> occurrences(String fileName, int line, int offset)
			throws TypeScriptException {
		return execute(new OccurrencesRequest(fileName, line, offset), true);
	}

	@Override
	public CompletableFuture<RenameResponseBody> rename(String file, int line, int offset, Boolean findInComments,
			Boolean findInStrings) throws TypeScriptException {
		return execute(new RenameRequest(file, line, offset, findInComments, findInStrings), true);
	}

	@Override
	public CompletableFuture<List<NavtoItem>> navto(String fileName, String searchValue, Integer maxResultCount,
			Boolean currentFileOnly, String projectFileName) throws TypeScriptException {
		return execute(new NavToRequest(fileName, searchValue, maxResultCount, currentFileOnly, projectFileName), true);
	}

	@Override
	public CompletableFuture<List<NavigationBarItem>> navbar(String fileName, IPositionProvider positionProvider)
			throws TypeScriptException {
		return execute(new NavBarRequest(fileName, positionProvider), true);
	}

	@Override
	public void configure(ConfigureRequestArguments arguments) throws TypeScriptException {
		execute(new ConfigureRequest(arguments), true);
	}

	@Override
	public CompletableFuture<ProjectInfo> projectInfo(String file, String projectFileName, boolean needFileNameList)
			throws TypeScriptException {
		return execute(new ProjectInfoRequest(file, needFileNameList), true);
	}

	// Since 2.0.3

	@Override
	public CompletableFuture<DiagnosticEventBody> semanticDiagnosticsSync(String file, Boolean includeLinePosition)
			throws TypeScriptException {
		return execute(new SemanticDiagnosticsSyncRequest(file, includeLinePosition), true).thenApply(d -> {
			return new DiagnosticEventBody(file, (List<IDiagnostic>) d);
		});
	}

	@Override
	public CompletableFuture<DiagnosticEventBody> syntacticDiagnosticsSync(String file, Boolean includeLinePosition)
			throws TypeScriptException {
		return execute(new SyntacticDiagnosticsSyncRequest(file, includeLinePosition), true).thenApply(d -> {
			return new DiagnosticEventBody(file, (List<IDiagnostic>) d);
		});
	}

	// Since 2.0.5

	@Override
	public CompletableFuture<Boolean> compileOnSaveEmitFile(String fileName, Boolean forced)
			throws TypeScriptException {
		return execute(new CompileOnSaveEmitFileRequest(fileName, forced), true);
	}

	@Override
	public CompletableFuture<List<CompileOnSaveAffectedFileListSingleProject>> compileOnSaveAffectedFileList(
			String fileName) throws TypeScriptException {
		return execute(new CompileOnSaveAffectedFileListRequest(fileName), true);
	}

	// Since 2.0.6

	@Override
	public CompletableFuture<NavigationBarItem> navtree(String fileName, IPositionProvider positionProvider)
			throws TypeScriptException {
		return execute(new NavTreeRequest(fileName, positionProvider), true);
	}

	@Override
	public CompletableFuture<TextInsertion> docCommentTemplate(String fileName, int line, int offset)
			throws TypeScriptException {
		return execute(new DocCommentTemplateRequest(fileName, line, offset), true);
	}

	// Since 2.1.0

	@Override
	public CompletableFuture<List<CodeAction>> getCodeFixes(String fileName, IPositionProvider positionProvider,
			int startLine, int startOffset, int endLine, int endOffset, List<Integer> errorCodes)
			throws TypeScriptException {
		return execute(new CodeFixRequest(fileName, startLine, startOffset, endLine, endOffset, errorCodes), true);
	}

	@Override
	public CompletableFuture<List<String>> getSupportedCodeFixes() throws TypeScriptException {
		return execute(new GetSupportedCodeFixesRequest(), true);
	}

	@Override
	public CompletableFuture<List<FileSpan>> implementation(String fileName, int line, int offset)
			throws TypeScriptException {
		return execute(new ImplementationRequest(fileName, line, offset), true);
	}

	// Since 2.4.0

	@Override
	public CompletableFuture<List<ApplicableRefactorInfo>> getApplicableRefactors(String fileName, int line, int offset)
			throws TypeScriptException {
		return execute(new GetApplicableRefactorsRequest(fileName, line, offset), true);
	}

	@Override
	public CompletableFuture<List<ApplicableRefactorInfo>> getApplicableRefactors(String fileName, int startLine,
			int startOffset, int endLine, int endOffset) throws TypeScriptException {
		return execute(new GetApplicableRefactorsRequest(fileName, startLine, startOffset, endLine, endOffset), true);
	}

	@Override
	public CompletableFuture<RefactorEditInfo> getEditsForRefactor(String fileName, int line, int offset,
			String refactor, String action) throws TypeScriptException {
		return execute(new GetEditsForRefactorRequest(fileName, line, offset, refactor, action), true);
	}

	@Override
	public CompletableFuture<RefactorEditInfo> getEditsForRefactor(String fileName, int startLine, int startOffset,
			int endLine, int endOffset, String refactor, String action) throws TypeScriptException {
		return execute(
				new GetEditsForRefactorRequest(fileName, startLine, startOffset, endLine, endOffset, refactor, action),
				true);
	}

	private <T> CompletableFuture<T> execute(Request<?> request, boolean expectsResult) throws TypeScriptException {
		if (!expectsResult) {
			sendRequest(request);
			return null;
		}
		final CompletableFuture<T> result = new CompletableFuture<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				tryCancelRequest(request);
				return super.cancel(mayInterruptIfRunning);
			}

			/**
			 * Try to cancel the given request:
			 * 
			 * <ul>
			 * <li>on client side : remove request from the received request queue.</li>
			 * <li>on server side (tsserver) : cancel the request.</li>
			 * </ul>
			 * 
			 * @param request
			 */
			private void tryCancelRequest(Request<?> request) {
				try {
					cancelServerRequest(request);
				} finally {
					cancelClientRequest(request);
				}
			}

			private void cancelClientRequest(Request<?> request) {
				if (request instanceof IRequestEventable) {
					List<String> keys = ((IRequestEventable) request).getKeys();
					synchronized (receivedRequestMap) {
						for (String key : keys) {
							receivedRequestMap.remove(key);
						}
					}
				} else {
					synchronized (sentRequestMap) {
						sentRequestMap.remove(request.getSeq());
					}
				}
			}

			private void cancelServerRequest(Request<?> request) {
				// Generate en empty file in the temp directory (ex:
				// $TMP_DIR/eclipse-tscancellation-4df2438b-ca7a-4ef3-9a46-83e8afef61b3.sock844
				// where 844 is request sequence)
				// for the given request sequence waited by tsserver
				// typescript/lib/cancellationToken.js.
				// to cancel request from tsserver.
				if (cancellationPipeName != null) {
					File tempFile = new File(TypeScriptServiceClient.this.cancellationPipeName + request.getSeq());
					try {
						tempFile.createNewFile();
						tempFile.deleteOnExit();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		};
		if (request instanceof IRequestEventable) {
			Consumer<Event<?>> responseHandler = (event) -> {
				if (((IRequestEventable) request).accept(event)) {
					result.complete((T) ((IRequestEventable) request).getEvents());
				}
			};
			List<String> keys = ((IRequestEventable) request).getKeys();
			PendingRequestEventInfo info = new PendingRequestEventInfo(request, responseHandler);
			synchronized (receivedRequestMap) {
				for (String key : keys) {
					receivedRequestMap.put(key, info);
				}
			}
		} else {
			Consumer<Response<?>> responseHandler = (response) -> {
				if (response.isSuccess()) {
					// tsserver response with success
					result.complete((T) response.getBody());
				} else {
					// tsserver response with error
					result.completeExceptionally(createException(response.getMessage()));
				}
			};
			int seq = request.getSeq();
			synchronized (sentRequestMap) {
				sentRequestMap.put(seq, new PendingRequestInfo(request, responseHandler));
			}
		}
		sendRequest(request);
		return result;
	}

	private TypeScriptException createException(String message) {
		if (NO_CONTENT_AVAILABLE.equals(message)) {
			return new TypeScriptNoContentAvailableException(message);
		}
		return new TypeScriptException(message);
	}

	private void sendRequest(Request<?> request) throws TypeScriptException {
		String req = GsonHelper.DEFAULT_GSON.toJson(request);
		handleRequest(request, req);
		getProcess().sendRequest(req);
	}

	private INodejsProcess getProcess() throws TypeScriptException {
		if (process == null) {
			throw new RuntimeException("unexpected error: process was stopped/killed before trying to use it again");
		}

		if (!process.isStarted()) {
			process.start();
		}
		return process;
	}

	@Override
	public void addClientListener(ITypeScriptClientListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeClientListener(ITypeScriptClientListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	private void fireStartServer() {
		synchronized (listeners) {
			for (ITypeScriptClientListener listener : listeners) {
				listener.onStart(this);
			}
		}
	}

	private void fireEndServer() {
		synchronized (listeners) {
			for (ITypeScriptClientListener listener : listeners) {
				listener.onStop(this);
			}
		}
	}

	@Override
	public void addInstallTypesListener(IInstallTypesListener listener) {
		synchronized (installTypesListener) {
			installTypesListener.add(listener);
		}
	}

	@Override
	public void removeInstallTypesListener(IInstallTypesListener listener) {
		synchronized (installTypesListener) {
			installTypesListener.remove(listener);
		}
	}

	private void fireBeginInstallTypes(BeginInstallTypesEventBody body) {
		synchronized (installTypesListener) {
			for (IInstallTypesListener listener : installTypesListener) {
				listener.onBegin(body);
			}
		}
	}

	private void fireEndInstallTypes(EndInstallTypesEventBody body) {
		synchronized (installTypesListener) {
			for (IInstallTypesListener listener : installTypesListener) {
				listener.onEnd(body);
			}
		}
	}

	private void fireLogTelemetry(String telemetryEventName, JsonObject payload) {
		synchronized (installTypesListener) {
			for (IInstallTypesListener listener : installTypesListener) {
				listener.logTelemetry(telemetryEventName, payload);
			}
		}
	}

	@Override
	public void addInterceptor(IInterceptor interceptor) {
		beginWriteState();
		try {
			if (interceptors == null) {
				interceptors = new ArrayList<IInterceptor>();
			}
			interceptors.add(interceptor);
		} finally {
			endWriteState();
		}
	}

	@Override
	public void removeInterceptor(IInterceptor interceptor) {
		beginWriteState();
		try {
			if (interceptors != null) {
				interceptors.remove(interceptor);
			}
		} finally {
			endWriteState();
		}
	}

	public void addProcessListener(INodejsProcessListener listener) {
		beginWriteState();
		try {
			if (nodeListeners == null) {
				nodeListeners = new ArrayList<INodejsProcessListener>();
			}
			nodeListeners.add(listener);
			if (process != null) {
				process.addProcessListener(listener);
			}
		} finally {
			endWriteState();
		}
	}

	public void removeProcessListener(INodejsProcessListener listener) {
		beginWriteState();
		try {
			if (nodeListeners != null && listener != null) {
				nodeListeners.remove(listener);
			}
			if (process != null) {
				process.removeProcessListener(listener);
			}
		} finally {
			endWriteState();
		}
	}

	@Override
	public void join() throws InterruptedException {
		if (process != null) {
			this.process.join();
		}
	}

	@Override
	public boolean isDisposed() {
		return dispose;
	}

	@Override
	public final void dispose() {
		beginWriteState();
		try {
			if (!isDisposed()) {
				this.dispose = true;
				System.out.println("dispose client - process=" + process);
				if (NodejsProcess.logProcessStopStack) {
					Thread.dumpStack();
				}
				if (process != null) {
					process.kill();
				}
				this.process = null;
			}
		} finally {
			endWriteState();
		}
	}

	private void beginReadState() {
		stateLock.readLock().lock();
	}

	private void endReadState() {
		stateLock.readLock().unlock();
	}

	private void beginWriteState() {
		stateLock.writeLock().lock();
	}

	private void endWriteState() {
		stateLock.writeLock().unlock();
	}

	public void setCompletionEntryMatcherProvider(ICompletionEntryMatcherProvider completionEntryMatcherProvider) {
		this.completionEntryMatcherProvider = completionEntryMatcherProvider;
	}

	public ICompletionEntryMatcherProvider getCompletionEntryMatcherProvider() {
		return completionEntryMatcherProvider;
	}

	// --------------------------- Handler for Request/response/Error
	// ------------------------------------

	/**
	 * Handle the given request.
	 * 
	 * @param request
	 */
	private void handleRequest(Request<?> request, String json) {
		if (interceptors == null) {
			return;
		}
		for (IInterceptor interceptor : interceptors) {
			interceptor.handleRequest(request, json, this);
		}
	}

	/**
	 * Handle the given reponse.
	 * 
	 * @param request
	 * @param response
	 * @param startTime
	 */
	private void handleResponse(Response<?> response, String json, long startTime) {
		if (interceptors == null) {
			return;
		}
		long ellapsedTime = getElapsedTimeInMs(startTime);
		for (IInterceptor interceptor : interceptors) {
			interceptor.handleResponse(response, json, ellapsedTime, this);
		}
	}

	/**
	 * Handle the given error.
	 * 
	 * @param request
	 * @param e
	 * @param startTime
	 */
	private void handleError(String command, Throwable e, long startTime) {
		if (interceptors == null) {
			return;
		}
		long ellapsedTime = getElapsedTimeInMs(startTime);
		for (IInterceptor interceptor : interceptors) {
			interceptor.handleError(e, this, command, ellapsedTime);
		}
	}

	/**
	 * Returns the elappsed time in ms.
	 * 
	 * @param startTime
	 *            in nano time.
	 * @return the elappsed time in ms.
	 */
	private static long getElapsedTimeInMs(long startTime) {
		return ((System.nanoTime() - startTime) / 1000000L);
	}

}
