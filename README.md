# JSweet: a Java to JavaScript transpiler [![Build Status](https://travis-ci.org/cincheo/jsweet.svg?branch=master)](https://travis-ci.org/cincheo/jsweet) [ ![Download](https://api.bintray.com/packages/jsweet/maven/jsweet-transpiler/images/download.svg?version=2.2.0) ](https://bintray.com/jsweet/maven/jsweet-transpiler/2.2.0/link)

JSweet leverages TypeScript to write rich and responsive Web applications in Java through the use of JavaScript libraries and frameworks. With JSweet, Java programs are transpiled (source-to-source compiled) to TypeScript and JavaScript for being run in browsers, mobile Web views, or in Node.js. 

* JSweet is safe and reliable. It provides web applications with type-checking and generates fully type-checked JavaScript programs. It stands on Oracle's Java Compiler (javac) and on Microsoft's TypeScript (tsc). 
* JSweet allows you to use your favorite JS library ([JSweet+Angular2](https://github.com/cincheo/jsweet-angular2-quickstart), [JSweet+threejs](https://github.com/cincheo/jsweet-examples-threejs), [IONIC/Cordova](https://github.com/lgrignon/jsweet-cordova-ionic-example), ...).
* JSweet enables code sharing between server-side Java and client-side JavaScript. JSweet provides implementations for the core Java libraries for code sharing and legacy Java migration purpose.
* JSweet is fast, lightweight and fully JavaScript-interoperable. The generated code is regular JavaScript code, which implies no overhead compared to JavaScript, and can directly interoperate with existing JavaScript programs and libraries.

How does it work? JSweet depends on well-typed descriptions of JavaScript APIs, so-called "candies", most of them being automatically generated from TypeScript definition files. These API descriptions in Java can be seen as headers (similarly to *.h header files in C) to bridge JavaSript libraries from Java. There are several sources of candies for existing libraries and you can easily build a candy for any library out there (see [more details](http://www.jsweet.org/jsweet-candies/)). 

With JSweet, you take advantage of all the Java tooling (IDE's, Maven, ...) to program real JavaScript applications using the latest JavaScript libraries.

## Java -> TypeScript -> JavaScript

Here is a first taste of what you get by using JSweet. Consider this simple Java program:

```java
package org.jsweet;

import static jsweet.dom.Globals.*;

/**
 * This is a very simple example that just shows an alert.
 */
public class HelloWorld {
	public static void main(String[] args) {
		alert("Hi there!");
	}
}
```

Transpiling with JSweet gives the following TypeScript program:

```TypeScript
namespace org.jsweet {
    /**
     * This is a very simple example that just shows an alert.
     */
    export class HelloWorld {
        public static main(args : string[]) {
            alert("Hi there!");
        }
    }
}
org.jsweet.HelloWorld.main(null);
```

Which in turn produces the following JavaScript output:

```JavaScript
var org;
(function (org) {
    var jsweet;
    (function (jsweet) {
        /**
         * This is a very simple example that just shows an alert.
         */
        var HelloWorld = (function () {
            function HelloWorld() {
            }
            HelloWorld.main = function (args) {
                alert("Hi there!");
            };
            return HelloWorld;
        }());
        jsweet.HelloWorld = HelloWorld;
    })(jsweet = org.jsweet || (org.jsweet = {}));
})(org || (org = {}));
org.jsweet.HelloWorld.main(null);
```

More with the [live sandbox](http://www.jsweet.org/jsweet-live-sandbox/).

## Features

- Full syntax mapping between Java and TypeScript, including classes, interfaces, functional types, union types, tuple types, object types, string types, and so on.
- Extensive support of Java constructs and semantics added since [version 1.1.0](https://github.com/cincheo/jsweet/releases/tag/v1.1.0) (inner classes, anonymous classes, final fields, method overloading, instanceof operator, static initializers, ...).
- *Over 1000 JavaScript libraries*, frameworks and plugins to write Web and Mobile HTML5 applications (JQuery, Underscore, Angular, Backbone, Cordova, Node.js, and much [more](http://www.jsweet.org/candies-snapshots/)).
- A [Maven repository](http://repository.jsweet.org/artifactory) containing all the available libraries in Maven artifacts (a.k.a. candies).
- Support for Java basic APIs as the [J4TS](https://github.com/j4ts/j4ts) candy (forked from the GWT's JRE emulation).
- An [Eclipse plugin](https://github.com/cincheo/jsweet-eclipse-plugin) for easy installation and use.
- A [Maven plugin](https://github.com/lgrignon/jsweet-maven-plugin) to use JSweet from any other IDE or from the command line.
- A [Gradle plugin](https://github.com/lgrignon/jsweet-gradle-plugin) to integrate JSweet with Gradle-based projects.
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
```bash
$ git clone https://github.com/cincheo/jsweet-quickstart.git
```
- Step 3: Run the transpiler to generate the JavaScript code:
```bash
$ cd jsweet-quickstart
$ mvn generate-sources
```
- Step 4: Check out the result in your browser:
```bash
$ firefox webapp/index.html
```
- Step 5: Edit the project and start programming:
	- Checkout the examples to see various use cases 
	- Get access to hundreds of [libs (candies)](http://www.jsweet.org/jsweet-candies/)
	- Refer to the [language specifications](https://github.com/cincheo/jsweet/blob/master/doc/jsweet-language-specifications.md) to know more about programming with JSweet
	- Eclipse users: install the [Eclipse plugin](http://www.jsweet.org/eclipse-plugin/) to get inline error reporting, build-on-save, and easy configuration UI

More info at http://www.jsweet.org.

## Examples

- Simple examples illustrating the use of various frameworks in Java (jQuery, Underscore, Backbone, AngularJS, Knockout): https://github.com/cincheo/jsweet-examples 
- Simple examples illustrating the use of the Threejs framework in Java: https://github.com/cincheo/jsweet-examples-threejs) 
- Node.js + Socket.IO + AngularJS: https://github.com/lgrignon/jsweet-node-example
- Some simple examples to get started with React.js: https://github.com/cincheo/jsweet-examples-react
- JSweet JAX-RS server example (how to share a Java model between client and server): https://github.com/lgrignon/jsweet-jaxrs-server-example 
- JSweet Cordova / Polymer example: https://github.com/lgrignon/jsweet-cordova-polymer-example
- JSweet Cordova / Ionic example: https://github.com/lgrignon/jsweet-cordova-ionic-example
- JSweet Angular 2 example: https://github.com/cincheo/jsweet-angular2-quickstart
- JSweet Angular 2 + PrimeNG: https://github.com/cincheo/jsweet-primeng-quickstart 

## Sub-projects

This repository is organized in sub-projects. Each sub-project has its own build process.

* [JSweet transpiler](https://github.com/cincheo/jsweet/tree/master/transpiler): the Java to TypeScript/JavaScript compiler.
* [JSweet core candy](https://github.com/cincheo/jsweet/tree/master/core-lib): the core APIs (JavaScript language, JavaScript DOM, and JSweet language utilities).
* [JDK runtime](https://github.com/j4ts/j4ts): a fork from GWT's JRE emulation to implement main JDK APIs in JSweet/TypeScript/JavaScript.
* [JSweet candy generator](https://github.com/cincheo/jsweet/tree/master/candy-generator): a tool to generate Java APIs from TypeScript definition files, and package them as JSweet candies.
* [JSweet documentation](https://github.com/cincheo/jsweet/tree/master/doc): JSweet documentation.

Additionally, some tools for JSweet are available in external repositories.

- [Maven plugin](https://github.com/lgrignon/jsweet-maven-plugin)
- [Gradle plugin](https://github.com/lgrignon/jsweet-gradle-plugin)
- [Eclipse plugin](https://github.com/cincheo/jsweet-eclipse-plugin)

## How to build

Please check each sub-project README file.

## Contributing

JSweet uses [Git Flow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).
You can fork this repository. Default branch is develop. Please use `git flow feature start myAwesomeFeature` to start working on something great :)
When you are done, you can submit a regular [GitHub Pull Request](https://help.github.com/en/articles/about-pull-requests).

## License

Please read the [LICENSE file](https://github.com/cincheo/jsweet/tree/master/LICENSE).
