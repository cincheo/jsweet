/*
 * Copyright 2014 Google Inc.
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

import static javaemul.internal.globals.Globals.Map;
import static jsweet.util.Globals.$new;

// TODO: remove this file!!

/**
 * A factory to create JavaScript Map instances.
 */
class InternalJsMapFactory {

	private static final Object jsMapCtor = getJsMapConstructor();

	private static def.js.Object getJsMapConstructor() { 
		return Map; 
	};

	/*-{
	// Firefox 24 & 25 throws StopIteration to signal the end of iteration.
	function isCorrectIterationProtocol() {
	  try {
	    return new Map().entries().next().done;
	  } catch(e) {
	    return false;
	  }
	}
	
	if (typeof Map === 'function' && Map.prototype.entries && isCorrectIterationProtocol()) {
	  return Map;
	} else {
	  return @InternalJsMapFactory::getJsMapPolyFill()();
	}
	}-*/;

	public static <V> InternalJsMap<V> newJsMap() {
		return $new(jsMapCtor);
	}
	/*-{
	return new @InternalJsMapFactory::jsMapCtor;
	}-*/;

	private InternalJsMapFactory() {
		// Hides the constructor.
	}
}
