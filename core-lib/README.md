# JSweet core API

This JSweet sub-project defines the core APIs for JSweet programs. It aims at allowing the programming of JavaScript applications in Java.

## Main content

It contains a Maven project for each JavaScript version (so far only ES5 version). Each version defines the following APIs:

- a ``def.js`` package, which contains all the JavaScript language core APIs for the appropriate JavaScript version.
- a ``def.dom`` package, which contains all the JavaScript language W3C DOM APIs for the appropriate JavaScript version.
- a ``jsweet.lang`` package that contains the JSweet language extensions for Java (mainly annotations).
- a ``jsweet.util`` package that contains some utilities (helpers).

## License

This project is licensed with the Apache 2 license. It means that you can use this API and its source code in any kind of projects, even closed source and commercial projects.