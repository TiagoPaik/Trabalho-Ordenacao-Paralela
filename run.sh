#!/usr/bin/env bash
# Compila e executa o benchmark.
#   ./run.sh            -> configuracao completa
#   ./run.sh --rapido   -> teste rapido
set -e
cd "$(dirname "$0")"
rm -rf out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -Xss64m -Xmx2g -cp out benchmark.Benchmark "$@"
