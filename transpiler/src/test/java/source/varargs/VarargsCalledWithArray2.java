package source.varargs;

public class VarargsCalledWithArray2 {

	public static String displayTemplateMessage(String aMessageId, String... someArgs) {
		return someArgs[0];
	}

	public static void main(String[] args) {
		System.out.println("v1".equals(displayTemplateMessage("messageLibre", new String[] { "v1" })));
		System.out.println("v1".equals(displayTemplateMessage("messageLibre", "v1")));
		assert "v1".equals(displayTemplateMessage("messageLibre", new String[] { "v1" }));
		assert "v1".equals(displayTemplateMessage("messageLibre", "v1"));
	}

}
