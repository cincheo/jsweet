package def.dom;
public class LongRunningScriptDetectedEvent extends Event {
    public double executionTime;
    public Boolean stopPageScriptExecution;
    public static LongRunningScriptDetectedEvent prototype;
    public LongRunningScriptDetectedEvent(){}
}

