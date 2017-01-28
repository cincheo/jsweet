package def.dom;

public class CommandEvent extends Event {
    public java.lang.String commandName;
    public java.lang.String detail;
    public static CommandEvent prototype;
    public CommandEvent(java.lang.String type, CommandEventInit eventInitDict){}
    public CommandEvent(java.lang.String type){}
    protected CommandEvent(){}
}

