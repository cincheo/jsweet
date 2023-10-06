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

import ts.client.Location;

/**
 * Represents diagnostic info that includes location of diagnostic in two forms
 * - start position and length of the error span - startLocation and endLocation
 * - a pair of Location objects that store start/end line and offset of the
 * error span.
 */
public class DiagnosticWithLinePosition extends AbstractDiagnostic {

	private Integer start;

	private Integer end;

	/**
	 * Starting file location at which text applies.
	 */
	private Location startLocation;

	/**
	 * The last file location at which the text applies.
	 */
	private Location endLocation;

	/**
	 * Text of diagnostic message.
	 */
	private String message;

	@Override
	public Location getStartLocation() {
		return startLocation;
	}

	@Override
	public Location getEndLocation() {
		return endLocation;
	}

	public Integer getStart() {
		return start;
	}

	public Integer getEnd() {
		return end;
	}

	@Override
	public String getText() {
		return message != null ? message : super.getText();
	}

}
