package ts.client.completions;

public interface ICompletionEntryMatcherProvider {

	public static ICompletionEntryMatcherProvider LCS_PROVIDER = new ICompletionEntryMatcherProvider() {
		@Override
		public ICompletionEntryMatcher getMatcher() {
			return ICompletionEntryMatcher.LCS;
		}
	};

	public static ICompletionEntryMatcherProvider START_WITH_MATCHER_PROVIDER = new ICompletionEntryMatcherProvider() {
		@Override
		public ICompletionEntryMatcher getMatcher() {
			return ICompletionEntryMatcher.START_WITH_MATCHER;
		}
	};

	ICompletionEntryMatcher getMatcher();
}
