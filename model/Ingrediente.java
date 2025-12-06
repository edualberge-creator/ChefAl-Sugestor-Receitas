package model; 
public class Ingrediente {

    private String nome;
    private String quantidade; // Usamos String para flexibilidade (ex: "1 xícara", "100g", "a gosto")

    public Ingrediente(String nome, String quantidade) {
        this.nome = nome;
        this.quantidade = quantidade;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return nome + " (" + quantidade + ")";
    }
}
