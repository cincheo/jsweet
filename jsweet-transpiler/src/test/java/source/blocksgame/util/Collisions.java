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

public class Collisions {

	// see http://williamecraver.wix.com/elastic-equations
	public static void sphericCollision(MobileElement refMobile, MobileElement targetMobile) {
		double th1 = refMobile.speedVector.angle();
		double th2 = targetMobile.speedVector.angle();
		double v1 = refMobile.speedVector.length();
		double v2 = targetMobile.speedVector.length();
		double m1 = refMobile.weight;
		double m2 = targetMobile.weight;

		Vector r_per = targetMobile.getPosition().to(refMobile.getPosition()); 
		double phi = r_per.angle();

		double a2 = (v2 * Math.cos(th2 - phi) * (m2 - m1) + 2 * m1 * v1 * Math.cos(th1 - phi)) / (m1 + m2);
		double b2 = v2 * Math.sin(th2 - phi);

		targetMobile.speedVector.x = a2 * Math.cos(phi) + b2 * Math.cos(phi + Math.PI / 2);
		targetMobile.speedVector.y = a2 * Math.sin(phi) + b2 * Math.sin(phi + Math.PI / 2);
	}

}
