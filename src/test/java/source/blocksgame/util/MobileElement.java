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
package source.blocksgame.util;


public class MobileElement extends AnimatedElement {

	public Point position;
	public double weight;
	public Vector speedVector = new Vector(0, 0);
	public double width = 0;
	public double height = 0;

	public MobileElement(Point position, double weight, double width, double height) {
		this.position = position;
		this.weight = weight;
		this.width = width;
		this.height = height;
	}

	public void moveTo(double x, double y) {
		this.position.x = x;
		this.position.y = y;
	}

	public void move(double dx, double dy) {
		this.position.x += dx;
		this.position.y += dy;
	}

	public Point getPosition() {
		return this.position;
	}

	public String toString() {
		return "mobile(" + this.position + ";" + this.speedVector + ")";
	}

}
