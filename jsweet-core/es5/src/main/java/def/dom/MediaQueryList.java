package def.dom;
public class MediaQueryList extends def.js.Object {
    public Boolean matches;
    public String media;
    native public void addListener(MediaQueryListListener listener);
    native public void removeListener(MediaQueryListListener listener);
    public static MediaQueryList prototype;
    public MediaQueryList(){}
}

