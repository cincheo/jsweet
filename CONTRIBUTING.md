
## What to contribute?

Check out the issues for bugs and features where your contributions could be useful.

## Your contributions

Your contributions are most welcome. Bugs and features should be first reported in the Github issues space and discussed, so that we all know who is working on what, and so that we avoid duplicate efforts. Your contribution (especially on the transpiler) should come with the corresponding test(s) if possible (see below on how to write tests).

## Contribution License Agreement

By contributing through a pull request, you acknowledge that you fully own your Contribution and that you are legally entitled to make it available to the JSweet project under the terms and conditions of the [license](https://github.com/cincheo/jsweet/blob/master/LICENSE). This license implies that you grant the JSweet project, and those who receive the code directly or indirectly from the JSweet project, a perpetual, worldwide, non-exclusive, royalty-free, irrevocable license in the Contribution to reproduce, prepare derivative works of, publicly display, publicly perform, and distribute your Contribution and such derivative works, and to sublicense any or all of the foregoing rights to third parties. 

Your execution and/or acceptance of this agreement does not influence any of your right and title to use and distribute your own Contribution.

## How to write JSweet transpiler tests

I guess that we all have our own perception of how a good test should look like. IMO, good transpilers/compilers tests fall into 2 categories: compilation tests and evaluation (semantic) tests.

For compilation tests:

1. Compile the source code you want to check.
2. Assert that you have the right number and kinds of errors (if you expect no errors, then just assert that everything went fine).

For evaluation tests:

1. Compile and run the source code you want to check.
2. Assert that you have the expected execution.

In JSweet, expected execution can be checked with various means:

- Use Java ``assert`` in the program (will raise an error if fails).
- Use the ``$export`` macro (JSweet-specific) that will export a variable in the evaluation result that you can check in the test. Most of the time, I like to export the full execution trace and check that it corresponds to the expected trace.

Note that although I find it very important in other systems I have been developing, I don't do mocking in JSweet. It does not look that useful in a transpiler context so far. 

Note also that in some tests it is even possible to execute the Java version an compare the output... I do it in some rare cases.
 
In order to implement all these tests, we have set up a small test "framework" that you should integrate into. All JUnit tests can be found [here](https://github.com/cincheo/jsweet/tree/master/transpiler/src/test/java/org/jsweet/test/transpiler). They use the corresponding source code that you will find [there](https://github.com/cincheo/jsweet/tree/master/transpiler/src/test/java/source). All the tests extend the [AbstractTest](https://github.com/cincheo/jsweet/blob/master/transpiler/src/test/java/org/jsweet/test/transpiler/AbstractTest.java) class that provides all the mechanics and utility methods you can use to write and run transpilation tests. In general, you'll just need the ``transpile`` and ``eval`` functions. You will also use the ``getSourceFile`` function that returns the input source file for the test out of its main class. This function will work if you respect the directory structure for JUnit tests and tested input files. 

Here are some typical tests using this "framework" (taken from [StructuralTests](https://github.com/cincheo/jsweet/blob/master/transpiler/src/test/java/org/jsweet/test/transpiler/StructuralTests.java)):

```java
// A compilation test that expects no errors
// Note the getSourceFile that looks up the input source file out of the class (very convenient)
@Test
public void testPrivateFieldNameClashes() {
  transpile(logHandler -> {
    logHandler.assertNoProblems();
  }, getSourceFile(PrivateFieldNameClashes.class));
}

// A compilation test that expects 3 errors of a particular kind
@Test
public void testVariableMethodNameClashes() {
  transpile(logHandler -> {
    logHandler.assertReportedProblems(JSweetProblem.HIDDEN_INVOCATION, JSweetProblem.HIDDEN_INVOCATION,
      JSweetProblem.HIDDEN_INVOCATION);
  }, getSourceFile(NameClashesWithMethodInvocations.class));
} 

// An evaluation test that asserts on the result of the evaluation ($export)
@Test
public void testNoNameClashesWithFields() {
  eval((logHandler, r) -> {
    logHandler.assertNoProblems();
    assertEquals(2, (int) r.get("v1"));
    assertEquals(2, (int) r.get("v2"));
    assertEquals(3, (int) r.get("v3"));
    assertEquals("hello", (String) r.get("v4"));
    assertEquals("hello", (String) r.get("v5"));
  }, getSourceFile(NoNameClashesWithFields.class));
}

// An evaluation test that asserts on an exported trace
@Test
public void testInnerClassNotStatic() {
  eval((logHandler, r) -> {
    logHandler.assertNoProblems();
    assertEquals("22abc,22a,22ABC,22a,22b,22c,22ABC,test22a,staticMethod,1", r.get("trace"));
  }, getSourceFile(InnerClassNotStatic.class));
}
```

By default the ``transpile`` and ``eval`` functions will test with and without modules (it also tests with bundles when several input files are given). The test can be forced to apply only with module or only without modules (check the overloads of ``transpile`` and ``eval``).
