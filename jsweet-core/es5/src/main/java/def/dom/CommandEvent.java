package def.dom;
public class CommandEvent extends Event {
    public String commandName;
    public String detail;
    public static CommandEvent prototype;
    public CommandEvent(String type, CommandEventInit eventInitDict){}
    public CommandEvent(String type){}
    protected CommandEvent(){}
}

