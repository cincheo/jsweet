package source.structural;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class JDKInheritance extends ArrayList<String> {

	public int getI() {
		return 7;
	}

	public static void main(String[] args) {

		JDKInheritance i = new JDKInheritance();
		i.add("a");
		assert i.contains("a") && i.size() == 1;
		assert i.getI() == 7;

	}

}
