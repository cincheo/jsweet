package def.dom;

public class TransitionEvent extends Event {
    public double elapsedTime;
    public java.lang.String propertyName;
    native public void initTransitionEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, java.lang.String propertyNameArg, double elapsedTimeArg);
    public static TransitionEvent prototype;
    public TransitionEvent(){}
}

