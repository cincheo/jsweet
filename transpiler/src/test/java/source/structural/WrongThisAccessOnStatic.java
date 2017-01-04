package source.structural;

public class WrongThisAccessOnStatic {

	  private static Logger log = new Logger();
	  private Logger log2 = new Logger();

	  private static void info() {
	  }
	  
	  void x() {
	    this.log.info("test");
	    log.info("test");
	    this.log2.info("test");
	    log2.info("test");
	    this.info();
	    info();
	  }	
}

class Logger {
	public void info(String s) {
	}
}

