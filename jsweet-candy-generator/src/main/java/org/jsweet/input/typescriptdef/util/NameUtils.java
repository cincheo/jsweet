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
