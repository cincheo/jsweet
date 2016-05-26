# JSweet: a Java to JavaScript transpiler [![Build Status](https://travis-ci.org/cincheo/jsweet.svg?branch=master)](https://travis-ci.org/cincheo/jsweet)

JSweet is built on the top of TypeScript to bring the most up-to-date JavaScript APIs right to the Java world. JSweet can be seen as a Java syntax layer to TypeScript.

JSweet is *not* Java, it is Java syntax only. JavaScript APIs and core objects are all accessible. Java APIs are accessible with some limitations, either through compile-time mapping to TypeScript APIs, either using the [J4TS](https://github.com/cincheo/j4ts) candy, which provides an implementation of the core Java APIs in JSweet. 

JSweet programs usually run on a JavaScript VM, but the programs that only use Java APIs can also run on a Java VM. So you are able to share some code between Java and JavaScript. With JSweet, you take advantage of all the Java tooling to program *real* JavaScript applications using the *latest* JavaScript libraries. JSweet is designed to be as safe as possible and generate fully type-checked JavaScript programs.

## Features

- Full syntax mapping between Java and TypeScript, including classes, interfaces, functional types, union types, tuple types, object types, string types, and so on.
- Extensive support of Java constructs and semantics added since [version 1.1.0](https://github.com/cincheo/jsweet/releases/tag/v1.1.0) (inner classes, anonymous classes, final fields, method overloading, instanceof operator, static initializers, ...).
- *Over 1000 JavaScript libraries*, frameworks and plugins to write Web and Mobile HTML5 applications (JQuery, Underscore, Angular, Backbone, Cordova, Node.js, and much [more](http://www.jsweet.org/candies-snapshots/)).
- A [Maven repository](http://repository.jsweet.org/artifactory) containing all the available libraries in Maven artifacts (a.k.a. candies).
- Support for Java basic APIs as the [J4TS](https://github.com/cincheo/j4ts) candy (forked from the GWT's JRE emulation).
- An [Eclipse plugin](https://github.com/cincheo/jsweet-eclipse-plugin) for easy installation and use.
- A [Maven plugin](https://github.com/lgrignon/jsweet-maven-plugin) to use JSweet from any other IDE or from the command line.
- A debug mode to enable Java code debugging within your favorite browser.
- A set of nice [WEB/Mobile HTML5 examples](https://github.com/cincheo/jsweet-examples) to get started and get used to JSweet and the most common JavaScript APIs (even more examples in the Examples section). 
- Support for bundles to run the generated programs in the most simple way.
- Support for JavaScript modules (commonjs, amd, umd). JSweet programs *can* run in a browser or in Node.js.
- Support for various EcmaScript target versions (ES3 to ES6).
- ...

For more details, go to the [language specifications](https://github.com/cincheo/jsweet/blob/master/doc/jsweet-language-specifications.md) ([PDF](https://github.com/cincheo/jsweet/raw/master/doc/jsweet-language-specifications.pdf)).

## Getting started

- Step 1: Install (or check that you have installed) [Git](https://git-scm.com/downloads), [Node.js](https://nodejs.org) and [Maven](https://maven.apache.org/) (commands `git`, `node`, `npm` and `mvn` should be in your path).
- Step 2: Clone the [jsweet-quickstart](https://github.com/cincheo/jsweet-quickstart) project from Github:
```
> git clone https://github.com/cincheo/jsweet-quickstart.git
```
- Step 3: Run the transpiler to generate the JavaScript code:
```
> cd jsweet-quickstart
> mvn generate-sources
```
- Step 4: Check out the result in your browser:
```
> firefox webapp/index.html
```
- Step 5: Edit the project and start programming:
	- Checkout the examples to see various use cases 
	- Refer to the [language specifications](https://github.com/cincheo/jsweet/blob/master/doc/jsweet-language-specifications.md) to know more about programming with JSweet
	- Eclipse users: install the [Eclipse plugin](http://www.jsweet.org/eclipse-plugin/) to get inline error reporting, build-on-save, and easy configuration UI

More info at http://www.jsweet.org.

## Examples

- Examples illustrating the use of various frameworks in Java (jQuery, Underscore, Backbone, AngularJS, Knockout). [Go to project](https://github.com/cincheo/jsweet-examples). 
- Examples illustrating the use of the Threejs framework in Java. [Go to project](https://github.com/cincheo/jsweet-examples-threejs). 
- Node.js + Socket.IO + AngularJS. [Go to project](https://github.com/lgrignon/jsweet-node-example).
- Some simple examples to get started with React.js. [Go to project](https://github.com/cincheo/jsweet-examples-react).

## Tooling

- [Eclipse plugin](https://github.com/cincheo/jsweet-eclipse-plugin)
- [Maven plugin](https://github.com/lgrignon/jsweet-maven-plugin)
- [Gradle plugin](https://github.com/lgrignon/jsweet-gradle-plugin)

## News and more information

Most information is available on the http://www.jsweet.org. Developers, check out the [wiki](https://github.com/cincheo/jsweet/wiki).

## How to build

Prerequisites: `node` and `npm` executables must be in the path (https://nodejs.org). Note that there seem to be an issue with the `node` executable on some Unix-based machines, which should be fixed (see for instance: http://askubuntu.com/questions/235655/node-js-conflicts-sbin-node-vs-usr-bin-node).

To build the `jsweet-transpiler` jars (in the project's directory):

```
> mvn package
```

To install the `jsweet-transpiler` artifact in your local Maven repository:

```
> mvn install
```

Note that current JUnit tests launch a Node.js instance for each test and will be quite slow (this will be improved). In order to easily test some changes locally without having to run all the tests, use the following command:

```
> mvn package -Dmaven.test.skip=true
```

or

```
> mvn install -Dmaven.test.skip=true
```

To generate the markdown language specifications from the Latex source file with [Pandoc](http://pandoc.org/):

```
> cd doc
> pandoc -r latex -w markdown_github --base-header-level=2 -s --toc --number-sections -B header.md -o jsweet-language-specifications.md jsweet-language-specifications.tex
```

Note that the following command will output the document in HTML:

```
> pandoc -r latex -w html5 --base-header-level=3 -o jsweet-language-specifications.html jsweet-language-specifications.tex
```

## License

JSweet is under the Apache 2.0 Open Source license.

