package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class NamedNodeMap extends Iterable<Attr> {
    public double length;
    native public Attr getNamedItem(java.lang.String name);
    native public Attr getNamedItemNS(java.lang.String namespaceURI, java.lang.String localName);
    native public Attr item(double index);
    native public Attr removeNamedItem(java.lang.String name);
    native public Attr removeNamedItemNS(java.lang.String namespaceURI, java.lang.String localName);
    native public Attr setNamedItem(Attr arg);
    native public Attr setNamedItemNS(Attr arg);
    native public Attr $get(double index);
    public static NamedNodeMap prototype;
    public NamedNodeMap(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Attr> iterator();
}

