#!/bin/bash
# Compila e roda todos os testes de aceitação (us1 a us10). Cada usN_1 roda numa
# JVM separada, logo depois do usN, para testar a persistência em arquivo.
set -e
rm -rf bin && mkdir bin
javac -encoding UTF-8 -d bin -cp lib/easyaccept.jar $(find src -name "*.java")
for n in 1 2 3 4 5 6 7 8 9 10; do
  rm -f wepayu.db
  java -cp bin:lib/easyaccept.jar easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests/us$n.txt
  if [ -f tests/us${n}_1.txt ]; then
    java -cp bin:lib/easyaccept.jar easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests/us${n}_1.txt
  fi
done
rm -f wepayu.db folha-*.txt
