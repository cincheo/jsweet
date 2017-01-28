package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class MSNavigatorDoNotTrack extends def.js.Object {
    native public java.lang.Boolean confirmSiteSpecificTrackingException(ConfirmSiteSpecificExceptionsInformation args);
    native public java.lang.Boolean confirmWebWideTrackingException(ExceptionInformation args);
    native public void removeSiteSpecificTrackingException(ExceptionInformation args);
    native public void removeWebWideTrackingException(ExceptionInformation args);
    native public void storeSiteSpecificTrackingException(StoreSiteSpecificExceptionsInformation args);
    native public void storeWebWideTrackingException(StoreExceptionsInformation args);
}

