package def.dom;
public class History extends def.js.Object {
    public double length;
    public Object state;
    native public void back(Object distance);
    native public void forward(Object distance);
    native public void go(Object delta);
    native public void pushState(Object statedata, String title, String url);
    native public void replaceState(Object statedata, String title, String url);
    public static History prototype;
    public History(){}
    native public void back();
    native public void forward();
    native public void go();
    native public void pushState(Object statedata, String title);
    native public void pushState(Object statedata);
    native public void replaceState(Object statedata, String title);
    native public void replaceState(Object statedata);
}

