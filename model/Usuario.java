package model;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o usuário do sistema.
 * Usa Composição (possui uma lista de Ingredientes disponíveis).
 */
public class Usuario {

    private List<Ingrediente> ingredientesDisponiveis;

    public Usuario() {
        // Inicializa a lista para evitar NullPointerException
        this.ingredientesDisponiveis = new ArrayList<>();
    }

    /**
     * Adiciona um ingrediente à "despensa" do usuário.
     */
    public void adicionarIngrediente(Ingrediente ingrediente) {
        this.ingredientesDisponiveis.add(ingrediente);
    }

    /**
     * Remove um ingrediente (opcional, pode ser útil no futuro).
     */
    public void removerIngrediente(Ingrediente ingrediente) {
        this.ingredientesDisponiveis.remove(ingrediente);
    }
    
    /**
     * Limpa a lista de ingredientes para um novo cadastro.
     */
     public void limparIngredientes() {
        this.ingredientesDisponiveis.clear();
     }

    /**
     * Retorna a lista de ingredientes que o usuário possui.
     */
    public List<Ingrediente> getIngredientesDisponiveis() {
        return ingredientesDisponiveis;
    }
}
