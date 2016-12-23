package def.dom;
public class Performance extends def.js.Object {
    public PerformanceNavigation navigation;
    public PerformanceTiming timing;
    native public void clearMarks(String markName);
    native public void clearMeasures(String measureName);
    native public void clearResourceTimings();
    native public Object getEntries();
    native public Object getEntriesByName(String name, String entryType);
    native public Object getEntriesByType(String entryType);
    native public Object getMarks(String markName);
    native public Object getMeasures(String measureName);
    native public void mark(String markName);
    native public void measure(String measureName, String startMarkName, String endMarkName);
    native public double now();
    native public void setResourceTimingBufferSize(double maxSize);
    native public Object toJSON();
    public static Performance prototype;
    public Performance(){}
    native public void clearMarks();
    native public void clearMeasures();
    native public Object getEntriesByName(String name);
    native public Object getMarks();
    native public Object getMeasures();
    native public void measure(String measureName, String startMarkName);
    native public void measure(String measureName);
}

