# JSweet transpiler

This Maven project contains the JSweet Java to TypeScript (to JavaScript) transpiler.

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

## License

JSweet is under the Apache 2.0 Open Source license.

