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

import ts.internal.client.protocol.Request;
import ts.internal.client.protocol.Response;

public class LoggingInterceptor implements IInterceptor {

	private static final IInterceptor INSTANCE = new LoggingInterceptor();

	public static IInterceptor getInstance() {
		return INSTANCE;
	}

	@Override
	public void handleRequest(Request<?> request, String json, ITypeScriptServiceClient client) {
		outPrintln("-----------------------------------");
		outPrintln("TypeScript request#" + request.getCommand() + ": ");
		outPrintln(json);
	}

	@Override
	public void handleResponse(Response<?> response, String json, long ellapsedTime,
			TypeScriptServiceClient typeScriptServiceClient) {
		outPrintln("");
		outPrintln("TypeScript response#" + response.getCommand() + " with " + ellapsedTime + "ms: ");
		outPrintln(json);
		outPrintln("-----------------------------------");
	}

	@Override
	public void handleError(Throwable error, ITypeScriptServiceClient server, String methodName, long ellapsedTime) {
		errPrintln("");
		errPrintln("TypeScript error#" + methodName + " with " + ellapsedTime + "ms: ");
		printStackTrace(error);
		errPrintln("-----------------------------------");
	}

	protected void outPrintln(String line) {
		System.out.println(line);
	}

	protected void errPrintln(String line) {
		System.err.println(line);
	}

	protected void printStackTrace(Throwable error) {
		error.printStackTrace(System.err);
	}

}
