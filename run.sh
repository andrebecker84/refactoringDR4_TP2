#!/bin/bash

menu() {
  clear
  echo ""
  echo " ========================================"
  echo " Build Pipeline Refactoring - DR4 TP2"
  echo " Java 21 · Maven · JUnit 5 · Jqwik"
  echo " ========================================"
  echo ""
  echo " 1 - Verificar Pre-requisitos"
  echo " 2 - Compilar Projeto"
  echo " 3 - Testes e Cobertura (JaCoCo)"
  echo " 4 - Abrir Relatorio JaCoCo"
  echo " 5 - Gerar JavaDoc"
  echo " 6 - Abrir Documentacao"
  echo " 7 - Limpar Build"
  echo " 0 - Sair"
  echo ""
  read -rp " Opcao: " opcao

  case $opcao in
    1) prereq ;;
    2) compilar ;;
    3) testes ;;
    4) relatorio ;;
    5) javadoc ;;
    6) docs ;;
    7) limpar ;;
    0) echo "" && echo " Encerrando." && echo "" && exit 0 ;;
    *) echo "" && echo " Opcao invalida." && sleep 2 && menu ;;
  esac
}

abrir() {
  if [[ "$OSTYPE" == "darwin"* ]]; then
    open "$1"
  elif command -v xdg-open &>/dev/null; then
    xdg-open "$1" 2>/dev/null
  elif command -v wslview &>/dev/null; then
    wslview "$1"
  else
    echo " Abra manualmente: $1"
  fi
}

prereq() {
  clear
  echo ""
  echo " --- Verificando Pre-requisitos ---"
  echo ""
  if command -v java &>/dev/null; then
    echo " [OK] Java:"
    java -version 2>&1 | head -1 | sed 's/^/ /'
  else
    echo " [X] Java nao encontrado. Baixe em: https://adoptium.net/"
  fi
  echo ""
  if command -v mvn &>/dev/null; then
    echo " [OK] Maven:"
    mvn -version 2>&1 | head -1 | sed 's/^/ /'
  else
    echo " [X] Maven nao encontrado. Baixe em: https://maven.apache.org/download.cgi"
  fi
  echo ""
  read -rp " Pressione ENTER para voltar..." && menu
}

testes() {
  clear
  echo ""
  echo " --- Executando Testes com Cobertura ---"
  echo ""
  mvn clean test
  echo ""
  echo " --- Gerando Relatorio JaCoCo ---"
  echo ""
  mvn jacoco:report
  if [ -f "target/site/jacoco/index.html" ]; then
    echo ""
    echo " Relatorio gerado em: target/site/jacoco/index.html"
  fi
  echo ""
  read -rp " Pressione ENTER para voltar..." && menu
}

compilar() {
  clear
  echo ""
  echo " --- Compilando Projeto ---"
  echo ""
  mvn clean package -DskipTests
  if [ -f "target/pipeline-refactoring-1.0.0.jar" ]; then
    echo ""
    echo " JAR gerado em: target/pipeline-refactoring-1.0.0.jar"
  fi
  echo ""
  read -rp " Pressione ENTER para voltar..." && menu
}

limpar() {
  clear
  echo ""
  echo " --- Limpando Build ---"
  echo ""
  mvn clean
  echo ""
  read -rp " Pressione ENTER para voltar..." && menu
}

javadoc() {
  clear
  echo ""
  echo " --- Gerando JavaDoc ---"
  echo ""
  mvn javadoc:javadoc
  if [ -f "target/site/apidocs/index.html" ]; then
    echo ""
    echo " JavaDoc gerado em: target/site/apidocs/index.html"
  fi
  echo ""
  read -rp " Pressione ENTER para voltar..." && menu
}

relatorio() {
  clear
  echo ""
  if [ -f "target/site/jacoco/index.html" ]; then
    echo " Abrindo relatorio JaCoCo..."
    abrir "target/site/jacoco/index.html"
  else
    echo " Relatorio nao encontrado. Execute os testes primeiro (opcao 3)."
  fi
  echo ""
  read -rp " Pressione ENTER para voltar..." && menu
}

docs() {
  clear
  echo ""
  if [ -f "doc/TP2_RELATORIO.md" ]; then
    echo " Abrindo documentacao..."
    abrir "doc/TP2_RELATORIO.md"
  else
    echo " Arquivo doc/TP2_RELATORIO.md nao encontrado."
  fi
  echo ""
  read -rp " Pressione ENTER para voltar..." && menu
}

menu