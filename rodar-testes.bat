@echo off
rem Compila o projeto e roda todos os testes de aceitacao (us1 a us10).
rem Rode este arquivo de dentro da pasta do projeto (onde ficam src, tests e lib).
if exist bin rmdir /s /q bin
mkdir bin
javac -encoding UTF-8 -d bin -cp lib\easyaccept.jar -sourcepath src src\br\ufal\ic\p2\wepayu\Facade.java
if errorlevel 1 (
  echo ERRO DE COMPILACAO
  exit /b 1
)
for %%n in (1 2 3 4 5 6 7 8 9 10) do (
  if exist wepayu.db del wepayu.db
  java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests\us%%n.txt
  if exist tests\us%%n_1.txt java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests\us%%n_1.txt
)
if exist wepayu.db del wepayu.db
echo.
echo Fim. Todas as linhas acima devem terminar em "tests OK".
