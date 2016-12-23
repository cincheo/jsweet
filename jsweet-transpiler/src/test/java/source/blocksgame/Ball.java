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
package source.blocksgame;

import static jsweet.util.Globals.union;

import jsweet.dom.CanvasRenderingContext2D;
import jsweet.lang.Math;
import source.blocksgame.util.MobileElement;
import source.blocksgame.util.Point;
import source.blocksgame.util.Vector;

public class Ball extends MobileElement {

	public static double MIN_SPEED = 1;
	public static double MAX_SPEED = 20;

	public Vector speedVector = new Vector(0, 0);
	public GameArea area;
	public double radius;

	public Ball(GameArea area, double radius, Point position, Vector normalizedDirection, double speed) {
		super(position, 1, radius * 2, radius * 2);
		this.setSpeedVector(normalizedDirection, speed);
		this.area = area;
		this.radius = radius;
	}

	private void draw(CanvasRenderingContext2D ctx) {
		ctx.save();
		ctx.beginPath();
		ctx.fillStyle = union("white");
		ctx.arc(this.position.x, this.position.y, this.radius, 0, Math.PI * 2, false);
		ctx.closePath();
		ctx.fill();

		ctx.beginPath();
		ctx.arc(this.position.x, this.position.y, this.radius, 0, Math.PI * 2, false);
		ctx.clip();

		ctx.beginPath();
		ctx.strokeStyle = union("black");
		ctx.lineWidth = this.radius * 0.1;
		ctx.shadowBlur = this.radius * 0.4;
		ctx.shadowColor = "black";
		ctx.shadowOffsetX = this.position.x + this.radius * 0.8;
		ctx.shadowOffsetY = this.position.y + this.radius * 0.8;
		ctx.arc(-this.radius, -this.radius, this.radius + this.radius * 0.01, 0, Math.PI * 2, false);
		ctx.stroke();
		ctx.restore();
	}

	public void render(CanvasRenderingContext2D ctx) {
		this.draw(ctx);
	}

	public void setSpeedVector(Vector normalizedDirection, double speed) {
		this.speedVector.x = normalizedDirection.x * speed;
		this.speedVector.y = normalizedDirection.y * speed;
	}

	public void setSpeed(double speed) {
		this.speedVector.normalize().times(speed);
	}

	public double getSpeed() {
		return this.speedVector.length();
	}

	public String toString() {
		return "ball(" + this.radius + "," + this.getPosition() + ")";
	}
}
