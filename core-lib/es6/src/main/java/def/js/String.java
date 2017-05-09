package def.js;

@jsweet.lang.SyntacticIterable
public class String extends Iterable<String> {
	/**
	 * Returns the character at the specified index.
	 * 
	 * @param pos
	 *            The zero-based index of the desired character.
	 */
	native public String charAt(int pos);

	/**
	 * Returns the Unicode value of the character at the specified location.
	 * 
	 * @param index
	 *            The zero-based index of the desired character. If there is no
	 *            character at the specified index, NaN is returned.
	 */
	native public int charCodeAt(int index);

	/**
	 * Returns a string that contains the concatenation of two or more strings.
	 * 
	 * @param strings
	 *            The strings to append to the end of the string.
	 	native public String concat(java.lang.String... strings);

	/**
	 * Returns a string that contains the concatenation of two or more strings.
	 * 
	 * @param strings
	 *            The strings to append to the end of the string.
	 */
	native public String concat(String... strings);

	/**
	 * Returns the position of the first occurrence of a substring.
	 * 
	 * @param searchString
	 *            The substring to search for in the string
	 * @param position
	 *            The index at which to begin searching the String object. If
	 *            omitted, search starts at the beginning of the string.
	 */
	native public int indexOf(java.lang.String searchString, int position);

	/**
	 * Returns the last occurrence of a substring in the string.
	 * 
	 * @param searchString
	 *            The substring to search for.
	 * @param position
	 *            The index at which to begin searching. If omitted, the search
	 *            begins at the end of the string.
	 */
	native public int lastIndexOf(java.lang.String searchString, int position);

	/**
	 * Returns the last occurrence of a substring in the string.
	 * 
	 * @param searchString
	 *            The substring to search for.
	 * @param position
	 *            The index at which to begin searching. If omitted, the search
	 *            begins at the end of the string.
	 */
	native public int lastIndexOf(String searchString, int position);

	/**
	 * Determines whether two strings are equivalent in the current locale.
	 * 
	 * @param that
	 *            String to compare to target string
	 */
	native public int localeCompare(java.lang.String that);

	/**
	 * Determines whether two strings are equivalent in the current locale.
	 * 
	 * @param that
	 *            String to compare to target string
	 */
	native public int localeCompare(String that);

	/**
	 * Matches a string with a regular expression, and returns an array
	 * containing the results of that search.
	 * 
	 * @param regexp
	 *            A variable name or string literal containing the regular
	 *            expression pattern and flags.
	 */
	native public RegExpMatchArray match(java.lang.String regexp);

	/**
	 * Matches a string with a regular expression, and returns an array
	 * containing the results of that search.
	 * 
	 * @param regexp
	 *            A variable name or string literal containing the regular
	 *            expression pattern and flags.
	 */
	native public RegExpMatchArray match(String regexp);

	/**
	 * Matches a string with a regular expression, and returns an array
	 * containing the results of that search.
	 * 
	 * @param regexp
	 *            A regular expression object that contains the regular
	 *            expression pattern and applicable flags.
	 */
	native public RegExpMatchArray match(RegExp regexp);

	/**
	 * Replaces text in a string, using a regular expression or search string.
	 * 
	 * @param searchValue
	 *            A String object or string literal that represents the regular
	 *            expression
	 * @param replaceValue
	 *            A String object or string literal containing the text to
	 *            replace for every successful match of rgExp in stringObj.
	 */
	native public String replace(java.lang.String searchValue, java.lang.String replaceValue);

	/**
	 * Replaces text in a string, using a regular expression or search string.
	 * 
	 * @param searchValue
	 *            A string that represents the regular expression.
	 * @param replaceValue
	 *            A string containing the text to replace for every successful
	 *            match of searchValue in this string.
	 */
	native public String replace(String searchValue, String replaceValue);

	/**
	 * Replaces text in a string, using a regular expression or search string.
	 * 
	 * @param searchValue
	 *            A String object or string literal that represents the regular
	 *            expression
	 * @param replaceValue
	 *            A function that returns the replacement text.
	 */
	native public <T> String replace(String searchValue,
			java.util.function.BiFunction<String, T, String> replaceValue);

    /**
     * Replaces text in a string, using a regular expression or search string.
     * 
     * @param searchValue
     *            A String object or string literal that represents the regular
     *            expression
     * @param replaceValue
     *            A function that returns the replacement text.
     */
    native public <T> String replace(String searchValue,
            java.util.function.Supplier<String> replaceValue);

