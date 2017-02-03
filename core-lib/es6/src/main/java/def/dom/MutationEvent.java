package def.dom;

public class MutationEvent extends Event {
    public double attrChange;
    public java.lang.String attrName;
    public java.lang.String newValue;
    public java.lang.String prevValue;
    public Node relatedNode;
    native public void initMutationEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Node relatedNodeArg, java.lang.String prevValueArg, java.lang.String newValueArg, java.lang.String attrNameArg, double attrChangeArg);
    public double ADDITION;
    public double MODIFICATION;
    public double REMOVAL;
    public static MutationEvent prototype;
    public MutationEvent(){}
}

