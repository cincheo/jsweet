/* Copyright 2015 CINCHEO SAS
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

import java.util.HashSet;
import java.util.Set;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.TreeScanner;

/**
 * A utility scanner that grabs all references to types used within a code tree.
 * 
 * @author Renaud Pawlak
 */
public class ReferenceGrabber extends TreeScanner {

	/**
	 * The grabbed references.
	 */
	public Set<TypeSymbol> referencedTypes = new HashSet<>();

	@Override
	public void visitNewClass(JCNewClass newClass) {
		add(newClass.clazz.type.tsym);
		super.visitNewClass(newClass);
	}

	@Override
	public void visitSelect(JCFieldAccess fieldAccess) {
		if (fieldAccess.selected.type != null && (fieldAccess.selected.type.tsym instanceof ClassSymbol)) {
			add(fieldAccess.selected.type.tsym);
		}
		super.visitSelect(fieldAccess);
	}

	private void add(TypeSymbol type) {
		referencedTypes.add(type);
	}

}
