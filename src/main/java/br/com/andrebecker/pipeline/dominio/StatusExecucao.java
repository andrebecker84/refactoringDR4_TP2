package br.com.andrebecker.pipeline.dominio;

/**
 * Elimina a Primitive Obsession do código original, que representava status
 * de execução via Strings "success" e "failure" espalhadas pelo método run().
 * O enum centraliza os estados possíveis e expõe métodos semânticos.
 */
public enum StatusExecucao {

    SUCESSO, FALHA;

    public boolean isSucesso() {
        return this == SUCESSO;
    }

    public boolean isFalha() {
        return this == FALHA;
    }
}