# JSweet candy generator

This tool is a helper to scaffold & package candy starting from a TypeScript definition file.

## Description

This tools takes a TypeScript definition file and produces the corresponding Java API, conforming to the JSweet core API (see the jsweet-core sub-project).

It comes with a scaffold mode that will generate a default JSweet candy Maven project, which you can publish and share with other projects.

## Usage 

Start by compiling/assembling the candy-tool running the following: 
```
 mvn clean compile assembly:single
```

To generate the basic sources for your candy, based on a TypeScript definition file, use the scaffold command.

Example:
```
java -jar target\candy-tool.jar scaffold --name=jquery --version=1.10 --tsFiles=typings\globals\jquery\index.d.ts --tsDeps=typings\lib.core\lib.core.d.ts,typings\lib.core\lib.core.ext.d.ts,typings\lib.core\lib.dom.d.ts
```

If you are all set and want to prepare your project for pushing / deploying your JSweet candy  
```
java -jar target\candy-tool.jar init-project --artifactId=jquery --version=1.10.0-SNAPSHOT --deps=jquery:1.10.0-SNAPSHOT -o ../workspace
```

*TODO : init git repository automatically + init project from existing sources*

To prepare a project for your candy, ready to package/deploy, type the following:
```
scripts\package-candy
```

## Regenerate CUP parser
Generate parser from syntax files:
```
mvn generate-sources -P genparser
```

## License

This tool's source code is licensed under GPL, which in short means that you can use/modify it in any open source project (commercial or not), as long as you conform to the license terms. On the other hand, your cannot use this tool's source code in a closed-source commercial project. In case you would want to do so, you must contact Renaud Pawlak (renaud.pawlak@gmail.com), who can grant you a commercial license depending on your use case.
