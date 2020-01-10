package source.structural.publicfieldmethodclash;

import jsweet.lang.Name;

public class User2 {

	public String name;
	@Name("_login")
	public String login;


	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
