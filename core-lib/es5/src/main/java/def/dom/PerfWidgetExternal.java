package def.dom;
import def.js.Function;
public class PerfWidgetExternal extends def.js.Object {
    public double activeNetworkRequestCount;
    public double averageFrameTime;
    public double averagePaintTime;
    public Boolean extraInformationEnabled;
    public Boolean independentRenderingEnabled;
    public String irDisablingContentString;
    public Boolean irStatusAvailable;
    public double maxCpuSpeed;
    public double paintRequestsPerSecond;
    public double performanceCounter;
    public double performanceCounterFrequency;
    native public void addEventListener(String eventType, Function callback);
    native public double getMemoryUsage();
    native public double getProcessCpuUsage();
    native public Object getRecentCpuUsage(double last);
    native public Object getRecentFrames(double last);
    native public Object getRecentMemoryUsage(double last);
    native public Object getRecentPaintRequests(double last);
    native public void removeEventListener(String eventType, Function callback);
    native public void repositionWindow(double x, double y);
    native public void resizeWindow(double width, double height);
    public static PerfWidgetExternal prototype;
    public PerfWidgetExternal(){}
}

