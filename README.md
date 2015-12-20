# JSweet: a Java to JavaScript transpiler [![Build Status](https://jenkins.jsweet.org/buildStatus/icon?job=jsweet)](https://jenkins.jsweet.org/job/jsweet/)

JSweet is built on the top of TypeScript to bring the most up-to-date JavaScript APIs right to the Java world.

JSweet is *not* Java, it is Java syntax only and the APIs and programs run in JavaScript. With JSweet, your take advantage of all the Java tooling to program *real* JavaScript applications using the *latest* JavaScript libraries. JSweet is designed to be as safe as possible and generate fully type-checked JavaScript programs.

## Features

- Full syntax mapping between Java and TypeScript, including classes, interfaces, functional types, union types, tuple types, object types, string types, and so on.
- *Over 1000 JavaScript libraries*, frameworks and plugins to write Web and Mobile HTML5 applications (JQuery, Underscore, Angular, Backbone, Cordova, Node.js, and much [more](http://www.jsweet.org/candies-snapshots/)).
- A Maven repository containing all the available libraries in Maven artifacts (a.k.a. candies).
- An Eclipse plugin for easy installation and use.
- A simple Maven integration to use JSweet from any other IDE or from the command line.
- A debug mode to enable Java code debugging within your favorite browser.
- A set of nice WEB/Mobile HTML5 examples to get started and get used to JSweet and the most common JavaScript APIs (see the Examples section). 
- Support for JavaScript modules (commonjs, amd, umd). JSweet programs *can* run in a browser or in Node.js.
- Support for various EcmaScript target versions (ES3 to ES6).
- Support for bundles to run the generated programs in the most simple way.
- ...

## Getting started

- Step 1: Install (or check that you have installed) [Git](https://git-scm.com/downloads), [Node.js](https://nodejs.org) and [Maven](https://maven.apache.org/) (commands `git`, `node`, `npm` and `mvn` should be in your path).
- Step 2: Clone the `jsweet-quickstart` project from Github:
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

## Examples [![Build Status](https://jenkins.jsweet.org/buildStatus/icon?job=jsweet-uitests-web)](https://jenkins.jsweet.org/job/jsweet-uitests-web/)

- [![Build Status](https://jenkins.jsweet.org/buildStatus/icon?job=jsweet-examples)](https://jenkins.jsweet.org/job/jsweet-examples/) Examples illustrating the use of various frameworks in Java (jQuery, Underscore, Backbone, AngularJS, Knockout). [Go to project](https://github.com/cincheo/jsweet-examples). 
- [![Build Status](https://jenkins.jsweet.org/buildStatus/icon?job=jsweet-examples-threejs)](https://jenkins.jsweet.org/job/jsweet-examples-threejs/) Examples illustrating the use of the Threejs framework in Java. [Go to project](https://github.com/cincheo/jsweet-examples-threejs).
- [![Build Status](https://jenkins.jsweet.org/buildStatus/icon?job=jsweet-node-example)](https://jenkins.jsweet.org/job/jsweet-node-example/) Node.js + Socket.IO + AngularJS. [Go to project](https://github.com/lgrignon/jsweet-node-example). 

## License

JSweet is under the Apache 2.0 Open Source license.

