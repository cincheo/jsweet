# J4TS
Java APIs for TypeScript / JavaScript / JSweet

J4TS is based on a fork of the GWT's JRE emulation library and is written in Java, and transpiled to TypeScript/JavaScript with the [JSweet transpiler](https://github.com/cincheo/jsweet/). It intends to be useful for the following cases:

- Programmers used to the Java APIs can be more efficient using J4TS than when having to learn basic JavaScript APIs.
- It can ease code sharing between Java and TypeScript/JavaScript (and also hopefully, ease the understanding and relationships between the Java fans and TypeScript/JavaScript ones).
- Typically, J4TS can be used as a runtime for transpilers, so that you can use the Java APIs in your transpiled Java programs. So far, J4TS main target is the [JSweet transpiler](https://github.com/cincheo/jsweet/), but it is not limited to it.

J4TS currently covers most of the core Java API supported by GWT (``java.lang``, ``java.util``, some ``java.io``). It does not support ``java.math`` yet because the GWT implementation requires a deep Java emulation, which is not consistent with the JSweet approach (so ``java.math`` should be implemented as a wrapper for [bignumber.js](https://github.com/MikeMcl/bignumber.js/) for instance). 

J4TS is intended to be completed on-the-fly as more use cases are needed. So feel free to contribute.

## Examples
```TypeScript
import List = java.util.List;
import ArrayList = java.util.ArrayList;
import Set = java.util.Set;
import HashSet = java.util.HashSet;
import Map = java.util.Map;
import HashMap = java.util.HashMap;

var l: List<String> = new ArrayList<String>();
l.add("a");
l.add("b");
l.add("c");
assertEquals("[a, b, c]", l.toString());
assertEquals(l.indexOf("a"), 0);

var s: Set<String> = new HashSet<String>();
s.add("a");
s.add("a");
s.add("b");
s.add("c");
s.add("c");
assertEquals(3, s.size());
assertTrue(s.contains("c"));

var s: Map<String, String> = new HashMap<String, String>();
s.put("a", "aa");
s.put("b", "bb");
s.put("c", "cc");
assertEquals("bb", s.get("b"));
```

## How to use

You can use the current JavaScript bundle (JSweet-generated runtime): ``dist/j4ts.js``. 

From TypeScript, you can compile with: ``dist/j4ts.d.ts``.

From JSweet, add the candy dependency in your ``pom.xml``.

```xml
<dependency>
	<groupId>org.jsweet.candies</groupId>
	<artifactId>j4ts</artifactId>
	<version>VERSION</version>
</dependency>
```

A simple and still incomplete test suite is available [there](https://github.com/cincheo/j4ts/blob/master/src/main/java/test/Test.java). Make sure that the tests pass by opening [index.html](https://github.com/cincheo/j4ts/blob/master/index.html).

## How to modify/package

You can compile, package and install the J4TS candy in your local Maven repository by running Maven in the project's directory:

```
> mvn install
```

## Disclaimer

J4TS is not a Java emulator and is not made for fully implementing the Java semantics in JavaScript. It is close to and mimics Java behavior, but it will never be completely Java. For instance, primitive types in Java and JavaScript are quite different (chars and numbers especially) and we don't want to emulate that difference.

## Contributions

J4TS is meant to serve the public interest and be as open as possible. So anyone is more than welcome to contribute as long as it does not deviate J4TS from its initial goals stated above. When you meet a class or a method that is not supported, please feel free to contribute under the terms of the license.

## License

J4TS is licensed under the Apache Open Source License version 2.