package def.dom;
public class NamedNodeMap extends def.js.Object implements Iterable<Attr> {
    public double length;
    native public Attr getNamedItem(String name);
    native public Attr getNamedItemNS(String namespaceURI, String localName);
    native public Attr item(double index);
    native public Attr removeNamedItem(String name);
    native public Attr removeNamedItemNS(String namespaceURI, String localName);
    native public Attr setNamedItem(Attr arg);
    native public Attr setNamedItemNS(Attr arg);
    native public Attr $get(double index);
    public static NamedNodeMap prototype;
    public NamedNodeMap(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Attr> iterator();
}

