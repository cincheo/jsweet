# file-visitor

This utility written in Java allows for visiting a set of files within a directory an apply bulk action written in so-called file visitors. It can be specialized to perform any job on the visited files.

## How to use

- Implement the ``FileVisitor`` interface.
- Invoke the ``FileVisitor.scan(root, fileVisitors)`` method, where ``root`` is the root file to be scanned, and ``fileVisitors`` are you ``FileVisitor`` implementations.

## Licence

Open Source Apache 2
