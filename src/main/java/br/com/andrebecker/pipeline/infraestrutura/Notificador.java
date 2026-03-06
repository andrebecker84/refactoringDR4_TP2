package br.com.andrebecker.pipeline.infraestrutura;

/**
 * Abstração de notificação por e-mail. Equivalente ao Emailer do código original.
 * O nome "Notificador" é mais genérico e permite futuras implementações
 * via SMS, Slack ou qualquer outro canal sem alterar o domínio.
 */
public interface Notificador {
    void enviar(String mensagem);
}