# JSweet version 4.0.0

This document describes our plan for JSweet version 4.

## Motivations

Having a transpilation framework generating JavaScript code from Java code with minimalistic overhead is of public interest for the community. By generating Typescript code from Java and relying on TSC to generate Javascript code, JSweet generated code is light and efficient (see https://www.infoq.com/news/2019/05/web-app-java-jvm-alternatives/), with no specific runtime or overhead, and no additional memory footprint compared to regular JavaScript.

As the creators and maintainers of JSweet (Renaud Pawlak & Louis Grignon), our interests have shifted to other projects such as eco-designed software (see Renaud's new project: https://www.daquota.io) and we don't have enough time to seriously maintain JSweet. So we want to make sure it is durable and sustainable. We want to leave it in a state where it will be easily useable and maintanable in the long term.  

## Objectives and plan for v4

In JSweet v4, we want to upgrade Java compatibility to all Java versions and simplify JSweet to make it as sustainable and easy-to-maintain as possible for the community.
Here is a list of objectives, from which we might be able to derivate a detailled action plan with a timeline, but all this will be best-effort-based as usual).

- Find a new maintainer that would have the time and motivation to take over JSweet and make JSweet 4 (and more) a reality.
  That new maintainer should probably be an organization that already uses JSweet. To on-board this new maintainer, we are open to relicense the entire project if necessary.
  In particular, relicensing under the Apache 2.0 license, could help collaboration with related projects, such as [TeaVM](https://teavm.org) and [j2cl](https://github.com/google/j2cl)
- Reformat all code automatically via Maven plugins to simplify contributions
- Make JSweet compatible with newer Java versions and make the support of future versions as easy as possible
  - Provide runtime support up to Java JDK 21 (NOTE: it is a non-goal to add support
    for new language features. The goal is to support transpipling Java source code using Java 8 or
    11 with a newer version of the JDK)
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

## Timeline

The objective would be to find potential organizations to take over the maintenance by february 2024.  
Several communications will be posted and some key orgs will be contacted. If no org is found to take over, then the backup plan is to leave mantenance to active users of the community, but open to the licensing to Apache 2.0 in order to remove potential adoption barriers w.r.t. licensing.   