	/**
	 * Replaces text in a string, using a regular expression or search string.
	 * 
	 * @param searchValue
	 *            A String object or string literal that represents the regular
	 *            expression
	 * @param replaceValue
	 *            A function that returns the replacement text.
	 */
	native public <T> String replace(java.lang.String searchValue,
			java.util.function.BiFunction<java.lang.String, T, java.lang.String> replaceValue);

    /**
     * Replaces text in a string, using a regular expression or search string.
     * 
     * @param searchValue
     *            A String object or string literal that represents the regular
     *            expression
     * @param replaceValue
     *            A function that returns the replacement text.
     */
    native public <T> String replace(java.lang.String searchValue,
            java.util.function.Supplier<java.lang.String> replaceValue);

	/**
	 * Replaces text in a string, using a regular expression or search string.
	 * 
	 * @param searchValue
	 *            A Regular Expression object containing the regular expression
	 *            pattern and applicable flags
	 * @param replaceValue
	 *            A String object or string literal containing the text to
	 *            replace for every successful match of rgExp in stringObj.
	 */
	native public String replace(RegExp searchValue, java.lang.String replaceValue);

	/**
	 * Replaces text in a string, using a regular expression or search string.
	 * 
	 * @param searchValue
	 *            A Regular Expression object containing the regular expression
	 *            pattern and applicable flags
	 * @param replaceValue
	 *            A String object or string literal containing the text to
	 *            replace for every successful match of rgExp in stringObj.
	 */
	native public String replace(RegExp searchValue, String replaceValue);

    /**
     * Replaces text in a string, using a regular expression or search string.
     * 
     * @param searchValue
     *            A Regular Expression object containing the regular expression
     *            pattern and applicable flags
     * @param replacer
     *            A function that returns the replacement text.
     */
    native public <T> String replace(RegExp searchValue, java.util.function.BiFunction<String, T, String> replacer);

    /**
     * Replaces text in a string, using a regular expression or search string.
     * 
     * @param searchValue
     *            A Regular Expression object containing the regular expression
     *            pattern and applicable flags
     * @param replacer
     *            A function that returns the replacement text.
     */
    native public <T> String replace(RegExp searchValue, java.util.function.Supplier<String> replacer);
    
	/**
	 * Finds the first substring match in a regular expression search.
	 * 
	 * @param regexp
	 *            The regular expression pattern and applicable flags.
	 */
	native public int search(java.lang.String regexp);

	/**
	 * Finds the first substring match in a regular expression search.
	 * 
	 * @param regexp
	 *            The regular expression pattern and applicable flags.
	 */
	native public int search(String regexp);

	/**
	 * Finds the first substring match in a regular expression search.
	 * 
	 * @param regexp
	 *            The regular expression pattern and applicable flags.
	 */
	native public int search(RegExp regexp);

	/**
	 * Returns a section of a string.
	 * 
	 * @param start
	 *            The index to the beginning of the specified portion of
	 *            stringObj.
	 * @param end
	 *            The index to the end of the specified portion of stringObj.
	 *            The substring includes the characters up to, but not
	 *            including, the character indicated by end. If this value is
	 *            not specified, the substring continues to the end of
	 *            stringObj.
	 */
	native public String slice(int start, int end);

	/**
	 * Split a string into substrings using the specified separator and return
	 * them as an array.
	 * 
	 * @param separator
	 *            A string that identifies character or characters to use in
	 *            separating the string. If omitted, a single-element array
	 *            containing the entire string is returned.
	 * @param limit
	 *            A value used to limit the number of elements returned in the
	 *            array.
	 */
	native public Array<String> split(java.lang.String separator, int limit);

	/**
	 * Split a string into substrings using the specified separator and return
	 * them as an array.
	 * 
	 * @param separator
	 *            A string that identifies character or characters to use in
	 *            separating the string. If omitted, a single-element array
	 *            containing the entire string is returned.
	 * @param limit
	 *            A value used to limit the number of elements returned in the
	 *            array.
	 */
	native public Array<String> split(String separator, int limit);
	
	/**
	 * Split a string into substrings using the specified separator and return
	 * them as an array.
	 * 
	 * @param separator
	 *            A Regular Express that identifies character or characters to
	 *            use in separating the string. If omitted, a single-element
	 *            array containing the entire string is returned.
	 * @param limit
	 *            A value used to limit the number of elements returned in the
	 *            array.
	 */
	native public Array<String> split(RegExp separator, int limit);

