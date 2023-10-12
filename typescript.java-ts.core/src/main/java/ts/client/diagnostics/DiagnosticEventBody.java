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
package ts.client.diagnostics;

import java.util.List;

public class DiagnosticEventBody {

	/**
	 * The file for which diagnostic information is reported.
	 */
	private String file;

	/**
	 * An array of diagnostic informatdiion items.
	 */
	private List<IDiagnostic> diagnostics;

	public DiagnosticEventBody() {
	}

	public DiagnosticEventBody(String file, List<IDiagnostic> diagnostics) {
		this.file = file;
		this.diagnostics = diagnostics;
	}

	public String getFile() {
		return file;
	}

	public List<IDiagnostic> getDiagnostics() {
		return diagnostics;
	}
}
