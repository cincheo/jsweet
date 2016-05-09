package source.overload;

public class WrongOverloadInInnerClass {

	interface I {
		
	}
	
	class Super {
		protected int start;
		protected int end;

		public Super() {
			start = 0;
			end = 0;
		}
	}

	class CharSequenceAdapter extends Super implements I {
		private char[] charArray;

		public CharSequenceAdapter(char[] charArray) {
			super();
		}

		public CharSequenceAdapter(char[] charArray, int start, int end) {
			super();
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
