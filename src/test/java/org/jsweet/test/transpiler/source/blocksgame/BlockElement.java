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
package org.jsweet.test.transpiler.source.blocksgame;

import static jsweet.dom.Globals.document;

import org.jsweet.test.transpiler.source.blocksgame.util.AnimatedElement;
import org.jsweet.test.transpiler.source.blocksgame.util.Collisions;
import org.jsweet.test.transpiler.source.blocksgame.util.MobileElement;
import org.jsweet.test.transpiler.source.blocksgame.util.Point;
import org.jsweet.test.transpiler.source.blocksgame.util.Vector;

import jsweet.dom.CanvasRenderingContext2D;
import jsweet.dom.HTMLImageElement;

public class BlockElement extends AnimatedElement {

	public static int CELL_SIZE = 44;
	public static HTMLImageElement spriteBreakableBlock = (HTMLImageElement)document.getElementById("sprite-breakable-block");
	public static HTMLImageElement spriteUnbreakableBlock = (HTMLImageElement)document.getElementById("sprite-unbreakable-block");

	public double size;
	public GameArea area;
	public int hitstoBreak;
	private Vector gravity = null;
	public boolean playerDisabled = false;
	public double x;
	public double y;
	public int cellX;
	public int cellY;

	public BlockElement(int hitsForBreaking) {
		this.hitstoBreak = hitsForBreaking;
	}

	protected void drawBreakable(CanvasRenderingContext2D ctx) {
		ctx.drawImage(spriteBreakableBlock, 0, 0, 100, 100, x, y, CELL_SIZE, CELL_SIZE);
	}

	protected void drawUnbreakable(CanvasRenderingContext2D ctx) {
		ctx.drawImage(spriteUnbreakableBlock, 0, 0, 100, 100, x, y, CELL_SIZE, CELL_SIZE);
	}

	public String toString() {
		return "BLOCK(" + this.hitstoBreak + ")";
	}

	protected void clear(CanvasRenderingContext2D ctx) {
		ctx.clearRect(this.x, this.y, this.size, this.size);
	}

	public void render(CanvasRenderingContext2D ctx) {
		if (this.isVisible()) {
			if (this.hitstoBreak == -1) {
				this.drawUnbreakable(ctx);
			} else {
				this.drawBreakable(ctx);
			}
		}
	}

	@Override
	public void renderAnimation(CanvasRenderingContext2D animationCtx, CanvasRenderingContext2D areaCtx) {
		if (this.hitstoBreak == -1) {
			this.drawUnbreakable(animationCtx);
			animationCtx.beginPath();
			animationCtx.fillStyle = "rgba(255,255,255,0.4)";
			animationCtx.rect(this.x, this.y, this.size, this.size);
			animationCtx.fill();
		} else {
			if (this.getAnimationStep() == 1) {
				render(areaCtx);
			}
			double factor = this.getRemainingAnimationSteps() / this.getAnimationStepCount();
			animationCtx.save();
			animationCtx.translate(this.x + this.size / 2, this.y + this.size / 2);
			animationCtx.scale(factor, factor);
			animationCtx.translate(-this.x - this.size / 2, -this.y - this.size / 2);
			this.drawBreakable(animationCtx);
			animationCtx.restore();
		}
		this.nextAnimationStep();
	}

	public boolean isVisible() {
		return this.hitstoBreak != 0;
	}

	public boolean contains(Point point, double radius) {
		if (point.x + radius > this.x + this.size) {
			return false;
		}
		if (point.x - radius < this.x) {
			return false;
		}
		if (point.y + radius > this.y + this.size) {
			return false;
		}
		if (point.y - radius < this.y) {
			return false;
		}
		return true;
	}

	public boolean overlaps(Point point, double radius) {
		if (point.x + radius > this.x + this.size) {
			return false;
		}
		if (point.x - radius < this.x) {
			return false;
		}
		if (point.y + radius > this.y + this.size) {
			return false;
		}
		if (point.y - radius < this.y) {
			return false;
		}
		return true;
	}

	public boolean hit(Ball ball, Vector hitObjectDirection, Point hitPoint) {
		if (this.hitstoBreak > 0) {
			this.hitstoBreak--;
		}
		if (hitPoint == null) {
			ball.speedVector.applyBounce(hitObjectDirection);
		} else {
			MobileElement refMobile = new MobileElement(hitPoint, 10000000, 0, 0);
			Collisions.sphericCollision(refMobile, ball);
		}
		if (this.hitstoBreak == -1) {
			this.initAnimation(2);
		} else {
			this.area.blockCount--;
			this.area.remainingBlocks.innerHTML="Blocks: "+this.area.blockCount;
			if(this.area.blockCount==0) {
				this.area.end(0);
			}
			this.area.clearAll = true;
			this.initAnimation(5);
		}
		return true;
	}

	public void onBallOver(Ball ball) {
		if (this.gravity != null) {
			ball.speedVector.add(this.gravity);
		}
	}

	public Point center() {
		return new Point(this.x + this.size / 2, this.y + this.size / 2);
	}

	public void setGravity(Vector gravity) {
		this.gravity = gravity;
	}

	public void onAddedToArea() {
	}

}