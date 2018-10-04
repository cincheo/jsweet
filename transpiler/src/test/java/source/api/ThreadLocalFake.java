package source.api;

import static jsweet.util.Lang.$export;

public class ThreadLocalFake {

	public static class ImportantClass {
		private String message;

		public ImportantClass(String string) {
			this.message = string;
		}
		
		public void setMessage(String message) {
			this.message = message;
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
		
		test.get().setMessage("nakach");
		$export("out2", "" + test.get());
		
		$export("out3", "" + test.get());
	}
}
