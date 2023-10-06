package ts.nodejs;

public class TraceNodejsProcess extends NodejsProcessAdapter {

	public static final INodejsProcessListener INSTANCE = new TraceNodejsProcess();

	@Override
	public void onMessage(INodejsProcess process, String response) {
		System.out.println(response);
	}

	@Override
	public void onError(INodejsProcess process, String response) {
		System.err.println(response);
	}

}
