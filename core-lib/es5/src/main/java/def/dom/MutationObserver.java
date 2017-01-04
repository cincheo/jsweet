package def.dom;
public class MutationObserver extends def.js.Object {
    native public void disconnect();
    native public void observe(Node target, MutationObserverInit options);
    native public MutationRecord[] takeRecords();
    public static MutationObserver prototype;
    public MutationObserver(MutationCallback callback){}
    protected MutationObserver(){}
}

