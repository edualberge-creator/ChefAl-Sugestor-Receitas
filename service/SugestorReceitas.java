package service;

import model.Ingrediente;
import model.Receita;
import java.util.List;

/**
 * Interface (Abstração) que define o contrato para qualquer classe 
 * que queira sugerir receitas.
 * * Isso permite o Polimorfismo: podemos tratar diferentes tipos 
 * de sugestores da mesma forma.
 */
public interface SugestorReceitas {

    /**
     * O método principal que, com base em uma lista de ingredientes do usuário
     * e filtros, deve retornar uma lista de receitas.
     * * @param ingredientesDisponiveis A lista de ingredientes que o usuário possui.
     * @param filtrosAdicionais Uma lista de strings para filtros (ex: "vegetariano", "sem glúten").
     * @return Uma Lista de objetos Receita.
     */
    List<Receita> sugerir(List<Ingrediente> ingredientesDisponiveis, List<String> filtrosAdicionais);
}