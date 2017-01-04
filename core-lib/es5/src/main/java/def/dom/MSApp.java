package def.dom;
@jsweet.lang.Interface
public abstract class MSApp extends def.js.Object {
    native public static MSAppAsyncOperation clearTemporaryWebDataAsync();
    native public static Blob createBlobFromRandomAccessStream(String type, Object seeker);
    native public static Object createDataPackage(Object object);
    native public static Object createDataPackageFromSelection();
    native public static File createFileFromStorageFile(Object storageFile);
    native public static MSStream createStreamFromInputStream(String type, Object inputStream);
    native public static void execAsyncAtPriority(MSExecAtPriorityFunctionCallback asynchronousCallback, String priority, Object... args);
    native public static Object execAtPriority(MSExecAtPriorityFunctionCallback synchronousCallback, String priority, Object... args);
    native public static String getCurrentPriority();
    native public static Object getHtmlPrintDocumentSourceAsync(Object htmlDoc);
    native public static Object getViewId(Object view);
    native public static Boolean isTaskScheduledAtPriorityOrHigher(String priority);
    native public static void pageHandlesAllApplicationActivations(Boolean enabled);
    native public static void suppressSubdownloadCredentialPrompts(Boolean suppress);
    native public static void terminateApp(Object exceptionObject);
    public static String CURRENT;
    public static String HIGH;
    public static String IDLE;
    public static String NORMAL;
}

