package def.js;
public class RegExp extends def.js.Object {
    /** 
      * Executes a search on a string using a regular expression pattern, and returns an array containing the results of that search.
      * @param string The String object or string literal on which to perform the search.
      */
    native public RegExpExecArray exec(java.lang.String string);
    /** 
      * Returns a Boolean value that indicates whether or not a pattern exists in a searched string.
      * @param string String on which to perform the search.
      */
    native public java.lang.Boolean test(java.lang.String string);
    /** Returns a copy of the text of the regular expression pattern. Read-only. The regExp argument is a Regular expression object. It can be a variable name or a literal. */
    public java.lang.String source;
    /** Returns a Boolean value indicating the state of the global flag (g) used with a regular expression. Default is false. Read-only. */
    public java.lang.Boolean global;
    /** Returns a Boolean value indicating the state of the ignoreCase flag (i) used with a regular expression. Default is false. Read-only. */
    public java.lang.Boolean ignoreCase;
    /** Returns a Boolean value indicating the state of the multiline flag (m) used with a regular expression. Default is false. Read-only. */
    public java.lang.Boolean multiline;
    public double lastIndex;
    native public RegExp compile();
    public RegExp(java.lang.String pattern, java.lang.String flags){}
    native public static RegExp applyStatic(java.lang.String pattern, java.lang.String flags);
    public static RegExp prototype;
    public static java.lang.String $1;
    public static java.lang.String $2;
    public static java.lang.String $3;
    public static java.lang.String $4;
    public static java.lang.String $5;
    public static java.lang.String $6;
    public static java.lang.String $7;
    public static java.lang.String $8;
    public static java.lang.String $9;
    public static java.lang.String lastMatch;
    /** 
      * Matches a string with a regular expression, and returns an array containing the results of 
      * that search.
      * @param string A string to search within.
      */
    native public java.lang.String[] match(java.lang.String string);
    /**
      * Replaces text in a string, using a regular expression.
      * @param searchValue A String object or string literal that represents the regular expression
      * @param replaceValue A String object or string literal containing the text to replace for every 
      * successful match of rgExp in stringObj.
      */
    native public java.lang.String replace(java.lang.String string, java.lang.String replaceValue);
    native public double search(java.lang.String string);
    /**
      * Returns an Array object into which substrings of the result of converting string to a String 
      * have been stored. The substrings are determined by searching from left to right for matches 
      * of the this value regular expression; these occurrences are not part of any substring in the 
      * returned array, but serve to divide up the String value.
      *
      * If the regular expression that contains capturing parentheses, then each time separator is 
      * matched the results (including any undefined results) of the capturing parentheses are spliced.
      * @param string string value to split
      * @param limit if not undefined, the output array is truncated so that it contains no more 
      * than limit elements.
      */
    native public java.lang.String[] split(java.lang.String string, double limit);
    /**
      * Returns a string indicating the flags of the regular expression in question. This field is read-only.
      * The characters in this string are sequenced and concatenated in the following order:
      *
      *    - "g" for global
      *    - "i" for ignoreCase
      *    - "m" for multiline
      *    - "u" for unicode
      *    - "y" for sticky
      *
      * If no flags are set, the value is the empty string.
      */
    public java.lang.String flags;
    /** 
      * Returns a Boolean value indicating the state of the sticky flag (y) used with a regular 
      * expression. Default is false. Read-only. 
      */
    public java.lang.Boolean sticky;
    /** 
      * Returns a Boolean value indicating the state of the Unicode flag (u) used with a regular 
      * expression. Default is false. Read-only. 
      */
    public java.lang.Boolean unicode;
    public RegExp(java.lang.String pattern){}
    native public static RegExp applyStatic(java.lang.String pattern);
    /**
      * Returns an Array object into which substrings of the result of converting string to a String 
      * have been stored. The substrings are determined by searching from left to right for matches 
      * of the this value regular expression; these occurrences are not part of any substring in the 
      * returned array, but serve to divide up the String value.
      *
      * If the regular expression that contains capturing parentheses, then each time separator is 
      * matched the results (including any undefined results) of the capturing parentheses are spliced.
      * @param string string value to split
      * @param limit if not undefined, the output array is truncated so that it contains no more 
      * than limit elements.
      */
    native public java.lang.String[] split(java.lang.String string);
    protected RegExp(){}
}

