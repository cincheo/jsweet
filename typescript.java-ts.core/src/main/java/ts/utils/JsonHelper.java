package ts.utils;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class JsonHelper {

	public static JsonArray toJson(String[] arr) {
		JsonArray json = new JsonArray();
		for (int i = 0; i < arr.length; i++) {
			json.add(arr[i]);
		}
		return json;
	}

	public static JsonArray getArray(JsonObject obj, String name) {
		JsonValue value = obj.get(name);
		return value != null ? value.asArray() : null;
	}

	public static Integer getInteger(JsonObject obj, String name) {
		JsonValue value = obj.get(name);
		return value != null ? value.asInt() : null;
	}

	public static Boolean getBoolean(JsonObject obj, String name) {
		JsonValue value = obj.get(name);
		return value != null ? value.asBoolean() : null;
	}
}
