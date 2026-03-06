package br.com.andrebecker.pipeline.infraestrutura;

/**
 * Abstração de logging. Equivalente ao Logger do código original,
 * renomeado para manter consistência com a nomenclatura em português.
 */
public interface Registrador {
    void info(String mensagem);
    void erro(String mensagem);
}