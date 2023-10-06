package ts.cmd;

import java.util.List;

import ts.utils.StringUtils;

public abstract class AbstractOptions implements IOptions{

	protected void fillOption(String name, Boolean value, List<String> args) {
		if (value != null && value) {
			args.add(name);
		}
	}

	protected void fillOption(String name, String value, List<String> args) {
		if (!StringUtils.isEmpty(value)) {
			args.add(name);
			args.add(value);
		}
	}

}
