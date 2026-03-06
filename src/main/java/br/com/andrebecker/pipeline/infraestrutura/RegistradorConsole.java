package br.com.andrebecker.pipeline.infraestrutura;

public class RegistradorConsole implements Registrador {

    @Override
    public void info(String mensagem) {
        System.out.println("[INFO]  " + mensagem);
    }

    @Override
    public void erro(String mensagem) {
        System.err.println("[ERRO]  " + mensagem);
    }
}