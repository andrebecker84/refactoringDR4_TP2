package br.com.andrebecker.pipeline;

import br.com.andrebecker.pipeline.configuracao.Configuracao;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaDeploy;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaNotificacao;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaTeste;
import br.com.andrebecker.pipeline.dominio.modelo.Projeto;
import br.com.andrebecker.pipeline.infraestrutura.Notificador;
import br.com.andrebecker.pipeline.infraestrutura.Registrador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PipelineOrquestradorTest {

    @Mock
    private Notificador notificador;

    @Mock
    private Registrador registrador;

    // --- Testes de fluxo completo ---

    @Test
    void projetoSemTestes_deployBemSucedido_enviaEmailDeSucesso() {
        var config = new Configuracao(true, true, true);
        var pipeline = montarPipeline(config);

        pipeline.executar(Projeto.semTestes("api-pagamentos"));

        verify(notificador).enviar("Deploy concluído com sucesso");
        verifyNoMoreInteractions(notificador);
    }

    @Test
    void projetoComTestes_testesPassam_deployBemSucedido_enviaEmailDeSucesso() {
        var config = new Configuracao(true, true, true);
        var pipeline = montarPipeline(config);

        pipeline.executar(Projeto.comTestes("servico-relatorios"));

        verify(notificador).enviar("Deploy concluído com sucesso");
    }

    @Test
    void testesFalham_deployNaoExecutado_enviaEmailDeFalha() {
        var config = new Configuracao(false, true, true);
        var pipeline = montarPipeline(config);

        pipeline.executar(Projeto.comTestes("modulo-fiscal"));

        verify(notificador).enviar("Falha no pipeline de build");
        verify(registrador).erro("Falha na execução dos testes");
    }

    @Test
    void testesPassam_deployFalha_enviaEmailDeFalha() {
        var config = new Configuracao(true, false, true);
        var pipeline = montarPipeline(config);

        pipeline.executar(Projeto.comTestes("gateway-integracao"));

        verify(notificador).enviar("Falha no pipeline de build");
        verify(registrador).erro("Falha durante o deploy");
    }

    @Test
    void notificacaoDesabilitada_nenhumEmailEnviado() {
        var config = new Configuracao(true, true, false);
        var pipeline = montarPipeline(config);

        pipeline.executar(Projeto.comTestes("worker-processamento"));

        verifyNoInteractions(notificador);
    }

    @Test
    void notificacaoDesabilitada_mesmoCom_testesFalhos_nenhumEmailEnviado() {
        var config = new Configuracao(false, false, false);
        var pipeline = montarPipeline(config);

        pipeline.executar(Projeto.comTestes("core-domain"));

        verifyNoInteractions(notificador);
    }

    // --- Testes de log ---

    @Test
    void projetoSemTestes_registraAusenciaDeTestes() {
        var config = new Configuracao(true, true, false);
        var pipeline = montarPipeline(config);

        pipeline.executar(Projeto.semTestes("lambda-worker"));

        verify(registrador).info(contains("Sem testes definidos"));
    }

    @Test
    void testesFalham_naoTentaDeploy_registraSoErroDosTestes() {
        var config = new Configuracao(false, true, false);
        var pipeline = montarPipeline(config);

        pipeline.executar(Projeto.comTestes("auth-service"));

        verify(registrador).erro("Falha na execução dos testes");
        verify(registrador, never()).info("Deploy realizado com sucesso");
        verify(registrador, never()).erro("Falha durante o deploy");
    }

    // --- Validação de objeto ---

    @Test
    void projetoComNomeVazio_lancaExcecaoComMensagemDescritiva() {
        var ex = assertThrows(IllegalArgumentException.class, () -> Projeto.comTestes(""));
        assertThat(ex.getMessage(), is("Nome do projeto não pode ser vazio"));
    }

    // --- Utilitário ---

    private PipelineOrquestrador montarPipeline(Configuracao config) {
        var etapaTeste = new EtapaTeste(config, registrador);
        var etapaDeploy = new EtapaDeploy(config, registrador);
        var etapaNotificacao = new EtapaNotificacao(config, notificador, registrador);
        return new PipelineOrquestrador(etapaTeste, etapaDeploy, etapaNotificacao);
    }

}