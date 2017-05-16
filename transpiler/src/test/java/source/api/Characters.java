package source.api;

public class Characters {

    public static void main(String[] args) {
	Character c = 'C';
	assert Character.isAlphabetic(c.charValue());
	assert !Character.isDigit(c);
	assert Character.isLetter(c);
	assert Character.isLetterOrDigit(c);
	assert Character.isLetterOrDigit(c.charValue());
	assert Character.isUpperCase(c);
	assert !Character.isLowerCase(c);
	assert Character.isLowerCase(Character.toLowerCase(c));
	assert 'c' == Character.toLowerCase(c);
	assert c == Character.toUpperCase(Character.toLowerCase(c));
	char c2 = 'C';
	assert c2 == Character.toUpperCase(Character.toLowerCase(c2));
    }

}