	/**
	 * Returns the substring at the specified location within a String object.
	 * 
	 * @param start
	 *            The zero-based index number indicating the beginning of the
	 *            substring.
	 * @param end
	 *            Zero-based index number indicating the end of the substring.
	 *            The substring includes the characters up to, but not
	 *            including, the character indicated by end. If end is omitted,
	 *            the characters from start through the end of the original
	 *            string are returned.
	 */
	native public String substring(int start, int end);

	/** Converts all the alphabetic characters in a string to lowercase. */
	native public String toLowerCase();

	/**
	 * Converts all alphabetic characters to lowercase, taking into account the
	 * host environment's current locale.
	 */
	native public String toLocaleLowerCase();

	/** Converts all the alphabetic characters in a string to uppercase. */
	native public String toUpperCase();

	/**
	 * Returns a string where all alphabetic characters have been converted to
	 * uppercase, taking into account the host environment's current locale.
	 */
	native public String toLocaleUpperCase();

	/**
	 * Removes the leading and trailing white space and line terminator
	 * characters from a string.
	 */
	native public String trim();

	/** Returns the length of a String object. */
	public final int length = 0;

	/**
	 * Gets a substring beginning at the specified location and having the
	 * specified length.
	 * 
	 * @param from
	 *            The starting position of the desired substring. The index of
	 *            the first character in the string is zero.
	 * @param length
	 *            The number of characters to include in the returned substring.
	 */
	native public String substr(int from, int length);

	/** Returns the primitive value of the specified object. */
	native public String valueOf();

	native public String $get(int index);

	public String(java.lang.Object value) {
	}

	native public static String $applyStatic(java.lang.Object value);

	public static String prototype;

	native public static String fromCharCode(int... codes);

	/**
	 * Returns a nonnegative integer Number less than 1114112 (0x110000) that is
	 * the code point value of the UTF-16 encoded code point starting at the
	 * string element at position pos in the String resulting from converting
	 * this object to a String. If there is no element at that position, the
	 * result is undefined. If a valid UTF-16 surrogate pair does not begin at
	 * pos, the result is the code unit at pos.
	 */
	native public int codePointAt(int pos);

	/**
	 * Returns true if searchString appears as a substring of the result of
	 * converting this object to a String, at one or more positions that are
	 * greater than or equal to position; otherwise, returns false.
	 * 
	 * @param searchString
	 *            search string
	 * @param position
	 *            If position is undefined, 0 is assumed, so as to search all of
	 *            the String.
	 */
	native public java.lang.Boolean contains(java.lang.String searchString, int position);

	/**
	 * Returns true if searchString appears as a substring of the result of
	 * converting this object to a String, at one or more positions that are
	 * greater than or equal to position; otherwise, returns false.
	 * 
	 * @param searchString
	 *            search string
	 * @param position
	 *            If position is undefined, 0 is assumed, so as to search all of
	 *            the String.
	 */
	native public java.lang.Boolean contains(String searchString, int position);

	/**
	 * Returns true if the sequence of elements of searchString converted to a
	 * String is the same as the corresponding elements of this object
	 * (converted to a String) starting at endPosition – length(this). Otherwise
	 * returns false.
	 */
	native public java.lang.Boolean endsWith(java.lang.String searchString, int endPosition);

	/**
	 * Returns true if the sequence of elements of searchString converted to a
	 * String is the same as the corresponding elements of this object
	 * (converted to a String) starting at endPosition – length(this). Otherwise
	 * returns false.
	 */
	native public java.lang.Boolean endsWith(String searchString, int endPosition);

	/**
	 * Returns the String value result of normalizing the string into the
	 * normalization form named by form as specified in Unicode Standard Annex
	 * #15, Unicode Normalization Forms.
	 * 
	 * @param form
	 *            Applicable values: "NFC", "NFD", "NFKC", or "NFKD", If not
	 *            specified default is "NFC"
	 */
	native public String normalize(java.lang.String form);

	/**
	 * Returns the String value result of normalizing the string into the
	 * normalization form named by form as specified in Unicode Standard Annex
	 * #15, Unicode Normalization Forms.
	 * 
	 * @param form
	 *            Applicable values: "NFC", "NFD", "NFKC", or "NFKD", If not
	 *            specified default is "NFC"
	 */
	native public String normalize(String form);

