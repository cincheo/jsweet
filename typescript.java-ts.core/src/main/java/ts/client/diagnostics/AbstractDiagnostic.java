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

/**
 * Item of diagnostic information found in a DiagnosticEvent message.
 *
 */
public abstract class AbstractDiagnostic implements IDiagnostic {

	private static final String TS_SOURCE = "ts";

	/**
	 * Text of diagnostic message.
	 */
	private String text;

	/**
	 * The error code of the diagnostic message.
	 */
	private Integer code;

	private String category;

	/**
	 * The name of the plugin reporting the message.
	 */
	private String source;

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getFullText() {
		String text = getText();
		String source = getSource();
		return new StringBuilder("[").append(source).append("] ").append(text != null ? text : "").toString();
	}

	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public DiagnosticCategory getCategory() {
		return DiagnosticCategory.getCategory(category);
	}

	@Override
	public String getSource() {
		return source != null ? source : TS_SOURCE;
	}
}
