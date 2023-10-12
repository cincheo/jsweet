package ts.utils;

import java.util.List;

import ts.client.completions.SymbolDisplayPart;

public class TypeScriptHelper {

	/**
	 * Returns the TypeScript prefix completion for the given position of the
	 * given content.
	 * 
	 * @param contents
	 * @param position
	 * @return the TypeScript prefix completion for the given position of the
	 *         given content.
	 */
	public static String getPrefix(String contents, int position) {
		StringBuilder prefix = null;
		int i = position - 1;
		while (i >= 0) {
			char c = contents.charAt(i);
			if (!Character.isJavaIdentifierPart(c)) {
				break;
			} else {
				if (prefix == null) {
					prefix = new StringBuilder();
				}
				prefix.insert(0, c);
			}
			i--;
		}
		return prefix != null ? prefix.toString() : null;
	}

	public static String text(List<SymbolDisplayPart> parts, boolean withPre) {
		if (parts == null || parts.size() < 1) {
			return null;
		}
		StringBuilder html = new StringBuilder(withPre ? "<pre>" : "");
		for (SymbolDisplayPart part : parts) {
			html.append(part.getText());
		}
		if (withPre) {
			html.append("</pre>");
		}
		return html.toString();
	}

	public static String extractFunctionParameters(List<SymbolDisplayPart> parts) {
		if (parts == null || parts.size() < 1) {
			return null;
		}
		StringBuilder information = new StringBuilder("");
		boolean hasParam = false;
		for (SymbolDisplayPart part : parts) {
			if (part.isParameterName()) {
				information.append(part.getText());
				hasParam = true;
			} else if (hasParam) {
				if (")".equals(part.getText())) {
					// end of parameters declaration
					break;
				} else {
					information.append(part.getText());
				}
			}
		}
		information.append("");
		return information.toString();
	}
}
