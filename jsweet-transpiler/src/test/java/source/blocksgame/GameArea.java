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

import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.document;
import static jsweet.util.Globals.array;
import static jsweet.util.Globals.union;
import static jsweet.util.StringTypes._2d;

import jsweet.dom.CanvasRenderingContext2D;
import jsweet.dom.Event;
import jsweet.dom.HTMLElement;
import jsweet.dom.MouseEvent;
import jsweet.dom.Touch;
import jsweet.dom.TouchEvent;
import jsweet.lang.Date;
import jsweet.lang.Math;
import source.blocksgame.util.Direction;
import source.blocksgame.util.Point;
import source.blocksgame.util.Rectangle;

public class GameArea {

	public HTMLElement sprites;

	public double naturalSpeed = 8;

	public double maxSpeed = 20;

	public BlockElement[][] positions;

	public Ball ball;

	public Player player;

	public Date currentDate = new Date();

	public boolean finished;

	public int winner = -1;

	public int cols;
	public int rows;
	public double positionSize;

	private int topMargin = 0;
	private int leftMargin = 0;
	private int rightMargin = 0;
	private int bottomMargin = 0;

	private Date initialDate = null;
	private double currentTime = 0;
	public int blockCount = 0;

	HTMLElement elapsedTime;
	public HTMLElement remainingBlocks;

	public boolean clearAll = true;

	public Factory factory;

	public CanvasRenderingContext2D areaLayerCtx;
	public CanvasRenderingContext2D topLayerCtx;
	public CanvasRenderingContext2D backgroundLayerCtx;
	public CanvasRenderingContext2D ballsLayerCtx;

	GameArea(Factory factory, int cols, int rows) {
		console.info("creating game area...");
		this.factory = factory;
		this.areaLayerCtx = factory.getGameManager().areaLayer.getContext(_2d);
		this.topLayerCtx = factory.getGameManager().topLayer.getContext(_2d);
		this.backgroundLayerCtx = factory.getGameManager().backgroundLayer.getContext(_2d);
		this.ballsLayerCtx = factory.getGameManager().ballsLayer.getContext(_2d);
		this.cols = cols;
		this.rows = rows;
		this.positionSize = GameManager.SIZE;
		this.positions = new BlockElement[cols][rows];
		for (int i = 0; i < cols; i++) {
			BlockElement[] line = {};
			array(this.positions).push(line);
		}
		this.remainingBlocks = document.getElementById("blocks");
		for (int i = 0; i < this.cols; i++) {
			for (int j = 0; j < this.rows; j++) {
				this.addBlock(new BlockElement(0), i, j);
			}
		}
		this.ball = null;
		this.elapsedTime = document.getElementById("time");
		this.sprites = document.getElementById("sprites");
		this.currentTime = 0;
		this.elapsedTime.innerHTML = "0s";
	}

