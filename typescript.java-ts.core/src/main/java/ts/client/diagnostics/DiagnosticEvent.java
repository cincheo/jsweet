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

import ts.client.Event;

/**
 * Event message for "syntaxDiag" and "semanticDiag" event types. These events
 * provide syntactic and semantic errors for a file.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class DiagnosticEvent extends Event<DiagnosticEventBody> {

	public String getKey() {
		return getEvent() + "_" + getBody().getFile();
	}
}
