@echo off
chcp 65001 >nul 2>nul
cls
goto MENU

:MENU
cls
echo(
echo ========================================
echo Build Pipeline Refactoring - DR4 TP2
echo Java 21 . Maven . JUnit 5 . Jqwik
echo ========================================
echo(
echo 1 - Verificar Pre-requisitos
echo 2 - Compilar Projeto
echo 3 - Testes e Cobertura (JaCoCo)
echo 4 - Abrir Relatorio JaCoCo
echo 5 - Gerar JavaDoc
echo 6 - Abrir Documentacao
echo 7 - Limpar Build
echo 0 - Sair
echo(
set /p "opcao= Opcao: "

if "%opcao%"=="1" goto PREREQ
if "%opcao%"=="2" goto COMPILAR
if "%opcao%"=="3" goto TESTES
if "%opcao%"=="4" goto RELATORIO
if "%opcao%"=="5" goto JAVADOC
if "%opcao%"=="6" goto DOCS
if "%opcao%"=="7" goto LIMPAR
if "%opcao%"=="0" goto FIM

echo(
echo Opcao invalida.
timeout /t 2 >nul
goto MENU

:PREREQ
cls
echo(
echo --- Verificando Pre-requisitos ---
echo(
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
 echo [X] Java nao encontrado. Baixe em: https://adoptium.net/
) else (
 echo [OK] Java:
 for /f "delims=" %%i in ('java -version 2^>^&1 ^| findstr "version"') do echo %%i
)
echo(
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
 echo [X] Maven nao encontrado. Baixe em: https://maven.apache.org/download.cgi
) else (
 echo [OK] Maven:
 for /f "delims=" %%i in ('mvn -version 2^>^&1 ^| findstr "Apache Maven"') do echo %%i
)
echo(
pause
goto MENU

:TESTES
cls
echo(
echo --- Executando Testes com Cobertura ---
echo(
call mvn clean test
echo(
echo --- Gerando Relatorio JaCoCo ---
echo(
call mvn jacoco:report
if exist target\\site\\jacoco\\index.html (
 echo(
 echo Relatorio gerado em: target\\site\\jacoco\\index.html
)
echo(
pause
goto MENU

:COMPILAR
cls
echo(
echo --- Compilando Projeto ---
echo(
call mvn clean package -DskipTests
if exist target\\pipeline-refactoring-1.0.0.jar (
 echo(
 echo JAR gerado em: target\\pipeline-refactoring-1.0.0.jar
)
echo(
pause
goto MENU

:LIMPAR
cls
echo(
echo --- Limpando Build ---
echo(
call mvn clean
echo(
pause
goto MENU

:JAVADOC
cls
echo(
echo --- Gerando JavaDoc ---
echo(
call mvn javadoc:javadoc
if exist target\\site\\apidocs\\index.html (
 echo(
 echo JavaDoc gerado em: target\\site\\apidocs\\index.html
)
echo(
pause
goto MENU

:RELATORIO
cls
echo(
if exist target\\site\\jacoco\\index.html (
 echo Abrindo relatorio JaCoCo...
 start "" target\\site\\jacoco\\index.html
) else (
 echo Relatorio nao encontrado. Execute os testes primeiro [opcao 3].
)
echo(
pause
goto MENU

:DOCS
cls
echo(
if exist doc\\TP2_RELATORIO.md (
 echo Abrindo documentacao...
 start "" doc\\TP2_RELATORIO.md
) else (
 echo Arquivo doc\\TP2_RELATORIO.md nao encontrado.
)
echo(
pause
goto MENU

:FIM
echo(
echo Encerrando.
echo(
exit /b 0