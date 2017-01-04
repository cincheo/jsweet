package def.dom;
public class TimeRanges extends def.js.Object {
    public double length;
    native public double end(double index);
    native public double start(double index);
    public static TimeRanges prototype;
    public TimeRanges(){}
}

