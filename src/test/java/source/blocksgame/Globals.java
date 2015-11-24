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
import static jsweet.dom.Globals.window;

import jsweet.dom.Event;
import jsweet.lang.Date;

public class Globals {

	static GameManager gm;

	public static void animate() {
		GameArea area = gm.getCurrentLevel();
		if (!gm.isPaused()) {
			if (!area.finished) {
				area.currentDate = new Date();
				area.render();
				area.calculateNextPositions();
				window.requestAnimationFrame((time) -> {
					animate();
				});
			} else {
				gm.onLevelEnded();
			}
		}
	}

	public static Object start(Event event) {
		gm = new GameManager();
		gm.init();
		gm.startGame();
		return event;
	}

	public static void main(String[] args) {
		window.onload = Globals::start;
	}
	
}
