package def.dom;

public class IDBVersionChangeEvent extends Event {
    public double newVersion;
    public double oldVersion;
    public static IDBVersionChangeEvent prototype;
    public IDBVersionChangeEvent(){}
}

