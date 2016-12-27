# JSweet candy generator

This tool is a helper to scaffold & package candy starting from a TypeScript definition file.

## Description

This tools takes a TypeScript definition file and produces the corresponding Java API, conforming to the JSweet core API (see the jsweet-core sub-project).

It comes with a scaffold mode that will generate a default JSweet candy Maven project, which you can publish and share with other projects.

## Usage 

### Start by compiling/assembling the candy-tool running the following: 
```
 mvn clean package
```

### Init the candy project using the `init-project` command:
```
java -jar target\candy-tool.jar init-project --artifactId=jquery --version=1.10.0-SNAPSHOT --deps=jquery:1.10.0-SNAPSHOT -o ../candies
```

This will create a development ready project, bound to the GitHub candies repository and configured for deployment on the candies Maven repository

### Generate the candy's sources using the `generate-sources` command: 

```
java -jar target\candy-tool.jar generate-sources --name=jquery --tsFiles=typings\globals\jquery\index.d.ts -o ../candies/candy-jquery/src/main/java 
```

```
java -jar target\candy-tool.jar generate-sources --name=jquery.ui.datetimepicker --tsFiles=typings\jquery.ui.datetimepicker\jquery.ui.datetimepicker.d.ts --tsDeps=typings\globals\jquery\index.d.ts,typings\jqueryui\jqueryui.d.ts,typings\lib.core\lib.core.d.ts,typings\lib.core\lib.core.ext.d.ts,typings\lib.core\lib.dom.d.ts -o ../candies/candy-jquery/src/main/java 
```

```
java -jar target\candy-tool.jar generate-sources --name=angular-agility --tsFiles=typings\angular-agility\angular-agility.d.ts --tsDeps=typings\angularjs\angular.d.ts,typings\lib.core\lib.core.d.ts,typings\lib.core\lib.core.ext.d.ts,typings\lib.core\lib.dom.d.ts -o ../candies/candy-jquery/src/main/java 
```



*TODO : init git repository automatically + init project from existing sources + use JSweet definitions instead of TS defs for dependencies*

## Regenerate CUP parser
Generate parser from syntax files:
```
mvn generate-sources -P genparser
```

## License

This tool's source code is licensed under GPL, which in short means that you can use/modify it in any open source project (commercial or not), as long as you conform to the license terms. On the other hand, your cannot use this tool's source code in a closed-source commercial project. In case you would want to do so, you must contact Renaud Pawlak (renaud.pawlak@gmail.com), who can grant you a commercial license depending on your use case.
