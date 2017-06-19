package source.enums;

import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EnumWithStatics {
	ANSI("ANSI", "Cp1252"), MAC("MAC", "MacRoman"), LATIN("850", "Cp850"), US("437", "Cp437"), ZH("ZH",
			"GB2312"), RU("RU", "Cp1251");

	private EnumWithStatics(String value, String charset) {
		m_value = value;
		m_charset = charset;
	}

	public static EnumWithStatics getInstance(String value) {
		EnumWithStatics result = NAME_MAP.get(value);
		if (result == null) {
			result = ANSI;
		}
		return (result);
	}

	public static void main(String[] args) {
		System.out.println(getInstance("XX").getCharset());
		assert "Cp1252".equals(getInstance("XX").getCharset());
		System.out.println(getInstance("MAC").getCharset());
		assert "MacRoman".equals(getInstance("MAC").getCharset());
		System.out.println(getInstance("ANSI").getCharset());
		assert "Cp1252".equals(getInstance("ANSI").getCharset());
	}

	public Charset getCharset() {
		return (Charset.forName(m_charset));
	}

	@Override
	public String toString() {
		return (m_value);
	}

	private String m_value;
	private String m_charset;

	private static final Map<String, EnumWithStatics> NAME_MAP = new HashMap<String, EnumWithStatics>();
	static {
		for (EnumWithStatics e : EnumSet.allOf(EnumWithStatics.class)) {
			NAME_MAP.put(e.m_value, e);
		}
	}
}