package def.dom;

public class MSManipulationEvent extends UIEvent {
    public double currentState;
    public double inertiaDestinationX;
    public double inertiaDestinationY;
    public double lastState;
    native public void initMSManipulationEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg, double lastState, double currentState);
    public double MS_MANIPULATION_STATE_ACTIVE;
    public double MS_MANIPULATION_STATE_CANCELLED;
    public double MS_MANIPULATION_STATE_COMMITTED;
    public double MS_MANIPULATION_STATE_DRAGGING;
    public double MS_MANIPULATION_STATE_INERTIA;
    public double MS_MANIPULATION_STATE_PRESELECT;
    public double MS_MANIPULATION_STATE_SELECTING;
    public double MS_MANIPULATION_STATE_STOPPED;
    public static MSManipulationEvent prototype;
    public MSManipulationEvent(){}
}

