<div align="center">

[![Instituto Infnet](https://img.shields.io/badge/Instituto-Infnet-red?style=for-the-badge)](https://www.infnet.edu.br)
[![Curso](https://img.shields.io/badge/Curso-Engenharia_de_Software-blue?style=for-the-badge)](https://www.infnet.edu.br)
[![Disciplina](https://img.shields.io/badge/Disciplina-Refatoração_(DR4)-green?style=for-the-badge)](https://www.infnet.edu.br)

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue?logo=apachemaven)](https://maven.apache.org)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![JUnit](https://img.shields.io/badge/JUnit-5-green?logo=junit5&logoColor=white)](https://junit.org/junit5)
[![Jqwik](https://img.shields.io/badge/Jqwik-1.9.2-purple)](https://jqwik.net)
[![JaCoCo](https://img.shields.io/badge/Coverage-JaCoCo-red?logo=codecov&logoColor=white)](https://www.jacoco.org)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?logo=readme&logoColor=white)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Completo-success)](https://github.com/andrebecker84/refactoringDR4_TP2)

</div>

# Build Pipeline Refactoring Kata

## Trabalho Prático 2 — DR4 Refatoração

> **Refatoração do BuildPipeline Kata aplicando Strategy Pattern, SOLID e testes automatizados sobre um sistema legado Java**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-@becker84-0077B5?logo=linkedin)](https://linkedin.com/in/becker84)
[![GitHub](https://img.shields.io/badge/GitHub-@andrebecker84-181717?logo=github&logoColor=white)](https://github.com/andrebecker84)

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Problema Identificado](#problema-identificado)
- [Solução Implementada](#solução-implementada)
- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Como Executar](#como-executar)
- [Testes](#testes)
- [Decisões Técnicas](#decisões-técnicas)
- [Documentação Completa](#documentação-completa)

---

## Sobre o Projeto

Refatoração do [BuildPipeline Refactoring Kata (Emily Bache)](https://github.com/emilybache/BuildPipeline-Refactoring-Kata) aplicando SOLID, Strategy Pattern, TDD e princípios de Clean Code sobre um sistema legado Java com sérios problemas de coesão e clareza.

O código original concentrava toda a lógica de um pipeline de build em um único método `run()` com aproximadamente 40 linhas, três responsabilidades distintas, Primitive Obsession, magic strings e condicionais aninhadas. A refatoração decompõe esse método em unidades coesas, testáveis e extensíveis, sem alterar o comportamento externo.

---

## Problema Identificado

O método `run()` da classe `Pipeline` original apresentava os seguintes bad smells:

| Bad Smell | Localização | Impacto |
|---|---|---|
| Long Method | `Pipeline.run()` — 40 linhas, 3 responsabilidades | Impossível testar cada fase isoladamente |
| Primitive Obsession | Strings `"success"` / `"failure"` como status | Typo silencioso quebra comportamento sem erro de compilação |
| Magic Strings | Literais de e-mail embutidos em condicionais | Nenhuma centralização ou rastreabilidade |
| Multiple Responsibilities | SRP violado — `Pipeline` faz testes, deploy e notificação | Alta rigidez, baixa coesão |
| Deep Nesting | Condicionais aninhadas em 3 níveis | Difícil de seguir e de estender |

---

## Solução Implementada

**Padrão aplicado:** Strategy Pattern — cada etapa do pipeline (`EtapaTeste`, `EtapaDeploy`, `EtapaNotificacao`) implementa o contrato `EtapaExecucao` e pode ser substituída ou estendida sem modificar o orquestrador.

**Métricas de refatoração:**
- Método `run()` original: ~40 linhas, 3 responsabilidades
- Método `executar()` refatorado: 3 linhas, delegação explícita
- Testes: 9 determinísticos (JUnit 5 + Mockito) + 5 propriedades (Jqwik)

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Maven | 3.9+ | Build e dependências |
| Spring Boot | 3.2.3 | Framework de aplicação |
| JUnit 5 | — | Testes determinísticos |
| Hamcrest | — | Assertions expressivas |
| Mockito | — | Mocks e verificações |
| Jqwik | 1.9.2 | Testes baseados em propriedades |
| JaCoCo | 0.8.12 | Cobertura de código |

---

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

---

## Como Executar

**Windows:** `run.bat`
**Linux/macOS:** `chmod +x run.sh && ./run.sh`

Menu interativo com compilação, testes, cobertura JaCoCo e JavaDoc.

```bash
# Comandos Maven diretos
mvn clean verify        # build completo com cobertura
mvn test                # apenas testes
# Relatório JaCoCo: target/site/jacoco/index.html
```

---

## Testes

### Testes Determinísticos

9 testes em `PipelineOrquestradorTest` com JUnit 5 + Mockito + Hamcrest:

| Cenário | Resultado esperado |
|---|---|
| Projeto sem testes + deploy OK + notificação ON | Email de sucesso |
| Projeto com testes passando + deploy OK | Email de sucesso |
| Testes falhando + notificação ON | Email de falha, sem deploy |
| Testes OK + deploy falhando | Email de falha |
| Notificação desabilitada (testes OK) | Nenhum email |
| Notificação desabilitada (testes falhos) | Nenhum email |
| Projeto sem testes | Registra ausência no log |
| Testes falhos | Não registra erro de deploy |
| Nome de projeto vazio | `IllegalArgumentException` |

### Testes Baseados em Propriedades

5 propriedades em `PipelinePropriedadesTest` com Jqwik, validando invariantes para toda combinação de entrada:

| Propriedade | Invariante |
|---|---|
| `notificacaoDesabilitada_nuncaEnviaMensagem` | Nenhum envio para qualquer combinação de estados |
| `notificacaoHabilitada_semprEnviaExatamenteUmaMensagem` | Sempre exatamente 1 mensagem |
| `testesFalham_mensagemEnviadaNuncaIndicaSucesso` | Falha de teste → mensagem nunca indica sucesso |
| `deployFalha_mensagemEnviadaNuncaIndicaSucesso` | Falha de deploy → mensagem nunca indica sucesso |
| `testesPassam_deployPassam_mensagemSempre_indicaSucesso` | Fluxo completo → sempre notifica sucesso |

---

## Decisões Técnicas

- **Strategy Pattern** — `EtapaExecucao` como contrato; `EtapaTeste` e `EtapaDeploy` são intercambiáveis, respeitando LSP e OCP
- **Orquestrador separado** — `PipelineOrquestrador` conhece apenas a sequência; não sabe o que cada etapa faz internamente
- **Enum `StatusExecucao`** — substitui as Strings `"success"`/`"failure"` do original; o compilador garante valores válidos
- **`Configuracao` tipada** — parâmetros booleanos descritivos em vez de Strings primitivas
- **Fail-Fast em `Projeto`** — validação no construtor impede propagação de objetos inválidos

---

## Documentação Completa

Análise detalhada de cada refatoração aplicada, bad smells identificados, princípios SOLID e comparação antes/depois disponível em:

[`doc/TP2_RELATORIO.md`](doc/TP2_RELATORIO.md)

---

<div align="center">

<p><strong>Desenvolvido como Trabalho Prático da disciplina de Engenharia de Software com foco em Refatoração.</strong></p>

<p>
  <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Made%20with-Java-orange?logo=openjdk" alt="Java"></a>
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Built%20with-Maven-blue?logo=apachemaven" alt="Maven"></a>
  <a href="https://junit.org/junit5/"><img src="https://img.shields.io/badge/Tested%20with-JUnit-green?logo=junit5&logoColor=white" alt="JUnit"></a>
</p>

<a href="doc/images/card.svg">
  <img src="doc/images/card.svg" width="360" alt="André Luis Becker - Software Engineer">
</a>

<p><em>Instituto Infnet — Engenharia de Software — 2026.</em></p>

<p>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow.svg?logo=readme&logoColor=white" alt="MIT License"></a>
</p>

</div>