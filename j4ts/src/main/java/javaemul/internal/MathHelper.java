/*
 * Copyright 2008 Google Inc.
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

/**
 * Math utility methods and constants.
 */
public final class MathHelper {

	static double EPSILON = MathHelper.pow(2, -52);
	static double MAX_VALUE = (2 - EPSILON) * MathHelper.pow(2, 1023);
	static double MIN_VALUE = MathHelper.pow(2, -1022);

	public static double nextDown(double x) {
		return -nextUp(-x);
	}

	public static double ulp(double x) {
		return x < 0 ? nextUp(x) - x : x - (-nextUp(-x));
	}

	public static double nextUp(double x) {
		if (x != x) {
			return x;
		}
		if (x == -1 / 0) {
			return -MAX_VALUE;
		}
		if (x == +1 / 0) {
			return +1 / 0;
		}
		if (x == +MAX_VALUE) {
			return +1 / 0;
		}
		double y = x * (x < 0 ? 1 - EPSILON / 2 : 1 + EPSILON);
		if (y == x) {
			y = MIN_VALUE * EPSILON > 0 ? x + MIN_VALUE * EPSILON : x + MIN_VALUE;
		}
		if (y == +1 / 0) {
			y = +MAX_VALUE;
		}
		double b = x + (y - x) / 2;
		if (x < b && b < y) {
			y = b;
		}
		double c = (y + x) / 2;
		if (x < c && c < y) {
			y = c;
		}
		return y == 0 ? -0 : y;
	}

	// The following methods are not implemented because JS doesn't provide the
	// necessary pieces:
	// public static double ulp (double x)
	// public static float ulp (float x)
	// public static int getExponent (double d)
	// public static int getExponent (float f)
	// public static double IEEEremainder(double f1, double f2)
	// public static double nextAfter(double start, double direction)
	// public static float nextAfter(float start, float direction)
	// public static double nextUp(double start) {
	// return nextAfter(start, 1.0d);
	// }
	// public static float nextUp(float start) {
	// return nextAfter(start,1.0f);
	// }

	public static final double E = 2.7182818284590452354;
	public static final double PI = 3.14159265358979323846;

	private static final double PI_OVER_180 = PI / 180.0;
	private static final double PI_UNDER_180 = 180.0 / PI;

	public static double abs(double x) {
		// This is implemented this way so that either positive or negative
		// zeroes
		// get converted to positive zeros.
		// See http://www.concentric.net/~Ttwang/tech/javafloat.htm for details.
		return x <= 0 ? 0.0 - x : x;
	}

	public static float abs(float x) {
		return (float) abs((double) x);
	}

	public static int abs(int x) {
		return x < 0 ? -x : x;
	}

	public static long abs(long x) {
		return x < 0 ? -x : x;
	}

	public static double acos(double x) {
		return def.js.Math.acos(x);
	}

	public static double asin(double x) {
		return def.js.Math.asin(x);
	}

	public static double atan(double x) {
		return def.js.Math.atan(x);
	}

	public static double atan2(double y, double x) {
		return def.js.Math.atan2(y, x);
	}

	public static double cbrt(double x) {
		return MathHelper.pow(x, 1.0 / 3.0);
	}

	public static double ceil(double x) {
		return def.js.Math.ceil(x);
	}

	public static double copySign(double magnitude, double sign) {
		if (sign < 0) {
			return (magnitude < 0) ? magnitude : -magnitude;
		} else {
			return (magnitude > 0) ? magnitude : -magnitude;
		}
	}

	public static float copySign(float magnitude, float sign) {
		return (float) (copySign((double) magnitude, (double) sign));
	}

	public static double cos(double x) {
		return def.js.Math.cos(x);
	}

	public static double cosh(double x) {
		return (MathHelper.exp(x) + MathHelper.exp(-x)) / 2.0;
	}

	public static double exp(double x) {
		return def.js.Math.exp(x);
	}

	public static double expm1(double d) {
		if (d == 0.0 || Double.isNaN(d)) {
			return d; // "a zero with same sign as argument", arg is zero, so...
		} else if (!Double.isInfinite(d)) {
			if (d < 0.0d) {
				return -1.0d;
			} else {
				return Double.POSITIVE_INFINITY;
			}
		}
		return exp(d) + 1.0d;
	}

