# JSweet version 4.0.0

This document describes our plan for JSweet version 4.

## Motivations

Having a transpilation framework generating JavaScript code from Java code with minimalistic overhead is of public interest for the community. By generating Typescript code from Java and relying on TSC to generate Javascript code, JSweet generated code is light and efficient (see https://www.infoq.com/news/2019/05/web-app-java-jvm-alternatives/), with no specific runtime or overhead, and no additional memory footprint compared to regular JavaScript.

As the creators and maintainers of JSweet (Renaud Pawlak & Louis Grignon), our interests have shifted to other projects such as eco-designed software (see Renaud's new project: https://www.daquota.io) and we don't have enough time to seriously maintain JSweet. So we want to make sure it is durable and sustainable. We want to leave it in a state where it will be easily useable and maintanable in the long term.  

## Objectives and plan for v4

In JSweet v4, we want to upgrade Java compatibility to all Java versions and simplify JSweet to make it as sustainable and easy-to-maintain as possible for the community.
Here is a list of objectives, from which we might be able to derivate a detailled action plan with a timeline, but all this will be best-effort-based as usual).

- Make sure that the Open Source licence is compatible with a community/fundation (for instance Apache 2) and make a licence change if necessary
- Make JSweet compatible for more Java versions (and make the support of future versions as easy as possible)
  - Check how the new syntax constructions can be easily supported and provide some basic documentation
  - Try to provide support up to current Java version (NOTE: the idea would not necessarily be to support all new syntax, but at least, that a Java program that only uses v8 or v11 syntax transpiles with a newer version of the JDK, for instance Java 21 if possible)
  - Make sure that the Java AST (compilier API) provided as from JDK 11 is still compatible with newer version of Java and make the necessary refactoring to achieve that goal
  - Make sure that key projects that use JSweet still transpile (potentially, create a migration guide if necessary)
    - All examples
    - SweetHome3D
    - ... 
- Remove Artifactory dependency
  - Update dependencies and make sure it works with latest versions of TSC (Typescript)
  - Publish JSweet core arfifacts, J4TS and basic candies to Maven Central
  - Make sure that it is easy to generate new candies on-the-fly (candies should not be published except for core ones), and make sure it is properly documented
  - Unplug Artifactory
- Remove JSweet Wordpress and all dependencies to legacy server
  - Make a simple minimalistic site with Gihub pages
  - Point jsweet.org to the new site
  - Unplug the server

## People and timeline

This plan is based on best effort of everyone, so no timeline is possible (let's say ASAP ;)). 
Lead will be ensured for now by @kohlschuetter (many thanks to him) and all individdual contributions are welcome. Especially, if you have a project using JSweet, making sure that it is easy to migrate and giving feedback would be great. 
