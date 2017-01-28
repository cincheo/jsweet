package def.dom;

import def.js.Object;

public class History extends def.js.Object {
    public double length;
    public java.lang.Object state;
    native public void back(java.lang.Object distance);
    native public void forward(java.lang.Object distance);
    native public void go(java.lang.Object delta);
    native public void pushState(java.lang.Object statedata, java.lang.String title, java.lang.String url);
    native public void replaceState(java.lang.Object statedata, java.lang.String title, java.lang.String url);
    public static History prototype;
    public History(){}
    native public void back();
    native public void forward();
    native public void go();
    native public void pushState(java.lang.Object statedata, java.lang.String title);
    native public void pushState(java.lang.Object statedata);
    native public void replaceState(java.lang.Object statedata, java.lang.String title);
    native public void replaceState(java.lang.Object statedata);
}

