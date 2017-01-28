package def.dom;

import def.js.Function;
import def.js.Object;

public class PerfWidgetExternal extends def.js.Object {
    public double activeNetworkRequestCount;
    public double averageFrameTime;
    public double averagePaintTime;
    public java.lang.Boolean extraInformationEnabled;
    public java.lang.Boolean independentRenderingEnabled;
    public java.lang.String irDisablingContentString;
    public java.lang.Boolean irStatusAvailable;
    public double maxCpuSpeed;
    public double paintRequestsPerSecond;
    public double performanceCounter;
    public double performanceCounterFrequency;
    native public void addEventListener(java.lang.String eventType, Function callback);
    native public double getMemoryUsage();
    native public double getProcessCpuUsage();
    native public java.lang.Object getRecentCpuUsage(double last);
    native public java.lang.Object getRecentFrames(double last);
    native public java.lang.Object getRecentMemoryUsage(double last);
    native public java.lang.Object getRecentPaintRequests(double last);
    native public void removeEventListener(java.lang.String eventType, Function callback);
    native public void repositionWindow(double x, double y);
    native public void resizeWindow(double width, double height);
    public static PerfWidgetExternal prototype;
    public PerfWidgetExternal(){}
}