	/**
	 * Returns a String value that is made from count copies appended together.
	 * If count is 0, T is the empty String is returned.
	 * 
	 * @param count
	 *            number of copies to append
	 */
	native public String repeat(int count);

	/**
	 * Returns true if the sequence of elements of searchString converted to a
	 * String is the same as the corresponding elements of this object
	 * (converted to a String) starting at position. Otherwise returns false.
	 */
	native public java.lang.Boolean startsWith(java.lang.String searchString, int position);

	/**
	 * Returns true if the sequence of elements of searchString converted to a
	 * String is the same as the corresponding elements of this object
	 * (converted to a String) starting at position. Otherwise returns false.
	 */
	native public java.lang.Boolean startsWith(String searchString, int position);

	/**
	 * Returns an <a> HTML anchor element and sets the name attribute to the
	 * text value
	 * 
	 * @param name
	 */
	native public String anchor(java.lang.String name);

	/**
	 * Returns an <a> HTML anchor element and sets the name attribute to the
	 * text value
	 * 
	 * @param name
	 */
	native public String anchor(String name);
	
	/** Returns a <big> HTML element */
	native public String big();

	/** Returns a <blink> HTML element */
	native public String blink();

	/** Returns a <b> HTML element */
	native public String bold();

	/** Returns a <tt> HTML element */
	native public String fixed();

	/** Returns a <font> HTML element and sets the color attribute value */
	native public String fontcolor(java.lang.String color);

	/** Returns a <font> HTML element and sets the color attribute value */
	native public String fontcolor(String color);

	/** Returns a <font> HTML element and sets the size attribute value */
	native public String fontsize(int size);

	/** Returns a <font> HTML element and sets the size attribute value */
	native public String fontsize(java.lang.String size);

	/** Returns a <font> HTML element and sets the size attribute value */
	native public String fontsize(String size);

	/** Returns an <i> HTML element */
	native public String italics();

	/** Returns an <a> HTML element and sets the href attribute value */
	native public String link(java.lang.String url);

	/** Returns an <a> HTML element and sets the href attribute value */
	native public String link(String url);

	/** Returns a <small> HTML element */
	native public String small();

	/** Returns a <strike> HTML element */
	native public String strike();

	/** Returns a <sub> HTML element */
	native public String sub();

	/** Returns a <sup> HTML element */
	native public String sup();

	/**
	 * Determines whether two strings are equivalent in the current locale.
	 * 
	 * @param that
	 *            String to compare to target string
	 * @param locales
	 *            An array of locale strings that contain one or more language
	 *            or locale tags. If you include more than one locale string,
	 *            list them in descending order of priority so that the first
	 *            entry is the preferred locale. If you omit this parameter, the
	 *            default locale of the JavaScript runtime is used. This
	 *            parameter must conform to BCP 47 standards; see the
	 *            Intl.Collator object for details.
	 * @param options
	 *            An object that contains one or more properties that specify
	 *            comparison options. see the Intl.Collator object for details.
	 */
	native public int localeCompare(java.lang.String that, Array<String> locales,
			def.dom.intl.CollatorOptions options);

	/**
	 * Determines whether two strings are equivalent in the current locale.
	 * 
	 * @param that
	 *            String to compare to target string
	 * @param locales
	 *            An array of locale strings that contain one or more language
	 *            or locale tags. If you include more than one locale string,
	 *            list them in descending order of priority so that the first
	 *            entry is the preferred locale. If you omit this parameter, the
	 *            default locale of the JavaScript runtime is used. This
	 *            parameter must conform to BCP 47 standards; see the
	 *            Intl.Collator object for details.
	 * @param options
	 *            An object that contains one or more properties that specify
	 *            comparison options. see the Intl.Collator object for details.
	 */
	native public int localeCompare(String that, Array<String> locales,
			def.dom.intl.CollatorOptions options);
	
	/**
	 * Determines whether two strings are equivalent in the current locale.
	 * 
	 * @param that
	 *            String to compare to target string
	 * @param locale
	 *            Locale tag. If you omit this parameter, the default locale of
	 *            the JavaScript runtime is used. This parameter must conform to
	 *            BCP 47 standards; see the Intl.Collator object for details.
	 * @param options
	 *            An object that contains one or more properties that specify
	 *            comparison options. see the Intl.Collator object for details.
	 */
	native public int localeCompare(java.lang.String that, java.lang.String locale,
			def.dom.intl.CollatorOptions options);

