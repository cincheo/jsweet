/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package javaemul.internal;

import static def.js.Globals.Infinity;
import static jsweet.util.Globals.$get;
import static jsweet.util.Globals.$set;
import static jsweet.util.Globals.typeof;

/**
 * Provides an interface for simple JavaScript idioms that can not be expressed in Java.
 */
public class JsUtils {

  public static double getInfinity() {
    return Infinity;
  }

  public static boolean isUndefined(Object value) {
    return value == null;
  };

  // TODO(goktug): replace this with a real cast when the compiler can optimize it.
  public static String unsafeCastToString(Object string) {
   return (String)string;
  };

  public static void setPropertySafe(Object map, String key, Object value) {
    try {
      // This may throw exception in strict mode.
      $set(map, key, value);
    } catch(Throwable e) { }
  };

  public static int getIntProperty(Object map, String key) {
    return (int)$get(map, key);
  };

  public static void setIntProperty(Object map, String key, int value) {
    $set(map, key, value);
  };

  public static String typeOf(Object o) {
    return typeof(o);
  };
}

