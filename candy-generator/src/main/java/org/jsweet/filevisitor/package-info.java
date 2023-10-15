/**
 * <h1>file-visitor</h1>
 * <p>
 * This utility written in Java allows for visiting a set of files within a directory an apply bulk
 * action written in so-called file visitors. It can be specialized to perform any job on the
 * visited files.
 * <h2>How to use</h2>
 * <ul>
 * <li>Implement the ``FileVisitor`` interface.</li>
 * <li>Invoke the ``FileVisitor.scan(root, fileVisitors)`` method, where ``root`` is the root file
 * to be scanned, and ``fileVisitors`` are you ``FileVisitor`` implementations.</li>
 * </ul>
 */
package org.jsweet.filevisitor;
