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

public class Labels {

	void m() {
		int i = 0;
		a: for (i = 0; i < 100; i++) {
			if (i == 50) {
				break a;
			} else {
				continue a;
			}
		}
		assert i == 50;
		System.out.println(i);
	}
	
	void unused() {
		b: 
			for(int i=0;i<10;i++) {
				
			}
	}

	{
		c: 
			for(int i=0;i<10;i++) {
				
			}
	}
	
	{
		int i = 0;
		a: for (i = 0; i < 100; i++) {
			if (i == 50) {
				break a;
			} else {
				continue a;
			}
		}
		assert i == 50;
		System.out.println(i);
	}

	static {
		c: 
			for(int i=0;i<10;i++) {
				
			}
	}
	
	static {
		int i = 0;
		a: for (i = 0; i < 100; i++) {
			if (i == 50) {
				break a;
			} else {
				continue a;
			}
		}
		assert i == 50;
		System.out.println(i);
	}
	
	public static void main(String[] args) {
		new Labels().m();
	}

}
