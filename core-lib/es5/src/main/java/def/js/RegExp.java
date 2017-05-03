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
    public final java.lang.String source=null;
    /** Returns a Boolean value indicating the state of the global flag (g) used with a regular expression. Default is false. Read-only. */
    public final java.lang.Boolean global=null;
    /** Returns a Boolean value indicating the state of the ignoreCase flag (i) used with a regular expression. Default is false. Read-only. */
    public final java.lang.Boolean ignoreCase=null;
    /** Returns a Boolean value indicating the state of the multiline flag (m) used with a regular expression. Default is false. Read-only. */
    public final java.lang.Boolean multiline=null;
    public double lastIndex;
    native public RegExp compile();
    public RegExp(java.lang.String pattern, java.lang.String flags){}
    public RegExp(String pattern, String flags){}
    native public static RegExp applyStatic(java.lang.String pattern, java.lang.String flags);
    public static final RegExp prototype=null;
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
    public RegExp(java.lang.String pattern){}
    native public static RegExp applyStatic(java.lang.String pattern);
    protected RegExp(){}
}

