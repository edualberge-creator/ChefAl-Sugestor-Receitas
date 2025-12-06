package service;

import model.Ingrediente;
import model.Receita;
import util.ConfigLoader;
import api.OpenAiApiClient;

import java.util.List;
import java.util.ArrayList;

public class SugestorChefAl implements SugestorReceitas {

    private OpenAiApiClient apiClient;

    public SugestorChefAl() {
        ConfigLoader configLoader = new ConfigLoader();
        String apiKey = configLoader.getApiKey();
        
        try {
            this.apiClient = new OpenAiApiClient(apiKey);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao inicializar: " + e.getMessage());
        }
    }

    @Override
    public List<Receita> sugerir(List<Ingrediente> ingredientesDisponiveis, List<String> filtrosAdicionais) {
        
        if (this.apiClient == null) {
            System.err.println("Serviço indisponível. Verifique a API Key.");
            return new ArrayList<>(); 
        }

        // 1. Construir o prompt JSON
        String promptJson = construirPromptJson(ingredientesDisponiveis, filtrosAdicionais);
        
        // Aviso de UX para o usuário não achar que travou
        System.out.println("\n[ChefAl]: Consultando a inteligência artificial...");

        try {
            // 2. Chamar a API
            String respostaBrutaDaAPI = apiClient.obterSugestoes(promptJson);
            
            // 3. Processar a resposta
            return parsearResposta(respostaBrutaDaAPI);

        } catch (Exception e) {
            System.err.println("Erro ao comunicar com a API: " + e.getMessage());
            return new ArrayList<>(); 
        }
    }

    private String construirPromptJson(List<Ingrediente> ingredientes, List<String> filtros) {
        
        StringBuilder promptUsuario = new StringBuilder();
        
        promptUsuario.append("Ingredientes disponíveis:\n");
        for (Ingrediente ing : ingredientes) {
            promptUsuario.append("- " + ing.toString() + "\n");
        }
        
        promptUsuario.append("\nRestrições:\n");
        promptUsuario.append("1. Usar PREFERENCIALMENTE os ingredientes acima.\n");
        promptUsuario.append("2. Tempo máx: 30 min.\n");
        
        if (!filtros.isEmpty()) {
            promptUsuario.append("3. Filtros: ");
            promptUsuario.append(String.join(", ", filtros));
            promptUsuario.append(".\n");
        }

        promptUsuario.append("\nFormate a resposta EXATAMENTE assim:\n");
        promptUsuario.append("NOME: [Nome]\n");
        promptUsuario.append("TEMPO: [Minutos]\n");
        promptUsuario.append("INGREDIENTES: [Ing1 (qtd)], [Ing2 (qtd)]\n");
        promptUsuario.append("MODO_PREPARO: [Passos]\n");
        promptUsuario.append("---\n"); 
        promptUsuario.append("NOME: [Nome 2]\n");
        promptUsuario.append("TEMPO: [Minutos]\n");
        promptUsuario.append("INGREDIENTES: [Lista]\n");
        promptUsuario.append("MODO_PREPARO: [Passos]\n");
        promptUsuario.append("---\n");
        promptUsuario.append("NOME: [Nome 3]\n");
        promptUsuario.append("TEMPO: [Minutos]\n");
        promptUsuario.append("INGREDIENTES: [Lista]\n");
        promptUsuario.append("MODO_PREPARO: [Passos]\n");

        String promptUsuarioEscapado = promptUsuario.toString()
                                                    .replace("\"", "\\\"")
                                                    .replace("\n", "\\n");

        return """
        {
          "model": "gpt-4o-mini",
          "messages": [
            {
              "role": "system",
              "content": "Você é o 'ChefAl', assistente de culinária. Sugira 3 receitas."
            },
            {
              "role": "user",
              "content": "%s"
            }
          ],
          "temperature": 0.5,
          "max_tokens": 1000
        }
        """.formatted(promptUsuarioEscapado);
    }

    private List<Receita> parsearResposta(String respostaBrutaJson) {
        List<Receita> receitas = new ArrayList<>();
        String conteudoTexto;
        
        try {
            int contentStartIndex = respostaBrutaJson.indexOf("\"content\": \"") + 12;
            int contentEndIndex = respostaBrutaJson.indexOf("\"\n    }", contentStartIndex);
            
            if (contentEndIndex == -1) {
                 contentEndIndex = respostaBrutaJson.indexOf("\",\n      \"role\": \"assistant\"", contentStartIndex);
            }
            
            if (contentEndIndex == -1) return receitas;

            conteudoTexto = respostaBrutaJson.substring(contentStartIndex, contentEndIndex);
            conteudoTexto = conteudoTexto.replace("\\n", "\n").replace("\\\"", "\"");
            
        } catch (Exception e) {
            System.err.println("Erro ao ler resposta da API.");
            return receitas;
        }

        String[] receitasStr = conteudoTexto.split("---\n");

        for (String blocoReceita : receitasStr) {
            if (blocoReceita.trim().isEmpty()) continue;

            String nome = null;
            int tempo = 0;
            String modoPreparo = null;
            List<Ingrediente> ingredientesReceita = null;

            String[] linhas = blocoReceita.trim().split("\n");
            for (String linha : linhas) {
                if (linha.startsWith("NOME: ")) {
                    nome = linha.substring(6).trim();
                } else if (linha.startsWith("TEMPO: ")) {
                    try {
                        String tempoStr = linha.substring(7).trim().split(" ")[0];
                        tempo = Integer.parseInt(tempoStr);
                    } catch (NumberFormatException e) {
                        tempo = 30;
                    }
                } else if (linha.startsWith("INGREDIENTES: ")) {
                    ingredientesReceita = parsearIngredientes(linha.substring(14).trim());
                } else if (linha.startsWith("MODO_PREPARO: ")) {
                    modoPreparo = linha.substring(14).trim();
                }
            }

            if (nome != null && modoPreparo != null && ingredientesReceita != null) {
                receitas.add(new Receita(nome, tempo, modoPreparo, ingredientesReceita));
            }
        }
        return receitas;
    }

    private List<Ingrediente> parsearIngredientes(String linhaIngredientes) {
        List<Ingrediente> ingredientes = new ArrayList<>();
        String[] ingredientesStr = linhaIngredientes.split("], \\["); 
        
        if (ingredientesStr.length <= 1) {
             ingredientesStr = linhaIngredientes.split(", ");
        }

        for (String ingStr : ingredientesStr) {
            ingStr = ingStr.replace("[", "").replace("]", ""); 
            
            int ultimoParenteseAberto = ingStr.lastIndexOf('(');
            int ultimoParenteseFechado = ingStr.lastIndexOf(')');

            if (ultimoParenteseAberto != -1 && ultimoParenteseFechado > ultimoParenteseAberto) {
                String nome = ingStr.substring(0, ultimoParenteseAberto).trim();
                String qtd = ingStr.substring(ultimoParenteseAberto + 1, ultimoParenteseFechado).trim();
                ingredientes.add(new Ingrediente(nome, qtd));
            } else {
                ingredientes.add(new Ingrediente(ingStr.trim(), ""));
            }
        }
        return ingredientes;
    }
}