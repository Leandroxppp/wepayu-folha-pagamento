#!/bin/bash
# Compila TODOS os fontes e roda os testes de aceitacao (us1 a us10). Cada
# usN_1 roda numa JVM separada, logo depois do usN, para testar persistencia.
set -euo pipefail

rm -rf bin
mkdir bin
sources_file=".java-sources.tmp"
trap 'rm -f "$sources_file" wepayu.db folha-*.txt' EXIT
find src -name '*.java' -print | sort > "$sources_file"
javac -encoding UTF-8 -d bin -cp lib/easyaccept.jar @"$sources_file"

for n in 1 2 3 4 5 6 7 8 9 10; do
  rm -f wepayu.db
  java -cp bin:lib/easyaccept.jar easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade "tests/us$n.txt"
  if [ -f "tests/us${n}_1.txt" ]; then
    java -cp bin:lib/easyaccept.jar easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade "tests/us${n}_1.txt"
  fi
done

echo
echo 'Fim. Todas as linhas acima devem terminar em "tests OK".'
