package source.init;

import def.dom.Event;
import jsweet.lang.Interface;

public class InterfaceWithSuperInterface {
	public static void main(String[] args) {

		m(new LoginEvent() {
			{
				user = new LoggedUserDto();
			}
		});

	}

	static void m(LoginEvent event) {

	}
}

@Interface
class LoginEvent extends Event {
	LoggedUserDto user;
}

class LoggedUserDto {

}
