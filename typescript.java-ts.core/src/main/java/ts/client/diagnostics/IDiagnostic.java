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
import ts.utils.StringUtils;

/**
 * Diagnostic API.
 *
 */
public interface IDiagnostic {

	public enum DiagnosticCategory {
		Warning, Error, Message;

		public static DiagnosticCategory getCategory(String category) {
			if (!StringUtils.isEmpty(category)) {
				DiagnosticCategory[] values = DiagnosticCategory.values();
				for (int i = 0; i < values.length; i++) {
					DiagnosticCategory c = values[i];
					if (category.equalsIgnoreCase(c.name())) {
						return c;
					}
				}
			}
			return Error;
		}
	}

	/**
	 * Return text of diagnostic message.
	 * 
	 * @return text of diagnostic message.
	 */
	String getText();

	String getFullText();
	
	Location getStartLocation();

	Location getEndLocation();

	/**
	 * Returns the error code of the diagnostic message.
	 * 
	 * @return the error code of the diagnostic message.
	 */
	Integer getCode();

	/**
	 * 
	 * @return
	 */
	DiagnosticCategory getCategory();

	/**
	 * Return the name of the plugin reporting the message.
	 * 
	 * @return the name of the plugin reporting the message.
	 */
	String getSource();
}
