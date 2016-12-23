package source.overload;

public class OverloadInInnerClass {

	class CharSequenceAdapter {
		private char[] charArray;
		private int start;
		private int end;

		public CharSequenceAdapter(char[] charArray) {
			this(charArray, 0, charArray.length);
		}

		public CharSequenceAdapter(char[] charArray, int start, int end) {
			this.charArray = charArray;
			this.start = start;
			this.end = end;
		}

		public char charAt(int index) {
			return charArray[index + start];
		}

		public int length() {
			return end - start;
		}

		public CharSequenceAdapter subSequence(int start, int end) {
			return new CharSequenceAdapter(charArray, this.start + start, this.start + end);
		}
	}
	
}
