package br.com.andrebecker.pipeline.original;

public class Config {

    private final String test;
    private final String deploy;
    private final boolean sendEmailSummary;

    public Config(String test, String deploy, boolean sendEmailSummary) {
        this.test = test;
        this.deploy = deploy;
        this.sendEmailSummary = sendEmailSummary;
    }

    public String test() {
        return test;
    }

    public String deploy() {
        return deploy;
    }

    public boolean sendEmailSummary() {
        return sendEmailSummary;
    }
}