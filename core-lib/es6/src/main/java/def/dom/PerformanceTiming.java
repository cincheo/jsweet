package def.dom;

import def.js.Object;

public class PerformanceTiming extends def.js.Object {
    public double connectEnd;
    public double connectStart;
    public double domComplete;
    public double domContentLoadedEventEnd;
    public double domContentLoadedEventStart;
    public double domInteractive;
    public double domLoading;
    public double domainLookupEnd;
    public double domainLookupStart;
    public double fetchStart;
    public double loadEventEnd;
    public double loadEventStart;
    public double msFirstPaint;
    public double navigationStart;
    public double redirectEnd;
    public double redirectStart;
    public double requestStart;
    public double responseEnd;
    public double responseStart;
    public double unloadEventEnd;
    public double unloadEventStart;
    native public java.lang.Object toJSON();
    public static PerformanceTiming prototype;
    public PerformanceTiming(){}
}

