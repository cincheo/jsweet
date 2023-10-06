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
package ts.nodejs;

import java.io.File;
import java.util.List;

/**
 * This adapter class provides default implementations for the methods described
 * by the {@link INodejsProcessListener} interface.
 * 
 * Classes that wish to deal with event can extend this class and override only
 * the methods which they are interested in.
 * 
 */
public class NodejsProcessAdapter implements INodejsProcessListener {

	@Override
	public void onCreate(INodejsProcess process, List<String> commands, File projectDir) {
	}

	@Override
	public void onStart(INodejsProcess process) {
	}

	@Override
	public void onMessage(INodejsProcess process, String response) {

	}

	@Override
	public void onStop(INodejsProcess process) {
	}

	@Override
	public void onError(INodejsProcess process, String line) {
	}

}
