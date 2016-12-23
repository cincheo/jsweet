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

import static def.dom.Globals.console;
import static def.dom.Globals.document;
import static jsweet.util.StringTypes.mousedown;
import static jsweet.util.StringTypes.mousemove;
import static jsweet.util.StringTypes.mouseup;
import static jsweet.util.StringTypes.touchend;
import static jsweet.util.StringTypes.touchmove;
import static jsweet.util.StringTypes.touchstart;
import static source.blocksgame.Globals.animate;

import def.dom.Event;
import def.dom.HTMLCanvasElement;
import def.dom.HTMLElement;
import def.dom.MouseEvent;
import def.dom.NodeList;
import def.dom.TouchEvent;
import def.js.Math;
import source.blocksgame.util.Point;

public class GameManager {

	public static final double SIZE = 46;
	public static final double TOUCH_SIZE = 46;

	public static final int FONT_SIZE = 24;

	private GameArea area;
	public int currentLevel = 0;
	private boolean paused;
	public String playerClassName = "IntuitivePlayer";

	public HTMLCanvasElement areaLayer;
	// private CanvasRenderingContext2D gameContext;
	public HTMLCanvasElement topLayer;
	public HTMLCanvasElement backgroundLayer;
	public HTMLCanvasElement ballsLayer;
	private HTMLElement textRow;

	private Factory factory;

	private HTMLElement gameRow;
	private HTMLElement gameContent;

	public static Point findPos(HTMLElement obj) {
		int curleft = 0, curtop = 0;
		if (obj.offsetParent != null) {
			do {
				curleft += obj.offsetLeft;
				curtop += obj.offsetTop;
			} while ((obj = (HTMLElement) obj.offsetParent) != null);
			return new Point(curleft, curtop);
		}
		return null;
	}

	public GameManager() {
		this.factory = new Factory(this);
		this.areaLayer = (HTMLCanvasElement) document.getElementById("areaLayer");
		this.topLayer = (HTMLCanvasElement) document.getElementById("topLayer");
		this.ballsLayer = (HTMLCanvasElement) document.getElementById("ballsLayer");
		this.backgroundLayer = (HTMLCanvasElement) document.getElementById("backgroundLayer");
		this.textRow = document.getElementById("textRow");
		this.gameRow = document.getElementById("gameRow");
		this.gameContent = document.getElementById("gameContent");
		this.installListeners();
	}

	public void init() {
		this.areaLayer.style.border = "";
		this.textRow.style.display = GameManager.DISPLAY_STYLE;
		this.initGame();
	}

	public GameArea getCurrentLevel() {
		return this.area;
	}

	private static final String DISPLAY_STYLE = "block";

	public void onLevelEnded() {
		console.info("level " + this.currentLevel + " ended: " + area.winner);
		GameArea area = this.getCurrentLevel();
		area.setMessage("Game ended.");
		if (area.winner == 0) {
			area.setMessage("Congratulations!");
		} else {
			area.setMessage("You loose. Try again.");
		}
		area.render();
	}

	public void initGame() {
		console.info("initializing game...");
		this.area = this.factory.createLevel();
		this.layoutGame();
	}

	double scale = 1;

	private void layoutGame() {
		// two passes a require to calculate actual scale
		this.doLayoutGame();
		this.doLayoutGame();
	}

