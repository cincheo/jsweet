package def.dom;

public class MSGestureEvent extends UIEvent {
    public double clientX;
    public double clientY;
    public double expansion;
    public java.lang.Object gestureObject;
    public double hwTimestamp;
    public double offsetX;
    public double offsetY;
    public double rotation;
    public double scale;
    public double screenX;
    public double screenY;
    public double translationX;
    public double translationY;
    public double velocityAngular;
    public double velocityExpansion;
    public double velocityX;
    public double velocityY;
    native public void initGestureEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, double offsetXArg, double offsetYArg, double translationXArg, double translationYArg, double scaleArg, double expansionArg, double rotationArg, double velocityXArg, double velocityYArg, double velocityExpansionArg, double velocityAngularArg, double hwTimestampArg);
    public double MSGESTURE_FLAG_BEGIN;
    public double MSGESTURE_FLAG_CANCEL;
    public double MSGESTURE_FLAG_END;
    public double MSGESTURE_FLAG_INERTIA;
    public double MSGESTURE_FLAG_NONE;
    public static MSGestureEvent prototype;
    public MSGestureEvent(){}
}

