@echo off

setLocal EnableDelayedExpansion	

set WORKING_DIR=%cd%

set DIRNAME=%~dp0
cd %DIRNAME%\..

set /p BUILD_PROJECT="compile project? y/(n) "

if "%BUILD_PROJECT%" == "y" (
	echo compile project
	call mvn package -DskipTests
)

cd target

set /p CANDY_NAME="candy name/artifactId? "
set /p CANDY_VERSION="candy (library) version? "
set /p TS_FILES="enter the ts definition files to translate, comma separated "
set /p TS_DEPENDENCY_FILES="enter the ts definition files of the dependencies, comma separated "
set /p OUT_DIR="output directory? default: %cd%/candy-sources "

set CLASSPATH=
for /R %%a in (*.jar) do (
	set CLASSPATH=!CLASSPATH!;%%a
)
echo launch scaffold tool - classpath=!CLASSPATH!
java -cp !CLASSPATH! org.jsweet.CandyScaffoldTool --name=%CANDY_NAME% --version=%CANDY_VERSION% --tsFiles=%TS_FILES% --tsDeps=%TS_DEPENDENCY_FILES% --out=%OUT_DIR%

cd %WORKING_DIR%