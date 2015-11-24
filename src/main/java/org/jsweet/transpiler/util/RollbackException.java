/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.transpiler.util;

import java.util.function.Consumer;

import com.sun.tools.javac.tree.JCTree;

/**
 * This exception can be thrown to rollback the scanning of an AST.
 * 
 * @author Renaud Pawlak
 */
public class RollbackException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private JCTree target;
	private Consumer<JCTree> onRollbacked;

	/**
	 * Rollback up to the target.
	 * 
	 * @param target
	 *            the target
	 * @param onRollbacked
	 *            the handler to be executed once rollbacked.
	 */
	public RollbackException(JCTree target, Consumer<JCTree> onRollbacked) {
		super();
		this.target = target;
		this.onRollbacked = onRollbacked;
	}

	/**
	 * Gets the target of the rollback.
	 */
	public JCTree getTarget() {
		return target;
	}

	/**
	 * Gets the rollback handler.
	 */
	public Consumer<JCTree> getOnRollbacked() {
		return onRollbacked;
	}

}
