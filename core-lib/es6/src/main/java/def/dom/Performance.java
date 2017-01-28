package def.dom;

import def.js.Object;

public class Performance extends def.js.Object {
    public PerformanceNavigation navigation;
    public PerformanceTiming timing;
    native public void clearMarks(java.lang.String markName);
    native public void clearMeasures(java.lang.String measureName);
    native public void clearResourceTimings();
    native public java.lang.Object getEntries();
    native public java.lang.Object getEntriesByName(java.lang.String name, java.lang.String entryType);
    native public java.lang.Object getEntriesByType(java.lang.String entryType);
    native public java.lang.Object getMarks(java.lang.String markName);
    native public java.lang.Object getMeasures(java.lang.String measureName);
    native public void mark(java.lang.String markName);
    native public void measure(java.lang.String measureName, java.lang.String startMarkName, java.lang.String endMarkName);
    native public double now();
    native public void setResourceTimingBufferSize(double maxSize);
    native public java.lang.Object toJSON();
    public static Performance prototype;
    public Performance(){}
    native public void clearMarks();
    native public void clearMeasures();
    native public java.lang.Object getEntriesByName(java.lang.String name);
    native public java.lang.Object getMarks();
    native public java.lang.Object getMeasures();
    native public void measure(java.lang.String measureName, java.lang.String startMarkName);
    native public void measure(java.lang.String measureName);
}

