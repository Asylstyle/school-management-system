#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes
find src/main/java src/test/java -name '*.java' > build/sources.txt
javac --release 17 -encoding UTF-8 -d build/classes @build/sources.txt
case "${1:-build}" in
  test) java -cp build/classes school.AllTests ;;
  run) java -cp build/classes school.ConsoleApp ;;
  demo) java -cp build/classes school.ConsoleApp --demo ;;
  build) ;;
  *) echo 'Usage: sh build.sh [build|test|run|demo]' >&2; exit 1 ;;
esac