	/**
	 * Determines whether two strings are equivalent in the current locale.
	 * 
	 * @param that
	 *            String to compare to target string
	 * @param locale
	 *            Locale tag. If you omit this parameter, the default locale of
	 *            the JavaScript runtime is used. This parameter must conform to
	 *            BCP 47 standards; see the Intl.Collator object for details.
	 * @param options
	 *            An object that contains one or more properties that specify
	 *            comparison options. see the Intl.Collator object for details.
	 */
	native public int localeCompare(String that, String locale, def.dom.intl.CollatorOptions options);

	/**
	 * Returns the position of the first occurrence of a substring.
	 * 
	 * @param searchString
	 *            The substring to search for in the string
	 * @param position
	 *            The index at which to begin searching the String object. If
	 *            omitted, search starts at the beginning of the string.
	 */
	native public int indexOf(java.lang.String searchString);

	/**
	 * Returns the position of the first occurrence of a substring.
	 * 
	 * @param searchString
	 *            The substring to search for in the string
	 * @param position
	 *            The index at which to begin searching the String object. If
	 *            omitted, search starts at the beginning of the string.
	 */
	native public int indexOf(String searchString);

	/**
	 * Returns the last occurrence of a substring in the string.
	 * 
	 * @param searchString
	 *            The substring to search for.
	 * @param position
	 *            The index at which to begin searching. If omitted, the search
	 *            begins at the end of the string.
	 */
	native public int lastIndexOf(java.lang.String searchString);

	/**
	 * Returns the last occurrence of a substring in the string.
	 * 
	 * @param searchString
	 *            The substring to search for.
	 * @param position
	 *            The index at which to begin searching. If omitted, the search
	 *            begins at the end of the string.
	 */
	native public int lastIndexOf(String searchString);

	/**
	 * Returns a section of a string.
	 * 
	 * @param start
	 *            The index to the beginning of the specified portion of
	 *            stringObj.
	 * @param end
	 *            The index to the end of the specified portion of stringObj.
	 *            The substring includes the characters up to, but not
	 *            including, the character indicated by end. If this value is
	 *            not specified, the substring continues to the end of
	 *            stringObj.
	 */
	native public String slice(int start);

	/**
	 * Returns a section of a string.
	 * 
	 * @param start
	 *            The index to the beginning of the specified portion of
	 *            stringObj.
	 * @param end
	 *            The index to the end of the specified portion of stringObj.
	 *            The substring includes the characters up to, but not
	 *            including, the character indicated by end. If this value is
	 *            not specified, the substring continues to the end of
	 *            stringObj.
	 */
	native public String slice();

	/**
	 * Split a string into substrings using the specified separator and return
	 * them as an array.
	 * 
	 * @param separator
	 *            A string that identifies character or characters to use in
	 *            separating the string. If omitted, a single-element array
	 *            containing the entire string is returned.
	 * @param limit
	 *            A value used to limit the number of elements returned in the
	 *            array.
	 */
	native public Array<String> split(java.lang.String separator);

	/**
	 * Split a string into substrings using the specified separator and return
	 * them as an array.
	 * 
	 * @param separator
	 *            A string that identifies character or characters to use in
	 *            separating the string. If omitted, a single-element array
	 *            containing the entire string is returned.
	 * @param limit
	 *            A value used to limit the number of elements returned in the
	 *            array.
	 */
	native public Array<String> split(String separator);
	
	/**
	 * Split a string into substrings using the specified separator and return
	 * them as an array.
	 * 
	 * @param separator
	 *            A Regular Express that identifies character or characters to
	 *            use in separating the string. If omitted, a single-element
	 *            array containing the entire string is returned.
	 * @param limit
	 *            A value used to limit the number of elements returned in the
	 *            array.
	 */
	native public Array<String> split(RegExp separator);

	/**
	 * Returns the substring at the specified location within a String object.
	 * 
	 * @param start
	 *            The zero-based index number indicating the beginning of the
	 *            substring.
	 * @param end
	 *            Zero-based index number indicating the end of the substring.
	 *            The substring includes the characters up to, but not
	 *            including, the character indicated by end. If end is omitted,
	 *            the characters from start through the end of the original
	 *            string are returned.
	 */
	native public String substring(int start);

