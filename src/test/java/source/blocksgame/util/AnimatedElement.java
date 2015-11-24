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

import jsweet.dom.CanvasRenderingContext2D;

public abstract class AnimatedElement {

	public boolean invalidated;
	private int animationStepCount = 0;
	private int remainingAnimationSteps = -1;

	public boolean isVisible() {
		return true;
	};

	public void setVisible(boolean visible) {
	};

	public int getAnimationStep() {
		return this.animationStepCount - this.remainingAnimationSteps;
	}

	public void nextAnimationStep() {
		this.remainingAnimationSteps--;
		this.invalidated = this.remainingAnimationSteps >= 0;
	}

	public double getRemainingAnimationSteps() {
		return this.remainingAnimationSteps;
	}

	public double getAnimationStepCount() {
		return this.animationStepCount;
	}

	public void initAnimation(int stepCount) {
		this.animationStepCount = stepCount;
		this.remainingAnimationSteps = stepCount - 1;
		this.invalidated = true;
	}

	public void stopAnimation() {
		this.remainingAnimationSteps = -1;
		this.invalidated = true;
	}

	public void renderAnimation(CanvasRenderingContext2D animationCxt, CanvasRenderingContext2D areaCxt) {
	}

	public boolean isAnimating() {
		return this.remainingAnimationSteps >= 0;
	}

}
