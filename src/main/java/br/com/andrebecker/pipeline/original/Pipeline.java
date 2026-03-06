package br.com.andrebecker.pipeline.original;

/**
 * Código original do BuildPipeline Refactoring Kata (Emily Bache).
 * Preservado intacto para fins de comparação com a versão refatorada.
 *
 * Bad smells identificados:
 * - Long Method: run() mistura testes, deploy e notificação em um único fluxo
 * - Primitive Obsession: status representado por Strings "success"/"failure"
 * - Multiple Responsibilities: SRP violado — uma classe faz três coisas distintas
 * - Magic Strings: literais espalhados sem centralização
 * - Condicionais aninhadas: lógica difícil de seguir e de testar isoladamente
 */
public class Pipeline {

    private final Config config;
    private final Emailer emailer;
    private final Logger log;

    public Pipeline(Config config, Emailer emailer, Logger log) {
        this.config = config;
        this.emailer = emailer;
        this.log = log;
    }

    public void run(Project project) {
        String testStatus;
        if (project.hasTests()) {
            if ("success".equals(config.test())) {
                log.info("tests passed");
                testStatus = "success";
            } else {
                log.error("tests failed");
                testStatus = "failure";
            }
        } else {
            log.info("no tests");
            testStatus = "success";
        }

        String deploymentStatus;
        if (testStatus.equals("success")) {
            if ("success".equals(config.deploy())) {
                log.info("deployment successful");
                deploymentStatus = "success";
            } else {
                log.error("deployment failed");
                deploymentStatus = "failure";
            }
        } else {
            deploymentStatus = "failure";
        }

        if (config.sendEmailSummary()) {
            log.info("sending email");
            if (deploymentStatus.equals("success")) {
                emailer.send("Deployment completed successfully");
            } else {
                emailer.send("Deployment failed");
            }
        } else {
            log.info("Email disabled");
        }
    }
}