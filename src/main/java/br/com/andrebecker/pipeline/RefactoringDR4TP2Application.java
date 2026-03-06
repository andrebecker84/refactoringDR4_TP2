package br.com.andrebecker.pipeline;

import br.com.andrebecker.pipeline.configuracao.Configuracao;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaDeploy;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaNotificacao;
import br.com.andrebecker.pipeline.dominio.etapa.EtapaTeste;
import br.com.andrebecker.pipeline.dominio.modelo.Projeto;
import br.com.andrebecker.pipeline.infraestrutura.NotificadorConsole;
import br.com.andrebecker.pipeline.infraestrutura.RegistradorConsole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RefactoringDR4TP2Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RefactoringDR4TP2Application.class, args);
    }

    @Override
    public void run(String... args) {
        var registrador = new RegistradorConsole();
        var notificador = new NotificadorConsole();

        var configuracao = new Configuracao(true, true, true);
        var etapaTeste = new EtapaTeste(configuracao, registrador);
        var etapaDeploy = new EtapaDeploy(configuracao, registrador);
        var etapaNotificacao = new EtapaNotificacao(configuracao, notificador, registrador);

        var pipeline = new PipelineOrquestrador(etapaTeste, etapaDeploy, etapaNotificacao);
        pipeline.executar(Projeto.comTestes("demo-app"));
    }
}