package source.api;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.number;
import static jsweet.util.Lang.string;

import def.js.Array;

public class Numbers {

    static Array<String> trace = new Array<String>();

    public static void main(String[] args) {

        Integer i = parseDuration("1:20");
        assert i == 80;

        float f = Float.intBitsToFloat(Float.floatToIntBits(3.14f));
        assert f != 3.14f;
        assert number(f).toFixed(2) == "3.14";

        double d = Double.longBitsToDouble(Double.doubleToLongBits(3.14));
        assert d != 3.14;
        assert ((double) Math.round(d * 100)) / 100 == 3.14;

        $export("trace", trace.join(","));

        boolean isFinite_i1 = Double.isFinite(Double.NEGATIVE_INFINITY);
        boolean isFinite_i2 = Double.isFinite(Double.POSITIVE_INFINITY);
        boolean isFinite_i3 = Double.isFinite(Double.NaN);
        boolean isFinite_f1 = Double.isFinite(0.1);
        boolean isFinite_f2 = Double.isFinite(10);
        boolean isFinite_f3 = Double.isFinite(10e34);
        boolean isFinite_f4 = Double.isFinite(-0.000001);
        boolean isFinite_f5 = Double.isFinite(-10e22);
        boolean isFinite_f6 = Double.isFinite(Double.MAX_VALUE);
        boolean isFinite_f7 = Double.isFinite(Double.MIN_VALUE);
        assert isFinite_f1;
        assert isFinite_f2;
        assert isFinite_f3;
        assert isFinite_f4;
        assert isFinite_f5;
        assert isFinite_f6;
        assert isFinite_f7;
        assert !isFinite_i1;
        assert !isFinite_i2;
        assert !isFinite_i3;

        boolean isInfinite_i1 = Double.isInfinite(Double.NEGATIVE_INFINITY);
        boolean isInfinite_i2 = Double.isInfinite(Double.POSITIVE_INFINITY);
        boolean isInfinite_f1 = Double.isInfinite(0.1);
        boolean isInfinite_f2 = Double.isInfinite(10);
        boolean isInfinite_f3 = Double.isInfinite(10e34);
        boolean isInfinite_f4 = Double.isInfinite(-0.000001);
        boolean isInfinite_f5 = Double.isInfinite(-10e22);
        boolean isInfinite_f6 = Double.isInfinite(Double.MAX_VALUE);
        boolean isInfinite_f7 = Double.isInfinite(Double.MIN_VALUE);
        boolean isInfinite_f8 = Double.isInfinite(Double.NaN);
        assert !isInfinite_f1;
        assert !isInfinite_f2;
        assert !isInfinite_f3;
        assert !isInfinite_f4;
        assert !isInfinite_f5;
        assert !isInfinite_f6;
        assert !isInfinite_f7;
        assert !isInfinite_f8;
        assert isInfinite_i1;
        assert isInfinite_i2;
    }

    public static Integer parseDuration(String duration) {
        if (duration == null) {
            return null;
        }
        String[] parts = duration.split(":");
        switch (parts.length) {
        case 1:
            return new Integer(parts[0]) * 60;
        case 2:
            return new Integer(parts[0]) * 60 + new Integer(parts[1]);
        }
        return null;
    }

}
