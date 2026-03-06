# Build Pipeline Refactoring Kata — DR4 TP2

Refatoração do [BuildPipeline Refactoring Kata (Emily Bache)](https://github.com/emilybache/BuildPipeline-Refactoring-Kata) aplicando SOLID, Strategy Pattern, TDD e princípios de Clean Code sobre um sistema legado Java com sérios problemas de coesão e clareza.

## Visão Geral

O código original concentrava toda a lógica de um pipeline de build em um único método `run()` com aproximadamente 40 linhas, três responsabilidades distintas, Primitive Obsession, magic strings e condicionais aninhadas. A refatoração decompõe esse método em unidades coesas, testáveis e extensíveis, sem alterar o comportamento externo.

Documentação técnica completa disponível em `doc/TP2_RELATORIO.md`.

## Tecnologias

**Java 21** / Maven 3.9+ / Spring Boot 3.2.3 / JUnit 5 / Hamcrest / Jqwik 1.9.2 / JaCoCo 0.8.12 / Mockito

## Arquitetura

**Padrão aplicado:** Strategy Pattern — cada etapa do pipeline (`EtapaTeste`, `EtapaDeploy`, `EtapaNotificacao`) implementa o contrato `EtapaExecucao` e pode ser substituída ou estendida sem modificar o orquestrador.

**Métricas de refatoração:**
- Método `run()` original: ~40 linhas, 3 responsabilidades
- Método `executar()` refatorado: 3 linhas, delegação explícita
- Testes: 9 determinísticos (JUnit 5 + Mockito) + 5 propriedades (Jqwik)

## Estrutura do Projeto

```
src/main/java/br/com/andrebecker/pipeline/
├── original/                     # Código original preservado intacto
│   ├── Pipeline.java             # Bad smells documentados em comentário
│   ├── Config.java
│   ├── Emailer.java
│   ├── Logger.java
│   └── Project.java
├── dominio/
│   ├── StatusExecucao.java       # Enum — elimina Primitive Obsession
│   ├── modelo/
│   │   └── Projeto.java          # Value object com construtores nomeados
│   └── etapa/
│       ├── EtapaExecucao.java    # Strategy: contrato das etapas
│       ├── EtapaTeste.java       # Responsabilidade única: executar testes
│       ├── EtapaDeploy.java      # Responsabilidade única: executar deploy
│       └── EtapaNotificacao.java # Responsabilidade única: notificar resultado
├── configuracao/
│   └── Configuracao.java         # Configuração tipada — sem magic strings
├── infraestrutura/
│   ├── Registrador.java          # Interface de logging
│   ├── Notificador.java          # Interface de notificação
│   ├── RegistradorConsole.java
│   └── NotificadorConsole.java
├── PipelineOrquestrador.java     # Coordena as etapas do pipeline
└── RefactoringDR4TP2Application.java

src/test/java/br/com/andrebecker/pipeline/
├── PipelineOrquestradorTest.java    # 9 testes determinísticos
└── PipelinePropriedadesTest.java    # 5 propriedades Jqwik
```

## Execução Rápida

**Windows:** `run.bat`
**Linux/macOS:** `chmod +x run.sh && ./run.sh`

Menu interativo com compilação, testes, cobertura JaCoCo e JavaDoc.

```bash
# Comandos Maven diretos
mvn clean verify        # build completo com cobertura
mvn test                # apenas testes
# Relatório JaCoCo: target/site/jacoco/index.html
```

## Decisões de Design

- **Strategy Pattern** — `EtapaExecucao` como contrato; `EtapaTeste` e `EtapaDeploy` são intercambiáveis, respeitando LSP e OCP
- **Orquestrador separado** — `PipelineOrquestrador` conhece apenas a sequência; não sabe o que cada etapa faz internamente
- **Enum `StatusExecucao`** — substitui as Strings `"success"`/`"failure"` do original; o compilador garante valores válidos
- **`Configuracao` tipada** — parâmetros booleanos descritivos em vez de Strings primitivas
- **Fail-Fast em `Projeto`** — validação no construtor impede propagação de objetos inválidos

## Autor

André Luis Becker — 2026