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
package source.blocksgame.util;

import jsweet.lang.Math;

public class Vector {

	public double x;
	public double y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static Vector fromPolar(double radius, double angle) {
		return new Vector(Math.cos(angle) * radius, Math.sin(angle) * radius);
	}

	public static Vector toDiscreteDirection(Vector vector) {
		double x, y;
		if (vector.x > 0) {
			x = 1;
		} else if (vector.x < 0) {
			x = -1;
		} else {
			x = 0;
		}
		if (vector.y > 0) {
			y = 1;
		} else if (vector.y < 0) {
			y = -1;
		} else {
			y = 0;
		}
		return new Vector(x, y);
	}

	public Vector clone() {
		return new Vector(this.x, this.y);
	}

	public Vector add(Vector vector) {
		this.x += vector.x;
		this.y += vector.y;
		return this;
	}

	public Vector applyBounce(Vector hitObjectDirection) {
		this.x *= hitObjectDirection.x == 0 ? 1 : -1;
		this.y *= hitObjectDirection.y == 0 ? 1 : -1;
		return this;
	}

	public Vector times(double factor) {
		this.x *= factor;
		this.y *= factor;
		return this;
	}

	public Vector invert() {
		this.x *= -1;
		this.y *= -1;
		return this;
	}

	public Vector abs() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		return this;
	}

	public String toString() {
		return "vector(" + this.x + "," + this.y + ")";
	}

	public boolean equals(Vector vector) {
		return this.x == vector.x && this.y == vector.y;
	}

	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public Vector normalize() {
		double l = this.length();
		this.x /= l;
		this.y /= l;
		return this;
	}

	public double dotProduct(Vector vector) {
		return this.x * vector.x + this.y * vector.y;
	}

	public double angle() {
		return Math.atan2(this.y, this.x);
	}

}
