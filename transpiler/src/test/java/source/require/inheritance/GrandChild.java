package source.require.inheritance;

import static jsweet.util.Lang.$export;

public class GrandChild extends Child
{
	public static void main(String[] args)
	{
		$export("mainInvoked", parentStr);
	}
}
