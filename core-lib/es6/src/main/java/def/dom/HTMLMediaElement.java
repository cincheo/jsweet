package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.*;

public class HTMLMediaElement extends HTMLElement {
    /**
      * Returns an AudioTrackList object with the audio tracks for a given video element.
      */
    public AudioTrackList audioTracks;
    /**
      * Gets or sets a value that indicates whether to start playing the media automatically.
      */
    public java.lang.Boolean autoplay;
    /**
      * Gets a collection of buffered time ranges.
      */
    public TimeRanges buffered;
    /**
      * Gets or sets a flag that indicates whether the client provides a set of controls for the media (in case the developer does not include controls for the player).
      */
    public java.lang.Boolean controls;
    /**
      * Gets the address or URL of the current media resource that is selected by IHTMLMediaElement.
      */
    public java.lang.String currentSrc;
    /**
      * Gets or sets the current playback position, in seconds.
      */
    public double currentTime;
    public java.lang.Boolean defaultMuted;
    /**
      * Gets or sets the default playback rate when the user is not using fast forward or reverse for a video or audio resource.
      */
    public double defaultPlaybackRate;
    /**
      * Returns the duration in seconds of the current media resource. A NaN value is returned if duration is not available, or Infinity if the media resource is streaming.
      */
    public double duration;
    /**
      * Gets information about whether the playback has ended or not.
      */
    public java.lang.Boolean ended;
    /**
      * Returns an object representing the current error state of the audio or video element.
      */
    public MediaError error;
    /**
      * Gets or sets a flag to specify whether playback should restart after it completes.
      */
    public java.lang.Boolean loop;
    /**
      * Specifies the purpose of the audio or video media, such as background audio or alerts.
      */
    public java.lang.String msAudioCategory;
    /**
      * Specifies the output device id that the audio will be sent to.
      */
    public java.lang.String msAudioDeviceType;
    public MSGraphicsTrust msGraphicsTrustStatus;
    /**
      * Gets the MSMediaKeys object, which is used for decrypting media data, that is associated with this media element.
      */
    public MSMediaKeys msKeys;
    /**
      * Gets or sets whether the DLNA PlayTo device is available.
      */
    public java.lang.Boolean msPlayToDisabled;
    /**
      * Gets or sets the path to the preferred media source. This enables the Play To target device to stream the media content, which can be DRM protected, from a different location, such as a cloud media server.
      */
    public java.lang.String msPlayToPreferredSourceUri;
    /**
      * Gets or sets the primary DLNA PlayTo device.
      */
    public java.lang.Boolean msPlayToPrimary;
    /**
      * Gets the source associated with the media element for use by the PlayToManager.
      */
    public java.lang.Object msPlayToSource;
    /**
      * Specifies whether or not to enable low-latency playback on the media element.
      */
    public java.lang.Boolean msRealTime;
    /**
      * Gets or sets a flag that indicates whether the audio (either audio or the audio track on video media) is muted.
      */
    public java.lang.Boolean muted;
    /**
      * Gets the current network activity for the element.
      */
    public double networkState;
    public java.util.function.Function<MSMediaKeyNeededEvent,java.lang.Object> onmsneedkey;
    /**
      * Gets a flag that specifies whether playback is paused.
      */
    public java.lang.Boolean paused;
    /**
      * Gets or sets the current rate of speed for the media resource to play. This speed is expressed as a multiple of the normal speed of the media resource.
      */
    public double playbackRate;
    /**
      * Gets TimeRanges for the current media resource that has been played.
      */
    public TimeRanges played;
    /**
      * Gets or sets the current playback position, in seconds.
      */
    public java.lang.String preload;
    public java.lang.Object readyState;
    /**
      * Returns a TimeRanges object that represents the ranges of the current media resource that can be seeked.
      */
    public TimeRanges seekable;
    /**
      * Gets a flag that indicates whether the the client is currently moving to a new playback position in the media resource.
      */
    public java.lang.Boolean seeking;
    /**
      * The address or URL of the a media resource that is to be considered.
      */
    public java.lang.String src;
    public TextTrackList textTracks;
    public VideoTrackList videoTracks;
    /**
      * Gets or sets the volume level for audio portions of the media element.
      */
    public double volume;
    native public TextTrack addTextTrack(java.lang.String kind, java.lang.String label, java.lang.String language);
    /**
      * Returns a string that specifies whether the client can play a given media resource type.
      */
    native public java.lang.String canPlayType(java.lang.String type);
    /**
      * Fires immediately after the client loads the object.
      */
    native public void load();
    /**
      * Clears all effects from the media pipeline.
      */
    native public void msClearEffects();
    native public java.lang.Object msGetAsCastingSource();
    /**
      * Inserts the specified audio effect into media pipeline.
      */
    native public void msInsertAudioEffect(java.lang.String activatableClassId, java.lang.Boolean effectRequired, java.lang.Object config);
    native public void msSetMediaKeys(MSMediaKeys mediaKeys);
    /**
      * Specifies the media protection manager for a given media pipeline.
      */
    native public void msSetMediaProtectionManager(java.lang.Object mediaProtectionManager);
    /**
      * Pauses the current playback and sets paused to TRUE. This can be used to test whether the media is playing or paused. You can also use the pause or play events to tell whether the media is playing or not.
      */
    native public void pause();
    /**
      * Loads and starts playback of a media resource.
      */
    native public void play();
    public double HAVE_CURRENT_DATA;
    public double HAVE_ENOUGH_DATA;
    public double HAVE_FUTURE_DATA;
    public double HAVE_METADATA;
    public double HAVE_NOTHING;
    public double NETWORK_EMPTY;
    public double NETWORK_IDLE;
    public double NETWORK_LOADING;
    public double NETWORK_NO_SOURCE;
    native public void addEventListener(jsweet.util.StringTypes.MSContentZoom type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureChange type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureDoubleTap type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureEnd type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureHold type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureStart type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureTap type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSGotPointerCapture type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSInertiaStart type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSLostPointerCapture type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSManipulationStateChanged type, java.util.function.Function<MSManipulationEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerCancel type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerDown type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerEnter type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerLeave type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerMove type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerOut type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerOver type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerUp type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.activate type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.ariarequest type, java.util.function.Function<AriaRequestEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.beforeactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.beforecopy type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.beforecut type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.beforedeactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.beforepaste type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.blur type, java.util.function.Function<FocusEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.canplay type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.canplaythrough type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.change type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.click type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.command type, java.util.function.Function<CommandEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.contextmenu type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.copy type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.cuechange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.cut type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.dblclick type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.deactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.drag type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.dragend type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.dragenter type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.dragleave type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.dragover type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.dragstart type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.drop type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.durationchange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.emptied type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.ended type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.focus type, java.util.function.Function<FocusEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.gotpointercapture type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.input type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.keydown type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.keypress type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.keyup type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.loadeddata type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.loadedmetadata type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.lostpointercapture type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.mousedown type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.mouseenter type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.mouseleave type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.mousemove type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.mouseout type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.mouseover type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.mouseup type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.mousewheel type, java.util.function.Function<MouseWheelEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.msneedkey type, java.util.function.Function<MSMediaKeyNeededEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.paste type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pause type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.play type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.playing type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointercancel type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerdown type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerenter type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerleave type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointermove type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerout type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerover type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerup type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.ratechange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.reset type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.scroll type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.seeked type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.seeking type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.select type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.selectstart type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.stalled type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.submit type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.suspend type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.timeupdate type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.touchcancel type, java.util.function.Function<TouchEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.touchend type, java.util.function.Function<TouchEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.touchmove type, java.util.function.Function<TouchEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.touchstart type, java.util.function.Function<TouchEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.volumechange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.waiting type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.webkitfullscreenchange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.webkitfullscreenerror type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.wheel type, java.util.function.Function<WheelEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLMediaElement prototype;
    public HTMLMediaElement(){}
    native public TextTrack addTextTrack(java.lang.String kind, java.lang.String label);
    native public TextTrack addTextTrack(java.lang.String kind);
    /**
      * Inserts the specified audio effect into media pipeline.
      */
    native public void msInsertAudioEffect(java.lang.String activatableClassId, java.lang.Boolean effectRequired);
    /**
      * Specifies the media protection manager for a given media pipeline.
      */
    native public void msSetMediaProtectionManager();
    native public void addEventListener(jsweet.util.StringTypes.MSContentZoom type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureChange type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureDoubleTap type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureEnd type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureHold type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureStart type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSGestureTap type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSGotPointerCapture type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSInertiaStart type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSLostPointerCapture type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSManipulationStateChanged type, java.util.function.Function<MSManipulationEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerCancel type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerDown type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerEnter type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerLeave type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerMove type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerOut type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerOver type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.MSPointerUp type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.activate type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.ariarequest type, java.util.function.Function<AriaRequestEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.beforeactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.beforecopy type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.beforecut type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.beforedeactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.beforepaste type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.blur type, java.util.function.Function<FocusEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.canplay type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.canplaythrough type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.change type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.click type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.command type, java.util.function.Function<CommandEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.contextmenu type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.copy type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.cuechange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.cut type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.dblclick type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.deactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.drag type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.dragend type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.dragenter type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.dragleave type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.dragover type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.dragstart type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.drop type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.durationchange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.emptied type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.ended type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.focus type, java.util.function.Function<FocusEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.gotpointercapture type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.input type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.keydown type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.keypress type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.keyup type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.loadeddata type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.loadedmetadata type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.lostpointercapture type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.mousedown type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.mouseenter type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.mouseleave type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.mousemove type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.mouseout type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.mouseover type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.mouseup type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.mousewheel type, java.util.function.Function<MouseWheelEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.msneedkey type, java.util.function.Function<MSMediaKeyNeededEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.paste type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pause type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.play type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.playing type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointercancel type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerdown type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerenter type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerleave type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointermove type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerout type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerover type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerup type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.ratechange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.reset type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.scroll type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.seeked type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.seeking type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.select type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.selectstart type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.stalled type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.submit type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.suspend type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.timeupdate type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.touchcancel type, java.util.function.Function<TouchEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.touchend type, java.util.function.Function<TouchEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.touchmove type, java.util.function.Function<TouchEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.touchstart type, java.util.function.Function<TouchEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.volumechange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.waiting type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.webkitfullscreenchange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.webkitfullscreenerror type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.wheel type, java.util.function.Function<WheelEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

