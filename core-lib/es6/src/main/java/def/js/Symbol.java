package def.js;
public class Symbol extends def.js.Object {
    /** Returns a string representation of an object. */
    native public java.lang.String toString();
    /** Returns the primitive value of the specified object. */
    native public java.lang.Object valueOf();
    native public java.lang.String $get(Symbol toStringTag);
    /** 
      * A reference to the prototype. 
      */
    public static Symbol prototype;
    /**
      * Returns a new unique Symbol value.
      * @param  description Description of the new Symbol object.
      */
    native public static String applyStatic(java.lang.String description);
    /**
      * Returns a Symbol object from the global symbol registry matching the given key if found. 
      * Otherwise, returns a new symbol with this key.
      * @param key key to search for.
      */
    @jsweet.lang.Name("for")
    native public static String For(java.lang.String key);
    /**
      * Returns a key from the global symbol registry matching the given Symbol if found. 
      * Otherwise, returns a undefined.
      * @param sym Symbol to find the key for.
      */
    native public static java.lang.String keyFor(String sym);
    /** 
      * A method that determines if a constructor object recognizes an object as one of the 
      * constructor’s instances. Called by the semantics of the instanceof operator. 
      */
    public static String hasInstance;
    /** 
      * A Boolean value that if true indicates that an object should flatten to its array elements
      * by Array.prototype.concat.
      */
    public static String isConcatSpreadable;
    /** 
      * A method that returns the default iterator for an object. Called by the semantics of the 
      * for-of statement.
      */
    public static String iterator;
    /**
      * A regular expression method that matches the regular expression against a string. Called 
      * by the String.prototype.match method. 
      */
    public static String match;
    /** 
      * A regular expression method that replaces matched substrings of a string. Called by the 
      * String.prototype.replace method.
      */
    public static String replace;
    /**
      * A regular expression method that returns the index within a string that matches the 
      * regular expression. Called by the String.prototype.search method.
      */
    public static String search;
    /** 
      * A function valued property that is the constructor function that is used to create 
      * derived objects.
      */
    public static String species;
    /**
      * A regular expression method that splits a string at the indices that match the regular 
      * expression. Called by the String.prototype.split method.
      */
    public static String split;
    /** 
      * A method that converts an object to a corresponding primitive value.Called by the ToPrimitive
      * abstract operation.
      */
    public static String toPrimitive;
    /** 
      * A String value that is used in the creation of the default string description of an object.
      * Called by the built-in method Object.prototype.toString.
      */
    public static String toStringTag;
    /** 
      * An Object whose own property names are property names that are excluded from the with 
      * environment bindings of the associated objects.
      */
    public static String unscopables;
    /**
      * Returns a new unique Symbol value.
      * @param  description Description of the new Symbol object.
      */
    native public static String applyStatic();
    /**
      * Returns a new unique Symbol value.
      * @param  description Description of the new Symbol object.
      */
    native public static String applyStatic(double description);
}

