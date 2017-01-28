package def.dom;

@jsweet.lang.Extends({NavigatorID.class,NavigatorOnLine.class,NavigatorContentUtils.class,NavigatorStorageUtils.class,NavigatorGeolocation.class,MSNavigatorDoNotTrack.class,MSFileSaver.class})
public class Navigator extends java.lang.Object {
    public java.lang.String appCodeName;
    public java.lang.String appMinorVersion;
    public java.lang.String browserLanguage;
    public double connectionSpeed;
    public java.lang.Boolean cookieEnabled;
    public java.lang.String cpuClass;
    public java.lang.String language;
    public double maxTouchPoints;
    public MSMimeTypesCollection mimeTypes;
    public java.lang.Boolean msManipulationViewsEnabled;
    public double msMaxTouchPoints;
    public java.lang.Boolean msPointerEnabled;
    public MSPluginsCollection plugins;
    public java.lang.Boolean pointerEnabled;
    public java.lang.String systemLanguage;
    public java.lang.String userLanguage;
    public java.lang.Boolean webdriver;
    native public Gamepad[] getGamepads();
    native public java.lang.Boolean javaEnabled();
    native public void msLaunchUri(java.lang.String uri, MSLaunchUriCallback successCallback, MSLaunchUriCallback noHandlerCallback);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static Navigator prototype;
    public Navigator(){}
    public java.lang.String appName;
    public java.lang.String appVersion;
    public java.lang.String platform;
    public java.lang.String product;
    public java.lang.String productSub;
    public java.lang.String userAgent;
    public java.lang.String vendor;
    public java.lang.String vendorSub;
    public java.lang.Boolean onLine;
    public Geolocation geolocation;
    native public java.lang.Boolean confirmSiteSpecificTrackingException(ConfirmSiteSpecificExceptionsInformation args);
    native public java.lang.Boolean confirmWebWideTrackingException(ExceptionInformation args);
    native public void removeSiteSpecificTrackingException(ExceptionInformation args);
    native public void removeWebWideTrackingException(ExceptionInformation args);
    native public void storeSiteSpecificTrackingException(StoreSiteSpecificExceptionsInformation args);
    native public void storeWebWideTrackingException(StoreExceptionsInformation args);
    native public java.lang.Boolean msSaveBlob(java.lang.Object blob, java.lang.String defaultName);
    native public java.lang.Boolean msSaveOrOpenBlob(java.lang.Object blob, java.lang.String defaultName);
    native public void msLaunchUri(java.lang.String uri, MSLaunchUriCallback successCallback);
    native public void msLaunchUri(java.lang.String uri);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public java.lang.Boolean msSaveBlob(java.lang.Object blob);
    native public java.lang.Boolean msSaveOrOpenBlob(java.lang.Object blob);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

