package def.dom;
public class MSHTMLWebViewElement extends HTMLElement {
    public Boolean canGoBack;
    public Boolean canGoForward;
    public Boolean containsFullScreenElement;
    public String documentTitle;
    public double height;
    public MSWebViewSettings settings;
    public String src;
    public double width;
    native public void addWebAllowedObject(String name, Object applicationObject);
    native public String buildLocalStreamUri(String contentIdentifier, String relativePath);
    native public MSWebViewAsyncOperation capturePreviewToBlobAsync();
    native public MSWebViewAsyncOperation captureSelectedContentToDataPackageAsync();
    native public DeferredPermissionRequest getDeferredPermissionRequestById(double id);
    native public DeferredPermissionRequest[] getDeferredPermissionRequests();
    native public void goBack();
    native public void goForward();
    native public MSWebViewAsyncOperation invokeScriptAsync(String scriptName, Object... args);
    native public void navigate(String uri);
    native public void navigateToLocalStreamUri(String source, Object streamResolver);
    native public void navigateToString(String contents);
    native public void navigateWithHttpRequestMessage(Object requestMessage);
    native public void refresh();
    native public void stop();
    public static MSHTMLWebViewElement prototype;
    public MSHTMLWebViewElement(){}
}

