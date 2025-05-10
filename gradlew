#!/usr/bin/env sh
# Gradle wrapper shell script
# This is a minimal wrapper script to invoke Gradle.

DIRNAME=$(dirname "$0")
exec java -jar "$DIRNAME/gradle/wrapper/gradle-wrapper.jar" "$@"
