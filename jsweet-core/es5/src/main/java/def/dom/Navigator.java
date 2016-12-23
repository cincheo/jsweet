package def.dom;
@jsweet.lang.Extends({NavigatorID.class,NavigatorOnLine.class,NavigatorContentUtils.class,NavigatorStorageUtils.class,NavigatorGeolocation.class,MSNavigatorDoNotTrack.class,MSFileSaver.class})
public class Navigator extends Object {
    public String appCodeName;
    public String appMinorVersion;
    public String browserLanguage;
    public double connectionSpeed;
    public Boolean cookieEnabled;
    public String cpuClass;
    public String language;
    public double maxTouchPoints;
    public MSMimeTypesCollection mimeTypes;
    public Boolean msManipulationViewsEnabled;
    public double msMaxTouchPoints;
    public Boolean msPointerEnabled;
    public MSPluginsCollection plugins;
    public Boolean pointerEnabled;
    public String systemLanguage;
    public String userLanguage;
    public Boolean webdriver;
    native public Gamepad[] getGamepads();
    native public Boolean javaEnabled();
    native public void msLaunchUri(String uri, MSLaunchUriCallback successCallback, MSLaunchUriCallback noHandlerCallback);
    native public Boolean vibrate(double pattern);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static Navigator prototype;
    public Navigator(){}
    public String appName;
    public String appVersion;
    public String platform;
    public String product;
    public String productSub;
    public String userAgent;
    public String vendor;
    public String vendorSub;
    public Boolean onLine;
    public Geolocation geolocation;
    native public Boolean confirmSiteSpecificTrackingException(ConfirmSiteSpecificExceptionsInformation args);
    native public Boolean confirmWebWideTrackingException(ExceptionInformation args);
    native public void removeSiteSpecificTrackingException(ExceptionInformation args);
    native public void removeWebWideTrackingException(ExceptionInformation args);
    native public void storeSiteSpecificTrackingException(StoreSiteSpecificExceptionsInformation args);
    native public void storeWebWideTrackingException(StoreExceptionsInformation args);
    native public Boolean msSaveBlob(Object blob, String defaultName);
    native public Boolean msSaveOrOpenBlob(Object blob, String defaultName);
    native public void msLaunchUri(String uri, MSLaunchUriCallback successCallback);
    native public void msLaunchUri(String uri);
    native public void addEventListener(String type, EventListener listener);
    native public Boolean msSaveBlob(Object blob);
    native public Boolean msSaveOrOpenBlob(Object blob);
    native public Boolean vibrate(double[] pattern);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

