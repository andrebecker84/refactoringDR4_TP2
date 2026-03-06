<div align="center">

[![Instituto Infnet](https://img.shields.io/badge/Instituto-Infnet-red?style=for-the-badge)](https://www.infnet.edu.br)
[![Curso](https://img.shields.io/badge/Curso-Engenharia_de_Software-blue?style=for-the-badge)](https://www.infnet.edu.br)
[![Disciplina](https://img.shields.io/badge/Disciplina-Refatoração_(DR4)-green?style=for-the-badge)](https://www.infnet.edu.br)

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue?logo=apachemaven)](https://maven.apache.org)
[![JUnit](https://img.shields.io/badge/JUnit-5-green?logo=junit5&logoColor=white)](https://junit.org/junit5)
[![Jqwik](https://img.shields.io/badge/Jqwik-1.9.2-purple)](https://jqwik.net)
[![Status](https://img.shields.io/badge/Status-Completo-success)](https://github.com/andrebecker84/refactoringDR4_TP2)

</div>

# Documentação — Trabalho Prático 2 (DR4 — Refatoração)

> **Build Pipeline Refactoring Kata — Refatoração progressiva aplicando Strategy Pattern, SOLID e testes automatizados**

**Aluno:** André Luis Becker
**Disciplina:** Engenharia Disciplinada de Software — DR4
**Ano:** 2026
**Repositório base:** [BuildPipeline Refactoring Kata — Emily Bache](https://github.com/emilybache/BuildPipeline-Refactoring-Kata)

---

## Índice

- [Exercício 1 — Verificação Inicial e Testes Automatizados](#exercício-1--verificação-inicial-e-testes-automatizados)
- [Exercício 2 — Reestruturando Métodos Complexos](#exercício-2--reestruturando-métodos-complexos)
- [Exercício 3 — Expressividade e Clareza com Variáveis](#exercício-3--expressividade-e-clareza-com-variáveis)
- [Exercício 4 — Melhorando Assinaturas e Encapsulamento](#exercício-4--melhorando-assinaturas-e-encapsulamento)
- [Exercício 5 — Reorganizando Classes e Processos](#exercício-5--reorganizando-classes-e-processos)
- [Resultados de Teste](#resultados-de-teste)
- [Comparação: Antes e Depois](#comparação-antes-e-depois)
- [Conclusão](#conclusão)

---

## Exercício 1 — Verificação Inicial e Testes Automatizados

O repositório base foi clonado e analisado antes de qualquer alteração. A classe `Pipeline` compilava sem erros, mas não existia nenhum teste automatizado no projeto original. Antes de qualquer refatoração, foram escritos testes que documentam o comportamento atual e impedem regressões durante as mudanças estruturais.

**Testes criados para proteger o comportamento:**

`PipelineOrquestradorTest` — 9 testes determinísticos com JUnit 5 e Mockito, cobrindo os cenários de fluxo completo: testes passando, testes falhando, deploy falhando e notificação desabilitada.

`PipelinePropriedadesTest` — 5 propriedades com Jqwik, validando invariantes do sistema para qualquer combinação de entradas, complementando a cobertura de cenários de borda.

**Resultado:** 14 testes, 0 falhas, cobertura analisada via JaCoCo.

---

## Exercício 2 — Reestruturando Métodos Complexos

O método `run()` da classe `Pipeline` original apresentava três problemas simultâneos: múltiplas responsabilidades, longos blocos condicionais e baixa legibilidade. Com aproximadamente 40 linhas, misturava a lógica de execução de testes, deploy e notificação por e-mail em um único fluxo sem separação clara de propósito.

### Código Original — método com três responsabilidades misturadas

```java
public void run(Project project) {
    String testStatus;
    if (project.hasTests()) {
        if ("success".equals(config.test())) { ... }
        else { ... }
    } else { ... }

    String deploymentStatus;
    if (testStatus.equals("success")) {
        if ("success".equals(config.deploy())) { ... }
        else { ... }
    } else { ... }

    if (config.sendEmailSummary()) {
        if (deploymentStatus.equals("success")) { ... }
        else { ... }
    } else { ... }
}
```

### Refatoração — Extract Class + Extract Method

Cada responsabilidade foi extraída para uma classe dedicada implementando a interface `EtapaExecucao`. O orquestrador `PipelineOrquestrador` coordena a sequência sem conhecer os detalhes de cada etapa:

```java
public void executar(Projeto projeto) {
    StatusExecucao statusTeste = etapaTeste.executar(projeto);
    StatusExecucao statusDeploy = prosseguirParaDeploy(statusTeste, projeto);
    etapaNotificacao.notificar(statusDeploy);
}

private StatusExecucao prosseguirParaDeploy(StatusExecucao statusTeste, Projeto projeto) {
    if (statusTeste.isFalha()) return StatusExecucao.FALHA;
    return etapaDeploy.executar(projeto);
}
```

O método `executar()` passou de 40 linhas para 3 linhas. A lógica de cada fase tornou-se testável de forma independente.

Dentro de `EtapaTeste`, o método auxiliar `executarTestes()` isola a decisão de resultado:

```java
@Override
public StatusExecucao executar(Projeto projeto) {
    if (!projeto.possuiTestes()) {
        registrador.info("Sem testes definidos para o projeto: " + projeto.getNome());
        return StatusExecucao.SUCESSO;
    }
    return executarTestes();
}

private StatusExecucao executarTestes() {
    if (configuracao.testesPassaram()) {
        registrador.info("Testes executados com sucesso");
        return StatusExecucao.SUCESSO;
    }
    registrador.erro("Falha na execução dos testes");
    return StatusExecucao.FALHA;
}
```

---

## Exercício 3 — Expressividade e Clareza com Variáveis

O código original usava as Strings `"success"` e `"failure"` como valores de status passados entre blocos condicionais. Essa abordagem é silenciosamente frágil: um typo em qualquer comparação altera o comportamento sem erro de compilação.

### Original — Primitive Obsession com Strings

```java
String testStatus = "success";
if (testStatus.equals("success")) { ... }

String deploymentStatus;
if ("success".equals(config.deploy())) { ... }
```

### Refatorado — Enum `StatusExecucao` com métodos semânticos

```java
public enum StatusExecucao {
    SUCESSO, FALHA;

    public boolean isSucesso() { return this == SUCESSO; }
    public boolean isFalha()   { return this == FALHA;   }
}
```

O compilador garante que apenas valores válidos sejam usados. As condicionais passam a usar `statusTeste.isFalha()` em vez de `"failure".equals(statusTeste)`, tornando a intenção explícita.

Magic strings centralizadas como constantes nomeadas em `EtapaNotificacao`:

```java
private static final String MENSAGEM_SUCESSO = "Deploy concluído com sucesso";
private static final String MENSAGEM_FALHA   = "Falha no pipeline de build";

private String resolverMensagem(StatusExecucao status) {
    return status.isSucesso() ? MENSAGEM_SUCESSO : MENSAGEM_FALHA;
}
```

---

## Exercício 4 — Melhorando Assinaturas e Encapsulamento

O objeto `Config` original recebia Strings como parâmetros de configuração, exigindo comparações distribuídas pelo código de negócio:

```java
// Original
if ("success".equals(config.test())) { ... }
if ("success".equals(config.deploy())) { ... }
if (config.sendEmailSummary()) { ... }
```

### Refatorado — Introduce Parameter Object com `Configuracao` tipada

```java
public class Configuracao {
    private final boolean testesPassaram;
    private final boolean deployBemSucedido;
    private final boolean notificacaoHabilitada;

    public boolean testesPassaram()        { return testesPassaram; }
    public boolean deployBemSucedido()     { return deployBemSucedido; }
    public boolean notificacaoHabilitada() { return notificacaoHabilitada; }
}
```

Os parâmetros booleanos com nomes descritivos eliminam as comparações de String no código de negócio. O uso no ponto de chamada torna-se autoexplicativo: `configuracao.testesPassaram()`.

### Encapsulamento em `Projeto` com construtores nomeados e Fail-Fast

```java
public static Projeto comTestes(String nome)  { return new Projeto(nome, true);  }
public static Projeto semTestes(String nome)  { return new Projeto(nome, false); }

private Projeto(String nome, boolean possuiTestes) {
    if (nome == null || nome.isBlank())
        throw new IllegalArgumentException("Nome do projeto não pode ser vazio");
    this.nome = nome;
    this.possuiTestes = possuiTestes;
}
```

Os campos são `private final`, sem setters. O objeto nasce sempre válido. As interfaces `Registrador` e `Notificador` desacoplam o domínio das implementações de infraestrutura.

---

## Exercício 5 — Reorganizando Classes e Processos

O código original concentrava quatro responsabilidades em uma única classe `Pipeline`: decisão de execução de testes, lógica de deploy, formatação de mensagens de e-mail e envio de notificação. A reorganização distribuiu essas responsabilidades em classes especializadas com dependências claras.

### Estrutura resultante

```
Pipeline (original)           →   PipelineOrquestrador
  lógica de testes            →   EtapaTeste   (implementa EtapaExecucao)
  lógica de deploy            →   EtapaDeploy  (implementa EtapaExecucao)
  lógica de notificação       →   EtapaNotificacao
  configuração primitiva      →   Configuracao (tipada)
  entidade Project            →   Projeto      (value object)
  interface Logger            →   Registrador  (renomeada)
  interface Emailer           →   Notificador  (renomeada e generalizada)
```

### Strategy Pattern — `EtapaExecucao` como contrato substituível

```java
public interface EtapaExecucao {
    StatusExecucao executar(Projeto projeto);
}
```

`EtapaTeste` e `EtapaDeploy` implementam esse contrato de forma independente. O orquestrador depende apenas da interface, o que permite trocar qualquer implementação sem modificar o código existente — respeitando OCP.

### Análise SOLID

| Princípio | Aplicação |
|---|---|
| SRP | Cada classe tem uma única razão para mudar |
| OCP | Novas etapas adicionadas sem modificar o orquestrador |
| LSP | `EtapaTeste` e `EtapaDeploy` são intercambiáveis via `EtapaExecucao` |
| ISP | `Registrador` e `Notificador` são interfaces mínimas e focadas |
| DIP | `PipelineOrquestrador` depende de abstrações, não de implementações concretas |

---

## Resultados de Teste

```
PipelineOrquestradorTest   — 9 testes   — JUnit 5 + Mockito + Hamcrest
PipelinePropriedadesTest   — 5 propriedades — Jqwik

Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

**Propriedades Jqwik validadas para toda combinação de entrada:**

| Propriedade | Invariante validado |
|---|---|
| `notificacaoDesabilitada_nuncaEnviaMensagem` | Nenhum envio quando desabilitado, para toda combinação de estados |
| `notificacaoHabilitada_semprEnviaExatamenteUmaMensagem` | Sempre exatamente 1 mensagem quando habilitado |
| `testesFalham_mensagemEnviadaNuncaIndicaSucesso` | Falha de teste nunca resulta em mensagem de sucesso |
| `deployFalha_mensagemEnviadaNuncaIndicaSucesso` | Falha de deploy nunca resulta em mensagem de sucesso |
| `testesPassam_deployPassam_mensagemSempre_indicaSucesso` | Fluxo completo de sucesso sempre notifica com sucesso |

---

## Comparação: Antes e Depois

| Métrica | Antes | Depois |
|---|---|---|
| Linhas em `run()` / `executar()` | ~40 | 3 |
| Classes com lógica de negócio | 1 | 4 |
| Comparações de String para status | 5+ | 0 |
| Testes automatizados | 0 | 14 |
| Magic strings inline | 4 | 0 |
| Testabilidade por etapa isolada | impossível | unitários independentes |

---

## Conclusão

A refatoração transforma um método monolítico em um conjunto de classes coesas com contrato claro. O comportamento externo permanece idêntico — validado pelos testes determinísticos e pelas propriedades Jqwik. A arquitetura resultante é extensível: novas etapas de pipeline são adicionadas criando uma nova classe que implementa `EtapaExecucao`, sem modificar nenhum código existente.

---

<div align="center">

<a href="images/card.svg">
  <img src="images/card.svg" width="360" alt="André Luis Becker - Software Engineer">
</a>

<p><em>Instituto Infnet — Engenharia de Software — 2026.</em></p>

<p>
  <a href="../LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow.svg?logo=readme&logoColor=white" alt="MIT License"></a>
</p>

</div>
