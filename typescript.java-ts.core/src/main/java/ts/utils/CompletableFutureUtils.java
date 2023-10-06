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
package ts.utils;

import java.util.concurrent.CompletableFuture;

/**
 * Utilities for {@link CompletableFuture}.
 */
public class CompletableFutureUtils {

	public static void cancel(CompletableFuture<?> promise) {
		// cancel future if needed
		if (promise != null && !promise.isDone()) {
			promise.cancel(true);
		}
	}
}
