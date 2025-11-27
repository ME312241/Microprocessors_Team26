@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup script, version 3.2.0
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
@echo off

@REM set title of command window
title %0

@REM enable echoing by setting MAVEN_BATCH_ECHO to 'on'
@if "%MAVEN_BATCH_ECHO%" == "on" goto echo_header

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%" == "" goto skip_rc_pre

@REM check for pre script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_pre.bat" call "%USERPROFILE%\mavenrc_pre.bat" %*
if exist "%USERPROFILE%\mavenrc_pre.cmd" call "%USERPROFILE%\mavenrc_pre.cmd" %*

:skip_rc_pre

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

if "%JAVA_HOME%" == "" (
  set "MAVEN_JAVA_EXE=%JAVA_EXE%"
) else (
  set "MAVEN_JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

for %%i in (java.exe) do set "JAVA_EXE=%%~$PATH:i"

if exist "%JAVA_EXE%" goto init

if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:init

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set "EXEC_DIR=%CD%"
set "WDIR=%EXEC_DIR%"

:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound

cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set "WDIR=%CD%"
goto findBaseDir

:baseDirNotFound
set "MAVEN_PROJECTBASEDIR=%EXEC_DIR%"
goto endDetectBaseDir

:baseDirFound
set "MAVEN_PROJECTBASEDIR=%WDIR%"
:endDetectBaseDir

IF NOT "%MAVEN_PROJECTBASEDIR%"=="" cd "%MAVEN_PROJECTBASEDIR%"

@REM Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
@REM This allows using the maven wrapper in projects that prohibit checking in binary data.
if not exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
    if not "%MVNW_REPOURL%" == "" (
        call :download "%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
    ) else (
        call :download "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
    )
)

@REM End of extension

@REM Provide a "standardized" way to retrieve the CLI args that will
@REM work with both Windows and non-Windows executions.
set MAVEN_CMD_LINE_ARGS=%*

"%MAVEN_JAVA_EXE%" ^
  %JVM_CONFIG_MAVEN_PROPS% ^
  %MAVEN_OPTS% ^
  %MAVEN_DEBUG_OPTS% ^
  -classpath "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" ^
  "-Dmaven.home=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %WRAPPER_LAUNCHER% org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CONFIG% %MAVEN_CMD_LINE_ARGS%

if ERRORLEVEL 1 goto error
goto end

:download
    set DOWNLOAD_URL=%1
    set DOWNLOAD_FILE=%2
    echo Downloading %DOWNLOAD_URL% to %DOWNLOAD_FILE%
    if not exist "%DOWNLOAD_FILE%" (
        mkdir "%~dp2" 2>nul
        if errorlevel 1 (
            echo Failed to create directory for %DOWNLOAD_FILE%
            goto error
        )
    )
    if exist "%DOWNLOAD_FILE%" (
        echo %DOWNLOAD_FILE% already exists, skipping download
        goto end
    )
    powershell -Command "& {Add-Type -AssemblyName System.Net.Http; $client = New-Object System.Net.Http.HttpClient; $response = $client.GetAsync('%DOWNLOAD_URL%').Result; $content = $response.Content.ReadAsByteArrayAsync().Result; [System.IO.File]::WriteAllBytes('%DOWNLOAD_FILE%', $content); }"
    if errorlevel 1 (
        echo Failed to download %DOWNLOAD_URL%
        goto error
    )
    echo Downloaded %DOWNLOAD_URL% to %DOWNLOAD_FILE%
goto :eof

:echo_header
    echo.
    echo %MAVEN_PROJECTBASEDIR%
    @setlocal
    set "MAVEN_BATCH_ECHO=on"
    @endlocal
    goto init

:error
set ERROR_CODE=1

:end

@REM Execute a user defined script after this one
if not "%MAVEN_SKIP_RC%" == "" goto skip_rc_post

@REM check for post script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_post.bat" call "%USERPROFILE%\mavenrc_post.bat"
if exist "%USERPROFILE%\mavenrc_post.cmd" call "%USERPROFILE%\mavenrc_post.cmd"

:skip_rc_post

@REM pause the script if MAVEN_BATCH_PAUSE is set to 'on'
if "%MAVEN_BATCH_PAUSE%" == "on" pause

if "%MAVEN_BATCH_ECHO%" == "on" echo [DEBUG] exit %ERROR_CODE%

exit /b %ERROR_CODE%