#!/bin/sh
GRADLE_HOME=/opt/gradle/gradle-8.4
APP_HOME=$( cd "${APP_HOME:-$(dirname "$0")}" > /dev/null && pwd -P )
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec "$GRADLE_HOME/bin/gradle" "$@"
