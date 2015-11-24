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

public class Rectangle {

	public double x1;
	public double y1;

	public double x2;
	public double y2;

	public Rectangle(double x, double y, double width, double height) {
		super();
		this.x1 = x;
		this.y1 = y;
		this.x2 = x + width;
		this.y2 = y + height;
	}

	public static Rectangle rectangle(double x1, double y1, double x2, double y2) {
		return new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
	}

	public Rectangle union(Rectangle rectangle) {
		return Rectangle.rectangle(Math.min(this.x1, rectangle.x1), Math.min(this.y1, rectangle.y1), Math.max(this.x2, rectangle.x2),
				Math.max(this.y2, rectangle.y2));
	}

	public boolean intersects(Rectangle rectangle) {
		return ((this.x1 <= rectangle.x2) && (rectangle.x1 <= this.x2) && (this.y1 <= rectangle.y2) && (rectangle.y1 <= this.y2));
	}

	public Rectangle discrete(double unit) {
		this.x1 = Math.floor(this.x1 / unit);
		this.y1 = Math.floor(this.y1 / unit);
		this.x2 = Math.floor(this.x2 / unit);
		this.y2 = Math.floor(this.y2 / unit);
		return this;
	}

	public boolean contains(Point point) {
		return this.x1 <= point.x && this.x2 >= point.x && this.y1 <= point.y && this.y2 >= point.y;
	}

	@Override
	public String toString() {
		return "RECT[" + x1 + "," + y1 + "," + x2 + "," + y2 + "]";
	}

	public double getWidth() {
		return x2 - x1;
	}

	public double getHeight() {
		return y2 - y1;
	}

}
