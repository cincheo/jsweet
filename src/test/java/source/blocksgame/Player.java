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
package source.blocksgame;

import static jsweet.dom.Globals.console;

import jsweet.dom.CanvasRenderingContext2D;
import jsweet.lang.Math;
import source.blocksgame.util.Collisions;
import source.blocksgame.util.Direction;
import source.blocksgame.util.MobileElement;
import source.blocksgame.util.Point;
import source.blocksgame.util.Vector;

public class Player extends MobileElement {

	private final static int WEIGHT = 10000000;
	boolean userControlling = false;
	Point requestedPosition = new Point(0, 0);
	public GameArea area;
	public String color;
	public Direction direction;
	private int hit = 0;

	public Vector calculateSpeedVector() {
		if (userControlling) {
			this.speedVector.x = this.requestedPosition.x - this.getPosition().x;
			this.speedVector.y = this.requestedPosition.y - this.getPosition().y;
			return this.speedVector;
		} else {
			this.speedVector.times(0.9);
			this.requestedPosition.x = this.position.x + this.speedVector.x;
			this.requestedPosition.y = this.position.y + this.speedVector.y;
			return speedVector;
		}
	}

	public double radius;

	public Player(GameArea area, String color, Direction direction, Point position, double radius) {
		super(position, WEIGHT, 0, 0);
		this.requestedPosition.x = position.x;
		this.requestedPosition.y = position.y;
		this.area = area;
		this.color = color;
		this.direction = direction;
		this.radius = radius;
		this.width = radius * 2;
		this.height = radius * 2;
	}

	private Point delta = new Point(0, 0);

	public void onInputDeviceDown(Point point) {
		if (this.isHitForUserControl(point)) {
			delta.x = position.x - point.x;
			delta.y = position.y - point.y;
			this.userControlling = true;
		}
	}

	static float f = 4;

	public void render(CanvasRenderingContext2D ctx) {
		ctx.save();
		ctx.fillStyle = hit > 0 ? "rgb(255,0,0)" : "rgba(255,0,0,0.4)";
		ctx.beginPath();
		// ctx.arc(this.getPosition().x, this.getPosition().y, this.radius -
		// hit, 0, Math.PI * 2);
		// ctx.fill();
		// ctx.beginPath();
		ctx.moveTo(position.x - radius, position.y - radius);
		ctx.lineTo(position.x + radius, position.y - radius);
		ctx.arc(position.x + radius, position.y - radius + radius / f, radius / f, -Math.PI / 2, Math.PI / 2);
		ctx.lineTo(position.x - radius, position.y - radius + radius / (f / 2));
		ctx.arc(position.x - radius, position.y - radius + radius / f, radius / f, Math.PI / 2, -Math.PI / 2);

		ctx.fill();
		ctx.strokeStyle = "white";
		ctx.stroke();
		ctx.beginPath();
		ctx.restore();
		if (hit > 0) {
			hit--;
		}
	}

	private Point _p = new Point(0, 0);

	public boolean checkHit(Ball ball) {
		_p.x = position.x + radius;
		_p.y = position.y - radius + radius / f;
		if (ball.position.distance(_p) < radius / f + ball.radius) {
			hit = 3;
			return true;
		}
		_p.x = position.x - radius;
		if (ball.position.distance(_p) < radius / f + ball.radius) {
			hit = 3;
			return true;
		}
		if (ball.position.x >= position.x - radius && ball.position.x <= position.x + radius) {
			if (ball.position.y < position.y - radius + radius / f && ball.position.y + ball.radius >= position.y - radius) {
				hit = 3;
				return true;
			}
			if (ball.position.y > position.y - radius + radius / f && ball.position.y - ball.radius <= position.y - radius + radius / (f / 2)) {
				hit = 3;
				return true;
			}
		}
		return false;
	}

	public void applyHit(Ball ball) {
		if (ball.position.x < position.x - radius) {
			console.info("left");
			Collisions.sphericCollision(new MobileElement(new Point(position.x - radius, position.y - radius + radius / f), WEIGHT, (radius / f) * 2,
					(radius / f) * 2), ball);
			return;
		}
		if (ball.position.x > position.x + radius) {
			console.info("right");
			Collisions.sphericCollision(new MobileElement(new Point(position.x + radius, position.y - radius + radius / f), WEIGHT, (radius / f) * 2,
					(radius / f) * 2), ball);
			return;
		}
		if (ball.position.x >= position.x - radius && ball.position.x <= position.x + radius) {
			console.info("straigth");
			ball.speedVector.y = -ball.speedVector.y;
			ball.speedVector.y += speedVector.y;
			ball.speedVector.x -= speedVector.x;
			return;
		}
	}

	public boolean isHitForUserControl(Point point) {
		return true; // this.getPosition().distance(point) < this.radius;
	}

	public void onInputDeviceUp() {
		this.userControlling = false;
	}

	public void onInputDeviceMove(Point point) {
		if (this.userControlling) {
			this.requestedPosition.x = point.x + delta.x;
			this.requestedPosition.y = point.y + delta.y;
		}
	}

}
