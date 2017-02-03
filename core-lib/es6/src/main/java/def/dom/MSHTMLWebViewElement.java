package def.dom;

public class MSHTMLWebViewElement extends HTMLElement {
    public java.lang.Boolean canGoBack;
    public java.lang.Boolean canGoForward;
    public java.lang.Boolean containsFullScreenElement;
    public java.lang.String documentTitle;
    public double height;
    public MSWebViewSettings settings;
    public java.lang.String src;
    public double width;
    native public void addWebAllowedObject(java.lang.String name, java.lang.Object applicationObject);
    native public java.lang.String buildLocalStreamUri(java.lang.String contentIdentifier, java.lang.String relativePath);
    native public MSWebViewAsyncOperation capturePreviewToBlobAsync();
    native public MSWebViewAsyncOperation captureSelectedContentToDataPackageAsync();
    native public DeferredPermissionRequest getDeferredPermissionRequestById(double id);
    native public DeferredPermissionRequest[] getDeferredPermissionRequests();
    native public void goBack();
    native public void goForward();
    native public MSWebViewAsyncOperation invokeScriptAsync(java.lang.String scriptName, java.lang.Object... args);
    native public void navigate(java.lang.String uri);
    native public void navigateToLocalStreamUri(java.lang.String source, java.lang.Object streamResolver);
    native public void navigateToString(java.lang.String contents);
    native public void navigateWithHttpRequestMessage(java.lang.Object requestMessage);
    native public void refresh();
    native public void stop();
    public static MSHTMLWebViewElement prototype;
    public MSHTMLWebViewElement(){}
}

