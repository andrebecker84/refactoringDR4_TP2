package br.com.andrebecker.pipeline;

import br.com.andrebecker.pipeline.dominio.StatusExecucao;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaDeploy;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaNotificacao;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaTeste;
import br.com.andrebecker.pipeline.dominio.modelo.Projeto;

/**
 * Orquestrador do pipeline de build. Substitui o método run() monolítico do código original
 * por uma sequência explícita de etapas coesas e substituíveis.
 *
 * Refatorações aplicadas:
 * - Extract Class: orquestração separada das responsabilidades de cada etapa
 * - Strategy Pattern: EtapaTeste e EtapaDeploy são intercambiáveis via EtapaExecucao
 * - Decompose Conditional: lógica de encadeamento extraída para método nomeado
 *
 * Fluxo: Testes → Deploy (se testes passaram) → Notificação (sempre)
 */
public class PipelineOrquestrador {

    private final EtapaTeste etapaTeste;
    private final EtapaDeploy etapaDeploy;
    private final EtapaNotificacao etapaNotificacao;

    public PipelineOrquestrador(EtapaTeste etapaTeste, EtapaDeploy etapaDeploy, EtapaNotificacao etapaNotificacao) {
        this.etapaTeste = etapaTeste;
        this.etapaDeploy = etapaDeploy;
        this.etapaNotificacao = etapaNotificacao;
    }

    public void executar(Projeto projeto) {
        StatusExecucao statusTeste = etapaTeste.executar(projeto);
        StatusExecucao statusDeploy = prosseguirParaDeploy(statusTeste, projeto);
        etapaNotificacao.notificar(statusDeploy);
    }

    private StatusExecucao prosseguirParaDeploy(StatusExecucao statusTeste, Projeto projeto) {
        if (statusTeste.isFalha()) {
            return StatusExecucao.FALHA;
        }
        return etapaDeploy.executar(projeto);
    }
}