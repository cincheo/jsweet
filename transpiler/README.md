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

Since version 2, the JSweet transpiler source code is licensed under GPLv3, which in short means that you can use is as is to compile any kind of programs (including closed-source commercial ones). You can also use/modify this transpiler's source code in any open source project (commercial or not), as long as you conform to the license terms (see the license file). On the other hand, your cannot embed this transpiler's source code in a closed-source commercial project. In case you would want to do so, you must contact Renaud Pawlak (renaud.pawlak@gmail.com), who can grant you a commercial license depending on your use case.

NOTE: JSweet transpiler version 1.x, which is no longer maintained, is licensed under Apache v2.

## Release
```
git flow release start X.Y.Z
```
Change core-lib versions (remove SNAPSHOT) and mvn deploy them
Change jsweet-transpiler's version and core-lib dependencies' versions and mvn deploy it

```
git flow release finish
```

Release related projets (jsweet-maven-plugin, jsweet-gradle-plugin, jsweet-examples, ...) by updating snapshot dependencies to release dependencies, bumping version number and mvn deploying them.
Create git tags.

If possible, deploy to Bintray and Maven Central.

Declare release in GitHub.
 
Update all projects to next snapshot versions.


