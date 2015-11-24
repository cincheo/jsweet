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
package source.overload;

public class WrongOverloads {

	private void draw() {
		System.out.println("tutu");
	}

	private void draw(String s) {
	}	
	
	int i;

	String getter() {
		return "";
	}
	
	void m() {
		// other statements are not allowed in overloading
		i = 0;
		this.m("");
	}
	
	void m(String s) {
		// calling a method is wrong for overloading
		this.m(getter(), 1);
	}

	// this method cannot overload the one with string
	void m(int i) {
	}
	
	void m(String s, int i) {
		s = "";
	}
	
	public WrongOverloads() {
		draw();
		draw("");
	}
	
}