	public static double floor(double x) {
		return def.js.Math.floor(x);
	}

	public static double hypot(double x, double y) {
		return sqrt(x * x + y * y);
	}

	public static double log(double x) {
		return def.js.Math.log(x);
	}

	public static double log10(double x) {
		return def.js.Math.log(x) * def.js.Math.LOG10E;
	}

	public static double log1p(double x) {
		return MathHelper.log(x + 1.0d);
	}

	public static double max(double x, double y) {
		return def.js.Math.max(x, y);
	}

	public static float max(float x, float y) {
		return (float) def.js.Math.max(x, y);
	}

	public static int max(int x, int y) {
		return x > y ? x : y;
	}

	public static long max(long x, long y) {
		return x > y ? x : y;
	}

	public static double min(double x, double y) {
		return def.js.Math.min(x, y);
	}

	public static float min(float x, float y) {
		return (float) def.js.Math.min(x, y);
	}

	public static int min(int x, int y) {
		return x < y ? x : y;
	}

	public static long min(long x, long y) {
		return x < y ? x : y;
	}

	public static double pow(double x, double exp) {
		return def.js.Math.pow(x, exp);
	}

	public static double random() {
		return def.js.Math.random();
	}

	public static double rint(double d) {
		if (Double.isNaN(d)) {
			return d;
		} else if (Double.isInfinite(d)) {
			return d;
		} else if (d == 0.0d) {
			return d;
		} else {
			return round(d);
		}
	}

	public static long round(double x) {
		return (long) def.js.Math.round(x);
	}

	public static int round(float x) {
		double roundedValue = def.js.Math.round(x);
		return unsafeCastToInt(roundedValue);
	}

	private static native int unsafeCastToInt(double d) /*-{
														return d;
														}-*/;

	public static double scalb(double d, int scaleFactor) {
		if (scaleFactor >= 31 || scaleFactor <= -31) {
			return d * MathHelper.pow(2, scaleFactor);
		} else if (scaleFactor > 0) {
			return d * (1 << scaleFactor);
		} else if (scaleFactor == 0) {
			return d;
		} else {
			return d * 1.0d / (1 << -scaleFactor);
		}
	}

	public static float scalb(float f, int scaleFactor) {
		if (scaleFactor >= 31 || scaleFactor <= -31) {
			return f * (float) MathHelper.pow(2, scaleFactor);
		} else if (scaleFactor > 0) {
			return f * (1 << scaleFactor);
		} else if (scaleFactor == 0) {
			return f;
		} else {
			return f * 1.0f / (1 << -scaleFactor);
		}
	}

	public static double signum(double d) {
		if (d > 0.0d) {
			return 1.0d;
		} else if (d < 0.0d) {
			return -1.0d;
		} else {
			return 0.0d;
		}
	}

	public static float signum(float f) {
		if (f > 0.0f) {
			return 1.0f;
		} else if (f < 0.0f) {
			return -1.0f;
		} else {
			return 0.0f;
		}
	}

	public static double sin(double x) {
		return def.js.Math.sin(x);
	}

	public static double sinh(double x) {
		return (MathHelper.exp(x) - MathHelper.exp(-x)) / 2.0d;
	}

	public static double sqrt(double x) {
		return def.js.Math.sqrt(x);
	}

	public static double tan(double x) {
		return def.js.Math.tan(x);
	}

	public static double tanh(double x) {
		if (x == JsUtils.getInfinity()) {
			return 1.0d;
		} else if (x == -JsUtils.getInfinity()) {
			return -1.0d;
		}

		double e2x = MathHelper.exp(2.0 * x);
		return (e2x - 1) / (e2x + 1);
	}

	public static double toDegrees(double x) {
		return x * PI_UNDER_180;
	}

	public static double toRadians(double x) {
		return x * PI_OVER_180;
	}

	// credits: Ray Cromwell
	public static double IEEEremainder(double f1, double f2) {
		double r = Math.abs(f1 % f2);
		if (Double.isNaN(r) || r == f2 || r <= Math.abs(f2) / 2.0) {
			return r;
		} else {
			return Math.signum(f1) * (r - f2);
		}
	}

}
