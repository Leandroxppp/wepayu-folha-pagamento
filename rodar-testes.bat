@echo off
setlocal EnableExtensions EnableDelayedExpansion
rem Compila TODOS os fontes e roda os testes de aceitacao (us1 a us10).
rem Cada usN_1 roda em outra JVM, logo depois do usN, para testar persistencia.

if exist bin rmdir /s /q bin
mkdir bin

set "SOURCES_FILE=.java-sources.tmp"
if exist "%SOURCES_FILE%" del "%SOURCES_FILE%"
for /r "src" %%f in (*.java) do echo "%%f">>"%SOURCES_FILE%"

javac -encoding UTF-8 -d bin -cp lib\easyaccept.jar @"%SOURCES_FILE%"
set "COMPILE_STATUS=%ERRORLEVEL%"
del "%SOURCES_FILE%" >nul 2>&1
if not "%COMPILE_STATUS%"=="0" (
  echo.
  echo ERRO DE COMPILACAO
  exit /b %COMPILE_STATUS%
)

for %%n in (1 2 3 4 5 6 7 8 9 10) do (
  if exist wepayu.db del wepayu.db
  java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests\us%%n.txt
  if errorlevel 1 exit /b !ERRORLEVEL!
  if exist tests\us%%n_1.txt (
    java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests\us%%n_1.txt
    if errorlevel 1 exit /b !ERRORLEVEL!
  )
)

if exist wepayu.db del wepayu.db
if exist folha-*.txt del folha-*.txt

echo.
echo Fim. Todas as linhas acima devem terminar em "tests OK".
exit /b 0
