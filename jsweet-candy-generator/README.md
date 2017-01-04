# JSweet candy generator

This tool is a helper to create candies (Java APIs) from TypeScript definition files.

## Description

This tools consumes TypeScript definition files and produces the corresponding Java APIs, conforming to the JSweet core API (see the jsweet-core sub-project).

It comes with a command that generates a default JSweet candy Maven project, which you can publish and share with other projects.

## Usage 

*All commands must be executed in the project's directory*

Prerequisite: build the project (see the *How to build* section).

### Initialize a candy project using the `init-project` command

The ``init-project`` command generates a default Maven project with all the appropriate configuration, so that you can place your candy Java API in this project later on. Optionally, you can ask the tool to create and publish the project on Github, assuming that you are part of the ``jsweet-candies`` organization (please contact us to become a member).

For example, the following command creates and publishes to Github a default (empty) candy project on Github for jquery-1.10.0-SNAPSHOT.

```bash
$ java -jar target/candy-tool.jar init-project --artifactId=jquery --version=1.10.0-SNAPSHOT -o ../candies --createGitHubRepository=true --gitHubUser=lgrignon
```

The created project's directory is ``candy-jquery`` and is placed in the ``../candies`` directory, as specified with the ``-o`` option. If you specify the ``createGitHubRepository`` option, an empty Github repository with a default name and description will be created in the [jsweet-candies organization](https://github.com/jsweet-candies). Please contact us to get authorized.

You can create a candy project with dependencies to other projects. For example, the following command creates a project for ``jqueryui-1.11.0-SNAPSHOT``, which depends on the ``jquery-1.10.0-SNAPSHOT`` candy.

```bash
$ java -jar target/candy-tool.jar init-project --artifactId=jqueryui --version=1.11.0-SNAPSHOT --deps=jquery:1.10.0-SNAPSHOT -o ../candies
```

### Generate the candy's Java sources from TypeScript using the `generate-sources` command

The `generate-sources` command parses the given TypeScript definition file and translates it to a Java API (``*.java`` files). You don't need to have an initialized project to run this command. Note that TypeScript definitions can be easily fetched with external tools such as ``typings`` or ``npm``.

```bash
$ java -jar target/candy-tool.jar generate-sources --name=jquery --tsFiles=typings/globals/jquery/index.d.ts -o ../candies/candy-jquery/src/main/java  
```

When a TypeScript definition depends on another definition, should shall specify it as a dependency:

```bash
$ java -jar target/candy-tool.jar generate-sources --name=jqueryui --tsFiles=typings/jqueryui/index.d.ts --tsDeps=typings/jquery/index.d.ts -o ../candies/candy-jqueryui/src/main/java  
```

Note that you can specify multiple definition files in the ``tsFiles`` and ``tsDeps`` options, as comma-separated lists.

### Push your candy to a remote Github repository

Go to your candy's project (here ``candy-jquery``).

```bash
$ git init 
$ git add .
$ git commit -m "Initial commit"
$ git remote add origin https://github.com/jsweet-candies/candy-jquery.git
$ git push origin master 
```

## How to build

In the project's directory, generate TypeScript parser from syntax files (CUP/JFlex):

```bash
$ mvn generate-sources -P genparser
```

Then build the entire project:

```bash
$ mvn clean package
```

Optionally, if you need to re-package the Jar file used to run the tool.

```bash
$ mvn clean compile assembly:single
```

## License

This tool's source code is licensed under GPL, which in short means that you can use is as is to generate any kinds of APIs (including closed-source commercial ones). You can also use/modify this tool's source code in any open source project (commercial or not), as long as you conform to the license terms (see the license file). On the other hand, your cannot embed this tool's source code in a closed-source commercial project. In case you would want to do so, you must contact Renaud Pawlak (renaud.pawlak@gmail.com), who can grant you a commercial license depending on your use case.
