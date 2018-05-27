package org.jsweet.transpiler.eval;

import java.util.regex.Matcher;

import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.util.EvaluationResult;

class TraceBasedEvaluationResult implements EvaluationResult {
	private String trace;

	public TraceBasedEvaluationResult(String trace) {
		super();
		this.trace = trace;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String variableName) {
		String[] var = null;
		Matcher matcher = JSweetTranspiler.EXPORTED_VAR_REGEXP.matcher(trace);
		int index = 0;
		boolean match = true;
		while (match) {
			match = matcher.find(index);
			if (match) {
				if (variableName.equals(matcher.group(1))) {
					var = new String[] { matcher.group(1), matcher.group(2) };
					match = false;

				}
				index = matcher.end() - 1;
			}
		}
		if (var == null) {
			return null;
		} else {
			String stringValue = var[1];
			try {
				return (T) (Integer) Integer.parseInt(stringValue);
			} catch (Exception e1) {
				try {
					return (T) (Double) Double.parseDouble(stringValue);
				} catch (Exception e2) {
					if ("true".equals(stringValue)) {
						return (T) Boolean.TRUE;
					}
					if ("false".equals(stringValue)) {
						return (T) Boolean.FALSE;
					}
					if ("undefined".equals(stringValue)) {
						return null;
					}
				}
			}
			return (T) stringValue;
		}
	}

	@Override
	public String getExecutionTrace() {
		return trace;
	}
}