package br.com.andrebecker.pipeline.dominio.modelo;

/**
 * Value object que representa o projeto sendo processado pelo pipeline.
 * Construtores estáticos nomeados deixam a intenção clara no ponto de uso.
 */
public class Projeto {

    private final String nome;
    private final boolean possuiTestes;

    private Projeto(String nome, boolean possuiTestes) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do projeto não pode ser vazio");
        this.nome = nome;
        this.possuiTestes = possuiTestes;
    }

    public static Projeto comTestes(String nome) {
        return new Projeto(nome, true);
    }

    public static Projeto semTestes(String nome) {
        return new Projeto(nome, false);
    }

    public String getNome() {
        return nome;
    }

    public boolean possuiTestes() {
        return possuiTestes;
    }
}