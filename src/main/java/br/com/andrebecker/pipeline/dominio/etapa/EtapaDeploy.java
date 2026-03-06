package br.com.andrebecker.pipeline.dominio.etapa;

import br.com.andrebecker.pipeline.configuracao.Configuracao;
import br.com.andrebecker.pipeline.dominio.StatusExecucao;
import br.com.andrebecker.pipeline.dominio.modelo.Projeto;
import br.com.andrebecker.pipeline.infraestrutura.Registrador;

/**
 * Responsabilidade única: executar a etapa de deploy do pipeline.
 * Extraída do método run() original conforme princípio SRP.
 *
 * O orquestrador garante que essa etapa só seja invocada após testes bem-sucedidos,
 * mantendo a lógica de sequenciamento fora desta classe (separação de responsabilidades).
 */
public class EtapaDeploy implements EtapaExecucao {

    private final Configuracao configuracao;
    private final Registrador registrador;

    public EtapaDeploy(Configuracao configuracao, Registrador registrador) {
        this.configuracao = configuracao;
        this.registrador = registrador;
    }

    @Override
    public StatusExecucao executar(Projeto projeto) {
        if (configuracao.deployBemSucedido()) {
            registrador.info("Deploy realizado com sucesso");
            return StatusExecucao.SUCESSO;
        }
        registrador.erro("Falha durante o deploy");
        return StatusExecucao.FALHA;
    }
}