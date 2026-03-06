package br.com.andrebecker.pipeline.configuracao;

/**
 * Objeto de configuração tipado, substituindo a Primitive Obsession do código original
 * que passava Strings "success"/"failure" como parâmetros de Config.test() e Config.deploy().
 *
 * O uso de booleanos explicita a semântica de cada flag e elimina comparações de Strings
 * espalhadas pelo código de negócio.
 */
public class Configuracao {

    private final boolean testesPassaram;
    private final boolean deployBemSucedido;
    private final boolean notificacaoHabilitada;

    public Configuracao(boolean testesPassaram, boolean deployBemSucedido, boolean notificacaoHabilitada) {
        this.testesPassaram = testesPassaram;
        this.deployBemSucedido = deployBemSucedido;
        this.notificacaoHabilitada = notificacaoHabilitada;
    }

    public boolean testesPassaram() {
        return testesPassaram;
    }

    public boolean deployBemSucedido() {
        return deployBemSucedido;
    }

    public boolean notificacaoHabilitada() {
        return notificacaoHabilitada;
    }
}