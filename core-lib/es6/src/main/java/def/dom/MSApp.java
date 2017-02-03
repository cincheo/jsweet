package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class MSApp extends def.js.Object {
    native public static MSAppAsyncOperation clearTemporaryWebDataAsync();
    native public static Blob createBlobFromRandomAccessStream(java.lang.String type, java.lang.Object seeker);
    native public static java.lang.Object createDataPackage(java.lang.Object object);
    native public static java.lang.Object createDataPackageFromSelection();
    native public static File createFileFromStorageFile(java.lang.Object storageFile);
    native public static MSStream createStreamFromInputStream(java.lang.String type, java.lang.Object inputStream);
    native public static void execAsyncAtPriority(MSExecAtPriorityFunctionCallback asynchronousCallback, java.lang.String priority, java.lang.Object... args);
    native public static java.lang.Object execAtPriority(MSExecAtPriorityFunctionCallback synchronousCallback, java.lang.String priority, java.lang.Object... args);
    native public static java.lang.String getCurrentPriority();
    native public static java.lang.Object getHtmlPrintDocumentSourceAsync(java.lang.Object htmlDoc);
    native public static java.lang.Object getViewId(java.lang.Object view);
    native public static java.lang.Boolean isTaskScheduledAtPriorityOrHigher(java.lang.String priority);
    native public static void pageHandlesAllApplicationActivations(java.lang.Boolean enabled);
    native public static void suppressSubdownloadCredentialPrompts(java.lang.Boolean suppress);
    native public static void terminateApp(java.lang.Object exceptionObject);
    public static java.lang.String CURRENT;
    public static java.lang.String HIGH;
    public static java.lang.String IDLE;
    public static java.lang.String NORMAL;
}

