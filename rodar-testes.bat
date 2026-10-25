@echo off
setlocal EnableExtensions EnableDelayedExpansion

echo ==========================================
echo       WE PAY U - TESTES DE ACEITACAO
echo ==========================================
echo.

rem ==========================================
rem LIMPEZA E COMPILACAO
rem ==========================================

if exist bin rmdir /s /q bin
mkdir bin

if exist wepayu.db del /q wepayu.db

set "SOURCES_FILE=.java-sources.tmp"

if exist "%SOURCES_FILE%" del /q "%SOURCES_FILE%"

rem Coleta TODOS os arquivos Java do projeto.
rem Converte \ para / para evitar problemas no arquivo
rem de argumentos utilizado pelo javac no Windows.
for /r "src" %%f in (*.java) do (
    set "SOURCE=%%~ff"
    set "SOURCE=!SOURCE:\=/!"
    echo "!SOURCE!">>"%SOURCES_FILE%"
)

echo Compilando todos os arquivos Java...
echo.

javac -Xlint:all -encoding UTF-8 -d bin -cp "lib\easyaccept.jar" @"%SOURCES_FILE%"

set "COMPILE_STATUS=%ERRORLEVEL%"

if exist "%SOURCES_FILE%" del /q "%SOURCES_FILE%"

if not "%COMPILE_STATUS%"=="0" (
    echo.
    echo ==========================================
    echo ERRO DE COMPILACAO
    echo ==========================================
    exit /b %COMPILE_STATUS%
)

echo ==========================================
echo COMPILACAO OK
echo ==========================================
echo.

rem ==========================================
rem TESTES US1 A US10
rem ==========================================

for %%n in (1 2 3 4 5 6 7 8 9 10) do (

    rem Cada US principal comeca com banco limpo.
    if exist wepayu.db del /q wepayu.db

    echo ==========================================
    echo US%%n
    echo ==========================================

    java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade "tests\us%%n.txt"

    if errorlevel 1 (
        echo.
        echo ERRO DURANTE US%%n
        exit /b !ERRORLEVEL!
    )

    rem O _1 deve rodar logo depois, sem apagar o banco,
    rem porque verifica persistencia entre execucoes.
    if exist "tests\us%%n_1.txt" (
        echo.
        echo ==========================================
        echo US%%n_1
        echo ==========================================

        java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade "tests\us%%n_1.txt"

        if errorlevel 1 (
            echo.
            echo ERRO DURANTE US%%n_1
            exit /b !ERRORLEVEL!
        )
    )

    echo.
)

rem ==========================================
rem LIMPEZA FINAL
rem ==========================================

if exist wepayu.db del /q wepayu.db
if exist folha-*.txt del /q folha-*.txt

echo ==========================================
echo TODOS OS TESTES FORAM EXECUTADOS
echo ==========================================
echo.
echo Todas as linhas de teste acima devem terminar em "tests OK".
echo Resultado esperado: 786 testes OK no total.
echo.

exit /b 0