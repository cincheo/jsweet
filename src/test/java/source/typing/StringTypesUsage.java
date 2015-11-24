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
package source.typing;

import static jsweet.util.StringTypes.pointerleave;
import jsweet.dom.CanvasRenderingContext2D;
import jsweet.dom.Element;
import jsweet.dom.HTMLCanvasElement;
import jsweet.util.StringTypes;

public class StringTypesUsage {

	void m1(Element e) {
		e.addEventListener(StringTypes.gotpointercapture, (pointerEvt) -> {
			return null;
		});
	}

	void m2(Element e) {
		e.addEventListener(pointerleave, (pointerEvt) -> {
			return null;
		});
	}
	
	void m3(HTMLCanvasElement canvas) {
		@SuppressWarnings("unused")
		CanvasRenderingContext2D ctx = canvas.getContext(StringTypes._2d);
	}

}
