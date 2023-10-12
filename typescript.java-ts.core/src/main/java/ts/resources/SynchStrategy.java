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
package ts.resources;

/**
 * Strategy for synchronize file content from the IDE editor and tsserver:
 * 
 * <ul>
 * <li>{{@value #CHANGE} : this strategy must be used if the IDE editor can
 * support change events when user modify the content of the editor: the IDE
 * editor send to the tsserver the change of the editor as soon as user modify
 * the content of the editor.</li>
 * <li>{{@value #RELOAD} : this strategy must be used if the IDE editor cannot
 * support change events when user modify the content of the editor: the
 * synchronization is done just before completion, hover, etc is executed: a
 * temporary file is created with the content of the editor and "reload" command
 * is sent to the tsserver by setting the path of the temporary file.</li>
 * <ul>
 *
 */
public enum SynchStrategy {

	CHANGE, RELOAD;
}