	public void changeSize(int cols, int rows) {
		BlockElement[][] newPositions = new BlockElement[cols][rows];
		for (int i = 0; i < cols; i++) {
			BlockElement[] line = {};
			array(newPositions).push(line);
		}
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				if (i < this.cols && j < this.rows) {
					newPositions[i][j] = this.positions[i][j];
				} else {
					BlockElement block = new BlockElement(0);
					newPositions[i][j] = block;
					block.cellX = i;
					block.cellY = j;
					block.size = this.positionSize;
					block.area = this;
					block.x = i * this.positionSize;
					block.y = j * this.positionSize;
				}
			}
		}
		this.cols = cols;
		this.rows = rows;
		this.positions = newPositions;
	}

	public void initDate() {
		if (this.initialDate == null) {
			this.initialDate = new Date();
		}
	}

	public void regulateSpeedWhenBounced(Ball ball) {
		double speed = ball.getSpeed();
		if (ball.getSpeed() > this.naturalSpeed) {
			ball.setSpeed(this.naturalSpeed + (speed - this.naturalSpeed) / 1.5);
		}
	}

	public void regulateSpeed(Ball ball) {
		if (ball.getSpeed() > this.maxSpeed) {
			ball.setSpeed(this.maxSpeed);
		}
		ball.speedVector.y += 0.1;
	}

	public void createDefaultBorders(boolean sideBorders) {
		for (int i = 0; i < this.rows; i++) {
			this.addBlock(new BlockElement(-1), 0, i);
			this.addBlock(new BlockElement(-1), this.cols - 1, i);
		}
		if (sideBorders) {
			for (int i = 1; i < this.cols - 1; i++) {
				this.addBlock(new BlockElement(-1), i, 0);
				this.addBlock(new BlockElement(-1), i, this.rows - 1);
			}
		}
	}

	public void createBorders(Direction side) {
		if (side == Direction.NORTH) {
			for (int i = 1; i < this.cols - 1; i++) {
				this.addBlock(new BlockElement(-1), i, 0);
			}
		} else if (side == Direction.SOUTH) {
			for (int i = 1; i < this.cols - 1; i++) {
				this.addBlock(new BlockElement(-1), i, this.rows - 1);
			}
		} else if (side == Direction.EAST) {
			for (int i = 0; i < this.rows; i++) {
				this.addBlock(new BlockElement(-1), 0, i);
			}
		} else if (side == Direction.WEST) {
			for (int i = 0; i < this.rows; i++) {
				this.addBlock(new BlockElement(-1), this.cols - 1, i);
			}
		}
	}

	public void addBlock(BlockElement block, int x, int y) {
		this.positions[x][y] = block;
		block.cellX = x;
		block.cellY = y;
		block.size = this.positionSize;
		block.area = this;
		block.x = x * this.positionSize;
		block.y = y * this.positionSize;
		block.onAddedToArea();
		this.clearAll = true;
		if (block.hitstoBreak > 0) {
			blockCount++;
			this.remainingBlocks.innerHTML="Blocks: "+blockCount;
		}
	}

	public void disablePlayer(int x, int y) {
		BlockElement element = this.positions[x][y];
		if (element == null) {
			element = new BlockElement(0);
			this.addBlock(element, x, y);
		}
		element.playerDisabled = true;
	}

	public boolean isPlayerDisabled(int x, int y) {
		BlockElement element = this.positions[x][y];
		if (element == null) {
			return false;
		} else {
			return element.playerDisabled;
		}
	}

	public void removeBlock(int x, int y) {
		BlockElement b = positions[x][y];
		if (b != null && b.hitstoBreak > 0) {
			blockCount--;
			this.remainingBlocks.innerHTML="Blocks: "+blockCount;
		}
		this.addBlock(new BlockElement(0), x, y);
		this.clearAll = true;
	}

	public double getWidth() {
		return this.cols * this.positionSize + this.leftMargin + this.rightMargin;
	}

	public double getHeight() {
		return this.rows * this.positionSize + this.topMargin + this.bottomMargin;
	}

	public double getGridWidth() {
		return this.cols * this.positionSize;
	}

	public double getGridHeight() {
		return this.rows * this.positionSize;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
		ball.area = this;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public BlockElement getElementAt(int cellX, int cellY) {
		if (cellX < 0 || cellX >= this.cols || cellY < 0 || cellY >= this.rows) {
			return null;
		}
		return this.positions[cellX][cellY];
	}

	public void convertPositionToDiscrete(Point point) {
		point.x = Math.floor(point.x / this.positionSize);
		point.y = Math.floor(point.y / this.positionSize);
	}

	public int convertCoordToDiscrete(double coord) {
		return (int) Math.floor(coord / this.positionSize);
	}

	Rectangle boundingRectangle = new Rectangle(0, 0, 0, 0);

	public void invalidateCell(int x, int y, boolean invalidate) {
		if (this.positions[x] != null && this.positions[x][y] != null) {
			this.positions[x][y].invalidated = invalidate;
		}
	}

	public void renderAll() {
		areaLayerCtx.clearRect(0, 0, this.getWidth(), this.getHeight());
		for (int i = 0; i < this.cols; i++) {
			for (int j = 0; j < this.rows; j++) {
				this.positions[i][j].render(areaLayerCtx);
			}
		}
		this.renderBall();
	}

	public void render() {
		boolean clearingArea = clearAll;
		this.renderBall();
		if (clearAll) {
			areaLayerCtx.clearRect(0, 0, this.getWidth(), this.getHeight());
			clearAll = false;
		}
		for (int i = 0; i < this.cols; i++) {
			for (int j = 0; j < this.rows; j++) {
				if (clearingArea) {
					this.positions[i][j].render(areaLayerCtx);
				}
				if (this.positions[i][j].invalidated) {
					this.positions[i][j].renderAnimation(ballsLayerCtx, areaLayerCtx);
				}
			}
		}

		this.renderPlayer();
		this.renderMessage();
	}

	public void renderMessage() {
		if (this.message == null) {
			return;
		}
		areaLayerCtx.save();
		areaLayerCtx.font = "14px impact";
		areaLayerCtx.textAlign = "center";
		areaLayerCtx.fillStyle = union("black");
		double margin = this.positionSize * 2;
		areaLayerCtx.fillRect(margin, (this.rows * this.positionSize) / 2 - 20, (this.cols * this.positionSize) - 2 * margin, 40);
		areaLayerCtx.fillStyle = union("white");
		areaLayerCtx.fillText(this.message, (this.cols * this.positionSize) / 2, (this.rows * this.positionSize) / 2);
		areaLayerCtx.restore();
	}

	public void renderBall() {
		ballsLayerCtx.clearRect(0, 0, this.getWidth(), this.getHeight());
		if (ball != null) {
			ball.render(ballsLayerCtx);
		}
	}

	public void renderPlayer() {
		topLayerCtx.clearRect(0, 0, this.getWidth(), this.getHeight());
		if (player != null) {
			player.render(topLayerCtx);
		}
	}

	public void dump() {
		console.info("dumping game area: ");
		console.info(" - player " + player.color + ": " + player.getPosition() + " - " + player.speedVector);
		console.info(" - ball: " + ball.getPosition() + " - " + ball.speedVector);
	}

	private double calculateMaxSpeedCoord() {
		double maxSpeedCoord = 0;
		maxSpeedCoord = Math.max(maxSpeedCoord, Math.max(Math.abs(player.speedVector.x), Math.abs(player.speedVector.y)));
		maxSpeedCoord = Math.max(maxSpeedCoord, Math.max(Math.abs(ball.speedVector.x), Math.abs(ball.speedVector.y)));
		return maxSpeedCoord;
	}

	public boolean contains(Point point) {
		return point.x >= 0 && point.x < this.cols * this.positionSize && point.y >= 0 && point.y < this.rows * this.positionSize;
	}

	private Point _tmpPoint = new Point(0, 0);
	private Point _oldPosition = new Point(0, 0);
	private Point ballLimit = new Point(0, 0);
	private Point _currentAreaPosition = new Point(0, 0);
	private Point _otherAreaPosition = new Point(0, 0);

	public void calculateNextPositions() {
		if (initialDate != null) {
			double newCurrentTime = Math.floor((new Date().getTime() - this.initialDate.getTime()) / 1000);
			if (newCurrentTime > this.currentTime) {
				this.currentTime = newCurrentTime;
				elapsedTime.innerHTML = this.currentTime + "s";
			}
		}

		player.calculateSpeedVector();
		double stepCount = this.calculateMaxSpeedCoord();
		if (stepCount < 1) {
			stepCount = 1;
		}

		if (stepCount != 0) {
			double stepValue = 1 / stepCount;
			for (int step = 1; step <= stepCount; step++) {

				player.move(player.speedVector.x * stepValue, player.speedVector.y * stepValue);

				boolean hit = false;
				_currentAreaPosition.x = ball.position.x;
				_currentAreaPosition.y = ball.position.y;
				this.convertPositionToDiscrete(_currentAreaPosition);
				BlockElement element;
				Direction direction;
				this._oldPosition.x = ball.getPosition().x;
				this._oldPosition.y = ball.getPosition().y;
				ball.move(ball.speedVector.x * stepValue, ball.speedVector.y * stepValue);

				if (player.checkHit(ball)) {
					console.info("player hits ball! ");
					player.applyHit(ball);
					ball.moveTo(ball.getPosition().x + player.speedVector.x * stepValue, ball.getPosition().y + player.speedVector.y * stepValue);
					while(player.checkHit(ball)) {
						ball.move(ball.speedVector.x * stepValue, ball.speedVector.y * stepValue);
					}
				}
				
				for (int i = 0; i < Direction.straightDirections.length; i++) {
					direction = Direction.straightDirections[i];
					this.ballLimit.x = ball.getPosition().x;
					this.ballLimit.y = ball.getPosition().y;
					this.ballLimit.add(ball.radius * direction.normalized.x, ball.radius * direction.normalized.y);
					this.convertPositionToDiscrete(this.ballLimit);
					_otherAreaPosition.x = _currentAreaPosition.x;
					_otherAreaPosition.y = _currentAreaPosition.y;
					_otherAreaPosition.add(direction.x, direction.y);
					element = this.getElementAt((int) this._otherAreaPosition.x, (int) this._otherAreaPosition.y);
					// there is an element in that direction
					if (element != null && element.isVisible()) {
						// wall-hit case (simple bounce)
						if (_otherAreaPosition.equals(ballLimit)) {
							hit = element.hit(ball, direction.normalized, null);
							if (hit) {
								break;
							}
						}
					}
				}
				if (!hit) {
					for (int i = 0; i < Direction.oblicDirections.length; i++) {
						direction = Direction.oblicDirections[i];
						_otherAreaPosition.x = _currentAreaPosition.x;
						_otherAreaPosition.y = _currentAreaPosition.y;
						_otherAreaPosition.add(direction.x, direction.y);
						element = this.getElementAt((int) this._otherAreaPosition.x, (int) this._otherAreaPosition.y);
						// there is an element in that direction
						if (element != null && element.isVisible()) {
							// corner-hit case (simple bounce)
							this._tmpPoint.x = _otherAreaPosition.x;
							this._tmpPoint.y = _otherAreaPosition.y;
							this._tmpPoint.times(this.positionSize).add(this.positionSize / 2, this.positionSize / 2);
							this._tmpPoint.add(-direction.x * this.positionSize / 2, -direction.y * this.positionSize / 2);
							if (ball.position.distance(this._tmpPoint) <= ball.radius) {
								hit = element.hit(ball, direction.normalized, this._tmpPoint);
								if (hit) {
									break;
								}
							}
						}
					}
				}
				if (hit) {
					// console.info("regulate after hit");
					ball.moveTo(this._oldPosition.x, this._oldPosition.y);
					this.regulateSpeedWhenBounced(ball);
				}
				this._tmpPoint.x = ball.getPosition().x;
				this._tmpPoint.y = ball.getPosition().y;
				this.convertPositionToDiscrete(this._tmpPoint);
				element = this.getElementAt((int) this._tmpPoint.x, (int) this._tmpPoint.y);
				if (element != null) {
					element.onBallOver(ball);
				}
				this._oldPosition.x = ball.getPosition().x;
				this._oldPosition.y = ball.getPosition().y;
				if (this._oldPosition.x + ball.radius < 0 || this._oldPosition.x - ball.radius > this.cols * positionSize
						|| this._oldPosition.y + ball.radius < 0 || this._oldPosition.y - ball.radius > this.rows * positionSize) {
					this.end(-1);
				}
			}
		}
		this.regulateSpeed(ball);
	}

	private String message = null;

	public void setMessage(String message) {
		this.message = message;
	}

	public Point positionInPage;

	public void onInputDeviceDown(Event event, boolean touchDevice) {
		if (this.initialDate == null) {
			this.initialDate = new Date();
		}
		Point point;
		if (touchDevice) {
			for (int i = 0; i < ((TouchEvent) event).changedTouches.length; i++) {
				Touch t = ((TouchEvent) event).changedTouches.item(i);
				point = this.factory.getGameManager().deviceToWorld(t.pageX, t.pageY);
				player.onInputDeviceDown(point);
			}
		} else {
			point = this.factory.getGameManager().deviceToWorld(((MouseEvent) event).pageX, ((MouseEvent) event).pageY);
			player.onInputDeviceDown(point);
		}
	}

	public void onInputDeviceUp(Event event, boolean touchDevice) {
		player.onInputDeviceUp();
	}

	public void onInputDeviceMove(Event event, boolean touchDevice) {
		if (touchDevice) {
			for (int i = 0; i < ((TouchEvent) event).changedTouches.length; i++) {
				Touch t = ((TouchEvent) event).changedTouches.item(i);
				Point point = this.factory.getGameManager().deviceToWorld(t.pageX, t.pageY);
				player.onInputDeviceMove(point);
			}
		} else {
			Point point = this.factory.getGameManager().deviceToWorld(((MouseEvent) event).pageX, ((MouseEvent) event).pageY);
			player.onInputDeviceMove(point);
		}
	}

	public void end(int winner) {
		this.finished = true;
		this.winner = winner;
	}

	public boolean inBounds(Point position) {
		return position.x >= 0 && position.x < this.cols && position.y >= 0 && position.y < this.rows;
	}

}
