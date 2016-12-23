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
package java.lang.ref;

/**
 * This implements the reference API in a minimal way. In JavaScript, there is
 * no control over the reference and the GC. So this implementation's only
 * purpose is for compilation.
 */
public abstract class Reference<T> {

	private T referent;

	Reference(T referent) {
		this.referent = referent;
	}

	public T get() {
		return this.referent;
	}

	public void clear() {
		this.referent = null;
	}

}