	private void doLayoutGame() {
		console.info("layout game1");
		// this.content.style.width = "" + this.area.getWidth() + "px";
		HTMLElement html = document.documentElement;

		double pageHeight = html.offsetHeight;
		this.scale = 1;
		console.info("height: " + html.offsetHeight);
		this.scale = (pageHeight / (this.area.getHeight() + this.gameRow.offsetTop));
		String scaledWidth = "" + Math.floor(this.area.getWidth() * this.scale) + "px";
		this.textRow.style.width = scaledWidth;
		this.gameRow.style.width = scaledWidth;
		this.gameRow.style.height = "" + Math.floor(this.area.getHeight() * this.scale) + "px";
		this.gameContent.style.width = "" + this.area.getWidth() + "px";
		this.gameContent.style.height = "" + this.area.getHeight() + "px";

		NodeList l = document.querySelectorAll("body *");
		for (int i = 0; i < l.length; i++) {
			HTMLElement e = (HTMLElement) l.item(i);
			e.style.fontSize = Math.floor(FONT_SIZE * this.scale) + "px";
		}

		this.areaLayer.width = this.area.getWidth();
		this.areaLayer.height = this.area.getHeight();
		this.topLayer.width = this.area.getWidth();
		this.topLayer.height = this.area.getHeight();
		this.backgroundLayer.width = this.area.getWidth();
		this.backgroundLayer.height = this.area.getHeight();
		this.ballsLayer.width = this.area.getWidth();
		this.ballsLayer.height = this.area.getHeight();

		String transform = "";
		String transformOrigin = "";
		if (this.scale != 1) {
			transform += "scale(" + this.scale + "," + this.scale + ")";
			transformOrigin = "0px 0px";
		}

		this.applyTransform(this.gameContent, transform, transformOrigin);

		Point p = GameManager.findPos(this.gameContent);
		this.area.positionInPage = p;
		// this.area.positionInPage = new Point(p.x + translationX, p.y +
		// translationY);
		this.area.clearAll = true;
		this.area.render();
	}

	public void applyTransform(HTMLElement element, String transform, String transformOrigin) {
		element.style.setProperty("-moz-transform-origin", transformOrigin);
		element.style.setProperty("-webkit-transform-origin", transformOrigin);
		element.style.setProperty("-ms-transform-origin", transformOrigin);
		element.style.setProperty("-o-transform-origin", transformOrigin);
		element.style.transformOrigin = transformOrigin;
		element.style.setProperty("-moz-transform", transform);
		element.style.setProperty("-webkit-transform", transform);
		element.style.setProperty("-ms-transform", transform);
		element.style.setProperty("-o-transform", transform);
		element.style.transform = transform;
	}

	public void startGame() {
		if (this.area != null) {
			console.info("starting game...");
			this.paused = false;
			animate();
		}
	}

	public boolean isPaused() {
		return this.paused;
	}

	public void pauseGame() {
		this.paused = true;
	}

	public Point deviceToWorld(double x, double y) {
		return new Point(Math.floor((x - this.area.positionInPage.x) / this.scale), Math.floor((y - this.area.positionInPage.y) / this.scale));
	}

	public void onMouseDown(MouseEvent event) {
		event.preventDefault();
		this.area.onInputDeviceDown(event, false);
	}

	public void onMouseUp(MouseEvent event) {
		event.preventDefault();
		if (this.area.finished) {
			this.initGame();
			this.startGame();
		}
		this.area.onInputDeviceUp(event, false);
	}

	public void onMouseMove(MouseEvent event) {
		event.preventDefault();
		this.area.onInputDeviceMove(event, false);
	}

	private boolean pressed = false;

	public void onTouchStart(TouchEvent event) {
		event.preventDefault();
		if (this.area.finished) {
			this.pressed = true;
		}
		console.info(event);
		this.area.onInputDeviceDown(event, true);
	}

	public void onTouchEnd(TouchEvent event) {
		event.preventDefault();
		if (this.area.finished && this.pressed) {
			this.pressed = false;
			this.initGame();
			this.startGame();
		}
		this.area.onInputDeviceUp(event, true);
	}

	public void onTouchMove(TouchEvent event) {
		event.preventDefault();
		this.area.onInputDeviceMove(event, true);
	}

	public void onMouseClick(Event event) {
		event.preventDefault();
		if (this.area.finished) {
			this.initGame();
			this.startGame();
		}
	}

	public Factory getFactory() {
		return factory;
	}

	public void installListeners() {
		this.topLayer.addEventListener(mousedown, event -> {
			this.onMouseDown(event);
			return null;
		}, true);
		this.topLayer.addEventListener(mousemove, event -> {
			this.onMouseMove(event);
			return null;
		}, true);
		this.topLayer.addEventListener(mouseup, event -> {
			this.onMouseUp(event);
			return null;
		}, true);
		this.topLayer.addEventListener(touchstart, event -> {
			this.onTouchStart(event);
			return null;
		}, true);
		this.topLayer.addEventListener(touchmove, event -> {
			this.onTouchMove(event);
			return null;
		}, true);
		this.topLayer.addEventListener(touchend, event -> {
			this.onTouchEnd(event);
			return null;
		}, true);
	}

}
