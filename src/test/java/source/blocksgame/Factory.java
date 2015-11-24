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

import source.blocksgame.util.Direction;
import source.blocksgame.util.Point;

public class Factory {

	private GameManager gameManager;

	public Factory(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public GameArea createDefaultEmptyLevel(int width, int height) {
		GameArea area;
		Player player;
		Ball ball;
		area = new GameArea(this, width, height);
		player = new Player(area, "blue", Direction.NORTH, new Point(GameManager.SIZE * width / 2, (GameManager.SIZE * height * 5) / 6 + 20),
				GameManager.TOUCH_SIZE/1.5);
		area.setPlayer(player);
		ball = new Ball(area, GameManager.SIZE / 4, new Point(GameManager.SIZE * width / 2, (GameManager.SIZE * height * 3) / 4), Direction.SOUTH.normalized, 0);
		area.setBall(ball);
		return area;
	}

	public GameArea createLevel() {
		GameArea area = this.createDefaultEmptyLevel(12, 16);
		area.createBorders(Direction.EAST);
		area.createBorders(Direction.WEST);
		area.createBorders(Direction.NORTH);
		area.createBorders(Direction.SOUTH);
		area.removeBlock(3, area.rows - 1);
		area.removeBlock(4, area.rows - 1);
		area.removeBlock(5, area.rows - 1);
		area.removeBlock(6, area.rows - 1);
		area.removeBlock(7, area.rows - 1);
		area.removeBlock(8, area.rows - 1);
		for (int i = 1; i < area.cols - 1; i++) {
			for (int j = 1; j < area.rows / 2; j++) {
				area.addBlock(new BlockElement(1), i, j);
			}
		}
		return area;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

}
