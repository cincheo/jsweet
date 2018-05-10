package def.dom;

@jsweet.lang.Interface
public abstract class NodeList {
	public int length;

	native public Node item(int index);

	native public Node $get(int index);

	public static NodeList prototype;
}
