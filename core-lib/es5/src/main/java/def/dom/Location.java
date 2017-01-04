package def.dom;
public class Location extends def.js.Object {
    public String hash;
    public String host;
    public String hostname;
    public String href;
    public String origin;
    public String pathname;
    public String port;
    public String protocol;
    public String search;
    native public void assign(String url);
    native public void reload(Boolean forcedReload);
    native public void replace(String url);
    native public String toString();
    public static Location prototype;
    public Location(){}
    native public void reload();
}

