/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.input.typescriptdef.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class NameUtils {

	public static String getDiff(String referenceFullSignature, String targetFullSignature) {
		List<String> referenceSegments = new ArrayList<String>(
				Arrays.asList(referenceFullSignature.replace("[]", "s").split("[<>,\\(\\)]")));
		String[] targetSegments = targetFullSignature.replace("[]", "s").split("[<>,\\(\\)]");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < targetSegments.length; i++) {
			if (!referenceSegments.contains(targetSegments[i])) {
				sb.append(StringUtils.capitalize(targetSegments[i]));
			} else {
				referenceSegments.remove(targetSegments[i]);
			}
		}
		return sb.toString();
	}

}
