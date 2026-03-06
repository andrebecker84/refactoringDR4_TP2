package br.com.andrebecker.pipeline;

import br.com.andrebecker.pipeline.configuracao.Configuracao;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaDeploy;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaNotificacao;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaTeste;
import br.com.andrebecker.pipeline.dominio.modelo.Projeto;
import br.com.andrebecker.pipeline.infraestrutura.Notificador;
import br.com.andrebecker.pipeline.infraestrutura.Registrador;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Testes baseados em propriedades via Jqwik.
 * Validam invariantes do sistema para qualquer combinação de entrada,
 * complementando os testes determinísticos com cobertura exaustiva de cenários.
 */
class PipelinePropriedadesTest {

    @Property
    void notificacaoDesabilitada_nuncaEnviaMensagem(
            @ForAll boolean possuiTestes,
            @ForAll boolean testesPassam,
            @ForAll boolean deployBemSucedido) {

        var capturador = new CapturadorMensagens();
        var config = new Configuracao(testesPassam, deployBemSucedido, false);
        var projeto = possuiTestes ? Projeto.comTestes("projeto-x") : Projeto.semTestes("projeto-x");

        montarPipeline(config, capturador).executar(projeto);

        assertThat("Com notificação desabilitada, nenhuma mensagem deve ser enviada",
                capturador.mensagensEnviadas(), is(empty()));
    }

    @Property
    void notificacaoHabilitada_semprEnviaExatamenteUmaMensagem(
            @ForAll boolean possuiTestes,
            @ForAll boolean testesPassam,
            @ForAll boolean deployBemSucedido) {

        var capturador = new CapturadorMensagens();
        var config = new Configuracao(testesPassam, deployBemSucedido, true);
        var projeto = possuiTestes ? Projeto.comTestes("projeto-y") : Projeto.semTestes("projeto-y");

        montarPipeline(config, capturador).executar(projeto);

        assertThat("Com notificação habilitada, exatamente uma mensagem deve ser enviada",
                capturador.mensagensEnviadas(), hasSize(1));
    }

    @Property
    void testesFalham_mensagemEnviadaNuncaIndicaSucesso(
            @ForAll boolean deployBemSucedido) {

        var capturador = new CapturadorMensagens();
        var config = new Configuracao(false, deployBemSucedido, true);

        montarPipeline(config, capturador).executar(Projeto.comTestes("projeto-z"));

        assertThat("Quando testes falham, a mensagem enviada deve indicar falha",
                capturador.mensagensEnviadas().get(0), not(containsString("sucesso")));
    }

    @Property
    void deployFalha_mensagemEnviadaNuncaIndicaSucesso(
            @ForAll boolean possuiTestes) {

        var capturador = new CapturadorMensagens();
        var config = new Configuracao(true, false, true);
        var projeto = possuiTestes ? Projeto.comTestes("projeto-w") : Projeto.semTestes("projeto-w");

        montarPipeline(config, capturador).executar(projeto);

        assertThat("Quando o deploy falha, a mensagem enviada deve indicar falha",
                capturador.mensagensEnviadas().get(0), not(containsString("sucesso")));
    }

    @Property
    void testesPassam_deployPassam_notificacaoHabilitada_mensagemSempre_indicaSucesso(
            @ForAll boolean possuiTestes) {

        var capturador = new CapturadorMensagens();
        var config = new Configuracao(true, true, true);
        var projeto = possuiTestes ? Projeto.comTestes("projeto-k") : Projeto.semTestes("projeto-k");

        montarPipeline(config, capturador).executar(projeto);

        assertThat(capturador.mensagensEnviadas().get(0), containsString("sucesso"));
    }

    // --- Utilitários de teste ---

    private PipelineOrquestrador montarPipeline(Configuracao config, Notificador notificador) {
        Registrador silencioso = new RegistradorSilencioso();
        var etapaTeste = new EtapaTeste(config, silencioso);
        var etapaDeploy = new EtapaDeploy(config, silencioso);
        var etapaNotificacao = new EtapaNotificacao(config, notificador, silencioso);
        return new PipelineOrquestrador(etapaTeste, etapaDeploy, etapaNotificacao);
    }

    static class CapturadorMensagens implements Notificador {
        private final List<String> enviadas = new ArrayList<>();

        @Override
        public void enviar(String mensagem) {
            enviadas.add(mensagem);
        }

        List<String> mensagensEnviadas() {
            return enviadas;
        }
    }

    static class RegistradorSilencioso implements Registrador {
        @Override public void info(String mensagem) {}
        @Override public void erro(String mensagem) {}
    }
}