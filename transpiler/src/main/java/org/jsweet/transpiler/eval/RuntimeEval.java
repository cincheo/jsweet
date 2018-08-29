package org.jsweet.transpiler.eval;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;

public abstract class RuntimeEval {

	protected final Logger logger = Logger.getLogger(getClass());

	protected void initExportedVarMap() throws Exception {
		Field f = null;
		try {
			f = Thread.currentThread().getContextClassLoader().loadClass(JSweetConfig.UTIL_CLASSNAME)
					.getDeclaredField("EXPORTED_VARS");
		} catch (ClassNotFoundException ex) {
			f = Thread.currentThread().getContextClassLoader().loadClass(JSweetConfig.DEPRECATED_UTIL_CLASSNAME)
					.getDeclaredField("EXPORTED_VARS");
		}
		f.setAccessible(true);
		@SuppressWarnings("unchecked")
		ThreadLocal<Map<String, Object>> exportedVars = (ThreadLocal<Map<String, Object>>) f.get(null);
		exportedVars.set(new HashMap<>());
	}

	protected Map<String, Object> getExportedVarMap() throws Exception {
		Field f = null;
		try {
			f = Thread.currentThread().getContextClassLoader().loadClass(JSweetConfig.UTIL_CLASSNAME)
					.getDeclaredField("EXPORTED_VARS");
		} catch (ClassNotFoundException ex) {
			f = Thread.currentThread().getContextClassLoader().loadClass(JSweetConfig.DEPRECATED_UTIL_CLASSNAME)
					.getDeclaredField("EXPORTED_VARS");
		}
		f.setAccessible(true);
		@SuppressWarnings("unchecked")
		ThreadLocal<Map<String, Object>> exportedVars = (ThreadLocal<Map<String, Object>>) f.get(null);
		return new HashMap<>(exportedVars.get());
	}
}
