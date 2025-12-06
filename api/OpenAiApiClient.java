package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenAiApiClient {

    // URL principal da API da OpenAI para "chat completions"
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private String apiKey;
    private HttpClient httpClient;

    public OpenAiApiClient(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty() || !apiKey.startsWith("sk-")) {
            throw new IllegalArgumentException("API Key da OpenAI inválida. Verifique seu config.properties.");
        }
        // A chave da OpenAI DEVE começar com "Bearer " no cabeçalho
        this.apiKey = "Bearer " + apiKey; 
        
        this.httpClient = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(20))
                            .build();
    }

    /**
     * Envia o prompt JSON para a API e retorna a resposta.
     * @param requestBodyJson O corpo da requisição JSON (com modelo e mensagens).
     * @return A resposta da API.
     */
    public String obterSugestoes(String requestBodyJson) throws Exception {
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", this.apiKey) 
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha na API: " + response.statusCode() + " - " + response.body());
        }

        // Retornar o corpo da resposta
        return response.body();
    }
}