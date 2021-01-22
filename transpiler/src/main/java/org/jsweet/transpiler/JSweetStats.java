package org.jsweet.transpiler;

/**
 * A data object to gather transpilation statistics when the stats option is on.
 * 
 * @author Renaud Pawlak
 */
public class JSweetStats {

	/**
	 * Number of files processed in the source input.
	 */
	public int javaFileCount;
	
	/**
	 * Number of classes processed in the source input.
	 */
	public int javaClassCount;

	/**
	 * Number of interfaces processed in the source input.
	 */
	public int javaInterfaceCount;

	/**
	 * Number of enums processed in the source input.
	 */
	public int javaEnumCount;
	
	/**
	 * Number of erased classes in the source input.
	 */
	public int erasedJavaClassCount;

	/**
	 * Number of methods processed in the source input.
	 */
	public int javaMethodCount;

	/**
	 * Number of erased methods in the source input.
	 */
	public int erasedJavaMethodCount;
	
	/**
	 * Number of Java lines of code (input).
	 */
	public int javaLineCount;

	/**
	 * Number of generated TypeScript files.
	 */
	public int tsFileCount;
	
	/**
	 * Number of generated TypeScript lines of code (output).
	 */
	public int tsLineCount;

	/**
	 * Number of generated TypeScript classes.
	 */
	public int tsClassCount;

	/**
	 * Number of generated TypeScript interfaces.
	 */
	public int tsInterfaceCount;

	/**
	 * Number of generated TypeScript enums.
	 */
	public int tsEnumCount;
	
	/**
	 * Number of generated TypeScript methods.
	 */
	public int tsMethodCount;
	
	/**
	 * Total time (ms).
	 */
	public long totalTime;
	
	public String toString() {
		return 
				"---------------------------------------------------------------------------\n" +
				"JSWEET TRANSPILATION STATS\n" +
				"---------------------------------------------------------------------------\n" +
				"Java to TypeScript total time: " + totalTime + "ms\n" +
				"---------------------------------------------------------------------------\n" +
				"INPUT\n" +
				"---------------------------------------------------------------------------\n" +
				"Java files:           " + javaFileCount + "\n" +
				"Java lines of code:   " + javaLineCount + "\n" +
				"Java classes:         " + javaClassCount + "\n" +
				"Java interfaces:      " + javaInterfaceCount + "\n" +
				"Java enums:           " + javaEnumCount + "\n" +
				"Java methods:         " + javaMethodCount + "\n" +
				"Java erased elements: " + erasedJavaClassCount + "\n" +
				"Java erased methods:  " + erasedJavaMethodCount + "\n" +
				"---------------------------------------------------------------------------\n" +
				"OUTPUT\n" +
				"---------------------------------------------------------------------------\n" +
				"Generated TypeScript files:         " + tsFileCount + "\n" +
				"Generated TypeScript lines of code: " + tsLineCount + "\n" +
				"Generated TypeScript classes:       " + tsClassCount + "\n" +
				"Generated TypeScript interfaces:    " + tsInterfaceCount + "\n" +
				"Generated TypeScript enums:         " + tsEnumCount + "\n" +
				"Generated TypeScript methods:       " + tsMethodCount + "\n" +
				"---------------------------------------------------------------------------\n";
	
	}
	
}
