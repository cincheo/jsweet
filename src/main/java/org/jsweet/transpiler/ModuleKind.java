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
package org.jsweet.transpiler;

/**
 * An enumeration of the support module kinds.
 * 
 * @author Renaud Pawlak
 * @see JSweetTranspiler#setModuleKind(ModuleKind)
 */
public enum ModuleKind {
	/**
	 * The generated code is flat and does not use modules.
	 */
	none,
	/**
	 * The generated code uses <code>commonjs</code> module kind, which can run
	 * under <code>node</code> and be bundled for running in a Web browser.
	 * 
	 * @see JSweetTranspiler#setModuleKind(ModuleKind)
	 */
	commonjs,
	/**
	 * The generated code uses the <code>amd</code> module kind. This can run on
	 * <code>require.js</code> enabled Web browsers.
	 */
	amd,
	/**
	 * The generated code uses the <code>system</code> module kind.
	 */
	system,
	/**
	 * The generated code uses the <code>umd</code> (EcmaScript 6 Universal
	 * Module Definition) module kind.
	 */
	umd
}
