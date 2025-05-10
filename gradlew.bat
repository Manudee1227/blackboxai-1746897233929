@echo off
REM Gradle wrapper batch script
REM This is a minimal wrapper script to invoke Gradle.

set DIRNAME=%~dp0
java -jar "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" %*
