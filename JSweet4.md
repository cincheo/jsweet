# JSweet version 4.0.0

This document describes our plan for JSweet version 4.

## Motivations

Having a transpilation framework generating JavaScript code from Java code with minimalistic overhead is of public interest for the community. By generating Typescript code from Java and relying on TSC to generate Javascript code, JSweet generated code is light and efficient (see https://www.infoq.com/news/2019/05/web-app-java-jvm-alternatives/), with no specific runtime or overhead, and no additional memory footprint compared to regular JavaScript.

As the creators and maintainers of JSweet (Renaud Pawlak & Louis Grignon), our interests have shifted to other projects such as eco-designed software (see Renaud's new project: https://www.daquota.io) and we don't have enough time to seriously maintain JSweet. So we want to make sure it is durable and sustainable. We want to leave it in a state where it will be easily useable and maintanable in the long term.  

## Objectives and plan for v4

In JSweet v4, we want to upgrade Java compatibility to all Java versions and simplify JSweet to make it as sustainable and easy-to-maintain as possible for the community.
Here is a list of objectives, from which we might be able to derivate a detailled action plan with a timeline, but all this will be best-effort-based as usual).

- Relicense the entire project under the Apache 2.0 license, which should help collaboration with
  related projects, such as [TeaVM](https://teavm.org) and [j2cl](https://github.com/google/j2cl)
- Reformat all code automatically via Maven plugins to simplify contributions
- Make JSweet compatible with newer Java versions and make the support of future versions as easy as possible
  - Provide runtime support up to Java JDK 21 (NOTE: it is a non-goal to add support
    for new language features. The goal is to support transpipling Java source code using Java 8 or
    11 with a newer version of the JDK)
  - In order to achieve compatibility with the JDK 11 internal compiler tree API even for higher
    Java VMs, use a [standalone implementation](https://github.com/kohlschutter/jdk.compiler.standalone)
    of the `jdk.compiler` component.
  - Make sure that key projects that use JSweet still transpile (potentially, create a migration guide if necessary)
    - All examples
    - SweetHome3D
    - ... 
  - Check how/if new Java syntax constructs could be supported and provide some basic documentation
- Remove Artifactory dependency
  - Update dependencies and make sure it works with latest version of TSC (Typescript)
  - Publish JSweet core arfifacts, J4TS and basic candies to Maven Central; no longer rely on
    a custom repository.
  - Make sure that it is easy to generate new candies on-the-fly (candies should not be published except for core ones), and make sure it is properly documented
  - The [JSweet Artifactory server](http://repository.jsweet.org/) will be unplugged at a future
    point in time.
- Remove JSweet Wordpress and all dependencies to legacy server
  - Deploy a website with Gihub pages
  - Point jsweet.org to the new site
  - The webserver will be unplugged at a future point in time.

## People and timeline

This plan is based on best effort of everyone, so no timeline is possible (let's say ASAP ;)). 
Lead will be ensured for now by @kohlschuetter (many thanks to him) and all individual contributions are welcome. Especially, if you have a project using JSweet, making sure that it is easy to migrate and giving feedback would be great. 
