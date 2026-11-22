#!/bin/sh
# Builds dist/Timer.app (double-clickable, Java runtime bundled inside).
set -e
cd "$(dirname "$0")"
rm -rf build dist
javac -d build/classes App.java
mkdir -p build/jar
jar --create --file build/jar/Timer.jar --main-class App -C build/classes .
jpackage --type app-image --name Timer --input build/jar --main-jar Timer.jar --dest dist
echo "Built dist/Timer.app"
