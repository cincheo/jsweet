package def.dom;
public class MutationEvent extends Event {
    public double attrChange;
    public String attrName;
    public String newValue;
    public String prevValue;
    public Node relatedNode;
    native public void initMutationEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Node relatedNodeArg, String prevValueArg, String newValueArg, String attrNameArg, double attrChangeArg);
    public double ADDITION;
    public double MODIFICATION;
    public double REMOVAL;
    public static MutationEvent prototype;
    public MutationEvent(){}
}

