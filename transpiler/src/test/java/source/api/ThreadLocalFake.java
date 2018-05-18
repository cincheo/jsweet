package source.api;

import static jsweet.util.Lang.$export;

public class ThreadLocalFake {

	public static class ImportantClass {
		private String message;

		public ImportantClass(String string) {
			this.message = string;
		}

		@Override
		public String toString() {
			return message;
		}
	}

	public static class ImportantClassThreadLocal extends ThreadLocal<ImportantClass> {
		protected ImportantClass initialValue() {
			return new ImportantClass("coucou");
		}
	}

	public static void main(String[] args) {
		System.out.println("starting");
		ImportantClassThreadLocal test = new ImportantClassThreadLocal();

		ImportantClass c = test.get();
		$export("out", "" + c);
	}
}
