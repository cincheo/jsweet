package def.dom;

import def.js.Object;

public class MediaQueryList extends def.js.Object {
    public java.lang.Boolean matches;
    public java.lang.String media;
    native public void addListener(MediaQueryListListener listener);
    native public void removeListener(MediaQueryListListener listener);
    public static MediaQueryList prototype;
    public MediaQueryList(){}
}

