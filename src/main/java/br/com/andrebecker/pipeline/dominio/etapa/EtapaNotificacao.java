package br.com.andrebecker.pipeline.dominio.etapa;

import br.com.andrebecker.pipeline.configuracao.Configuracao;
import br.com.andrebecker.pipeline.dominio.StatusExecucao;
import br.com.andrebecker.pipeline.infraestrutura.Notificador;
import br.com.andrebecker.pipeline.infraestrutura.Registrador;

/**
 * Responsabilidade única: notificar o resultado do pipeline via e-mail.
 * Extraída do método run() original e desacoplada das etapas de teste e deploy.
 *
 * Refatorações aplicadas:
 * - Extract Class: lógica de notificação isolada
 * - Replace Magic String with Symbolic Constant: mensagens centralizadas
 * - Decompose Conditional: decisão de envio separada da montagem da mensagem
 */
public class EtapaNotificacao {

    private static final String MENSAGEM_SUCESSO = "Deploy concluído com sucesso";
    private static final String MENSAGEM_FALHA = "Falha no pipeline de build";

    private final Configuracao configuracao;
    private final Notificador notificador;
    private final Registrador registrador;

    public EtapaNotificacao(Configuracao configuracao, Notificador notificador, Registrador registrador) {
        this.configuracao = configuracao;
        this.notificador = notificador;
        this.registrador = registrador;
    }

    public void notificar(StatusExecucao statusFinal) {
        if (!configuracao.notificacaoHabilitada()) {
            registrador.info("Notificação por e-mail desabilitada");
            return;
        }
        registrador.info("Enviando notificação por e-mail");
        notificador.enviar(resolverMensagem(statusFinal));
    }

    private String resolverMensagem(StatusExecucao status) {
        return status.isSucesso() ? MENSAGEM_SUCESSO : MENSAGEM_FALHA;
    }
}