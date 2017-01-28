# JSweet jsweet-core candy

## Description

This project is a Java bridge to the jsweet-core library, so that jsweet-core can be used from Java (thanks to the JSweet transpiler).

This candy is used for compilation (typing and API checking) but does not contain any implementation, similarly to a C/C++ header file or to a TypeScript definition file. 

## How to install the candy

Clone this project with git and install the JSweet candy in your local Maven repository (so that you can refer to it as a dependency in other Maven projects):

```bash
> mvn install
```

To deploy the candy to the [jsweet-candies](https://github.com/jsweet-candies) organization (please contact us to get an authorized access):

```bash
> mvn deploy
```

Note: if you want to publish the candy in your own Maven repository, please refer to existing documentation on that particular topic.

## Use from a Java/JSweet project

Add the following dependency to the ``pom.xml``:

```xml
<dependency>
	<groupId>org.jsweet.candies.ext</groupId>
	<artifactId>jsweet-core</artifactId>
	<version>6-SNAPSHOT</version>
</dependency>
```

## License

By default, JSweet candies are published with the Apache 2 Open Source license. Since they are pure APIs, remember that it does not make sense to try to bind a JSweet candy to a commercial license (on contrary to the bridged JS library/framework, which can be distributed under a non-open source license).  