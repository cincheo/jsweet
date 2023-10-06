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
package ts;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TypeScript model kind modifier.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/services/types.ts
 *
 */
public enum ScriptElementKindModifier {

	none(""), publicMemberModifier("public"), privateMemberModifier("private"), protectedMemberModifier(
			"protected"), exportedModifier(
					"export"), ambientModifier("declare"), staticModifier("static"), abstractModifier("abstract");

	private static final Map<String, ScriptElementKindModifier> cache = Collections.unmodifiableMap(initializeCache());

	private final String name;

	private ScriptElementKindModifier(String name) {
		this.name = name;
	}

	private static Map<String, ScriptElementKindModifier> initializeCache() {
		Map<String, ScriptElementKindModifier> cache = new HashMap<>();
		ScriptElementKindModifier[] values = ScriptElementKindModifier.values();
		for (int i = 0; i < values.length; i++) {
			ScriptElementKindModifier value = values[i];
			cache.put(value.getName(), value);
		}
		return cache;
	}

	public String getName() {
		return name;
	}

	public static ScriptElementKindModifier getKindModifier(String modifier) {
		return cache.get(modifier);
	}
}
