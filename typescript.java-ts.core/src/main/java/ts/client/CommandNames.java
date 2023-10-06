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
package ts.client;

import ts.utils.VersionHelper;

/**
 * Command names of tsserver.
 *
 */
public enum CommandNames implements ISupportable {

	Open("open"), 
	Close("close"), 
	Change("change"), 
	NavBar("navbar"), 
	Completions("completions"), 
	CompletionEntryDetails("completionEntryDetails"), 
	Reload("reload"), 
	Definition("definition"), 
	SignatureHelp("signatureHelp"), 
	QuickInfo("quickinfo"), 
	Geterr("geterr"),
	GeterrForProject("geterrForProject"),
	Format("format"), 
	References("references"), 
	Occurrences("occurrences"), 
	Configure("configure"),
	ProjectInfo("projectInfo"),
	Rename("rename"),
	NavTo("navto"),

	// 2.0.0
	SemanticDiagnosticsSync("semanticDiagnosticsSync", "2.0.0"), 
	SyntacticDiagnosticsSync("syntacticDiagnosticsSync", "2.0.0"), 
	
	// 2.0.5
	CompileOnSaveAffectedFileList("compileOnSaveAffectedFileList", "2.0.5"),
	CompileOnSaveEmitFile("compileOnSaveEmitFile", "2.0.5"),
	
	// 2.0.6
	NavTree("navtree", "2.0.6"),
	DocCommentTemplate("docCommentTemplate", "2.0.6"),
	
	// 2.1.0
	Implementation("implementation", "2.1.0"),
	GetSupportedCodeFixes("getSupportedCodeFixes", "2.1.0"),
	GetCodeFixes("getCodeFixes", "2.1.0"),

	// 2.4.0
	GetApplicableRefactors("getApplicableRefactors", "2.4.0"),
    GetEditsForRefactor("getEditsForRefactor", "2.4.0"),
	
    OpenExternalProject("openExternalProject"),
	CloseExternalProject("closeExternalProject");
    
	private final String name;
	private final String sinceVersion;

	private CommandNames(String name) {
		this(name, null);
	}

	private CommandNames(String name, String sinceVersion) {
		this.name = name;
		this.sinceVersion = sinceVersion;
	}

	public String getName() {
		return name;
	}

	/**
	 * Return true if the tsserver command support the given version and false
	 * otherwise.
	 * 
	 * @param version
	 * @return true if the tsserver command support the given version and false
	 *         otherwise.
	 */
	public boolean canSupport(String version) {
		return VersionHelper.canSupport(version, sinceVersion);
	}

}
