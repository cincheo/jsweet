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
package ts.internal.client.protocol;

/**
 * 
 * TypeScript Server message type.
 *
 */
public enum MessageType {

	request, response, event;

	public static MessageType getType(String type) {
		try {
			return MessageType.valueOf(type);
		} catch (Throwable e) {
			return null;
		}
	}
}
