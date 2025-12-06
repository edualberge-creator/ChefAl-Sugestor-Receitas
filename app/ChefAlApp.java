package app;

import model.Ingrediente;
import model.Receita;
import model.Usuario;
import service.SugestorChefAl;
import service.SugestorReceitas;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal da aplicação ChefAl.
 * Responsável pela interação com o usuário via console.
 */
public class ChefAlApp {

    // Scanner é estático para ser usado em todos os métodos da classe
    private static Scanner scanner = new Scanner(System.in);
    
    // Criamos as instâncias centrais que a aplicação usará
    private static Usuario usuario = new Usuario();
    
    // Polimorfismo: Declaramos como a Interface, instanciamos como a Classe Concreta
    private static SugestorReceitas sugestor = new SugestorChefAl();

    /**
     * Ponto de entrada (main) da aplicação.
     */
    public static void main(String[] args) {
        System.out.println("--- Bem-vindo ao ChefAl ---");
        System.out.println("Seu assistente de receitas inteligentes.\n");

        boolean executando = true;
        
        // Loop principal do menu
        while (executando) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    cadastrarIngredientes();
                    break;
                case 2:
                    buscarReceitas();
                    break;
                case 3:
                    executando = false;
                    System.out.println("Obrigado por usar o ChefAl. Bom apetite!");
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, tente novamente.");
            }
        }
        
        // Fechar o scanner ao sair
        scanner.close();
    }

    /**
     * Exibe o menu de opções.
     */
    private static void exibirMenuPrincipal() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Cadastrar ingredientes que tenho em casa");
        System.out.println("2. Buscar Receitas");
        System.out.println("3. Sair");
        System.out.print("Escolha uma opção: ");
    }

    /**
     * Lê a opção do usuário e trata entradas inválidas.
     */
    private static int lerOpcao() {
        try {
            // Ler a linha inteira e converter para int
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Retorna um valor inválido
        }
    }

    /**
     * Lida com a lógica de cadastro de ingredientes.
     */
    private static void cadastrarIngredientes() {
        System.out.println("\n--- Cadastro de Ingredientes ---");
        System.out.println("Digite os ingredientes que você tem. (Digite 'fim' para parar)");
        
        // Limpa a lista de ingredientes anterior para novo cadastro
        usuario.limparIngredientes(); 

        while (true) {
            System.out.print("Nome do ingrediente (ou 'fim'): ");
            String nome = scanner.nextLine();
            
            if (nome.equalsIgnoreCase("fim")) {
                break; // Encerra o loop
            }

            System.out.print("Quantidade (ex: 100g, 2 unidades): ");
            String qtd = scanner.nextLine();

            // Adiciona o ingrediente ao usuário
            Ingrediente ing = new Ingrediente(nome, qtd);
            usuario.adicionarIngrediente(ing);
            System.out.println("   -> " + ing.toString() + " adicionado!");
        }
        System.out.println("Ingredientes cadastrados com sucesso!");
    }

    /**
     * Lida com a lógica de busca e exibição de receitas.
     */
    private static void buscarReceitas() {
        // Validação: precisa ter ingredientes
        if (usuario.getIngredientesDisponiveis().isEmpty()) {
            System.out.println("\n[ERRO] Você precisa cadastrar ingredientes primeiro (Opção 1).");
            return;
        }

        // --- Lógica dos Filtros (Diferencial) ---
        List<String> filtros = new ArrayList<>();
        System.out.println("\n--- Filtros Adicionais ---");
        System.out.println("Deseja adicionar algum filtro? (ex: vegetariano, sem glúten, vegano)");
        System.out.print("Digite os filtros separados por vírgula ou deixe em branco: ");
        
        String filtrosInput = scanner.nextLine();
        if (filtrosInput != null && !filtrosInput.trim().isEmpty()) {
            String[] filtrosArray = filtrosInput.split(",");
            for (String f : filtrosArray) {
                filtros.add(f.trim()); // Adiciona o filtro limpando espaços
            }
        }
        // ---- Fim Filtros ----

        System.out.println("\n[ChefAl]: Consultando a inteligência artificial... Isso pode levar um momento.");
        
        // Chama o serviço (que chama a API)
        List<Receita> receitasSugeridas = sugestor.sugerir(
            usuario.getIngredientesDisponiveis(), 
            filtros
        );

        // Exibir resultados
        if (receitasSugeridas.isEmpty()) {
            System.out.println("[ChefAl]: Desculpe, não foi possível encontrar receitas.");
            System.out.println("Verifique sua conexão, a chave de API ou tente ingredientes diferentes.");
        } else {
            System.out.println("\n--- RECEITAS SUGERIDAS (em até 30 minutos) ---");
            exibirReceitasFormatadas(receitasSugeridas);
        }
    }

    /**
     * Formata e exibe a lista de receitas no console.
     */
    private static void exibirReceitasFormatadas(List<Receita> receitas) {
        int count = 1;
        for (Receita receita : receitas) {
            System.out.println("\n======================================");
            System.out.println(" RECEITA " + count + ": " + receita.getNome().toUpperCase());
            System.out.println("======================================");
            System.out.println("TEMPO: " + receita.getTempoPreparoMinutos() + " minutos");
            
            System.out.println("\nINGREDIENTES:");
            for (Ingrediente ing : receita.getIngredientesNecessarios()) {
                // Requisito: "destacando os que o usuário já tem" [cite: 15]
                String destaque = temIngrediente(ing) ? " (Você tem!)" : "";
                System.out.println("- " + ing.toString() + destaque);
            }
            
            System.out.println("\nMODO DE PREPARO:");
            // Formata o modo de preparo para melhor leitura
            String modoPreparoFormatado = receita.getModoPreparo()
                                                 .replace(". ", ".\n"); // Quebra linha a cada ponto final
            System.out.println(modoPreparoFormatado);

            count++;
        }
    }
    
    /**
     * Método auxiliar para verificar se o usuário possui um ingrediente.
     * Usado para o "destaque".
     */
    private static boolean temIngrediente(Ingrediente ingReceita) {
        // Verificação simples por nome (pode ser aprimorada)
        for (Ingrediente ingUsuario : usuario.getIngredientesDisponiveis()) {
            String nomeUsuario = ingUsuario.getNome().toLowerCase();
            String nomeReceita = ingReceita.getNome().toLowerCase();
            
            // Se o nome do ingrediente da receita contiver o nome do ingrediente do usuário
            // (ex: "Tomate cereja" na receita e "Tomate" no usuário)
            if (nomeReceita.contains(nomeUsuario)) {
                return true;
            }
        }
        return false;
    }
}