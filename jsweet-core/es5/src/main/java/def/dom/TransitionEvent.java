package def.dom;
public class TransitionEvent extends Event {
    public double elapsedTime;
    public String propertyName;
    native public void initTransitionEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, String propertyNameArg, double elapsedTimeArg);
    public static TransitionEvent prototype;
    public TransitionEvent(){}
}

