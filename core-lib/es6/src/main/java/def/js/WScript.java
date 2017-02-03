package def.js;

import def.dom.TextStreamReader;
import def.dom.TextStreamWriter;

/** This is an automatically generated object type (see the source definition). */
public class WScript extends def.js.Object {
    /**
    * Outputs text to either a message box (under WScript.exe) or the command console window followed by
    * a newline (under CScript.exe).
    */
    native public static void Echo(java.lang.Object s);
    /**
     * Exposes the write-only error output stream for the current script.
     * Can be accessed only while using CScript.exe.
     */
    public static TextStreamWriter StdErr;
    /**
     * Exposes the write-only output stream for the current script.
     * Can be accessed only while using CScript.exe.
     */
    public static TextStreamWriter StdOut;
    public static Arguments Arguments;
    /**
     *  The full path of the currently running script.
     */
    public static java.lang.String ScriptFullName;
    /**
     * Forces the script to stop immediately, with an optional exit code.
     */
    native public static double Quit(double exitCode);
    /**
     * The Windows Script Host build version number.
     */
    public static double BuildVersion;
    /**
     * Fully qualified path of the host executable.
     */
    public static java.lang.String FullName;
    /**
     * Gets/sets the script mode - interactive(true) or batch(false).
     */
    public static java.lang.Boolean Interactive;
    /**
     * The name of the host executable (WScript.exe or CScript.exe).
     */
    public static java.lang.String Name;
    /**
     * Path of the directory containing the host executable.
     */
    public static java.lang.String Path;
    /**
     * The filename of the currently running script.
     */
    public static java.lang.String ScriptName;
    /**
     * Exposes the read-only input stream for the current script.
     * Can be accessed only while using CScript.exe.
     */
    public static TextStreamReader StdIn;
    /**
     * Windows Script Host version
     */
    public static java.lang.String Version;
    /**
     * Connects a COM object's event sources to functions named with a given prefix, in the form prefix_event.
     */
    native public static void ConnectObject(java.lang.Object objEventSource, java.lang.String strPrefix);
    /**
     * Creates a COM object.
     * @param strProgiID
     * @param strPrefix Function names in the form prefix_event will be bound to this object's COM events.
     */
    native public static java.lang.Object CreateObject(java.lang.String strProgID, java.lang.String strPrefix);
    /**
     * Disconnects a COM object from its event sources.
     */
    native public static void DisconnectObject(java.lang.Object obj);
    /**
     * Retrieves an existing object with the specified ProgID from memory, or creates a new one from a file.
     * @param strPathname Fully qualified path to the file containing the object persisted to disk.
     *                       For objects in memory, pass a zero-length string.
     * @param strProgID
     * @param strPrefix Function names in the form prefix_event will be bound to this object's COM events.
     */
    native public static java.lang.Object GetObject(java.lang.String strPathname, java.lang.String strProgID, java.lang.String strPrefix);
    /**
     * Suspends script execution for a specified length of time, then continues execution.
     * @param intTime Interval (in milliseconds) to suspend script execution.
     */
    native public static void Sleep(double intTime);
    /** This is an automatically generated object type (see the source definition). */
    @jsweet.lang.ObjectType
    public static class Arguments extends def.js.Object {
        public double length;
        native public java.lang.String Item(double n);
    }
    /**
     * Forces the script to stop immediately, with an optional exit code.
     */
    native public static double Quit();
    /**
     * Creates a COM object.
     * @param strProgiID
     * @param strPrefix Function names in the form prefix_event will be bound to this object's COM events.
     */
    native public static java.lang.Object CreateObject(java.lang.String strProgID);
    /**
     * Retrieves an existing object with the specified ProgID from memory, or creates a new one from a file.
     * @param strPathname Fully qualified path to the file containing the object persisted to disk.
     *                       For objects in memory, pass a zero-length string.
     * @param strProgID
     * @param strPrefix Function names in the form prefix_event will be bound to this object's COM events.
     */
    native public static java.lang.Object GetObject(java.lang.String strPathname, java.lang.String strProgID);
    /**
     * Retrieves an existing object with the specified ProgID from memory, or creates a new one from a file.
     * @param strPathname Fully qualified path to the file containing the object persisted to disk.
     *                       For objects in memory, pass a zero-length string.
     * @param strProgID
     * @param strPrefix Function names in the form prefix_event will be bound to this object's COM events.
     */
    native public static java.lang.Object GetObject(java.lang.String strPathname);
}

