package def.dom;

import def.js.Object;

public class Location extends def.js.Object {
    public java.lang.String hash;
    public java.lang.String host;
    public java.lang.String hostname;
    public java.lang.String href;
    public java.lang.String origin;
    public java.lang.String pathname;
    public java.lang.String port;
    public java.lang.String protocol;
    public java.lang.String search;
    native public void assign(java.lang.String url);
    native public void reload(java.lang.Boolean forcedReload);
    native public void replace(java.lang.String url);
    native public java.lang.String toString();
    public static Location prototype;
    public Location(){}
    native public void reload();
}

