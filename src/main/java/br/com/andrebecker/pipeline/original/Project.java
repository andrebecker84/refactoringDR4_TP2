package br.com.andrebecker.pipeline.original;

public class Project {

    private final boolean hasTests;

    private Project(boolean hasTests) {
        this.hasTests = hasTests;
    }

    public static Project withTests() {
        return new Project(true);
    }

    public static Project withoutTests() {
        return new Project(false);
    }

    public boolean hasTests() {
        return hasTests;
    }
}