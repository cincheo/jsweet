package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class PluginArray extends Iterable<Plugin> {
    public double length;
    native public Plugin item(double index);
    native public Plugin namedItem(java.lang.String name);
    native public void refresh(java.lang.Boolean reload);
    native public Plugin $get(double index);
    public static PluginArray prototype;
    public PluginArray(){}
    native public void refresh();
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Plugin> iterator();
}

