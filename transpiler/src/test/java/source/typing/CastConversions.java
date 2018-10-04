package source.typing;

public class CastConversions {

	static void print(int value) {
		System.out.println(value);
	}
	
	public static void main(String[] args) {
		char key = 'c';
		print((int)key);
	}
}
