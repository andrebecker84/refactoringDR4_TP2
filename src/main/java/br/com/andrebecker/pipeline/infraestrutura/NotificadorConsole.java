package br.com.andrebecker.pipeline.infraestrutura;

public class NotificadorConsole implements Notificador {

    @Override
    public void enviar(String mensagem) {
        System.out.println("[EMAIL] " + mensagem);
    }
}