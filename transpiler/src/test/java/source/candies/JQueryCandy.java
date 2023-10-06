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
package source.candies;

import static def.jquery.Globals.$;

import def.jquery.JQueryAjaxSettings;
import def.jquery.JQueryXHR;
import static jsweet.util.Lang.function;

@SuppressWarnings("all")
public class JQueryCandy {

	public static void main(String[] args) {
		$("p").append(" (end)");
		JQueryAjaxSettings jas1 = new JQueryAjaxSettings() {
			{
				url = "...";
				$set("success", function((Object data, String textStatus, JQueryXHR jqXHR) -> {
					return "...";
				}));
			}
		};

		// see #259
		JQueryAjaxSettings jas2 = new JQueryAjaxSettings() {
			{
				url = "...";
			}

			@Override
			public Object success(Object data, String textStatus, JQueryXHR jqXHR) {
				return "...";
			}
		};

	}

}
