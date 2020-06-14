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
package source.syntax;

import static jsweet.util.Lang.string;
import static source.syntax.Globals.toTitleCase;

import def.js.RegExp;
import def.js.String;

public class GlobalsInvocation {

	String firstName;
	String lastName;

	public String getFullName() {
		return toTitleCase(string(firstName + " " + lastName));
	}
	
	public void m() {
		Globals.m();
		source.syntax.Globals.m();
	}
}

/**
 * This is a doc comment to test comments.
 */
class Globals {
	/**
	 * A global method.
	 */
	public static String toTitleCase(String str) {
		return str.replace(new RegExp("/\\w\\S*/g"), (tok, i) -> {
			return tok.charAt(0).toUpperCase().concat(tok.substr(1).toLowerCase());
		});
	}
	public static void m() {};
	
    public final static int explicitFinalGlobal = 1;
    public static int implicitFinalGlobal = 1;
    public static int notFinalGlobal = 1;
    
    public static void init() {
        notFinalGlobal = 1;
    }
    
    
}
