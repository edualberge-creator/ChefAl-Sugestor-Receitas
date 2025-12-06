 package model;
import java.util.List;

/**
 * Representa uma receita sugerida pelo ChefAl.
 * Usa Composição (possui uma lista de Ingredientes).
 */
public class Receita {

    private String nome;
    private int tempoPreparoMinutos;
    private String modoPreparo;
    private List<Ingrediente> ingredientesNecessarios;

    public Receita(String nome, int tempoPreparoMinutos, String modoPreparo, List<Ingrediente> ingredientesNecessarios) {
        this.nome = nome;
        this.tempoPreparoMinutos = tempoPreparoMinutos;
        this.modoPreparo = modoPreparo;
        this.ingredientesNecessarios = ingredientesNecessarios;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public int getTempoPreparoMinutos() {
        return tempoPreparoMinutos;
    }

    public String getModoPreparo() {
        return modoPreparo;
    }

    public List<Ingrediente> getIngredientesNecessarios() {
        return ingredientesNecessarios;
    }
}