	/**
	 * Gets a substring beginning at the specified location and having the
	 * specified length.
	 * 
	 * @param from
	 *            The starting position of the desired substring. The index of
	 *            the first character in the string is zero.
	 * @param length
	 *            The number of characters to include in the returned substring.
	 */
	native public String substr(int from);

	public String() {
	}

	native public static String $applyStatic();

	/**
	 * Returns true if searchString appears as a substring of the result of
	 * converting this object to a String, at one or more positions that are
	 * greater than or equal to position; otherwise, returns false.
	 * 
	 * @param searchString
	 *            search string
	 * @param position
	 *            If position is undefined, 0 is assumed, so as to search all of
	 *            the String.
	 */
	native public java.lang.Boolean contains(java.lang.String searchString);

	/**
	 * Returns true if searchString appears as a substring of the result of
	 * converting this object to a String, at one or more positions that are
	 * greater than or equal to position; otherwise, returns false.
	 * 
	 * @param searchString
	 *            search string
	 * @param position
	 *            If position is undefined, 0 is assumed, so as to search all of
	 *            the String.
	 */
	native public java.lang.Boolean contains(String searchString);

	/**
	 * Returns true if the sequence of elements of searchString converted to a
	 * String is the same as the corresponding elements of this object
	 * (converted to a String) starting at endPosition – length(this). Otherwise
	 * returns false.
	 */
	native public java.lang.Boolean endsWith(java.lang.String searchString);

	/**
	 * Returns true if the sequence of elements of searchString converted to a
	 * String is the same as the corresponding elements of this object
	 * (converted to a String) starting at endPosition – length(this). Otherwise
	 * returns false.
	 */
	native public java.lang.Boolean endsWith(String searchString);

	/**
	 * Returns the String value result of normalizing the string into the
	 * normalization form named by form as specified in Unicode Standard Annex
	 * #15, Unicode Normalization Forms.
	 * 
	 * @param form
	 *            Applicable values: "NFC", "NFD", "NFKC", or "NFKD", If not
	 *            specified default is "NFC"
	 */
	native public String normalize();

	/**
	 * Returns true if the sequence of elements of searchString converted to a
	 * String is the same as the corresponding elements of this object
	 * (converted to a String) starting at position. Otherwise returns false.
	 */
	native public java.lang.Boolean startsWith(java.lang.String searchString);

	/**
	 * Returns true if the sequence of elements of searchString converted to a
	 * String is the same as the corresponding elements of this object
	 * (converted to a String) starting at position. Otherwise returns false.
	 */
	native public java.lang.Boolean startsWith(String searchString);

	/**
	 * Determines whether two strings are equivalent in the current locale.
	 * 
	 * @param that
	 *            String to compare to target string
	 * @param locales
	 *            An array of locale strings that contain one or more language
	 *            or locale tags. If you include more than one locale string,
	 *            list them in descending order of priority so that the first
	 *            entry is the preferred locale. If you omit this parameter, the
	 *            default locale of the JavaScript runtime is used. This
	 *            parameter must conform to BCP 47 standards; see the
	 *            Intl.Collator object for details.
	 * @param options
	 *            An object that contains one or more properties that specify
	 *            comparison options. see the Intl.Collator object for details.
	 */
	native public int localeCompare(String that, Array<String> locales);

	/**
	 * Determines whether two strings are equivalent in the current locale.
	 * 
	 * @param that
	 *            String to compare to target string
	 * @param locale
	 *            Locale tag. If you omit this parameter, the default locale of
	 *            the JavaScript runtime is used. This parameter must conform to
	 *            BCP 47 standards; see the Intl.Collator object for details.
	 * @param options
	 *            An object that contains one or more properties that specify
	 *            comparison options. see the Intl.Collator object for details.
	 */
	native public int localeCompare(java.lang.String that, java.lang.String locale);

	/**
	 * Determines whether two strings are equivalent in the current locale.
	 * 
	 * @param that
	 *            String to compare to target string
	 * @param locale
	 *            Locale tag. If you omit this parameter, the default locale of
	 *            the JavaScript runtime is used. This parameter must conform to
	 *            BCP 47 standards; see the Intl.Collator object for details.
	 * @param options
	 *            An object that contains one or more properties that specify
	 *            comparison options. see the Intl.Collator object for details.
	 */
	native public int localeCompare(String that, String locale);

	/** From Iterable, to allow foreach loop (do not use directly). */
	@jsweet.lang.Erased
	native public java.util.Iterator<String> iterator();
}
