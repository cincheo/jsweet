package def.dom;

public class AnimationEvent extends Event {
    public java.lang.String animationName;
    public double elapsedTime;
    native public void initAnimationEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, java.lang.String animationNameArg, double elapsedTimeArg);
    public static AnimationEvent prototype;
    public AnimationEvent(){}
}

