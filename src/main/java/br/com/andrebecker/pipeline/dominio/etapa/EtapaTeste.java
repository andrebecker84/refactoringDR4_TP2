package br.com.andrebecker.pipeline.dominio.etapa;

import br.com.andrebecker.pipeline.configuracao.Configuracao;
import br.com.andrebecker.pipeline.dominio.StatusExecucao;
import br.com.andrebecker.pipeline.dominio.modelo.Projeto;
import br.com.andrebecker.pipeline.infraestrutura.Registrador;

/**
 * Responsabilidade única: executar a etapa de testes do pipeline.
 * Extraída do método run() original que misturava testes, deploy e notificação.
 *
 * Refatorações aplicadas:
 * - Extract Class: lógica de testes isolada em classe própria
 * - Extract Method: separação entre "projeto sem testes" e "executar testes"
 * - Replace Primitive with Type: String "success" substituída por StatusExecucao
 */
public class EtapaTeste implements EtapaExecucao {

    private final Configuracao configuracao;
    private final Registrador registrador;

    public EtapaTeste(Configuracao configuracao, Registrador registrador) {
        this.configuracao = configuracao;
        this.registrador = registrador;
    }

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
}