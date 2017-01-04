package def.dom;
public class AnimationEvent extends Event {
    public String animationName;
    public double elapsedTime;
    native public void initAnimationEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, String animationNameArg, double elapsedTimeArg);
    public static AnimationEvent prototype;
    public AnimationEvent(){}
}

