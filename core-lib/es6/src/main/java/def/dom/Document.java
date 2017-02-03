package def.dom;

import def.dom.ErrorEvent;
import def.js.StringTypes;
import def.js.StringTypes.*;
import def.js.StringTypes.DeviceMotionEvent;
import def.js.StringTypes.DeviceOrientationEvent;
import def.js.StringTypes.DragEvent;
import def.js.StringTypes.MouseWheelEvent;
import def.js.StringTypes.MutationEvent;
import def.js.StringTypes.NavigationEventWithReferrer;
import def.js.StringTypes.PageTransitionEvent;
import def.js.StringTypes.TrackEvent;
import def.js.StringTypes.UnviewableContentIdentifiedEvent;
import def.js.StringTypes.WebGLContextEvent;

@jsweet.lang.Extends({GlobalEventHandlers.class,NodeSelector.class,DocumentEvent.class})
public class Document extends Node {
    /**
      * Sets or gets the URL for the current document. 
      */
    public java.lang.String URL;
    /**
      * Gets the URL for the document, stripped of any character encoding.
      */
    public java.lang.String URLUnencoded;
    /**
      * Gets the object that has the focus when the parent document has focus.
      */
    public Element activeElement;
    /**
      * Sets or gets the color of all active links in the document.
      */
    public java.lang.String alinkColor;
    /**
      * Returns a reference to the collection of elements contained by the object.
      */
    public HTMLCollection all;
    /**
      * Retrieves a collection of all a objects that have a name and/or id property. Objects in this collection are in HTML source order.
      */
    public HTMLCollection anchors;
    /**
      * Retrieves a collection of all applet objects in the document.
      */
    public HTMLCollection applets;
    /**
      * Deprecated. Sets or retrieves a value that indicates the background color behind the object. 
      */
    public java.lang.String bgColor;
    /**
      * Specifies the beginning and end of the document body.
      */
    public HTMLElement body;
    public java.lang.String characterSet;
    /**
      * Gets or sets the character set used to encode the object.
      */
    public java.lang.String charset;
    /**
      * Gets a value that indicates whether standards-compliant mode is switched on for the object.
      */
    public java.lang.String compatMode;
    public java.lang.String cookie;
    /**
      * Gets the default character set from the current regional language settings.
      */
    public java.lang.String defaultCharset;
    public Window defaultView;
    /**
      * Sets or gets a value that indicates whether the document can be edited.
      */
    public java.lang.String designMode;
    /**
      * Sets or retrieves a value that indicates the reading order of the object. 
      */
    public java.lang.String dir;
    /**
      * Gets an object representing the document type declaration associated with the current document. 
      */
    public DocumentType doctype;
    /**
      * Gets a reference to the root node of the document. 
      */
    public HTMLElement documentElement;
    /**
      * Sets or gets the security domain of the document. 
      */
    public java.lang.String domain;
    /**
      * Retrieves a collection of all embed objects in the document.
      */
    public HTMLCollection embeds;
    /**
      * Sets or gets the foreground (text) color of the document.
      */
    public java.lang.String fgColor;
    /**
      * Retrieves a collection, in source order, of all form objects in the document.
      */
    public HTMLCollection forms;
    public Element fullscreenElement;
    public java.lang.Boolean fullscreenEnabled;
    public HTMLHeadElement head;
    public java.lang.Boolean hidden;
    /**
      * Retrieves a collection, in source order, of img objects in the document.
      */
    public HTMLCollection images;
    /**
      * Gets the implementation object of the current document. 
      */
    public DOMImplementation implementation;
    /**
      * Returns the character encoding used to create the webpage that is loaded into the document object.
      */
    public java.lang.String inputEncoding;
    /**
      * Gets the date that the page was last modified, if the page supplies one. 
      */
    public java.lang.String lastModified;
    /**
      * Sets or gets the color of the document links. 
      */
    public java.lang.String linkColor;
    /**
      * Retrieves a collection of all a objects that specify the href property and all area objects in the document.
      */
    public HTMLCollection links;
    /**
      * Contains information about the current URL. 
      */
    public Location location;
    public java.lang.String media;
    public java.lang.Boolean msCSSOMElementFloatMetrics;
    public java.lang.Boolean msCapsLockWarningOff;
    public java.lang.Boolean msHidden;
    public java.lang.String msVisibilityState;
    /**
      * Fires when the user aborts the download.
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onabort;
    /**
      * Fires when the object is set as the active element.
      * @param ev The event.
      */
    public java.util.function.Function<UIEvent,java.lang.Object> onactivate;
    /**
      * Fires immediately before the object is set as the active element.
      * @param ev The event.
      */
    public java.util.function.Function<UIEvent,java.lang.Object> onbeforeactivate;
    /**
      * Fires immediately before the activeElement is changed from the current object to another object in the parent document.
      * @param ev The event.
      */
    public java.util.function.Function<UIEvent,java.lang.Object> onbeforedeactivate;
    /** 
      * Fires when the object loses the input focus. 
      * @param ev The focus event.
      */
    public java.util.function.Function<FocusEvent,java.lang.Object> onblur;
    /**
      * Occurs when playback is possible, but would require further buffering. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> oncanplay;
    public java.util.function.Function<Event,java.lang.Object> oncanplaythrough;
    /**
      * Fires when the contents of the object or selection have changed. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onchange;
    /**
      * Fires when the user clicks the left mouse button on the object
      * @param ev The mouse event.
      */
    public java.util.function.Function<MouseEvent,java.lang.Object> onclick;
    /**
      * Fires when the user clicks the right mouse button in the client area, opening the context menu. 
      * @param ev The mouse event.
      */
    public java.util.function.Function<PointerEvent,java.lang.Object> oncontextmenu;
    /**
      * Fires when the user double-clicks the object.
      * @param ev The mouse event.
      */
    public java.util.function.Function<MouseEvent,java.lang.Object> ondblclick;
    /**
      * Fires when the activeElement is changed from the current object to another object in the parent document.
      * @param ev The UI Event
      */
    public java.util.function.Function<UIEvent,java.lang.Object> ondeactivate;
    /**
      * Fires on the source object continuously during a drag operation.
      * @param ev The event.
      */
    public java.util.function.Function<DragEvent,java.lang.Object> ondrag;
    /**
      * Fires on the source object when the user releases the mouse at the close of a drag operation.
      * @param ev The event.
      */
    public java.util.function.Function<DragEvent,java.lang.Object> ondragend;
    /** 
      * Fires on the target element when the user drags the object to a valid drop target.
      * @param ev The drag event.
      */
    public java.util.function.Function<DragEvent,java.lang.Object> ondragenter;
    /** 
      * Fires on the target object when the user moves the mouse out of a valid drop target during a drag operation.
      * @param ev The drag event.
      */
    public java.util.function.Function<DragEvent,java.lang.Object> ondragleave;
    /**
      * Fires on the target element continuously while the user drags the object over a valid drop target.
      * @param ev The event.
      */
    public java.util.function.Function<DragEvent,java.lang.Object> ondragover;
    /**
      * Fires on the source object when the user starts to drag a text selection or selected object. 
      * @param ev The event.
      */
    public java.util.function.Function<DragEvent,java.lang.Object> ondragstart;
    public java.util.function.Function<DragEvent,java.lang.Object> ondrop;
    /**
      * Occurs when the duration attribute is updated. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> ondurationchange;
    /**
      * Occurs when the media element is reset to its initial state. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onemptied;
    /**
      * Occurs when the end of playback is reached. 
      * @param ev The event
      */
    public java.util.function.Function<Event,java.lang.Object> onended;
    /**
      * Fires when an error occurs during object loading.
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onerror;
    /**
      * Fires when the object receives focus. 
      * @param ev The event.
      */
    public java.util.function.Function<FocusEvent,java.lang.Object> onfocus;
    public java.util.function.Function<Event,java.lang.Object> onfullscreenchange;
    public java.util.function.Function<Event,java.lang.Object> onfullscreenerror;
    public java.util.function.Function<Event,java.lang.Object> oninput;
    /**
      * Fires when the user presses a key.
      * @param ev The keyboard event
      */
    public java.util.function.Function<KeyboardEvent,java.lang.Object> onkeydown;
    /**
      * Fires when the user presses an alphanumeric key.
      * @param ev The event.
      */
    public java.util.function.Function<KeyboardEvent,java.lang.Object> onkeypress;
    /**
      * Fires when the user releases a key.
      * @param ev The keyboard event
      */
    public java.util.function.Function<KeyboardEvent,java.lang.Object> onkeyup;
    /**
      * Fires immediately after the browser loads the object. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onload;
    /**
      * Occurs when media data is loaded at the current playback position. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onloadeddata;
    /**
      * Occurs when the duration and dimensions of the media have been determined.
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onloadedmetadata;
    /**
      * Occurs when Internet Explorer begins looking for media data. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onloadstart;
    /**
      * Fires when the user clicks the object with either mouse button. 
      * @param ev The mouse event.
      */
    public java.util.function.Function<MouseEvent,java.lang.Object> onmousedown;
    /**
      * Fires when the user moves the mouse over the object. 
      * @param ev The mouse event.
      */
    public java.util.function.Function<MouseEvent,java.lang.Object> onmousemove;
    /**
      * Fires when the user moves the mouse pointer outside the boundaries of the object. 
      * @param ev The mouse event.
      */
    public java.util.function.Function<MouseEvent,java.lang.Object> onmouseout;
    /**
      * Fires when the user moves the mouse pointer into the object.
      * @param ev The mouse event.
      */
    public java.util.function.Function<MouseEvent,java.lang.Object> onmouseover;
    /**
      * Fires when the user releases a mouse button while the mouse is over the object. 
      * @param ev The mouse event.
      */
    public java.util.function.Function<MouseEvent,java.lang.Object> onmouseup;
    /**
      * Fires when the wheel button is rotated. 
      * @param ev The mouse event
      */
    public java.util.function.Function<MouseWheelEvent,java.lang.Object> onmousewheel;
    public java.util.function.Function<UIEvent,java.lang.Object> onmscontentzoom;
    public java.util.function.Function<MSGestureEvent,java.lang.Object> onmsgesturechange;
    public java.util.function.Function<MSGestureEvent,java.lang.Object> onmsgesturedoubletap;
    public java.util.function.Function<MSGestureEvent,java.lang.Object> onmsgestureend;
    public java.util.function.Function<MSGestureEvent,java.lang.Object> onmsgesturehold;
    public java.util.function.Function<MSGestureEvent,java.lang.Object> onmsgesturestart;
    public java.util.function.Function<MSGestureEvent,java.lang.Object> onmsgesturetap;
    public java.util.function.Function<MSGestureEvent,java.lang.Object> onmsinertiastart;
    public java.util.function.Function<MSManipulationEvent,java.lang.Object> onmsmanipulationstatechanged;
    public java.util.function.Function<MSPointerEvent,java.lang.Object> onmspointercancel;
    public java.util.function.Function<MSPointerEvent,java.lang.Object> onmspointerdown;
    public java.util.function.Function<MSPointerEvent,java.lang.Object> onmspointerenter;
    public java.util.function.Function<MSPointerEvent,java.lang.Object> onmspointerleave;
    public java.util.function.Function<MSPointerEvent,java.lang.Object> onmspointermove;
    public java.util.function.Function<MSPointerEvent,java.lang.Object> onmspointerout;
    public java.util.function.Function<MSPointerEvent,java.lang.Object> onmspointerover;
    public java.util.function.Function<MSPointerEvent,java.lang.Object> onmspointerup;
    /**
      * Occurs when an item is removed from a Jump List of a webpage running in Site Mode. 
      * @param ev The event.
      */
    public java.util.function.Function<MSSiteModeEvent,java.lang.Object> onmssitemodejumplistitemremoved;
    /**
      * Occurs when a user clicks a button in a Thumbnail Toolbar of a webpage running in Site Mode.
      * @param ev The event.
      */
    public java.util.function.Function<MSSiteModeEvent,java.lang.Object> onmsthumbnailclick;
    /**
      * Occurs when playback is paused.
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onpause;
    /**
      * Occurs when the play method is requested. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onplay;
    /**
      * Occurs when the audio or video has started playing. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onplaying;
    public java.util.function.Function<Event,java.lang.Object> onpointerlockchange;
    public java.util.function.Function<Event,java.lang.Object> onpointerlockerror;
    /**
      * Occurs to indicate progress while downloading media data. 
      * @param ev The event.
      */
    public java.util.function.Function<ProgressEvent,java.lang.Object> onprogress;
    /**
      * Occurs when the playback rate is increased or decreased. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onratechange;
    /**
      * Fires when the state of the object has changed.
      * @param ev The event
      */
    public java.util.function.Function<ProgressEvent,java.lang.Object> onreadystatechange;
    /**
      * Fires when the user resets a form. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onreset;
    /**
      * Fires when the user repositions the scroll box in the scroll bar on the object. 
      * @param ev The event.
      */
    public java.util.function.Function<UIEvent,java.lang.Object> onscroll;
    /**
      * Occurs when the seek operation ends. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onseeked;
    /**
      * Occurs when the current playback position is moved. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onseeking;
    /**
      * Fires when the current selection changes.
      * @param ev The event.
      */
    public java.util.function.Function<UIEvent,java.lang.Object> onselect;
    public java.util.function.Function<Event,java.lang.Object> onselectstart;
    /**
      * Occurs when the download has stopped. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onstalled;
    /**
      * Fires when the user clicks the Stop button or leaves the Web page.
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onstop;
    public java.util.function.Function<Event,java.lang.Object> onsubmit;
    /**
      * Occurs if the load operation has been intentionally halted. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onsuspend;
    /**
      * Occurs to indicate the current playback position.
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> ontimeupdate;
    public java.util.function.Function<TouchEvent,java.lang.Object> ontouchcancel;
    public java.util.function.Function<TouchEvent,java.lang.Object> ontouchend;
    public java.util.function.Function<TouchEvent,java.lang.Object> ontouchmove;
    public java.util.function.Function<TouchEvent,java.lang.Object> ontouchstart;
    /**
      * Occurs when the volume is changed, or playback is muted or unmuted.
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onvolumechange;
    /**
      * Occurs when playback stops because the next frame of a video resource is not available. 
      * @param ev The event.
      */
    public java.util.function.Function<Event,java.lang.Object> onwaiting;
    public java.util.function.Function<Event,java.lang.Object> onwebkitfullscreenchange;
    public java.util.function.Function<Event,java.lang.Object> onwebkitfullscreenerror;
    public HTMLCollection plugins;
    public Element pointerLockElement;
    /**
      * Retrieves a value that indicates the current state of the object.
      */
    public java.lang.String readyState;
    /**
      * Gets the URL of the location that referred the user to the current page.
      */
    public java.lang.String referrer;
    /**
      * Gets the root svg element in the document hierarchy.
      */
    public SVGSVGElement rootElement;
    /**
      * Retrieves a collection of all script objects in the document.
      */
    public HTMLCollection scripts;
    public java.lang.String security;
    /**
      * Retrieves a collection of styleSheet objects representing the style sheets that correspond to each instance of a link or style object in the document.
      */
    public StyleSheetList styleSheets;
    /**
      * Contains the title of the document.
      */
    public java.lang.String title;
    public java.lang.String visibilityState;
    /** 
      * Sets or gets the color of the links that the user has visited.
      */
    public java.lang.String vlinkColor;
    public Element webkitCurrentFullScreenElement;
    public Element webkitFullscreenElement;
    public java.lang.Boolean webkitFullscreenEnabled;
    public java.lang.Boolean webkitIsFullScreen;
    public java.lang.String xmlEncoding;
    public java.lang.Boolean xmlStandalone;
    /**
      * Gets or sets the version attribute specified in the declaration of an XML document.
      */
    public java.lang.String xmlVersion;
    native public Node adoptNode(Node source);
    native public void captureEvents();
    native public void clear();
    /**
      * Closes an output stream and forces the sent data to display.
      */
    native public void close();
    /**
      * Creates an attribute object with a specified name.
      * @param name String that sets the attribute object's name.
      */
    native public Attr createAttribute(java.lang.String name);
    native public Attr createAttributeNS(java.lang.String namespaceURI, java.lang.String qualifiedName);
    native public CDATASection createCDATASection(java.lang.String data);
    /**
      * Creates a comment object with the specified data.
      * @param data Sets the comment object's data.
      */
    native public Comment createComment(java.lang.String data);
    /**
      * Creates a new document.
      */
    native public DocumentFragment createDocumentFragment();
    /**
      * Creates an instance of the element for the specified tag.
      * @param tagName The name of an element.
      */
    native public HTMLAnchorElement createElement(def.js.StringTypes.a tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.abbr tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.acronym tagName);
    native public HTMLBlockElement createElement(def.js.StringTypes.address tagName);
    native public HTMLAppletElement createElement(def.js.StringTypes.applet tagName);
    native public HTMLAreaElement createElement(def.js.StringTypes.area tagName);
    native public HTMLAudioElement createElement(def.js.StringTypes.audio tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.b tagName);
    native public HTMLBaseElement createElement(def.js.StringTypes.base tagName);
    native public HTMLBaseFontElement createElement(def.js.StringTypes.basefont tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.bdo tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.big tagName);
    native public HTMLBlockElement createElement(def.js.StringTypes.blockquote tagName);
    native public HTMLBodyElement createElement(def.js.StringTypes.body tagName);
    native public HTMLBRElement createElement(def.js.StringTypes.br tagName);
    native public HTMLButtonElement createElement(def.js.StringTypes.button tagName);
    native public HTMLCanvasElement createElement(def.js.StringTypes.canvas tagName);
    native public HTMLTableCaptionElement createElement(def.js.StringTypes.caption tagName);
    native public HTMLBlockElement createElement(def.js.StringTypes.center tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.cite tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.code tagName);
    native public HTMLTableColElement createElement(def.js.StringTypes.col tagName);
    native public HTMLTableColElement createElement(def.js.StringTypes.colgroup tagName);
    native public HTMLDataListElement createElement(def.js.StringTypes.datalist tagName);
    native public HTMLDDElement createElement(def.js.StringTypes.dd tagName);
    native public HTMLModElement createElement(def.js.StringTypes.del tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.dfn tagName);
    native public HTMLDirectoryElement createElement(def.js.StringTypes.dir tagName);
    native public HTMLDivElement createElement(def.js.StringTypes.div tagName);
    native public HTMLDListElement createElement(def.js.StringTypes.dl tagName);
    native public HTMLDTElement createElement(def.js.StringTypes.dt tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.em tagName);
    native public HTMLEmbedElement createElement(def.js.StringTypes.embed tagName);
    native public HTMLFieldSetElement createElement(def.js.StringTypes.fieldset tagName);
    native public HTMLFontElement createElement(def.js.StringTypes.font tagName);
    native public HTMLFormElement createElement(def.js.StringTypes.form tagName);
    native public HTMLFrameElement createElement(def.js.StringTypes.frame tagName);
    native public HTMLFrameSetElement createElement(def.js.StringTypes.frameset tagName);
    native public HTMLHeadingElement createElement(def.js.StringTypes.h1 tagName);
    native public HTMLHeadingElement createElement(def.js.StringTypes.h2 tagName);
    native public HTMLHeadingElement createElement(def.js.StringTypes.h3 tagName);
    native public HTMLHeadingElement createElement(def.js.StringTypes.h4 tagName);
    native public HTMLHeadingElement createElement(def.js.StringTypes.h5 tagName);
    native public HTMLHeadingElement createElement(def.js.StringTypes.h6 tagName);
    native public HTMLHeadElement createElement(def.js.StringTypes.head tagName);
    native public HTMLHRElement createElement(def.js.StringTypes.hr tagName);
    native public HTMLHtmlElement createElement(def.js.StringTypes.html tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.i tagName);
    native public HTMLIFrameElement createElement(def.js.StringTypes.iframe tagName);
    native public HTMLImageElement createElement(def.js.StringTypes.img tagName);
    native public HTMLInputElement createElement(def.js.StringTypes.input tagName);
    native public HTMLModElement createElement(def.js.StringTypes.ins tagName);
    native public HTMLIsIndexElement createElement(def.js.StringTypes.isindex tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.kbd tagName);
    native public HTMLBlockElement createElement(def.js.StringTypes.keygen tagName);
    native public HTMLLabelElement createElement(def.js.StringTypes.label tagName);
    native public HTMLLegendElement createElement(def.js.StringTypes.legend tagName);
    native public HTMLLIElement createElement(def.js.StringTypes.li tagName);
    native public HTMLLinkElement createElement(def.js.StringTypes.link tagName);
    native public HTMLBlockElement createElement(def.js.StringTypes.listing tagName);
    native public HTMLMapElement createElement(def.js.StringTypes.map tagName);
    native public HTMLMarqueeElement createElement(def.js.StringTypes.marquee tagName);
    native public HTMLMenuElement createElement(def.js.StringTypes.menu tagName);
    native public HTMLMetaElement createElement(def.js.StringTypes.meta tagName);
    native public HTMLNextIdElement createElement(def.js.StringTypes.nextid tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.nobr tagName);
    native public HTMLObjectElement createElement(def.js.StringTypes.object tagName);
    native public HTMLOListElement createElement(def.js.StringTypes.ol tagName);
    native public HTMLOptGroupElement createElement(def.js.StringTypes.optgroup tagName);
    native public HTMLOptionElement createElement(def.js.StringTypes.option tagName);
    native public HTMLParagraphElement createElement(def.js.StringTypes.p tagName);
    native public HTMLParamElement createElement(def.js.StringTypes.param tagName);
    native public HTMLBlockElement createElement(def.js.StringTypes.plaintext tagName);
    native public HTMLPreElement createElement(def.js.StringTypes.pre tagName);
    native public HTMLProgressElement createElement(def.js.StringTypes.progress tagName);
    native public HTMLQuoteElement createElement(def.js.StringTypes.q tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.rt tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.ruby tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.s tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.samp tagName);
    native public HTMLScriptElement createElement(def.js.StringTypes.script tagName);
    native public HTMLSelectElement createElement(def.js.StringTypes.select tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.small tagName);
    native public HTMLSourceElement createElement(def.js.StringTypes.source tagName);
    native public HTMLSpanElement createElement(def.js.StringTypes.span tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.strike tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.strong tagName);
    native public HTMLStyleElement createElement(def.js.StringTypes.style tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.sub tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.sup tagName);
    native public HTMLTableElement createElement(def.js.StringTypes.table tagName);
    native public HTMLTableSectionElement createElement(def.js.StringTypes.tbody tagName);
    native public HTMLTableDataCellElement createElement(def.js.StringTypes.td tagName);
    native public HTMLTextAreaElement createElement(def.js.StringTypes.textarea tagName);
    native public HTMLTableSectionElement createElement(def.js.StringTypes.tfoot tagName);
    native public HTMLTableHeaderCellElement createElement(def.js.StringTypes.th tagName);
    native public HTMLTableSectionElement createElement(def.js.StringTypes.thead tagName);
    native public HTMLTitleElement createElement(def.js.StringTypes.title tagName);
    native public HTMLTableRowElement createElement(def.js.StringTypes.tr tagName);
    native public HTMLTrackElement createElement(def.js.StringTypes.track tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.tt tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.u tagName);
    native public HTMLUListElement createElement(def.js.StringTypes.ul tagName);
    native public HTMLPhraseElement createElement(def.js.StringTypes.var tagName);
    native public HTMLVideoElement createElement(def.js.StringTypes.video tagName);
    native public MSHTMLWebViewElement createElement(def.js.StringTypes.x_ms_webview tagName);
    native public HTMLBlockElement createElement(def.js.StringTypes.xmp tagName);
    native public HTMLElement createElement(java.lang.String tagName);
    native public Element createElementNS(java.lang.String namespaceURI, java.lang.String qualifiedName);
    native public XPathExpression createExpression(java.lang.String expression, XPathNSResolver resolver);
    native public XPathNSResolver createNSResolver(Node nodeResolver);
    /**
      * Creates a NodeIterator object that you can use to traverse filtered lists of nodes or elements in a document. 
      * @param root The root element or node to start traversing on.
      * @param whatToShow The type of nodes or elements to appear in the node list
      * @param filter A custom NodeFilter function to use. For more information, see filter. Use null for no filter.
      * @param entityReferenceExpansion A flag that specifies whether entity reference nodes are expanded.
      */
    native public NodeIterator createNodeIterator(Node root, double whatToShow, NodeFilter filter, java.lang.Boolean entityReferenceExpansion);
    native public ProcessingInstruction createProcessingInstruction(java.lang.String target, java.lang.String data);
    /**
      *  Returns an empty range object that has both of its boundary points positioned at the beginning of the document. 
      */
    native public Range createRange();
    /**
      * Creates a text string from the specified value. 
      * @param data String that specifies the nodeValue property of the text node.
      */
    native public Text createTextNode(java.lang.String data);
    native public Touch createTouch(java.lang.Object view, EventTarget target, double identifier, double pageX, double pageY, double screenX, double screenY);
    native public TouchList createTouchList(Touch... touches);
    /**
      * Creates a TreeWalker object that you can use to traverse filtered lists of nodes or elements in a document.
      * @param root The root element or node to start traversing on.
      * @param whatToShow The type of nodes or elements to appear in the node list. For more information, see whatToShow.
      * @param filter A custom NodeFilter function to use.
      * @param entityReferenceExpansion A flag that specifies whether entity reference nodes are expanded.
      */
    native public TreeWalker createTreeWalker(Node root, double whatToShow, NodeFilter filter, java.lang.Boolean entityReferenceExpansion);
    /**
      * Returns the element for the specified x coordinate and the specified y coordinate. 
      * @param x The x-offset
      * @param y The y-offset
      */
    native public Element elementFromPoint(double x, double y);
    native public XPathResult evaluate(java.lang.String expression, Node contextNode, XPathNSResolver resolver, double type, XPathResult result);
    /**
      * Executes a command on the current document, current selection, or the given range.
      * @param commandId String that specifies the command to execute. This command can be any of the command identifiers that can be executed in script.
      * @param showUI Display the user interface, defaults to false.
      * @param value Value to assign.
      */
    native public java.lang.Boolean execCommand(java.lang.String commandId, java.lang.Boolean showUI, java.lang.Object value);
    /**
      * Displays help information for the given command identifier.
      * @param commandId Displays help information for the given command identifier.
      */
    native public java.lang.Boolean execCommandShowHelp(java.lang.String commandId);
    native public void exitFullscreen();
    native public void exitPointerLock();
    /**
      * Causes the element to receive the focus and executes the code specified by the onfocus event.
      */
    native public void focus();
    /**
      * Returns a reference to the first object with the specified value of the ID or NAME attribute.
      * @param elementId String that specifies the ID value. Case-insensitive.
      */
    native public HTMLElement getElementById(java.lang.String elementId);
    native public NodeList getElementsByClassName(java.lang.String classNames);
    /**
      * Gets a collection of objects based on the value of the NAME or ID attribute.
      * @param elementName Gets a collection of objects based on the value of the NAME or ID attribute.
      */
    native public NodeList getElementsByName(java.lang.String elementName);
    /**
      * Retrieves a collection of objects based on the specified element name.
      * @param name Specifies the name of an element.
      */
    native public NodeListOf<HTMLAnchorElement> getElementsByTagName(def.js.StringTypes.a tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.abbr tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.acronym tagname);
    native public NodeListOf<HTMLBlockElement> getElementsByTagName(def.js.StringTypes.address tagname);
    native public NodeListOf<HTMLAppletElement> getElementsByTagName(def.js.StringTypes.applet tagname);
    native public NodeListOf<HTMLAreaElement> getElementsByTagName(def.js.StringTypes.area tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.article tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.aside tagname);
    native public NodeListOf<HTMLAudioElement> getElementsByTagName(def.js.StringTypes.audio tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.b tagname);
    native public NodeListOf<HTMLBaseElement> getElementsByTagName(def.js.StringTypes.base tagname);
    native public NodeListOf<HTMLBaseFontElement> getElementsByTagName(def.js.StringTypes.basefont tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.bdo tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.big tagname);
    native public NodeListOf<HTMLBlockElement> getElementsByTagName(def.js.StringTypes.blockquote tagname);
    native public NodeListOf<HTMLBodyElement> getElementsByTagName(def.js.StringTypes.body tagname);
    native public NodeListOf<HTMLBRElement> getElementsByTagName(def.js.StringTypes.br tagname);
    native public NodeListOf<HTMLButtonElement> getElementsByTagName(def.js.StringTypes.button tagname);
    native public NodeListOf<HTMLCanvasElement> getElementsByTagName(def.js.StringTypes.canvas tagname);
    native public NodeListOf<HTMLTableCaptionElement> getElementsByTagName(def.js.StringTypes.caption tagname);
    native public NodeListOf<HTMLBlockElement> getElementsByTagName(def.js.StringTypes.center tagname);
    native public NodeListOf<SVGCircleElement> getElementsByTagName(def.js.StringTypes.circle tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.cite tagname);
    native public NodeListOf<SVGClipPathElement> getElementsByTagName(def.js.StringTypes.clippath tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.code tagname);
    native public NodeListOf<HTMLTableColElement> getElementsByTagName(def.js.StringTypes.col tagname);
    native public NodeListOf<HTMLTableColElement> getElementsByTagName(def.js.StringTypes.colgroup tagname);
    native public NodeListOf<HTMLDataListElement> getElementsByTagName(def.js.StringTypes.datalist tagname);
    native public NodeListOf<HTMLDDElement> getElementsByTagName(def.js.StringTypes.dd tagname);
    native public NodeListOf<SVGDefsElement> getElementsByTagName(def.js.StringTypes.defs tagname);
    native public NodeListOf<HTMLModElement> getElementsByTagName(def.js.StringTypes.del tagname);
    native public NodeListOf<SVGDescElement> getElementsByTagName(def.js.StringTypes.desc tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.dfn tagname);
    native public NodeListOf<HTMLDirectoryElement> getElementsByTagName(def.js.StringTypes.dir tagname);
    native public NodeListOf<HTMLDivElement> getElementsByTagName(def.js.StringTypes.div tagname);
    native public NodeListOf<HTMLDListElement> getElementsByTagName(def.js.StringTypes.dl tagname);
    native public NodeListOf<HTMLDTElement> getElementsByTagName(def.js.StringTypes.dt tagname);
    native public NodeListOf<SVGEllipseElement> getElementsByTagName(def.js.StringTypes.ellipse tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.em tagname);
    native public NodeListOf<HTMLEmbedElement> getElementsByTagName(def.js.StringTypes.embed tagname);
    native public NodeListOf<SVGFEBlendElement> getElementsByTagName(def.js.StringTypes.feblend tagname);
    native public NodeListOf<SVGFEColorMatrixElement> getElementsByTagName(def.js.StringTypes.fecolormatrix tagname);
    native public NodeListOf<SVGFEComponentTransferElement> getElementsByTagName(def.js.StringTypes.fecomponenttransfer tagname);
    native public NodeListOf<SVGFECompositeElement> getElementsByTagName(def.js.StringTypes.fecomposite tagname);
    native public NodeListOf<SVGFEConvolveMatrixElement> getElementsByTagName(def.js.StringTypes.feconvolvematrix tagname);
    native public NodeListOf<SVGFEDiffuseLightingElement> getElementsByTagName(def.js.StringTypes.fediffuselighting tagname);
    native public NodeListOf<SVGFEDisplacementMapElement> getElementsByTagName(def.js.StringTypes.fedisplacementmap tagname);
    native public NodeListOf<SVGFEDistantLightElement> getElementsByTagName(def.js.StringTypes.fedistantlight tagname);
    native public NodeListOf<SVGFEFloodElement> getElementsByTagName(def.js.StringTypes.feflood tagname);
    native public NodeListOf<SVGFEFuncAElement> getElementsByTagName(def.js.StringTypes.fefunca tagname);
    native public NodeListOf<SVGFEFuncBElement> getElementsByTagName(def.js.StringTypes.fefuncb tagname);
    native public NodeListOf<SVGFEFuncGElement> getElementsByTagName(def.js.StringTypes.fefuncg tagname);
    native public NodeListOf<SVGFEFuncRElement> getElementsByTagName(def.js.StringTypes.fefuncr tagname);
    native public NodeListOf<SVGFEGaussianBlurElement> getElementsByTagName(def.js.StringTypes.fegaussianblur tagname);
    native public NodeListOf<SVGFEImageElement> getElementsByTagName(def.js.StringTypes.feimage tagname);
    native public NodeListOf<SVGFEMergeElement> getElementsByTagName(def.js.StringTypes.femerge tagname);
    native public NodeListOf<SVGFEMergeNodeElement> getElementsByTagName(def.js.StringTypes.femergenode tagname);
    native public NodeListOf<SVGFEMorphologyElement> getElementsByTagName(def.js.StringTypes.femorphology tagname);
    native public NodeListOf<SVGFEOffsetElement> getElementsByTagName(def.js.StringTypes.feoffset tagname);
    native public NodeListOf<SVGFEPointLightElement> getElementsByTagName(def.js.StringTypes.fepointlight tagname);
    native public NodeListOf<SVGFESpecularLightingElement> getElementsByTagName(def.js.StringTypes.fespecularlighting tagname);
    native public NodeListOf<SVGFESpotLightElement> getElementsByTagName(def.js.StringTypes.fespotlight tagname);
    native public NodeListOf<SVGFETileElement> getElementsByTagName(def.js.StringTypes.fetile tagname);
    native public NodeListOf<SVGFETurbulenceElement> getElementsByTagName(def.js.StringTypes.feturbulence tagname);
    native public NodeListOf<HTMLFieldSetElement> getElementsByTagName(def.js.StringTypes.fieldset tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.figcaption tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.figure tagname);
    native public NodeListOf<SVGFilterElement> getElementsByTagName(def.js.StringTypes.filter tagname);
    native public NodeListOf<HTMLFontElement> getElementsByTagName(def.js.StringTypes.font tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.footer tagname);
    native public NodeListOf<SVGForeignObjectElement> getElementsByTagName(def.js.StringTypes.foreignobject tagname);
    native public NodeListOf<HTMLFormElement> getElementsByTagName(def.js.StringTypes.form tagname);
    native public NodeListOf<HTMLFrameElement> getElementsByTagName(def.js.StringTypes.frame tagname);
    native public NodeListOf<HTMLFrameSetElement> getElementsByTagName(def.js.StringTypes.frameset tagname);
    native public NodeListOf<SVGGElement> getElementsByTagName(def.js.StringTypes.g tagname);
    native public NodeListOf<HTMLHeadingElement> getElementsByTagName(def.js.StringTypes.h1 tagname);
    native public NodeListOf<HTMLHeadingElement> getElementsByTagName(def.js.StringTypes.h2 tagname);
    native public NodeListOf<HTMLHeadingElement> getElementsByTagName(def.js.StringTypes.h3 tagname);
    native public NodeListOf<HTMLHeadingElement> getElementsByTagName(def.js.StringTypes.h4 tagname);
    native public NodeListOf<HTMLHeadingElement> getElementsByTagName(def.js.StringTypes.h5 tagname);
    native public NodeListOf<HTMLHeadingElement> getElementsByTagName(def.js.StringTypes.h6 tagname);
    native public NodeListOf<HTMLHeadElement> getElementsByTagName(def.js.StringTypes.head tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.header tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.hgroup tagname);
    native public NodeListOf<HTMLHRElement> getElementsByTagName(def.js.StringTypes.hr tagname);
    native public NodeListOf<HTMLHtmlElement> getElementsByTagName(def.js.StringTypes.html tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.i tagname);
    native public NodeListOf<HTMLIFrameElement> getElementsByTagName(def.js.StringTypes.iframe tagname);
    native public NodeListOf<SVGImageElement> getElementsByTagName(def.js.StringTypes.image tagname);
    native public NodeListOf<HTMLImageElement> getElementsByTagName(def.js.StringTypes.img tagname);
    native public NodeListOf<HTMLInputElement> getElementsByTagName(def.js.StringTypes.input tagname);
    native public NodeListOf<HTMLModElement> getElementsByTagName(def.js.StringTypes.ins tagname);
    native public NodeListOf<HTMLIsIndexElement> getElementsByTagName(def.js.StringTypes.isindex tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.kbd tagname);
    native public NodeListOf<HTMLBlockElement> getElementsByTagName(def.js.StringTypes.keygen tagname);
    native public NodeListOf<HTMLLabelElement> getElementsByTagName(def.js.StringTypes.label tagname);
    native public NodeListOf<HTMLLegendElement> getElementsByTagName(def.js.StringTypes.legend tagname);
    native public NodeListOf<HTMLLIElement> getElementsByTagName(def.js.StringTypes.li tagname);
    native public NodeListOf<SVGLineElement> getElementsByTagName(def.js.StringTypes.line tagname);
    native public NodeListOf<SVGLinearGradientElement> getElementsByTagName(def.js.StringTypes.lineargradient tagname);
    native public NodeListOf<HTMLLinkElement> getElementsByTagName(def.js.StringTypes.link tagname);
    native public NodeListOf<HTMLBlockElement> getElementsByTagName(def.js.StringTypes.listing tagname);
    native public NodeListOf<HTMLMapElement> getElementsByTagName(def.js.StringTypes.map tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.mark tagname);
    native public NodeListOf<SVGMarkerElement> getElementsByTagName(def.js.StringTypes.marker tagname);
    native public NodeListOf<HTMLMarqueeElement> getElementsByTagName(def.js.StringTypes.marquee tagname);
    native public NodeListOf<SVGMaskElement> getElementsByTagName(def.js.StringTypes.mask tagname);
    native public NodeListOf<HTMLMenuElement> getElementsByTagName(def.js.StringTypes.menu tagname);
    native public NodeListOf<HTMLMetaElement> getElementsByTagName(def.js.StringTypes.meta tagname);
    native public NodeListOf<SVGMetadataElement> getElementsByTagName(def.js.StringTypes.metadata tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.nav tagname);
    native public NodeListOf<HTMLNextIdElement> getElementsByTagName(def.js.StringTypes.nextid tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.nobr tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.noframes tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.noscript tagname);
    native public NodeListOf<HTMLObjectElement> getElementsByTagName(def.js.StringTypes.object tagname);
    native public NodeListOf<HTMLOListElement> getElementsByTagName(def.js.StringTypes.ol tagname);
    native public NodeListOf<HTMLOptGroupElement> getElementsByTagName(def.js.StringTypes.optgroup tagname);
    native public NodeListOf<HTMLOptionElement> getElementsByTagName(def.js.StringTypes.option tagname);
    native public NodeListOf<HTMLParagraphElement> getElementsByTagName(def.js.StringTypes.p tagname);
    native public NodeListOf<HTMLParamElement> getElementsByTagName(def.js.StringTypes.param tagname);
    native public NodeListOf<SVGPathElement> getElementsByTagName(def.js.StringTypes.path tagname);
    native public NodeListOf<SVGPatternElement> getElementsByTagName(def.js.StringTypes.pattern tagname);
    native public NodeListOf<HTMLBlockElement> getElementsByTagName(def.js.StringTypes.plaintext tagname);
    native public NodeListOf<SVGPolygonElement> getElementsByTagName(def.js.StringTypes.polygon tagname);
    native public NodeListOf<SVGPolylineElement> getElementsByTagName(def.js.StringTypes.polyline tagname);
    native public NodeListOf<HTMLPreElement> getElementsByTagName(def.js.StringTypes.pre tagname);
    native public NodeListOf<HTMLProgressElement> getElementsByTagName(def.js.StringTypes.progress tagname);
    native public NodeListOf<HTMLQuoteElement> getElementsByTagName(def.js.StringTypes.q tagname);
    native public NodeListOf<SVGRadialGradientElement> getElementsByTagName(def.js.StringTypes.radialgradient tagname);
    native public NodeListOf<SVGRectElement> getElementsByTagName(def.js.StringTypes.rect tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.rt tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.ruby tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.s tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.samp tagname);
    native public NodeListOf<HTMLScriptElement> getElementsByTagName(def.js.StringTypes.script tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.section tagname);
    native public NodeListOf<HTMLSelectElement> getElementsByTagName(def.js.StringTypes.select tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.small tagname);
    native public NodeListOf<HTMLSourceElement> getElementsByTagName(def.js.StringTypes.source tagname);
    native public NodeListOf<HTMLSpanElement> getElementsByTagName(def.js.StringTypes.span tagname);
    native public NodeListOf<SVGStopElement> getElementsByTagName(def.js.StringTypes.stop tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.strike tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.strong tagname);
    native public NodeListOf<HTMLStyleElement> getElementsByTagName(def.js.StringTypes.style tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.sub tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.sup tagname);
    native public NodeListOf<SVGSVGElement> getElementsByTagName(def.js.StringTypes.svg tagname);
    native public NodeListOf<SVGSwitchElement> getElementsByTagName(def.js.StringTypes.Switch tagname);
    native public NodeListOf<SVGSymbolElement> getElementsByTagName(def.js.StringTypes.symbol tagname);
    native public NodeListOf<HTMLTableElement> getElementsByTagName(def.js.StringTypes.table tagname);
    native public NodeListOf<HTMLTableSectionElement> getElementsByTagName(def.js.StringTypes.tbody tagname);
    native public NodeListOf<HTMLTableDataCellElement> getElementsByTagName(def.js.StringTypes.td tagname);
    native public NodeListOf<SVGTextElement> getElementsByTagName(def.js.StringTypes.text tagname);
    native public NodeListOf<SVGTextPathElement> getElementsByTagName(def.js.StringTypes.textpath tagname);
    native public NodeListOf<HTMLTextAreaElement> getElementsByTagName(def.js.StringTypes.textarea tagname);
    native public NodeListOf<HTMLTableSectionElement> getElementsByTagName(def.js.StringTypes.tfoot tagname);
    native public NodeListOf<HTMLTableHeaderCellElement> getElementsByTagName(def.js.StringTypes.th tagname);
    native public NodeListOf<HTMLTableSectionElement> getElementsByTagName(def.js.StringTypes.thead tagname);
    native public NodeListOf<HTMLTitleElement> getElementsByTagName(def.js.StringTypes.title tagname);
    native public NodeListOf<HTMLTableRowElement> getElementsByTagName(def.js.StringTypes.tr tagname);
    native public NodeListOf<HTMLTrackElement> getElementsByTagName(def.js.StringTypes.track tagname);
    native public NodeListOf<SVGTSpanElement> getElementsByTagName(def.js.StringTypes.tspan tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.tt tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.u tagname);
    native public NodeListOf<HTMLUListElement> getElementsByTagName(def.js.StringTypes.ul tagname);
    native public NodeListOf<SVGUseElement> getElementsByTagName(def.js.StringTypes.use tagname);
    native public NodeListOf<HTMLPhraseElement> getElementsByTagName(def.js.StringTypes.var tagname);
    native public NodeListOf<HTMLVideoElement> getElementsByTagName(def.js.StringTypes.video tagname);
    native public NodeListOf<SVGViewElement> getElementsByTagName(def.js.StringTypes.view tagname);
    native public NodeListOf<HTMLElement> getElementsByTagName(def.js.StringTypes.wbr tagname);
    native public NodeListOf<MSHTMLWebViewElement> getElementsByTagName(def.js.StringTypes.x_ms_webview tagname);
    native public NodeListOf<HTMLBlockElement> getElementsByTagName(def.js.StringTypes.xmp tagname);
    native public NodeList getElementsByTagName(java.lang.String tagname);
    native public NodeList getElementsByTagNameNS(java.lang.String namespaceURI, java.lang.String localName);
    /**
      * Returns an object representing the current selection of the document that is loaded into the object displaying a webpage.
      */
    native public Selection getSelection();
    /**
      * Gets a value indicating whether the object currently has focus.
      */
    native public java.lang.Boolean hasFocus();
    native public Node importNode(Node importedNode, java.lang.Boolean deep);
    native public NodeList msElementsFromPoint(double x, double y);
    native public NodeList msElementsFromRect(double left, double top, double width, double height);
    native public Document msGetPrintDocumentForNamedFlow(java.lang.String flowName);
    native public void msSetPrintDocumentUriForNamedFlow(java.lang.String flowName, java.lang.String uri);
    /**
      * Opens a new window and loads a document specified by a given URL. Also, opens a new window that uses the url parameter and the name parameter to collect the output of the write method and the writeln method.
      * @param url Specifies a MIME type for the document.
      * @param name Specifies the name of the window. This name is used as the value for the TARGET attribute on a form or an anchor element.
      * @param features Contains a list of items separated by commas. Each item consists of an option and a value, separated by an equals sign (for example, "fullscreen=yes, toolbar=yes"). The following values are supported.
      * @param replace Specifies whether the existing entry for the document is replaced in the history list.
      */
    native public jsweet.util.union.Union<Document,Window> open(java.lang.String url, java.lang.String name, java.lang.String features, java.lang.Boolean replace);
    /** 
      * Returns a Boolean value that indicates whether a specified command can be successfully executed using execCommand, given the current state of the document.
      * @param commandId Specifies a command identifier.
      */
    native public java.lang.Boolean queryCommandEnabled(java.lang.String commandId);
    /**
      * Returns a Boolean value that indicates whether the specified command is in the indeterminate state.
      * @param commandId String that specifies a command identifier.
      */
    native public java.lang.Boolean queryCommandIndeterm(java.lang.String commandId);
    /**
      * Returns a Boolean value that indicates the current state of the command.
      * @param commandId String that specifies a command identifier.
      */
    native public java.lang.Boolean queryCommandState(java.lang.String commandId);
    /**
      * Returns a Boolean value that indicates whether the current command is supported on the current range.
      * @param commandId Specifies a command identifier.
      */
    native public java.lang.Boolean queryCommandSupported(java.lang.String commandId);
    /**
      * Retrieves the string associated with a command.
      * @param commandId String that contains the identifier of a command. This can be any command identifier given in the list of Command Identifiers. 
      */
    native public java.lang.String queryCommandText(java.lang.String commandId);
    /**
      * Returns the current value of the document, range, or current selection for the given command.
      * @param commandId String that specifies a command identifier.
      */
    native public java.lang.String queryCommandValue(java.lang.String commandId);
    native public void releaseEvents();
    /**
      * Allows updating the print settings for the page.
      */
    native public void updateSettings();
    native public void webkitCancelFullScreen();
    native public void webkitExitFullscreen();
    /**
      * Writes one or more HTML expressions to a document in the specified window. 
      * @param content Specifies the text and HTML tags to write.
      */
    native public void write(java.lang.String... content);
    /**
      * Writes one or more HTML expressions, followed by a carriage return, to a document in the specified window. 
      * @param content The text and HTML tags to write.
      */
    native public void writeln(java.lang.String... content);
    native public void addEventListener(def.js.StringTypes.MSContentZoom type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSGestureChange type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSGestureDoubleTap type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSGestureEnd type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSGestureHold type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSGestureStart type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSGestureTap type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSInertiaStart type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSManipulationStateChanged type, java.util.function.Function<MSManipulationEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSPointerCancel type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSPointerDown type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSPointerEnter type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSPointerLeave type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSPointerMove type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSPointerOut type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSPointerOver type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.MSPointerUp type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.activate type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.beforeactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.beforedeactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.blur type, java.util.function.Function<FocusEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.canplay type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.canplaythrough type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.change type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.click type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.contextmenu type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.dblclick type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.deactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.drag type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.dragend type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.dragenter type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.dragleave type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.dragover type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.dragstart type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.drop type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.durationchange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.emptied type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.ended type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.focus type, java.util.function.Function<FocusEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.fullscreenchange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.fullscreenerror type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.input type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.keydown type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.keypress type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.keyup type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.loadeddata type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.loadedmetadata type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.mousedown type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.mousemove type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.mouseout type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.mouseover type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.mouseup type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.mousewheel type, java.util.function.Function<MouseWheelEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.mssitemodejumplistitemremoved type, java.util.function.Function<MSSiteModeEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.msthumbnailclick type, java.util.function.Function<MSSiteModeEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pause type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.play type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.playing type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pointerlockchange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pointerlockerror type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.ratechange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.readystatechange type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.reset type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.scroll type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.seeked type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.seeking type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.select type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.selectstart type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.stalled type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.stop type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.submit type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.suspend type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.timeupdate type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.touchcancel type, java.util.function.Function<TouchEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.touchend type, java.util.function.Function<TouchEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.touchmove type, java.util.function.Function<TouchEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.touchstart type, java.util.function.Function<TouchEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.volumechange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.waiting type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.webkitfullscreenchange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.webkitfullscreenerror type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    public static Document prototype;
    public Document(){}
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointercancel;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerdown;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerenter;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerleave;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointermove;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerout;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerover;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerup;
    public java.util.function.Function<WheelEvent,java.lang.Object> onwheel;
    native public void addEventListener(def.js.StringTypes.pointercancel type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pointerdown type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pointerenter type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pointerleave type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pointermove type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pointerout type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pointerover type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pointerup type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.wheel type, java.util.function.Function<WheelEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public Element querySelector(java.lang.String selectors);
    native public NodeList querySelectorAll(java.lang.String selectors);
    native public AnimationEvent createEvent(def.js.StringTypes.AnimationEvent eventInterface);
    native public AriaRequestEvent createEvent(def.js.StringTypes.AriaRequestEvent eventInterface);
    native public AudioProcessingEvent createEvent(def.js.StringTypes.AudioProcessingEvent eventInterface);
    native public BeforeUnloadEvent createEvent(def.js.StringTypes.BeforeUnloadEvent eventInterface);
    native public ClipboardEvent createEvent(def.js.StringTypes.ClipboardEvent eventInterface);
    native public CloseEvent createEvent(def.js.StringTypes.CloseEvent eventInterface);
    native public CommandEvent createEvent(def.js.StringTypes.CommandEvent eventInterface);
    native public CompositionEvent createEvent(def.js.StringTypes.CompositionEvent eventInterface);
    native public CustomEvent createEvent(def.js.StringTypes.CustomEvent eventInterface);
    native public DeviceMotionEvent createEvent(def.js.StringTypes.DeviceMotionEvent eventInterface);
    native public DeviceOrientationEvent createEvent(def.js.StringTypes.DeviceOrientationEvent eventInterface);
    native public DragEvent createEvent(def.js.StringTypes.DragEvent eventInterface);
    native public ErrorEvent createEvent(def.js.StringTypes.ErrorEvent eventInterface);
    native public Event createEvent(def.js.StringTypes.Event eventInterface);
    native public Event createEvent(def.js.StringTypes.Events eventInterface);
    native public FocusEvent createEvent(def.js.StringTypes.FocusEvent eventInterface);
    native public GamepadEvent createEvent(def.js.StringTypes.GamepadEvent eventInterface);
    native public HashChangeEvent createEvent(def.js.StringTypes.HashChangeEvent eventInterface);
    native public IDBVersionChangeEvent createEvent(def.js.StringTypes.IDBVersionChangeEvent eventInterface);
    native public KeyboardEvent createEvent(def.js.StringTypes.KeyboardEvent eventInterface);
    native public LongRunningScriptDetectedEvent createEvent(def.js.StringTypes.LongRunningScriptDetectedEvent eventInterface);
    native public MSGestureEvent createEvent(def.js.StringTypes.MSGestureEvent eventInterface);
    native public MSManipulationEvent createEvent(def.js.StringTypes.MSManipulationEvent eventInterface);
    native public MSMediaKeyMessageEvent createEvent(def.js.StringTypes.MSMediaKeyMessageEvent eventInterface);
    native public MSMediaKeyNeededEvent createEvent(def.js.StringTypes.MSMediaKeyNeededEvent eventInterface);
    native public MSPointerEvent createEvent(def.js.StringTypes.MSPointerEvent eventInterface);
    native public MSSiteModeEvent createEvent(def.js.StringTypes.MSSiteModeEvent eventInterface);
    native public MessageEvent createEvent(def.js.StringTypes.MessageEvent eventInterface);
    native public MouseEvent createEvent(def.js.StringTypes.MouseEvent eventInterface);
    native public MouseEvent createEvent(def.js.StringTypes.MouseEvents eventInterface);
    native public MouseWheelEvent createEvent(def.js.StringTypes.MouseWheelEvent eventInterface);
    native public MutationEvent createEvent(def.js.StringTypes.MutationEvent eventInterface);
    native public MutationEvent createEvent(def.js.StringTypes.MutationEvents eventInterface);
    native public NavigationCompletedEvent createEvent(def.js.StringTypes.NavigationCompletedEvent eventInterface);
    native public NavigationEvent createEvent(def.js.StringTypes.NavigationEvent eventInterface);
    native public NavigationEventWithReferrer createEvent(def.js.StringTypes.NavigationEventWithReferrer eventInterface);
    native public OfflineAudioCompletionEvent createEvent(def.js.StringTypes.OfflineAudioCompletionEvent eventInterface);
    native public PageTransitionEvent createEvent(def.js.StringTypes.PageTransitionEvent eventInterface);
    native public PermissionRequestedEvent createEvent(def.js.StringTypes.PermissionRequestedEvent eventInterface);
    native public PointerEvent createEvent(def.js.StringTypes.PointerEvent eventInterface);
    native public PopStateEvent createEvent(def.js.StringTypes.PopStateEvent eventInterface);
    native public ProgressEvent createEvent(def.js.StringTypes.ProgressEvent eventInterface);
    native public SVGZoomEvent createEvent(def.js.StringTypes.SVGZoomEvent eventInterface);
    native public SVGZoomEvent createEvent(def.js.StringTypes.SVGZoomEvents eventInterface);
    native public ScriptNotifyEvent createEvent(def.js.StringTypes.ScriptNotifyEvent eventInterface);
    native public StorageEvent createEvent(def.js.StringTypes.StorageEvent eventInterface);
    native public TextEvent createEvent(def.js.StringTypes.TextEvent eventInterface);
    native public TouchEvent createEvent(def.js.StringTypes.TouchEvent eventInterface);
    native public TrackEvent createEvent(def.js.StringTypes.TrackEvent eventInterface);
    native public TransitionEvent createEvent(def.js.StringTypes.TransitionEvent eventInterface);
    native public UIEvent createEvent(def.js.StringTypes.UIEvent eventInterface);
    native public UIEvent createEvent(def.js.StringTypes.UIEvents eventInterface);
    native public UnviewableContentIdentifiedEvent createEvent(def.js.StringTypes.UnviewableContentIdentifiedEvent eventInterface);
    native public WebGLContextEvent createEvent(def.js.StringTypes.WebGLContextEvent eventInterface);
    native public WheelEvent createEvent(def.js.StringTypes.WheelEvent eventInterface);
    native public Event createEvent(java.lang.String eventInterface);
    /**
      * Creates a NodeIterator object that you can use to traverse filtered lists of nodes or elements in a document. 
      * @param root The root element or node to start traversing on.
      * @param whatToShow The type of nodes or elements to appear in the node list
      * @param filter A custom NodeFilter function to use. For more information, see filter. Use null for no filter.
      * @param entityReferenceExpansion A flag that specifies whether entity reference nodes are expanded.
      */
    native public NodeIterator createNodeIterator(Node root, double whatToShow, NodeFilter filter);
    /**
      * Creates a NodeIterator object that you can use to traverse filtered lists of nodes or elements in a document. 
      * @param root The root element or node to start traversing on.
      * @param whatToShow The type of nodes or elements to appear in the node list
      * @param filter A custom NodeFilter function to use. For more information, see filter. Use null for no filter.
      * @param entityReferenceExpansion A flag that specifies whether entity reference nodes are expanded.
      */
    native public NodeIterator createNodeIterator(Node root, double whatToShow);
    /**
      * Creates a NodeIterator object that you can use to traverse filtered lists of nodes or elements in a document. 
      * @param root The root element or node to start traversing on.
      * @param whatToShow The type of nodes or elements to appear in the node list
      * @param filter A custom NodeFilter function to use. For more information, see filter. Use null for no filter.
      * @param entityReferenceExpansion A flag that specifies whether entity reference nodes are expanded.
      */
    native public NodeIterator createNodeIterator(Node root);
    /**
      * Creates a TreeWalker object that you can use to traverse filtered lists of nodes or elements in a document.
      * @param root The root element or node to start traversing on.
      * @param whatToShow The type of nodes or elements to appear in the node list. For more information, see whatToShow.
      * @param filter A custom NodeFilter function to use.
      * @param entityReferenceExpansion A flag that specifies whether entity reference nodes are expanded.
      */
    native public TreeWalker createTreeWalker(Node root, double whatToShow, NodeFilter filter);
    /**
      * Creates a TreeWalker object that you can use to traverse filtered lists of nodes or elements in a document.
      * @param root The root element or node to start traversing on.
      * @param whatToShow The type of nodes or elements to appear in the node list. For more information, see whatToShow.
      * @param filter A custom NodeFilter function to use.
      * @param entityReferenceExpansion A flag that specifies whether entity reference nodes are expanded.
      */
    native public TreeWalker createTreeWalker(Node root, double whatToShow);
    /**
      * Creates a TreeWalker object that you can use to traverse filtered lists of nodes or elements in a document.
      * @param root The root element or node to start traversing on.
      * @param whatToShow The type of nodes or elements to appear in the node list. For more information, see whatToShow.
      * @param filter A custom NodeFilter function to use.
      * @param entityReferenceExpansion A flag that specifies whether entity reference nodes are expanded.
      */
    native public TreeWalker createTreeWalker(Node root);
    /**
      * Executes a command on the current document, current selection, or the given range.
      * @param commandId String that specifies the command to execute. This command can be any of the command identifiers that can be executed in script.
      * @param showUI Display the user interface, defaults to false.
      * @param value Value to assign.
      */
    native public java.lang.Boolean execCommand(java.lang.String commandId, java.lang.Boolean showUI);
    /**
      * Executes a command on the current document, current selection, or the given range.
      * @param commandId String that specifies the command to execute. This command can be any of the command identifiers that can be executed in script.
      * @param showUI Display the user interface, defaults to false.
      * @param value Value to assign.
      */
    native public java.lang.Boolean execCommand(java.lang.String commandId);
    /**
      * Opens a new window and loads a document specified by a given URL. Also, opens a new window that uses the url parameter and the name parameter to collect the output of the write method and the writeln method.
      * @param url Specifies a MIME type for the document.
      * @param name Specifies the name of the window. This name is used as the value for the TARGET attribute on a form or an anchor element.
      * @param features Contains a list of items separated by commas. Each item consists of an option and a value, separated by an equals sign (for example, "fullscreen=yes, toolbar=yes"). The following values are supported.
      * @param replace Specifies whether the existing entry for the document is replaced in the history list.
      */
    native public jsweet.util.union.Union<Document,Window> open(java.lang.String url, java.lang.String name, java.lang.String features);
    /**
      * Opens a new window and loads a document specified by a given URL. Also, opens a new window that uses the url parameter and the name parameter to collect the output of the write method and the writeln method.
      * @param url Specifies a MIME type for the document.
      * @param name Specifies the name of the window. This name is used as the value for the TARGET attribute on a form or an anchor element.
      * @param features Contains a list of items separated by commas. Each item consists of an option and a value, separated by an equals sign (for example, "fullscreen=yes, toolbar=yes"). The following values are supported.
      * @param replace Specifies whether the existing entry for the document is replaced in the history list.
      */
    native public jsweet.util.union.Union<Document,Window> open(java.lang.String url, java.lang.String name);
    /**
      * Opens a new window and loads a document specified by a given URL. Also, opens a new window that uses the url parameter and the name parameter to collect the output of the write method and the writeln method.
      * @param url Specifies a MIME type for the document.
      * @param name Specifies the name of the window. This name is used as the value for the TARGET attribute on a form or an anchor element.
      * @param features Contains a list of items separated by commas. Each item consists of an option and a value, separated by an equals sign (for example, "fullscreen=yes, toolbar=yes"). The following values are supported.
      * @param replace Specifies whether the existing entry for the document is replaced in the history list.
      */
    native public jsweet.util.union.Union<Document,Window> open(java.lang.String url);
    /**
      * Opens a new window and loads a document specified by a given URL. Also, opens a new window that uses the url parameter and the name parameter to collect the output of the write method and the writeln method.
      * @param url Specifies a MIME type for the document.
      * @param name Specifies the name of the window. This name is used as the value for the TARGET attribute on a form or an anchor element.
      * @param features Contains a list of items separated by commas. Each item consists of an option and a value, separated by an equals sign (for example, "fullscreen=yes, toolbar=yes"). The following values are supported.
      * @param replace Specifies whether the existing entry for the document is replaced in the history list.
      */
    native public jsweet.util.union.Union<Document,Window> open();
    native public void addEventListener(def.js.StringTypes.MSContentZoom type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSGestureChange type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSGestureDoubleTap type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSGestureEnd type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSGestureHold type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSGestureStart type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSGestureTap type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSInertiaStart type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSManipulationStateChanged type, java.util.function.Function<MSManipulationEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSPointerCancel type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSPointerDown type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSPointerEnter type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSPointerLeave type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSPointerMove type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSPointerOut type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSPointerOver type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.MSPointerUp type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.activate type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.beforeactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.beforedeactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.blur type, java.util.function.Function<FocusEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.canplay type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.canplaythrough type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.change type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.click type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.contextmenu type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.dblclick type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.deactivate type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.drag type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.dragend type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.dragenter type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.dragleave type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.dragover type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.dragstart type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.drop type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.durationchange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.emptied type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.ended type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.focus type, java.util.function.Function<FocusEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.fullscreenchange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.fullscreenerror type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.input type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.keydown type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.keypress type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.keyup type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.loadeddata type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.loadedmetadata type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.mousedown type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.mousemove type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.mouseout type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.mouseover type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.mouseup type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.mousewheel type, java.util.function.Function<MouseWheelEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.mssitemodejumplistitemremoved type, java.util.function.Function<MSSiteModeEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.msthumbnailclick type, java.util.function.Function<MSSiteModeEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pause type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.play type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.playing type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointercancel type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointerdown type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointerenter type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointerleave type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointerlockchange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointerlockerror type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointermove type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointerout type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointerover type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pointerup type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.ratechange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.readystatechange type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.reset type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.scroll type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.seeked type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.seeking type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.select type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.selectstart type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.stalled type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.stop type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.submit type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.suspend type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.timeupdate type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.touchcancel type, java.util.function.Function<TouchEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.touchend type, java.util.function.Function<TouchEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.touchmove type, java.util.function.Function<TouchEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.touchstart type, java.util.function.Function<TouchEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.volumechange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.waiting type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.webkitfullscreenchange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.webkitfullscreenerror type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.wheel type, java.util.function.Function<WheelEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

