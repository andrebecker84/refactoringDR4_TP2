package br.com.andrebecker.pipeline.dominio.etapa;

import br.com.andrebecker.pipeline.dominio.StatusExecucao;
import br.com.andrebecker.pipeline.dominio.modelo.Projeto;

/**
 * Contrato do Strategy Pattern para etapas do pipeline.
 * Cada etapa recebe o projeto em execução e retorna o status resultante.
 * A interface garante que todas as implementações sejam substituíveis (LSP).
 */
public interface EtapaExecucao {
    StatusExecucao executar(Projeto projeto);
}