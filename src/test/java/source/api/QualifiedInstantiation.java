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
package source.api;

public class QualifiedInstantiation {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		jsweet.lang.Array<String> array = new jsweet.lang.Array<String>();
		jsweet.lang.Array<jsweet.lang.String> array2 = new jsweet.lang.Array<jsweet.lang.String>();
		jsweet.lang.String string = new jsweet.lang.String("1");
		jsweet.lang.Number number = new jsweet.lang.Number("3.0");
		jsweet.lang.Date date = new jsweet.lang.Date("2015-05-01");
		jsweet.lang.Error error = new jsweet.lang.Error("bloody error");
		jsweet.lang.RegExp regex = new jsweet.lang.RegExp("\\d", "g");
	}
}
