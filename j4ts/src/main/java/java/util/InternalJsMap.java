/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package java.util;
import static jsweet.util.Globals.$apply;
import static jsweet.util.Globals.$get;

// TODO(goktug): These classes should be interfaces with defender methods instead.
class InternalJsMap<V> {
	static class Iterator<V> {
		public IteratorEntry<V> next() { return null; };
	}

	static class IteratorEntry<V> {
		public Object[] value;
		public boolean done;
	}

	public V get(int key) {return null;};

	public V get(String key) {return null;};

	public void set(int key, V value) {};

	public void set(String key, V value) {};

	public final void delete(int key) {
		JsHelper.delete(this, key);
	}

	public final void delete(String key) {
		JsHelper.delete(this, key);
	}

	public Iterator<V> entries() { return null; };

	// Calls to delete are via brackets to be compatible with old browsers where
	// delete is keyword.
	private static class JsHelper {
		static void delete(InternalJsMap<?> obj, int key) {
			$apply($get(obj, "delete"), key);
		};

		static void delete(InternalJsMap<?> obj, String key) {
			$apply($get(obj, "delete"), key);
		};
	}
}